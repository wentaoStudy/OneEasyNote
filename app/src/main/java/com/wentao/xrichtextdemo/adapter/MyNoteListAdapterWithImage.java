package com.wentao.xrichtextdemo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sendtion.xrichtextdemo.R;
import com.wentao.xrichtextdemo.bean.Note;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.wenming.library.upload.UploadService.TAG;

/**
 * 作者：Sendtion on 2016/10/21 0021 16:59
 * 邮箱：sendtion@163.com
 * 博客：http://sendtion.cn
 * 描述：笔记列表适配器
 */

public class MyNoteListAdapterWithImage extends RecyclerView.Adapter<MyNoteListAdapterWithImage.ViewHolder>
        implements View.OnClickListener, View.OnLongClickListener {
    private Context mContext;
    private List<Note> mNotes;
    private OnRecyclerViewItemClickListener mOnItemClickListener ;
    private OnRecyclerViewItemLongClickListener mOnItemLongClickListener ;

    public MyNoteListAdapterWithImage() {
        mNotes = new ArrayList<>();
    }

    public void setmNotes(List<Note> notes) {
        this.mNotes = notes;
    }

    //deal with the iamge note
    @Override
    public int getItemViewType(int position) {
        if(getImagesAddress(mNotes.get(position).getContent()).length != 0)
        {
            Log.d(TAG, "getItemViewType: " + mNotes.get(position).getContent());
            Log.d(TAG, "getItemViewType: " + getImagesAddress(mNotes.get(position).getContent())[0]);
            return 1;
        }
        else return 0;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,(Note)v.getTag());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnItemLongClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemLongClickListener.onItemLongClick(v,(Note)v.getTag());
        }
        return true;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, Note note);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View view, Note note);
    }

    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Log.i(TAG, "###onCreateViewHolder: ");
        //inflate(R.layout.list_item_record,parent,false) 如果不这么写，cardview不能适应宽度
        mContext = parent.getContext();
        if(viewType == 0){
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_note,parent,false);
            //将创建的View注册点击事件
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolder(view);
        }else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_note_with_image,parent,false);
            //将创建的View注册点击事件
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Log.i(TAG, "###onBindViewHolder: ");
        Log.d(TAG, "UpdateTime: " + mNotes.get(position).getUpdateTime());
        final Note note = mNotes.get(position);
        //将数据保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(note);
        //Log.e("adapter", "###record="+record);
        holder.tv_list_title.setText(note.getTitle());
        holder.tv_list_summary.setText(getShortCut(note.getContent()));
        holder.tv_list_time.setText(note.getCreateTime());
        holder.tv_list_group.setText(note.getGroupName());
        if(getImagesAddress(mNotes.get(position).getContent()).length != 0){
            File file = new File(getImagesAddress(mNotes.get(position).getContent())[0]);
            Glide.with(mContext).load(file).override(66,66).into(holder.tv_list_image);
        }
    }

    @Override
    public int getItemCount() {
        //Log.i(TAG, "###getItemCount: ");
        if (mNotes != null && mNotes.size()>0){
            return mNotes.size();
        }
        return 0;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_list_title;//笔记标题
        public TextView tv_list_summary;//笔记摘要
        public TextView tv_list_time;//创建时间
        public TextView tv_list_group;//笔记分类
        public CardView card_view_note;
        public ImageView tv_list_image;

        public ViewHolder(View view){
            super(view);
            card_view_note = (CardView) view.findViewById(R.id.card_view_note);
            tv_list_title = (TextView) view.findViewById(R.id.tv_list_title);
            tv_list_summary = (TextView) view.findViewById(R.id.tv_list_summary);
            tv_list_time = (TextView) view.findViewById(R.id.tv_list_time);
            tv_list_group = (TextView) view.findViewById(R.id.tv_list_group);
            if( view.findViewById(R.id.tv_list_image) != null){
                tv_list_image = (ImageView) view.findViewById(R.id.tv_list_image);
            }
        }
    }




    //Method By WenTao

    public static int[]  getIndexOfImage(String note_content){
        int image_beginindex = 0;
        int image_endindex = -1;
        int count = 0;
        int indexs[] = new int[101];
        //这里调用indexs[]的第一个作为数组的存放个数，考虑到图片的存放数量，暂时设计用户不会
        //一次性传入50张以上的照片，所以设计大小为100
        while (image_beginindex != -1){
            indexs[0] = count;
            image_beginindex = note_content.indexOf("<img" , image_endindex);
            image_endindex = note_content.indexOf("/>" , image_beginindex);
            if(image_beginindex == -1)
                break;
            count ++;
            indexs[2*count] = image_endindex;
            indexs[2*count -1] = image_beginindex;

            image_beginindex = image_beginindex;
        }
        return indexs;
    }

    public static String[] getImagesContent(String content){
        int [] a = getIndexOfImage(content);
        String Result[] = new String[a[0]];
        for(int i = 0 ; i <  a[0] ; i++) {
            Result[i] = content.substring(a[(i+1)*2 -1] , a[(i+1)*2] + 2);
        }
        return Result;
    }

    public static String[] getImagesAddress(String contents){
        String [] mycontent = getImagesContent(contents);
        String [] Result = new String[mycontent.length];
        int i = 0;
        for (String content : mycontent){
            int srcIndex = content.indexOf("src");
            int contentBeginIndex = content.indexOf("\"" , srcIndex );
            int contentEndIndex = content.indexOf("\"" , contentBeginIndex + 1);
            Result[i++] = content.substring(contentBeginIndex + 1 , contentEndIndex );
        }
        return Result;
    }

    //获取没有图片内容的内容
    public static String getContentWithNoImage(String note_content){
        int [] a = getIndexOfImage(note_content);
        String Result = "";
        int BeginIndex = 0;
        if (a[0] != 0){
            for (int i = 1 ; i <= a[0] ; i ++){
                Result += note_content.substring(BeginIndex , a[2*i - 1]);
                BeginIndex = a[2*i] + 2;
            }
        }else {
            Result = note_content;
        }
        return Result;
    }

    //获取文本内容的剪切，用来在主页展示
    public String getShortCut(String note_content){
        String shortCut = "" ;
        String mnote_content = getContentWithNoImage(note_content) ;
        if(mnote_content.length() >= 36 )
            shortCut = mnote_content.substring(0 , 36);
        else
            shortCut = mnote_content;
        return shortCut;
    }



}
