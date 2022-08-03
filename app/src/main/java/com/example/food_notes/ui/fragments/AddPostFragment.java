package com.example.food_notes.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food_notes.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class AddPostFragment extends Fragment {

    private static final String TAG = "add_post";
    private TextView tvChooseImage;
    private EditText title, description;
    private RatingBar ratingBar;
    private FloatingActionButton fab_create, fab_back;
    //TODO NULL REFERENCE TO EDITTEXT FIX ASAP
    private AddPostFragment() {}

    @NonNull
    public static AddPostFragment getInstance() {
        AddPostFragment fragment = new AddPostFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = new Bundle();
        //TODO BE MINDFUL ABOUT THESE THINGS HERE, MAYBE A BUG
        bundle.putString("TITLE", title.getText().toString());
        bundle.putString("DESCRIPTION", description.getText().toString());
        bundle.putFloat("USER_RATING", ratingBar.getRating());
        //if (getArguments() != null) {
         //   getArguments().get()
        //}

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);
        tvChooseImage = view.findViewById(R.id.tv_choose_image);
        //title = view.findViewById(R.id.et_title);
        //description = view.findViewById(R.id.et_description);
        ratingBar = view.findViewById(R.id.rating_bar_add_post);
        fab_back = view.findViewById(R.id.fab_back_to_main);
        fab_create = view.findViewById(R.id.fab_create_post);
        initRatingBar(ratingBar);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvChooseImage = view.findViewById(R.id.tv_choose_image);
        chooseImage(tvChooseImage);
        title = view.findViewById(R.id.et_title);
        description = view.findViewById(R.id.et_description);
        fab_back.setOnClickListener(v -> backToUserMainFragment());
        new Thread(() -> createPost(fab_create)).start();
    }

    //TODO IMPLEMENT THIS FUNCTION AS IT IS: GO TO GALLERY, SET PERMS, GET THE IMAGE AND CONVERT URL TO STRING , STORE IN DB
    private void chooseImage(@NonNull View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
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