package uk.co.ribot.androidboilerplate.ui.main;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import uk.co.ribot.androidboilerplate.data.model.Result;
import uk.co.ribot.androidboilerplate.ui.base.MvpView;

/**
 * Interface that allows the presenter to give orders to the view.
 */
public interface MainMvpView extends MvpView {

    void noPathFound();

    void errorPathFound();

    Location getLocation();

    void showSettingsGPSAlert();

    void showSettingsNetworkAlert();

    void showLocationNotAvailable();

    void parkCar();

    void changeParkFlag();

    void paintPath(Result result);

    void changeBtnText();

    void addCurrentLocationMarker(LatLng ltlng, int title);

    void moveCameraToSavedPosition();

    void moveCameraToCurrentLocation();

    void moveCameraToDefault();

    void initializeFragmentMap();

    void updateUIInitLocation();

    boolean isLocationEnabled();

    boolean isNetworkAvailable();
}