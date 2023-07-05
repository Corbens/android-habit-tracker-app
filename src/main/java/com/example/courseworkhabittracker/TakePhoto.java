package com.example.courseworkhabittracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class TakePhoto extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private String prevHabit;
    private ImageView imageViewCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.page_home);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.page_home:
                        Intent home = new Intent(TakePhoto.this, UserHome.class);
                        startActivity(home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                        return true;

                    case R.id.page_social:
                        Intent social = new Intent(TakePhoto.this, SocialList.class);
                        startActivity(social.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                        return true;

                    case R.id.page_settings:
                        Intent settings = new Intent(TakePhoto.this, UserSettings.class);
                        startActivity(settings.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                        return true;
                }
                return false;
            }
        });

        prevHabit = "unknown";
        imageViewCurrent = findViewById(R.id.imageViewCurrent);

        if (getIntent().getExtras() != null) { //check you're able to get the extras from the intent
            prevHabit = getIntent().getStringExtra("habitName");
        }

        if (ContextCompat.checkSelfPermission(TakePhoto.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) { //get permissions to use camera
            ActivityCompat.requestPermissions(TakePhoto.this, new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }
    }

    public void onButtonClickSavePhoto(View view) { //save photo to database
        Bitmap image = ((BitmapDrawable) imageViewCurrent.getDrawable()).getBitmap();
        PhotoMem photoMem = new PhotoMem(prevHabit, image);
        new PhotoDbHelper(this).addPhoto(photoMem);
        finish();
    }

    public void onButtonClickOpenCamera(View view) { //open camera app
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap image = (Bitmap) extras.get("data");
            imageViewCurrent.setImageBitmap(image);
        }
    }
}