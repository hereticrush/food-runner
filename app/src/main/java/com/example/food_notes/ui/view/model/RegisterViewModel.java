package com.example.food_notes.ui.view.model;

import android.view.View;

import androidx.lifecycle.ViewModel;

import com.example.food_notes.ui.view.ApiRegister;
import com.example.food_notes.ui.view.util.regex.RegexValidation;

public class RegisterViewModel extends ViewModel {

    private String username;
    private String password;
    private ApiRegister apiRegister;
    private final RegexValidation validation = new RegexValidation();

    public void onClickRegisterButton(View view) {
        apiRegister.onHold();
        if (!(validation.validateUsername(username) || validation.validatePassword(password))) {
            apiRegister.onFailed("");
        } else if (validation.validateUsername(username)) {
            apiRegister.onFailed("Invalid username");
        } else if (validation.validatePassword(password)) {
            apiRegister.onFailed("Invalid password");
        } else {
            apiRegister.onSuccess();
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

    public ApiRegister getApiRegister() {
        return apiRegister;
    }
}
