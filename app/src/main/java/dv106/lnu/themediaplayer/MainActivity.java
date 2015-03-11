package dv106.lnu.themediaplayer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import dv106.lnu.themediaplayer.activities.PlaySongActivity;
import dv106.lnu.themediaplayer.database.DataSource;
import dv106.lnu.themediaplayer.dialogfragments.AddNewPlaylistDialogFragment.AddNewPlaylistDialogListener;
import dv106.lnu.themediaplayer.dialogfragments.AddSongToPlaylistDialogFragment.AddSongToPlaylistDialogListener;
import dv106.lnu.themediaplayer.dialogfragments.CreatePlaylistDialogFragment;
import dv106.lnu.themediaplayer.dialogfragments.CreatePlaylistDialogFragment.CreatePlaylistDialogListener;
import dv106.lnu.themediaplayer.dialogfragments.FirstTimeDialogFragment;
import dv106.lnu.themediaplayer.dialogfragments.NotMountedDialogFragment;
import dv106.lnu.themediaplayer.dialogfragments.RemovePlaylistDialogFragment.RemovePlaylistDialogListener;
import dv106.lnu.themediaplayer.dialogfragments.SendFeedbackDialogFragment;
import dv106.lnu.themediaplayer.dialogfragments.UpdatePlaylistDialogFragment.UpdatePlaylistDialogListener;
import dv106.lnu.themediaplayer.listadapters.PlaylistListAdapter;
import dv106.lnu.themediaplayer.pojo.Song;
import dv106.lnu.themediaplayer.preferences.PreferencesActivity;
import dv106.lnu.themediaplayer.screenslide.ScreenSlideFragmentMusic;
import dv106.lnu.themediaplayer.screenslide.ScreenSlideFragmentPlaylists;
import dv106.lnu.themediaplayer.screenslide.ScreenSlideFragmentVideo;
import dv106.lnu.themediaplayer.screenslide.ScreenSlidePagerAdapter;
import dv106.lnu.themediaplayer.service.SongService;
import dv106.lnu.themediaplayer.service.SongService.SongServiceBinder;
import dv106.lnu.themediaplayer.util.DBCleaner;

public class MainActivity extends FragmentActivity implements
        OnPageChangeListener, CreatePlaylistDialogListener,
        RemovePlaylistDialogListener, AddSongToPlaylistDialogListener,
        UpdatePlaylistDialogListener, LoaderManager.LoaderCallbacks<Cursor>,
        OnClickListener, AddNewPlaylistDialogListener {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private TextView tvHeaderPlaylists;
    private TextView tvHeaderMusic;
    private TextView tvHeaderVideo;
    private DataSource datasource;
    private LoaderManager loadermanager;
    private ArrayList<String> allVideoPaths;
    private ArrayList<String> allVideoIDs;
    private TextView tvGlobalControlArtist;
    private TextView tvGlobalControlTitle;
    private ImageButton ibGlobalControlPlayPause;
    private LinearLayout llGlobalControl;
    private BroadcastReceiver mMessageReceiver;

    private boolean bound;
    private ServiceConnection connection;
    private SongService service = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        if (checkIfMounted()) {
            connectToService();
            Intent startIntent = new Intent(this, SongService.class);
            startService(startIntent);
            this.bindService(startIntent, connection, Context.BIND_AUTO_CREATE);
            setUpComponents();
            setUpPager();
            getVideosAndCreateThumbnails();
            updateGlobalControl();
            cleanDatabaseIfNeeded();
            showWelcomeDialogIfNeeded();
        } else {
            DialogFragment notMountedDialogFragment = new NotMountedDialogFragment();
            notMountedDialogFragment.show(getSupportFragmentManager(),
                    "notMountedDialogFragment");
        }
    }

    private void showWelcomeDialogIfNeeded() {
        boolean firstrun = getSharedPreferences("firstTime", MODE_PRIVATE)
                .getBoolean("firstTime", true);
        if (firstrun) {
            DialogFragment firstTime = new FirstTimeDialogFragment();
            firstTime.show(getSupportFragmentManager(), "firstTime");
        }
    }

    private void cleanDatabaseIfNeeded() {
        DBCleaner cleaner = new DBCleaner();
        cleaner.cleanIfNeeded(this);
    }

    private void updateGlobalControl() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String txtColor = sharedPref.getString(PreferencesActivity.KEY_PREF_TXT_COLOR, "");
        tvGlobalControlArtist.setTextColor(Integer.parseInt(txtColor));
        tvGlobalControlTitle.setTextColor(Integer.parseInt(txtColor));
        if (ibGlobalControlPlayPause != null) {
            ibGlobalControlPlayPause.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_action_play_over_video));
        }
        if (service != null && service.isPlaying()) {
            try {
                datasource.open();
                Song tmpSong = datasource.getSong(service.getCurrentSongPath());
                tvGlobalControlArtist.setText(tmpSong.getARTIST());
                tvGlobalControlTitle.setText(tmpSong.getTITLE());
                ibGlobalControlPlayPause.setImageDrawable(getResources()
                        .getDrawable(R.drawable.ic_action_pause_over_video));
                datasource.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (connection != null)
            unbindService(connection);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGlobalControl();
        updateHeaderColorIfNeeded();
        cleanDatabaseIfNeeded();
        /*
		 * If tracks change while the MainActivity is active, this
		 * BroadcastReceiver will update the global control
		 */
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateGlobalControl();
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver,
                new IntentFilter(
                        "dv106.lnu.themediaplayer.UPDATE_GLOBAL_CONTROL"));
    }

    private void updateHeaderColorIfNeeded() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String txtColor = sharedPref.getString(PreferencesActivity.KEY_PREF_TXT_COLOR, "");

        if (mPager != null) {
            int current = mPager.getCurrentItem();
            switch (current) {
                case 0: // Playlist
                    tvHeaderPlaylists.setTextColor(Integer.parseInt(txtColor));
                    tvHeaderMusic.setTextColor(getResources().getColor(R.color.black));
                    tvHeaderVideo.setTextColor(getResources().getColor(R.color.black));
                    break;

                case 1: // Music
                    tvHeaderPlaylists.setTextColor(getResources().getColor(
                            R.color.black));
                    tvHeaderMusic.setTextColor(Integer.parseInt(txtColor));
                    tvHeaderVideo.setTextColor(getResources().getColor(R.color.black));
                    break;

                case 2: // Video
                    tvHeaderPlaylists.setTextColor(getResources().getColor(
                            R.color.black));
                    tvHeaderMusic.setTextColor(getResources().getColor(R.color.black));
                    tvHeaderVideo.setTextColor(Integer.parseInt(txtColor));
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);
    }

    private void setUpComponents() {
        tvHeaderPlaylists = (TextView) findViewById(R.id.tvHeadingPlaylists);
        tvHeaderMusic = (TextView) findViewById(R.id.tvHeadingsMusic);
        tvHeaderVideo = (TextView) findViewById(R.id.tvHeadingsVideo);
        tvGlobalControlArtist = (TextView) findViewById(R.id.tvGlobalControlArtist);
        tvGlobalControlTitle = (TextView) findViewById(R.id.tvGlobalControlTitle);
        ibGlobalControlPlayPause = (ImageButton) findViewById(R.id.ibGlobalControlPlayPause);
        datasource = new DataSource(this);
        tvHeaderPlaylists.setTextColor(getResources().getColor(R.color.green));
        ibGlobalControlPlayPause.setOnClickListener(this);
        llGlobalControl = (LinearLayout) findViewById(R.id.llGlobalControl);
        llGlobalControl.setOnClickListener(this);

        //For navigation
        tvHeaderPlaylists.setOnClickListener(this);
        tvHeaderMusic.setOnClickListener(this);
        tvHeaderVideo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibGlobalControlPlayPause:
                if (bound) {
                    int state = service.pauseOrResumeMusic();
                    if (state == 0) {
                        ibGlobalControlPlayPause.setImageDrawable(getResources()
                                .getDrawable(R.drawable.ic_action_play_over_video));
                    } else if (state == 1) {
                        ibGlobalControlPlayPause
                                .setImageDrawable(getResources().getDrawable(
                                        R.drawable.ic_action_pause_over_video));
                    }
                    updateGlobalControl();
                }

                break;

            case R.id.llGlobalControl:
                if (service.isPlaying()) {
                    Intent resumePlaySongActivity = new Intent(this,
                            PlaySongActivity.class);
                    resumePlaySongActivity.putExtra("resumePlaySongActivity", true);
                    startActivity(resumePlaySongActivity);
                }
                break;

            case R.id.tvHeadingPlaylists:
                if (mPager != null)
                    mPager.setCurrentItem(0);
                break;

            case R.id.tvHeadingsMusic:
                if (mPager != null)
                    mPager.setCurrentItem(1);
                break;

            case R.id.tvHeadingsVideo:
                if (mPager != null)
                    mPager.setCurrentItem(2);
                break;
        }
    }

    private void connectToService() {
        connection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName cName, IBinder binder) {
                SongServiceBinder songBinder = (SongServiceBinder) binder;
                service = songBinder.getService();
                bound = true;
                updateGlobalControl();
/*				if (service.isPlaying()) {
					Intent resumePlaySongActivity = new Intent(
							getApplication(), PlaySongActivity.class);
					resumePlaySongActivity.putExtra("resumePlaySongActivity",
							true);
					startActivity(resumePlaySongActivity);
				}*/
            }

            @Override
            public void onServiceDisconnected(ComponentName cName) {
                service = null;
                bound = false;
            }
        };
    }

    private void setUpPager() {
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();

        fragments.add(Fragment.instantiate(this,
                ScreenSlideFragmentPlaylists.class.getName()));
        fragments.add(Fragment.instantiate(this,
                ScreenSlideFragmentMusic.class.getName()));
        fragments.add(Fragment.instantiate(this,
                ScreenSlideFragmentVideo.class.getName()));

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(
                getSupportFragmentManager(), fragments);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(this);
        mPager.setPageTransformer(true, new CubeOutTransformer());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent prefIntent = new Intent(this, PreferencesActivity.class);
                startActivity(prefIntent);
                break;

            case R.id.action_create_playlist:
                DialogFragment dialog = new CreatePlaylistDialogFragment();
                dialog.show(getSupportFragmentManager(), "dialog");
                break;

            case R.id.action_feedback:
                DialogFragment feedbackDialog = new SendFeedbackDialogFragment();
                feedbackDialog.show(getSupportFragmentManager(), "feedbackDialog");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    /* header color selection depending on the current page */
    @Override
    public void onPageSelected(int arg0) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String txtColor = sharedPref.getString(PreferencesActivity.KEY_PREF_TXT_COLOR, "");
        switch (arg0) {
            case 0: // Playlist
                tvHeaderPlaylists.setTextColor(Integer.parseInt(txtColor));
                tvHeaderMusic.setTextColor(getResources().getColor(R.color.black));
                tvHeaderVideo.setTextColor(getResources().getColor(R.color.black));
                break;

            case 1: // Music
                tvHeaderPlaylists.setTextColor(getResources().getColor(
                        R.color.black));
                tvHeaderMusic.setTextColor(Integer.parseInt(txtColor));
                tvHeaderVideo.setTextColor(getResources().getColor(R.color.black));
                break;

            case 2: // Video
                tvHeaderPlaylists.setTextColor(getResources().getColor(
                        R.color.black));
                tvHeaderMusic.setTextColor(getResources().getColor(R.color.black));
                tvHeaderVideo.setTextColor(Integer.parseInt(txtColor));
                break;
        }
    }

    @Override
    public void onCreatePlaylistDialogPositiveClick(String name) {
        try {
            datasource.open();
            datasource.createPlaylist(name);
            datasource.close();
            Toast.makeText(
                    this,
                    getResources().getString(
                            R.string.add_playlist_user_feedback)
                            + " " + name, Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        updatePlaylistFragmentListAdapter();
    }

    @Override
    public void onRemovePlaylistDialogFragmentPositiveClick(String playlistName) {
        updatePlaylistFragmentListAdapter();
        Toast.makeText(
                this,
                getResources()
                        .getString(R.string.remove_playlist_user_feedback)
                        + " " + playlistName, Toast.LENGTH_LONG).show();
        if (playlistName.equals(service.getCurrentPlaylist())) {
            SharedPreferences settings = getSharedPreferences(
                    "play_mode_prefs", 0);
            SharedPreferences.Editor edit = settings.edit();
            edit.putBoolean("from_playlist", false);
            edit.commit();
        }
    }

    @Override
    public void onAddSongToPlaylistDialogPositiveClick(String playlistName,
                                                       boolean success) {
        if (success) {
            updatePlaylistFragmentListAdapter();
            Toast.makeText(
                    this,
                    getResources().getString(
                            R.string.add_song_to_playlist_user_feedback)
                            + " " + playlistName, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(
                    this,
                    getResources().getString(
                            R.string.add_song_to_playlist_success_feedback),
                    Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onUpdatePlaylistDialogPositiveClick(String oldName,
                                                    String newName, boolean success) {
        if (success) {
            updatePlaylistFragmentListAdapter();
            Toast.makeText(
                    this,
                    getResources().getString(
                            R.string.update_playlist_name_user_feedback1)
                            + " "
                            + oldName
                            + " "
                            + getResources()
                            .getString(
                                    R.string.update_playlist_name_user_feedback2)
                            + " " + newName, Toast.LENGTH_LONG).show();

            if (oldName.equals(service.getCurrentPlaylist())) {
                SharedPreferences settings = getSharedPreferences(
                        "play_mode_prefs", 0);
                SharedPreferences.Editor edit = settings.edit();
                edit.putString("playlist_name", newName);
                edit.commit();
                service.setCurrentPlaylist(newName);
            }
        } else {
            Toast.makeText(
                    this,
                    getResources().getString(
                            R.string.update_playlist_success_feedback),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void addNewPlaylistOnPositiveBtnClick() {
        DialogFragment createNewPlaylistDialog = new CreatePlaylistDialogFragment();
        createNewPlaylistDialog.show(getSupportFragmentManager(),
                "createNewPlaylistDialog");
    }

    private void updatePlaylistFragmentListAdapter() {
        ArrayList<String> allPlaylists = new ArrayList<String>();
        try {
            datasource.open();
            allPlaylists = datasource.getAllPlaylists();
            datasource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ListView playlistListView = (ListView) findViewById(R.id.lwPlaylists);
        if (playlistListView != null) {
            PlaylistListAdapter adapter = (PlaylistListAdapter) playlistListView
                    .getAdapter();
            adapter.clear();
            adapter.addAll(allPlaylists);
        }
    }

    private void getVideosAndCreateThumbnails() {
        allVideoPaths = new ArrayList<String>();
        allVideoIDs = new ArrayList<String>();
        loadermanager = getSupportLoaderManager();
        loadermanager.initLoader(1, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        String[] projection = {MediaStore.Video.Media.DATA,
                MediaStore.Video.Media._ID,};

        return new CursorLoader(this,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
        if (!c.isClosed()) {
            while (c.moveToNext()) {
                String path = c.getString(0);
                String id = c.getString(1);
                allVideoPaths.add(path);
                allVideoIDs.add(id);
            }
            BuildVideoThumbnailsTask task = new BuildVideoThumbnailsTask();
            task.execute(allVideoPaths.toArray(new String[allVideoPaths.size()]));
            c.close();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }

    /*
     * This method checks if the device has its SD card mounted and returns true
     * if that's the case, false if not.
     */
    private boolean checkIfMounted() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Create a thumbnail image for each video on the SD card, save the image on
     * the external storage and map them in shared preferences
     */
    private class BuildVideoThumbnailsTask extends
            AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... allPaths) {
            for (int i = 0; i < allPaths.length; i++) {

                String root = Environment.getExternalStorageDirectory()
                        .toString();
                File thumbDir = new File(root
                        + "/TheMediaPlayer/data/video_thumbnails");
                thumbDir.mkdirs();
                File file = new File(thumbDir, allVideoIDs.get(i) + ".jpg");
                Bitmap tmpThumbnail = ThumbnailUtils.createVideoThumbnail(
                        allPaths[i], Thumbnails.MICRO_KIND);
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    if (tmpThumbnail != null) {
                        tmpThumbnail.compress(Bitmap.CompressFormat.JPEG, 90,
                                out);
                    }
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SharedPreferences settings = getSharedPreferences(
                        "videoThumbnail", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(allPaths[i], file.getAbsolutePath());
                editor.commit();
            }

            return null;
        }
    }

}
