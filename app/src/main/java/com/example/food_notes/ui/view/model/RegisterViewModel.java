package com.example.food_notes.ui.view.model;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.food_notes.data.user.User;
import com.example.food_notes.data.user.UserDataSource;
import com.example.food_notes.ui.view.ApiRegister;
import com.example.food_notes.ui.view.CustomHandlers;
import com.example.food_notes.ui.view.util.regex.UserRegexValidation;


import io.reactivex.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class RegisterViewModel extends ViewModel implements CustomHandlers {

    public MutableLiveData<String> username = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    private final UserDataSource mDataSource;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private Completable mUser;
    private ApiRegister apiRegister;
    private UserRegexValidation validation;

    public RegisterViewModel(UserDataSource repository){
        mDataSource = repository;
    }

    public Completable insertOrUpdateUser(String username, String password) {

    }

    @NonNull
    public String getUsername() {
        return this.username.getValue();
    }

    public void setUsername(String username) {
        this.username.setValue(username);
    }

    @NonNull
    public String getPassword() {
        return this.password.getValue();
    }

    public void setPassword(String password) {
        this.password.setValue(password);
    }

    @Override
    public void onRegisterButtonClicked() {
        if (!(UserRegexValidation.validate(getUsername(), getPassword()))) {
            apiRegister.onFailed("Invalid username or password");
        }  else {
            CompositeDisposable disposable = new CompositeDisposable();
        }
    }

    public ApiRegister getApiRegister() {
        return apiRegister;
    }


}
