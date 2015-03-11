package dv106.lnu.themediaplayer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "themediaplayer.db";
	
	/* Song table */
	public static final String TABLE_NAME_SONG = "song";
	public static final String SONG_COLUMN_ARTIST = "artist";
	public static final String SONG_COLUMN_TITLE = "title";
	public static final String SONG_COLUMN_DATA = "data"; //PK
	public static final String SONG_COLUMN_DISPLAY_NAME = "display_name";
	public static final String SONG_COLUMN_DURATION = "duration";
	
	/* Playlist table */
	public static final String TABLE_NAME_PLAYLIST = "playlist";
	public static final String PLAYLIST_COLUMN_NAME = "name"; //PK
	public static final String PLAYLIST_COLUMN_TIMESTAMP = "timestamp";
	
	/* Playlist_Song table */
	public static final String TABLE_NAME_PLAYLIST_SONG = "playlist_song";
	public static final String PLAYLIST_SONG_COLUMN_NAME = "name"; //FK
	public static final String PLAYLIST_SONG_COLUMN_DATA = "data"; //FK
	
	/* Video table */
	public static final String TABLE_NAME_VIDEO = "video";
	public static final String VIDEO_COLUMN_ID = "id";
	public static final String VIDEO_COLUMN_DATA = "data"; //PK
	public static final String VIDEO_COLUMN_DURATION = "duration";
	public static final String VIDEO_COLUMN_DISPLAY_NAME = "display_name";
	public static final String VIDEO_COLUMN_RESOLUTION = "resolution";
	public static final String VIDEO_COLUMN_SIZE = "size";
	public static final String VIDEO_COLUMN_TITLE = "title";
	public static final String VIDEO_COLUMN_DATE_ADDED = "date_added";
	
	public static final int DATABASE_VERSION = 1;
	
	public static final String DATABASE_CREATE_SONG = 
			"create table "
			+ TABLE_NAME_SONG
			+" ( "
			+SONG_COLUMN_DATA + " text primary key,"
			+SONG_COLUMN_ARTIST + " text,"	
			+SONG_COLUMN_DISPLAY_NAME + " text,"
			+SONG_COLUMN_DURATION + " text,"
			+SONG_COLUMN_TITLE + " text);";
	
	public static final String DATABASE_CREATE_PLAYLIST = 
			"create table "
			+TABLE_NAME_PLAYLIST
			+" ( "
			+PLAYLIST_COLUMN_NAME + " text primary key, " 
			+PLAYLIST_COLUMN_TIMESTAMP + " integer);";
			
	public static final String DATABASE_CREATE_SONG_PLAYLIST = "create table "
			+TABLE_NAME_PLAYLIST_SONG
			+" ( "
			+PLAYLIST_SONG_COLUMN_DATA + " text, "
			+PLAYLIST_SONG_COLUMN_NAME + " text, "
			+"primary key (" +PLAYLIST_SONG_COLUMN_DATA + ", " +PLAYLIST_SONG_COLUMN_NAME + "), "
			+"foreign key(" + PLAYLIST_SONG_COLUMN_NAME + ") references " + TABLE_NAME_PLAYLIST + "(" + PLAYLIST_COLUMN_NAME + "), "
			+"foreign key(" + PLAYLIST_SONG_COLUMN_DATA + ") references " + TABLE_NAME_SONG + "(" + SONG_COLUMN_DATA + "));";
	
	public static final String DATABASE_CREATE_VIDEO = "create table " 
			+TABLE_NAME_VIDEO
			+" ( "
			+VIDEO_COLUMN_DATA + " text primary key, "
			+VIDEO_COLUMN_DATE_ADDED + " text, "
			+VIDEO_COLUMN_DISPLAY_NAME + " text, "
			+VIDEO_COLUMN_DURATION + " text, "
			+VIDEO_COLUMN_ID + " text, "
			+VIDEO_COLUMN_RESOLUTION + " text, "
			+VIDEO_COLUMN_SIZE + " text, "
			+VIDEO_COLUMN_TITLE + " text); ";
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE_SONG);
		db.execSQL(DATABASE_CREATE_PLAYLIST);
		db.execSQL(DATABASE_CREATE_SONG_PLAYLIST);
		db.execSQL(DATABASE_CREATE_VIDEO);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PLAYLIST_SONG);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PLAYLIST);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SONG);
		onCreate(db);
	}
}
