package com.yahi.food_nutrition;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseAuth;


public class RegisterActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText RegEmail, RegPwd;
    private Button RegBtn;
    private TextView RegQn;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        toolbar = findViewById(R.id.registertoolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registration");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        RegEmail = findViewById(R.id.registerEmail);
        RegPwd = findViewById(R.id.registerPasswd);
        RegBtn = findViewById(R.id.RegisterButton);
        RegQn = findViewById(R.id.registerPageQuestion);

        //mAuth = FirebaseAuth.getInstance();

        RegQn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        RegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = RegEmail.getText().toString().trim();
                String password = RegPwd.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    RegEmail.setError("email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    RegPwd.setError("password is required");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }

        });
    }
}