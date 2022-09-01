package com.example.food_notes.ui.view.model;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.food_notes.data.user.User;
import com.example.food_notes.data.user.UserDataSource;
import com.example.food_notes.ui.view.ApiClient;
import com.example.food_notes.ui.view.util.regex.UserRegexValidation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.internal.operators.flowable.FlowableCollect;
import io.reactivex.rxjava3.internal.operators.flowable.FlowableCollectSingle;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AuthenticationViewModel extends ViewModel {

    private boolean isInputValid;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final UserDataSource mDataSource;
    private User mUser;
    private ApiClient apiClient;

    public AuthenticationViewModel(UserDataSource repository){
        mDataSource = repository;
    }


    public ApiClient getApi() {
        return apiClient;
    }

    /**
     * Insert new user object into database, also setting it's time of creation
     * @param username username
     * @param password password
     * @return {@link Completable} which completes once user object is updated
     */
    public Completable insertUser(final String username, final String password) {
        mUser = new User(username, password);
        Date currentDate = Calendar.getInstance().getTime();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:s", Locale.getDefault());
        String dateString = format.format(currentDate);
        mUser.setCreated_at(dateString);
        return mDataSource.insertUser(mUser).doOnComplete(() -> System.out.println(mUser + "added."))
                .doOnError(throwable -> Log.e("ERROR", "Error occurred"));
    }

    public Single<User> getUser(final String username) {
       return getAllUsers()
               .flatMap(Flowable::fromIterable)
               .filter(i -> i.getUsername().matches(username)).toObservable().singleOrError();
    }


    public Flowable<List<User>> getAllUsers() {
        return Flowable.fromAction(mDataSource::getAllUsers);
    }

    /**
     *
     * @param username username
     * @param password password
     */
    private void validateUserCredentials(String username, String password) {

        if (!(UserRegexValidation.INPUT_PATTERN.matcher(username.trim()).matches()
                || UserRegexValidation.INPUT_PATTERN.matcher(password.trim()).matches())) {
            setInputValid(false);
            return;
        }
        if (!UserRegexValidation.INPUT_PATTERN.matcher(username.trim()).matches()) {
            setInputValid(false);
            return;
        }
        if (!UserRegexValidation.INPUT_PATTERN.matcher(password.trim()).matches()) {
            setInputValid(false);
            return;
        }
        setInputValid(true);
    }

    public boolean isInputValid() {
        return this.isInputValid;
    }

    public void setInputValid(boolean inputValid) {
        isInputValid = inputValid;
    }

    @Override
    protected void onCleared() {
        disposable.clear();
        super.onCleared();
    }

}
