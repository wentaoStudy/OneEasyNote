package com.wentao.xrichtextdemo.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.Toast;

//import com.sendtion.xrichtext.R;
import com.sendtion.xrichtextdemo.R;
import com.wentao.xrichtextdemo.bean.Note;
import com.wentao.xrichtextdemo.ui.MainActivity;
import com.wentao.xrichtextdemo.ui.NewActivity;
import com.wentao.xrichtextdemo.ui.NoteActivity;
import com.wentao.xrichtextdemo.ui.NoteListFragment;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class MyAppWidgetProvider extends AppWidgetProvider {

    public static final String COLLECTION_VIEW_ACTION = "com.example.COLLECTION_VIEW_ACTION";
    public static final String COLLECTION_VIEW_EXTRA = "com.example.COLLECTION_VIEW_EXTRA";
    private List<Note> mNotes;

    /*public static final String CHANGE_IMAGE = "com.example.joy.action.CHANGE_IMAGE";


    private RemoteViews mRemoteViews;
    private ComponentName mComponentName;

    private int[] imgs = new int[]{
            R.mipmap.app_icon,
            R.mipmap.app_icon,
            R.mipmap.app_icon,
            R.mipmap.app_icon,
            R.mipmap.app_icon,
            R.mipmap.app_icon
    };*/

    /*static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_app_widget_provider);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }*/

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            // 获取AppWidget对应的视图

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.my_app_widget_provider);
            remoteViews.setTextViewText(R.id.appwidget_text,"笔记");
            NoteListFragment nf=new NoteListFragment();
            mNotes=nf.getLatestedNoteList();
            Intent intentnote = new Intent(context, NoteActivity.class);
            Bundle bundle1 = new Bundle();
            bundle1.putSerializable("note",mNotes.get(0));
            intentnote.putExtra("data", bundle1);
            PendingIntent pi = PendingIntent.getActivity(context, 200, intentnote, PendingIntent.FLAG_CANCEL_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.appwidget_text, pi);
            // 设置 “ListView” 的adapter。
            // (01) intent: 对应启动 ListWidgetService(RemoteViewsService) 的intent
            // (02) setRemoteAdapter: 设置 gridview的适配器
            //    通过setRemoteAdapter将ListView和ListWidgetService关联起来，
            //    以达到通过 ListWidgetService 更新 ListView的目的
            Intent serviceIntent = new Intent(context, ListViewService.class);
            remoteViews.setRemoteAdapter(R.id.notelist, serviceIntent);


            // 设置响应 “ListView” 的intent模板
            // 说明：“集合控件(如GridView、ListView、StackView等)”中包含很多子元素，如GridView包含很多格子。
            //     它们不能像普通的按钮一样通过 setOnClickPendingIntent 设置点击事件，必须先通过两步。
            //        (01) 通过 setPendingIntentTemplate 设置 “intent模板”，这是比不可少的！
            //        (02) 然后在处理该“集合控件”的RemoteViewsFactory类的getViewAt()接口中 通过 setOnClickFillInIntent 设置“集合控件的某一项的数据”
            Intent gridIntent = new Intent();

            gridIntent.setAction(COLLECTION_VIEW_ACTION);
            gridIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gridIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            // 设置intent模板
            remoteViews.setPendingIntentTemplate(R.id.notelist, pendingIntent);
            // 调用集合管理器对集合进行更新
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        /*
        mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.my_app_widget_provider);
        mRemoteViews.setTextViewText(R.id.appwidget_text, "笔记");
        Intent skipIntent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 200, skipIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        //mRemoteViews.setOnClickPendingIntent(R.id.btn_test, pi);*/

        // 设置 ListView 的adapter。
        // (01) intent: 对应启动 ListViewService(RemoteViewsService) 的intent
        // (02) setRemoteAdapter: 设置 ListView 的适配器
        // 通过setRemoteAdapter将 ListView 和ListViewService关联起来，
        // 以达到通过 GridWidgetService 更新 gridview 的目的
        /*
        Intent lvIntent = new Intent(context, ListViewService.class);//?
        mRemoteViews.setRemoteAdapter(R.id.notelist, lvIntent);
        mRemoteViews.setEmptyView(R.id.notelist,android.R.id.empty);*/

        // 设置响应 ListView 的intent模板
        // 说明：“集合控件(如GridView、ListView、StackView等)”中包含很多子元素，如GridView包含很多格子。
        // 它们不能像普通的按钮一样通过 setOnClickPendingIntent 设置点击事件，必须先通过两步。
        // (01) 通过 setPendingIntentTemplate 设置 “intent模板”，这是比不可少的！
        // (02) 然后在处理该“集合控件”的RemoteViewsFactory类的getViewAt()接口中 通过 setOnClickFillInIntent 设置“集合控件的某一项的数据”

        /*
         * setPendingIntentTemplate 设置pendingIntent 模板
         * setOnClickFillInIntent   可以将fillInIntent 添加到pendingIntent中
         */
        /*
        Intent toIntent = new Intent(CHANGE_IMAGE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 200, toIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setPendingIntentTemplate(R.id.notelist, pendingIntent);


        mComponentName = new ComponentName(context, MyAppWidgetProvider.class);
        appWidgetManager.updateAppWidget(mComponentName, mRemoteViews); */
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if (action.equals(COLLECTION_VIEW_ACTION)) {
            // 接受“ListView”的点击事件的广播
            int type = intent.getIntExtra("Type", 0);
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            int index = intent.getIntExtra(COLLECTION_VIEW_EXTRA, 0);
            Bundle bundle = intent.getBundleExtra("data");
            Note note = (Note) bundle.getSerializable("note");
            switch (type) {
                case 0:

                   Toast.makeText(context, "item" + index, Toast.LENGTH_SHORT).show();
                    NoteListFragment nf=new NoteListFragment();
                    mNotes=nf.getLatestedNoteList();
                    Intent intentnote = new Intent(context, NoteActivity.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putSerializable("note",note);
                    intentnote.putExtra("data", bundle1);
                    context.startActivity(intentnote);

                    break;
                case 1:
                    Toast.makeText(context, "lock"+index, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(context, "unlock"+index, Toast.LENGTH_SHORT).show();
                    break;
            }
        } /*else if (action.equals(REFRESH_WIDGET)) {
            // 接受“bt_refresh”的点击事件的广播
            Toast.makeText(context, "刷新...", Toast.LENGTH_SHORT).show();
            final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            final ComponentName cn = new ComponentName(context,ListWidgetProvider.class);
            ListRemoteViewsFactory.refresh();
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn),R.id.lv_device);
            mHandler.postDelayed(runnable,2000);
            showLoading(context);
        }*/
        super.onReceive(context, intent);

        /*super.onReceive(context, intent);
        if(TextUtils.equals(CHANGE_IMAGE,intent.getAction())){
            Bundle extras = intent.getExtras();
            int position = extras.getInt(ListViewService.INITENT_DATA);
            mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.my_app_widget_provider);
            mRemoteViews.setImageViewResource(R.id.notelist, imgs[position]);
            mComponentName = new ComponentName(context, MyAppWidgetProvider.class);
            AppWidgetManager.getInstance(context).updateAppWidget(mComponentName, mRemoteViews);
        }*/
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

