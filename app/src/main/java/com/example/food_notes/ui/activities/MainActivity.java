package com.example.food_notes.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.example.food_notes.R;
import com.example.food_notes.databinding.ActivityMainBinding;
import com.example.food_notes.ui.fragments.UserRegisterFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        showRegisterForm();

    }

    public void showRegisterForm() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true).replace(
                        binding.fragmentContainerView.getId(), UserRegisterFragment.getInstance(), null
                ).addToBackStack("main").commit();
    }

}