package com.example.food_notes.db;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseRepository implements FirebaseDataSource {

    private static final String TAG = "repository";
    private final FirebaseStorage mStorage;
    private final FirebaseFirestore mFirestore;
    private Context mContext;
    private final CollectionReference mUserCollectionRef;
    private ArrayList<FoodPost> list;
    private List<DocumentSnapshot> snapshots;

    public FirebaseRepository(Context context) {
        this.mContext = context;
        this.mStorage = FirebaseStorage.getInstance();
        this.mFirestore = FirebaseFirestore.getInstance();
        mUserCollectionRef = mFirestore.collection(DatabaseConstants.USERS);
    }

    @Override
    public void getCurrentUserDocument(final String uid, final CustomCallback callback) {
        Query userQuery = mUserCollectionRef.whereEqualTo("user_id", uid);
        userQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            final DocumentReference documentReference = queryDocumentSnapshots
                    .getDocuments().get(0).getReference();
            callback.onEventSuccess(documentReference);
            Log.d(TAG, "onSuccess: " + documentReference.getPath());
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onEventFailure(e.getLocalizedMessage());
                Log.w(TAG, "onFailure: ", e);
            }
        });
    }

    @Override
    public void getFirestoreDocuments(final String uid, final CustomCallback callback) {
        snapshots = new ArrayList<>();
        if (uid != null) {
            Query query = mUserCollectionRef.document().collection(DatabaseConstants.FOOD_POSTS).whereEqualTo("user_id", uid);
            query.get().addOnSuccessListener(queryDocumentSnapshots -> queryDocumentSnapshots.getDocuments().forEach(documentSnapshot -> {
                Log.d(TAG, "getFirestoreDocuments: snaps:"+documentSnapshot.getData());
                snapshots.add(documentSnapshot);
                Log.d(TAG, "getFirestoreDocuments: list adding?");
                callback.onEventSuccess(snapshots);
            })).addOnFailureListener(e -> {
                callback.onEventFailure(e.getLocalizedMessage());
                Log.d(TAG, "onFailure: firestoredocments:");
            });
        }
    }

    @Override
    public void deletePostDocument(final DocumentReference documentReference) {

    }

    /**
     * Insert a post document to Firestore under user document as a sub-collection.
     * Once successful, fires a callback.
     * @param uid currentUserId
     * @param hashMap FoodPost hashMap
     */
    @Override
    public void createPostDocument(final String uid, final HashMap<String, Object> hashMap, final CustomCallback callback) {
        if (uid != null && hashMap != null) {
            getCurrentUserDocument(uid, new CustomCallback() {
                @Override
                public void onEventSuccess(Object o) {
                    final DocumentReference documentReference = (DocumentReference) o;
                    documentReference.collection(DatabaseConstants.FOOD_POSTS).add(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "onSuccess: added postDocPath:" + documentReference.getPath());
                                }
                            });
                }

                @Override
                public void onEventFailure(Object o) {
                    Log.d(TAG, "onEventFailure: post cannot be added");
                    callback.onEventFailure(o.toString());
                }
            });

        }
    }

    /**
     * Insert a user document to Firestore. Once successful, fires a callback
     * @param uid currentUserId
     * @param hashMap User hashMap
     */
    @Override
    public void createUserDocument(final String uid, final HashMap<String, Object> hashMap, final CustomCallback callback) {
        /*mFirestore.collection(DatabaseConstants.USERS).add(hashMap)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: error while adding document"+ e.getLocalizedMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "onSuccess: user document added:"+ documentReference.getPath());
                    }
                });*/
        if (uid != null && hashMap != null) {
            mUserCollectionRef.add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    callback.onEventSuccess(documentReference);
                    Log.d(TAG, "onSuccess: user document added:"+ documentReference.getPath());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onEventFailure(e);
                    Log.d(TAG, "onFailure: error while adding document"+ e.getLocalizedMessage());
                }
            });
        }
    }


    /**
     * Get list of documents
     * @return Arraylist of FoodPost objects
     */
    @Override
    public ArrayList<FoodPost> getList() {
        return list;
    }

    @Override
    public FirebaseStorage getStorageInstanceFromRepository() {
        return this.mStorage;
    }

}
