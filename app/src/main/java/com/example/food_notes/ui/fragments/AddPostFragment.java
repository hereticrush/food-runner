package com.example.food_notes.ui.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.SavedStateHandle;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.food_notes.R;
import com.example.food_notes.databinding.FragmentAddPostBinding;
import com.example.food_notes.injection.Injection;
import com.example.food_notes.ui.view.factory.FoodPostModelViewFactory;
import com.example.food_notes.ui.view.model.FoodPostViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;


public class AddPostFragment extends Fragment {

    private static final String TAG = "add_post";
    private static int USER_ID;

    // view binding
    private FragmentAddPostBinding binding;

    private RatingBar ratingBar;
    private FloatingActionButton fab_create, fab_back, fab_choose_image;
    private EditText title;
    private EditText description;

    // view model components
    private FoodPostModelViewFactory mFactory;
    private FoodPostViewModel mViewModel;

    // navigation components
    private NavHostFragment navHostFragment;
    private NavController navController;
    private SavedStateHandle savedStateHandle;
    private NavBackStackEntry navBackStackEntry;

    public AddPostFragment() {}

    @Nullable
    public static AddPostFragment newInstance() {
        AddPostFragment fragment = new AddPostFragment();
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

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRatingBar();
        if (isFilledAllRequiredFields()) {
            fab_create.setOnClickListener(v -> backToUserMainFragment());
        }
        fab_back.setOnClickListener(v -> backToUserMainFragment());
    }
    //TODO IMPLEMENT THIS FUNCTION AS IT IS: GO TO GALLERY, SET PERMS, GET THE IMAGE AND CONVERT URL TO STRING , STORE IN DB
    //TODO BUG HAS TO DO WITH FUNCTION CALLS IN THIS FUNCTION

    //TODO gotta work this func out, clashing with ui or related to thread
    private void showPermissionDialog() {
        final ArrayList<String> items = new ArrayList<>(2);
        items.add("Select photo from gallery");
        items.add("Capture a photo with camera");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getApplicationContext());
        builder.setTitle(" Choose an option ");
        builder.setItems(items.toArray(new String[2]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) // TODO needs fix
                {
                    case 0:
                        chooseImageFromGallery();
                        break;
                    case 1:
                        captureImageWithCamera();
                        break;
                    default: dialog.dismiss();
                }
            }
        }).show();
    }

    private void captureImageWithCamera() {
        Toast.makeText(getActivity(), "Go capture an image!", Toast.LENGTH_SHORT).show();
    }

    private void chooseImageFromGallery() {
        getPermissionForGallery();
        Toast.makeText(getActivity(), "This works as well", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void getPermissionForGallery() {
        Dexter.withContext(this.getActivity()).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            Toast.makeText(getActivity(), "Went to gallery", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        showRationaleDialogForPermissions();
                    }
                }).withErrorListener(
                        dexterError -> Toast.makeText(getActivity(), "An error occurred! " + dexterError.toString(), Toast.LENGTH_SHORT).show()
                ).onSameThread().check();
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
                        } catch (Exception e) {
                            Log.e("ERROR", e.getLocalizedMessage());
                        }
                    }
                }).setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * initializes some of the attributes of the rating bar in the add_post fragment
     */
    private void initRatingBar() {
            binding.ratingBarAddPost.setNumStars(5);
            binding.ratingBarAddPost.setIsIndicator(false);
            binding.ratingBarAddPost.setRating(0);
    }

    /**
     * Navigates user back to UserMainFragment
     */
    private void backToUserMainFragment() {
        navController.navigate(R.id.userMainFragment);
    }

    /**
     * if any field(title,desc) is empty return false, otherwise true
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
}