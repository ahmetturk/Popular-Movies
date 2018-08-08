package com.ahmetroid.popularmovies.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViewsService;

public class SinemateRemoteViewsService extends RemoteViewsService {

    public static Intent getIntent(Context context) {
        return new Intent(context, SinemateRemoteViewsService.class);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new SinemateRemoteViewsFactory(getApplicationContext());
    }
}
