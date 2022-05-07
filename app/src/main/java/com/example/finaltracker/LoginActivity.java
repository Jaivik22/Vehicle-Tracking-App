package com.example.finaltracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.utils.widget.ImageFilterButton;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText email;
    private  EditText password;
    private Button login;
    private Button signup;
    private ProgressBar progressBar;
    private ImageView passVisible;
    View homeImg,userImg;
    public String userId;
    Toolbar toolbar;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private ImageFilterButton googleSignin;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInApi googleSignInApi;

    private static final int RC_GOOGLE_SIGN_IN = 500;
    private String user_email;
    private TextView showError,resetPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
        progressBar = findViewById(R.id.progressBar);
        homeImg = findViewById(R.id.homeimg);
        userImg = findViewById(R.id.userimg);
        auth= FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.toolbar);
        showError = findViewById(R.id.error);
        passVisible = findViewById(R.id.passVisible);
        resetPass = findViewById(R.id.resetPass);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        password.setTransformationMethod(null);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        homeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String txt_email = email.getText().toString().trim();
                String txt_password = password.getText().toString().trim();
                loginUser(txt_email,txt_password);
            }
        });
        passVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowHidePass();
            }
        });
        createRequest();
        googleSignin = findViewById(R.id.sign_in_button);
        googleSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        try{
            user_email = auth.getCurrentUser().getEmail();

        }catch (Exception e){
            System.out.println("Error");
        }
        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText resetEmail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password?");
                passwordResetDialog.setMessage("Enter your email to received reset link.");
                passwordResetDialog.setView(resetEmail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            String mail = resetEmail.getText().toString();
                            auth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(LoginActivity.this,"Reset link sent to your email",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this,"Error!,reset link is not sent"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passwordResetDialog.create().show();

            }
        });

    }
//    private void loginUser(String email, String password){
//        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//            @Override
//            public void onSuccess(AuthResult authResult) {
//                Toast.makeText(LoginActivity.this,"login successfull",Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(LoginActivity.this,userProfile.class);
//                startActivity(intent);
////                userId = auth.getCurrentUser().getUid();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(LoginActivity.this,"try again",Toast.LENGTH_LONG).show();
//
//
//            }
//        });
//    }
    private void loginUser(String email,String password){
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if(auth.getCurrentUser().isEmailVerified()) {
                        Toast.makeText(LoginActivity.this, "login successfull", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, userProfile.class);
                        startActivity(intent);
                    }
                    else{

                        progressBar.setVisibility(View.INVISIBLE);
//                        Toast.makeText(LoginActivity.this, "Please varify your email", Toast.LENGTH_LONG).show();
                        showError.setText("Please verify your email");
                        auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(LoginActivity.this,"email is sent please verify",Toast.LENGTH_LONG).show();
                            }
                        });
                        auth.signOut();
                    }
                }
//                else if(auth.getCurrentUser().is)
                else {
                    progressBar.setVisibility(View.INVISIBLE);
                    showError.setText("incorrect id or password");
                    Toast.makeText(LoginActivity.this,"error",Toast.LENGTH_SHORT).show();
                    System.out.println("Error"+task.getException().getMessage());
                }
            }
        });
    }
    private void createRequest(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);

    }
    private void signIn() {

//        if (mGoogleApiClient.isConnected()) {
//            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
//            mGoogleApiClient.disconnect();
//            mGoogleApiClient.connect();
//        }

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = auth.getCurrentUser();
                            Intent intent = new Intent(LoginActivity.this,userProfile.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
//                            Snackbar.make(findViewById(R.id.main_layout,"Authentication failed",Snackbar.LENGTH_SHORT)).show();
                            Toast.makeText(LoginActivity.this,"failed",Toast.LENGTH_SHORT).show();
                        }
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