package nottheory.donationtracker.Controllers;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.Objects;

import nottheory.donationtracker.Model.Location;
import nottheory.donationtracker.Model.LocationCollection;
import nottheory.donationtracker.Model.LoginManager;
import nottheory.donationtracker.R;

/**
 * Controller link to activity_maps UI
 */
public class MapsActivity extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        FragmentManager thisFragmentManager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) thisFragmentManager
                .findFragmentById(R.id.map); //Most concise way to do this, does not disobey LoD
        try {
            Objects.requireNonNull(mapFragment).getMapAsync(this);
        } catch (Exception e) {}
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap;
        final LatLng ATLANTA_LATLNG = new LatLng(33.7490, -84.3880);
        final float ZOOM = 10.0f;
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        LocationCollection allLocations = LoginManager.getLocations();
        for(Location l: allLocations.getLocations()) {
            mMap.addMarker(l.makeMarkerOption());
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ATLANTA_LATLNG, ZOOM));
        mMap.setOnInfoWindowClickListener(this);



    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        marker.getTitle();
        Intent i = new Intent(MapsActivity.this, LocationInfoActivity.class);
        i.putExtra("location", marker.getTitle());
        startActivity(i);
    }

}
