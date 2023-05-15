package ir.derasat.mydiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RecordActivity extends AppCompatActivity {

    public static final String EXTRA_OUTPUT_FILE = "output_file";

    private MediaRecorder mRecorder;
    private String mOutputFile;
    private ProgressBar mProgressBar;
    private TextView mDurationTextView;
    private Handler mHandler = new Handler();
    private long mStartTimeMillis;
    private Button mRecordButton;
    private Button mPlayButton;
    private Button mResetButton;
    private Button mConfirmButton;
    private Button mDiscardButton;
    private boolean mIsRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        mRecordButton = findViewById(R.id.record_button);
        mPlayButton = findViewById(R.id.play_button);
        mResetButton = findViewById(R.id.reset_button);
        mConfirmButton = findViewById(R.id.confirm_button);
        mDiscardButton = findViewById(R.id.discard_button);
        mProgressBar = findViewById(R.id.progress_bar);
        mDurationTextView = findViewById(R.id.duration_text_view);

        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecordClick(v);
            }
        });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlayClick(v);
            }
        });

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResetClick(v);
            }
        });

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmClick(v);
            }
        });

        mDiscardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDiscardClick(v);
            }
        });
    }

    private void onRecordClick(View view) {
        if (mIsRecording) {
            // Stop recording
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            mRecordButton.setText("record");
            stopProgressBar();
            stopRecordingDuration();
            mPlayButton.setEnabled(true);
            mResetButton.setVisibility(View.VISIBLE);
            mConfirmButton.setVisibility(View.VISIBLE);
            mDiscardButton.setVisibility(View.VISIBLE);
            mIsRecording = false;
        } else {
            // Start recording
            mOutputFile = getExternalCacheDir().getAbsolutePath() + "/recording.3gp";
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mOutputFile);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("RecordAudio", "prepare() failed");
            }

            mPlayButton.setEnabled(false);
            mRecordButton.setText("stop");
            mRecorder.start();
            startProgressBar();
            startRecordingDuration();
            mResetButton.setVisibility(View.INVISIBLE);
            mConfirmButton.setVisibility(View.INVISIBLE);
            mDiscardButton.setVisibility(View.INVISIBLE);
            mIsRecording = true;
        }
    }

    private void onPlayClick(View view) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(mOutputFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e("RecordAudio", "playback failed");
        }
    }

    private void onResetClick(View view) {
        mOutputFile = null;
        mProgressBar.setProgress(0);
        mDurationTextView.setVisibility(View.INVISIBLE);
        mRecordButton.setEnabled(true);
        mPlayButton.setEnabled(false);
        mResetButton.setVisibility(View.INVISIBLE);
        mConfirmButton.setVisibility(View.INVISIBLE);
        mDiscardButton.setVisibility(View.INVISIBLE);
    }

    private void onConfirmClick(View view) {
        // Return the recorded audio file path as a result
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_OUTPUT_FILE, mOutputFile);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void onDiscardClick(View view) {
        File file = new File(mOutputFile);
        if (file.exists()) {
            file.delete();
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    private void startProgressBar() {
        mProgressBar.setProgress(0);
        mProgressBar.setMax(100);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int progress = (int) ((System.currentTimeMillis() - mStartTimeMillis) / 10);
                mProgressBar.setProgress(progress);
                if (progress < 100) {
                    mHandler.postDelayed(this, 10);
                }
            }
        }, 10);
    }

    private void stopProgressBar() {
        mHandler.removeCallbacksAndMessages(null);
    }

    private void startRecordingDuration() {
        mStartTimeMillis = System.currentTimeMillis();
        mDurationTextView.setVisibility(View.VISIBLE);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                long durationMillis = System.currentTimeMillis() - mStartTimeMillis;
                mDurationTextView.setText(getDurationString(durationMillis));
                mHandler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private void stopRecordingDuration() {
        mHandler.removeCallbacksAndMessages(null);
        mDurationTextView.setVisibility(View.INVISIBLE);
    }

    private String getDurationString(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
                - TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%02d:%02d", minutes, seconds);
    }
}