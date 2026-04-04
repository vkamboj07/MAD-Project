package com.mad.q2.mediaplayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mad.q2.mediaplayer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    @Nullable
    private Uri audioUri;
    @Nullable
    private MediaPlayer mediaPlayer;

    /** When true, transport controls affect MediaPlayer; when false, VideoView. */
    private boolean audioMode = true;

    private final ActivityResultLauncher<String[]> pickAudio = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null) {
                    audioUri = uri;
                    audioMode = true;
                    binding.textStatus.setText(getString(R.string.audio_selected, uri.toString()));
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (binding.editVideoUrl.getText() == null || binding.editVideoUrl.getText().toString().trim().isEmpty()) {
            binding.editVideoUrl.setText(R.string.sample_video_url);
        }

        binding.buttonOpenFile.setOnClickListener(v ->
                pickAudio.launch(new String[]{"audio/*"})
        );

        binding.buttonOpenUrl.setOnClickListener(v -> {
            String url = binding.editVideoUrl.getText() != null
                    ? binding.editVideoUrl.getText().toString().trim()
                    : "";
            if (url.isEmpty()) {
                Toast.makeText(this, R.string.enter_url, Toast.LENGTH_SHORT).show();
                return;
            }
            audioMode = false;
            binding.textStatus.setText(getString(R.string.video_url_set, url));
        });

        binding.buttonPlay.setOnClickListener(v -> play());
        binding.buttonPause.setOnClickListener(v -> pause());
        binding.buttonStop.setOnClickListener(v -> stopPlayback());
        binding.buttonRestart.setOnClickListener(v -> restart());
    }

    private void play() {
        if (audioMode) {
            playAudio();
        } else {
            playVideo();
        }
    }

    private void playAudio() {
        if (audioUri == null) {
            Toast.makeText(this, R.string.pick_audio_first, Toast.LENGTH_SHORT).show();
            return;
        }
        binding.videoView.stopPlayback();
        binding.videoView.setVisibility(View.GONE);
        releaseAudioPlayer();
        MediaPlayer mp = MediaPlayer.create(this, audioUri);
        if (mp != null) {
            mediaPlayer = mp;
            mediaPlayer.setOnCompletionListener(m ->
                    binding.textStatus.setText(R.string.audio_finished)
            );
            mediaPlayer.start();
            binding.textStatus.setText(R.string.playing_audio);
        } else {
            Toast.makeText(this, R.string.cannot_open_audio, Toast.LENGTH_LONG).show();
        }
    }

    private void playVideo() {
        String url = binding.editVideoUrl.getText() != null
                ? binding.editVideoUrl.getText().toString().trim()
                : "";
        if (url.isEmpty()) {
            Toast.makeText(this, R.string.enter_url, Toast.LENGTH_SHORT).show();
            return;
        }
        releaseAudioPlayer();
        binding.videoView.setVisibility(View.VISIBLE);
        binding.videoView.setVideoURI(Uri.parse(url));
        binding.videoView.setOnPreparedListener(mp -> {
            mp.setLooping(false);
            binding.videoView.start();
            binding.textStatus.setText(R.string.playing_video);
        });
        binding.videoView.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(MainActivity.this, R.string.video_error, Toast.LENGTH_LONG).show();
            return true;
        });
    }

    private void pause() {
        if (audioMode) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        } else if (binding.videoView.isPlaying()) {
            binding.videoView.pause();
        }
        binding.textStatus.setText(R.string.paused);
    }

    private void stopPlayback() {
        if (audioMode) {
            if (mediaPlayer != null) {
                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                } catch (Exception ignored) {
                }
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } else {
            binding.videoView.stopPlayback();
            binding.videoView.setVisibility(View.GONE);
        }
        binding.textStatus.setText(R.string.stopped);
    }

    private void restart() {
        if (audioMode) {
            if (audioUri == null) {
                Toast.makeText(this, R.string.pick_audio_first, Toast.LENGTH_SHORT).show();
                return;
            }
            releaseAudioPlayer();
            MediaPlayer mp = MediaPlayer.create(this, audioUri);
            if (mp != null) {
                mediaPlayer = mp;
                mediaPlayer.start();
                binding.textStatus.setText(R.string.playing_audio);
            }
        } else {
            String url = binding.editVideoUrl.getText() != null
                    ? binding.editVideoUrl.getText().toString().trim()
                    : "";
            if (url.isEmpty()) {
                return;
            }
            binding.videoView.setVisibility(View.VISIBLE);
            binding.videoView.seekTo(0);
            binding.videoView.start();
            binding.textStatus.setText(R.string.playing_video);
        }
    }

    private void releaseAudioPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseAudioPlayer();
        binding.videoView.stopPlayback();
    }
}
