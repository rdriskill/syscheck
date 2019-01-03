package com.github.rdriskill.syscheck.model;

import java.util.Date;
import java.util.UUID;

/**
 * @author rdriskill
 */
public class CheckPointHistory {
	private String id;
	private CheckPoint checkPoint;
	private CheckPointStatus status;
	private Date date;
	private String message;
	
	public CheckPointHistory(CheckPoint checkPoint, CheckPointStatus status, String message) {
		this();
		this.checkPoint = checkPoint;
		this.status = status;
		this.message = message;
	}
	
	public CheckPointHistory() {
		this.id = UUID.randomUUID().toString();
		this.date = new Date();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CheckPoint getCheckPoint() {
		return checkPoint;
	}

	public void setCheckPoint(CheckPoint checkPoint) {
		this.checkPoint = checkPoint;
	}

	public CheckPointStatus getStatus() {
		return status;
	}

	public void setStatus(CheckPointStatus status) {
		this.status = status;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CheckPointHistory other = (CheckPointHistory) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CheckPointHistory [checkPoint=" + checkPoint + ", status=" + status + ", date=" + date + ", message=" + message + "]";
	}
	
	
}
