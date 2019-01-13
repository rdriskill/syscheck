package com.github.rdriskill.syscheck.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.github.rdriskill.syscheck.model.CheckPoint;
import com.github.rdriskill.syscheck.model.CheckPointHistory;
import com.github.rdriskill.syscheck.model.CheckPointStatus;

/**
 * @author rdriskill
 */
@Service
public class NotifierGateway {
	private static Logger log = LogManager.getLogger(NotifierGateway.class);
	final Double connectionIssueThreshold = 0.45;
	private Map<String, Collection<CheckPointHistory>> failuresByCheckpointName = new HashMap<String, Collection<CheckPointHistory>>();
	private EmailNotifier emailNotifier;
	private Boolean connectionIssues = Boolean.FALSE;
	private Environment env;
	
	public void notifyNotAvailable(Map<CheckPoint, String> notifyNotAvailable, Integer totalCheckPointCount) {
		final Double percentNotAvailable = Double.valueOf(notifyNotAvailable.size()) / Double.valueOf(totalCheckPointCount);
		final Integer failuresBeforeNotifying = this.getFailuresBeforeNotifying();
		
		if (percentNotAvailable >= connectionIssueThreshold) {
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
				log.warn(msg);
				
				if (!this.failuresByCheckpointName.containsKey(checkPoint.getName())) {
					this.failuresByCheckpointName.put(checkPoint.getName(), new ArrayList<CheckPointHistory>());
				}
				
				this.failuresByCheckpointName.get(checkPoint.getName()).add(new CheckPointHistory(checkPoint, CheckPointStatus.FAILURE, msg));
				
				if (this.failuresByCheckpointName.get(checkPoint.getName()).size() == failuresBeforeNotifying) {
					emailNotifier.sendEmail(msg);
				}
			}));
		}
	}
	
	public void notifyAvailable(Set<CheckPoint> notifyAvailable) {
		final Integer failuresBeforeNotifying = this.getFailuresBeforeNotifying();
		
		Optional.ofNullable(notifyAvailable).ifPresent(stream -> stream.forEach(checkPoint -> {
			if (this.failuresByCheckpointName.containsKey(checkPoint.getName())) {
				Collection<CheckPointHistory> history = this.failuresByCheckpointName.get(checkPoint.getName());
				String msg = String.format("%s has recovered from %s prior failures", checkPoint.getName(), history.size());
				log.info(msg);
				
				if (history.size() >= failuresBeforeNotifying) {
					emailNotifier.sendEmail(msg);
				}
				
				this.failuresByCheckpointName.remove(checkPoint.getName());
			}
		}));
	}
	
	public int getCurrentFailureCount() {
		return this.failuresByCheckpointName.size();
	}

	@Autowired
	public void setEmailNotifier(EmailNotifier emailNotifier) {
		this.emailNotifier = emailNotifier;
	}

	@Autowired
	public void setEnv(Environment env) {
		this.env = env;
	}
	
	private Integer getFailuresBeforeNotifying() {
		return Integer.valueOf(env.getProperty("app.failuresBeforeNotifying", "1"));
	}
}
