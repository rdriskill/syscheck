package com.github.rdriskill.syscheck.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rdriskill.syscheck.AppConfig;
import com.github.rdriskill.syscheck.model.CheckPoint;
import com.github.rdriskill.syscheck.model.CheckPointType;

/**
 * @author rdriskill
 */
@Service
public class CheckPointProcessor {
	private static Logger log = LogManager.getLogger(CheckPointProcessor.class);
	private Collection<CheckPoint> checkPoints = new HashSet<CheckPoint>();
	private NotifierGateway notifier;
	private String checkpointFilePath;
	private HttpClient httpClient;
	
	@Scheduled(initialDelay=10000, fixedRate=180000)
	private void checkAvailability() {
		log.info("Starting check point availability test.");
		this.loadCheckpointFile();
		
		Map<CheckPoint, String> notifyNotAvailable = new HashMap<CheckPoint, String>();
		Set<CheckPoint> notifyAvailable = new HashSet<CheckPoint>();
		
		this.checkPoints.forEach(checkPoint -> {
			try {
				if (checkPoint.getType().equals(CheckPointType.DATABASE)) {
					Class.forName("com.mysql.jdbc.Driver");
					
					try (
					    Connection conn = DriverManager.getConnection(checkPoint.getUrl(), checkPoint.getUser(), checkPoint.getPassword());
						Statement stmt = conn.createStatement();
					) {
						stmt.setQueryTimeout(AppConfig.TIMEOUT_SECS /1000);
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
						HttpGet request = new HttpGet(checkPoint.getUrl());
						HttpResponse response = httpClient.execute(request);
					    
						if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
							notifyNotAvailable.put(checkPoint, String.format("Received the status code %s for %s", response.getStatusLine().getStatusCode(), checkPoint.getName()));
						} else {
							notifyAvailable.add(checkPoint);
						}
						
						request.releaseConnection();
					} catch (Exception ex) {
						notifyNotAvailable.put(checkPoint, String.format("Error connecting for %s: %s", checkPoint.getName(), ex.getMessage()));
					}
				}
			} catch (Exception ex) {
				notifyNotAvailable.put(checkPoint, String.format("Error connecting for %s: %s", checkPoint.getName(), ex.getMessage()));
			}
		});
		
		this.notifier.notifyNotAvailable(notifyNotAvailable, this.checkPoints.size());
		this.notifier.notifyAvailable(notifyAvailable);
		log.info(String.format("Completed check point availability test. %s currently failing.", this.notifier.getCurrentFailureCount()));
	}
	
    private void loadCheckpointFile() {
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader(this.checkpointFilePath));
    		Set<CheckPoint> incomingCheckPoints = new ObjectMapper().readValue(reader, new TypeReference<HashSet<CheckPoint>>(){});
    		this.setCheckPoints(incomingCheckPoints);
		} catch (Exception ex) {
			log.error("Error loading checkpoint file.", ex);
		}
    }

	private void setCheckPoints(Collection<CheckPoint> incomingCheckPoints) {
		if (incomingCheckPoints == null || incomingCheckPoints.isEmpty()) {
			log.warn("Attempted to set check points with empy collection.");
		} else {
			Set<ConstraintViolation<CheckPoint>> violations = new HashSet<ConstraintViolation<CheckPoint>>();
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			Validator validator = factory.getValidator();
			
			incomingCheckPoints.forEach(checkPoint -> {
				violations.addAll(validator.validate(checkPoint));
			});
			
			if (violations.isEmpty()) {
				Set<CheckPoint> filteredIncomingCheckPoints = 
						incomingCheckPoints
							.stream()
							.filter(checkPoint -> checkPoint.getEnabled())
							.collect(Collectors.toSet());
						
				this.checkPoints.clear();
				this.checkPoints.addAll(filteredIncomingCheckPoints);
				log.info("{} check points have been loaded.", filteredIncomingCheckPoints.size());
			} else {
				violations.forEach(violation -> log.error("Validation error loading checkpoints: {}", violation.getMessage()));
			}
		}
	}

	@Autowired
	public void setNotifier(NotifierGateway notifier) {
		this.notifier = notifier;
	}

	public void setCheckpointFilePath(String checkpointFilePath) {
		this.checkpointFilePath = checkpointFilePath;
	}

	@Autowired
	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}
}
