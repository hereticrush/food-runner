package com.example.food_notes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserRegisterTest extends ExampleInstrumentedTest {

    private Pattern pattern;
    private Matcher matcher;

    @Before
    public void setPattern() {
        this.pattern = Pattern.compile("^([\\p{Alnum}!@#$%-]){7,24}[^\\s]\1*$");
    }

    @Test
    public void regexTextMatches_True() {
        String username = "00BjarneSTOUSTRUP--3";
        matcher = pattern.matcher(username);
        assertTrue(matcher.matches());
    }

    @Test
    public void regexTextMatches_False() {
        String password = "oqweoqpw1239130-----12930312??ASHDUD";
        matcher = pattern.matcher(password);
        assertFalse(matcher.matches());
    }

    @Test
    public void regexTextMatches_Both() {
        String password = "oqweoqpw1239130-----12930312??ASHDUD";
        matcher = pattern.matcher(password);
        assertTrue(matcher.matches());
        assertFalse(matcher.matches());
    }
}
