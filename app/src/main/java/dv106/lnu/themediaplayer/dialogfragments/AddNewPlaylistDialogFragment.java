package dv106.lnu.themediaplayer.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import dv106.lnu.themediaplayer.R;

public class AddNewPlaylistDialogFragment extends DialogFragment implements OnClickListener {
	
	private AddNewPlaylistDialogListener mListener;
	
	public interface AddNewPlaylistDialogListener{
		public void addNewPlaylistOnPositiveBtnClick();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (AddNewPlaylistDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					activity.toString()
							+ getResources()
									.getString(
											R.string.add_new_playlist_dialog_fragment_feedback));
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setTitle(getResources().getString(R.string.add_new_playlist_dialog_fragment_title));
		builder.setMessage(getResources().getString(R.string.add_new_playlist_dialog_fragment_msg));
		builder.setNegativeButton(getResources().getString(R.string.add_new_playlist_dialog_fragment_negbtn), this);
		builder.setPositiveButton(getResources().getString(R.string.add_new_playlist_dialog_fragment_posbtn), this);
		
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch(which){
		case DialogInterface.BUTTON_POSITIVE:
			mListener.addNewPlaylistOnPositiveBtnClick();
			break;
			
		case DialogInterface.BUTTON_NEGATIVE:
			dialog.dismiss();
			break;
		}
	}
}
