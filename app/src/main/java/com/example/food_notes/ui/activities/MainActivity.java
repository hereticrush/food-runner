package com.example.food_notes.ui.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

import com.example.food_notes.R;
import com.example.food_notes.databinding.ActivityMainBinding;
import com.example.food_notes.ui.fragments.LoginFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * binding type : ViewBinding
 */
public class MainActivity extends AppCompatActivity {

    private final static String ENTRY_FRAGMENT = "login";

    // view binding variable
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater()); // viewBinding initialized
        setContentView(binding.getRoot());

    }

    @Override
    protected void onDestroy() {
        binding = null; // avoid memory leak caused by view binding, and set binding pointer to null once activity changes
        super.onDestroy();
    }
}