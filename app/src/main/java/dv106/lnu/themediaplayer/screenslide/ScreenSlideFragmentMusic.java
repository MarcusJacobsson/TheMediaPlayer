package dv106.lnu.themediaplayer.screenslide;

import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import dv106.lnu.themediaplayer.R;
import dv106.lnu.themediaplayer.activities.PlaySongActivity;
import dv106.lnu.themediaplayer.database.DataSource;
import dv106.lnu.themediaplayer.dialogfragments.AddNewPlaylistDialogFragment;
import dv106.lnu.themediaplayer.dialogfragments.AddSongToPlaylistDialogFragment;
import dv106.lnu.themediaplayer.dialogfragments.RemovedSongFromDatabaseDialogFragment;
import dv106.lnu.themediaplayer.listadapters.SongListAdapter;
import dv106.lnu.themediaplayer.pojo.Song;
import dv106.lnu.themediaplayer.util.DBCleaner;

public class ScreenSlideFragmentMusic extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener,
		OnItemLongClickListener, OnClickListener {

	private ArrayList<Song> allSongs;
	private SongListAdapter adapter;
	private ListView lwMusic;
	private DataSource datasource;
	private ActionMode mActionMode;
	private int selectedItem;
	private EditText etSearch;
	private ImageButton ibSearch;
	private String searchQuery;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSongs();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent playIntent = new Intent(getActivity(), PlaySongActivity.class);
		String songPath = allSongs.get(position).getDATA();
		String artist = allSongs.get(position).getARTIST();
		String title = allSongs.get(position).getTITLE();
		String duration = allSongs.get(position).getDURATION();

		playIntent.putExtra("songPath", songPath);
		playIntent.putExtra("artist", artist);
		playIntent.putExtra("title", title);
		playIntent.putExtra("duration", duration);

		File tmpFile = new File(songPath);
		if (tmpFile.exists()){
			
			SharedPreferences settings = getActivity().getSharedPreferences(
					"play_mode_prefs", 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("from_playlist", false);
			editor.commit();

			startActivity(playIntent);
		}else{
			try{
				DBCleaner cleaner = new DBCleaner();
				cleaner.cleanIfNeeded(getActivity());
				datasource.open();
				allSongs = datasource.getAllSongs();
				datasource.close();
				adapter.clear();
				adapter.addAll(allSongs);
				RemovedSongFromDatabaseDialogFragment removedSongDialg = new RemovedSongFromDatabaseDialogFragment();
				removedSongDialg.show(getActivity().getSupportFragmentManager(), "removedSongDialg");
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}

	private void getSongs() {
		allSongs = new ArrayList<Song>();
		LoaderManager loadermanager = getLoaderManager();
		loadermanager.initLoader(1, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_screen_slide_page_music, container, false);
		setUpComponents(rootView);

		if (savedInstanceState != null) {
			try {
				datasource.open();
				if(savedInstanceState.getString("searchQuery") != null){
					searchQuery = savedInstanceState.getString("searchQuery");
					allSongs = datasource.searchSongByName(savedInstanceState.getString("searchQuery"));
				}else{
					allSongs = datasource.getAllSongs();
				}	
				datasource.close();
				
				adapter = new SongListAdapter(getActivity(), allSongs);
				lwMusic.setAdapter(adapter);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return rootView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		if(searchQuery != null)
		outState.putString("searchQuery", searchQuery);
		super.onSaveInstanceState(outState);
	}

	private void setUpComponents(ViewGroup rootView) {
		lwMusic = (ListView) rootView.findViewById(R.id.lwMusic);
		ibSearch = (ImageButton) rootView.findViewById(R.id.ibSearchMusic);
		etSearch = (EditText) rootView.findViewById(R.id.etSearchMusic);	
		ibSearch.setOnClickListener(this);
		lwMusic.setOnItemClickListener(this);
		lwMusic.setOnItemLongClickListener(this);

		datasource = new DataSource(getActivity());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibSearchMusic:
			searchQuery = etSearch.getText().toString();
			
			try {
				datasource.open();
				allSongs = datasource.searchSongByName(searchQuery);
				datasource.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (allSongs.size() != 0) {
				adapter.clear();
				adapter.addAll(allSongs);
			}

			break;
		}
	}

	@Override
	public void onResume() {
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
		super.onResume();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String[] projection = { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Audio.Media.DURATION, };

		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
		return new CursorLoader(getActivity(),
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
				selection, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		if (!c.isClosed()) {
			while (c.moveToNext()) {
				String _ID = c.getString(0);
				String ARTIST = c.getString(1);
				String TITLE = c.getString(2);
				String DATA = c.getString(3);
				String DISPLAY_NAME = c.getString(4);
				String DURATION = c.getString(5);
				Song tmpSong = new Song(_ID, ARTIST, TITLE, DATA, DISPLAY_NAME,
						DURATION);
				allSongs.add(tmpSong);
				try {
					datasource.open();
					datasource.createSong(tmpSong);
					datasource.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			c.close();

			adapter = new SongListAdapter(getActivity(), allSongs);
			lwMusic.setAdapter(adapter);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
	}

	/** CONTEXT MENU **/

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.context_menu_add_song_to_playlist, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.add_to_playlist:
				try{
					datasource.open();
					ArrayList<String> allPlaylists = datasource.getAllPlaylists();
					datasource.close();
					if(allPlaylists.size() == 0){
						DialogFragment addPlaylistDialogFragment = new AddNewPlaylistDialogFragment();
						addPlaylistDialogFragment.show(getActivity().getSupportFragmentManager(), "addPlaylistDialogFragment");
					}else{
						String songPath = allSongs.get(selectedItem).getDATA();
                        Bundle bundle = new Bundle();
                        bundle.putString("songPath", songPath);
						DialogFragment addSongToPlaylistDialogFragment = new AddSongToPlaylistDialogFragment();
                        addSongToPlaylistDialogFragment.setArguments(bundle);
						addSongToPlaylistDialogFragment.show(getActivity()
								.getSupportFragmentManager(),
								"addSongToPlaylistDialogFragment");
					}
				}catch(SQLException e){
					e.printStackTrace();
				}
				mode.finish();
				return true;
			}
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
		}

	};

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (mActionMode != null) {
			return false;
		}
		selectedItem = position;
		mActionMode = getActivity().startActionMode(mActionModeCallback);
		view.setSelected(true);
		return true;
	}
}
