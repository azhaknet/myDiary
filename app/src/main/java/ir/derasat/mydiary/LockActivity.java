package ir.derasat.mydiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LockActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);


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