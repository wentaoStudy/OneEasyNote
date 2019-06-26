package com.wentao.xrichtextdemo.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sendtion.xrichtextdemo.R;
import com.wentao.xrichtextdemo.MyApplication;
import com.wentao.xrichtextdemo.adapter.MyNoteListAdapterWithImage;
import com.wentao.xrichtextdemo.bean.Note;
import com.wentao.xrichtextdemo.bean.Photo;
import com.wentao.xrichtextdemo.db.NoteDao;
import com.wentao.xrichtextdemo.util.SystemUtils;
import com.wentao.xrichtextdemo.view.SpacesItemDecoration;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import static cn.bmob.v3.BmobRealTimeData.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NoteListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NoteListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static Context mContext ;
    private FloatingActionButton fab;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView rv_list_main;
    private MyNoteListAdapterWithImage mNoteListAdapter;
    private List<Note> noteList;
    private NoteDao noteDao;
    private int groupId;//分类ID
    private String groupName;
    protected DrawerLayout drawer;
    private Activity aty;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Note> note_list_query_from_net;

    private OnFragmentInteractionListener mListener;

    public NoteListFragment()
    {
        // Required empty public constructor
    }

//    public NoteListFragment(Context context){
//        mContext = context;
//    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NoteListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NoteListFragment newInstance(String param1, String param2) {
        NoteListFragment fragment = new NoteListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static NoteListFragment newInstance(String param1, String param2 , Context context) {
        NoteListFragment fragment = new NoteListFragment();
        mContext = context;
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater , container , savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_note_list, null, false);
        aty = getActivity();
        initView(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshNoteList();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void initView(View rootView) {
//        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar_main);
//        getActivity().setSupportActionBar(toolbar);
        fab = rootView.findViewById(R.id.Create_New_Note);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NewActivity.class);
                intent.putExtra("groupName", groupName);
                intent.putExtra("flag", 0);
                startActivity(intent);
            }
        });

        noteDao = new NoteDao(mContext);

        rv_list_main = (RecyclerView) rootView.findViewById(R.id.rv_list_main);
        rv_list_main.addItemDecoration(new SpacesItemDecoration(0));//设置item间距
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//竖向列表
        rv_list_main.setLayoutManager(layoutManager);

        mNoteListAdapter = new MyNoteListAdapterWithImage();
        mNoteListAdapter.setmNotes(noteList);
        rv_list_main.setAdapter(mNoteListAdapter);

        mNoteListAdapter.setOnItemClickListener(new MyNoteListAdapterWithImage.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Note note) {
                //showToast(note.getTitle());

                Intent intent = new Intent(mContext, NoteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("note", note);
                intent.putExtra("data", bundle);
                startActivity(intent);
            }
        });
        mNoteListAdapter.setOnItemLongClickListener(new MyNoteListAdapterWithImage.OnRecyclerViewItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final Note note) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("提示");
                builder.setMessage("确定删除笔记？");
                builder.setCancelable(false);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int ret = noteDao.deleteNote(note.getId());
                        if (ret > 0){
                            showToast("删除成功");
                            //TODO 删除笔记成功后，记得删除图片（分为本地图片和网络图片）
                            //获取笔记中图片的列表 StringUtils.getTextFromHtml(note.getContent(), true);
                            refreshNoteList();
                        }
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
            }
        });

        swipeRefreshLayout = rootView.findViewById(R.id.main_content_swiprefreshlayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        getImageFromNet();
                        refreshNetNoteList();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 4000);

            }
        });
    }

    //刷新笔记列表
    private void refreshNoteList(){
        if (noteDao == null)
            noteDao = new NoteDao(mContext);
        noteList = noteDao.queryNotesAll(groupId);
        mNoteListAdapter.setmNotes(noteList);
        mNoteListAdapter.notifyDataSetChanged();
    }

    private void refreshNetNoteList(){
        final List<Note> note_list = getLatestedNoteList();
        BmobQuery<Note> query = new BmobQuery<>();
        query.addWhereEqualTo("userId" , MyApplication.phoneNumber);
        query.findObjects(new FindListener<Note>() {
            @Override
            public void done(List<Note> list, BmobException e) {
                note_list_query_from_net = list;
                for (Note note : list){
                    Log.d(TAG, "ResultFromNet" + note.toString());
                }
                if(note_list_query_from_net != null){
                    for (Note net_Note : note_list_query_from_net){
                        int count = 0;
                        for (Note note : note_list) {
                            if (note.getId() == net_Note.getId()) {
                                count++;
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date net_Note_Date = new Date();
                                Date Note_Date = net_Note_Date;
                                try {
                                    net_Note_Date = sdf.parse(net_Note.getUpdateTime());
                                    Note_Date = sdf.parse(note.getUpdateTime());
                                } catch (ParseException e1e) {
                                    e1e.printStackTrace();
                                }
                                Log.d(TAG, "done: " + "ATIME:" + net_Note.getUpdateTime() + "BTIME" + note.getUpdateTime() );
                                if (net_Note_Date.after(Note_Date)) {
                                    Log.d(TAG, "done: local Text has updated");
                                    if (noteDao == null)
                                        noteDao = new NoteDao(mContext);
                                    note = net_Note;
                                    noteDao.updateNote(note);
                                }
                            }
                        }
                        if (count == 0) {
                            if (noteDao == null)
                                noteDao = new NoteDao(mContext);
                            noteDao.insertNote(net_Note);
                        }
                    }refreshNoteList();
                }
            }
        });

    }

    private void getImageFromNet(){
        final List<Note> note_list = getLatestedNoteList();
        for(Note myNote : note_list){
            final String[] myImageAddress = MainActivity.getImagesAddress(myNote.getContent());
            final Note ThereMyNote = myNote;
            if(myImageAddress.length != 0){////.........
                BmobQuery<Photo> query = new BmobQuery<>();
                query.addWhereEqualTo("userId" , MyApplication.phoneNumber);
                Log.d(TAG, "getImageFromNet: " + MyApplication.phoneNumber);
                query.addWhereEqualTo("id" , myNote.getId());
                Log.d(TAG, "getImageFromNet: " + myNote.getId());
                query.findObjects(new FindListener<Photo>() {
                    @Override
                    public void done(List<Photo> list, BmobException e) {
                        Log.d(TAG, "done: getThree Image" );
                        if(e == null){
                            for (Photo photo : list){
                                int count = 0;
//                                for(String myAdr : myImageAddress){
//                                    final BmobFile bmobFile = new BmobFile(new File(myAdr));
//                                    Log.d(TAG, "done:bmobFile.getFilename() " + bmobFile.getFilename() );
//                                    Log.d(TAG, "done: " + myAdr);
//                                    if(bmobFile.getFilename() != null){
//                                        if(bmobFile.getFilename().equals(photo.getPhoto().getFilename())){
//                                            count++;
//                                        }
//                                    }
//
//                                }
                                downloadFile(photo.getPhoto());
                            }
                        }else {
                            Log.d(TAG, "done: " + e.getMessage());
                        }
                    }
                });

            }
        }
    }



    public List<Note> getLatestedNoteList(){
        if (noteDao == null)
            noteDao = new NoteDao(mContext);
        noteList = noteDao.queryNotesAll(groupId);
        return noteList;
    }


    void showToast(String msg){
        Toast.makeText(mContext , msg , Toast.LENGTH_SHORT);
    }


    private void downloadFile(BmobFile file){
        Log.d(TAG, "downloadFile: " + file.getFilename());
        //允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
        File saveFile = new File(Environment.getExternalStorageDirectory(), file.getFilename());
        file.download(saveFile, new DownloadFileListener() {

            @Override
            public void onStart() {
                showToast("开始下载");
            }

            @Override
            public void done(String savePath,BmobException e) {
                if(e==null){
                    showToast("下载成功,保存路径:"+savePath);
                    Log.d(TAG, "done: "+savePath);
                }else{
                    showToast("下载失败："+e.getErrorCode()+","+e.getMessage());
                    Log.d(TAG, "done: "+e.getErrorCode()+","+e.getMessage() );
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
                Log.i("bmob","下载进度："+value+","+newworkSpeed);
            }

        });
    }


}
