package ir.derasat.mydiary;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class AudioPlayerView extends FrameLayout implements Player.Listener {

    private ExoPlayer player;
    //private PlayerView playerView;
    private ImageView playButton;
    private TextView currentTimeTextView;
    private TextView durationTextView;
    private SeekBar seekBar;

    private Handler handler;
    private Runnable updateProgressRunnable;

    public AudioPlayerView(Context context) {
        super(context);
        init();
    }

    public AudioPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AudioPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_audio_player, this);

        //playerView = findViewById(R.id.playerView);
        playButton = findViewById(R.id.playButton);
        currentTimeTextView = findViewById(R.id.currentTimeTextView);
        durationTextView = findViewById(R.id.durationTextView);
        seekBar = findViewById(R.id.seekBar);

        player = new ExoPlayer.Builder(getContext()).build();
        player.addListener(this);

        //playerView.setPlayer(player);

        playButton.setOnClickListener(v -> {
            if (player.isPlaying()) {
                player.pause();
                playButton.setImageResource(R.drawable.play_back);

            } else {
                player.play();
                playButton.setImageResource(R.drawable.pause_back);
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    player.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateProgressRunnable);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.postDelayed(updateProgressRunnable, 1000);

            }
        });
        handler = new Handler();
        updateProgressRunnable = new Runnable() {
            @Override
            public void run() {
                updateProgress();
                handler.postDelayed(this, 1000);
            }
        };
    }


    public void setAudioUri(Uri audioUri) {
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                Util.getUserAgent(getContext(), "MyDiary"));
        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory, extractorsFactory)
                .createMediaSource(MediaItem.fromUri(audioUri));

        player.setMediaSource(mediaSource);
        player.prepare();
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case Player.STATE_READY:
                seekBar.setMax((int) player.getDuration());
                durationTextView.setText(formatTime((int) player.getDuration()));
                handler.post(updateProgressRunnable);
                break;
            case Player.STATE_ENDED:
                player.pause();

                player.seekTo(0);

                playButton.setImageResource(R.drawable.play_back);
                handler.post(updateProgressRunnable);
                break;
        }
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
        updateProgress();
    }
        private void updateProgress() {
            seekBar.setProgress((int) player.getCurrentPosition());
            currentTimeTextView.setText(formatTime((int) player.getCurrentPosition()));
        }

    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}