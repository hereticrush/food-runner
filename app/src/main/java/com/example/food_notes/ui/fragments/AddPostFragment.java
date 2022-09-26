package com.example.food_notes.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.food_notes.R;
import com.example.food_notes.auth.AppAuthentication;
import com.example.food_notes.databinding.FragmentAddPostBinding;
import com.example.food_notes.injection.Injection;
import com.example.food_notes.ui.view.factory.FoodPostModelViewFactory;
import com.example.food_notes.ui.view.model.FoodPostViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
//TODO fix activityResultLauncher for camera
public class AddPostFragment extends Fragment implements AddImageOptionsDialogFragment.AddImageOptionsListener {

    private static final String TAG = "add_post";
    private static String USER_ID;

    // view binding
    private FragmentAddPostBinding binding;

    // firebase storage
    private FirebaseStorage mStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore mFirestore;
    private StorageReference mReference;

    // views
    private RatingBar ratingBar;
    private FloatingActionButton fab_create, fab_back, fab_choose_image;
    private EditText title;
    private EditText description;
    private AppCompatImageView imageView;

    // view model components
    private FoodPostModelViewFactory mFactory;
    private FoodPostViewModel mViewModel;

    private Uri mUri;

    // navigation components
    private NavController navController;
    private NavBackStackEntry navBackStackEntry;

    // location services
    private FusedLocationProviderClient fusedLocationProviderClient;

    // dialog window
    private AddImageOptionsDialogFragment dialogFragment;

    /**
     * Registers an ActivityResultLauncher with Intent to access camera. If result is successful,
     * image will be set into imageView with imageUri
     */
    private final ActivityResultLauncher<Intent> mCameraIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent uri_data = result.getData();
                        if (uri_data != null) {
                            mUri = uri_data.getData();
                            Log.d(TAG, "onActivityResult: URI?"+mUri.getPath());
                            loadImageThumbnail(mUri);
                            Log.d(TAG, "onActivityResult: DONE_loadingThumbnail");
                        }
                    } else {
                        toast("Something went wrong.");
                    }
                }
            }
    );

    // camera permissions
    private final String[] CAMERA_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private String currentPhotoPath;

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Register an ActivityResultLauncher for camera permissions
     */
    private final ActivityResultLauncher<String[]> mCameraPermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    if (!result.isEmpty() && result.containsValue(true)) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
                            Log.d(TAG, "onActivityResult: WORKS?");
                            intent.putExtra(Intent.EXTRA_INTENT, Intent.ACTION_GET_CONTENT);
                            Log.d(TAG, "onActivityResult: EXTRA?");
                            mCameraIntent.launch(intent);
                    }
                }
            }
    );

    /**
     * Registers an intent to go to gallery. If result is a success, pick an image and retrieve its uri.
     * Finally the launcher sets the image thumbnail in imageView
     */
    private final ActivityResultLauncher<Intent> mGalleryIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent uri_data = result.getData();
                        if (uri_data != null) {
                            mUri = uri_data.getData();
                            loadImageThumbnail(mUri);
                            Log.d(TAG, "onActivityResult: DONE_loadingThumbnail");
                        }
                    }
                }
            }
    );

    // storage permissions are required to allow local storage actions and database operations
    private final String[] STORAGE_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };

    /**
     * Registers an ActivityResultLauncher for storage permissions
     */
    private final ActivityResultLauncher<String[]> mStoragePermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    if (!result.isEmpty() && result.containsValue(true)) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        mGalleryIntent.launch(intent);
                        Toast.makeText(requireActivity().getApplicationContext(), "Storage access has been granted.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    // location permission array and variables for getting location attributes
    private final String[] LOCATION_PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private Double LATITUDE;
    private Double LONGITUDE;

    /**
     * Once entering this fragment, permissions for location services are asked to user.
     * On successful result, latitude and longitude of user's last known location are set.
     */
    private final ActivityResultLauncher<String[]> mLocationPermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    if (!result.isEmpty() && result.containsValue(true)) {
                            try {
                                fusedLocationProviderClient.getLastLocation()
                                        .addOnSuccessListener(location -> {
                                            LATITUDE = location.getLatitude();
                                            LONGITUDE = location.getLongitude();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.d(TAG, "getUserLocation: FAILED: " + e.getLocalizedMessage());
                                        });
                            } catch (SecurityException e) {
                                toast("NOT SECURE:" + e.getLocalizedMessage());
                            }
                        } else {
                        navigateToSettings();
                    }
                }
            }
    );

    public AddPostFragment() {
    }

    @Nullable
    public static AddPostFragment newInstance(String requestKey) {
        AddPostFragment fragment = new AddPostFragment();
        Bundle args = new Bundle();
        args.putString("requestKey", requestKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init view model
        mFactory = Injection.provideFoodPostViewModelFactory(requireActivity().getApplicationContext());
        mViewModel = mFactory.create(FoodPostViewModel.class);
        navController = NavHostFragment.findNavController(this);
        navBackStackEntry = navController.getCurrentBackStackEntry();

        // init firebase
        auth = AppAuthentication.AUTH;
        mStorage = FirebaseStorage.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mReference = mStorage.getReference();
        if (auth.getCurrentUser() != null) {
            Log.d(TAG, "onCreate: USER?" + auth.getCurrentUser().getUid());
            USER_ID = auth.getCurrentUser().getUid();
        }

        // init location services and get user's location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity().getApplicationContext());
        getUserLocation();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //init view binding
        binding = FragmentAddPostBinding.inflate(inflater, container, false);
        title = binding.tilTitlePost.getEditText();
        description = binding.tilPost.getEditText();
        fab_back = binding.fabBackToMain;
        fab_create = binding.fabCreatePost;
        fab_choose_image = binding.fabChooseImage;
        ratingBar = binding.ratingBarAddPost;
        imageView = binding.ivSetImage;

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab_choose_image.setOnClickListener(v -> showDialogFragment());

        // add everything to database by clicking on create button
        fab_create.setOnClickListener(v -> {
            if (mUri != null) {
                mViewModel.uploadImageAndCreateFoodPost(
                        USER_ID,
                        mUri,
                        title.getText().toString(),
                        description.getText().toString(),
                        ratingBar.getRating(),
                        LATITUDE,
                        LONGITUDE
                );
                backToUserMainFragment();
            }
        });

        fab_back.setOnClickListener(v -> backToUserMainFragment());
    }

    /**
     * Launch the ActivityResultLauncher for location permissions
     */
    private void getUserLocation() {
        mLocationPermissions.launch(LOCATION_PERMISSIONS);
    }

    /**
     * Shows the DialogFragment for image options
     */
    private void showDialogFragment() {
        dialogFragment = new AddImageOptionsDialogFragment();
        dialogFragment.show(getChildFragmentManager(), AddImageOptionsDialogFragment.TAG);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * Launch the ActivityResultLauncher for camera permissions
     */
    private void askPermissionsForCamera() {
        mCameraPermissions.launch(CAMERA_PERMISSIONS);
    }

    /**
     * Launch the ActivityResultLauncher for storage permissions
     */
    private void askPermissionForGallery() {
        mStoragePermissions.launch(STORAGE_PERMISSIONS);
    }

    /**
     * Navigates user to Android settings through
     * an AlertDialog with options
     */
    private void navigateToSettings() {
        new AlertDialog.Builder(requireActivity())
                .setMessage("You are required to turn on permissions from settings manually")
                .setPositiveButton("To Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    .setData(Uri.fromParts("package", requireContext().getPackageName(), null))
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Log.e("ERROR", e.getLocalizedMessage());
                        }
                    }
                }).setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss())
                .show();
    }



    /**
     * Navigates user back to UserMainFragment
     */
    private void backToUserMainFragment() {
        navBackStackEntry.getSavedStateHandle().getLiveData("post");
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.userMainFragment, true, true).build();
        navController.navigate(R.id.userMainFragment, null, navOptions);
    }

    // avoid memory leak and result complications after this fragment is out of sight
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLocationPermissions.unregister();
        mStoragePermissions.unregister();
        mCameraPermissions.unregister();
        mGalleryIntent.unregister();
        mCameraIntent.unregister();
        binding = null;
    }

    /**
     * Calls on DialogListener to listen and handle the click event for storage option.
     * @param dialog AddImageOptionsDialogFragment
     */
    @Override
    public void onDialogStorageOptionClick(DialogFragment dialog) {
        Log.d(TAG, "onDialogStorageOptionClick: storage");
        askPermissionForGallery();
    }

    /**
     * Calls on DialogListener to listen and handle the click event for camera option
     * @param dialog AddImageOptionsDialogFragment
     */
    @Override
    public void onDialogCameraOptionClick(DialogFragment dialog) {
        Log.d(TAG, "onDialogCameraOptionClick: camera");
        askPermissionsForCamera();
    }

    /**
     * Loads selected image as a thumbnail into imageView.
     * @param uri Uri type image file uri
     */
    private void loadImageThumbnail(Uri uri) {
        final int thumbnailSize = 200;
        Glide.with(requireParentFragment()).load(uri).centerCrop()
                .thumbnail(Glide.with(requireParentFragment())
                        .load(uri)
                        .override(thumbnailSize))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(imageView);
    }

    /**
     * Provides a Toast object prototype with custom message
     * @param msg String type message
     */
    private void toast(String msg) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show());
    }

}