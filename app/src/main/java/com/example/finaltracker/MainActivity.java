package com.example.finaltracker;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 1;
    Button locateBtn,trackBtn;
    View homeImg,userImg;
    LocationManager locationManager;
    private LocationListener locationListener;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseUser firebaseUser;
    String userId;
    String latitude, longitude;
    Toolbar toolbar;
    Boolean flageSwitch = false;
     Switch sw;
     boolean switchState = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTheme(R.style.NoActionBar);
        setContentView(R.layout.activity_main);
        setTitle(R.string.no_lebel);
//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayShowTitleEnabled(false);



        locateBtn = findViewById(R.id.locateBtn);
        trackBtn = findViewById(R.id.trackbtn);
        homeImg = findViewById(R.id.homeimg);
        userImg = findViewById(R.id.userimg);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        firebaseUser =fAuth.getCurrentUser();


        toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitleTextColor();
//        setSupportActionBar(toolbar);


        locateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseUser!=null) {
                    Intent intent = new Intent(MainActivity.this, locateActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this,"Please login",Toast.LENGTH_LONG).show();
                }

            }
        });
        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(MainActivity.this,userProfile.class);
                startActivity(intent);
            }
        });
        trackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser!=null) {
                    Intent intent = new Intent(MainActivity.this, trackActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this,"Please login",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.action_menu, menu);
        MenuItem itemswitch = menu.findItem(R.id.switch_action_bar);
        itemswitch.setActionView(R.layout.switch_layout);



        sw = (Switch) menu.findItem(R.id.switch_action_bar).getActionView().findViewById(R.id.switch2);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && firebaseUser!=null) {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    flageSwitch = true;
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        OnGPS();
                    }
//                    else if (firebaseUser==null){
//                        Toast.makeText(MainActivity.this,"Please login",Toast.LENGTH_LONG).show();
//                        sw.setChecked(false);
//                    }
                    else {
                        getLocation();
                    }
                }else if(isChecked && firebaseUser==null){
                    sw.setChecked(false);
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this,"Please login",Toast.LENGTH_LONG).show();
                }
            }
        });
        return true;
    }



    //////////                                            Locate activity's work here                        ////////


    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            getLocation();

                        }

                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        public void onProviderEnabled(String provider) {
                        }

                        public void onProviderDisabled(String provider) {
                        }
                    };
                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
                            } catch (SecurityException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (locationGPS != null) {
                        double lat = locationGPS.getLatitude();
                        double longi = locationGPS.getLongitude();
                        latitude = String.valueOf(lat);
                        longitude = String.valueOf(longi);
                    } else {
//                        Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
//                getLocation();
                    }



//                  textView.setText("Your Location: " + "\n" + "Latitude: " + latitude );
//                  textView2.setText("Your Location: " + "\n" + "Longitude: " + longitude);
                    userId = firebaseUser.getUid();
                    DocumentReference documentReference =fStore.collection(userId).document("location");
                    Map<String,Object> user = new HashMap<>();
                    user.put("Longitude",longitude);
                    user.put("Latitude",latitude);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG,"data is being stored");
                        }
                    });
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (flageSwitch){
            sw.setChecked(true);
        }

    }
}