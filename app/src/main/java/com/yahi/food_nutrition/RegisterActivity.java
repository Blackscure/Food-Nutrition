package com.yahi.food_nutrition;

import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class RegisterActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText RegEmail, RegPwd;
    private Button RegBtn;
    private TextView RegQn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        toolbar = findViewById(R.id.registertoolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registration");


        RegEmail = findViewById(R.id.registerEmail);
        RegPwd = findViewById(R.id.registerPasswd);
        RegBtn = findViewById(R.id.RegisterButton);
        RegQn = findViewById(R.id.registerPageQuestion);

        RegQn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}