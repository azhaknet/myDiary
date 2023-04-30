package ir.derasat.mydiary;


import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;
import static androidx.constraintlayout.widget.StateSet.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.Executor;

import ir.derasat.mydiary.listener.PatternLockViewListener;
import ir.derasat.mydiary.pin.IndicatorDots;
import ir.derasat.mydiary.pin.PinLockListener;
import ir.derasat.mydiary.pin.PinLockView;
import ir.derasat.mydiary.utils.PatternLockUtils;

public class LockActivity extends AppCompatActivity {

    public static final String TAG = "PinLockView";
    private PatternLockView patternLockView;

    private EditText passwedit;
    private Button submit;
    SharedPreferences sharedPreferences;
    private PinLockView pinLockView;
    private IndicatorDots mIndicatorDots;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private static final int REQUEST_CODE = 101010;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        LinearLayout pwly=findViewById(R.id.passwordLay);
        LinearLayout pcly=findViewById(R.id.pinLay);


        sharedPreferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
        patternLockView = findViewById(R.id.patter_lock_view);
        pinLockView = findViewById(R.id.pinLockView);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicatorDots);
        passwedit=findViewById(R.id.editTextPassword);
        submit=findViewById(R.id.goBtn);

        pinLockView.attachIndicatorDots(mIndicatorDots);
        pinLockView.setPinLength(4);
        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);
        String correctPass="";
        String Type = sharedPreferences.getString("passType",null);
        switch (Type){
            case "p":
                correctPass=sharedPreferences.getString("pattern",null);
                patternLockView.setVisibility(View.VISIBLE);
                pwly.setVisibility(View.GONE);
                pcly.setVisibility(View.GONE);

                break;
            case "pc":
                correctPass=sharedPreferences.getString("passcode",null);
                patternLockView.setVisibility(View.GONE);
                pwly.setVisibility(View.GONE);
                pcly.setVisibility(View.VISIBLE);
                break;
            case "pw":
                correctPass=sharedPreferences.getString("password",null);
                patternLockView.setVisibility(View.GONE);
                pwly.setVisibility(View.VISIBLE);
                pcly.setVisibility(View.GONE);
                break;
        }

        String finalCorrectPass = correctPass.replaceAll(" ", "");


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputP=passwedit.getText().toString();
                if (finalCorrectPass.equals(encryptPass(inputP))){
                    Toast.makeText(LockActivity.this, "password correct", Toast.LENGTH_SHORT).show();
                    unlockApp();
                }else {
                    Toast.makeText(LockActivity.this, "password incorrect", Toast.LENGTH_SHORT).show();
                    passwedit.setError("رمز وارد شده اشتباه است");
                    passwedit.setText("");

                }
            }
        });
        patternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {
                Log.d(getClass().getName(), "Pattern drawing started");
            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {
                Log.d(getClass().getName(), "Pattern progress: " +
                        PatternLockUtils.patternToString(patternLockView, progressPattern));
            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                if (finalCorrectPass.equals(PatternLockUtils.patternToSha256(patternLockView,pattern))){
                    patternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                    Toast.makeText(LockActivity.this, "password correct", Toast.LENGTH_SHORT).show();
                    unlockApp();
                }else {
                    patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);

                }
            }

            @Override
            public void onCleared() {
                Log.d(getClass().getName(), "Pattern has been cleared");
            }
        });

        pinLockView.setPinLockListener(new PinLockListener() {
            @Override
            public void onComplete(String pin) {
                Log.d(TAG, "Pin complete: " + pin);
                if (finalCorrectPass.equals(encryptPass(pin))){
                    Toast.makeText(LockActivity.this, "password correct", Toast.LENGTH_SHORT).show();
                    unlockApp();
                }else {
                    pinLockView.resetPinLockView();
                    Toast.makeText(LockActivity.this, "password incorrect", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onEmpty() {
                Log.d(TAG, "Pin empty");
            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {
                Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
            }
        });

        ImageView fingerBtn=findViewById(R.id.fingerprint_icon);
        TextView fingerSt=findViewById(R.id.fingerprint_status);
        boolean fingerOn=getIntent().getExtras().getBoolean("finger");


        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M && fingerOn) {
            BiometricManager biometricManager = BiometricManager.from(LockActivity.this);
            switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
                case BiometricManager.BIOMETRIC_SUCCESS:
                    Log.e("MY_APP_TAG", "App can authenticate using biometrics.");
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    Log.e("MY_APP_TAG", "Fingerprint sensor Not Exist");
                    break;
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    Log.e("MY_APP_TAG", "Fingerprint sensor not avail or busy");
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    // Prompts the user to create credentials that your app accepts.
                    final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                    enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                    startActivityForResult(enrollIntent, REQUEST_CODE);
                    break;
            }

            executor = ContextCompat.getMainExecutor(this);
            biometricPrompt = new BiometricPrompt(LockActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    //Toast.makeText(getApplicationContext(), "خطا در شناسایی: " + errString, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAuthenticationSucceeded(
                        @NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);

                    Toast.makeText(getApplicationContext(), "شناسایی موفق!", Toast.LENGTH_SHORT).show();

                    //Redirect User to Home Screen
                    unlockApp();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(getApplicationContext(), "شناسایی ناموفق", Toast.LENGTH_SHORT).show();
                }
            });

            //create Your BiometricPrompt

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("اثر انگشت")
                    .setDescription("بدون وارد کردن رمز عبور وارد برنامه شوید")
                    .setNegativeButtonText("انصراف")
                    .setDeviceCredentialAllowed(false)
                    .setConfirmationRequired(true)
                    .build();
            biometricPrompt.authenticate(promptInfo);

            fingerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    biometricPrompt.authenticate(promptInfo);
                }
            });
        }else {
            fingerBtn.setVisibility(View.GONE);
            fingerSt.setVisibility(View.GONE);
        }

        //


       /* Button submitButton = findViewById(R.id.next_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder passcode = new StringBuilder();


                if (validatePasscode(passcode.toString())) {
                    unlockApp();
                } else {
                    Toast.makeText(LockActivity.this, "رمز نا معتبر ", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    private void unlockApp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private String encryptPass(String pass) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(pass.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(hash, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

    }
}