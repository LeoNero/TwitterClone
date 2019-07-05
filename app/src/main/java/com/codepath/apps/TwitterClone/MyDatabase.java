package com.codepath.apps.TwitterClone;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.codepath.apps.TwitterClone.models.Tweet;

@Database(entities={Tweet.class}, version=1)
public abstract class MyDatabase extends RoomDatabase {
    // Database name to be used
    public static final String NAME = "TwitterClone";
}
