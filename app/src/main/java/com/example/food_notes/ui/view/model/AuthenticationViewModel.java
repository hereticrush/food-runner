package com.example.food_notes.ui.view.model;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.food_notes.data.user.User;
import com.example.food_notes.data.user.UserDataSource;
import com.example.food_notes.ui.view.ApiClient;
import com.example.food_notes.ui.view.util.regex.UserRegexValidation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * ViewModel connects the user interface and data flow together,
 * AuthenticationViewModel deals with sign up and login events
 */
public class AuthenticationViewModel extends ViewModel implements ApiClient {

    // disposable container
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final UserDataSource mDataSource;

    public AuthenticationViewModel(UserDataSource repository){
        mDataSource = repository;
    }

    /**
     * Insert new user object into local database, also setting it's time of creation
     * @param user User entity object
     * @return {@link Completable} which completes once user object is updated
     */
    public Completable insertUserToLocalDB(User user) {
        Date currentDate = Calendar.getInstance().getTime();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:s", Locale.getDefault());
        String dateString = format.format(currentDate);
        user.setCreatedAt(dateString);
        return mDataSource.insertUser(user).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).delaySubscription(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread());
    }

    /*/**
     * Get a specific user object from database
     * @param username String user.username
     * @return emits either 0 or 1, returns a match or an error
     */
    public Maybe<User> getUser(final String uid) {
            return mDataSource.getUserById(uid)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io());
    }



    /**
     * Fetches all users registered in database
     * @return RxJava Flowable list of user object
     */
    public Flowable<List<User>> getAllUsers() {
        return mDataSource.getAllUsers().subscribeOn(Schedulers.computation());
    }


    /**
     * Checks whether user input is violating the regex rules or not
     * @param username text input field username
     * @param password text input field password
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

    /*/**
     * Connects with the observer in ui thread,
     * queries for user object and attempts to log the user in
     * @param username text input field username
     * @param password text input field password
     * @return User object wrapped in RxJava Maybe,
     * either returns 0 or 1, otherwise an error
     */
    /*public Maybe<User> login(final String username, final String password) {
        return getUser(username, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }*/

    @Override
    protected void onCleared() {
        disposable.clear();
        super.onCleared();
    }

    @Override
    public void onSuccess() {
        Log.v("SUCCESS", "works as intended");
    }

    @Override
    public void onFailed(String log) {
        Log.e("FAILED_TRANSACTION", log);
    }

    @Override
    public void whenCompleted() {
        Log.d("COMPLETED", "done");
    }
}
