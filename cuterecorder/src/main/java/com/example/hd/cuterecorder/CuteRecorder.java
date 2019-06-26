package com.example.hd.cuterecorder;

import android.os.Environment;
import android.os.Handler;

/**
 * Created by HONGDA on 2017/7/4.11:32
 */

public class CuteRecorder implements AudioManger.AudioStateListener {

    private AudioManger audioManger;
    //is recording ?
    private boolean isRecording = false;

    private int time = 0;
    private Handler handler;

    private boolean isPrepared = false;

    public static final int HIGH = 20;
    public static final int NORMAL = 14;
    public static final int LOW = 8;

    private String DIR_PATH;
    private int MAX_TIME;
    private int MIN_TIME;
    private int VOICE_LEVEL;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isRecording) {
                time++;
                audioRecordListener.hasRecord(time);
                audioRecordListener.curVoice(audioManger.getVoiceLevel(VOICE_LEVEL));
                handler.postDelayed(this, 1000);
            }
        }
    };

    public CuteRecorder(Builder builder) {
        this.DIR_PATH = builder.DIR_PATH;
        this.MAX_TIME = builder.MAX_TIME;
        this.MIN_TIME = builder.MIN_TIME;
        this.VOICE_LEVEL = builder.VOICE_LEVEL;

        audioManger = AudioManger.getInstance(DIR_PATH);
        audioManger.setOnAudioStateListener(this);
        handler = new Handler();
        audioManger.prepareAudio();

    }

    //the callback for prepared to record
    @Override
    public void wellPrepared() {
        isRecording = false;
        time = 0;
        isPrepared = true;
    }

    public void start() {
        if (!isPrepared) {
            audioManger.prepareAudio();
        }
        audioManger.start();
        isRecording = true;
        handler.post(runnable);
    }

    //结束录制
    public void stop() {
        if (time <= MIN_TIME) {
            audioRecordListener.tooShort();
        } else {
            audioRecordListener.finish(time, audioManger.getCurrentFilePath());
        }
        audioManger.release();
        isPrepared = false;
        reset();
    }

    //置位
    private void reset() {
        isRecording = false;
        time = 0;

        handler.removeCallbacks(runnable);
    }


    public static class Builder {

        /*
        *    the default output dir path is SD/audios
        * */
        private String DIR_PATH = Environment.getExternalStorageDirectory() + "/audios";
        /*
        *    max record time  60s
        * */
        private int MAX_TIME = 60;
        /*
        *   min record time 3s
        * */
        private int MIN_TIME = 3;

        /*
        *  the voice level HIGH 20 NORMAL 14  LOW 8  default NORMAL
        * */
        private int VOICE_LEVEL = NORMAL;

        public CuteRecorder build() {
            return new CuteRecorder(this);
        }

        public Builder outPutDir(String dirPath) {
            this.DIR_PATH = dirPath;
            return this;
        }

        public Builder maxTime(int maxTime) {
            this.MAX_TIME = maxTime;
            return this;
        }

        public Builder minTime(int minTime) {
            this.MIN_TIME = minTime;
            return this;
        }

        public Builder voiceLevel(int voiceLevel) {
            this.VOICE_LEVEL = voiceLevel;
            return this;
        }

    }

    public interface AudioRecordListener {
        //has record
        void hasRecord(int seconds);

        void finish(int seconds, String filePath);

        void tooShort();

        //current voice level
        void curVoice(int voice);
    }

    private AudioRecordListener audioRecordListener;

    public void setOnAudioRecordListener(AudioRecordListener listener) {
        audioRecordListener = listener;
    }

    //is prepare to record
    public boolean isPrepared() {
        return isPrepared;
    }
    //get the output file
    public String getOutputDirPath() {
        return DIR_PATH;
    }
    //get max record time
    public int getMaxTime() {
        return MAX_TIME;
    }
    //get min record time
    public int getMinTime() {
        return MIN_TIME;
    }
    //get the voice level
    public int getVoiceLevel() {
        return VOICE_LEVEL;
    }

}
