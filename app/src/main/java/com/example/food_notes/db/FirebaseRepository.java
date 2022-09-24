package com.example.food_notes.db;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.food_notes.data.foodpost.FoodPost;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseRepository implements FirebaseDataSource {

    private static final String TAG = "repository";
    private final FirebaseStorage mStorage;
    private final FirebaseFirestore mFirestore;
    private Context mContext;
    private DocumentReference mReference;
    private final CollectionReference mUserCollectionRef;
    private final ArrayList<FoodPost> list;

    public FirebaseRepository(Context context) {
        this.mContext = context;
        this.mStorage = FirebaseStorage.getInstance();
        this.mFirestore = FirebaseFirestore.getInstance();
        mUserCollectionRef = mFirestore.collection(DatabaseConstants.USERS);
        this.list = new ArrayList<>();
    }

    @Override
    public DocumentReference getUserDocument(final String uid) {
        Query query_user = mUserCollectionRef.whereEqualTo("user_id", uid);
        query_user.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    DocumentSnapshot d = querySnapshot.getDocuments().get(0);
                    mReference = d.getReference();
                    Log.d(TAG, d.getId()+" => "+d.getData());
                } else {
                    Log.d(TAG, "Error getting documents: "+ task.getException());
                }
            }
        }).addOnFailureListener(e -> Log.d(TAG, "getUserDocument: "+e.getLocalizedMessage()));
        return mReference;
    }

    @Override
    public void getFirestoreDocument(final DocumentReference documentReference, final String uid) {
        mUserCollectionRef.document().collection(DatabaseConstants.FOOD_POSTS)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    Log.d(TAG, "onEvent: ERROR");
                                    return;
                                } if (value != null && !value.isEmpty()) {
                                    for (DocumentSnapshot s: value.getDocuments()) {
                                        Map<String, Object> data = s.getData();

                                        String uid = (String) data.get("user_id");
                                        String image_uri = (String) data.get("image_uri");
                                        String title = (String) data.get("title");
                                        String desc = (String) data.get("description");
                                        float rating = (float) data.get("rating");
                                        Double lat = (Double) data.get("latitude");
                                        Double lon = (Double) data.get("longitude");

                                        FoodPost foodPost = new FoodPost(uid, image_uri, title, desc, rating, lat, lon);

                                        list.add(foodPost);
                                    }
                                } else {
                                    Log.d(TAG, "onEvent: Failed to get doc");
                                }
                            }
                        });
    }

    @Override
    public FirebaseFirestore getFirestore() {
        return mFirestore;
    }

    @Override
    public FirebaseStorage getStorage() {
        return mStorage;
    }



    /**
     * Insert a post document to Firestore
     * @param uid currentUserId
     * @param hashMap FoodPost hashMap
     */
    @Override
    public void createPostDocument(final String uid, final HashMap<String, Object> hashMap) {
        if (uid != null && hashMap != null) {
            mUserCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(0);
                    snapshot.getReference().collection(DatabaseConstants.FOOD_POSTS)
                            .add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "onSuccess: "+documentReference.getId());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: error:"+e.getLocalizedMessage());
                                }
                            });
                }
            });
        } else {
            Log.d(TAG, "createPostDocument: I do not know what to do");
        }
    }

    /**
     * Insert a user document to Firestore
     * @param uid currentUserId
     * @param hashMap User hashMap
     */
    @Override
    public void createUserDocument(final String uid, final HashMap<String, Object> hashMap) {
        mFirestore.collection(DatabaseConstants.USERS).add(hashMap)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: error doc add"+ e.getLocalizedMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "onSuccess: USER doc added:"+ documentReference.getPath());
                    }
                });
    }

    /**
     * Get list of documents
     * @return Arraylist of FoodPost objects
     */
    @Override
    public ArrayList<FoodPost> getList() {
        return list;
    }

}
