package com.github.rdriskill.syscheck.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.rdriskill.syscheck.model.CheckPoint;

/**
 * @author rdriskill
 */
@Service
public class NotifierGateway {
	private static Logger log = LogManager.getLogger(NotifierGateway.class);
	final Double connectionIssueThreshold = 0.45;
	private Map<String, CheckPoint> pointsNotAvail = new HashMap<String, CheckPoint>();
	private EmailNotifier emailNotifier;
	private Boolean connectionIssues = Boolean.FALSE;
	
	public void notifyNotAvailable(Map<CheckPoint, String> notifyNotAvailable, Integer totalCheckPointCount) {
		if (totalCheckPointCount > 0 && (notifyNotAvailable.size() / totalCheckPointCount >= connectionIssueThreshold)) {
			String msg = String.format("%s out of %s checkpoints have failed. Either there is a connection issue or a catastrophic failure.", notifyNotAvailable.size(), totalCheckPointCount);
			log.warn(msg);
			
			if (!this.connectionIssues) {
				emailNotifier.sendEmail(msg);
				connectionIssues = Boolean.TRUE;
			}
		} else {
			if (this.connectionIssues) {
				String msg = "A prior connection issue or catastrophic failure has recovered.";
				log.info(msg);
				emailNotifier.sendEmail(msg);
				connectionIssues = Boolean.FALSE;
			}
			
			Optional.ofNullable(notifyNotAvailable).ifPresent(map -> map.entrySet().forEach(entry -> {
				CheckPoint checkPoint = entry.getKey();
				String msg = entry.getValue();
				
				if (!this.pointsNotAvail.containsKey(checkPoint.getName())) {
					log.warn(msg);
					emailNotifier.sendEmail(msg);
					this.pointsNotAvail.put(checkPoint.getName(), checkPoint);
				}
			}));
		}
	}
	
	public void notifyAvailable(Set<CheckPoint> notifyAvailable) {
		Optional.ofNullable(notifyAvailable).ifPresent(stream -> stream.forEach(checkPoint -> {
			if (this.pointsNotAvail.containsKey(checkPoint.getName())) {
				String msg = String.format("%s has recovered.", checkPoint.getName());
				log.info(msg);
				emailNotifier.sendEmail(msg);
				this.pointsNotAvail.remove(checkPoint.getName());
			}
		}));
	}
	
	public Collection<CheckPoint> getCheckPointsInFailure() {
		return this.pointsNotAvail.values();
	}

	@Autowired
	public void setEmailNotifier(EmailNotifier emailNotifier) {
		this.emailNotifier = emailNotifier;
	}
}
