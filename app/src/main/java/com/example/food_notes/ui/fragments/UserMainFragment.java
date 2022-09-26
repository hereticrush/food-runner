package com.example.food_notes.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_notes.R;
import com.example.food_notes.data.foodpost.FoodPost;
import com.example.food_notes.databinding.FragmentUserMainBinding;
import com.example.food_notes.db.DatabaseConstants;
import com.example.food_notes.injection.Injection;
import com.example.food_notes.ui.adapters.FoodPostRecyclerViewAdapter;
import com.example.food_notes.ui.adapters.RecyclerViewItemClickListener;
import com.example.food_notes.ui.view.factory.AuthenticationViewModelFactory;
import com.example.food_notes.ui.view.factory.FoodPostModelViewFactory;
import com.example.food_notes.ui.view.model.AuthenticationViewModel;
import com.example.food_notes.ui.view.model.FoodPostViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Map;

/**
 * Fragment structure that has user card items, provide functionalities
 * such as adding items and removing
 * main functionality point for
 * application
 */
public class UserMainFragment extends Fragment implements RecyclerViewItemClickListener{

    private static final String TAG = "usr_main";
    private static final String LOGGED_USERID = "LOGGED_USERID";
    private static final boolean LOGIN_STATE = true;
    private static String USER_ID;

    // view binding
    private FragmentUserMainBinding binding;

    private RecyclerView mRecyclerView;
    private FoodPostRecyclerViewAdapter mAdapter;

    // view models
    private FoodPostModelViewFactory mFoodPostFactory;
    private FoodPostViewModel mViewModel;
    private AuthenticationViewModelFactory modelFactory;
    private AuthenticationViewModel authenticationViewModel;

    private FirebaseAuth auth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private DocumentReference mUserReference;
    private Query query;

    // bottom nav view
    private BottomNavigationView bottomNavigationView;

    // navigation components
    private NavController navController;
    private NavBackStackEntry navBackStackEntry;

    private ArrayList<FoodPost> postArrayList;


    public UserMainFragment() {}

    @Nullable
    public static UserMainFragment newInstance(int user_id) {
        UserMainFragment fragment = new UserMainFragment();
        Bundle args = new Bundle();
        args.putInt(LOGGED_USERID, user_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFoodPostFactory = Injection.provideFoodPostViewModelFactory(requireActivity().getApplicationContext());
        mViewModel = mFoodPostFactory.create(FoodPostViewModel.class);
        navController = NavHostFragment.findNavController(this);
        modelFactory = Injection.provideAuthViewModelFactory(requireActivity().getApplicationContext());
        authenticationViewModel = modelFactory.create(AuthenticationViewModel.class);

        postArrayList = new ArrayList<>();

        auth = authenticationViewModel.getAuth();
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        navBackStackEntry = navController.getPreviousBackStackEntry();

        if (auth.getCurrentUser() != null) {
            USER_ID = auth.getCurrentUser().getUid();
            Log.d("userMain", "onCreate: id:"+USER_ID);
        }
        //keeping active userReferenceFromFirestore in onCreate
        mViewModel.getUserDocument(getUserId());
        initData();
        mViewModel.getDocumentArrayById(getUserId());

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserMainBinding.inflate(inflater, container, false);
        displayCardItems();
        bottomNavigationView = binding.bottomNavView;
        // bottom bar navigation setting
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // clicking on items on bottomNavView
               if (item.getItemId() == R.id.action_add_card_item) {
                    toAddPostFragment(getUserId());
                    return true;
                } else if (item.getItemId() == R.id.action_logout_user) {
                   navBackStackEntry = navController.getCurrentBackStackEntry();
                   int destination = navController.getGraph().getStartDestinationId();
                   NavOptions navOptions = new NavOptions.Builder().setPopUpTo(destination, true, true).build();
                   navController.navigate(destination, null, navOptions);
                   auth.signOut();
                   return true;
               }
                return false;
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        query = mFirestore.collection(DatabaseConstants.USERS).document().collection(DatabaseConstants.FOOD_POSTS).whereEqualTo("user_id", getUserId());
        mViewModel.getData().observe(getViewLifecycleOwner(), new Observer<ArrayList<FoodPost>>() {
            @Override
            public void onChanged(ArrayList<FoodPost> foodPosts) {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Display cardview row items in fragment
     */
    private void displayCardItems() {
        if (mAdapter == null) {
            mRecyclerView = binding.rvUserMain;
            CollectionReference userColRef = mFirestore.collection(DatabaseConstants.USERS);
            userColRef.whereEqualTo("user_id", getUserId())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot snapshot = task.getResult();
                                snapshot.getDocuments().forEach(documentSnapshot -> {
                                    //final DocumentReference documentReference = documentSnapshot.getReference();
                                    //query = documentReference.collection(DatabaseConstants.FOOD_POSTS).whereEqualTo("user_id", getUserId());
                                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                task.getResult().getDocuments().forEach(ds -> {
                                                    DocumentReference posts = ds.getReference();
                                                    posts.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                                            if (value != null && value.exists()) {
                                                                Log.d(TAG, "onEvent: "+value.getReference().getPath());
                                                                Map<String, Object> data = value.getData();
                                                                String post_id = (String) data.get("post_id");
                                                                String user_id = (String) data.get("user_id");
                                                                String image_uri = (String) data.get("image_uri");
                                                                String title = (String) data.get("title");
                                                                String description = (String) data.get("description");
                                                                Double rating = (Double) data.get("rating");
                                                                Double latitude = (Double) data.get("latitude");
                                                                Double longitude = (Double) data.get("longitude");

                                                                FoodPost foodPost = new FoodPost(post_id, user_id, image_uri, title, description, rating.floatValue(), latitude, longitude);
                                                                postArrayList.add(foodPost);
                                                                //Log.d(TAG, "data:"+ foodPost);
                                                            }
                                                        }
                                                    });
                                                });
                                            }
                                        }
                                    });
                                });
                            }
                        }
                    });
            mAdapter = new FoodPostRecyclerViewAdapter(this.getContext(), query, this);
            binding.rvUserMain.setLayoutManager(new LinearLayoutManager(this.getContext()));
            binding.rvUserMain.setHasFixedSize(true);
            binding.rvUserMain.setItemAnimator(new DefaultItemAnimator());
            binding.rvUserMain.setAdapter(mAdapter);
        }
    }


    /**
     * Initializes a list of documents from firestore collection
     */
    public void initData() {
        mViewModel.getDocumentArrayById(getUserId());
        //postArrayList.addAll(mViewModel.getDocumentArrayById(getUserId()));

        /*CollectionReference userColRef = mFirestore.collection(DatabaseConstants.USERS);
        Query user = userColRef.whereEqualTo("user_id", USER_ID);
        user.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot snapshot = task.getResult();
                    snapshot.getDocuments().forEach(documentSnapshot -> {
                            documentSnapshot.getReference().collection(DatabaseConstants.FOOD_POSTS)
                                    .whereEqualTo("user_id", USER_ID)
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot snapshots = task.getResult();
                                                snapshots.getDocuments().forEach(ds -> {
                                                    Map<String, Object> data = ds.getData();

                                                    String post_id = (String) data.get("post_id");
                                                    String user_id = (String) data.get("user_id");
                                                    String image_uri = (String) data.get("image_uri");
                                                    String title = (String) data.get("title");
                                                    String description = (String) data.get("description");
                                                    Double rating = (Double) data.get("rating");
                                                    Double latitude = (Double) data.get("latitude");
                                                    Double longitude = (Double) data.get("longitude");

                                                    FoodPost foodPost = new FoodPost(post_id, user_id, image_uri, title, description, rating.floatValue(), latitude, longitude);
                                                    postArrayList.add(foodPost);
                                                    mAdapter.notifyItemInserted(postArrayList.lastIndexOf(foodPost));
                                                });
                                            }
                                        }
                                    });
                    });
                }
            }
        });*/
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUserReference = null;
        binding = null; // avoid memory leak
    }

    /**
     * Navigates user to the AddPostFragment
     */
    private void toAddPostFragment(final String id) {
        Bundle args = new Bundle();
        args.putString(LOGGED_USERID, id);
        args.putBoolean("LOGGED_IN", isLoginState());
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.addPostFragment, true, true)
                        .build();
        navController.navigate(R.id.addPostFragment, args, navOptions);
    }

    public static boolean isLoginState() {
        return LOGIN_STATE;
    }

    public static String getUserId() {
        return USER_ID;
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onLongItemClick(int position) {
        Toast.makeText(requireActivity().getApplicationContext(), "Item deleted", Toast.LENGTH_SHORT).show();
        removeItem(position);
    }

    public void removeItem(int position) {
        postArrayList.remove(position);
        mViewModel.getUserDocument(getUserId()).collection(DatabaseConstants.FOOD_POSTS)
                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "onEvent: ", error);
                        }
                        if (value != null && !value.isEmpty()) {
                            value.getDocuments().forEach(documentSnapshot -> {
                                final String id = (String) documentSnapshot.get("post_id");
                                Log.d(TAG, "onEvent: IDs:" + id);
                                mViewModel.deletePostDocument(getUserId(), id);
                            });
                        }
                    }
                });

        mAdapter.notifyItemRemoved(position);
    }

}