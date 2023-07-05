package com.example.courseworkhabittracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;


public class AccountabilityGallery extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ImageView imageViewHabitView;
    private TextView textViewHabitName;
    private FloatingActionButton fabAddImage;
    private PhotoDbHelper dbHelper;
    private Cursor cursor;
    private int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountability_gallery);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.page_home);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.page_home:
                        Intent home = new Intent(AccountabilityGallery.this, UserHome.class);
                        startActivity(home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                        return true;

                    case R.id.page_social:
                        Intent social = new Intent(AccountabilityGallery.this, SocialList.class);
                        startActivity(social.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                        return true;

                    case R.id.page_settings:
                        Intent settings = new Intent(AccountabilityGallery.this, UserSettings.class);
                        startActivity(settings.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                        return true;
                }
                return false;
            }
        });
        imageViewHabitView = findViewById(R.id.imageViewHabitView);
        textViewHabitName = findViewById(R.id.textViewHabitName);
        fabAddImage = findViewById(R.id.fabAddImage);
        fabAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //if click on fab, go to take photo
                Intent intent = new Intent(AccountabilityGallery.this, TakePhoto.class);
                if (getIntent().getExtras() != null) { //check you're able to get the extras from the intent
                    String prevHabit = getIntent().getStringExtra("habitName");
                    intent.putExtra("habitName", prevHabit);
                }
                startActivity(intent);
            }
        });
        dbHelper = new PhotoDbHelper(this);
        cursor = dbHelper.readAllPhotos();
        if (cursor.moveToFirst()) {
            cursor.moveToPosition(pos);
            PhotoMem photoMem = new PhotoMem(cursor);
            imageViewHabitView.setImageBitmap(photoMem.getImage());
            textViewHabitName.setText(photoMem.getHabitName());

            final GestureDetector gdt = new GestureDetector(new GestureListener());
            imageViewHabitView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View view, final MotionEvent event) {
                    gdt.onTouchEvent(event);
                    return true;
                }
            });
        }

    }

    @Override
    public void onResume() { //when user returns after taking a photo, update the list of photos that you can swipe between on image view
        super.onResume();
        pos = 0;
        cursor = dbHelper.readAllPhotos();
        if (cursor.moveToFirst()) {
            cursor.moveToPosition(pos);
            PhotoMem photoMem = new PhotoMem(cursor);
            imageViewHabitView.setImageBitmap(photoMem.getImage());
            textViewHabitName.setText(photoMem.getHabitName());

            final GestureDetector gdt = new GestureDetector(new GestureListener());
            imageViewHabitView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View view, final MotionEvent event) {
                    gdt.onTouchEvent(event);
                    return true;
                }
            });
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener { //detect a swipe left or right
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > 50 && Math.abs(velocityX) > 50) { //if touch has moved enough right to left
                if (cursor.moveToNext()) {
                    try {
                        cursor.moveToPosition(pos + 1);
                        pos++;
                        PhotoMem photoMem = new PhotoMem(cursor);
                        imageViewHabitView.setImageBitmap(photoMem.getImage());
                        textViewHabitName.setText(photoMem.getHabitName());
                    } catch (Exception e) {
                        e.getMessage();
                    }
                } else {
                    Toast.makeText(AccountabilityGallery.this, "No Next Image", Toast.LENGTH_SHORT).show();
                }
                return false;
            } else if (e2.getX() - e1.getX() > 50 && Math.abs(velocityX) > 50) { //if touch has moved enough left to right
                if (cursor.moveToPrevious()) {
                    try {
                        cursor.moveToPosition(pos - 1);
                        pos--;
                        PhotoMem photoMem = new PhotoMem(cursor);
                        imageViewHabitView.setImageBitmap(photoMem.getImage());
                        textViewHabitName.setText(photoMem.getHabitName());
                    } catch (Exception e) {
                        e.getMessage();
                    }
                } else {
                    Toast.makeText(AccountabilityGallery.this, "No Previous Image", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
            return false;
        }
    }
}