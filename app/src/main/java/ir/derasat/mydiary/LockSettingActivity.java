package ir.derasat.mydiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

public class LockSettingActivity extends AppCompatActivity {

    // نمایش صفحه قفل بعد از وارد شدن به تنظیمات قفل امنیتی
    boolean isSet;
    boolean islocked;
    boolean fingerEnbl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_setting);
        CheckBox isLock = findViewById(R.id.lockedOn);
        CheckBox finger = findViewById(R.id.fingerPrint);
        TextView passTv =findViewById(R.id.passCode);
        TextView passWTv =findViewById(R.id.passWord);
        TextView patternTv =findViewById(R.id.pattern);

        SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
        islocked = sharedPreferences.getBoolean("LockEnabled", false);
        fingerEnbl =sharedPreferences.getBoolean("fingerEnabled",false);
        isSet =sharedPreferences.getBoolean("lockSet",false);

        isLock.setChecked(islocked);
        finger.setChecked(fingerEnbl);
        if (isLock.isChecked()){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("LockEnabled", true);
            editor.apply();
            finger.setEnabled(true);
            passTv.setEnabled(true);
            passWTv.setEnabled(true);
            passTv.setTextColor(Color.BLACK);
            passWTv.setTextColor(Color.BLACK);
            patternTv.setEnabled(true);
            patternTv.setTextColor(Color.BLACK);
        }else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("LockEnabled", false);
            editor.apply();
            finger.setEnabled(false);
            passTv.setEnabled(false);
            passWTv.setEnabled(false);
            passTv.setTextColor(Color.GRAY);
            passWTv.setTextColor(Color.GRAY);
            patternTv.setEnabled(false);
            patternTv.setTextColor(Color.GRAY);
        }

        patternTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LockSettingActivity.this,SecurityLockActivity.class).putExtra("Type","p"));
            }
        });
        passTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LockSettingActivity.this,SecurityLockActivity.class).putExtra("Type","pc"));
            }
        });
        passWTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LockSettingActivity.this,SecurityLockActivity.class).putExtra("Type","pw"));
            }
        });


        isLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                islocked=isChecked;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked){
                    editor.putBoolean("LockEnabled", islocked);

                    finger.setEnabled(true);
                    passTv.setEnabled(true);
                    passWTv.setEnabled(true);
                    passTv.setTextColor(Color.BLACK);
                    passWTv.setTextColor(Color.BLACK);
                    patternTv.setEnabled(true);
                    patternTv.setEnabled(true);
                    patternTv.setTextColor(Color.BLACK);
                }else {
                    editor.putBoolean("LockEnabled", islocked);
                    finger.setEnabled(false);
                    passTv.setEnabled(false);
                    passWTv.setEnabled(false);
                    passTv.setTextColor(Color.GRAY);
                    passWTv.setTextColor(Color.GRAY);
                    patternTv.setEnabled(false);
                    patternTv.setTextColor(Color.GRAY);
                }
                editor.apply();
            }
        });

        finger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fingerEnbl=isChecked;
                isSet=fingerEnbl;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked){
                    editor.putBoolean("fingerEnabled", fingerEnbl);
                    editor.putBoolean("lockSet", isSet);
                }else{
                    editor.putBoolean("fingerEnabled", fingerEnbl);

                }
                editor.apply();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (!isSet && islocked){
            Toast.makeText(this, "لطفا یک قفل تنظیم کنید", Toast.LENGTH_SHORT).show();
        }else {
            super.onBackPressed();    
        }
        
    }
}