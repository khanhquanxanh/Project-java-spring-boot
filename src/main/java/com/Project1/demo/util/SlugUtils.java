package com.Project1.demo.util;

public class SlugUtils {

    public static String toSlug(String input) {
        String slug = input.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        return slug;
    }
}