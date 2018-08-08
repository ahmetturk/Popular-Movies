package com.ahmetroid.popularmovies.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.ahmetroid.popularmovies.R;
import com.ahmetroid.popularmovies.ui.MovieListActivity;

/**
 * Implementation of App Widget functionality.
 */
public class SinemateAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MovieListActivity.class), 0);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.sinemate_app_widget);

        views.setTextViewText(R.id.widget_textview_title, context.getString(R.string.widget_title));

        views.setRemoteAdapter(R.id.widget_listview_movies,
                SinemateRemoteViewsService.getIntent(context));

        views.setPendingIntentTemplate(R.id.widget_listview_movies, pendingIntent);

        views.setOnClickPendingIntent(R.id.widget_parent_layout, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}

