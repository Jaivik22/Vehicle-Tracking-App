package com.example.finaltracker;

import static android.widget.Toast.LENGTH_SHORT;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class trackActivity extends FragmentActivity implements OnMapReadyCallback {
    GoogleMap gMap;
    private TextView Longitude,Latitude;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    private String uId;
    private String latitude,longitude;
    private double doubleLat,doubleLong;

    Runnable runnable;
    Handler handler;
    Boolean isRunning = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        Longitude = findViewById(R.id.longitude);
        Latitude = findViewById(R.id.latitude);
//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        try {
            uId = fAuth.getCurrentUser().getUid();
        }catch (Exception e){
            System.out.println(e);
        }







//        Runnable runnable2 = new Runnable() {
//            @Override
//            public void run() {
//                Looper.prepare();
//                if(isRunning) {
                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            // this method will contain your almost-finished HTTP calls
                            handler.postDelayed(this, 5000);
                            getData();
                        }
                    }, 5000);

//                }
//                Looper.loop();
//            }
//        };
//        Thread thread2 = new Thread(runnable2);
//        thread2.start();


    }




    private void getData(){
//        Runnable runnable1 = new Runnable() {
//            @Override
//            public void run() {
                DocumentReference documentReference = fStore.collection(uId).document("location");
                documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                        latitude = documentSnapshot.getString("Latitude");
                        longitude = documentSnapshot.getString("Longitude");


                    }
                });

//            }
//        };
//        Thread thread1 = new Thread(runnable1);
//        thread1.start();
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
                Longitude.setText(longitude);
                Latitude.setText(latitude);
                Toast.makeText(trackActivity.this,"data updated", LENGTH_SHORT).show();
//            }
//        });

        SupportMapFragment mapFragment =  (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        try {
            doubleLat = Double.parseDouble(latitude);
            doubleLong = Double.parseDouble(longitude);
        }
        catch (Exception e){
            System.out.println(e);
        }




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        handler.removeCallbacksAndMessages(null);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
//        LatLng my_position = new LatLng(doubleLat, doubleLong);
        LatLng my_position = new LatLng(doubleLat, doubleLong);

        gMap.addMarker(new MarkerOptions().position(my_position).title("Position"));
        gMap.moveCamera(CameraUpdateFactory.newLatLng(my_position));

    }
}