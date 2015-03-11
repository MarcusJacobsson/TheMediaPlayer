package dv106.lnu.themediaplayer.preferences;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import dv106.lnu.themediaplayer.R;
import dv106.lnu.themediaplayer.widgetprovider.SongAppWidgetProvider;

public class PreferencesFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		ListPreference txtColor = (ListPreference) findPreference("txtColor");
		txtColor.setSummary(txtColor.getEntry());

		ListPreference txtSize = (ListPreference) findPreference("txtSize");
		txtSize.setSummary(txtSize.getEntry());

		CheckBoxPreference notification = (CheckBoxPreference) findPreference("notificationEnabled");
		notification.setSummary(getResources().getString(
				R.string.preferences_notification_preferences_warning));

		CheckBoxPreference musicAnimation = (CheckBoxPreference) findPreference("animationEnabled");
		musicAnimation.setSummary(getResources().getString(
				R.string.music_animation_preference_summary));

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		/*
		 * Update the preference summary when you set a new preference
		 */
		Preference pref = findPreference(key);

		if (pref instanceof ListPreference) {
			ListPreference listPref = (ListPreference) pref;
			pref.setSummary(listPref.getEntry());
		}

		if (key.equals("txtColor")) {

            RemoteViews widgetView = new RemoteViews(getActivity().getPackageName(),
                    R.layout.appwidget_homescreen);
            AppWidgetManager appWidgetManager = AppWidgetManager
                    .getInstance(getActivity());
            SharedPreferences sharedPref = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            String txtColorString = sharedPref.getString(
                    PreferencesActivity.KEY_PREF_TXT_COLOR, "");
            int txtColor = Integer.parseInt(txtColorString);

            Bitmap b = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            c.drawColor(txtColor);

            widgetView.setTextColor(R.id.tvAppWidgetArtist, txtColor);
            widgetView.setTextColor(R.id.tvAppWidgetTitle, txtColor);
            widgetView.setImageViewBitmap(R.id.ivAppWidgetHS, b);
            widgetView.setImageViewBitmap(R.id.ivAppWidgetVS, b);

            appWidgetManager.updateAppWidget(new ComponentName(getActivity(),
                    SongAppWidgetProvider.class), widgetView);


		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceManager().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		getPreferenceManager().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}
}
