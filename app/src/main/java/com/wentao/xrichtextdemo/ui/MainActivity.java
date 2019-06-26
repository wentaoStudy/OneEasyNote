package com.wentao.xrichtextdemo.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.haozhang.lib.AnimatedRecordingView;
import com.sendtion.xrichtextdemo.R;
//import MyNoteListAdapter;
import com.wentao.xrichtextdemo.MyApplication;
import com.wentao.xrichtextdemo.bean.Note;
import com.wentao.xrichtextdemo.bean.Photo;
import com.wentao.xrichtextdemo.bean.User;
import com.wentao.xrichtextdemo.db.NoteDao;
import com.wentao.xrichtextdemo.db.PhotoDao;
import com.wentao.xrichtextdemo.db.RadioDao;
import com.wentao.xrichtextdemo.util.ImageUtils;
import com.wentao.xrichtextdemo.util.MyGlideEngine;
import com.wentao.xrichtextdemo.util.SDCardUtil;
import com.wentao.xrichtextdemo.util.SystemUtils;
import com.wentao.xrichtextdemo.widget.CircleImageView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 作者：Sendtion on 2016/10/21 0021 16:43
 * 邮箱：sendtion@163.com
 * 博客：http://sendtion.cn
 * 描述：主界面
 */

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener , EasyPermissions.PermissionCallbacks{
    private static final String TAG = "MainActivity";
//    private RecyclerView rv_list_main;
//    private MyNoteListAdapterWithImage mNoteListAdapter;
    private List<Note> noteList;
    private NoteDao noteDao;
    private PhotoDao photoDao;
    private int groupId;//分类ID
//    private String groupName;
//    private FloatingActionButton fab;
    protected DrawerLayout drawer;
    private CircleImageView ivHead;
    public NoteListFragment noteListFragment;
    public RecorderFragment recorderFragment;
    Boolean autoLogin = true;
    Boolean audioMainPage = false;
    //存放需要的用户权限
    private String[] permissions = {Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.RECORD_AUDIO};

    List<Note> note_list_query_from_net = null;

    private void prfLoad() {
        SharedPreferences preferences = this.getSharedPreferences("settings",MODE_PRIVATE);
        autoLogin = preferences.getBoolean(SettingFragment.SWITCH_AUTO_LOGIN , false);
        audioMainPage = preferences.getBoolean(SettingFragment.SWITCH_AUDIO_MAIN_PAGE , false);
    }

    private static final int REQUEST_CODE_CHOOSE = 23;//定义请求码常量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用于初始化Bmob云，使用aapkey初始化
        Bmob.initialize(this , "ff5c29133b8b8f55b14844d7db71aee8");
        setContentView(R.layout.activity_main);
        prfLoad();
        getPermission();
//        initBgPic();
        initView();
        if(autoLogin){
            autoLogin();
        }

    }

    private boolean autoLogin(){
        Map<String , String > myuser_get = get_User_Info();
        if(myuser_get == null){
            return false;
        }else {
            String userName = myuser_get.get("UserName");
            String passWord = myuser_get.get("PassWord");
            SignIn(userName , passWord);
            return true;
        }
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        initMainFragment();
        drawer = findViewById(R.id.drawer_layout);
        initBgPic();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        ivHead  =  headerView.findViewById(R.id.nav_header_user_imageview);
        ivHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callGallery();
            }
        });
        SharedPreferences myget = getSharedPreferences("images" , Context.MODE_PRIVATE);
        File file = new File(myget.getString("head_image" , ""));
        Glide.with(this).load(file).override(666,666).into(ivHead);
        drawer.setScrimColor(Color.TRANSPARENT);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = drawer.getChildAt(0);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
                float rightScale = 0.8f + scale * 0.2f;
                float leftScale = 0.5f + slideOffset * 0.5f;
                mMenu.setAlpha(leftScale);
                mMenu.setScaleX(leftScale);
                mMenu.setScaleY(leftScale);
                mContent.setPivotX(0);
                mContent.setPivotY(mContent.getHeight() * 1/2);
                mContent.setScaleX(rightScale);
                mContent.setScaleY(rightScale);
                mContent.setTranslationX(mMenu.getWidth() * slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
//                cardView.setRadius(20);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
//                cardView.setRadius(0);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


    }

    private void initBgPic(){

        SystemUtils systemUtils=new SystemUtils(this);
        String path=systemUtils.getPath();
        if(path!=null) {
            Bitmap bitmap = systemUtils.getBitmapByPath(this, path);
            if (bitmap != null) {
                drawer.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));

            }
        }
    }

    private void initMainFragment(){
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        noteListFragment=new NoteListFragment();
        recorderFragment=new RecorderFragment();
        if(audioMainPage){
            ft.replace(R.id.main_fraglayout, recorderFragment,null);
            ft.commit();
        }else {
            ft.replace(R.id.main_fraglayout, noteListFragment,null);
            ft.commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }



    protected  void changeFragment(Fragment fragment){
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        ft.replace(R.id.main_fraglayout, fragment,null);
        ft.addToBackStack(fragment.toString());
        ft.commit();
    }


    //@SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            case R.id.nav_change_bg :
                Log.d(TAG, "onNavigationItemSelected: 更换皮肤");
//                setTitle("更换皮肤");
                ChangeBgFragment changeBgFragment=new ChangeBgFragment();
                changeFragment(changeBgFragment);
                break;
            case R.id.nav_home:
                changeFragment(noteListFragment);
                break;
            case R.id.nav_login:
                LoginFragment loginFragment = new LoginFragment();
                changeFragment(loginFragment);
                break;
            case R.id.nav_push_to_net:
                pushToNet();
                //因为bmob文件服务器域名问题而流产，准备更换为自己写的服务器
                //pushImageToNet();
                break;
            case R.id.voice_note:
                changeFragment(recorderFragment);
                break;
            case R.id.nav_setting:
                SettingFragment settingFragment = new SettingFragment();
                changeFragment(settingFragment);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //获取权限，一下为动态申请权限内容
    private void getPermission() {
        if (EasyPermissions.hasPermissions(this, permissions)) {
            //已经打开权限
            Log.d(TAG, "getPermission: 已经申请相关权限");
        } else {
            //没有打开相关权限、申请权限
            EasyPermissions.requestPermissions(this, "需要获取您的相关权限", 1, permissions);
        }

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //使用该方法返回已经保存的用户名和密码
    public Map<String , String> get_User_Info(){
        Map<String , String> user_info = new HashMap<>();
        SharedPreferences userInfo_d = null;
        try {
            userInfo_d = this.getSharedPreferences("user_info" , MODE_PRIVATE);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(userInfo_d != null){
            user_info.put("UserName" ,userInfo_d.getString("UserName" , ""));
            user_info.put("PassWord" ,userInfo_d.getString("Password" , ""));
        }
        return user_info;
    }

    public void SignIn(final String PhoneNumber , final String PassWord ){
        Log.d(TAG, "SignIn: 开始登录");
        BmobQuery<User> categoryBmobQuery = new BmobQuery<>();
        categoryBmobQuery.addWhereEqualTo("Number", PhoneNumber);
        categoryBmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> object, BmobException e) {
                if (e == null) {
                    if (object != null){
                        if(PassWord.equals(object.get(0).getPassword())){
                            Log.d(TAG, "done: 登录成功");
                            showToast("登录成功");
                            MyApplication.ifSignIn = true;
                            MyApplication.phoneNumber = PhoneNumber;
                            changeFragment(noteListFragment);
                        }
                    }
                    else
                        showToast("登录失败");
                } else {
                    showToast("登录失败");
                    MyApplication.ifSignIn = false;
                    changeFragment(new LoginFragment());
                }
            }
        });

    }

    private List<Note> getLatestedNoteList(){
        if (noteDao == null)
            noteDao = new NoteDao(this);
        noteList = noteDao.queryNotesAll(groupId);
        return noteList;
    }

    //if List A contains B , judge by Note id
    public static boolean ifContains(List<Note> A , Note B){
        int count = 0;
        for(Note note : A){
            Log.d(TAG, "ifContains: " + ":BID" + B.getId() +":AID" + note.getId());
            if(B.getId() == note.getId()){
                count++;
            }
        }
        if(count == 0){
            Log.d(TAG, "ifContains: " + count);
            return false;
        }
        else {
            return true;
        }
    }

    public static boolean ifContains(List<Photo> A , BmobFile B){
        int count = 0;
        for(Photo photo : A){
            Log.d(TAG, "ifContains: " + ":BID" + B.getFilename() +":AID" + photo.getPhoto().getFilename());
            if(B.getFilename().equals(photo.getPhoto().getFilename())){
                count++;
            }
        }
        if(count == 0){
            Log.d(TAG, "ifContains: " + count);
            return false;
        }
        else {
            return true;
        }
    }


    private void pushToNet(){
        final List<Note> note_list = getLatestedNoteList();
        for (Note mynote : note_list){
            Log.d(TAG, "done: networkUpdate" + mynote.getContent());
        }
        BmobQuery<Note> query = new BmobQuery<>();
        query.addWhereEqualTo("userId" , MyApplication.phoneNumber);
        query.findObjects(new FindListener<Note>() {
            @Override
            public void done(List<Note> list, BmobException e) {
                if(e == null){
                    for(Note myTestNote : list){
                        Log.d(TAG, "done: " + myTestNote.getObjectId());
                    }
                    Log.d(TAG, "QueryResult");
                    note_list_query_from_net = list;
                    for(final Note note : note_list){
                        //如果不包含，则存进数据库
                        if( !ifContains(note_list_query_from_net , note)){
                            note.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if(e == null){
                                        Log.d(TAG, "saveobjectid" + s);
                                        note.setObjectId(s);
                                        if (noteDao == null)
                                            noteDao = new NoteDao(MainActivity.this);
                                        noteDao.updateNote(note);
                                    }
                                }
                            });
                        }
                        //如果包含，则根据编辑时间更新数据库
                        else {
                            Note net_Note = null;
                            for(Note note1 : note_list_query_from_net){
                                if(note1.getId() == note.getId()){
                                    net_Note = note1;
                                }
                            }
                            if(net_Note == null )
                                continue;
                            Log.d(TAG, "done: networkUpdate");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date net_Note_Date = new Date();
                            Date Note_Date = new Date();
                            try {
                                net_Note_Date = sdf.parse(net_Note.getUpdateTime());
                                Note_Date = sdf.parse(note.getUpdateTime());
                            } catch (ParseException me) {
                                me.printStackTrace();
                            }
                            if(net_Note_Date.before(Note_Date)){
                                Log.d(TAG, "done: networkUpdate2" + net_Note.getObjectId());
                                note.setObjectId(net_Note.getObjectId());
                                note.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e == null){
                                            Log.d(TAG, "done: networkUpdate2" + note.getContent());
                                            Log.d(TAG, "done: networkUpdate2");
                                        }else {
                                            Log.d(TAG, "done: networkUpdate2" + e.getMessage());
                                        }
                                    }
                                });
                            }
                        }
                    }
                    if (note_list_query_from_net == null) {
                        for (Note note : note_list) {
                            note.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {

                                }
                            });
                        }
                    }
                }
                else {
                    //检查无误
                    for(final Note note : note_list){
                        try{
                            note.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    Log.d(TAG, "done: " + s);
                                    if (e == null){
                                        note.setObjectId(s);
                                        if (noteDao == null)
                                            noteDao = new NoteDao(MainActivity.this);
                                        noteDao.updateNote(note);
                                    }else {
                                        Log.d(TAG, "done: " + e.getMessage());
                                    }

                                }
                            });
                        }catch (Exception mme){
                            Log.d(TAG, "done: " + mme.getMessage());
                        }

                    }
                }
            }
        });

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

    private void pushImageToNet(){
        final List<Note> note_list = getLatestedNoteList();
        for(Note myNote : note_list){
            final String[] myImageAddress = getImagesAddress(myNote.getContent());
            final Note ThereMyNote = myNote;

                for(String myAdr : myImageAddress){
                    final BmobFile bmobFile = new BmobFile(new File(myAdr));
                    Log.d(TAG, "done:bmobFile.getFilename() " + bmobFile.getFilename() );
                        final Photo photo = new Photo();
                        photo.setId(ThereMyNote.getId());
                        photo.setUserId(ThereMyNote.getUserId());
                        bmobFile.upload(new UploadFileListener() {
                            @Override
                            public void done(BmobException e) {
                                photo.setPhoto(bmobFile);
                                photo.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {

                                    }
                                });
                            }
                        });
                    }
        }
    }

    @Override
    public void showToast(String text) {
        super.showToast(text);
    }

    /**
     * 调用图库选择
     */
    private void callGallery(){
//        //调用系统图库
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");// 相片类型
//        startActivityForResult(intent, 1);

        Matisse.from(this)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.GIF))//照片视频全部显示MimeType.allOf()
                .countable(true)//true:选中后显示数字;false:选中后显示对号
                .maxSelectable(1)//最大选择数量为1
                //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))//图片显示表格的大小
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//图像选择和预览活动所需的方向
                .thumbnailScale(0.85f)//缩放比例
                .theme(R.style.Matisse_Zhihu)//主题  暗色主题 R.style.Matisse_Dracula
                .imageEngine(new MyGlideEngine())//图片加载方式，Glide4需要自定义实现
                .capture(true) //是否提供拍照功能，兼容7.0系统需要下面的配置
                //参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
                .captureStrategy(new CaptureStrategy(true,"com.sendtion.matisse.fileprovider"))//存储到哪里
                .forResult(REQUEST_CODE_CHOOSE);//请求码
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                if (requestCode == 1){
                    //处理调用系统图库
                } else if (requestCode == REQUEST_CODE_CHOOSE){
                    //异步方式插入图片
                    List<Uri> mSelected = Matisse.obtainResult(data);
                    String imagePath = SDCardUtil.getFilePathFromUri(MainActivity.this,  mSelected.get(0));
                    //Log.e(TAG, "###path=" + imagePath);
                    Bitmap bitmap = ImageUtils.getSmallBitmap(imagePath, 99, 99);//压缩图片
                    //bitmap = BitmapFactory.decodeFile(imagePath);
                    imagePath = SDCardUtil.saveToSdCard(bitmap);
                    File file = new File(imagePath);
                    Glide.with(this).load(file).override(369,369).into(ivHead);
                    SharedPreferences.Editor editor = getSharedPreferences("images" , Context.MODE_PRIVATE).edit();
                    editor.putString("head_image" , imagePath);
                    editor.apply();

                }
            }
        }
    }
}
