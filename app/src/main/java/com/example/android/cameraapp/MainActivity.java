package com.example.android.cameraapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView mImageView;
    private Uri imageUri;
    private String LOGTAG = "CameraApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        mImageView = (ImageView) findViewById(R.id.iv);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            deleteImg();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Uri selectedImage = imageUri;
            this.getContentResolver().notifyChange(selectedImage, null);

            ContentResolver cr = this.getContentResolver();
            Bitmap imageBitmap;

            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(cr, selectedImage);
                int nh = (int) ( imageBitmap.getHeight() * (512.0 / imageBitmap.getWidth()) ); //Calculate height of scaled-down bitmap
                imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 512, nh, true);
                mImageView.setImageBitmap(imageBitmap);
            } catch (Exception e) {
                Log.e(LOGTAG, e.toString());
            }
        }
    }

    public void takePicture() {
        String filename = "CameraApp.png";

        File dir = new File(Environment.getExternalStorageDirectory().toString() + "/CameraApp/");
        if (!dir.exists()) {
            dir.mkdir();
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(dir, filename);
        imageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void deleteImg() {
        String filename = "CameraApp.png";
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/CameraApp/" + filename);
        File dir = new File(Environment.getExternalStorageDirectory().toString() + "/CameraApp/");

        if (file.exists()) {
            if(file.delete()) Log.d(LOGTAG, "Deleted file");
        }

        if (dir.exists()) {
            if(dir.delete()) Log.d(LOGTAG, "Deleted directory");
        }
    }
}
