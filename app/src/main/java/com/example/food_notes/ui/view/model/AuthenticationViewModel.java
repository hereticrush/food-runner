package com.example.food_notes.ui.view.model;

import androidx.lifecycle.ViewModel;

import com.example.food_notes.auth.AppAuthentication;
import com.example.food_notes.db.FirebaseDataSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;

/**
 * ViewModel connects the user interface and data flow together,
 * AuthenticationViewModel deals with sign up and login events
 */
public class AuthenticationViewModel extends ViewModel {

    private final FirebaseDataSource mDataSource;
    private FirebaseAuth auth;

    public AuthenticationViewModel(FirebaseDataSource repository){
        mDataSource = repository;
        auth = AppAuthentication.AUTH;
    }

    public HashMap<String, Object> createUserHashmap(final String uid, final String email) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("user_id", uid);
        data.put("user_email", email.trim());
        data.put("created_at", FieldValue.serverTimestamp());
        return data;
    }

    public void createUserDocumentForFirestore(final String uid, final String email) {
        HashMap<String, Object> data = createUserHashmap(uid, email);
        mDataSource.createUserDocument(uid, data);
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

}
