package com.example.food_notes.ui.view.model;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.internal.operators.flowable.FlowableCollect;
import io.reactivex.rxjava3.internal.operators.flowable.FlowableCollectSingle;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

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
        return mDataSource.insertUser(mUser).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).delaySubscription(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread());
    }

    public Maybe<User> getUser(final String username) {
       return getAllUsers()
               .observeOn(AndroidSchedulers.mainThread())
               .subscribeOn(Schedulers.io())
               .flatMap(Flowable::fromIterable)
               .filter(user -> username.matches(user.getUsername()))
               .doOnError(throwable -> Log.e("ERROR", throwable.getLocalizedMessage()))
               .toObservable().singleElement().delay(1, TimeUnit.SECONDS,
                       Schedulers.io());
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
            System.out.println("1");
            return false;
        }
        if (!UserRegexValidation.INPUT_PATTERN.matcher(username).matches()) {
            System.out.println("2");
            return false;
        }
        if (!UserRegexValidation.INPUT_PATTERN.matcher(password).matches()) {
            System.out.println("3");
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
        System.out.println("Successfully added user");
    }

    @Override
    public void onFailed(String log) {
       Log.e("FAILED", log);
    }
}
