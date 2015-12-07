package barqsoft.footballscores.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Utility;
import barqsoft.footballscores.data.ScoresContract;

/**
 * Created by sanyukta on 12/3/15.
 */
public class DetailWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String[] SCORES_COLUMNS = {
            ScoresContract.scores_table.MATCH_ID,
            ScoresContract.scores_table.DATE_COL,
            ScoresContract.scores_table.TIME_COL,
            ScoresContract.scores_table.HOME_COL,
            ScoresContract.scores_table.AWAY_COL,
            ScoresContract.scores_table.HOME_GOALS_COL,
            ScoresContract.scores_table.AWAY_GOALS_COL,
            ScoresContract.scores_table.LEAGUE_COL,
            ScoresContract.scores_table.MATCH_DAY
    };

    private static final int INDEX_MATCH_ID = 0;
    private static final int INDEX_DATE_COL = 1;
    private static final int INDEX_TIME_COL = 2;
    private static final int INDEX_HOME_COL = 3;
    private static final int INDEX_AWAY_COL = 4;
    private static final int INDEX_HOME_GOALS_COL = 5;
    private static final int INDEX_AWAY_GOALS_COL = 6;
    private static final int INDEX_LEAGUE_COL = 7;
    private static final int INDEX_MATCH_DAY = 8;

    private Cursor cursor = null;
    private Context mContext;

    public DetailWidgetFactory(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (cursor != null) {
            cursor.close();
        }

        final long identityToken = Binder.clearCallingIdentity();
        Uri uri = ScoresContract.scores_table.buildScoreWithDate();
        //java.util.Date fragmentdate = new java.util.Date(System.currentTimeMillis()-((2)*86400000));
        //cursor = mContext.getContentResolver().query(uri, SCORES_COLUMNS, null, new String[]{new SimpleDateFormat("yyyy-MM-dd").format(fragmentdate)}, null);

        cursor = mContext.getContentResolver().query(uri,
                SCORES_COLUMNS,
                null,
                new String[]{new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()))},
                null);
                //ScoresContract.scores_table.HOME_GOALS_COL + " ASC");

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    @Override
    public int getCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        if (position == AdapterView.INVALID_POSITION || cursor == null || !cursor.moveToPosition(position)) {
            return null;
        }

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_detail_list_item);

        String homeTeam = cursor.getString(INDEX_HOME_COL);
        int ihomeGoals = cursor.getInt(INDEX_HOME_GOALS_COL);
        String awayTeam = cursor.getString(INDEX_AWAY_COL);
        int iawayGoals = cursor.getInt(INDEX_AWAY_GOALS_COL);
        String matchTime = cursor.getString(INDEX_TIME_COL);

        String goals = Utility.getScores(ihomeGoals, iawayGoals);

//        String homeGoals = String.valueOf(ihomeGoals);
//        String awayGoals = String.valueOf(iawayGoals);
//        if(ihomeGoals<0)
//        {
//            homeGoals="-";
//        }
//        if(iawayGoals<0)
//        {
//            awayGoals="-";
//        }


        // Add the data to the RemoteViews
        remoteViews.setTextViewText(R.id.widget_home, homeTeam);
        remoteViews.setTextViewText(R.id.widget_away, awayTeam);
        remoteViews.setTextViewText(R.id.widget_goals, goals);
        remoteViews.setTextViewText(R.id.widget_match_time, matchTime);

        // Set FillIntent to be used in the PendingIntentTemplate
        Bundle bundle = new Bundle();
        bundle.putInt(DetailWidgetProvider.EXTRA_ITEM, cursor.getInt(INDEX_MATCH_ID));
        Intent fillIntent = new Intent();
        fillIntent.putExtras(bundle);
        remoteViews.setOnClickFillInIntent(R.id.widget, fillIntent);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.widget_detail_list_item);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if (cursor.moveToPosition(position))
            return cursor.getLong(INDEX_MATCH_ID);
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

