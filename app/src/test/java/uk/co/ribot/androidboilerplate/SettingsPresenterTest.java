package uk.co.ribot.androidboilerplate;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import uk.co.ribot.androidboilerplate.data.DataManager;
import uk.co.ribot.androidboilerplate.data.model.Settings;
import uk.co.ribot.androidboilerplate.ui.settings.SettingsMvpView;
import uk.co.ribot.androidboilerplate.ui.settings.SettingsPresenter;
import uk.co.ribot.androidboilerplate.util.RxSchedulersOverrideRule;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SettingsPresenterTest {

    @Mock SettingsMvpView mMockSettingsMvpView;
    @Mock DataManager mMockDataManager;
    @Mock Settings settings;
    private SettingsPresenter mSettingsPresenter;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() {
        mSettingsPresenter = new SettingsPresenter(mMockDataManager);
    }

    @After
    public void tearDown() {
        mSettingsPresenter.detachView();
    }

    @Test
    public void testSaveSettingsSuccess() {
        int updateInterval = 1;
        settings.setUpdateInterval(updateInterval);
        mSettingsPresenter.saveSettings(settings);

        mMockDataManager.saveMaxIntervalPreference("update_interval", settings.getUpdateInterval());

        verify(mMockSettingsMvpView).showLoader();
        verify(mMockSettingsMvpView).hideLoader();
        verify(mMockSettingsMvpView).showSuccess();
        verify(mMockSettingsMvpView, never()).showError();
    }

    @Test
    public void testSaveSettingsError() {
        int updateInterval = 1;
        settings.setUpdateInterval(updateInterval);
        mSettingsPresenter.saveSettings(settings);

        mMockDataManager.saveMaxIntervalPreference("update_interval", settings.getUpdateInterval());

        verify(mMockSettingsMvpView).showLoader();
        verify(mMockSettingsMvpView).hideLoader();
        verify(mMockSettingsMvpView, never()).showSuccess();
        verify(mMockSettingsMvpView).showError();
    }

}