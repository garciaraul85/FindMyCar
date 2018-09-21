package uk.co.ribot.androidboilerplate.ui.mainmvvm;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import javax.inject.Inject;

import timber.log.Timber;
import uk.co.ribot.androidboilerplate.BoilerplateApplication;
import uk.co.ribot.androidboilerplate.R;
import uk.co.ribot.androidboilerplate.injection.component.ActivityComponent;
import uk.co.ribot.androidboilerplate.injection.component.ConfigPersistentComponent;
import uk.co.ribot.androidboilerplate.injection.component.DaggerConfigPersistentComponent;
import uk.co.ribot.androidboilerplate.injection.module.ActivityModule;
import uk.co.ribot.androidboilerplate.util.ViewModelFactory;

public class MainActivityMVVM extends AppCompatActivity {

    @Inject
    ViewModelFactory factory;
    MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mvvm);
        inject();
        Timber.d("test");
    }

    private void inject() {
        ConfigPersistentComponent configPersistentComponent = DaggerConfigPersistentComponent.builder()
                .applicationComponent(BoilerplateApplication.get(this).getComponent())
                .build();
        ActivityComponent activityComponent = configPersistentComponent.activityComponent(new ActivityModule(this));
        activityComponent.inject(this);

        mainViewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);
    }

    public void doLocationX(View view) {
    }
}