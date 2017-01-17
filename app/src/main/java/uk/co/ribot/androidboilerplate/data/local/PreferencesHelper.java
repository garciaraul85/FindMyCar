package uk.co.ribot.androidboilerplate.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import uk.co.ribot.androidboilerplate.injection.ApplicationContext;

@Singleton
public class PreferencesHelper {

    public static final String PREF_FILE_NAME = "android_boilerplate_pref_file";

    private final SharedPreferences mPref;

    @Inject
    public PreferencesHelper(@ApplicationContext Context context) {
        mPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public SharedPreferences getSharedPreferences() {
        return mPref;
    }

    public void saveStringPreference(String key, String value) {
        mPref.edit().putString(key, value);
    }

    public void saveIntPreference(String key, int value) {
        mPref.edit().putInt(key, value);
    }

    public void saveBooleanPreference(String key, boolean value) {
        mPref.edit().putBoolean(key, value);
    }

    public void saveFloatPreference(String key, float value) {
        mPref.edit().putFloat(key, value);
    }

    public void saveLongPreference(String key, long value) {
        mPref.edit().putLong(key, value);
    }

    public String gettSringPreference(String key, String value) {
        return mPref.getString(key, value);
    }

    public int getIntPreference(String key, int value) {
        return mPref.getInt(key, value);
    }

    public boolean getBooleanPreference(String key, boolean value) {
        return mPref.getBoolean(key, value);
    }

    public float getFloatPreference(String key, float value) {
        return mPref.getFloat(key, value);
    }

    public long getLongPreference(String key, long value) {
        return mPref.getLong(key, value);
    }

    public void clear() {
        mPref.edit().clear().apply();
    }

}
