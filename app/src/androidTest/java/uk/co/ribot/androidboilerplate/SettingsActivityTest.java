package uk.co.ribot.androidboilerplate;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import uk.co.ribot.androidboilerplate.R;
import uk.co.ribot.androidboilerplate.test.common.TestComponentRule;
import uk.co.ribot.androidboilerplate.ui.settings.SettingsActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Created by Raul on 11/01/2017.
 */
@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest {

    public final TestComponentRule component =
            new TestComponentRule(InstrumentationRegistry.getTargetContext());
    public final ActivityTestRule<SettingsActivity> settings =
            new ActivityTestRule<SettingsActivity>(SettingsActivity.class, false, false) {
                @Override
                protected Intent getActivityIntent() {
                    // Override the default intent so we pass a false flag for syncing so it doesn't
                    // start a sync service in the background that would affect  the behaviour of
                    // this test.
                    return SettingsActivity.getStartIntent(
                            InstrumentationRegistry.getTargetContext());
                }
            };

    // TestComponentRule needs to go first to make sure the Dagger ApplicationTestComponent is set
    // in the Application before any Activity is launched.
    @Rule
    public final TestRule chain = RuleChain.outerRule(component).around(settings);

    @Test
    public void testEnterDataSaveSuccess() {
        settings.launchActivity(null);

        String hint = settings.getActivity().getString(R.string
                .hint_update_interval);

        onView(withId(R.id.input_layout_update_interval)).
                check(matches(withHint(hint)));

        onView(withId(R.id.input_update_interval))
        .perform(clearText(), typeText("1"));

        onView(withId(R.id.btn_register))
                .perform(click());

        onView(withText("Success")).
                inRoot(withDecorView(not(is(settings.getActivity().
                        getWindow().getDecorView())))).
                check(matches(isDisplayed()));
    }

    @Test
    public void testEnterDataSaveNoInput() {
        settings.launchActivity(null);

        String hint = settings.getActivity().getString(R.string
                .hint_update_interval);

        onView(withId(R.id.input_layout_update_interval)).
                check(matches(withHint(hint)));

        onView(withId(R.id.btn_register))
                .perform(click());

        String error = settings.getActivity().getString(R.string
                .error1_update_interval);

        onView(withText(error)).check(matches(isDisplayed()));
    }

    @Test
    public void testEnterDataSaveZeroInput() {
        settings.launchActivity(null);

        String hint = settings.getActivity().getString(R.string
                .hint_update_interval);

        onView(withId(R.id.input_layout_update_interval)).
                check(matches(withHint(hint)));

        onView(withId(R.id.input_update_interval))
                .perform(clearText(), typeText("0"));

        onView(withId(R.id.btn_register))
                .perform(click());

        String error = settings.getActivity().getString(R.string
                .error2_update_interval);

        onView(withText(error)).check(matches(isDisplayed()));
    }

    public static Matcher<View> withHint(final String expectedHint) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TextInputLayout)) {
                    return false;
                }

                String hint = ((TextInputLayout) view).getHint().toString();

                return expectedHint.equals(hint);
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }

}