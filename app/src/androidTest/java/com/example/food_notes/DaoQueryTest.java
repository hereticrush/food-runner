package com.example.food_notes;

import static org.junit.Assert.assertNotNull;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.food_notes.data.user.User;
import com.example.food_notes.db.ApplicationDatabase;
import com.example.food_notes.injection.Injection;
import com.example.food_notes.ui.fragments.LoginFragment;
import com.example.food_notes.ui.view.factory.AuthenticationViewModelFactory;
import com.example.food_notes.ui.view.model.AuthenticationViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@RunWith(AndroidJUnit4.class)
public class DaoQueryTest {

    private ApplicationDatabase mDatabase;
    private AuthenticationViewModelFactory modelFactory;
    private AuthenticationViewModel model;

    @Before
    public void initDatabaseAndViewModel() throws Exception {
        mDatabase = ApplicationDatabase.getInstance(InstrumentationRegistry.getInstrumentation().getTargetContext());
        modelFactory = Injection.provideAuthViewModelFactory(InstrumentationRegistry.getInstrumentation().getTargetContext());
        model = modelFactory.create(AuthenticationViewModel.class);
    }

    @After
    public void closeDatabase() throws Exception {
        mDatabase.close();
    }


    @Test
    public void insertUser_() {
        User testUser = new User(102, "test_user1", "test_password1");
        model.insertUser("hell0_wrLd111", "PasSW0rD22");
        User testU = new User("hell0_wrLd111", "PasSW0rD22");
        Single<User> userSingle = model.getAllUsers().flatMap(Flowable::fromIterable)
                .filter(i -> i.getUsername().equals("hell0_wrLd111"))
                .toObservable().singleOrError();
        userSingle.test().assertComplete();
        userSingle.test().assertValue(testU);
    }

    @Test
    public void testLoginFragment_init() {
        FragmentScenario<LoginFragment> scenario =  FragmentScenario.launchInContainer(LoginFragment.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.recreate();
        scenario.onFragment(loginFragment -> assertNotNull(loginFragment.getActivity()));
    }
}
