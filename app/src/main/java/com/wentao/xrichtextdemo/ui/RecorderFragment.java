package com.wentao.xrichtextdemo.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.hd.cuterecorder.CuteRecorder;
import com.haozhang.lib.AnimatedRecordingView;
import com.sendtion.xrichtextdemo.R;
import com.wentao.xrichtextdemo.adapter.RadioListAdapter;
import com.wentao.xrichtextdemo.bean.Radio;
import com.wentao.xrichtextdemo.db.MyOpenHelper;
import com.wentao.xrichtextdemo.db.RadioDao;
import com.wentao.xrichtextdemo.util.CommonUtil;
import com.wentao.xrichtextdemo.view.SpacesItemDecoration;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecorderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecorderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecorderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView rv_list_main;
    private Button Say;
    private ImageView recorder_fragment_mic;
    private AnimatedRecordingView animatedRecordingView;
    private CuteRecorder recorder;
    private RadioDao radioDao;
    private static int vol = 0;
    public String outPutDir = Environment.getExternalStorageDirectory() + "/test_cuteRecorder";

    private OnFragmentInteractionListener mListener;
    private Context mContext = getActivity();
    private MediaPlayer myMediaPlayer = new MediaPlayer();

    public RecorderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecorderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecorderFragment newInstance(String param1, String param2) {
        RecorderFragment fragment = new RecorderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recorder, null, false);
        initView(rootView);
        initEvent();
        return rootView;
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

        Say = rootView.findViewById(R.id.Say);
        animatedRecordingView = rootView.findViewById(R.id.recording);
        rv_list_main = rootView.findViewById(R.id.rv_list_main);
        recorder_fragment_mic = rootView.findViewById(R.id.recorder_fragment_mic);
        if (radioDao == null) {
            radioDao = new RadioDao(getActivity());
        }
        animatedRecordingView.setVisibility(View.INVISIBLE);
        setRv_list_main();
    }

    void setRv_list_main() {
        if (radioDao == null) {
            radioDao = new RadioDao(mContext);
        }
        List<Radio> radioList = radioDao.queryRadiosAll();
        final RadioListAdapter adapter = new RadioListAdapter();
        adapter.setmRadios(radioList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//竖向列表
        rv_list_main.addItemDecoration(new SpacesItemDecoration(0));//设置item间距
        rv_list_main.setLayoutManager(layoutManager);
        adapter.setOnItemClickListener(new RadioListAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Radio radio) {
                try {
                    play(radio.getAddress());
                }catch (Exception e){
                }
            }
        });
        adapter.setOnItemLongClickListener(new RadioListAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final Radio radio) {
                PopupMenu popupMenu = new PopupMenu(getActivity() ,view);
                popupMenu.getMenuInflater().inflate(R.menu.menu3,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.del) {
                            if (radioDao == null) {
                                radioDao = new RadioDao(getActivity());
                            }
                            radioDao.deleteRadio(radio.getId());
                            setRv_list_main();
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        rv_list_main.setAdapter(adapter);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        initRecorder();
        Say.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case ACTION_DOWN:
                        if (!animatedRecordingView.isWorking()) {
                            if (recorder.isPrepared()) {
                                recorder.start();
                                recorder_fragment_mic.setVisibility(View.INVISIBLE);
                                animatedRecordingView.setVisibility(View.VISIBLE);
                            }
                            animatedRecordingView.setVolume(vol);
                            animatedRecordingView.loading();
                        }
                        animatedRecordingView.setVolume(vol);
                        return true;
                    case ACTION_UP:
                        if (animatedRecordingView.isWorking()) {
                            animatedRecordingView.setVisibility(View.INVISIBLE);
                            animatedRecordingView.stop();
                            recorder.stop();
                            recorder_fragment_mic.setVisibility(View.VISIBLE);
                            recorder = null;
                            initRecorder();
                        }
                        animatedRecordingView.setVisibility(View.INVISIBLE);
                        animatedRecordingView.stop();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void initRecorder() {
        recorder = new CuteRecorder.Builder()
                .maxTime(60)
                .minTime(2)
                .outPutDir(outPutDir)
                .voiceLevel(CuteRecorder.NORMAL)
                .build();
        recorder.setOnAudioRecordListener(new CuteRecorder.AudioRecordListener() {
            @Override
            public void hasRecord(int seconds) {
                Log.i("HD", "hasRecord = " + seconds);

            }

            @Override
            public void finish(int seconds, final String filePath) {
                Log.i("HD", "finish = " + seconds + "  " + filePath);
                Radio radio = new Radio();
                radio.setId(Integer.toString(CommonUtil.getIdByTime()));
                radio.setCreateTime(CommonUtil.date2string(new Date()));
                radio.setLength(Integer.toString(seconds));
                radio.setName("");
                radio.setAddress(filePath);
                if (radioDao == null) {
                    radioDao = new RadioDao(mContext);
                }
                radioDao.insertRadio(radio);
                setRv_list_main();
            }

            @Override
            public void tooShort() {
                if (animatedRecordingView.isWorking()) {
                    animatedRecordingView.setVisibility(View.INVISIBLE);
                    animatedRecordingView.stop();
                    recorder.stop();
                    recorder_fragment_mic.setVisibility(View.VISIBLE);
                    recorder = null;
                    initRecorder();
                }
                Log.i("HD", "tooShort");
                Toast.makeText(getActivity() , "录音时间太短" ,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void curVoice(final int voice) {
                Log.i("HD", "curVoice = " + voice);
                vol = voice;
            }
        });
    }


    void play(String path) throws IOException {
        MediaPlayer mMediaPlayer = null;
        if(myMediaPlayer.isPlaying()){
            myMediaPlayer.stop();
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setDataSource(path);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                myMediaPlayer = mp;
            }
        });
        try {
            mMediaPlayer.setDataSource(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
        mMediaPlayer.start();
    }
}
