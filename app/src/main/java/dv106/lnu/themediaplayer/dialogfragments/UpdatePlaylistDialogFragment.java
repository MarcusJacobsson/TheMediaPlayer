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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import dv106.lnu.themediaplayer.R;
import dv106.lnu.themediaplayer.database.DataSource;

public class UpdatePlaylistDialogFragment extends DialogFragment implements OnClickListener {

	private String oldName;
	private UpdatePlaylistDialogListener mListener;
	private DataSource datasource;
	private EditText etNewName;
	
	public interface UpdatePlaylistDialogListener{
		public void onUpdatePlaylistDialogPositiveClick(String oldName, String newName, boolean success);
	}

    public UpdatePlaylistDialogFragment(){}

    public void setArguments(Bundle args){
        this.oldName = args.getString("oldName");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("oldName", this.oldName);
    }

    @Override
    @NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            this.oldName = savedInstanceState.getString("oldName");
        }
		datasource = new DataSource(getActivity());
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View ourView = inflater.inflate(
				R.layout.dialog_fragment_update_playlist, null);
		TextView tvCurrentName = (TextView) ourView.findViewById(R.id.tvUpdatePlaylistDialogCurrentName);
		etNewName = (EditText) ourView.findViewById(R.id.etUpdatePlaylistDialogNewName);
		tvCurrentName.setText(getResources().getString(R.string.update_playlist_dialog_current_name) + " " + oldName);
		builder.setView(ourView);
		builder.setTitle(getActivity().getResources().getString(R.string.update_playlist_dialog_title));
		builder.setPositiveButton(getActivity().getResources().getString(R.string.update_playlist_dialog_posBtn), this);
		builder.setNegativeButton(getActivity().getResources().getString(R.string.update_playlist_dialog_negBtn), this);
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch(which){
		case DialogInterface.BUTTON_NEGATIVE:
			dialog.dismiss();
			break;
			
		case DialogInterface.BUTTON_POSITIVE:
			String newName = etNewName.getText().toString();
			if(newName != null && newName.trim().isEmpty()){
				Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.update_playlist_dialog_error_feedback_newname), Toast.LENGTH_LONG).show();
			}else{
				try{				
					datasource.open();
					boolean success = datasource.updatePlaylistName(oldName, newName);
					datasource.close();
					mListener.onUpdatePlaylistDialogPositiveClick(oldName, newName, success);
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
			break;
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (UpdatePlaylistDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					activity.toString()
							+ getResources()
									.getString(
											R.string.add_playlist_dialog_error_feedback));
		}
	}

}
