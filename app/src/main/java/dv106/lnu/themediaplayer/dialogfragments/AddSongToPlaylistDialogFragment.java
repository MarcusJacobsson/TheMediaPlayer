package dv106.lnu.themediaplayer.dialogfragments;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.SQLException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import dv106.lnu.themediaplayer.R;
import dv106.lnu.themediaplayer.database.DataSource;

public class AddSongToPlaylistDialogFragment extends DialogFragment implements
		OnClickListener {

	private DataSource datasource;
	private ArrayList<String> allPlaylists;
	private String songPath;
	private String[] allPlaylistsArray;
	private AddSongToPlaylistDialogListener mListener;

    public AddSongToPlaylistDialogFragment(){}

    public void setArguments(Bundle args){
        this.songPath = args.getString("songPath");
    }
	
	public interface AddSongToPlaylistDialogListener{
		public void onAddSongToPlaylistDialogPositiveClick(String playlistName, boolean success);
	}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("songPath", songPath);
    }

    @Override
    @NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            this.songPath = savedInstanceState.getString("songPath");
        }
		datasource = new DataSource(getActivity());
		try {
			datasource.open();
			allPlaylists = datasource.getAllPlaylists();
			datasource.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		allPlaylistsArray = allPlaylists
				.toArray(new String[allPlaylists.size()]);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getResources().getString(
				R.string.add_song_to_playlist_dialog_fragment_title));
		builder.setPositiveButton(
				getResources().getString(R.string.add_playlist_dialog_posBtn),
				this);
		builder.setNegativeButton(
				getResources().getString(R.string.add_playlist_dialog_negBtn),
				this);
		builder.setSingleChoiceItems(allPlaylistsArray, -1, this);

		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
			if(selectedPosition != -1){
				String playlistName = allPlaylists.get(selectedPosition);
				try{
					datasource.open();
					boolean success = datasource.addSongToPlaylist(songPath, playlistName);
					datasource.close();
					mListener.onAddSongToPlaylistDialogPositiveClick(playlistName, success);
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
			break;

		case DialogInterface.BUTTON_NEGATIVE:
			dialog.dismiss();
			break;

		default:
			
			break;
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (AddSongToPlaylistDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					activity.toString()
							+ getResources()
									.getString(
											R.string.add_song_to_playlist_dialog_fragment_error_feedback));
		}
	}

}
