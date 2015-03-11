package dv106.lnu.themediaplayer.screenslide;

import java.util.ArrayList;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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
import dv106.lnu.themediaplayer.activities.ShowPlaylistActivity;
import dv106.lnu.themediaplayer.database.DataSource;
import dv106.lnu.themediaplayer.dialogfragments.CreatePlaylistDialogFragment;
import dv106.lnu.themediaplayer.dialogfragments.RemovePlaylistDialogFragment;
import dv106.lnu.themediaplayer.dialogfragments.UpdatePlaylistDialogFragment;
import dv106.lnu.themediaplayer.listadapters.PlaylistListAdapter;

public class ScreenSlideFragmentPlaylists extends Fragment implements
		OnClickListener, OnItemClickListener,
		OnItemLongClickListener {

	private ImageButton btnAddPlaylist;
	private ListView lwPlaylists;
	private DataSource datasource;
	private PlaylistListAdapter adapter;
	private ArrayList<String> allPlaylists;
	private ActionMode mActionMode;
	private int selectedItem;
	private ImageButton ibSearch;
	private EditText etSearch;
	private String searchQuery;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater
				.inflate(R.layout.fragment_screen_slide_page_playlists,
						container, false);
		setUpComponents(rootView);
		getAllPlaylists(savedInstanceState);
		return rootView;
	}

	private void setUpComponents(ViewGroup rootView) {
		btnAddPlaylist = (ImageButton) rootView
				.findViewById(R.id.ibAddPlaylist);
		lwPlaylists = (ListView) rootView.findViewById(R.id.lwPlaylists);
		ibSearch = (ImageButton) rootView.findViewById(R.id.ibSearchPlaylist);
		etSearch = (EditText) rootView.findViewById(R.id.etSearchPlaylist);

		btnAddPlaylist.setOnClickListener(this);
		lwPlaylists.setOnItemClickListener(this);
		lwPlaylists.setOnItemLongClickListener(this);
		ibSearch.setOnClickListener(this);

		datasource = new DataSource(getActivity());
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.ibAddPlaylist:
			DialogFragment createPlaylistDialog = new CreatePlaylistDialogFragment();
			createPlaylistDialog.show(getFragmentManager(), "createFragmentDialog");
			break;
			
		case R.id.ibSearchPlaylist:
			searchQuery = etSearch.getText().toString();
			try {
				datasource.open();
				allPlaylists = datasource.searchPlaylistByName(searchQuery);
				datasource.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (allPlaylists.size() != 0) {
				adapter.clear();
				adapter.addAll(allPlaylists);
			}
			break;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String playlistName = allPlaylists.get(position);
		Intent showPlaylistIntent = new Intent(getActivity(),
				ShowPlaylistActivity.class);
		showPlaylistIntent.putExtra("playlistName", playlistName);
		startActivity(showPlaylistIntent);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		if(searchQuery != null)
			outState.putString("searchQuery", searchQuery);	
		super.onSaveInstanceState(outState);
	}

	private void getAllPlaylists(Bundle savedInstanceState) {
		try {
			datasource.open();
			if(savedInstanceState != null && savedInstanceState.getString("searchQuery") != null){
				searchQuery = savedInstanceState.getString("searchQuery");
				allPlaylists = datasource.searchPlaylistByName(savedInstanceState.getString("searchQuery"));
			}else{
				allPlaylists = datasource.getAllPlaylists();
			}
			
			datasource.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (allPlaylists != null) {
			adapter = new PlaylistListAdapter(getActivity(), allPlaylists);
			lwPlaylists.setAdapter(adapter);
		}
	}

	@Override
	public void onResume() {
		if(adapter != null){
			adapter.notifyDataSetChanged();
		}	
		super.onResume();
	}

    @Override
    public void onPause() {
        if(mActionMode != null){
            mActionMode.finish();
        }
        super.onPause();
    }

    /** CONTEXT MENU **/

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.context_menu_remove_playlist, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.remove_playlist:
                Bundle bundle = new Bundle();
                bundle.putString("playlistName", allPlaylists.get(selectedItem));
				DialogFragment removePlaylistDialog = new RemovePlaylistDialogFragment();
                removePlaylistDialog.setArguments(bundle);
				removePlaylistDialog.show(getActivity()
						.getSupportFragmentManager(), "removePlaylistDialog");
				mode.finish();
				return true;
				
			case R.id.update_playlist:
                Bundle bundle2 = new Bundle();
                bundle2.putString("oldName", allPlaylists.get(selectedItem));
				DialogFragment updatePlaylistDialog = new UpdatePlaylistDialogFragment();
                updatePlaylistDialog.setArguments(bundle2);
				updatePlaylistDialog.show(getActivity().getSupportFragmentManager(), "updatePlaylistDialog");
				
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
