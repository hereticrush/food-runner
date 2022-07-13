package com.example.food_notes;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;

import java.util.Objects;

public class AddNewUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);

        Toolbar customToolbar = (Toolbar) findViewById(R.id.toolbar_add_user);
        setSupportActionBar(customToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        customToolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    public void createUser(View view) {

        view.findViewById(R.id.btn_add_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Runnable() {
                    @Override
                    public void run() {

                    }
                };
            }
        });
    }
}