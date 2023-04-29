package ir.derasat.mydiary;

import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.security.keystore.KeyProperties;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.security.Key;
import java.security.KeyStore;

import javax.crypto.Cipher;

public class LockActivity extends AppCompatActivity {

    private FingerprintManager fingerprintManager;
    private FingerprintHandler fingerprintHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        // Check if fingerprint authentication is available
        fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
        if (!fingerprintManager.isHardwareDetected()) {
            // Fingerprint sensor not available
            Toast.makeText(this, "Fingerprint sensor not available", Toast.LENGTH_SHORT).show();
            finish();
        } else if (!fingerprintManager.hasEnrolledFingerprints()) {
            // User has not enrolled any fingerprints
            Toast.makeText(this, "No fingerprints registered", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // Authenticate with the fingerprint
            fingerprintHandler = new FingerprintHandler(this);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (keyguardManager.isKeyguardSecure()) {
                // Create a CryptoObject to authenticate with the fingerprint
                try {
                    KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                    keyStore.load(null);
                    Key key = keyStore.getKey("my_key", null);
                    Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
                    cipher.init(Cipher.ENCRYPT_MODE, key);
                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);

                    // Start the fingerprint authentication process
                    CancellationSignal cancellationSignal = new CancellationSignal();
                    fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, fingerprintHandler, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Lock screen not set up", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // Handle the authentication events in the FingerprintHandler class
    @RequiresApi(api = Build.VERSION_CODES.M)
    private class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

        private Context context;

        public FingerprintHandler(Context context) {
            this.context = context;
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            Toast.makeText(context, "Authentication succeeded!", Toast.LENGTH_SHORT).show();
            // Handle authentication success
            finish();
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            Toast.makeText(context, "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            // Handle authentication error
        }

        @Override
        public void onAuthenticationFailed() {
            Toast.makeText(context, "Authentication failed!", Toast.LENGTH_SHORT).show();
            // Handle authentication failure
        }
    }
}