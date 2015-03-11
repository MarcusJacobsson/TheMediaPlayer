package dv106.lnu.themediaplayer.listadapters;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import dv106.lnu.themediaplayer.R;
import dv106.lnu.themediaplayer.pojo.Song;
import dv106.lnu.themediaplayer.preferences.PreferencesActivity;
import dv106.lnu.themediaplayer.util.MediaPlayerTimeUtil;

public class SongListAdapter extends ArrayAdapter<Song> {
	
	private ArrayList<Song> allSongs;
	
	public SongListAdapter(Context c, ArrayList<Song> songs){
		super(c, R.layout.music_list_item, songs);
		this.allSongs = songs;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		/**
		 * Method re-used from the lecture slides to save battery
		 */
		View row;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			row = inflater.inflate(R.layout.music_list_item, null);
		} else {
			row = convertView;
		}
		
		Song song = allSongs.get(position);
		
		String title = song.getTITLE();
		String artist = song.getARTIST();
		long songDur = Long.valueOf(song.getDURATION());
		
		String duration = MediaPlayerTimeUtil.formatMillisecond(songDur);
		
		TextView tvTitle = (TextView) row.findViewById(R.id.tvMusicListItemTitle);
		TextView tvArtist = (TextView) row.findViewById(R.id.tvMusicListItemArtist);
		TextView tvDuration = (TextView) row.findViewById(R.id.tvMusicListItemDuration);
		
		tvTitle.setText(title);
		tvArtist.setText(artist);
		tvDuration.setText(duration);
		
		/* prefs */
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
		String txtSize = sharedPref.getString(PreferencesActivity.KEY_PREF_TXT_SIZE, "");
		String txtColor = sharedPref.getString(PreferencesActivity.KEY_PREF_TXT_COLOR, "");
		tvTitle.setTextSize(Float.parseFloat(txtSize));
		tvTitle.setTextColor(Integer.parseInt(txtColor));
		tvArtist.setTextSize(Float.parseFloat(txtSize));
		tvArtist.setTextColor(Integer.parseInt(txtColor));
		tvDuration.setTextSize(Float.parseFloat(txtSize));
		tvDuration.setTextColor(Integer.parseInt(txtColor));

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.push_left_in);
        animation.setDuration(500);
        row.startAnimation(animation);
		
		return row;
	}
		
}
