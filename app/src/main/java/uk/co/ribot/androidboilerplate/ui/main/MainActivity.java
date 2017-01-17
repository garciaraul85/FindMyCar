package uk.co.ribot.androidboilerplate.ui.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.ribot.androidboilerplate.R;
import uk.co.ribot.androidboilerplate.data.model.Leg;
import uk.co.ribot.androidboilerplate.data.model.Result;
import uk.co.ribot.androidboilerplate.data.model.Route;
import uk.co.ribot.androidboilerplate.data.model.Step;
import uk.co.ribot.androidboilerplate.ui.base.BaseActivity;
import uk.co.ribot.androidboilerplate.util.DialogFactory;
import uk.co.ribot.androidboilerplate.util.MapUtil;

import static java.lang.String.valueOf;

/**
 * Activity with the core functionality of the map.
 * It works as the view and communicates with the presenter.
 */
public class MainActivity extends BaseActivity implements
        OnMapReadyCallback, MainMvpView {

    private static final String TAG = MainActivity.class.getSimpleName();
    // Google Map
    private GoogleMap googleMap;
    private CameraPosition mCameraPosition;
    private static final int DEFAULT_ZOOM = 19;
    private final LatLng mDefaultLocation = new LatLng(37.369082, -122.038246);
    // The geographical location where the device is currently located.
    private Location mCurrentLocation;

    // Keys for storing activity state.
    private static final String KEY_MAP = "map_";
    private static final String KEY_PARK = "park";

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION        = "location";

    private Bundle savedInstanceState;

    private int park = 0; // 0. Park, 1. Find my car, 2. Start over

    @BindView(R.id.locationBtnMain)
    public Button locationBtn;

    private ArrayList<LatLng> markerPoints;

    @Inject MainPresenter mMainPresenter;

    private LocationManager locationManager;
    private boolean isGPSEnabled     = false;
    private boolean isNetworkEnabled = false;

    /**
     * Retrieve saved location.
     * Retrieve saved camera position.
     * Init the location manager to check if the location has been enabled.
     * Init map fragment.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        activityComponent().inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mMainPresenter.attachView(this);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition  = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Build the Play services client for use by the Fused LocationGPS Provider and the Places API.
        locationManager = (LocationManager) this
                .getSystemService(LOCATION_SERVICE);
        initializeFragmentMap();
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (googleMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, googleMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mCurrentLocation);
            int i = 1;
            for (LatLng latlng : markerPoints) {
                outState.putParcelable(KEY_MAP + i, latlng);
                i++;
            }
            outState.putInt(KEY_PARK, park);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Function to load map. If map is not created it will getLocationRoute it for you
     * */
    @Override
    public void initializeFragmentMap() {
        if (googleMap == null) {
            // Cannot be injected with ButterKnife because SupportFragment is not a view
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Called by the presenter to enable the map to track the user current location and to show the
     * button that zooms the map to the users current location.
     */
    @Override
    public void updateUIInitLocation() {
        if (googleMap != null) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    /**
     * Return an Intent to start this Activity.
     * Should only be set to false during testing.
     */
    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    /**
     * Init the markerpoints array.
     * Asks the presenter if the view should show the map controls or should it show the network and
     * location dialogs.
     * If there is data saved it asks the presenter if the view should restore the markers the
     * path between the markers, the current state and the button text.
     * It asks the presenter where should the camera be positioned.
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (markerPoints == null) {
            markerPoints = new ArrayList<LatLng>();
        }
        mMainPresenter.updateMapInterface(isNetworkAvailable(), isLocationEnabled());
        // Retrieve location and camera position from saved instance state.
        if (this.savedInstanceState != null) {
            markerPoints.clear();
            park = savedInstanceState.getInt(KEY_PARK);
            LatLng latLng1 = savedInstanceState.getParcelable(KEY_MAP + 1);
            LatLng latLng2 = savedInstanceState.getParcelable(KEY_MAP + 2);
            mMainPresenter.addMarker(latLng1, 1);

            mMainPresenter.addMarker(latLng2, 2);

            mMainPresenter.getMvpView().changeBtnText();
            // Checks, whether start and end locations are captured
            mMainPresenter.restorePath(this.markerPoints);

            if (activityMode == APP_MODE.UNIT_TEST_MODE) {
                if (mCurrentLocation != null) {
                    mCurrentLocation.setLatitude(mockDestinyCoordinates.latitude);
                    mCurrentLocation.setLongitude(mockDestinyCoordinates.longitude);
                }
            }

            mMainPresenter.cameraLogic((mCameraPosition != null), mCurrentLocation);
        }
        try {

            this.googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    mCurrentLocation = location;
                }
            });
        } catch (Exception e) {
            Log.e(TAG, getString(R.string.exception_on_map_ready), e);
        }
    }

    /**
     * Called by the presenter to show a dialog telling the user that an error happened when trying
     * to find a path between him and his car.
     */
    @Override
    public void noPathFound() {
        DialogFactory.createGenericErrorDialog(this, getString(R.string.error_path_location))
                .show();
    }

    /**
     * Called by the presenter to show a dialog telling the user that the app service couldnt
     * find a path between the 2 markers.
     */
    @Override
    public void errorPathFound() {
        DialogFactory.createGenericErrorDialog(this, getString(R.string.path_not_found))
                .show();
    }

    @Override
    public Location getLocation() {
        return null;
    }

    /**
     * Shows the location settings dialog; asking the user to enable location and
     * giving the the possibility to do so.
     */
    @Override
    public void showSettingsGPSAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle(R.string.location_title);

        // Setting Dialog Message
        alertDialog.setMessage(R.string.location_message);

        // On pressing Settings button
        alertDialog.setPositiveButton(R.string.button_settings, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    /**
     * Shows network settings dialog; asking the user to enable the network and
     * giving the the possibility to do so.
     */
    @Override
    public void showSettingsNetworkAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle(R.string.network_title);

        // Setting Dialog Message
        alertDialog.setMessage(R.string.network_message);

        // On pressing Settings button
        alertDialog.setPositiveButton(R.string.button_settings, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void showLocationNotAvailable() {
        DialogFactory.createGenericErrorDialog(this, getString(R.string.error_location_available))
                .show();
    }

    /**
     * Gets the current location and asks the presenter to tell him where to place the camera and to add a marker
     */
    @Override
    public void parkCar() {
        if (googleMap == null) {
            return;
        }

        LatLng mLocation = null;

        if (activityMode == APP_MODE.APPLICATION_MODE) {
            try {
                mCurrentLocation = googleMap.getMyLocation();
                mLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            } catch (Exception e) {
                mLocation = mDefaultLocation;
            }
        } else {
            if (park == 0) {
                mLocation = mockOriginCoordinates;
            } else {
                mLocation = mockDestinyCoordinates;
            }
        }

        /*
         * Set the map's camera position to the current location of the device.
         * If the previous state was saved, set the position to the saved state.
         * If the current location is unknown, use a default position and zoom value.
         */
        if (activityMode == APP_MODE.UNIT_TEST_MODE) {
            if (mCurrentLocation != null) {
                mCurrentLocation.setLatitude(mockDestinyCoordinates.latitude);
                mCurrentLocation.setLongitude(mockDestinyCoordinates.longitude);
            }
        }
        this.mMainPresenter.cameraLogic((mCameraPosition != null), mCurrentLocation);

        this.mMainPresenter.addMarker(mLocation, park);
    }

    /**
     * Adds a new marker to the map
     * @param latLng
     * @param title
     */
    @Override
    public void addCurrentLocationMarker(LatLng latLng, int title) {
        markerPoints.add(latLng);
        // getLocationRoute marker
        MarkerOptions marker = new MarkerOptions().
                position(latLng).
                title(getString(title));
        // adding marker
        if (googleMap != null) {
            googleMap.addMarker(marker);
        }
    }

    /**
     * Based on the current state it asks the presenter if it should show the network and location dialog,
     * then the presenter tells the view to update the button text and the status.
     * Finally it ask the presenter if it should load the path between the 2 markers.
     * @param view
     */
    public void doLocationX(View view) {
        if (park == 0 || park == 1) {
            boolean netEnabled      = isNetworkAvailable();
            boolean locationEnabled = isLocationEnabled();
            Location location       = getLocation();
            mMainPresenter.parkCar(netEnabled, locationEnabled, location);
        } else if (park == 2) {
            mMainPresenter.getMvpView().changeParkFlag();
            mMainPresenter.getMvpView().changeBtnText();
        }
        if (markerPoints.size() >= 2) {
            if (activityMode == APP_MODE.APPLICATION_MODE) {
                StringBuilder originC = new StringBuilder(valueOf(markerPoints.get(0).latitude)).
                        append(",").append(valueOf(markerPoints.get(0).longitude));
                StringBuilder destC = new StringBuilder(valueOf(markerPoints.get(1).latitude)).
                        append(",").append(valueOf(markerPoints.get(1).longitude));
                mMainPresenter.loadPath("walking", originC.toString(), destC.toString(), false);
            } else {
                StringBuilder origin      = new StringBuilder("37.368695,-122.038270");
                StringBuilder destination = new StringBuilder("37.369082,-122.038246");
                mMainPresenter.loadPath("walking", origin.toString(), destination.toString(), false);
            }
        }
    }

    /**
     * Changes the current state
     */
    @Override
    public void changeParkFlag() {
        if (park == 0) {
            park = 1;
        } else if (park == 1) {
            park = 2;
        } else if (park == 2) {
            park = 0;
        }
    }

    /**
     * Based on the result of the presenter it paints in the map a path between the 2 markers.
     * @param result
     */
    @Override
    public void paintPath(Result result) {
        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;

        /** Traversing all routes */
        for (Route r : result.getRoutes()) {
            List path = new ArrayList<HashMap<String, String>>();

            /** Traversing all legs */
            for (Leg l : r.getLegs()) {

                /** Traversing all steps */
                for (Step s : l.getSteps()) {
                    if (s.getPolyline() != null && s.getPolyline().getPoints() != null) {
                        String point = s.getPolyline().getPoints();
                        List<LatLng> list = MapUtil.decodePoly(point);

                        /** Traversing all points */
                        for (int i = 0; i < list.size(); i++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(i)).latitude));
                            hm.put("lng", Double.toString(((LatLng)list.get(i)).longitude));
                            path.add(hm);
                        }
                    }
                }
                routes.add(path);
            }
        }

        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;

        // Traversing through all the routes
        for (int i = 0; i < routes.size(); i++) {
            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = routes.get(i);

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String,String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                if (j == 0) {
                    lat = markerPoints.get(0).latitude;
                    lng = markerPoints.get(0).longitude;
                }
                if (j == (path.size() - 1)) {
                    lat = markerPoints.get(1).latitude;
                    lng = markerPoints.get(1).longitude;
                }
                LatLng position = new LatLng(lat, lng);
                points.add(position);
            }
            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            lineOptions.width(2);
            lineOptions.color(Color.RED);
        }
        // Drawing polyline in the Google Map for the i-th route
        googleMap.addPolyline(lineOptions);
    }

    /**
     * Called by the presenter to change the button text based on the state.
     */
    @Override
    public void changeBtnText() {
        if (markerPoints == null) {
            markerPoints = new ArrayList<LatLng>();
        }
        if (park == 0) {
            markerPoints.clear();
            if (googleMap != null) {
                googleMap.clear();
            }
            locationBtn.setText(R.string.text_park);
        } else if (park == 1) {
            locationBtn.setText(R.string.text_find_my_car);
        } else if (park == 2) {
            locationBtn.setText(R.string.text_start_over);
        }
    }

    /**
     * Called by the presenter to move the camera to the saved position.
     */
    @Override
    public void moveCameraToSavedPosition() {
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
    }

    /**
     * Called by the presenter to move the camera to the current position
     */
    @Override
    public void moveCameraToCurrentLocation() {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mCurrentLocation.getLatitude(),
                        mCurrentLocation.getLongitude()), DEFAULT_ZOOM));
    }

    /**
     * Called by the presenter to move the camera to a default position
     */
    @Override
    public void moveCameraToDefault() {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
    }

    /**
     * Function to check if the location is enabled
     * @return boolean location is enabled
     * */
    @Override
    public boolean isLocationEnabled() {
        if (activityMode == APP_MODE.APPLICATION_MODE) {
            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            isNetworkEnabled = (activeNetworkInfo != null && activeNetworkInfo.isConnected());
        } else {
            isGPSEnabled = isMockLocationEnabled;
        }
        return isGPSEnabled;
    }

    /**
     * Function to check if network is enabled
     * @return boolean is network enabled
     */
    @Override
    public boolean isNetworkAvailable() {
        if (activityMode == APP_MODE.APPLICATION_MODE) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            isNetworkEnabled = (activeNetworkInfo != null && activeNetworkInfo.isConnected());
        } else {
            isNetworkEnabled = isMockNetworkEnabled;
        }
        return isNetworkEnabled;
    }

}