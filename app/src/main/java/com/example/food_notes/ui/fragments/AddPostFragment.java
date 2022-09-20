package com.example.food_notes.ui.fragments;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.food_notes.BuildConfig;
import com.example.food_notes.R;
import com.example.food_notes.databinding.FragmentAddPostBinding;
import com.example.food_notes.injection.Injection;
import com.example.food_notes.ui.observers.AddPostLifeCycleObserver;
import com.example.food_notes.ui.view.factory.FoodPostModelViewFactory;
import com.example.food_notes.ui.view.model.FoodPostViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class AddPostFragment extends Fragment implements AddImageOptionsDialogFragment.AddImageOptionsListener {

    private static String URI_STRING;
    private static final String TAG = "add_post";
    private static int USER_ID;
    private static final int GALLERY = 187;

    // view binding
    private FragmentAddPostBinding binding;

    // firebase storage
    private FirebaseStorage mStorage;
    private FirebaseAuth mAuth;
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
    private final LiveData<Bitmap> mBitmap = new MutableLiveData<>();
    private AddPostLifeCycleObserver mObserver;

    // navigation components
    private NavController navController;
    private SavedStateHandle savedStateHandle;
    private NavBackStackEntry navBackStackEntry;

    // dialog window
    private AddImageOptionsDialogFragment dialogFragment;

    // permissions array
    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };



    private ActivityResultLauncher<Intent> mGalleryIntent;

    /**
     * This launcher is used to get the URI of an image from gallery
     */
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    Log.d(TAG, "onActivityResult: SUCCESS?"+result.getPath());
                    loadImageThumbnail(result);
                    mViewModel.getBitmap(result.getEncodedPath());
                    Log.d(TAG, "onActivityResult: getBitmap?");
                }
            }
    );

    private final ActivityResultLauncher<Uri> mTakePicture = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (!result) {
                        Log.d(TAG, "onActivityResult: NO RESULT");
                        return;
                    }
                    requireContext().grantUriPermission(
                            "com.example.food_notes",
                            mUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    );
                }
            }
    );

    public AddPostFragment() {}

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

        mObserver = new AddPostLifeCycleObserver(requireActivity().getActivityResultRegistry(), requireContext());
        getLifecycle().addObserver(mObserver);

        navBackStackEntry = navController.getPreviousBackStackEntry();
        savedStateHandle = navBackStackEntry.getSavedStateHandle();
        if (savedStateHandle.contains("LOGGED_USERID")) {
            USER_ID = savedInstanceState.getInt("LOGGED_USERID");
        }

        mStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mReference = mStorage.getReference();
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

        fab_choose_image.setOnClickListener(v -> {
            showDialogFragment();
        });

        fab_create.setOnClickListener(v -> {
            mViewModel.addItem(
                    USER_ID,
                    URI_STRING,
                    title.getText().toString(),
                    description.getText().toString(),
                    ratingBar.getRating(),
                    1.52,
                    1.11
            );
            backToUserMainFragment();
        });

        fab_back.setOnClickListener(v -> backToUserMainFragment());
    }

    private void showDialogFragment() {
        dialogFragment = new AddImageOptionsDialogFragment();
        dialogFragment.show(getChildFragmentManager(), AddImageOptionsDialogFragment.TAG);
    }

    /**
     * This function prompt user to allow permissions, then depending
     * on the result it navigates to camera and returns a preview image,
     * finally sets it into imageView
     */
    private void askPermissionsForCamera() {
        Dexter.withContext(requireActivity())
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            navigateToSettings();
                        } else if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            Toast.makeText(requireActivity().getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                            mObserver.previewImage();
                            mObserver.getBitmapLiveData().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
                                @Override
                                public void onChanged(Bitmap bitmap) {
                                    Glide.with(requireContext()).load(bitmap).centerCrop().into(imageView);
                                    Log.d(TAG, "onChanged: BITMAP?");
                                }
                            });
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                }).withErrorListener(dexterError -> Log.d(TAG, "askPermissionsForCamera:"+dexterError.toString()))
                .onSameThread().check();
    }

    /**
     * This function initially asks for permission to go to local storage,
     * picks an image and gets its URI. Finally sets the image uri into imageView
     */
    private void askPermissionForGallery() {
        Dexter.withContext(requireActivity()).withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            navigateToSettings();
                        }
                        else if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            Toast.makeText(requireActivity().getApplicationContext(), "Storage access has been granted.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onPermissionsChecked: GRANTED STORAGE ACCESS");
                            // go to gallery
                            mGetContent.launch("image/*");
                            mGetContent.getContract().getSynchronousResult(requireContext().getApplicationContext(), "image/*");
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        navigateToSettings();
                    }
                }).withErrorListener(dexterError -> Log.d(TAG, "getPermissionForGallery: ERROR - "+ dexterError.toString()))
                .onSameThread().check();

    }

    @Override
    public void onResume() {
        super.onResume();

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
        navController.navigate(R.id.userMainFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGetContent.unregister();
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
        final int thumbnailSize = 150;
        Glide.with(requireParentFragment()).load(uri).centerCrop()
                .thumbnail(Glide.with(requireParentFragment())
                        .load(uri)
                        .override(thumbnailSize))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(imageView);
    }

    private void createTempFileForTakenImage() {
        String uuid = UUID.randomUUID().toString();
        File outputDir = requireActivity().getCacheDir();
        File file = null;
        try {
            file = File.createTempFile(uuid, ".png", outputDir);
        } catch (IOException e) {
            return;
        }

        try {
            mUri = FileProvider.getUriForFile(requireActivity().getApplicationContext(),
                    BuildConfig.APPLICATION_ID + ".fileProvider", file);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "createTempFileForTakenImage: fileError"+ e.getLocalizedMessage());
        }
    }
}