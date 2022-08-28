package com.example.food_notes.ui.view.model;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.food_notes.data.user.User;
import com.example.food_notes.ui.view.ApiLogin;
import com.example.food_notes.ui.view.util.regex.UserRegexValidation;

public class AuthenticationViewModel extends ViewModel {

    private String username;
    private String password;
    private MutableLiveData<User> mUser;
    private ApiLogin apiLogin;
    private UserRegexValidation validation;

    public void onClickLoginButton(View view) {
        apiLogin.onReady();
        if (!(validation.validate(username) || validation.validate(password))) {
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

    public ApiLogin getApiLogin() {
        return apiLogin;
    }
}
