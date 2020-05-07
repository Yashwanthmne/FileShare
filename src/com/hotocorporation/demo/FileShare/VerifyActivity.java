package com.hotocorporation.demo.FileShare;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;


import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    String mVerificationId;
    PinView pinView;
    TextView time_counter_text;
    Button verify_button;
    String codeSent;

    DynamoDBMapper dynamoDBMapper;
    private AmazonDynamoDBClient dynamoDBClient;
    private int counter = 120;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        time_counter_text = findViewById(R.id.txt_timer);
        verify_button = findViewById(R.id.sign_in_btn);

        AWSMobileClient.getInstance().initialize(this, new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails result) {

            }

            @Override
            public void onError(Exception e) {

            }
        });

        AWSCredentialsProvider credentialsProvider = AWSMobileClient.getInstance();
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();


        // Add code to instantiate a AmazonDynamoDBClient
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);

        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(configuration)
                .build();

        FirebaseApp.initializeApp(this);




        //initialization
        mAuth = FirebaseAuth.getInstance();

        pinView = findViewById(R.id.pinView);

        String phone = getIntent().getStringExtra("phone");


        startCountDown();
        sendVerificationCode(phone);

        verify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = Objects.requireNonNull(pinView.getText()).toString();
                verifySignInCode(code);
            }
        });
    }

    //for verify code
    private void verifySignInCode(String code){
        Toast.makeText(this, code, Toast.LENGTH_SHORT).show();
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
            signInWithPhoneAuthCredential(credential); //call for check code
            Intent welcome_intent = new Intent(VerifyActivity.this, AwsListActivity.class);
            startActivity(welcome_intent);
            Toast.makeText(getApplicationContext(), "Verification Successful", Toast.LENGTH_LONG).show();
        } catch (Exception e){
            Toast toast = Toast.makeText(this, "Verification Code is wrong", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }





    }

    //for getting verification code
    private void sendVerificationCode(String number){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + number,        // Phone number to verify
                120,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallBacks);        // OnVerificationStateChangedCallbacks


    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                pinView.setText(code);
                verifySignInCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //here you can open new activity
                            Intent welcome_intent = new Intent(VerifyActivity.this, AwsListActivity.class);
                            startActivity(welcome_intent);


                            Toast.makeText(getApplicationContext(), "Verification Successful", Toast.LENGTH_LONG).show();
                            //textView.setText("SMS Verification Successful");
                            //textView.setTextColor(Color.GREEN);
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),
                                        "Incorrect Verification Code ", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });

    }



    public void startCountDown() {
        counter=120;
        //60_000=60 sec or 1 min and another is interval of count down is 1 sec
        new CountDownTimer(120_000, 1_000) {
            @Override
            public void onTick(long l) {

                time_counter_text.setText("Enter Code Within "+ counter + " Seconds");
                time_counter_text.setTextColor(Color.GREEN);
                counter--;

            }

            @Override
            public void onFinish() {

                time_counter_text.setText("Finish!! OTP Verification Times up!");
                time_counter_text.setTextColor(Color.RED);
            }
        }.start();
    }
}
