package com.example.hd.cuterecorder;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;

/**
 * Created by LHD on 2016/8/30.
 */
public class AudioManger {
    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurrentFilePath;

    private static AudioManger mInstance;

    private boolean isPrepared;

    //recorder prepared
    public interface AudioStateListener {
        void wellPrepared();
    }

    public AudioStateListener mListener;

    public void setOnAudioStateListener(AudioStateListener Listener) {
        this.mListener = Listener;
    }

    public static AudioManger getInstance(String dir) {
        if (mInstance == null) {
            synchronized (AudioManger.class) {
                if (mInstance == null) {
                    mInstance = new AudioManger(dir);
                }
            }
        }
        return mInstance;
    }

    private AudioManger(String dir) {
        mDir = dir;
    }

    //prepare to start
    public void prepareAudio() {
        try {
            isPrepared = false;
            File dir = new File(mDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = generateFileName();
            File file = new File(dir, fileName);
            mCurrentFilePath = file.getAbsolutePath();

            mMediaRecorder = new MediaRecorder();
            //set output file
            mMediaRecorder.setOutputFile(mCurrentFilePath);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.prepare();

            isPrepared = true;
            if (mListener != null) {
                mListener.wellPrepared();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (isPrepared) {
            mMediaRecorder.start();
        }
    }

    private String generateFileName() {
        return System.currentTimeMillis() + ".amr";
    }

    public void release() {
        if (mMediaRecorder != null) {
                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;

        }
    }

    public void cancel() {
        release();
        //如果取消就删除生成的录音文件
        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }

    public int getVoiceLevel(int maxLevel) {
        if (isPrepared) {
            try {
                //mMediaRecorder.getMaxAmplitude() 1-32767
                return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
            } catch (Exception e) {

            }
        }
        return 1;
    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }

}
