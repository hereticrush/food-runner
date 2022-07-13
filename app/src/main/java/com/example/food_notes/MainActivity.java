package com.example.food_notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View fabAddContent = findViewById(R.id.fabAddContent);
        fabAddContent.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddNewUserActivity.class);
            startActivity(intent);
        });
    }
}