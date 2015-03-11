package dv106.lnu.themediaplayer.database;

import java.util.ArrayList;
import java.util.HashMap;

import dv106.lnu.themediaplayer.pojo.Playlist;
import dv106.lnu.themediaplayer.pojo.Song;
import dv106.lnu.themediaplayer.pojo.Video;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataSource {
	DBHelper dbHelper;
	SQLiteDatabase database;

	public DataSource(Context context) {
		dbHelper = new DBHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getReadableDatabase();
	}

	public void close() throws SQLException {
		dbHelper.close();
	}
	/********************************** VIDEO ********************************/
	
	public void createVideo(Video tmpVideo){
		if(!checkIfVideoExists(tmpVideo.getDATA())){
			ContentValues values = new ContentValues();
			values.put(DBHelper.VIDEO_COLUMN_DATA, tmpVideo.getDATA());
			values.put(DBHelper.VIDEO_COLUMN_DATE_ADDED, tmpVideo.getDATE_ADDED());
			values.put(DBHelper.VIDEO_COLUMN_DISPLAY_NAME, tmpVideo.getDISPLAY_NAME());
			values.put(DBHelper.VIDEO_COLUMN_DURATION, tmpVideo.getDURATION());
			values.put(DBHelper.VIDEO_COLUMN_ID, tmpVideo.get_ID());
			values.put(DBHelper.VIDEO_COLUMN_RESOLUTION, tmpVideo.getRESOLUTION());
			values.put(DBHelper.VIDEO_COLUMN_SIZE, tmpVideo.getSIZE());
			values.put(DBHelper.VIDEO_COLUMN_TITLE, tmpVideo.getTITLE());
			database.insert(DBHelper.TABLE_NAME_VIDEO, null, values);
		}
	}
	
	public void removeVideo(String videoPath){
		database.delete(DBHelper.TABLE_NAME_VIDEO,
				DBHelper.VIDEO_COLUMN_DATA + "='" + videoPath + "'", null);
	}
	
	public ArrayList<Video> getAllVideos(){
		Cursor cursor = database.query(DBHelper.TABLE_NAME_VIDEO, null, null,
				null, null, null, null);
		ArrayList<Video> allVideos = new ArrayList<Video>();
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			Video tmpVideo = new Video();
			tmpVideo.setDATA(cursor.getString(0));
			tmpVideo.setDATE_ADDED(cursor.getString(1));
			tmpVideo.setDISPLAY_NAME(cursor.getString(2));
			tmpVideo.setDURATION(cursor.getString(3));
			tmpVideo.set_ID(cursor.getString(4));
			tmpVideo.setRESOLUTION(cursor.getString(5));
			tmpVideo.setSIZE(cursor.getString(6));
			tmpVideo.setTITLE(cursor.getString(7));
			allVideos.add(tmpVideo);
			cursor.moveToNext();
		}
		cursor.close();
		return allVideos;
	}
	
	public ArrayList<Video> searchVideoByName(String searchQuery){
		ArrayList<Video> foundVideos = new ArrayList<Video>();
		String[] selectionArgs = new String[1];
		selectionArgs[0] = "%" + searchQuery + "%";
		Cursor cursor = database.query(DBHelper.TABLE_NAME_VIDEO, null, DBHelper.VIDEO_COLUMN_DISPLAY_NAME + " LIKE ?",selectionArgs, null, null, null);
		if(cursor.moveToFirst()){
			while (cursor.isAfterLast() == false) {
				Video tmpVideo = new Video();
				tmpVideo.setDATA(cursor.getString(0));
				tmpVideo.setDATE_ADDED(cursor.getString(1));
				tmpVideo.setDISPLAY_NAME(cursor.getString(2));
				tmpVideo.setDURATION(cursor.getString(3));
				tmpVideo.set_ID(cursor.getString(4));
				tmpVideo.setRESOLUTION(cursor.getString(5));
				tmpVideo.setSIZE(cursor.getString(6));
				tmpVideo.setTITLE(cursor.getString(7));
				foundVideos.add(tmpVideo);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return foundVideos;
	}
	
	private boolean checkIfVideoExists(String videoPath){
		Cursor cursor = database.query(DBHelper.TABLE_NAME_VIDEO, null,
				DBHelper.VIDEO_COLUMN_DATA + "='" + videoPath + "'", null, null,
				null, null);
		if (cursor.getCount() > 0) {
			cursor.close();
			return true;
		} else {
			cursor.close();
			return false;
		}
	}

	/********************************** SONG *********************************/

	public void createSong(Song tmpSong) {
		if (!checkIfSongExists(tmpSong.getDATA())) {
			ContentValues values = new ContentValues();
			values.put(DBHelper.SONG_COLUMN_DATA, tmpSong.getDATA());
			values.put(DBHelper.SONG_COLUMN_ARTIST, tmpSong.getARTIST());
			values.put(DBHelper.SONG_COLUMN_DISPLAY_NAME,
					tmpSong.getDISPLAY_NAME());
			values.put(DBHelper.SONG_COLUMN_DURATION, tmpSong.getDURATION());
			values.put(DBHelper.SONG_COLUMN_TITLE, tmpSong.getTITLE());
			database.insert(DBHelper.TABLE_NAME_SONG, null, values);
		}
	}
	
	public void removeSong(String songPath){
		database.delete(DBHelper.TABLE_NAME_SONG,
				DBHelper.SONG_COLUMN_DATA + "='" + songPath + "'", null);
	}

	public ArrayList<Song> getAllSongs() {
		Cursor cursor = database.query(DBHelper.TABLE_NAME_SONG, null, null,
				null, null, null, null);
		ArrayList<Song> allSongs = new ArrayList<Song>();
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			Song tmpSong = new Song();
			tmpSong.setDATA(cursor.getString(0));
			tmpSong.setARTIST(cursor.getString(1));
			tmpSong.setDISPLAY_NAME(cursor.getString(2));
			tmpSong.setDURATION(cursor.getString(3));
			tmpSong.setTITLE(cursor.getString(4));
			allSongs.add(tmpSong);
			cursor.moveToNext();
		}
		cursor.close();
		return allSongs;
	}

	public Song getSong(String songPath) {
		if (songPath != null) {
			Cursor cursor = database.query(DBHelper.TABLE_NAME_SONG, null,
					DBHelper.SONG_COLUMN_DATA + "='" + songPath + "'", null,
					null, null, null);
			cursor.moveToFirst();
			Song tmpSong = new Song();
			tmpSong.setDATA(cursor.getString(0));
			tmpSong.setARTIST(cursor.getString(1));
			tmpSong.setDISPLAY_NAME(cursor.getString(2));
			tmpSong.setDURATION(cursor.getString(3));
			tmpSong.setTITLE(cursor.getString(4));
			cursor.close();
			return tmpSong;
		} else {
			
			return null;
		}
	}
	
	public ArrayList<Song> searchSongByName(String searchQuery){
		ArrayList<Song> foundSongs = new ArrayList<Song>();
		String[] selectionArgs = new String[1];
		selectionArgs[0] = "%" + searchQuery + "%";
		Cursor cursor = database.query(DBHelper.TABLE_NAME_SONG, null, DBHelper.SONG_COLUMN_DISPLAY_NAME + " LIKE ?",selectionArgs, null, null, null);
		if(cursor.moveToFirst()){
			while (cursor.isAfterLast() == false) {
				Song tmpSong = new Song();
				tmpSong.setDATA(cursor.getString(0));
				tmpSong.setARTIST(cursor.getString(1));
				tmpSong.setDISPLAY_NAME(cursor.getString(2));
				tmpSong.setDURATION(cursor.getString(3));
				tmpSong.setTITLE(cursor.getString(4));
				foundSongs.add(tmpSong);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return foundSongs;
	}

	private boolean checkIfSongExists(String songPath) {
		Cursor cursor = database.query(DBHelper.TABLE_NAME_SONG, null,
				DBHelper.SONG_COLUMN_DATA + "='" + songPath + "'", null, null,
				null, null);
		if (cursor.getCount() > 0) {
			cursor.close();
			return true;
		} else {
			cursor.close();
			return false;
		}
	}

	/****************************** PLAYLIST *********************************/
	
	public void createPlaylist(String name) {
		if (!checkIfPlaylistExists(name)) {
			ContentValues values = new ContentValues();
			values.put(DBHelper.PLAYLIST_COLUMN_NAME, name);
			values.put(DBHelper.PLAYLIST_COLUMN_TIMESTAMP, System.currentTimeMillis());
			database.insert(DBHelper.TABLE_NAME_PLAYLIST, null, values);
		}
	}

	public void removePlaylist(String name) {
		database.delete(DBHelper.TABLE_NAME_PLAYLIST,
				DBHelper.PLAYLIST_COLUMN_NAME + "='" + name + "'", null);
		database.delete(DBHelper.TABLE_NAME_PLAYLIST_SONG, DBHelper.PLAYLIST_SONG_COLUMN_NAME + " = '" + name + "'", null);
	}

	public ArrayList<String> getAllPlaylists() {
		ArrayList<String> allPlaylists = new ArrayList<String>();
		Cursor cursor = database.query(DBHelper.TABLE_NAME_PLAYLIST, null,
				null, null, null, null, null);
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			allPlaylists.add(cursor.getString(0));
			cursor.moveToNext();
		}
		cursor.close();
		return allPlaylists;
	}
	
	public boolean updatePlaylistName(String oldName, String newName) {
		if (!checkIfPlaylistExists(newName)) {
			ArrayList<Playlist> allPlaylists = new ArrayList<Playlist>();
			Playlist oldPlaylist = new Playlist();
			
			//Get the playlist to be updated
			Cursor cursorOldPlaylist = database.query(DBHelper.TABLE_NAME_PLAYLIST, null,
					DBHelper.PLAYLIST_COLUMN_NAME + "='" + oldName + "'",
					null, null, null, null);
			cursorOldPlaylist.moveToFirst();			
			oldPlaylist.setName(cursorOldPlaylist.getString(0));
			oldPlaylist.setTimestamp(cursorOldPlaylist.getInt(1));
			cursorOldPlaylist.close();
			
			//Get the songs from the old playlist
			ArrayList<Song> oldPlaylistSongs = this.getSongsFromPlaylist(oldName);
			
			//Get all playlists
			Cursor cursorAllPlaylists = database.query(DBHelper.TABLE_NAME_PLAYLIST, null,
					null, null, null, null, null);
			cursorAllPlaylists.moveToFirst();
			while (cursorAllPlaylists.isAfterLast() == false) {
				Playlist tmpPlaylist = new Playlist();
				tmpPlaylist.setName(cursorAllPlaylists.getString(0));
				tmpPlaylist.setTimestamp(cursorAllPlaylists.getInt(1));
				allPlaylists.add(tmpPlaylist);
				cursorAllPlaylists.moveToNext();
			}
			cursorAllPlaylists.close();
			
			//Get the songs of all the playlists created after the updated playlist, as well as their songs. 
			HashMap <String, ArrayList<Song>> songMap = new HashMap<String, ArrayList<Song>>();
			ArrayList<String> removedPlaylistsNames = new ArrayList<String>();
			for(Playlist playlist : allPlaylists){
				if(oldPlaylist.getTimestamp() < playlist.getTimestamp()){
					ArrayList<Song> allPlaylistSongs = this.getSongsFromPlaylist(playlist.getName());
					songMap.put(playlist.getName(), allPlaylistSongs);
					removedPlaylistsNames.add(playlist.getName());
					this.removePlaylist(playlist.getName());
					
				}
			}
			
			//Re-add all songs from the old playlist to the newly created playlist with the new name
			this.removePlaylist(oldName);
			this.createPlaylist(newName);
			for(Song song : oldPlaylistSongs){
				this.addSongToPlaylist(song.getDATA(), newName);
			}
			
			//Re-add all songs to all the playlists that was removed
			for(String playlistName : removedPlaylistsNames){
				this.createPlaylist(playlistName);
				ArrayList<Song> tmpList = songMap.get(playlistName);
				for(Song song : tmpList){
					this.addSongToPlaylist(song.getDATA(), playlistName);
				}
			}
			return true;
		}else{
			return false;
		}
	}
	
	public ArrayList<String> searchPlaylistByName(String searchQuery){
		ArrayList<String> foundPlaylists = new ArrayList<String>();
		
		String[] selectionArgs = new String[1];
		selectionArgs[0] = "%" + searchQuery + "%";
		Cursor cursor = database.query(DBHelper.TABLE_NAME_PLAYLIST, null, DBHelper.PLAYLIST_COLUMN_NAME + " LIKE ?",selectionArgs, null, null, null);
		if(cursor.moveToFirst()){
			while (cursor.isAfterLast() == false) {
				foundPlaylists.add(cursor.getString(0));
				cursor.moveToNext();
			}
		}
		cursor.close();
		return foundPlaylists;
	}

	private boolean checkIfPlaylistExists(String playlistName) {
		Cursor cursor = database.query(DBHelper.TABLE_NAME_PLAYLIST, null,
				DBHelper.PLAYLIST_COLUMN_NAME + "='" + playlistName + "'",
				null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.close();
			return true;
		} else {
			cursor.close();
			return false;
		}
	}

	/***************************** PLAYLIST_SONG *****************************/

	public boolean addSongToPlaylist(String songPath, String playlistName) {
		if(!checkIfSongAlreadyAdded(songPath, playlistName)){
			ContentValues values = new ContentValues();
			values.put(DBHelper.PLAYLIST_SONG_COLUMN_DATA, songPath);
			values.put(DBHelper.PLAYLIST_SONG_COLUMN_NAME, playlistName);
			database.insert(DBHelper.TABLE_NAME_PLAYLIST_SONG, null, values);
			return true;
		}
		return false;
		
	}

	public ArrayList<Song> getSongsFromPlaylist(String playlistName) {
		ArrayList<Song> allSongsOnPlaylist = new ArrayList<Song>();

		Cursor cursor = database.query(DBHelper.TABLE_NAME_PLAYLIST_SONG,
				new String[] { DBHelper.PLAYLIST_SONG_COLUMN_DATA },
				DBHelper.PLAYLIST_SONG_COLUMN_NAME + "='" + playlistName + "'",
				null, null, null, null);
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			allSongsOnPlaylist.add(this.getSong(cursor.getString(0)));
			cursor.moveToNext();
		}
		cursor.close();
		return allSongsOnPlaylist;
	}

	public void removeSongFromPlaylist(String songPath, String playlistName) {
		database.delete(DBHelper.TABLE_NAME_PLAYLIST_SONG,
				DBHelper.PLAYLIST_SONG_COLUMN_DATA + "='" + songPath + "'" + " AND " + DBHelper.PLAYLIST_SONG_COLUMN_NAME + "='" + playlistName +"'",
				null);
	}
	
	public void removeSongFromAllPlaylists(String songPath){
		database.delete(DBHelper.TABLE_NAME_PLAYLIST_SONG,
				DBHelper.PLAYLIST_SONG_COLUMN_DATA + "='" + songPath + "'",
				null);
	}

	public int getPlaylistCount(String name) {
		Cursor cursor = database.query(DBHelper.TABLE_NAME_PLAYLIST_SONG,
				new String[] { DBHelper.PLAYLIST_SONG_COLUMN_DATA },
				DBHelper.PLAYLIST_SONG_COLUMN_NAME + "='" + name + "'", null,
				null, null, null);
		return cursor.getCount();
	}
	
	private boolean checkIfSongAlreadyAdded(String songPath, String playlistName) {
		Cursor cursor = database.query(DBHelper.TABLE_NAME_PLAYLIST_SONG, null,
				DBHelper.PLAYLIST_SONG_COLUMN_NAME + "='" + playlistName + "' AND " + DBHelper.PLAYLIST_SONG_COLUMN_DATA + " = '" + songPath + "'",
				null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.close();
			return true;
		} else {
			cursor.close();
			return false;
		}
	}
}
