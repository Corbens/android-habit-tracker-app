package com.example.courseworkhabittracker;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class PhotoMem {

    private static final float PREFERRED_WIDTH = 250;
    private static final float PREFERRED_HEIGHT = 250;
    private String habitName;
    private String image;

    public PhotoMem(Cursor cursor) {
        this.habitName = cursor.getString(1);
        this.image = cursor.getString(2);
    }

    public PhotoMem(String habitName, Bitmap image) {
        this.habitName = habitName;
        this.image = bitmapToString(resizeBitmap(image));
    }

    public String getHabitName() {
        return this.habitName;
    }

    public Bitmap getImage() {
        return stringToBitmap(this.image);
    }

    public String getImageAsString() {
        return this.image;
    }

    private String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //compress so doesn't take up too much space
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private Bitmap stringToBitmap(String string) {
        try {
            byte[] encodeByte = Base64.decode(string, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception exception) {
            exception.getMessage();
            return null;
        }
    }

    public static Bitmap resizeBitmap(Bitmap bitmap) { //resize bitmap
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = PREFERRED_WIDTH / width;
        float scaleHeight = PREFERRED_HEIGHT / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, width, height, matrix, false);
        bitmap.recycle();
        return resizedBitmap;
    }
}
