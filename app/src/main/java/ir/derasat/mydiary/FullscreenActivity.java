package ir.derasat.mydiary;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.Toast;

import ir.derasat.mydiary.databinding.ActivityFullscreenBinding;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {


    private ActivityFullscreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFullscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
        boolean islocked = sharedPreferences.getBoolean("LockEnabled", false);
        boolean fingerEnbl =sharedPreferences.getBoolean("fingerEnabled",false);
        Toast.makeText(this, "lock State: "+islocked, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "finger State: "+fingerEnbl, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (islocked) {
                    intent = new Intent(FullscreenActivity.this, LockActivity.class);
                    intent.putExtra("finger", fingerEnbl);
                    startActivity(intent);
                }
                else {
                    intent = new Intent(FullscreenActivity.this,MainActivity.class);
                    startActivity(intent);
                }

                finish();
            }
        }, 3000); //Delay for 2 seconds (2000 milliseconds)




        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
    }

}