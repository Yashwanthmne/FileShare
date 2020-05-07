package com.hotocorporation.demo.FileShare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    //declare object
    EditText editTextPhone;

    FirebaseAuth mAuth;
    Button btnGetVerficationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialization
        mAuth = FirebaseAuth.getInstance();
        editTextPhone = findViewById(R.id.editTextPhone);
        btnGetVerficationCode=findViewById(R.id.btn_get_verfication);

        if(mAuth.getCurrentUser() != null){
            //that means user is already logged in
            //so close this activity
            finish();

            //and open profile activity
            startActivity(new Intent(getApplicationContext(), AwsListActivity.class));
        }



        btnGetVerficationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = editTextPhone.getText().toString();

                if(phone.isEmpty()){
                    editTextPhone.setError("Phone number is required");
                    editTextPhone.requestFocus();
                    return;
                }

                if(phone.length() < 10 ){
                    editTextPhone.setError("Please enter a valid phone");
                    editTextPhone.requestFocus();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, VerifyActivity.class);
                intent.putExtra("phone", phone);
                startActivity(intent);
                finish();
            }
        });


    }



}