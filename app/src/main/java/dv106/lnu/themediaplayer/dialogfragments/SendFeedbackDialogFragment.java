package dv106.lnu.themediaplayer.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;
import dv106.lnu.themediaplayer.R;

public class SendFeedbackDialogFragment extends DialogFragment implements OnClickListener {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setTitle(getResources().getString(R.string.send_feedback_dialog_fragment_title));
		builder.setMessage(getResources().getString(R.string.send_feedback_dialog_fragment_msg));
		builder.setPositiveButton(getResources().getString(R.string.send_feedback_dialog_fragment_pos_btn), this);
		builder.setNegativeButton(getResources().getString(R.string.send_feedback_dialog_fragment_neg_btn), this);
		
		return builder.create();
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch(which){
		case DialogInterface.BUTTON_NEGATIVE:
			dialog.dismiss();
			break;
			
		case DialogInterface.BUTTON_POSITIVE:
			/* Create the Intent */
			final Intent emailIntent = new Intent(Intent.ACTION_SEND);

			/* Fill it with Data */
			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"marcus.jacobsson@ottomatech.se"});
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "The Media Player feedback");
			//emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Text");

			try {
			    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			} catch (android.content.ActivityNotFoundException ex) {
			    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

}
