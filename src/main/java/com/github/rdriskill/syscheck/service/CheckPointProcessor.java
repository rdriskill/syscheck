package com.github.rdriskill.syscheck.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.github.rdriskill.syscheck.model.CheckPoint;
import com.github.rdriskill.syscheck.model.CheckPointType;

/**
 * @author rdriskill
 */
@Service
public class CheckPointProcessor {
	private static Logger log = LogManager.getLogger(CheckPointProcessor.class);

	private Collection<CheckPoint> checkPoints;
	
	private NotifierGateway notifier;
	
	@Scheduled(initialDelay=10000, fixedRate=180000)
	private void checkAvailability() {
		Map<CheckPoint, String> notifyNotAvailable = new HashMap<CheckPoint, String>();
		Set<CheckPoint> notifyAvailable = new HashSet<CheckPoint>();
		
		Optional.ofNullable(this.checkPoints).ifPresent(stream -> stream.forEach(checkPoint -> {
			try {
				if (checkPoint.getType().equals(CheckPointType.DATABASE)) {
					Class.forName("com.mysql.jdbc.Driver");
					
					try (
					    Connection conn = DriverManager.getConnection(checkPoint.getUrl(), checkPoint.getUser(), checkPoint.getPassword());
						Statement stmt = conn.createStatement();
					) {
						ResultSet resultSet = stmt.executeQuery("select 1");
						
						if (!resultSet.next() || !resultSet.getString(1).equals("1")) {
							notifyNotAvailable.put(checkPoint, String.format("Unexpected query result for %s.", checkPoint.getName()));
						} else {
							notifyAvailable.add(checkPoint);
						}
					} catch (Exception ex) {
						notifyNotAvailable.put(checkPoint, String.format("Error connecting for %s: %s", checkPoint.getName(), ex.getMessage()));
					}
				} else if (checkPoint.getType().equals(CheckPointType.WEB)) {
					try {
						HttpClient client = HttpClientBuilder.create().build(); 
						HttpResponse response = client.execute(new HttpGet(checkPoint.getUrl()));
					    
						if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
							notifyNotAvailable.put(checkPoint, String.format("Received the status code %s for %s", response.getStatusLine().getStatusCode(), checkPoint.getName()));
						} else {
							notifyAvailable.add(checkPoint);
						}
					} catch (Exception ex) {
						notifyNotAvailable.put(checkPoint, String.format("Error connecting for %s: %s", checkPoint.getName(), ex.getMessage()));
					}
				}
			} catch (Exception ex) {
				notifyNotAvailable.put(checkPoint, String.format("Error connecting for %s: %s", checkPoint.getName(), ex.getMessage()));
			}
		}));
		
		this.notifier.notifyNotAvailable(notifyNotAvailable, this.checkPoints.size());
		this.notifier.notifyAvailable(notifyAvailable);
		log.info(String.format("Completed check point availability test. %s currently failing.", this.notifier.getCheckPointsInFailure().size()));
	}

	public void setCheckPoints(Collection<CheckPoint> checkPoints) {
		if (checkPoints == null || checkPoints.isEmpty()) {
			log.warn("Attempted to set check points with empy collection.");
		} else {
			Set<ConstraintViolation<CheckPoint>> violations = new HashSet<ConstraintViolation<CheckPoint>>();
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			Validator validator = factory.getValidator();
			
			checkPoints.forEach(checkPoint -> {
				violations.addAll(validator.validate(checkPoint));
			});
			
			if (violations.isEmpty()) {
				log.info("{} check points have been loaded.", checkPoints.size());
				this.checkPoints = checkPoints;
			} else {
				violations.forEach(violation -> log.error("Validation error loading checkpoints: {}", violation.getMessage()));
			}
		}
	}

	@Autowired
	public void setNotifier(NotifierGateway notifier) {
		this.notifier = notifier;
	}
}
