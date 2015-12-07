package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

/**
 * Created by sanyukta on 12/3/15.
 */
public class DetailWidgetProvider extends AppWidgetProvider {

    public static final String EXTRA_ITEM = "position";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {

            // Instantiate the RemoteViews object for the app widget layout.
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_football_detail);

            Intent intent = new Intent(context, DetailWidgetRemoteViewsService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            //Set up the RemoteViews object to use a RemoteViews adapter to connect
            //to a RemoteViewsService  through the specified intent.

            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            {
                setRemoteAdapter(context,remoteViews, intent);
            }
            else
            {
                setRemoteAdapterVII(context, remoteViews, intent);
            }
            //remoteViews.setRemoteAdapter(R.id.widget_list, intent);

            // The empty view to be displayed when the collection has no items.
            remoteViews.setEmptyView(R.id.widget_list, R.id.widget_empty_view);


            // Set pending intent template to launch app when match is selected
            Intent launchIntent = new Intent(context, MainActivity.class);
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.widget_list, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews remoteViews, Intent intent)
    {
        remoteViews.setRemoteAdapter(R.id.widget_list,intent);
    }

    @SuppressWarnings("deprecation")
    private void setRemoteAdapterVII(Context context, @NonNull final RemoteViews remoteViews, Intent intent)
    {
        remoteViews.setRemoteAdapter(0, R.id.widget_list,intent);
    }
}
