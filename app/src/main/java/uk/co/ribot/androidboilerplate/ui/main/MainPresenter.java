package uk.co.ribot.androidboilerplate.ui.main;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import uk.co.ribot.androidboilerplate.R;
import uk.co.ribot.androidboilerplate.data.DataManager;
import uk.co.ribot.androidboilerplate.data.model.Result;
import uk.co.ribot.androidboilerplate.injection.ConfigPersistent;
import uk.co.ribot.androidboilerplate.ui.base.BasePresenter;
import uk.co.ribot.androidboilerplate.util.RxUtil;

import static java.lang.String.valueOf;

/**
* Presenter class
* */
@ConfigPersistent
public class MainPresenter extends BasePresenter<MainMvpView> {

    private static final String TAG = "MainPresenterTAG_";
    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public MainPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(MainMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    /**
     * It asks Google API for the path between 2 coordinates in the walking mode and then orders the
     * view to paint the path in the map, if there is no map or an error happens; it tells the view
     * to show the corresponding dialog.
     * @param mode
     * @param origin
     * @param destination
     * @param sensor
     */
    public void loadPath(String mode, String origin, String destination, boolean sensor) {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.getPath(mode, origin, destination, sensor)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Result>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().errorPathFound();
                    }

                    @Override
                    public void onNext(Result result) {
                        if (result != null && !result.getRoutes().isEmpty() && !result.getRoutes().get(0).getLegs().isEmpty() &&
                                !result.getRoutes().get(0).getLegs().get(0).getSteps().isEmpty()) {
                            // pass routes to view
                            getMvpView().paintPath(result);
                        } else {
                            getMvpView().noPathFound();
                        }
                    }
                });
    }

    /**
     * If network and location are enabled it tells the view to add a marker and set the camera to
     * the current location.
     * If there is no network and/or location; it tells the view to ask the user to enable location
     * and/location.
     * @param netAvailable
     * @param locationEnabled
     * @param location
     */
    public void parkCar(boolean netAvailable, boolean locationEnabled, Location location) {
        if (netAvailable) {
            if (locationEnabled) {
                getMvpView().updateUIInitLocation();
                getMvpView().parkCar();
                getMvpView().changeParkFlag();
                getMvpView().changeBtnText();
            } else {
                getMvpView().showSettingsGPSAlert();
            }
        } else {
            getMvpView().showSettingsNetworkAlert();
        }
    }

    /**
     * If the activity was destroyed and the user had already set 2 markers; it calls another
     * presenter method to get and paint the path between 2 markers.
     * @param markerPoints
     */
    public void restorePath(ArrayList<LatLng> markerPoints) {
        if (markerPoints.size() >= 2) {
            StringBuilder originC = new StringBuilder(valueOf(markerPoints.get(0).latitude)).
                    append(",").append(valueOf(markerPoints.get(0).longitude));
            StringBuilder destC = new StringBuilder(valueOf(markerPoints.get(1).latitude)).
                    append(",").append(valueOf(markerPoints.get(1).longitude));
            // Loading path from google
            loadPath("walking", originC.toString(), destC.toString(), false);
        }
    }

    /**
     * It tells the view where to move the camera based on the input of the view.
     * @param mCameraPosition
     * @param mCurrentLocation
     */
    public void cameraLogic(boolean mCameraPosition, Location mCurrentLocation) {
        if (mCameraPosition) {
            getMvpView().moveCameraToSavedPosition();
        } else if (mCurrentLocation != null) {
            getMvpView().moveCameraToCurrentLocation();
        } else {
            getMvpView().moveCameraToDefault();
        }
    }

    /**
     * It tells the view to add a marker and gives the marker a title based on the view state.
     * @param latLng
     * @param nMarker
     */
    public void addMarker(LatLng latLng, int nMarker) {
        int title   = R.string.default_info_title;
        if (nMarker == 0) {
            title   = R.string.park_title;
        } else if (nMarker == 1) {
            title   = R.string.find_car_title;
        }
        getMvpView().addCurrentLocationMarker(latLng, title);
    }

    /**
     * If network and location are enabled it tells the view to update the map with the user current
     * location and show the button that shows the blue dot with the users current location.
     * If there is no network and/or location; it tells the view to ask the user to enable location
     * and/location.
     * @param netAvailable
     * @param locationEnabled
     */
    public void updateMapInterface(boolean isNetworkAvailable, boolean isLocationEnabled) {
        if (isNetworkAvailable) {
            if (isLocationEnabled) {
                getMvpView().updateUIInitLocation();
            } else {
                getMvpView().showSettingsGPSAlert();
            }
        } else {
            getMvpView().showSettingsNetworkAlert();
        }
    }

}