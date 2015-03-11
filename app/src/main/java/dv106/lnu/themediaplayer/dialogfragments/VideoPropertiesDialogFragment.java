package dv106.lnu.themediaplayer.dialogfragments;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import dv106.lnu.themediaplayer.R;
import dv106.lnu.themediaplayer.pojo.Video;
import dv106.lnu.themediaplayer.util.MediaPlayerTimeUtil;

public class VideoPropertiesDialogFragment extends DialogFragment implements OnClickListener {

	private Video video;

    public VideoPropertiesDialogFragment(){}

    public void setArguments(Bundle args){
        this.video = args.getParcelable("video");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("video", this.video);
    }

    @Override
    @NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            this.video = savedInstanceState.getParcelable("video");
        }
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View ourView = inflater.inflate(
				R.layout.dialog_fragment_video_properties, null);
		builder.setView(ourView);
		builder.setTitle(video.getTITLE());
		
		DecimalFormat df = new DecimalFormat("#.#");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		
		TextView tvDuration = (TextView) ourView.findViewById(R.id.tvVideoPropertiesDuration);
		TextView tvResolution = (TextView) ourView.findViewById(R.id.tvVideoPropertiesResolution);
		TextView tvFile = (TextView) ourView.findViewById(R.id.tvVideoPropertiesFileDisplayName);
		TextView tvSize = (TextView) ourView.findViewById(R.id.tvVideoPropertiesSize);
		TextView tvLocation = (TextView) ourView.findViewById(R.id.tvVideoPropertiesLocation);
		TextView tvCreatedDate = (TextView) ourView.findViewById(R.id.tvVideoPropertiesCreateDate);
		
		String resolution = video.getRESOLUTION();
		String file = video.getDISPLAY_NAME();
		String s_size = video.getSIZE();
		String location = video.getDATA();
		String s_date = video.getDATE_ADDED();
		long vidDur = Long.valueOf(video.getDURATION());
		String duration = String.format(MediaPlayerTimeUtil.formatMillisecond(vidDur));
		double l_size = Double.valueOf(s_size);
		double sizeInMb = l_size/(1024*1024);
		
		long l_date = Long.valueOf(s_date);
		long l_date_ms = l_date*1000;
		Date theDate = new Date();
		theDate.setTime(l_date_ms);
		String date = sdf.format(theDate);
		
		tvDuration.setText(duration);
		tvResolution.setText(resolution);
		tvFile.setText(file);
		tvSize.setText(df.format(sizeInMb) + " " + getResources().getString(R.string.video_properties_dialog_mb));
		tvLocation.setText(location);
		tvCreatedDate.setText(date);
		builder.setPositiveButton(getResources().getString(R.string.video_properties_dialog_posBtn), this);
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch(which){
		case DialogInterface.BUTTON_POSITIVE:
			dialog.dismiss();
			break;
		}
	}
	
}
