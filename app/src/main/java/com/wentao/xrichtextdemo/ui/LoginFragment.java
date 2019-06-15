package com.wentao.xrichtextdemo.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.sendtion.xrichtext.R;
import com.sendtion.xrichtextdemo.R;
import com.wentao.xrichtextdemo.MyApplication;
import com.wentao.xrichtextdemo.bean.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private  Context mContext;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    //存放错误信息的字符串
    private String wrongInfo;

    //布局中控件（名字和用处相联系）
    private EditText userName;
    private EditText passWord;
    private Button signIn ;
    private Button signUp ;
    private Button forgotPassword;

    //判断用户是否可以登录
    private boolean IF_CAN_SIGNIN = false;

    //用来存放用户名和密码的HashMap
    private Map<String  , String > user = new HashMap<String, String>();



    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_login, null, false);
        initView(rootView);
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

    //用于初始化布局中的控件
    private void initView(View rootView){

        userName = rootView.findViewById(R.id.SignIn_Account_ID);
        passWord = rootView.findViewById(R.id.SignIn_Password_ID);
        signIn = rootView.findViewById(R.id.SignIn_Signin_Btn_ID);
        signUp = rootView.findViewById(R.id.SignIn_To_SignUp_Btn_ID);
        forgotPassword = rootView.findViewById(R.id.SignIn_ForgotPassword_Btn_ID);

        //填入保存的用户名和密码
        Map<String , String > myuser_get = get_User_Info();
        if(myuser_get != null){
            userName.setText(myuser_get.get("UserName"));
            passWord.setText(myuser_get.get("PassWord"));
            userName.setSelection(userName.getText().length());
            passWord.setSelection(passWord.getText().length());
        }

        signIn.setOnClickListener(new LoginFragmentOnClickListener());
        signUp.setOnClickListener(new LoginFragmentOnClickListener());
        forgotPassword.setOnClickListener(new LoginFragmentOnClickListener());

    }

    class LoginFragmentOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.SignIn_Signin_Btn_ID:
                    passWord.setEnabled(false);
                    userName.setEnabled(false);
                    save_User_Info(userName.getText().toString() , passWord.getText().toString());
                    SignIn( userName.getText().toString() ,passWord.getText().toString() ,mContext);
                case R.id.SignIn_To_SignUp_Btn_ID:
                    break;
                case R.id.SignIn_ForgotPassword_Btn_ID:
                    break;


            }
        }
    }

    public void SignIn(final String PhoneNumber , final String PassWord , final Context context){
        BmobQuery<User> categoryBmobQuery = new BmobQuery<>();
        categoryBmobQuery.addWhereEqualTo("Number", PhoneNumber);
        categoryBmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> object, BmobException e) {
                if (e == null) {
                    if (object != null){
                        if(PassWord.equals(object.get(0).getPassword())){
                            showToast("登录成功");
                            MyApplication.ifSignIn = true;
                            MyApplication.phoneNumber = PhoneNumber;
                            ((MainActivity)mContext).changeFragment(((MainActivity)mContext).noteListFragment);
                        }
                    }
                    else
                        showToast("登录失败");
                    passWord.setEnabled(true);
                    userName.setEnabled(true);
                } else {
                    showToast("登录失败");
                    passWord.setEnabled(true);
                    userName.setEnabled(true);
                    MyApplication.ifSignIn = false;
                    ((MainActivity)mContext).changeFragment(new LoginFragment());
                }
            }
        });

    }

    void showToast(String msg){
        Toast.makeText(mContext , msg ,Toast.LENGTH_SHORT);
    }

    //使用该方法返回已经保存的用户名和密码
    public Map<String , String> get_User_Info(){
        Map<String , String> user_info = new HashMap<>();
        SharedPreferences userInfo_d = null;
        try {
            userInfo_d = mContext.getSharedPreferences("user_info" , MODE_PRIVATE);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(userInfo_d != null){
            user_info.put("UserName" ,userInfo_d.getString("UserName" , ""));
            user_info.put("PassWord" ,userInfo_d.getString("Password" , ""));
        }
        return user_info;
    }

    private void save_User_Info(String userName , String passWord){
        SharedPreferences.Editor editor = ((MainActivity)getActivity()).getSharedPreferences("user_info" , MODE_PRIVATE).edit();
        editor.putString("UserName" , userName);
        editor.putString("Password" , passWord);
        editor.apply();
    }
}
