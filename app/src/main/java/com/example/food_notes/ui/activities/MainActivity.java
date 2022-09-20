package com.example.food_notes.ui.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.food_notes.R;
import com.example.food_notes.databinding.ActivityMainBinding;
import com.example.food_notes.ui.fragments.LoginFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * binding type : ViewBinding
 */
public class MainActivity extends AppCompatActivity {

    private final static String ENTRY_FRAGMENT = "login";

    // view binding variable
    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater()); // viewBinding initialized
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_nav_host_fragment);
        navController = navHostFragment.getNavController();

    }

    @Override
    protected void onDestroy() {
        binding = null; // avoid memory leak caused by view binding, and set binding pointer to null once activity changes
        super.onDestroy();
    }
}