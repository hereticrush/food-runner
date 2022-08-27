package com.example.food_notes.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexValidation {

    private final Pattern pattern;

    public RegexValidation() {
        this.pattern = Pattern.compile("^([a-zA-Z0-9!\\-%@$_]{8,24})$+");
    }

    public boolean validateUsername(String username) {
        return this.pattern.matcher(username).find();
    }
    public boolean validatePassword(String password) {
        return this.pattern.matcher(password).find();
    }
}
