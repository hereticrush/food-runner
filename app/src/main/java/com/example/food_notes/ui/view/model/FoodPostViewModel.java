package com.example.food_notes.ui.view.model;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.example.food_notes.data.foodpost.FoodPost;
import com.example.food_notes.data.user.User;
import com.example.food_notes.db.FirebaseDataSource;
import com.example.food_notes.ui.fragments.UserMainFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * FoodPostViewModel deals with operations
 * involving FoodPost model and connects with ui
 */
public class FoodPostViewModel extends ViewModel {

    private static final String TAG = "food_postVM";
    private final FirebaseDataSource mDataSource;
    private StorageReference mReference;
    private static final String PATH = "images/"+UUID.randomUUID()+".jpg";


    public FoodPostViewModel(FirebaseDataSource repository) {
        mDataSource = repository;
    }


    /**
     * Creates a hashMap from FoodPost attributes
     * @param uid
     * @param uriString
     * @param title
     * @param description
     * @param rating
     * @param latitude
     * @param longitude
     * @return
     */
    public HashMap<String, Object> createHashMapFromData(final String uid, final String uriString, final String title, final String description,
                                                         final float rating, final Double latitude, final Double longitude) {

        HashMap<String, Object> data = new HashMap<>();
        data.put("user_id", uid);
        data.put("image_uri", uriString);
        data.put("title", title);
        data.put("description", description);
        data.put("rating", rating);
        data.put("sent_at", FieldValue.serverTimestamp());
        data.put("latitude", latitude);
        data.put("longitude", longitude);

        return data;
    }

    /**
     * Uploads image to FireStorage and creates a foodPost document
     * @param uid
     * @param uri
     * @param title
     * @param desc
     * @param rating
     * @param latitude
     * @param longitude
     */
    public void uploadImageAndCreateFoodPost(final String uid, final Uri uri, final String title, final String desc, final float rating,
                                             final Double latitude, final Double longitude) {
        if (uri != null) {
            mReference = mDataSource.getStorage().getReference();
            mReference.child(PATH).putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d(TAG, "onSuccess: bytes: " + taskSnapshot.getBytesTransferred());
                        StorageReference reference = mDataSource.getStorage().getReference(PATH);
                        reference.getDownloadUrl().addOnSuccessListener(resultUri -> {
                            mDataSource.createPostDocument(
                                    uid,
                            createHashMapFromData(uid, resultUri.toString(), title, desc, rating, latitude, longitude)
                            );
                        }).addOnFailureListener(e -> Log.d(TAG, "onFailure: FAILED" + e.getLocalizedMessage()));
                    }).addOnFailureListener(e -> Log.d(TAG, "onFailure: failed"+e.getLocalizedMessage()));
        }
    }

    public void getUserRef() {
        mDataSource.getUserDocument(UserMainFragment.getUserId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.d(TAG, "error:"+ error.getLocalizedMessage());
                        } if (value != null && value.exists()) {
                            Log.d(TAG, value.getId()+" => "+value.getData());
                            Map<String, Object> data = value.getData();
                            if (data != null) {
                                String user_id = (String) data.get("user_id");
                                String user_email = (String) data.get("user_email");
                                User user = new User(user_id, user_email);
                                Log.d(TAG, "onEvent: "+user.getUser_id());
                            }
                        }
                    }
                });
    }

}
