package uk.co.ribot.androidboilerplate.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import uk.co.ribot.androidboilerplate.R;
import uk.co.ribot.androidboilerplate.ui.base.BaseActivity;

public class SettingsActivity extends BaseActivity implements SettingsMvpView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    /**
     * Return an Intent to start this Activity.
     * triggerDataSyncOnCreate allows disabling the background sync service onCreate. Should
     * only be set to false during testing.
     */
    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        return intent;
    }

    /**
     * Shows the loader for when the user saves.
     */
    @Override
    public void showLoader() {

    }

    /**
     * Hides the loader after an operation no matter the outcome.
     */
    @Override
    public void hideLoader() {

    }

    /**
     * Shows an error window if an error happened.
     */
    @Override
    public void showError() {

    }

    /**
     * Shows a success window.
     */
    @Override
    public void showSuccess() {

    }
}