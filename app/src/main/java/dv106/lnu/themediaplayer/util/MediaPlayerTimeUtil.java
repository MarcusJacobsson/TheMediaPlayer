package dv106.lnu.themediaplayer.util;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MediaPlayerTimeUtil {
	/** This method will format the time given in milliseconds to a string in the 00:00 format */
	public static String formatMillisecond(long milliseconds){		
		return String.format(Locale.getDefault(),"%02d:%02d", 
				TimeUnit.MILLISECONDS.toMinutes(milliseconds) -  
				TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)), 
				TimeUnit.MILLISECONDS.toSeconds(milliseconds) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
	}
}
