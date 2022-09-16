package com.example.food_notes.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
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
import androidx.lifecycle.SavedStateHandle;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.food_notes.R;
import com.example.food_notes.databinding.FragmentAddPostBinding;
import com.example.food_notes.injection.Injection;
import com.example.food_notes.ui.view.factory.FoodPostModelViewFactory;
import com.example.food_notes.ui.view.model.FoodPostViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class AddPostFragment extends Fragment implements AddImageOptionsDialogFragment.AddImageOptionsListener {

    public static final int CAMERA_REQUEST_CODE = 1938;
    public static final int GALLERY = 100;
    public static final int CAMERA = 200;
    private static final String TAG = "add_post";
    private static int USER_ID;

    // view binding
    private FragmentAddPostBinding binding;

    // views
    private RatingBar ratingBar;
    private FloatingActionButton fab_create, fab_back, fab_choose_image;
    private EditText title;
    private EditText description;
    private AppCompatImageView imageView;

    // view model components
    private FoodPostModelViewFactory mFactory;
    private FoodPostViewModel mViewModel;

    // navigation components
    private NavHostFragment navHostFragment;
    private NavController navController;
    private SavedStateHandle savedStateHandle;
    private NavBackStackEntry navBackStackEntry;

    // dialog window
    private AddImageOptionsDialogFragment dialogFragment;

    // result launcher is required to choose image and capture image with camera
    private ActivityResultLauncher<Intent> galleryResultLauncher;
    private ActivityResultLauncher<Intent> cameraResultLauncher;
    private ActivityResultLauncher<Uri> mTakePhoto;

    private Observable<Bitmap> mBitmap;

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

        navBackStackEntry = navController.getPreviousBackStackEntry();
        savedStateHandle = navBackStackEntry.getSavedStateHandle();
        if (savedStateHandle.contains("LOGGED_USERID")) {
            USER_ID = savedInstanceState.getInt("LOGGED_USERID");
        }

        registerIntentToGalleryLauncher();
        registerIntentToCaptureImageWithCameraLauncher();
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
        if (isFilledAllRequiredFields()) {
            fab_create.setOnClickListener(v -> {
                mViewModel.addItem(
                        USER_ID,
                        title.getText().toString(),
                        description.getText().toString(), 
                        ratingBar.getRating()
                );
                Toast.makeText(requireActivity().getApplicationContext(), "Yeah", Toast.LENGTH_SHORT).show();
                backToUserMainFragment();
            });
        }
        fab_back.setOnClickListener(v -> backToUserMainFragment());
    }

    private void showDialogFragment() {
        dialogFragment = new AddImageOptionsDialogFragment();
        dialogFragment.show(getChildFragmentManager(), AddImageOptionsDialogFragment.TAG);
    }

    private void captureImageWithCamera() {
        Dexter.withContext(requireActivity()).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    Log.d(TAG, "onPermissionsChecked: GRANTED CAMERA ACCESS");
                    // go to camera
                    Intent intentToCaptureImageWithCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraResultLauncher.launch(intentToCaptureImageWithCamera);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                showRationaleDialogForPermissions();
            }
        }).withErrorListener(dexterError -> Log.d(TAG, "captureImageWithCamera: gallery"+dexterError.toString()))
                .onSameThread().check();
    }

    private void chooseImage() {
        Intent intentToChooseImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryResultLauncher.launch(intentToChooseImage);
    }

    private void chooseImageFromGallery() {
        Dexter.withContext(requireActivity()).withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            Toast.makeText(requireActivity().getApplicationContext(), "Storage access has been granted.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onPermissionsChecked: GRANTED STORAGE ACCESS");
                            // go to gallery
                            chooseImage();
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        showRationaleDialogForPermissions();
                    }
                }).withErrorListener(dexterError -> Log.d(TAG, "getPermissionForGallery: ERROR - "+ dexterError.toString()))
                .onSameThread().check();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void showRationaleDialogForPermissions() {
        new AlertDialog.Builder(requireActivity())
                .setMessage("You are required to turn on permissions from settings")
                .setPositiveButton("To Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", intent.getPackage(), null);
                            intent.setData(uri);
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

    /**
     * if any field(title,desc) is empty return false, otherwise true.
     * @return bool
     */
    private boolean isFilledAllRequiredFields() {
        if (TextUtils.isEmpty(title.getText()) &&
        TextUtils.isEmpty(description.getText())) {
            title.setError("Title is required");
            description.setError("Description is required");
            return false;
        }
        if (TextUtils.isEmpty(title.getText())) {
            title.setError("Title is required");
            return false;
        }
        if (TextUtils.isEmpty(description.getText())) {
            description.setError("Description is required");
            return false;
        }
        return true;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Calls on DialogListener to listen and handle the click event for storage option.
     * @param dialog AddImageOptionsDialogFragment
     */
    @Override
    public void onDialogStorageOptionClick(DialogFragment dialog) {
        Log.d(TAG, "onDialogStorageOptionClick: storage");
        chooseImageFromGallery();
    }


    /**
     * Calls on DialogListener to listen and handle the click event for camera option
     * @param dialog AddImageOptionsDialogFragment
     */
    @Override
    public void onDialogCameraOptionClick(DialogFragment dialog) {
        Log.d(TAG, "onDialogCameraOptionClick: camera");
        captureImageWithCamera();
    }

    /**
     * Registers the result launcher to provide
     * entry to gallery, choosing an image and setting thumbnail into imageView
     */
    private void registerIntentToGalleryLauncher() {
        galleryResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Intent intent = result.getData();
                        Log.d(TAG, "onActivityResult: Entry intent block");
                        if (result.getResultCode() == Activity.RESULT_OK && intent != null) {
                            Uri uri = intent.getData();
                            Log.d(TAG, "onActivityResult: uri:" + uri.getEncodedPath());
                            try {
                                Log.d(TAG, "onActivityResult: Entered try block");
                                loadImageThumbnail(uri);
                            } catch (Exception e) {
                                Log.d(TAG, "onActivityResult: " + e.getLocalizedMessage());
                            }
                        }
                    }
                });
    }
    //TODO needs to be finished
    private void registerIntentToCaptureImageWithCameraLauncher() {
        cameraResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Intent intent = result.getData();
                        if (result.getResultCode() == Activity.RESULT_OK && intent != null) {
                            Uri uri = intent.getData();
                            try {
                                //TODO experimental
                                Log.d(TAG, "onActivityResult: Entry camera try block");
                                Intent parcelizeUri = Intent.parseUri(uri.getPath(), Intent.URI_INTENT_SCHEME);
                                Log.d(TAG, "onActivityResult: uri:"+parcelizeUri.getData().getPath());
                                //loadImageThumbnail(uri); //TODO this has to be fixed
                            } catch (Exception e) {
                                Log.d(TAG, "onActivityResult: Error:"+e.getLocalizedMessage());
                            }
                        }
                    }
                }
        );
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
}