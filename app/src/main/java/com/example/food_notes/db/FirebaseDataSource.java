package com.example.food_notes.db;

import com.example.food_notes.data.foodpost.FoodPost;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;

public interface FirebaseDataSource {

    ArrayList<FoodPost> getList();

    FirebaseFirestore getFirestore();

    FirebaseStorage getStorage();

    DocumentReference getUserDocument(final String uid);

    void createPostDocument(final String uid, final HashMap<String, Object> hashMap);

    void createUserDocument(final String uid, final HashMap<String, Object> hashMap);

    void getFirestoreDocument(final DocumentReference documentReference, final String uid);

}
