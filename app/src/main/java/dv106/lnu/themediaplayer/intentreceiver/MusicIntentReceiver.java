package dv106.lnu.themediaplayer.intentreceiver;

import dv106.lnu.themediaplayer.service.SongService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;

public class MusicIntentReceiver extends android.content.BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(
				android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
			// signal the service to stop playback
            Intent pauseIntent = new Intent(context, SongService.class);
            pauseIntent.putExtra("action", "pause");
            context.startService(pauseIntent);
		}
	}
}