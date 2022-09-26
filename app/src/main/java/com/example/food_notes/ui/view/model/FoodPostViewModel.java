package com.example.food_notes.ui.view.model;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.food_notes.data.foodpost.FoodPost;
import com.example.food_notes.db.CustomCallback;
import com.example.food_notes.db.DatabaseConstants;
import com.example.food_notes.db.FirebaseDataSource;
import com.example.food_notes.ui.adapters.FoodPostRecyclerViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * FoodPostViewModel deals with operations
 * involving FoodPost model and connects with ui
 */
public class FoodPostViewModel extends ViewModel {

    private static final String TAG = "food_postVM";
    private final FirebaseDataSource mDataSource;
    private StorageReference mReference;
    private DocumentReference mRef;
    private MutableLiveData<ArrayList<FoodPost>> mList = new MutableLiveData<>();
    private ArrayList<FoodPost> list;
    private static final String PATH = "images/"+UUID.randomUUID()+".jpg";


    public FoodPostViewModel(FirebaseDataSource repository) {
        mDataSource = repository;
        list = new ArrayList<>();
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
        data.put("post_id", UUID.randomUUID().toString());
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
        if (uid != null && uri != null) {
            mReference = mDataSource.getStorageInstanceFromRepository().getReference();
            mReference.child(PATH).putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d(TAG, "onSuccess: bytes: " + taskSnapshot.getBytesTransferred());
                        StorageReference reference = mDataSource.getStorageInstanceFromRepository().getReference(PATH);
                        reference.getDownloadUrl().addOnSuccessListener(resultUri -> {
                            mDataSource.createPostDocument(
                                    uid,
                                    createHashMapFromData(uid, resultUri.toString(), title, desc, rating, latitude, longitude),
                                    new CustomCallback() {
                                        @Override
                                        public void onEventSuccess(Object o) {
                                            Log.d(TAG, "uploadImageAndCreateFoodPost: CREATION SUCCESS:");
                                        }

                                        @Override
                                        public void onEventFailure(Object o) {
                                            Log.d(TAG, "onEventFailure: CREATION FAILED");
                                        }
                                    }
                            );
                        }).addOnFailureListener(e -> Log.d(TAG, "onFailure: FAILED" + e.getLocalizedMessage()));
                    }).addOnFailureListener(e -> Log.d(TAG, "onFailure: failedToPutFileUri"+e.getLocalizedMessage()));
        }
    }

    public DocumentReference getUserDocument(final String uid) {
        mDataSource.getCurrentUserDocument(uid, new CustomCallback() {
            @Override
            public void onEventSuccess(Object o) {
                Log.d(TAG, "onEventSuccess: "+o.toString());
                mRef = (DocumentReference) o;
            }

            @Override
            public void onEventFailure(Object o) {
                Log.d(TAG, "onEventFailure: FAILED CALLBACK");
            }
        });
        return mRef;
    }

   public void deletePostDocument(final String uid, final String item_id) {
        if (uid != null && item_id != null) {
            DocumentReference reference = getUserDocument(uid);
            Log.d(TAG, "userRef:"+reference.getId()+" => "+reference.getPath());
            CollectionReference posts = reference.collection(DatabaseConstants.FOOD_POSTS);
                    posts.whereEqualTo("user_id", uid);
            posts.whereEqualTo("post_id", item_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        task.getResult().getDocuments().forEach(documentSnapshot -> {
                            documentSnapshot.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "onSuccess: DELETED ITEM:"+documentSnapshot.getReference().getPath());
                                }
                            });
                        });
                    } else
                        Log.d(TAG, "Failed:"+task.getException());
                }
            });
        }
    }

    public ArrayList<FoodPost> getDocumentArrayById(final String uid) {
        mDataSource.getFirestoreDocuments(uid, new CustomCallback() {
            @Override
            public void onEventSuccess(Object o) {
                Log.d(TAG, "onEventSuccess: "+o.toString());
                FoodPost foodPost = (FoodPost) o;
                Log.d(TAG, "onEventSuccess: foodpost?"+foodPost);
                list.add(foodPost);
            }

            @Override
            public void onEventFailure(Object o) {
                Log.d(TAG, "onEventFailure: failed"+o.toString());
            }
        });
        return list;
    }

    public LiveData<ArrayList<FoodPost>> getData() {
        mList.setValue(list);
        return mList;
    }

}
