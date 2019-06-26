package com.wentao.xrichtextdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wentao.xrichtextdemo.bean.Radio;
import com.sendtion.xrichtextdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：Sendtion on 2016/10/21 0021 16:59
 * 邮箱：sendtion@163.com
 * 博客：http://sendtion.cn
 * 描述：笔记列表适配器
 */

public class RadioListAdapter extends RecyclerView.Adapter<RadioListAdapter.ViewHolder>
        implements View.OnClickListener, View.OnLongClickListener {
    private Context mContext;
    private List<Radio> mRadios;
    private OnRecyclerViewItemClickListener mOnItemClickListener ;
    private OnRecyclerViewItemLongClickListener mOnItemLongClickListener ;

    public RadioListAdapter() {
        mRadios = new ArrayList<>();
    }

    public void setmRadios(List<Radio> radios) {
        this.mRadios = radios;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,(Radio)v.getTag());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnItemLongClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemLongClickListener.onItemLongClick(v,(Radio)v.getTag());
        }
        return true;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, Radio radio);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View view, Radio radio);
    }

    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Log.i(TAG, "###onCreateViewHolder: ");
        //inflate(R.layout.list_item_record,parent,false) 如果不这么写，cardview不能适应宽度
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_radio,parent,false);
        //将创建的View注册点击事件
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Log.i(TAG, "###onBindViewHolder: ");
        final Radio radio = mRadios.get(position);
        //将数据保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(radio);
        //Log.e("adapter", "###record="+record);
        holder.create_time.setText(radio.getLength() + "秒");
        holder.time_length.setText(radio.getCreateTime());
    }

    @Override
    public int getItemCount() {
        //Log.i(TAG, "###getItemCount: ");
        if (mRadios != null && mRadios.size()>0){
            return mRadios.size();
        }
        return 0;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView create_time;
        public TextView time_length;

        public ViewHolder(View view){
            super(view);
            create_time =  view.findViewById(R.id.TextView_Create_Time);
            time_length =  view.findViewById(R.id.TextView_Time_Length);

        }
    }


}


