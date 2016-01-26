package net.wandroid.charge;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    /**
     * Default camera position. Is currently at The netherlands
     */
    public static final LatLng DEFAULT_LAT_LNG = new LatLng(52.3, 5.5);
    /**
     * Default zoom. Currently zoom in at the Netherlands
     */
    public static final float DEFAULT_ZOOM = 6f;
    /**
     * The json data. Normally this would be downloaded at runtime.
     */
    public static final String ASSETS_CHARGEPOINTS_JSON = "chargepoints.json";
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LAT_LNG, DEFAULT_ZOOM));
        loadMarksFromJson();
    }

    /**
     * Loads and convert the json data to markers on the map.
     * It will read from a file and should be runned in a background thread.
     */
    private void loadMarksFromJson() {
        InputStream is = null;
        try {
            is = getAssets().open(ASSETS_CHARGEPOINTS_JSON);
            Gson gson = new Gson();
            InputStreamReader reader = new InputStreamReader(is);
            ChargePoint[] chargePoints = gson.fromJson(reader, ChargePoint[].class);
            for (ChargePoint cp : chargePoints) {
                LatLng position = new LatLng(cp.lat, cp.lng);
                mMap.addMarker(new MarkerOptions().position(position).title(cp.address));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * ChargePoint class for json conversion.
     */
    public static class ChargePoint {
        private String city;
        private double lng;
        private double lat;
        private int id;
        private String address;
        private Connector[] connectors;

        //connectors
        public static class Connector {
            private int id;
            private String connectorType;
            private Power power;

            //power
            public static class Power {
                private String current;
                private int phase;
                private int voltage;
                private int amperage;
            }

        }
    }

}
