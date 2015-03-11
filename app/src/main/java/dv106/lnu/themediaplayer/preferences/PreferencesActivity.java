package dv106.lnu.themediaplayer.preferences;

import android.app.Activity;
import android.os.Bundle;

public class PreferencesActivity extends Activity {
    
	public final static String KEY_PREF_TXT_SIZE = "txtSize";
	public final static String KEY_PREF_TXT_COLOR = "txtColor";
	public final static String KEY_PREF_NOTIFICATION = "notificationEnabled";
	public final static String KEY_PREF_MUSIC_ANIMATION = "animationEnabled";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferencesFragment())
                .commit();
    }
}
