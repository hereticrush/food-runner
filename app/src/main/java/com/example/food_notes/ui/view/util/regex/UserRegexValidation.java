package com.example.food_notes.ui.view.util.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.annotations.NonNull;

public abstract class UserRegexValidation {

    public static final Pattern INPUT_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[$#^%&@=_])(?=\\S+$).{8,24}$");

}
