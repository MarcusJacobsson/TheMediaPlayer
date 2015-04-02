package dv106.lnu.themediaplayer.listadapters;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import dv106.lnu.themediaplayer.R;
import dv106.lnu.themediaplayer.database.DataSource;
import dv106.lnu.themediaplayer.preferences.PreferencesActivity;

public class PlaylistListAdapter extends ArrayAdapter<String> {

	private ArrayList<String> allPlaylists;
	private DataSource datasource;
	
	public PlaylistListAdapter(Context c, ArrayList<String> playlists){
		super(c, R.layout.playlist_list_item, playlists);
		this.allPlaylists = playlists;
		datasource = new DataSource(c);
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		/**
		 * Method re-used from the lecture slides to save battery
		 */
		View row;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			row = inflater.inflate(R.layout.playlist_list_item, null);
		} else {
			row = convertView;
		}
		
		String name = allPlaylists.get(position);
		String c = null;
		try{
			datasource.open();
			c = String.valueOf(datasource.getPlaylistCount(name));
			datasource.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		String count = null;
		if(c != null && !c.equals("1")){
			count = c.concat(" " + getContext().getResources().getString(R.string.playlist_list_adapter_tracks));
		}else if(c != null){
			count = c.concat(" " + getContext().getResources().getString(R.string.playlist_list_adapter_track));
		}
		
		TextView tvPlaylistName = (TextView) row.findViewById(R.id.tvPlaylistListItemName);
		TextView tvPlaylistTracks = (TextView) row.findViewById(R.id.tvPlaylistListItemNumberOfTracks);
		
		tvPlaylistName.setText(name);
		if(count != null)
		tvPlaylistTracks.setText(count);
		
		/* prefs */
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
		String txtSize = sharedPref.getString(PreferencesActivity.KEY_PREF_TXT_SIZE, "");
		int txtColor = sharedPref.getInt(PreferencesActivity.KEY_PREF_TXT_COLOR, 1);
		tvPlaylistName.setTextColor(txtColor);
		tvPlaylistName.setTextSize(Float.parseFloat(txtSize));
		tvPlaylistTracks.setTextColor(txtColor);
		tvPlaylistTracks.setTextSize(Float.parseFloat(txtSize));

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.push_left_in);
        animation.setDuration(500);
        row.startAnimation(animation);
		
		return row;
	}
	
}
