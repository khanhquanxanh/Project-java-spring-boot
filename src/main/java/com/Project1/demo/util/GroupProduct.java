package com.Project1.demo.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GroupProduct {
	@JsonProperty("shoes")
	Shoes,
	@JsonProperty("tshirts")
	Tshirts,
	@JsonProperty("pants")
	Pants,
	@JsonProperty("hoodie")
	Hoodie,
	@JsonProperty("outer")
	Outer,
	@JsonProperty("jackets")
	Jackets,
	@JsonProperty("accessories")
	Accessories
}
