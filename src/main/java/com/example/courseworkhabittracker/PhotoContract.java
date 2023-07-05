package com.example.courseworkhabittracker;

import android.provider.BaseColumns;

public class PhotoContract {

    private PhotoContract() {
    }

    public final class PhotoEntry implements BaseColumns {
        public static final String TABLE_NAME = "photos";
        public static final String COLUMN_TITLE = "habitname";
        public static final String COLUMN_IMAGE = "image";
    }
}
