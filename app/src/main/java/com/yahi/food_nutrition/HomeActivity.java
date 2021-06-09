package com.yahi.food_nutrition;

import android.app.ProgressDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;


    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserId;
    private ProgressDialog loader;

    public HomeActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.hometoolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Meal App");

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager  linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loader = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserId = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("meals").child(onlineUserId);

        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMeal();
            }
        });
    }

    private void addMeal() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View myView = inflater.inflate(R.layout.input_file,null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);
       // dialog.show();

        final EditText meal = myView.findViewById(R.id.meal);
        final EditText brand = myView.findViewById(R.id.brand);
        final EditText calories = myView.findViewById(R.id.calories);
        Button save = myView.findViewById(R.id.saveBtn);
        Button cancel = myView.findViewById(R.id.cancelBtn);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mMeal = meal.getText().toString().trim();
                String mBrand = brand.getText().toString().trim();
                String mCalories = calories.getText().toString().trim();
                String id = reference.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());

                if (TextUtils.isEmpty(mMeal)){
                    meal.setError("Meal Required");
                    return;
                }
                if (TextUtils.isEmpty(mBrand)){
                    brand.setError("Brand required");
                    return;
                }
                if (TextUtils.isEmpty(mCalories)){
                    calories.setError("Calories Required");
                    return;
                }else {
                    loader.setMessage("Adding your data");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    Model model = new  Model(mMeal,mBrand,mCalories,id,date);

                   reference.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(HomeActivity.this, "Meal has been inserted", Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }else {
                                String error = task.getException().toString();
                                Toast.makeText(HomeActivity.this, "Failed" + error, Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }
                        }
                    });
                }
                dialog.dismiss();
            }

        });
        dialog.show();
    }
}