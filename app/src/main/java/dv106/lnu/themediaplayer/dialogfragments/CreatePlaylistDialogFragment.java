package dv106.lnu.themediaplayer.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import dv106.lnu.themediaplayer.R;

public class CreatePlaylistDialogFragment extends DialogFragment implements OnClickListener {

	private EditText etName;
	private CreatePlaylistDialogListener mListener;
	
	public interface CreatePlaylistDialogListener{
		public void onCreatePlaylistDialogPositiveClick(String name);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View ourView = inflater.inflate(
				R.layout.dialog_fragment_create_playlist, null);
		etName = (EditText) ourView.findViewById(R.id.etCreatePlaylistName);
		builder.setView(ourView);
		builder.setTitle(getResources().getString(R.string.add_playlist_dialog_title));
		builder.setPositiveButton(getResources().getString(R.string.add_playlist_dialog_posBtn), this);
		builder.setNegativeButton(getResources().getString(R.string.add_playlist_dialog_negBtn), this);
		
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch(which){
		case DialogInterface.BUTTON_POSITIVE:
			String name = etName.getText().toString();
			if(name != null && name.trim().isEmpty()){
				Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.add_playlist_dialog_feedback), Toast.LENGTH_LONG).show();
			}else{
				mListener.onCreatePlaylistDialogPositiveClick(name);
			}	
			break;
			
		case DialogInterface.BUTTON_NEGATIVE:
			dialog.dismiss();
			break;
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (CreatePlaylistDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					activity.toString()
							+ getResources()
									.getString(
											R.string.add_playlist_dialog_error_feedback));
		}
	}
}
