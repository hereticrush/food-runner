package com.example.food_notes;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.food_notes.data.user.User;
import com.example.food_notes.db.ApplicationDatabase;
import com.example.food_notes.injection.Injection;
import com.example.food_notes.ui.view.factory.AuthenticationViewModelFactory;
import com.example.food_notes.ui.view.model.AuthenticationViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.rxjava3.core.MaybeObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@RunWith(AndroidJUnit4.class)
public class DaoQueryTest {

    private ApplicationDatabase mDatabase;
    private AuthenticationViewModelFactory modelFactory;
    private AuthenticationViewModel model;
    private CompositeDisposable disposable;
    private MaybeObserver<User> subscriber;

    @Before
    public void initDatabaseAndViewModel() throws Exception {
        mDatabase = ApplicationDatabase.getInstance(InstrumentationRegistry.getInstrumentation().getTargetContext());
        modelFactory = Injection.provideAuthViewModelFactory(InstrumentationRegistry.getInstrumentation().getTargetContext());
        model = modelFactory.create(AuthenticationViewModel.class);
        disposable = new CompositeDisposable();
    }

    @After
    public void closeDatabase() throws Exception {
        mDatabase.userDao().deleteAllUsers();
        mDatabase.close();
    }

    @Test
    public void insertUser_() {
        User testUser = new User("abcdefgh", "testUser1");
        mDatabase.userDao().insertUser(testUser).test().assertNoErrors();
        model.insertUser(new User("polyphia$3", "newAlbum322@")).test().assertNoErrors();
    }


    @Test
    public void insertUserAndGetUserThenDeleteUser_() {
        User testUser = new User(102, "test_user1", "test_password1");
        User another = new User(144, "testingUseR999", "pasSw0rD_TeST222");
        model.insertUser(testUser);
        model.insertUser(another);
        model.insertUser(new User("hell0_wrLd111", "PasSW0rD22"))
                .test()
                .assertNoErrors();

        model.getUser("test_user1", "test_password1")
                .test()
                .assertNoErrors();
    }

    @Test
    public void getAllUsersFromDb_() {
        model.getAllUsers().test().assertNoErrors();
    }

    /*@Test
    public void testLoginFragment_init() {
        FragmentScenario<LoginFragment> scenario =  FragmentScenario.launchInContainer(LoginFragment.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.recreate();
        scenario.onFragment(loginFragment -> assertNotNull(loginFragment.getActivity()));
    }*/
}
