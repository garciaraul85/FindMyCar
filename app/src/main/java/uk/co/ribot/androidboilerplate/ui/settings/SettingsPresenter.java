package uk.co.ribot.androidboilerplate.ui.settings;

import javax.inject.Inject;

import uk.co.ribot.androidboilerplate.data.DataManager;
import uk.co.ribot.androidboilerplate.data.model.Settings;
import uk.co.ribot.androidboilerplate.ui.base.BasePresenter;

/**
* Presenter class
* */
public class SettingsPresenter extends BasePresenter<SettingsMvpView> {
    private final DataManager mDataManager;

    @Inject
    public SettingsPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(SettingsMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    /**
     * Saves the user preferences.
     * @param settings
     */
    public void saveSettings(Settings settings) {

    }

}