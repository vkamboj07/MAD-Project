package com.example.photogallerycamera;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.documentfile.provider.DocumentFile;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "gallery_prefs";
    public static final String KEY_FOLDER_URI = "folder_uri";

    private TextView tvSelectedFolder;
    private Uri selectedFolderUri;

    private final ActivityResultLauncher<Uri> openTreeLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocumentTree(), uri -> {
                if (uri == null) {
                    Toast.makeText(this, "Folder selection cancelled", Toast.LENGTH_SHORT).show();
                    return;
                }

                final int flags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(uri, flags);
                saveFolderUri(uri);
                selectedFolderUri = uri;
                updateFolderLabel();
                Toast.makeText(this, "Folder selected successfully", Toast.LENGTH_SHORT).show();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvSelectedFolder = findViewById(R.id.tvSelectedFolder);
        Button btnSelectFolder = findViewById(R.id.btnSelectFolder);
        Button btnCapturePhoto = findViewById(R.id.btnCapturePhoto);
        Button btnOpenGallery = findViewById(R.id.btnOpenGallery);

        selectedFolderUri = loadFolderUri();
        updateFolderLabel();

        btnSelectFolder.setOnClickListener(v -> openTreeLauncher.launch(null));

        btnCapturePhoto.setOnClickListener(v -> {
            if (!hasSelectedWritableFolder()) {
                Toast.makeText(this, "Please select a folder first", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, CameraActivity.class));
        });

        btnOpenGallery.setOnClickListener(v -> {
            if (!hasSelectedWritableFolder()) {
                Toast.makeText(this, "Please select a folder first", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, GalleryActivity.class));
        });
    }

    private void updateFolderLabel() {
        if (selectedFolderUri == null) {
            tvSelectedFolder.setText("No folder selected");
            return;
        }
        DocumentFile doc = DocumentFile.fromTreeUri(this, selectedFolderUri);
        String folderName = doc != null && doc.getName() != null ? doc.getName() : selectedFolderUri.toString();
        tvSelectedFolder.setText("Selected Folder: " + folderName);
    }

    private boolean hasSelectedWritableFolder() {
        if (selectedFolderUri == null) {
            return false;
        }
        DocumentFile folder = DocumentFile.fromTreeUri(this, selectedFolderUri);
        return folder != null && folder.exists() && folder.canWrite();
    }

    private void saveFolderUri(Uri uri) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_FOLDER_URI, uri.toString()).apply();
    }

    private Uri loadFolderUri() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String value = prefs.getString(KEY_FOLDER_URI, null);
        return value == null ? null : Uri.parse(value);
    }
}
