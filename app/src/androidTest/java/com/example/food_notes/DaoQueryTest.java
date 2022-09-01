package com.example.food_notes;

import static org.junit.Assert.assertNotNull;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.food_notes.data.user.User;
import com.example.food_notes.db.ApplicationDatabase;
import com.example.food_notes.ui.fragments.LoginFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DaoQueryTest {

    private ApplicationDatabase mDatabase;

    @Before
    public void initDatabase() throws Exception {
        mDatabase = ApplicationDatabase.getInstance(InstrumentationRegistry.getInstrumentation().getTargetContext());
    }

    @After
    public void closeDatabase() throws Exception {
        mDatabase.close();
    }

    /*@Test
    public void getUser_whenUserNotSaved() {

    }*/

    @Test
    public void insertUserAndGetUserById() {
        User testUser = new User(102, "test_user1", "test_password1");
        mDatabase.userDao().insertUser(testUser);

        mDatabase.userDao().getUserById(testUser.getUser_id())
                .test().assertValue(
                        user -> user.equals(testUser)
                );
    }

    @Test
    public void testLoginFragment_init() {
        FragmentScenario<LoginFragment> scenario =  FragmentScenario.launchInContainer(LoginFragment.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.recreate();
        scenario.onFragment(loginFragment -> assertNotNull(loginFragment.getActivity()));
    }
}
