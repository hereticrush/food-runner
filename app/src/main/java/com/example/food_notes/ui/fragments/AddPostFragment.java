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

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.food_notes.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;


public class AddPostFragment extends Fragment {

    private static final String TAG = "add_post";
    //private AppCompatTextView tvChooseImage;
    private AppCompatEditText title, description;
    private RatingBar ratingBar;
    private FloatingActionButton fab_create, fab_back, fab_choose_image;
    private AddPostFragment() {}

    @NonNull
    public static AddPostFragment getInstance() {
        AddPostFragment fragment = new AddPostFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);
        fab_choose_image = view.findViewById(R.id.fab_choose_image);
        title = view.findViewById(R.id.et_title);
        description = view.findViewById(R.id.et_description);
        ratingBar = view.findViewById(R.id.rating_bar_add_post);
        fab_back = view.findViewById(R.id.fab_back_to_main);
        fab_create = view.findViewById(R.id.fab_create_post);
        initRatingBar(ratingBar);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //tvChooseImage = view.findViewById(R.id.tv_choose_image);
        //title = view.findViewById(R.id.et_title);
        //description = view.findViewById(R.id.et_description);
        showDialogOptions(fab_choose_image);
        fab_back.setOnClickListener(v -> backToUserMainFragment());
        new Thread(() -> createPost(fab_create)).start();
    }
    //TODO IMPLEMENT THIS FUNCTION AS IT IS: GO TO GALLERY, SET PERMS, GET THE IMAGE AND CONVERT URL TO STRING , STORE IN DB
    //TODO BUG HAS TO DO WITH FUNCTION CALLS IN THIS FUNCTION
    private void showDialogOptions(@NonNull View view) { view.findViewById(R.id.fab_choose_image).setOnClickListener(
            v -> {
                showPermissionDialog();
                Toast.makeText(getActivity(), "Works like a charm", Toast.LENGTH_SHORT).show();
            });
    }

    //TODO gotta work this func out, clashing with ui or related to thread
    private void showPermissionDialog() {
         ArrayList<String> items = new ArrayList<>();
         final CharSequence[] arr = items.toArray(new CharSequence[2]);
         final boolean[] clickedOption = {false, false};
         items.add("Select photo from gallery");
         items.add("Capture a photo with camera");
         AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getApplicationContext()).setTitle("Select Options")
                         .setMultiChoiceItems(arr, clickedOption, new DialogInterface.OnMultiChoiceClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                clickedOption[which] = isChecked;

                                String currentItem = items.get(which);

                                 Toast.makeText(getActivity().getApplicationContext(), arr[which], Toast.LENGTH_SHORT).show();
                                 if (currentItem.matches(arr[0].toString())) {
                                     Toast.makeText(getActivity().getApplicationContext(), "Choose an image!", Toast.LENGTH_SHORT).show();
                                     //chooseImageFromGallery();
                                 } else if (currentItem.matches(arr[1].toString())) {
                                     //TODO WRITE A FUNCTION TO CAPTURE PHOTO WITH CAM
                                     captureImageWithCamera();
                                 }
                             }
                         });
         builder.show();
    }

    private void captureImageWithCamera() {
        Toast.makeText(getActivity().getApplicationContext(), "Go capture an image!", Toast.LENGTH_SHORT).show();
    }

    private void chooseImageFromGallery() {
       //getPermissionForGallery();
        Toast.makeText(getActivity().getApplicationContext(), "This works as well", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getActivity().getApplicationContext(), "Went to gallery", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        showRationaleDialogForPermissions();
                    }
                }).withErrorListener(
                        dexterError -> Toast.makeText(getActivity().getApplicationContext(), "An error occurred! " + dexterError.toString(), Toast.LENGTH_SHORT).show()
                ).onSameThread().check();
    }

    private void showRationaleDialogForPermissions() {
        new AlertDialog.Builder(getActivity().getApplicationContext())
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
                            e.printStackTrace();
                        }
                    }
                }).setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * initializes few attributes of the rating bar in the add_post fragment
     * @param view
     */
    private void initRatingBar(View view) {
        if (view != null ) {
            ratingBar.setNumStars(5);
            ratingBar.setIsIndicator(false);
            ratingBar.setRating(0);
        }
    }

    /**
     * pops the recent fragment from the activity stack
     */
    private void backToUserMainFragment() {
        FragmentManager manager = getParentFragmentManager();
        manager.popBackStack("main", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }


    private void createPost(View view) {
        boolean flag_value = areFieldsFilled();
        try {
            view.setOnClickListener(v -> {
                if (!flag_value) {
                    Toast.makeText(getActivity().getApplicationContext(), "You cannot get outta here", Toast.LENGTH_SHORT).show();
                } else {
                    backToUserMainFragment();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * if any field(title,desc) is empty return false, otherwise true
     * @return bool
     */
    private boolean areFieldsFilled() {
        if (!checkDescriptionLength()) {
            description.setError("Description is required to create a post");
        }
        if (!checkTitleLength()) {
            title.setError("Title is required to create a post");
        }
        else {return checkDescriptionLength() && checkTitleLength();}
        return false;
    }

    /**
     * checks whether description has sufficient number of chars
      * @return bool
     */
    private boolean checkDescriptionLength() {
        return description.getText().toString().length() < 140 &&
                description.getText().toString().length() > 5;
    }

    /**
     * checks whether title has sufficient number of chars
     * @return bool
     */
    private boolean checkTitleLength() {
        return title.getText().toString().length() < 50 &&
                title.getText().toString().length() > 0;
    }

}