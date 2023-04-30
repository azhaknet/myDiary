package ir.derasat.mydiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ForgetPassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
        SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
        String SQ=sharedPreferences.getString("securityQuestion",null).replaceAll(" ","");
        String SA=sharedPreferences.getString("securityAnswer",null).replaceAll(" ","");

        Spinner sqs=findViewById(R.id.security_question_spinner);
        EditText sae=findViewById(R.id.security_answer_edittext);
        Button recBtn=findViewById(R.id.recoveryBtn);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{"What is your favorite color?", "What is your mother's maiden name?", "What is the name of your first pet?",
                "What is the name of your first school?",
                "What is your favorite food?",
                "What is the name of the city where you were born?",
                "What is your favorite movie?",
                "What is your favorite hobby?",
                "What is the name of your favorite teacher?",
                "What is your favorite sports team?",
                "What is your mother's middle name?"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sqs.setAdapter(adapter);


        recBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(sae.getText().toString())){
                    sae.setError("پاسخ امنیتی نمی تواند خالی باشد");
                }else {
                    if (SQ.equals(encryptPass(sqs.getSelectedItem().toString())) && SA.equals(encryptPass(sae.getText().toString()))){
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("lockSet", false);
                        editor.apply();
                        Toast.makeText(ForgetPassActivity.this, "پاسخ صحیح است. لطفا یک قفل جدید تنظیم کنید", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ForgetPassActivity.this,LockSettingActivity.class));
                        finish();
                    }else {
                        Toast.makeText(ForgetPassActivity.this, "سوال امنیتی یا پاسخ آن اشتباه است", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });


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