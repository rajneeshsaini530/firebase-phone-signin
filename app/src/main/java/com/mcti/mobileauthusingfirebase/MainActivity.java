    package com.mcti.mobileauthusingfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

    public class MainActivity extends AppCompatActivity {

    private EditText mobile;
    private EditText OTP;
    private Button sendOtp;
    private Button verifyOtp;
    private String code;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mobile = findViewById(R.id.mobile);
        OTP = findViewById(R.id.otp);
        sendOtp = findViewById(R.id.send_otp);
        verifyOtp = findViewById(R.id.verify);
        verifyOtp.setClickable(false);
        verifyOtp.setEnabled(false);
        mAuth = FirebaseAuth.getInstance();

        sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobileNumber = mobile.getText().toString();
                if("".equals(mobileNumber)){
                    Toast.makeText(MainActivity.this, "Enter mobile number", Toast.LENGTH_SHORT).show();
                }
                else{
                    sendOtp("+91"+mobileNumber);
                    verifyOtp.setClickable(true);
                    verifyOtp.setEnabled(true);
                }
            }
        });

        verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = OTP.getText().toString().trim();
                if("".equals(otp)){
                    Toast.makeText(MainActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                }
                else{
                   PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(code,otp);
                   signInWithPhoneAuthCredential(phoneAuthCredential);
                }
            }
        });

    }

    private void sendOtp(String mobileNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobileNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Toast.makeText(MainActivity.this, "verification completed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(MainActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        code = s;
                        Toast.makeText(MainActivity.this, "OTP send", Toast.LENGTH_SHORT).show();
                    }
                });
    }
        private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //do something when task successful
                                Toast.makeText(MainActivity.this, "sign In successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                finish();
                            } else {
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    // The verification code entered was invalid
                                    Toast.makeText(MainActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }
}
