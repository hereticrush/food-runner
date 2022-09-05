package com.example.food_notes.ui.view.model;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.food_notes.data.user.User;
import com.example.food_notes.data.user.UserDataSource;
import com.example.food_notes.ui.view.ApiClient;
import com.example.food_notes.ui.view.util.regex.UserRegexValidation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * ViewModel connects the user interface and data flow together,
 * AuthenticationViewModel deals with sign up and login events
 */
public class AuthenticationViewModel extends ViewModel implements ApiClient{

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
     * @param username username String
     * @param password password String
     * @return {@link Completable} which completes once user object is updated
     */
    public Completable insertUser(final String username, final String password) {
        mUser = new User(username, password);
        Date currentDate = Calendar.getInstance().getTime();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:s", Locale.getDefault());
        String dateString = format.format(currentDate);
        mUser.setCreated_at(dateString);
        return mDataSource.insertUser(mUser).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).delaySubscription(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread());
    }


    public Single<User> getUser(final String username, final String password) {

        return getAllUsers()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .flatMap(Flowable::fromIterable)
                        .filter(user -> user.getUsername().matches(username)
                        && user.getPassword().matches(password))
                        .toObservable().singleOrError();

    }

    /**
     * Queries database to get a username.
     * @param id
     * @return String that represents username, wrapped
     * in a Flowable
     */
    public Flowable<String> getUsername(final int id) {
        return getAllUsers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .flatMap(Flowable::fromIterable)
                .filter(user -> id == user.getUser_id())
                .concatMap(user -> Flowable.just(user.getUsername()));
    }


    /**
     * Fetches all users registered in database
     * @return Flowable list of user object
     */
    public Flowable<List<User>> getAllUsers() {
        return mDataSource.getAllUsers();
    }


    /**
     * Checks whether user input is violating the regex rules
     * @param username username
     * @param password password
     * @return boolean output for validation
     */
    public boolean validateUserInput(String username, String password) {

        if (!(UserRegexValidation.INPUT_PATTERN.matcher(username).matches()
                || UserRegexValidation.INPUT_PATTERN.matcher(password).matches())) {
            return false;
        }
        if (!UserRegexValidation.INPUT_PATTERN.matcher(username).matches()) {
            return false;
        }
        if (!UserRegexValidation.INPUT_PATTERN.matcher(password).matches()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onCleared() {
        disposable.clear();
        super.onCleared();
    }

    @Override
    public void onSuccess() {
        System.out.println("Transaction completed");
    }

    @Override
    public void onFailed(String log) {
       Log.e("FAILED", log);
    }
}
