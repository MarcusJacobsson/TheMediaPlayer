package dv106.lnu.themediaplayer.widgetprovider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import dv106.lnu.themediaplayer.MainActivity;
import dv106.lnu.themediaplayer.R;
import dv106.lnu.themediaplayer.preferences.PreferencesActivity;
import dv106.lnu.themediaplayer.service.SongService;

public class SongAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

			/* set on click for the AppWidget layout */
            Intent layoutIntent = new Intent(context, MainActivity.class);
            layoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent layoutPendingIntent = PendingIntent.getActivity(
                    context, 0, layoutIntent, 0);
            RemoteViews widgetView = new RemoteViews(context.getPackageName(),
                    R.layout.appwidget_homescreen);
            widgetView.setOnClickPendingIntent(R.id.widget_layout,
                    layoutPendingIntent);

            //Color theme
            SharedPreferences sharedPref = PreferenceManager
                    .getDefaultSharedPreferences(context);
            String txtColorString = sharedPref.getString(
                    PreferencesActivity.KEY_PREF_TXT_COLOR, "");
            int txtColor = Integer.parseInt(txtColorString);
            Bitmap b = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            c.drawColor(txtColor);
            widgetView.setTextColor(R.id.tvAppWidgetArtist, txtColor);
            widgetView.setTextColor(R.id.tvAppWidgetTitle, txtColor);
            widgetView.setImageViewBitmap(R.id.ivAppWidgetHS, b);
            widgetView.setImageViewBitmap(R.id.ivAppWidgetVS, b);

			/* set on click for the AppWidget 'next' button */
            Intent playNextIntent = new Intent(context, SongService.class);
            playNextIntent.putExtra("action", "next");
            PendingIntent playNextPendingIntent = PendingIntent.getService(
                    context, 1, playNextIntent, 0);
            widgetView.setOnClickPendingIntent(R.id.ibAppWidgetNext,
                    playNextPendingIntent);

			/* set on click for the AppWidget 'play/pause' button */
            Intent playOrPauseIntent = new Intent(context, SongService.class);
            playOrPauseIntent.putExtra("action", "playOrPause");
            PendingIntent playOrPausePendingIntent = PendingIntent.getService(
                    context, 2, playOrPauseIntent, 0);
            widgetView.setOnClickPendingIntent(R.id.ibAppWidgetPlayPause,
                    playOrPausePendingIntent);

			/* set on click for the AppWidget 'previous' button */
            Intent playPreviousIntent = new Intent(context, SongService.class);
            playPreviousIntent.putExtra("action", "previous");
            PendingIntent playPreviousPendingIntent = PendingIntent.getService(
                    context, 3, playPreviousIntent, 0);
            widgetView.setOnClickPendingIntent(R.id.ibAppWidgetPrevious,
                    playPreviousPendingIntent);


            appWidgetManager.updateAppWidget(appWidgetId, widgetView);

        }
    }
}
