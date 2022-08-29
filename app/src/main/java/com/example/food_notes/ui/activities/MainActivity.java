package com.example.food_notes.ui.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.food_notes.databinding.ActivityMainBinding;
import com.example.food_notes.ui.fragments.LoginFragment;

public class MainActivity extends AppCompatActivity {

    private final static String ENTRY_FRAGMENT = "login";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        LoginFragment fragment = LoginFragment.getInstance();
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(binding.mainNavHostFragment.getId(), fragment, null)
                .addToBackStack(ENTRY_FRAGMENT).commit();
    }

}