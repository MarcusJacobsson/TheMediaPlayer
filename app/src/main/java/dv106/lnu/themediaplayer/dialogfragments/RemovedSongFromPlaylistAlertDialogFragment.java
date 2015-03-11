package dv106.lnu.themediaplayer.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import dv106.lnu.themediaplayer.R;

public class RemovedSongFromPlaylistAlertDialogFragment extends DialogFragment implements OnClickListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.removed_song_from_playlist_dialog_fragment_title));
        builder.setMessage(getResources().getString(R.string.removed_song_from_playlist_dialog_fragment_msg));
        builder.setNegativeButton(getResources().getString(R.string.removed_song_from_playlist_dialog_fragment_negBtn), this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_NEGATIVE:
                dialog.dismiss();
                break;
        }
    }
}
