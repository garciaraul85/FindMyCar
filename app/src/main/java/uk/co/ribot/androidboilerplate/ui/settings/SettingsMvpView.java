package uk.co.ribot.androidboilerplate.ui.settings;

import uk.co.ribot.androidboilerplate.ui.base.MvpView;

/**
 * Interface that allows the presenter to give orders to the view.
 */
public interface SettingsMvpView extends MvpView {
    void showLoader();
    void hideLoader();
    void showError();
    void showSuccess();
}