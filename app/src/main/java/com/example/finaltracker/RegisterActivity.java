package com.example.finaltracker;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText userName;
    private EditText email,errorEmail;
    private  EditText password;
    private Button register;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    Toolbar toolbar;
    FirebaseFirestore fStore;
    String userId;
    FirebaseUser firebaseUser;
    private String txt_userName;
    private ImageView passVisible;
    private TextView showError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userName = findViewById(R.id.userName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        progressBar = findViewById(R.id.progressBar2);
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        passVisible = findViewById(R.id.passVisible);
        toolbar = findViewById(R.id.toolbar);
        errorEmail = findViewById(R.id.errorEmail);
        showError = findViewById(R.id.error);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                txt_userName = userName.getText().toString();
                String txt_email =email.getText().toString();
                String txt_password = password.getText().toString();

                if(TextUtils.isEmpty(txt_userName) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                    Toast.makeText(RegisterActivity.this,"Empty credentials",Toast.LENGTH_SHORT).show();
                }else if(txt_password.length()<6){
                    Toast.makeText(RegisterActivity.this,"Password is too Short",Toast.LENGTH_SHORT).show();
                }else {
                    registerUser(txt_userName,txt_email,txt_password);
                }
            }
        });
        passVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowHidePass();
            }
        });

    }
    private  void registerUser(String userName,String email,String password){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    firebaseUser = auth.getCurrentUser();

                    firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(RegisterActivity.this,"verification complete",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            errorEmail.setText("Email does not exist");
                            return;
                        }
                    });
                    if(firebaseUser.isEmailVerified()) {

                        Toast.makeText(RegisterActivity.this, "Registration Successfull", Toast.LENGTH_LONG).show();

                        userId = firebaseUser.getUid();
                        DocumentReference documentReference = fStore.collection(userId).document("userData");
                        Map<String, Object> user = new HashMap<>();
                        user.put("user_name", txt_userName);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "data is being stored");
                            }
                        });

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }else{
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegisterActivity.this,"Please verfiy your email",Toast.LENGTH_LONG).show();
                        auth.signOut();
                    }

                }
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                progressBar.setVisibility(View.INVISIBLE);
                showError.setText("email is used or already exists");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                showError.setText("email is used or already exists");

            }
        });




    }
    public void ShowHidePass() {

//        if(view.getId()==R.id.passVisible){
        if(password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
            passVisible.setImageResource(R.drawable.invisible_eye);
            //Show Password
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        else{
            passVisible.setImageResource(R.drawable.visible_eye);
            //Hide Password
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
//        }
    }
}