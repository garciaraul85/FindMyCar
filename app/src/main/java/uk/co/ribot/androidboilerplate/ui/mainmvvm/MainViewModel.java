package uk.co.ribot.androidboilerplate.ui.mainmvvm;

import android.arch.lifecycle.ViewModel;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import uk.co.ribot.androidboilerplate.data.DataManager;

public class MainViewModel extends ViewModel implements OnMapReadyCallback {

    private DataManager dataManager;

    // Google Map
    private GoogleMap mGoogleMap;

    public MainViewModel(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
    }


}