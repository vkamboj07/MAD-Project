package com.example.photogallerycamera;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.documentfile.provider.DocumentFile;

import com.bumptech.glide.Glide;
import com.example.photogallerycamera.utils.FileUtils;

public class ImageDetailActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_URI = "extra_image_uri";

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        ImageView imageView = findViewById(R.id.imagePreview);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvUri = findViewById(R.id.tvUri);
        TextView tvSize = findViewById(R.id.tvSize);
        TextView tvDate = findViewById(R.id.tvDate);
        Button btnDelete = findViewById(R.id.btnDelete);

        Intent intent = getIntent();
        String uriString = intent.getStringExtra(EXTRA_IMAGE_URI);
        if (uriString == null) {
            Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        imageUri = Uri.parse(uriString);
        DocumentFile doc = DocumentFile.fromSingleUri(this, imageUri);
        if (doc == null || !doc.exists()) {
            Toast.makeText(this, "Unable to read image", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Glide.with(this).load(imageUri).into(imageView);
        tvName.setText("Name: " + FileUtils.getDisplayName(doc));
        tvUri.setText("URI: " + imageUri);
        tvSize.setText("Size: " + FileUtils.getReadableSize(doc.length()));
        tvDate.setText("Last Modified: " + FileUtils.getFormattedDate(doc.lastModified()));

        btnDelete.setOnClickListener(v -> showDeleteDialog());
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("Delete", (dialog, which) -> deleteImage())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteImage() {
        try {
            DocumentFile doc = DocumentFile.fromSingleUri(this, imageUri);
            if (doc != null && doc.exists() && doc.delete()) {
                Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Unable to delete image", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Delete failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
