package com.Project1.demo.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Gender {
	@JsonProperty("male")
	MALE,
	@JsonProperty("female")
	FEMALE,
	@JsonProperty("other")
	OTHER
}
