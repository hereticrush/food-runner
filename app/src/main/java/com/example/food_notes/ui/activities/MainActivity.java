package com.example.food_notes.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.food_notes.R;
import com.example.food_notes.ui.fragments.UserRegisterFragment;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);;
        showRegisterForm();

    }

    public void showRegisterForm() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true).replace(
                        R.id.fragmentContainerView, UserRegisterFragment.getInstance(), null
                ).addToBackStack("main").commit();
    }

}