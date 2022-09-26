package com.example.food_notes.db;

import com.example.food_notes.data.foodpost.FoodPost;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;

public interface FirebaseDataSource {

    ArrayList<FoodPost> getList();

    FirebaseStorage getStorageInstanceFromRepository();

    void getCurrentUserDocument(final String uid, final CustomCallback callback);

    void createPostDocument(final String uid, final HashMap<String, Object> hashMap, final CustomCallback callback);

    void createUserDocument(final String uid, final HashMap<String, Object> hashMap, final CustomCallback callback);

    void getFirestoreDocuments(final String uid, final CustomCallback callback);

    void deletePostDocument(final DocumentReference documentReference, final CustomCallback callback);

}
