package com.Project1.demo.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum UserStatus {
	@JsonProperty("active")
	ACTIVE,
	@JsonProperty("inactive")
	INACTIVE,
	@JsonProperty("none")
	NONE
}
