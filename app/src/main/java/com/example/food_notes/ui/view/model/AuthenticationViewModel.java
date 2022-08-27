package com.example.food_notes.ui.view.model;

import android.view.View;

import androidx.lifecycle.ViewModel;

import com.example.food_notes.ui.view.ApiLogin;
import com.example.food_notes.ui.view.util.regex.RegexValidation;

public class AuthenticationViewModel extends ViewModel {

    private String username;
    private String password;
    private ApiLogin apiLogin;
    private final RegexValidation validation = new RegexValidation();

    public void onClickLoginButton(View view) {
        apiLogin.onReady();
        if (!(validation.validateUsername(username) || validation.validatePassword(password))) {
            apiLogin.onFailed("Credentials are invalid");
        } else {
            apiLogin.onSuccess();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
