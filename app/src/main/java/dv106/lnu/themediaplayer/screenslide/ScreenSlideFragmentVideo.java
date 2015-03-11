package dv106.lnu.themediaplayer.screenslide;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
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

import java.io.File;
import java.util.ArrayList;

import dv106.lnu.themediaplayer.R;
import dv106.lnu.themediaplayer.activities.PlayVideoActivity;
import dv106.lnu.themediaplayer.database.DataSource;
import dv106.lnu.themediaplayer.dialogfragments.RemovedVideoFromDatabaseDialogFragment;
import dv106.lnu.themediaplayer.dialogfragments.VideoPropertiesDialogFragment;
import dv106.lnu.themediaplayer.listadapters.VideoListAdapter;
import dv106.lnu.themediaplayer.pojo.Video;

public class ScreenSlideFragmentVideo extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener,
        OnItemLongClickListener, OnClickListener {

    private LoaderManager loadermanager;
    private ArrayList<Video> allVideos;
    private ListView lwVideos;
    private VideoListAdapter adapter;
    private ActionMode mActionMode;
    private int selectedItem;
    private ImageButton ibSearch;
    private EditText etSearch;
    private DataSource datasource;
    private String searchQuery;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getVideos();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (searchQuery != null)
            outState.putString("searchQuery", searchQuery);
        super.onSaveInstanceState(outState);
    }

    private void getVideos() {
        allVideos = new ArrayList<Video>();
        loadermanager = getLoaderManager();
        loadermanager.initLoader(1, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.RESOLUTION,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATE_ADDED,};

        return new CursorLoader(getActivity(),
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
        if (!c.isClosed() && allVideos.size() == 0) {
            while (c.moveToNext()) {
                Video tmpVideo = new Video();
                tmpVideo.set_ID(c.getString(0));
                tmpVideo.setDATA(c.getString(1));
                tmpVideo.setDURATION(c.getString(2));
                tmpVideo.setDISPLAY_NAME(c.getString(3));
                tmpVideo.setRESOLUTION(c.getString(4));
                tmpVideo.setSIZE(c.getString(5));
                tmpVideo.setTITLE(c.getString(6));
                tmpVideo.setDATE_ADDED(c.getString(7));
                allVideos.add(tmpVideo);
                try {
                    datasource.open();
                    datasource.createVideo(tmpVideo);
                    datasource.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            c.close();

            adapter = new VideoListAdapter(getActivity(), allVideos);
            lwVideos.setAdapter(adapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page_video, container, false);
        setUpComponents(rootView);

        if (savedInstanceState != null) {
            try {
                datasource.open();
                if (savedInstanceState.getString("searchQuery") != null) {
                    searchQuery = savedInstanceState.getString("searchQuery");
                    allVideos = datasource.searchVideoByName(savedInstanceState.getString("searchQuery"));
                } else {
                    allVideos = datasource.getAllVideos();
                }
                datasource.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            adapter = new VideoListAdapter(getActivity(), allVideos);
            lwVideos.setAdapter(adapter);
        }
        return rootView;
    }

    private void setUpComponents(ViewGroup rootView) {
        datasource = new DataSource(getActivity());
        etSearch = (EditText) rootView.findViewById(R.id.etSearchVideo);
        ibSearch = (ImageButton) rootView.findViewById(R.id.ibSearchVideo);

        ibSearch.setOnClickListener(this);
        lwVideos = (ListView) rootView.findViewById(R.id.lwVideos);
        lwVideos.setOnItemClickListener(this);
        lwVideos.setOnItemLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibSearchVideo:
                searchQuery = etSearch.getText().toString();
                try {
                    datasource.open();
                    allVideos = datasource.searchVideoByName(searchQuery);
                    datasource.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (allVideos.size() != 0) {
                    adapter.clear();
                    adapter.addAll(allVideos);
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
    public void onPause() {
        if(mActionMode != null){
            mActionMode.finish();
        }
        super.onPause();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        File tmpFile = new File(allVideos.get(position).getDATA());
        if (tmpFile.exists()) {
            Intent playVideo = new Intent(getActivity(), PlayVideoActivity.class);
            playVideo.putExtra("videoPath", allVideos.get(position).getDATA());
            startActivity(playVideo);
        } else {
            DialogFragment removedVideoFromDatabaseDialogFragment = new RemovedVideoFromDatabaseDialogFragment();
            removedVideoFromDatabaseDialogFragment.show(getActivity().getSupportFragmentManager(), "removedVideoFromDatabaseDialogFragment");
            try {
                datasource.open();
                datasource.removeVideo(allVideos.get(position).getDATA());
                allVideos = datasource.getAllVideos();
                datasource.close();
                adapter.clear();
                adapter.addAll(allVideos);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * CONTEXT MENU *
     */

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu_video, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.video_properties:
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("video", allVideos.get(selectedItem));
                    DialogFragment videoPropertiesDialogFragment = new VideoPropertiesDialogFragment();
                    videoPropertiesDialogFragment.setArguments(bundle);
                    videoPropertiesDialogFragment.show(getActivity()
                                    .getSupportFragmentManager(),
                            "videoPropertiesDialogFragment");
                    mode.finish();
                    return true;

                case R.id.video_share:
                    Uri uri = Uri.parse(allVideos.get(selectedItem).getDATA());
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.cm_video_share_title));
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sharingIntent.setType("video/*");
                    startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.cm_video_share_msg)));
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
