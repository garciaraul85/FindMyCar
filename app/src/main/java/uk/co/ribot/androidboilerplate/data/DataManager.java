package uk.co.ribot.androidboilerplate.data;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import uk.co.ribot.androidboilerplate.data.local.DatabaseHelper;
import uk.co.ribot.androidboilerplate.data.local.PreferencesHelper;
import uk.co.ribot.androidboilerplate.data.model.Result;
import uk.co.ribot.androidboilerplate.data.model.Ribot;
import uk.co.ribot.androidboilerplate.data.remote.GoogleService;
import uk.co.ribot.androidboilerplate.data.remote.RibotsService;

@Singleton
public class DataManager {

    private RibotsService mRibotsService;
    private DatabaseHelper mDatabaseHelper;
    private PreferencesHelper mPreferencesHelper;
    private GoogleService mGoogleService;

    @Inject
    public DataManager(RibotsService ribotsService, PreferencesHelper preferencesHelper,
                       DatabaseHelper databaseHelper, GoogleService GoogleService) {
        mRibotsService = ribotsService;
        mPreferencesHelper = preferencesHelper;
        mDatabaseHelper = databaseHelper;
        mGoogleService = GoogleService;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public Observable<Ribot> syncRibots() {
        return null;/*mRibotsService.getPath()
                .concatMap(new Func1<List<Ribot>, Observable<Ribot>>() {
                    @Override
                    public Observable<Ribot> call(List<Ribot> ribots) {
                        return mDatabaseHelper.setRibots(ribots);
                    }
                });*/
    }

    /**
     * Saves the users preferences
     * @param key
     * @param value
     */
    public void saveMaxIntervalPreference(String key, int value) {
        mPreferencesHelper.saveIntPreference(key, value);
    }

    public Observable<Result> getPath() {
        return null;//mRibotsService.getPath();
    }

    /**
     * Calls Google API to retrieve the path between 2 coordinates.
     * @param mode
     * @param origin
     * @param destination
     * @param sensor
     * @return Observable with the result of the call.
     */
    public Observable<Result> getPath(String mode, String origin, String destination, boolean sensor) {
        return mRibotsService.getRibots(mode, origin, destination, sensor);
    }

}
