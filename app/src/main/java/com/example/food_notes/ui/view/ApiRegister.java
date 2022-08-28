package com.example.food_notes.ui.view;

import io.reactivex.rxjava3.core.Completable;

public interface ApiRegister {

    void onSuccess(Completable completable);

    void onFailed(String log);
}
