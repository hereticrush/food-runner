package com.example.food_notes.ui.view.util.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.annotations.NonNull;

public abstract class UserRegexValidation {

    private static final Pattern INPUT_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[$#^%&@=_])(?=\\S+$).{8,24}$");
    public static boolean validate(@NonNull String input, @NonNull String input2) {
        Matcher matcher = INPUT_PATTERN.matcher(input);
        Matcher matcher1 = INPUT_PATTERN.matcher(input2);
        return matcher.matches() && matcher1.matches();
    }

}
