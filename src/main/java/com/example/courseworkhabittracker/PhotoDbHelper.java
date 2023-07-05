package com.example.courseworkhabittracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PhotoDbHelper extends SQLiteOpenHelper {

    public PhotoDbHelper(Context context) {
        super(context, "photos.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) { //create the database for the first time
        db.execSQL("CREATE TABLE " + PhotoContract.PhotoEntry.TABLE_NAME + " (" +
                PhotoContract.PhotoEntry._ID + " INTEGER PRIMARY KEY, " +
                PhotoContract.PhotoEntry.COLUMN_TITLE + " TEXT, " +
                PhotoContract.PhotoEntry.COLUMN_IMAGE + " TEXT )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }

    public Cursor readAllPhotos() { //retrieves all photos from sqlite database
        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                PhotoContract.PhotoEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }


    public boolean addPhoto(PhotoMem photoMem) { //adds new photo to sqlite database
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PhotoContract.PhotoEntry.COLUMN_TITLE, photoMem.getHabitName());
        values.put(PhotoContract.PhotoEntry.COLUMN_IMAGE, photoMem.getImageAsString());

        return db.insert(PhotoContract.PhotoEntry.TABLE_NAME, null, values) != -1;
    }
}
