package com.example.food_notes.ui.view.model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.food_notes.data.user.User;
import com.example.food_notes.data.user.UserDataSource;
import com.example.food_notes.ui.view.ApiClient;
import com.example.food_notes.ui.view.CustomHandlers;
import com.example.food_notes.ui.view.util.regex.UserRegexValidation;

import io.reactivex.rxjava3.core.Single;

public class AuthenticationViewModel extends AndroidViewModel implements CustomHandlers {

    private LiveData<String> username;
    private LiveData<String> password;
    private final UserDataSource mDataSource;
    Application application;
    private ApiClient apiClient;

    public AuthenticationViewModel(UserDataSource repository, Application application){
        super(application);
        mDataSource = repository;
    }

    public LiveData<String> getUsername() {
        return this.username;
    }

    public void setUsername(LiveData<String> username) {
        this.username = username;
    }

    public LiveData<String> getPassword() {
        return this.password;
    }

    public void setPassword(LiveData<String> password) {
        this.password = password;
    }

    public ApiClient getApi() {
        return apiClient;
    }

    @Override
    public void onButtonClicked() {
        if (!(UserRegexValidation.validate(getUsername().getValue(), getPassword().getValue()))) {
            apiClient.onFailed("Credentials are invalid");
        } else {
            apiClient.onSuccess();
        }
    }
}
