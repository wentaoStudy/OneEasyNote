package com.wentao.xrichtextdemo.ui;

import android.Manifest;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sendtion.xrichtextdemo.R;
//import MyNoteListAdapter;
import com.wentao.xrichtextdemo.MyApplication;
import com.wentao.xrichtextdemo.bean.Note;
import com.wentao.xrichtextdemo.bean.User;
import com.wentao.xrichtextdemo.db.NoteDao;
import com.wentao.xrichtextdemo.util.SystemUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
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
    private int groupId;//分类ID
//    private String groupName;
//    private FloatingActionButton fab;
    protected DrawerLayout drawer;
    public NoteListFragment noteListFragment;
    //存放需要的用户权限
    private String[] permissions = {Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    List<Note> note_list_query_from_net = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用于初始化Bmob云，使用aapkey初始化
        Bmob.initialize(this , "Repalce There with your own Bmob apk id");

        setContentView(R.layout.activity_main);
        getPermission();


//        initBgPic();
        initView();
        autoLogin();


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
        ft.replace(R.id.main_fraglayout, noteListFragment,null);
        ft.commit();
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
    private boolean ifContains(List<Note> A , Note B){
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
}
