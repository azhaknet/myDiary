package ir.derasat.mydiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kevalpatel.passcodeview.PinView;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityLockActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_lock);
        PinView pinView = (PinView) findViewById(R.id.pin_view);

        Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder passcode = new StringBuilder();
                for (int k : pinView.getCurrentTypedPin()){
                    passcode.append(k);
                }

                if (validatePasscode(passcode.toString())) {
                    unlockApp();
                } else {
                    Toast.makeText(SecurityLockActivity.this, "Invalid passcode", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validatePasscode(String passcode) {
        String storedPasscode = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                .getString("passcode", null);
        if (storedPasscode == null) {
            return false;
        }
        // Compare the entered passcode with the stored passcode
        return storedPasscode.equals(encryptPasscode(passcode));
    }

    private void unlockApp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private String encryptPasscode(String passcode) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(passcode.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return passcode;
        }
    }
}