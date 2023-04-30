package ir.derasat.mydiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import ir.derasat.mydiary.utils.PatternLockUtils;

public class SecurityLockActivity extends AppCompatActivity {


    String firstPass,secondPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_lock);
        String Type=getIntent().getExtras().getString("Type");

        Button nextBtn= findViewById(R.id.next_button);
        Button checkBtn= findViewById(R.id.check_button);
        TextView guide = findViewById(R.id.guidetv1);
        PatternLockView patternLockView= findViewById(R.id.patter_lock_view);
        LinearLayout passLay=findViewById(R.id.passLy);
        EditText fPass=findViewById(R.id.firstPass);
        EditText sPass=findViewById(R.id.secondPass);
        Spinner sqs=findViewById(R.id.SecurityQ);
        EditText sae=findViewById(R.id.SecurityA);
        patternLockView.getPattern().toArray();
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


        switch (Type){
            case "p":
                patternLockView.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.VISIBLE);
                checkBtn.setVisibility(View.GONE);
                fPass.setVisibility(View.GONE);
                sPass.setVisibility(View.GONE);
                guide.setText("لطفا الگوی خود را وارد کنید");
                break;
            case "pc":
                patternLockView.setVisibility(View.GONE);
                nextBtn.setVisibility(View.GONE);
                checkBtn.setVisibility(View.VISIBLE);
                fPass.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                fPass.setVisibility(View.VISIBLE);
                sPass.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                sPass.setVisibility(View.VISIBLE);
                guide.setText("لطفا رمز عبور خود را وارد کنید");
                break;
            case "pw":
                patternLockView.setVisibility(View.GONE);
                nextBtn.setVisibility(View.GONE);
                checkBtn.setVisibility(View.VISIBLE);
                fPass.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD);
                fPass.setFilters(new InputFilter[] {new InputFilter.LengthFilter(16)});
                fPass.setVisibility(View.VISIBLE);
                sPass.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD);
                sPass.setFilters(new InputFilter[] {new InputFilter.LengthFilter(16)});
                sPass.setVisibility(View.VISIBLE);
                guide.setText("لطفا کلمه عبور خود را وارد کنید");

                break;
        }





        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                firstPass= PatternLockUtils.patternToString(patternLockView,patternLockView.getPattern());
                nextBtn.setVisibility(View.GONE);
                guide.setText("لطفا الگوی خود را تکرار کنید");
                checkBtn.setVisibility(View.VISIBLE);
                patternLockView.clearPattern();
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(sae.getText().toString())){
                    sae.setError("پاسخ امنیتی نمی تواند خالی باشد");
                }else {

                    switch (Type) {
                        case "p":
                            secondPass=PatternLockUtils.patternToString(patternLockView,patternLockView.getPattern());

                            if (!firstPass.equals(secondPass)){
                                guide.setText("دوباره تلاش کنید");
                                checkBtn.setVisibility(View.GONE);
                                nextBtn.setVisibility(View.VISIBLE);
                                patternLockView.clearPattern();
                                Toast.makeText(SecurityLockActivity.this, "تکرار الگو صحیح نمی باشد", Toast.LENGTH_LONG).show();
                            }else {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("pattern", PatternLockUtils.patternToSha256(patternLockView,patternLockView.getPattern()));
                                editor.putString("passType", "p");
                                editor.putString("securityQuestion", encryptPass(sqs.getSelectedItem().toString()));
                                editor.putString("securityAnswer", encryptPass(sae.getText().toString()));
                                editor.putBoolean("lockSet", true);
                                editor.apply();
                                Toast.makeText(SecurityLockActivity.this, "قفل با موفقیت تنظیم شد", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            break;
                        case "pc":
                            if (fPass.getText().toString().length()<4)
                                fPass.setError("رمز باید 4 رقم باشد");
                            else if (TextUtils.isEmpty(fPass.getText().toString()))
                                fPass.setError("رمز نمی تواند خالی باشد");
                            else if (TextUtils.isEmpty(sPass.getText().toString()))
                                sPass.setError("تکرار رمز نمی تواند خالی باشد");
                            else {
                                firstPass = fPass.getText().toString();
                                secondPass = sPass.getText().toString();
                                if (!firstPass.equals(secondPass))
                                    sPass.setError("تکرار رمز صحیح نیست");
                                else{
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("passcode", encryptPass(firstPass));
                                    editor.putString("passType", "pc");
                                    editor.putString("securityQuestion", encryptPass(sqs.getSelectedItem().toString()));
                                    editor.putString("securityAnswer", encryptPass(sae.getText().toString()));
                                    editor.putBoolean("lockSet", true);
                                    editor.apply();
                                    Toast.makeText(SecurityLockActivity.this, "قفل با موفقیت تنظیم شد", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                            break;
                        case "pw":
                            if (fPass.getText().toString().length()<4)
                                fPass.setError("رمز باید حداقل 4 کارکتر باشد");
                            else if (TextUtils.isEmpty(fPass.getText().toString()))
                                fPass.setError("رمز نمی تواند خالی باشد");
                            else if (TextUtils.isEmpty(sPass.getText().toString()))
                                sPass.setError("تکرار رمز نمی تواند خالی باشد");
                            else {
                                firstPass = fPass.getText().toString();
                                secondPass = sPass.getText().toString();
                                if (!firstPass.equals(secondPass))
                                    sPass.setError("تکرار رمز صحیح نیست");
                                else{
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("password", encryptPass(firstPass));
                                    editor.putString("passType", "pw");
                                    editor.putString("securityQuestion", encryptPass(sqs.getSelectedItem().toString()));
                                    editor.putString("securityAnswer", encryptPass(sae.getText().toString()));
                                    editor.putBoolean("lockSet", true);
                                    editor.apply();
                                    Toast.makeText(SecurityLockActivity.this, "قفل با موفقیت تنظیم شد", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                            break;

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