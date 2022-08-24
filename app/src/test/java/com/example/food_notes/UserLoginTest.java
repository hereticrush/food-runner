package com.example.food_notes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;

import com.example.food_notes.data.user.User;
import com.example.food_notes.data.user.UserDataSource;
import com.example.food_notes.data.user.UserRepository;
import com.example.food_notes.db.ApplicationDatabase;

import io.reactivex.rxjava3.core.Flowable;

@RunWith(JUnit4.class)
public class UserLoginTest {

    private UserDataSource model;
    private Flowable<User> userFlowable;
    private User testUser;
    private ApplicationDatabase database;
    private Context context;

    @Before
    public void createObjects() {
        //database = ApplicationDatabase.getInstance();
        testUser = new User("Hello", "World");
    }

    @Test
    public void insertUserObjectAndQuery() {
        model = new UserRepository(database.userDao());
        model.insertOrUpdateUser(testUser);
        userFlowable = model.getUser("Hello", "World");
        assertEquals(userFlowable.blockingLast().getUser_id(), testUser.getUser_id());
        assertEquals(userFlowable.blockingLast().getUsername(), testUser.getUsername());
        assertEquals(userFlowable.blockingLast().getPassword(), testUser.getPassword());
    }
}
