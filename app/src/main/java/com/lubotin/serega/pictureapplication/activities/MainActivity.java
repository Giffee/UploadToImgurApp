package com.lubotin.serega.pictureapplication.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import com.lubotin.serega.pictureapplication.R;
import com.lubotin.serega.pictureapplication.adapters.RecyclerViewAdapter;
import com.lubotin.serega.pictureapplication.model.Image;
import com.lubotin.serega.pictureapplication.utils.Permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//TODO make navigationView from right side
public class MainActivity extends AppCompatActivity {
    public static final int PORTRAIT_COLUMNS_COUNT = 3;
    public static final int LANDSCAPE_COLUMNS_COUNT = 5;
    final int REQUEST_PERMISSION = 1;
    private RecyclerView recyclerView;
    private Button addAllLinksButton;
    private ListView listView;

    private List<Image> imagesList;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initViews();
        checkPermissions();

    }

    //All views initialization
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        addAllLinksButton = findViewById(R.id.addAllLinksButton);
        drawerLayout = findViewById(R.id.drawer_layout);
        listView = findViewById(R.id.drawer_list_right);
    }

    //Toolbar initialization
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

    }

    //Check permissions for read/write access
    public void checkPermissions() {
        String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!Permissions.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION);
        } else {
            imagesList = getImagesPath();
            initAdapter();
        }
    }

    //Get path of all images
    public ArrayList<Image> getImagesPath() {
        ArrayList<Image> listOfAllImages = new ArrayList<>();
        int columnIndex;
        String pathOfImage;
        String[] projection = {MediaStore.MediaColumns.DATA};
        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
            assert cursor != null;
            cursor.moveToLast();
            columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
            do {
                pathOfImage = cursor.getString(columnIndex);
                listOfAllImages.add(new Image(pathOfImage));
            } while (cursor.moveToPrevious());
        }
        return listOfAllImages;
    }

    //RecyclerView adapter initialization
    private void initAdapter() {
        if (!isAnyImages()) {
            return;
        }
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, imagesList, addAllLinksButton, listView, drawerLayout);
        //Count of columns to create in which orientation
        Configuration configuration = getResources().getConfiguration();
//        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//            recyclerView.setLayoutManager(new GridLayoutManager(this, PORTRAIT_COLUMNS_COUNT));
//        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            recyclerView.setLayoutManager(new GridLayoutManager(this, LANDSCAPE_COLUMNS_COUNT));
//        }

        onConfigurationChanged(configuration);
        recyclerView.setAdapter(adapter);
    }

    //Does user have any images?
    private boolean isAnyImages() {
        boolean b = true;
        if (imagesList.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("You dont have any images in your gallery, " +
                            "please restart application after you take one")
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            b = false;
        }
        return b;
    }

    //Result of request for permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == 0) {
            imagesList = getImagesPath();
            initAdapter();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Permissions error")
                    .setMessage("You didnt grant permissions. You should grant it for correct application work.")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Grant permissions", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkPermissions();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, LANDSCAPE_COLUMNS_COUNT));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, PORTRAIT_COLUMNS_COUNT));
        }
    }
}

