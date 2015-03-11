package dv106.lnu.themediaplayer.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.SQLException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import dv106.lnu.themediaplayer.R;
import dv106.lnu.themediaplayer.database.DataSource;
import dv106.lnu.themediaplayer.listadapters.SongListAdapter;
import dv106.lnu.themediaplayer.pojo.Song;

public class AddTrackToPlaylistDialogFragment extends DialogFragment implements
        OnClickListener {

    private SongListAdapter adapter;
    private String playlistName;
    private ArrayList<Song> allSongs;
    private DataSource datasource;
    private AddTrackToPlaylistDialogListener mListener;

    public interface AddTrackToPlaylistDialogListener {
        public void AddTrackToPlaylistDialogClick();
    }

    public void setArguments(Bundle args){
        this.allSongs = args.getParcelableArrayList("allSongs");
        this.playlistName = args.getString("playlistName");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("allSongs", this.allSongs);
        outState.putString("playlistName", this.playlistName);
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            this.allSongs = savedInstanceState.getParcelableArrayList("allSongs");
            this.playlistName = savedInstanceState.getString("playlistName");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(
                R.string.add_track_to_playlist_dialog_fragment_title));
        builder.setNegativeButton(
                getResources().getString(
                        R.string.add_track_to_playlist_dialog_fragment_neg_btn),
                this);

        adapter = new SongListAdapter(getActivity(), allSongs);

        builder.setAdapter(adapter, this);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_NEGATIVE:
                dialog.dismiss();
                break;
            default:
                String songPath = allSongs.get(which).getDATA();
                String songName = allSongs.get(which).getDISPLAY_NAME();
                try {
                    datasource = new DataSource(getActivity());
                    datasource.open();
                    datasource.addSongToPlaylist(songPath, playlistName);
                    datasource.close();
                    mListener.AddTrackToPlaylistDialogClick();
                    Toast.makeText(
                            getActivity(),
                            getResources()
                                    .getString(
                                            R.string.add_track_to_playlist_dialog_fragment_feedback_added)
                                    + " "
                                    + songName
                                    + " "
                                    + getResources()
                                    .getString(
                                            R.string.add_track_to_playlist_dialog_fragment_feedback_to)
                                    + " " + playlistName, Toast.LENGTH_SHORT)
                            .show();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (AddTrackToPlaylistDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    activity.toString()
                            + getResources()
                            .getString(
                                    R.string.add_song_to_playlist_dialog_fragment_error_feedback));
        }
    }
}
