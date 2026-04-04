package com.example.photogallerycamera;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;

import com.example.photogallerycamera.utils.PermissionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class CameraActivity extends AppCompatActivity {

    private Uri tempCameraUri;
    private File tempCameraFile;

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                if (PermissionUtils.hasCameraAndStoragePermissions(this)) {
                    launchCamera();
                } else {
                    Toast.makeText(this, "Permissions are required to capture photos", Toast.LENGTH_LONG).show();
                }
            });

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    saveCapturedImageToSelectedFolder();
                } else {
                    Toast.makeText(this, "Photo capture cancelled", Toast.LENGTH_SHORT).show();
                    cleanupTempFile();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        Button btnCaptureNow = findViewById(R.id.btnCaptureNow);
        btnCaptureNow.setOnClickListener(v -> {
            if (PermissionUtils.hasCameraAndStoragePermissions(this)) {
                launchCamera();
            } else {
                permissionLauncher.launch(PermissionUtils.getCameraAndStoragePermissions());
            }
        });
    }

    private void launchCamera() {
        try {
            tempCameraFile = File.createTempFile("photo_", ".jpg", getExternalFilesDir("camera_temp"));
            tempCameraUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    tempCameraFile
            );

            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, tempCameraUri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cameraLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open camera: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void saveCapturedImageToSelectedFolder() {
        Uri selectedFolderUri = loadFolderUri();
        if (selectedFolderUri == null) {
            Toast.makeText(this, "No folder selected", Toast.LENGTH_SHORT).show();
            cleanupTempFile();
            return;
        }

        DocumentFile folder = DocumentFile.fromTreeUri(this, selectedFolderUri);
        if (folder == null || !folder.canWrite()) {
            Toast.makeText(this, "Cannot write to selected folder", Toast.LENGTH_SHORT).show();
            cleanupTempFile();
            return;
        }

        String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
        DocumentFile imageFile = folder.createFile("image/jpeg", fileName);
        if (imageFile == null) {
            Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show();
            cleanupTempFile();
            return;
        }

        try (InputStream inputStream = new FileInputStream(tempCameraFile);
             OutputStream outputStream = getContentResolver().openOutputStream(imageFile.getUri())) {

            if (outputStream == null) {
                Toast.makeText(this, "Failed to open destination", Toast.LENGTH_SHORT).show();
                cleanupTempFile();
                return;
            }

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();

            Toast.makeText(this, "Photo saved to selected folder", Toast.LENGTH_SHORT).show();
            cleanupTempFile();
        } catch (Exception e) {
            Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            cleanupTempFile();
        }
    }

    private Uri loadFolderUri() {
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
        String value = prefs.getString(MainActivity.KEY_FOLDER_URI, null);
        return value == null ? null : Uri.parse(value);
    }

    private void cleanupTempFile() {
        if (tempCameraFile != null && tempCameraFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            tempCameraFile.delete();
        }
    }
}
