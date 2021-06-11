package com.yahi.food_nutrition;

import android.app.ProgressDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

    private String key = "";
    private String meal;
    private String barnd;
    private String calories;

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

                if (TextUtils.isEmpty(mMeal)) {
                    meal.setError("Meal Required");
                    return;
                }
                if (TextUtils.isEmpty(mBrand)) {
                    brand.setError("Brand required");
                    return;
                }
                if (TextUtils.isEmpty(mCalories)) {
                    calories.setError("Calories Required");
                    return;
                } else {
                    loader.setMessage("Adding your data");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    Model model = new Model(mMeal, mBrand, mCalories, id, date);

                    reference.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(HomeActivity.this, "Meal has been inserted", Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            } else {
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

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(reference,Model.class)
                .build();

        FirebaseRecyclerAdapter<Model, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Model, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Model model) {
                holder.setDate(model.getDate());
                holder.setMeal(model.getMeal());
                holder.setBrand(model.getBrand());
                holder.setCalories(model.getCalories());

                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        key = getRef(position).getKey();
                        meal = model.getMeal();
                        barnd = model.getBrand();
                        calories = model.getCalories();

                        updateMeal();
                    }
                });

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieved_layout,parent,false);
                return new MyViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View mview;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mview = itemView;
        }

        public void setMeal(String meal){
            TextView mealTextView = mview.findViewById(R.id.mealTv);
            mealTextView.setText(meal);
        }
        public void setBrand(String brand){
            TextView brandTextView = mview.findViewById(R.id.brandTv);
            brandTextView.setText(brand);
        }
        public void setCalories(String calories){
            TextView caloriesTextView = mview.findViewById(R.id.caloriesTv);
            caloriesTextView.setText(calories);
        }

        public void setDate(String date){
            TextView dateTextView = mview.findViewById(R.id.dateTv);
            dateTextView.setText(date);
        }
    }

    private  void updateMeal(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.update_data,null);
        myDialog.setView(view);

        AlertDialog dialog = myDialog.create();

        EditText mMeal = view.findViewById(R.id.mEditTextMeal);
        EditText mBrand = view.findViewById(R.id.mEditTextBrand);
        EditText mCalories = view.findViewById(R.id.mEditTextCalories);

        mMeal.setText(meal);
        mMeal.setSelection(meal.length());

        mBrand.setText(barnd);
        mBrand.setSelection(barnd.length());

        mCalories.setText(calories);
        mCalories.setSelection(calories.length());

        Button delButton = view.findViewById(R.id.btnDelete);
        Button updateButton = view.findViewById(R.id.updateBtn);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meal = mMeal.getText().toString().trim();
                barnd = mBrand.getText().toString().trim();
                calories = mCalories.getText().toString().trim();

                String date = DateFormat.getDateInstance().format(new Date());

                Model model = new Model(meal,barnd,calories,key,date);

                reference.child(key).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(HomeActivity.this, "Data has been updated Succesfully",Toast.LENGTH_SHORT).show();
                        }else {
                            String err = task.getException().toString();
                            Toast.makeText(HomeActivity.this, "update Faild"+err,Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog.dismiss();
            }
        });

        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(HomeActivity.this, "Meal Deleted Succesfully",Toast.LENGTH_SHORT).show();
                        }else {

                            String err = task.getException().toString();
                            Toast.makeText(HomeActivity.this, "Meal faild t Del"+err,Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog.dismiss();
            }
        });




        dialog.show();
    }
}
