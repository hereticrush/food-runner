package com.example.food_notes.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.food_notes.db.converters.Converters;
import com.example.food_notes.data.foodpost.FoodPostDao;
import com.example.food_notes.data.user.UserDao;
import com.example.food_notes.data.foodpost.FoodPost;
import com.example.food_notes.data.user.User;
import com.example.food_notes.db.dbviews.FoodPostDetails;
import com.example.food_notes.db.dbviews.UserDetails;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                User.class,
                FoodPost.class,
        },
        views = {
                FoodPostDetails.class,
                UserDetails.class,
        },
        version = 1,
        exportSchema = false
)
@TypeConverters(Converters.class)
public abstract class ApplicationDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "food-runner-db";
    public abstract UserDao userDao();
    public abstract FoodPostDao foodPostDao();

    private static volatile ApplicationDatabase INSTANCE;
    private static final int THREADS = 4;
    private static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(THREADS);

    private final MutableLiveData<Boolean> isDbCreated = new MutableLiveData<>();

    /**
     * Singleton pattern function for initializing the database
      * @param context
     * @return instance of a database
     */
    public static ApplicationDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (ApplicationDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = buildAppDatabase(context);
                    INSTANCE.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    private static ApplicationDatabase buildAppDatabase(final Context context) {
        return Room.databaseBuilder(context, ApplicationDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        databaseWriteExecutor.execute(() -> {
                            ApplicationDatabase database = ApplicationDatabase.getInstance(context);
                            database.setDatabaseCreated();
                        });
                    }
                }).build();
    }

    private void setDatabaseCreated() {
        isDbCreated.postValue(true);
    }

    private void updateDatabaseCreated(final Context mContext) {
        if (mContext.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    public MutableLiveData<Boolean> getIsDbCreated() {
        return isDbCreated;
    }

    public static ExecutorService getDatabaseWriteExecutor() {
        return databaseWriteExecutor;
    }
}
