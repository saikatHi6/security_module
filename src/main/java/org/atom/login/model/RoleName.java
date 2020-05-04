package org.atom.login.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RoleName {
	ROLE_ADMIN("ROLE_ADMIN"),ROLE_USER("ROLE_USER");
	
	private String roleType;
	
	private RoleName(String roleType){
		this.roleType = roleType;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	
	
	
}
