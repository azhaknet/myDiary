package ir.derasat.mydiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class LockSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_setting);
        CheckBox isLock = findViewById(R.id.lockedOn);
        CheckBox finger = findViewById(R.id.fingerPrint);
        TextView passTv =findViewById(R.id.passCode);
        TextView patternTv =findViewById(R.id.pattern);

        SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
        boolean islocked = sharedPreferences.getBoolean("LockEnabled", false);
        boolean fingerEnbl =sharedPreferences.getBoolean("fingerEnabled",false);

        isLock.setChecked(islocked);
        finger.setChecked(fingerEnbl);
        if (isLock.isChecked()){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("LockEnabled", true);
            editor.apply();
            finger.setEnabled(true);
            passTv.setEnabled(true);
            passTv.setTextColor(Color.BLACK);
            patternTv.setEnabled(true);
            patternTv.setTextColor(Color.BLACK);
        }else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("LockEnabled", false);
            editor.apply();
            finger.setEnabled(false);
            passTv.setEnabled(false);
            passTv.setTextColor(Color.GRAY);
            patternTv.setEnabled(false);
            patternTv.setTextColor(Color.GRAY);
        }


        isLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isLock.isChecked()){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("LockEnabled", true);
                    editor.apply();
                    finger.setEnabled(true);
                    passTv.setEnabled(true);
                    passTv.setTextColor(Color.BLACK);
                    patternTv.setEnabled(true);
                    patternTv.setTextColor(Color.BLACK);
                }else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("LockEnabled", false);
                    editor.apply();
                    finger.setEnabled(false);
                    passTv.setEnabled(false);
                    passTv.setTextColor(Color.GRAY);
                    patternTv.setEnabled(false);
                    patternTv.setTextColor(Color.GRAY);
                }
            }
        });

        finger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (finger.isChecked()){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("fingerEnabled", true);
                    editor.apply();
                }else{
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("fingerEnabled", false);
                    editor.apply();
                }
            }
        });

    }
}