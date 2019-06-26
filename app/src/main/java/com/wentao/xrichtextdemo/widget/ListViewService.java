package com.wentao.xrichtextdemo.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sendtion.xrichtextdemo.R;
import com.wentao.xrichtextdemo.bean.Note;
import com.wentao.xrichtextdemo.ui.NoteActivity;
import com.wentao.xrichtextdemo.ui.NoteListFragment;

import java.util.ArrayList;
import java.util.List;

public class ListViewService extends RemoteViewsService {
    public static final String INITENT_DATA = "extra_data";
//    public static  List<Note> mNotes;

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this, intent);
    }

    private class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private final static String TAG="Widget";
        private Context mContext;
        private int mAppWidgetId;

        //private List<String> mList = new ArrayList<>();
        private List<Note> mNotes;

        public ListRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        public RemoteViews getViewAt(int position) {

            // 获取 item_widget_device.xml 对应的RemoteViews
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.item_widget_note);

            // 设置 第position位的“视图”的数据
            Note note = mNotes.get(position);
            //  rv.setImageViewResource(R.id.iv_lock, ((Integer) map.get(IMAGE_ITEM)).intValue());
            rv.setTextViewText(R.id.titleview, note.getTitle());
            rv.setTextViewText(R.id.dateview, note.getUpdateTime());
            rv.setTextViewText(R.id.contentview, note.getContent());

            Intent intentnote = new Intent(mContext, NoteActivity.class);
            Bundle bundle1 = new Bundle();
            bundle1.putSerializable("note",mNotes.get(0));
            intentnote.putExtra("data", bundle1);
            PendingIntent pi = PendingIntent.getActivity(mContext, 200, intentnote, PendingIntent.FLAG_CANCEL_CURRENT);
            rv.setOnClickPendingIntent(R.id.appwidget_text, pi);



//            // 设置 第position位的“视图”对应的响应事件
//            Intent fillInIntent = new Intent();
//            fillInIntent.putExtra("Type", 0);
//            fillInIntent.setPackage("com.wentao.xrichtextdemo.MyApplication");
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("note", mNotes.get(position));
//            fillInIntent.putExtra("data", bundle);
//            fillInIntent.putExtra(MyAppWidgetProvider.COLLECTION_VIEW_EXTRA, position);
//            rv.setOnClickFillInIntent(R.id.titleview, fillInIntent);
//            rv.setOnClickFillInIntent(R.id.dateview, fillInIntent);
//            rv.setOnClickFillInIntent(R.id.contentview, fillInIntent);

            /*
            Intent lockIntent = new Intent();
            lockIntent.putExtra(MyAppWidgetProvider.COLLECTION_VIEW_EXTRA, position);
            lockIntent.putExtra("Type", 1);
            rv.setOnClickFillInIntent(R.id.contentview, lockIntent);*/

            /*Intent unlockIntent = new Intent();
            unlockIntent.putExtra("Type", 2);
            unlockIntent.putExtra(MyAppWidgetProvider.COLLECTION_VIEW_EXTRA, position);
            rv.setOnClickFillInIntent(R.id.iv_unlock, unlockIntent);*/

            return rv;

            /*
            RemoteViews views = new RemoteViews(mContext.getPackageName(), android.R.layout.simple_list_item_1);
            views.setTextViewText(android.R.id.text1, mList.get(position));

            Bundle extras = new Bundle();
            extras.putInt(ListViewService.INITENT_DATA, position);
            Intent changeIntent = new Intent();
            changeIntent.setAction(MyAppWidgetProvider.CHANGE_IMAGE);
            changeIntent.putExtras(extras);*/

            /* android.R.layout.simple_list_item_1 --- id --- text1
             * listview的item click：将 changeIntent 发送，
             * changeIntent 它默认的就有action 是provider中使用 setPendingIntentTemplate 设置的action*/

            /*Intent intent = new Intent(mContext, NoteActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("note", mNotes.get(position));
            intent.putExtra("data", bundle);
            views.setOnClickFillInIntent(android.R.id.text1, intent);
            //startActivity(intent);
            return views;*/
        }

        private void initListViewData() {
            NoteListFragment nf=new NoteListFragment();
            mNotes=nf.getLatestedNoteList();
        }

        @Override
        public void onCreate() {
            Log.e(TAG,"onCreate");
            initListViewData();

            /*
            NoteListFragment nf=new NoteListFragment();
            mNotes=nf.getLatestedNoteList();
            int count=mNotes.size();
            for(int i=0;i<count;i++){
                mList.add(mNotes.get(1).getContent());
            }*/
            /*
            mList.add("一");
            mList.add("二");
            mList.add("三");
            mList.add("四");
            mList.add("五");
            mList.add("六");*/
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {
            mNotes.clear();
        }

        @Override
        public int getCount() {
            return mNotes.size();
        }




        /* 在更新界面的时候如果耗时就会显示 正在加载... 的默认字样，但是你可以更改这个界面
         * 如果返回null 显示默认界面
         * 否则 加载自定义的，返回RemoteViews
         */
        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}