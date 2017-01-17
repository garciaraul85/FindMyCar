package uk.co.ribot.androidboilerplate;

import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import rx.Observable;
import uk.co.ribot.androidboilerplate.data.DataManager;
import uk.co.ribot.androidboilerplate.data.model.Result;
import uk.co.ribot.androidboilerplate.test.common.TestDataFactory;
import uk.co.ribot.androidboilerplate.ui.main.MainMvpView;
import uk.co.ribot.androidboilerplate.ui.main.MainPresenter;
import uk.co.ribot.androidboilerplate.util.RxSchedulersOverrideRule;

import static java.lang.String.valueOf;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {

    @Mock MainMvpView mMockMainMvpView;
    @Mock DataManager mMockDataManager;
    @Mock Location location;
    @Mock Bundle savedState;
    @Mock Location mCurrentLocation;
    private MainPresenter mMainPresenter;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() {
        mMainPresenter = new MainPresenter(mMockDataManager);
        mMainPresenter.attachView(mMockMainMvpView);
    }

    @After
    public void tearDown() {
        mMainPresenter.detachView();
    }

    // Path service
    @Test
    public void testLoadPathSuccess() {
        String mode        = "walking";
        String origin      = "36.235412,-99.667969";
        String destination = "36.235182,-99.667887";
        boolean sensor     = false;
        Result result      = TestDataFactory.getDirectionsPathFoundResponse();
        when(mMockDataManager.getPath(mode, origin, destination, sensor))
                .thenReturn(Observable.just(result));

        mMainPresenter.loadPath(mode, origin, destination, sensor);

        verify(mMockMainMvpView).paintPath(result);
        verify(mMockMainMvpView, never()).noPathFound();
        verify(mMockMainMvpView, never()).errorPathFound();
    }

    @Test
    public void testLoadPathNoPath() {
        String mode        = "walking";
        String origin      = "36.235412,-99.667969";
        String destination = "36.235182,-99.667887";
        boolean sensor     = false;
        Result result      = null;
        when(mMockDataManager.getPath(mode, origin, destination, sensor))
                .thenReturn(Observable.just(result));

        mMainPresenter.loadPath(mode, origin, destination, sensor);

        verify(mMockMainMvpView).noPathFound();
        verify(mMockMainMvpView, never()).paintPath(result);
        verify(mMockMainMvpView, never()).errorPathFound();
    }

    @Test
    public void testLoadErrorPath() {
        String mode        = "walking";
        String origin      = "36.235412,-99.667969";
        String destination = "36.235182,-99.667887";
        boolean sensor     = false;
        when(mMockDataManager.getPath(mode, origin, destination, sensor))
                .thenReturn(Observable.<Result>error(new RuntimeException()));

        mMainPresenter.loadPath(mode, origin, destination, sensor);
        verify(mMockMainMvpView).errorPathFound();
        verify(mMockMainMvpView, never()).noPathFound();
        verify(mMockMainMvpView, never()).paintPath(any(Result.class));
    }

    @Test
    public void testRestorePath() {
        ArrayList<LatLng> markerPoints = new ArrayList<LatLng>();
        markerPoints.add(new LatLng(36.235412, -99.667969));
        markerPoints.add(new LatLng(36.235182, -99.667887));
        StringBuilder origin = new StringBuilder(valueOf(markerPoints.get(0).latitude)).
                append(",").append(valueOf(markerPoints.get(0).longitude));
        StringBuilder destination = new StringBuilder(valueOf(markerPoints.get(1).latitude)).
                append(",").append(valueOf(markerPoints.get(1).longitude));

        String mode        = "walking";
        boolean sensor     = false;
        Result result      = TestDataFactory.getDirectionsPathFoundResponse();
        when(mMockDataManager.getPath(mode, origin.toString(), destination.toString(), sensor))
                .thenReturn(Observable.just(result));

        mMainPresenter.loadPath(mode, origin.toString(), destination.toString(), sensor);

        verify(mMockMainMvpView).paintPath(result);
        verify(mMockMainMvpView, never()).noPathFound();
        verify(mMockMainMvpView, never()).errorPathFound();

    }


    // Park car
    @Test
    public void testParkCarSuccess() {
        boolean netAvailable = true;
        boolean locationEnabled = true;
        mMainPresenter.parkCar(netAvailable, locationEnabled, location);
        verify(mMockMainMvpView).parkCar();
        verify(mMockMainMvpView).changeParkFlag();
        verify(mMockMainMvpView).changeBtnText();
        verify(mMockMainMvpView, never()).showSettingsNetworkAlert();
        verify(mMockMainMvpView, never()).showSettingsGPSAlert();
        verify(mMockMainMvpView, never()).showLocationNotAvailable();
    }

    @Test
    public void testParkCarNoInternet() {
        boolean netAvailable = false;
        boolean locationEnabled = false;
        mMainPresenter.parkCar(netAvailable, locationEnabled, location);
        verify(mMockMainMvpView).showSettingsNetworkAlert();
        verify(mMockMainMvpView, never()).parkCar();
        verify(mMockMainMvpView, never()).changeParkFlag();
        verify(mMockMainMvpView, never()).changeBtnText();
        verify(mMockMainMvpView, never()).showSettingsGPSAlert();
        verify(mMockMainMvpView, never()).showLocationNotAvailable();
    }

    @Test
    public void testParkCarNoLocation() {
        boolean netAvailable = true;
        boolean locationEnabled = false;
        mMainPresenter.parkCar(netAvailable, locationEnabled, location);
        verify(mMockMainMvpView).showSettingsGPSAlert();
        verify(mMockMainMvpView, never()).showSettingsNetworkAlert();
        verify(mMockMainMvpView, never()).parkCar();
        verify(mMockMainMvpView, never()).changeParkFlag();
        verify(mMockMainMvpView, never()).changeBtnText();
        verify(mMockMainMvpView, never()).showLocationNotAvailable();
    }

    // cameraLogic
    @Test
    public void testCameraLogicToSavedPosition() {
        boolean mCameraPosition = true;
        Assert.assertNotNull(mCameraPosition);
        mMainPresenter.cameraLogic(mCameraPosition, mCurrentLocation);
        verify(mMockMainMvpView).moveCameraToSavedPosition();
    }

    @Test
    public void testCameraLogicToCurrentPosition() {
        boolean mCameraPosition = false;
        Assert.assertNotNull(mCurrentLocation);
        mMainPresenter.cameraLogic(mCameraPosition, mCurrentLocation);
        verify(mMockMainMvpView).moveCameraToCurrentLocation();
    }

    @Test
    public void testCameraLogicToDefaultPosition() {
        boolean mCameraPosition = false;
        Location noCurrentLocation = null;
        mMainPresenter.cameraLogic(mCameraPosition, noCurrentLocation);
        verify(mMockMainMvpView).moveCameraToDefault();
    }

    // addMarker
    @Test
    public void testAddMarkerCarLocation() {
        int flag = 0;
        double lat = 11;
        double lon = 11;
        LatLng latLng = new LatLng(lat, lon);
        mMainPresenter.addMarker(latLng, flag);
        int title = R.string.park_title;
        verify(mMockMainMvpView).addCurrentLocationMarker(latLng, title);
    }

    @Test
    public void testAddMarkerMyLocation() {
        int flag = 1;
        double lat = 12;
        double lon = 12;
        LatLng latLng = new LatLng(lat, lon);
        mMainPresenter.addMarker(latLng, flag);
        int title = R.string.find_car_title;
        verify(mMockMainMvpView).addCurrentLocationMarker(latLng, title);
    }

    // Update interface
    @Test
    public void testUpdateInterfaceNoNetwork() {
        boolean isNetworkAvailable = false;
        boolean isLocationEnabled  = false;
        mMainPresenter.updateMapInterface(isNetworkAvailable, isLocationEnabled);
        verify(mMockMainMvpView).showSettingsNetworkAlert();
        verify(mMockMainMvpView, never()).showSettingsGPSAlert();
        verify(mMockMainMvpView, never()).updateUIInitLocation();
    }

    @Test
    public void testUpdateInterfaceNoLocation() {
        boolean isNetworkAvailable = true;
        boolean isLocationEnabled  = false;
        mMainPresenter.updateMapInterface(isNetworkAvailable, isLocationEnabled);
        verify(mMockMainMvpView, never()).showSettingsNetworkAlert();
        verify(mMockMainMvpView).showSettingsGPSAlert();
        verify(mMockMainMvpView, never()).updateUIInitLocation();
    }

    @Test
    public void testUpdateInterfaceSuccess() {
        boolean isNetworkAvailable = true;
        boolean isLocationEnabled  = true;
        mMainPresenter.updateMapInterface(isNetworkAvailable, isLocationEnabled);
        verify(mMockMainMvpView, never()).showSettingsNetworkAlert();
        verify(mMockMainMvpView, never()).showSettingsGPSAlert();
        verify(mMockMainMvpView).updateUIInitLocation();
    }

}