package activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.projects.shrungbhatt.employeemanagement.R;

import service.TrackerService;

import java.util.HashMap;

public class DisplayActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = DisplayActivity.class.getSimpleName();
    private static final String EXTRA_USER_NAME = "user_name";
    private static final int PERMISSIONS_REQUEST = 1;
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private GoogleMap mMap;
    private String mUsername;

    public static Intent newIntent(Context context, String userName) {
        Intent intent = new Intent(context, DisplayActivity.class);
        intent.putExtra(EXTRA_USER_NAME, userName);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        mUsername = getIntent().getStringExtra(EXTRA_USER_NAME);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // Check GPS is enabled
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        assert lm != null;
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Check location permission is granted - if it is, start
        // the service, otherwise request the permission
//        int permission = ContextCompat.checkSelfPermission(this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION);
//        if (permission == PackageManager.PERMISSION_GRANTED) {
//            startTrackerService();
//        } else {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    PERMISSIONS_REQUEST);
//        }
    }

    private void startTrackerService() {
//        startService(TrackerService.newIntent(this, mUsername));
    }


    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Start the service when the permission is granted
            startTrackerService();
        }else {
            finish();
        }
    }*/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Authenticate with Firebase when the Google map is loaded
        mMap = googleMap;
        mMap.setMaxZoomPreference(18);
        mMap.setMinZoomPreference(6);
        subscribeToUpdates();

    }


    private void subscribeToUpdates() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_path));
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setMarker(DataSnapshot dataSnapshot) {
        // When a location update is received, put or update
        // its value in mMarkers, which contains all the markers
        // for locations received, so that we can build the
        // boundaries required to show them all on the map at once
        String key = dataSnapshot.getKey();
        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
        double lat = Double.parseDouble(value.get("latitude").toString());
        double lng = Double.parseDouble(value.get("longitude").toString());
        LatLng location = new LatLng(lat, lng);
//        if (!mMarkers.containsKey(key)) {
//            mMarkers.put(key, mMap.addMarker(new MarkerOptions().title(key).position(location)));
//            mMarkers.get(key).showInfoWindow();
//        } else {
//            mMarkers.get(key).setPosition(location);
//            mMarkers.get(key).showInfoWindow();
//        }
        if (key.equalsIgnoreCase(mUsername)) {
            if (!mMarkers.containsKey(mUsername)) {
                mMarkers.put(mUsername, mMap.addMarker(new MarkerOptions().title(key).position(location)));
                mMarkers.get(mUsername).showInfoWindow();
            } else {
                mMarkers.get(mUsername).setPosition(location);
                mMarkers.get(mUsername).showInfoWindow();
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            builder.include(mMarkers.get(mUsername).getPosition());
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));
        }

    }


}

