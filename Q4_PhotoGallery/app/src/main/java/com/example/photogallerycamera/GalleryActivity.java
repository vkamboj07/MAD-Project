package com.example.photogallerycamera;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photogallerycamera.adapter.ImageAdapter;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity implements ImageAdapter.OnImageClickListener {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private ImageAdapter adapter;
    private final List<Uri> imageUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerView);
        tvEmpty = findViewById(R.id.tvEmpty);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new ImageAdapter(imageUris, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImagesFromSelectedFolder();
    }

    private void loadImagesFromSelectedFolder() {
        imageUris.clear();
        Uri folderUri = loadFolderUri();
        if (folderUri == null) {
            Toast.makeText(this, "No folder selected", Toast.LENGTH_SHORT).show();
            showEmptyState(true);
            return;
        }

        DocumentFile folder = DocumentFile.fromTreeUri(this, folderUri);
        if (folder == null || !folder.exists()) {
            Toast.makeText(this, "Selected folder is unavailable", Toast.LENGTH_SHORT).show();
            showEmptyState(true);
            return;
        }

        DocumentFile[] files = folder.listFiles();
        for (DocumentFile file : files) {
            String type = file.getType();
            if (file.isFile() && type != null && type.startsWith("image/")) {
                imageUris.add(file.getUri());
            }
        }

        adapter.notifyDataSetChanged();
        showEmptyState(imageUris.isEmpty());
    }

    private void showEmptyState(boolean isEmpty) {
        tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private Uri loadFolderUri() {
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
        String value = prefs.getString(MainActivity.KEY_FOLDER_URI, null);
        return value == null ? null : Uri.parse(value);
    }

    @Override
    public void onImageClick(Uri imageUri) {
        Intent intent = new Intent(this, ImageDetailActivity.class);
        intent.putExtra(ImageDetailActivity.EXTRA_IMAGE_URI, imageUri.toString());
        startActivity(intent);
    }
}
