package com.mad.q2.mediaplayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mad.q2.mediaplayer.databinding.ActivityMainBinding;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Nullable private Uri audioUri;
    @Nullable private MediaPlayer mediaPlayer;

    /** true = audio mode, false = video mode */
    private boolean audioMode = true;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean userSeeking = false;

    // Runnable that ticks every 500 ms to update the seek bar
    private final Runnable progressTick = new Runnable() {
        @Override
        public void run() {
            updatePlayerBar();
            handler.postDelayed(this, 500);
        }
    };

    private final ActivityResultLauncher<String[]> pickAudio = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null) {
                    audioUri = uri;
                    audioMode = true;
                    binding.textStatus.setText(getString(R.string.audio_selected, uri.getLastPathSegment()));
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (binding.editVideoUrl.getText() == null
                || binding.editVideoUrl.getText().toString().trim().isEmpty()) {
            binding.editVideoUrl.setText(R.string.sample_video_url);
        }

        binding.buttonOpenFile.setOnClickListener(v -> pickAudio.launch(new String[]{"audio/*"}));

        binding.buttonOpenUrl.setOnClickListener(v -> {
            String url = urlText();
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

        // SeekBar drag
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                if (!fromUser) return;
                int duration = getDuration();
                if (duration > 0) {
                    int target = (int) ((long) progress * duration / 1000);
                    if (audioMode && mediaPlayer != null) {
                        mediaPlayer.seekTo(target);
                    } else {
                        binding.videoView.seekTo(target);
                    }
                    binding.textCurrentTime.setText(formatMs(target));
                }
            }
            @Override public void onStartTrackingTouch(SeekBar sb) { userSeeking = true; }
            @Override public void onStopTrackingTouch(SeekBar sb)  { userSeeking = false; }
        });
    }

    // ── Playback ──────────────────────────────────────────────────────────────

    private void play() {
        if (audioMode) playAudio(); else playVideo();
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
        if (mp == null) {
            Toast.makeText(this, R.string.cannot_open_audio, Toast.LENGTH_LONG).show();
            return;
        }
        mediaPlayer = mp;
        mediaPlayer.setOnCompletionListener(m -> {
            binding.textStatus.setText(R.string.audio_finished);
            hidePlayerBar();
        });
        mediaPlayer.start();
        binding.textStatus.setText(R.string.playing_audio);
        String name = audioUri.getLastPathSegment();
        showPlayerBar(name != null ? name : getString(R.string.playing_audio));
        startProgressUpdates();
    }

    private void playVideo() {
        String url = urlText();
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
            showPlayerBar(url);
            startProgressUpdates();
        });
        binding.videoView.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(this, R.string.video_error, Toast.LENGTH_LONG).show();
            hidePlayerBar();
            return true;
        });
    }

    private void pause() {
        if (audioMode) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.pause();
        } else if (binding.videoView.isPlaying()) {
            binding.videoView.pause();
        }
        binding.textStatus.setText(R.string.paused);
        stopProgressUpdates();
    }

    private void stopPlayback() {
        stopProgressUpdates();
        if (audioMode) {
            if (mediaPlayer != null) {
                try { if (mediaPlayer.isPlaying()) mediaPlayer.stop(); } catch (Exception ignored) {}
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } else {
            binding.videoView.stopPlayback();
            binding.videoView.setVisibility(View.GONE);
        }
        binding.textStatus.setText(R.string.stopped);
        hidePlayerBar();
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
                String name = audioUri.getLastPathSegment();
                showPlayerBar(name != null ? name : getString(R.string.playing_audio));
                startProgressUpdates();
            }
        } else {
            String url = urlText();
            if (url.isEmpty()) return;
            binding.videoView.setVisibility(View.VISIBLE);
            binding.videoView.seekTo(0);
            binding.videoView.start();
            binding.textStatus.setText(R.string.playing_video);
            showPlayerBar(url);
            startProgressUpdates();
        }
    }

    // ── Player bar helpers ────────────────────────────────────────────────────

    private void showPlayerBar(String title) {
        binding.textTrackTitle.setText(title);
        binding.playerBar.setVisibility(View.VISIBLE);
    }

    private void hidePlayerBar() {
        binding.playerBar.setVisibility(View.GONE);
        binding.seekBar.setProgress(0);
        binding.textCurrentTime.setText("0:00");
        binding.textTotalTime.setText("0:00");
    }

    private void startProgressUpdates() {
        handler.removeCallbacks(progressTick);
        handler.post(progressTick);
    }

    private void stopProgressUpdates() {
        handler.removeCallbacks(progressTick);
    }

    private void updatePlayerBar() {
        if (userSeeking) return;
        int pos = getPosition();
        int dur = getDuration();
        if (dur > 0) {
            binding.seekBar.setProgress((int) ((long) pos * 1000 / dur));
        }
        binding.textCurrentTime.setText(formatMs(pos));
        binding.textTotalTime.setText(formatMs(dur));
    }

    private int getPosition() {
        try {
            if (audioMode) {
                return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
            } else {
                return binding.videoView.getCurrentPosition();
            }
        } catch (Exception e) { return 0; }
    }

    private int getDuration() {
        try {
            if (audioMode) {
                return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
            } else {
                return binding.videoView.getDuration();
            }
        } catch (Exception e) { return 0; }
    }

    private static String formatMs(int ms) {
        if (ms < 0) ms = 0;
        int totalSec = ms / 1000;
        int min = totalSec / 60;
        int sec = totalSec % 60;
        return String.format(Locale.US, "%d:%02d", min, sec);
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    private void releaseAudioPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private String urlText() {
        return binding.editVideoUrl.getText() != null
                ? binding.editVideoUrl.getText().toString().trim()
                : "";
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopProgressUpdates();
        releaseAudioPlayer();
        binding.videoView.stopPlayback();
    }
}
