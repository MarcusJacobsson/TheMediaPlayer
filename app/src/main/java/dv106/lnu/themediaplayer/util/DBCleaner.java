package dv106.lnu.themediaplayer.util;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.database.SQLException;
import dv106.lnu.themediaplayer.pojo.Song;
import dv106.lnu.themediaplayer.pojo.Video;
import dv106.lnu.themediaplayer.database.*;

public class DBCleaner {
	private DataSource datasource;

	/**
	 * Removes songs and videos from the database that no longer exists, this
	 * includes removing songs from playlists if they no longer exists.
	 * 
	 * @param context
	 */
	public void cleanIfNeeded(Context context) {
		datasource = new DataSource(context);
		try {
			datasource.open();
			ArrayList<Song> allSongs = datasource.getAllSongs();
			ArrayList<Video> allVideos = datasource.getAllVideos();
			for (Song song : allSongs) {
				File tmpFile = new File(song.getDATA());
				if (!tmpFile.exists()) {
					datasource.removeSong(song.getDATA());
					datasource.removeSongFromAllPlaylists(song.getDATA());
				}
			}
			for (Video video : allVideos) {
				File tmpFile = new File(video.getDATA());
				if (!tmpFile.exists()) {
					datasource.removeVideo(video.getDATA());
				}
			}
			datasource.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
