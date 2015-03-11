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

import dv106.lnu.themediaplayer.R;
import dv106.lnu.themediaplayer.database.DataSource;

public class RemovePlaylistDialogFragment extends DialogFragment implements OnClickListener {

    private DataSource datasource;
    private String playlistName;
    private RemovePlaylistDialogListener mListener;

    public RemovePlaylistDialogFragment(){}

    public void setArguments(Bundle args){
        this.playlistName = args.getString("playlistName");
    }

    public interface RemovePlaylistDialogListener {
        public void onRemovePlaylistDialogFragmentPositiveClick(String playlistName);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (RemovePlaylistDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    activity.toString()
                            + getResources()
                            .getString(
                                    R.string.remove_playlist_dialog_fragment_error_feedback));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("playlistName", playlistName);
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            this.playlistName = savedInstanceState.getString("playlistName");
        }
        datasource = new DataSource(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.remove_playlist_dialog_fragment_title) + " " + playlistName);
        builder.setMessage(getResources().getString(R.string.remove_playlist_dialog_fragment_msg));
        builder.setPositiveButton(getResources().getString(R.string.remove_playlist_dialog_fragment_posBtn), this);
        builder.setNegativeButton(getResources().getString(R.string.remove_playlist_dialog_fragment_negBtn), this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_NEGATIVE:
                dialog.dismiss();
                break;

            case DialogInterface.BUTTON_POSITIVE:
                try {
                    datasource.open();
                    datasource.removePlaylist(playlistName);
                    datasource.close();
                    mListener.onRemovePlaylistDialogFragmentPositiveClick(playlistName);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
