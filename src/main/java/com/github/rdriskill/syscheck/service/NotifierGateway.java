package com.github.rdriskill.syscheck.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
	private Map<String, CheckPoint> pointsNotAvail = new HashMap<String, CheckPoint>();
	private EmailNotifier emailNotifier;
	
	public void notifyNotAvailable(CheckPoint checkPoint, String msg) {
		if (!this.pointsNotAvail.containsKey(checkPoint.getName())) {
			this.sendFailureAlert(checkPoint, msg);
			this.pointsNotAvail.put(checkPoint.getName(), checkPoint);
		}
	}
	
	public void notifyAvailable(CheckPoint checkPoint) {
		if (this.pointsNotAvail.containsKey(checkPoint.getName())) {
			this.sendRecoveryAlert(this.pointsNotAvail.get(checkPoint.getName()));
			this.pointsNotAvail.remove(checkPoint.getName());
		}
	}
	
	public Collection<CheckPoint> getCheckPointsInFailure() {
		return this.pointsNotAvail.values();
	}
	
	private void sendFailureAlert(CheckPoint checkPoint, String msg) {
		log.warn(msg);
		emailNotifier.sendEmail(msg);
	}
	
	private void sendRecoveryAlert(CheckPoint checkPoint) {
		String msg = String.format("%s has recovered.", checkPoint.getName());
		log.info(msg);
		emailNotifier.sendEmail(msg);
	}

	@Autowired
	public void setEmailNotifier(EmailNotifier emailNotifier) {
		this.emailNotifier = emailNotifier;
	}
}
