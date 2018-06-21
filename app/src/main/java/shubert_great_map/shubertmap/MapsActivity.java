package shubert_great_map.shubertmap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final int SHUBERT_DIALOG = 1;

    private GoogleMap mMap;

    private String coordinat_latit;
    private String coordinat_long;
    private String zone_name;
    private String map_link;

    private ShubertMapHelper mShubertHelper;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mShubertHelper = new ShubertMapHelper(this);

        try {
            mShubertHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            mDb = mShubertHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(50.45, 30.52);
        mMap.addMarker(new MarkerOptions().position(sydney).draggable(true).title("Start marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        whenMapLongClick();

    }

    public void whenMapLongClick() {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                zone_name = mShubertHelper.getShubertMapPosition(latLng.latitude, latLng.longitude, mDb);
                coordinat_latit = String.valueOf(latLng.latitude);
                coordinat_long = String.valueOf(latLng.longitude);
                showSubertDialog(zone_name);
            }
        });
    }

    private void showSubertDialog(String zone_name) {
        map_link = "http://www.etomesto.ru/shubert-map/";
        if (zone_name != "") {
            map_link += zone_name + "/";
            showDialog(SHUBERT_DIALOG);
        }
    }


    protected Dialog onCreateDialog(int id) {
        if (id == SHUBERT_DIALOG) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Карта Шуберта");
            adb.setMessage("Широта: " + coordinat_latit + "\n" + "\n" +
                    "Долгота: " + coordinat_long + "\n" + "\n" +
                    "Карта: " + map_link);

            return adb.create();
        }
        return super.onCreateDialog(id);
    }

    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        if (id == SHUBERT_DIALOG) {
            ((AlertDialog) dialog).setMessage("Широта: " + coordinat_latit + "\n" + "\n" +
                    "Долгота: " + coordinat_long + "\n" + "\n" +
                    "Карта: " + map_link);

        }
    }


}
