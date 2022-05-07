package com.example.finaltracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class userProfile extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    FirebaseFirestore fStore;
    String user_name;
    private String uId;

    View homeImg,userImg;
    TextView username;
    Button signOut;
    Toolbar toolbar;
    GoogleSignInClient googleSignInClient;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        homeImg = findViewById(R.id.homeimg);
        userImg = findViewById(R.id.userimg);
        username = findViewById(R.id.username);
        signOut = findViewById(R.id.signOut);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        try{
            uId = auth.getCurrentUser().getUid();
        }catch (Exception e){
            System.out.println("error");}


        try{
            user_name = auth.getCurrentUser().getEmail();
            username.setText(user_name);
        }catch (Exception e){
            System.out.println("Error");
        }
//        try {
//            username.setText(firebaseUser.getEmail());
//        }catch (Exception e){
//            System.out.println("error");
//        }

        homeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userProfile.this,MainActivity.class);
                startActivity(intent);
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Disconnect_google();

                Intent intent = new Intent(userProfile.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
//        try {
//            getData();
//        }catch (Exception e){
//            System.out.println("Error");
//        }

    }
//    private void getData() {
//
//        DocumentReference documentReference = fStore.collection(uId).document("userData");
//        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
//
//                user_name = documentSnapshot.getString("user_name");
//                username.setText(user_name);
//
//
//            }
//        });
//    }



    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseUser == null) {
            startActivity(new Intent(userProfile.this, LoginActivity.class));
        }
        else if(firebaseUser!=null && !firebaseUser.isEmailVerified()){
            startActivity(new Intent(userProfile.this, LoginActivity.class));


        }
    }
    public void Disconnect_google() {
        try {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mGoogleApiClient.clearDefaultAccountAndReconnect().setResultCallback(new ResultCallback<Status>() {

                    @Override
                    public void onResult(Status status) {

                        mGoogleApiClient.disconnect();
                        Toast.makeText(getApplicationContext(),"Disconnected",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        } catch (Exception e) {
            Log.d("DISCONNECT ERROR", e.toString());
        }
    }
}