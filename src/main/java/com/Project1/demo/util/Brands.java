package com.Project1.demo.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Brands {
	@JsonProperty("nike")
	Nike,
	@JsonProperty("adidas")
	Adidas,
	@JsonProperty("puma")
	Puma,
	@JsonProperty("spike")
	Spike;
	
	public static Brands fromString(String value) {
        for (Brands b : Brands.values()) {
            JsonProperty prop = null;
            try {
                prop = b.getClass().getField(b.name()).getAnnotation(JsonProperty.class);
            } catch (NoSuchFieldException e) { }
            if ((prop != null && prop.value().equalsIgnoreCase(value)) || b.name().equalsIgnoreCase(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("No enum constant for value " + value);
    }
}
