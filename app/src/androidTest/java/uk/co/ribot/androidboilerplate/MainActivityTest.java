package uk.co.ribot.androidboilerplate;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import uk.co.ribot.androidboilerplate.data.model.Result;
import uk.co.ribot.androidboilerplate.test.common.TestComponentRule;
import uk.co.ribot.androidboilerplate.test.common.TestDataFactory;
import uk.co.ribot.androidboilerplate.ui.main.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    public final TestComponentRule component =
            new TestComponentRule(InstrumentationRegistry.getTargetContext());
    public final ActivityTestRule<MainActivity> main =
            new ActivityTestRule<MainActivity>(MainActivity.class, false, false) {
                @Override
                protected Intent getActivityIntent() {
                    // Override the default intent so we pass a false flag for syncing so it doesn't
                    // start a sync service in the background that would affect  the behaviour of
                    // this test.
                    return MainActivity.getStartIntent(
                            InstrumentationRegistry.getTargetContext());
                }
            };

    // Location mocks
    public final String mockMode         = "walking";
    public final String mockOrigin       = "37.368695,-122.038270";
    public final String mock_Destination = "37.369082,-122.038246";
    public final boolean mockSensor      = false;

    // TestComponentRule needs to go first to make sure the Dagger ApplicationTestComponent is set
    // in the Application before any Activity is launched.
    @Rule
    public final TestRule chain = RuleChain.outerRule(component).around(main);

    @Before
    public void setUp() {
        MainActivity.activityMode = MainActivity.APP_MODE.UNIT_TEST_MODE;
    }

    @Test
    public void testHappyPath() {
        Result testDataRibots = TestDataFactory.getDirectionsPathFoundResponse();
        when(component.getMockDataManager().getPath(mockMode, mockOrigin, mock_Destination, mockSensor))
                .thenReturn(Observable.just(testDataRibots));

        MainActivity.isMockNetworkEnabled  = true;
        MainActivity.isMockLocationEnabled = true;

        main.launchActivity(null);

        onView(isRoot()).perform(waitFor(TimeUnit.SECONDS.toMillis(5)));

        onView(withId(R.id.locationBtnMain))
                .perform(click())
                .check(matches(withText(R.string.text_find_my_car)));

        onView(isRoot()).perform(waitFor(TimeUnit.SECONDS.toMillis(5)));

        onView(withId(R.id.locationBtnMain))
                .perform(click())
                .check(matches(withText(R.string.text_start_over)));

        onView(isRoot()).perform(waitFor(TimeUnit.SECONDS.toMillis(15)));
    }

    @Test
    public void testNoNetwork() {
        Result testDataRibots = TestDataFactory.getDirectionsPathFoundResponse();
        when(component.getMockDataManager().getPath(mockMode, mockOrigin, mock_Destination, mockSensor))
                .thenReturn(Observable.just(testDataRibots));

        MainActivity.isMockNetworkEnabled  = false;
        MainActivity.isMockLocationEnabled = true;

        main.launchActivity(null);

        onView(isRoot()).perform(waitFor(TimeUnit.SECONDS.toMillis(5)));

        onView(withText(R.string.network_title))
                .check(matches(withText(R.string.network_title)));

        onView(withText(R.string.button_cancel)).perform(click());

        MainActivity.isMockNetworkEnabled  = true;

        ///////////////////////////////////////////// Happy Path

        onView(withId(R.id.locationBtnMain))
                .perform(click())
                .check(matches(withText(R.string.text_find_my_car)));

        onView(isRoot()).perform(waitFor(TimeUnit.SECONDS.toMillis(5)));

        onView(withId(R.id.locationBtnMain))
                .perform(click())
                .check(matches(withText(R.string.text_start_over)));

        onView(isRoot()).perform(waitFor(TimeUnit.SECONDS.toMillis(15)));
    }

    @Test
    public void testNoLocation() {
        Result testDataRibots = TestDataFactory.getDirectionsPathFoundResponse();
        when(component.getMockDataManager().getPath(mockMode, mockOrigin, mock_Destination, mockSensor))
                .thenReturn(Observable.just(testDataRibots));

        MainActivity.isMockNetworkEnabled  = true;
        MainActivity.isMockLocationEnabled = false;

        main.launchActivity(null);

        onView(isRoot()).perform(waitFor(TimeUnit.SECONDS.toMillis(5)));

        onView(withText(R.string.location_title))
                .check(matches(withText(R.string.location_title)));

        onView(withText(R.string.button_cancel)).perform(click());

        MainActivity.isMockLocationEnabled  = true;

        ///////////////////////////////////////////// Happy Path

        onView(withId(R.id.locationBtnMain))
                .perform(click())
                .check(matches(withText(R.string.text_find_my_car)));

        onView(isRoot()).perform(waitFor(TimeUnit.SECONDS.toMillis(5)));

        onView(withId(R.id.locationBtnMain))
                .perform(click())
                .check(matches(withText(R.string.text_start_over)));

        onView(isRoot()).perform(waitFor(TimeUnit.SECONDS.toMillis(15)));
    }

    @Test
    public void testNoNetworkNoLocation() {
        Result testDataRibots = TestDataFactory.getDirectionsPathFoundResponse();
        when(component.getMockDataManager().getPath(mockMode, mockOrigin, mock_Destination, mockSensor))
                .thenReturn(Observable.just(testDataRibots));

        MainActivity.isMockNetworkEnabled  = false;
        MainActivity.isMockLocationEnabled = false;

        main.launchActivity(null);

        onView(isRoot()).perform(waitFor(TimeUnit.SECONDS.toMillis(5)));

        onView(withText(R.string.network_title))
                .check(matches(withText(R.string.network_title)));

        onView(withText(R.string.button_cancel)).perform(click());

        MainActivity.isMockNetworkEnabled  = true;

        onView(withId(R.id.locationBtnMain))
                .perform(click());

        onView(withText(R.string.location_title))
                .check(matches(withText(R.string.location_title)));

        onView(withText(R.string.button_cancel)).perform(click());

        MainActivity.isMockLocationEnabled  = true;

        ///////////////////////////////////////////// Happy Path

        onView(withId(R.id.locationBtnMain))
                .perform(click())
                .check(matches(withText(R.string.text_find_my_car)));

        onView(isRoot()).perform(waitFor(TimeUnit.SECONDS.toMillis(5)));

        onView(withId(R.id.locationBtnMain))
                .perform(click())
                .check(matches(withText(R.string.text_start_over)));

        onView(isRoot()).perform(waitFor(TimeUnit.SECONDS.toMillis(15)));
    }

    /**
     * Perform action of waiting for a specific time.
     */
    public ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, final View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }
}