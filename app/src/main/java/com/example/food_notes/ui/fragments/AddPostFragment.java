package com.example.food_notes.ui.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;


public class AddPostFragment extends Fragment implements AddImageOptionsDialogFragment.AddImageOptionsListener {

    private static final String TAG = "add_post";
    private static int USER_ID;

    // view binding
    private FragmentAddPostBinding binding;

    // views
    private RatingBar ratingBar;
    private FloatingActionButton fab_create, fab_back, fab_choose_image;
    private EditText title;
    private EditText description;
    private ImageView imageView;

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
        
        this.getChildFragmentManager().setFragmentResultListener(
                "requestKey", this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        if (requestKey.equals("requestKey")) {
                            if (result.containsKey("storage")) {
                                final int RESULT_CODE = result.getInt(requestKey);
                                Log.d(TAG, "onFragmentResult: "+RESULT_CODE);
                                if (RESULT_CODE == 750) {
                                    System.out.println("750");
                                    Toast.makeText(requireActivity().getApplicationContext(), "storage", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
        );
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
                Drawable drawable = imageView.getDrawable();
                Glide.with(this).load(drawable).diskCacheStrategy(DiskCacheStrategy.ALL);

                mViewModel.addItem(
                        USER_ID,
                        title.getText().toString(),
                        description.getText().toString(),
                        ratingBar.getRating());
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

    @Override
    public void onDialogStorageOptionClick(DialogFragment dialog) {
        // TODO implement functions
        dialog.dismiss();
    }

    @Override
    public void onDialogCameraOptionClick(DialogFragment dialog) {
        // TODO implement functions
        dialog.dismiss();
    }
}