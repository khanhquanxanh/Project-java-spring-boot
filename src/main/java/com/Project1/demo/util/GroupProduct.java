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
	Accessories;
	
	public static GroupProduct fromString(String value) {
        for (GroupProduct g : GroupProduct.values()) {
            JsonProperty prop = null;
            try {
                prop = g.getClass().getField(g.name()).getAnnotation(JsonProperty.class);
            } catch (NoSuchFieldException e) { }
            if ((prop != null && prop.value().equalsIgnoreCase(value)) || g.name().equalsIgnoreCase(value)) {
                return g;
            }
        }
        throw new IllegalArgumentException("No enum constant for value " + value);
    }
}
