package com.example.food_notes.auth;

import com.google.firebase.auth.FirebaseAuth;

public class AppAuthentication {
    private AppAuthentication() {

    }

    public static final FirebaseAuth AUTH = FirebaseAuth.getInstance();
}
