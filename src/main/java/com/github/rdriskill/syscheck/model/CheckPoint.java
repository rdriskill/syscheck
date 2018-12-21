package com.github.rdriskill.syscheck.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

/**
 * @author rdriskill
 */
public class CheckPoint {
	
	@Id
	private String id;
	
	@NotBlank
	private String name;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private CheckPointType sysCheckPointType;
	
	@NotBlank
	private String url;
	
	private String user;
	private String password;
	
	@PastOrPresent
	private Date startTime;
	
	public CheckPoint() {
		this.id = UUID.randomUUID().toString();
		this.startTime = new Date();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CheckPointType getSysCheckPointType() {
		return sysCheckPointType;
	}

	public void setSysCheckPointType(CheckPointType sysCheckPointType) {
		this.sysCheckPointType = sysCheckPointType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
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
		CheckPoint other = (CheckPoint) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SysCheckPoint [id=" + id + ", name=" + name + ", sysCheckPointType=" + sysCheckPointType + "]";
	}
}
