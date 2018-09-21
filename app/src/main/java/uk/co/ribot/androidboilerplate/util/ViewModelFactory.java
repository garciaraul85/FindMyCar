package uk.co.ribot.androidboilerplate.util;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import uk.co.ribot.androidboilerplate.data.DataManager;
import uk.co.ribot.androidboilerplate.ui.mainmvvm.MainViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private DataManager dataManager;

    @Inject
    public ViewModelFactory(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainViewModel(dataManager);
//        if (modelClass == LoginViewModel.class)         return (T) new LoginViewModel(dataManager, eventBus);
//        else if (modelClass == DetailViewModel.class)   return (T) new DetailViewModel(dataManager, eventBus, gson);
//        else if (modelClass == VideoViewModel.class)    return (T) new VideoViewModel(dataManager, eventBus);
//        else return (T) new NullViewModel();
    }
}
