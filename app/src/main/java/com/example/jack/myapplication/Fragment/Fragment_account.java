package com.example.jack.myapplication.Fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.jack.myapplication.MainActivity;
import com.example.jack.myapplication.Model.User;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.Util.Event.ImageEvent;
import com.example.jack.myapplication.Util.Event.ListEvent;
import com.example.jack.myapplication.Util.Event.MessageEvent;
import com.example.jack.myapplication.Util.Interface.refreshPic;
import com.example.jack.myapplication.Util.InternetUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;


/**
 * 设置账号信息，本来想用PreferFragment，但是不兼容v4下的Fragment
 * 只能手动绘制
 */
public class Fragment_account extends Fragment implements refreshPic {
    final private String TAG = "Fragment_account";
    private Context mContext;
    /**
     * 单例对象实例
     */
    private static Fragment_account instance = null;

    EditText etName;
    EditText etMail;
    Spinner spSex;
    LinearLayout llAler;
    LinearLayout llSpend;
    FloatingActionButton commitFab;
    CircleImageView avatar;

    private String name;
    private String sex;
    private String mail;

    public static final int RESULT_CHOOSE_LOCATION = 1;
    public static final int RESULT_CHOOSE_PHOTO = 2;
    public static String picPath = "";
    /**
     * 对外接口
     *
     * @return Fragment2
     */
    public static Fragment_account GetInstance() {
        if (instance == null)
            instance = new Fragment_account();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = this.getActivity();
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        bindView(view);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                name = etName.getText().toString();
                MainActivity.user.setName(name);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etMail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mail = etMail.getText().toString();
                MainActivity.user.setMail(mail);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //设置默认下标,这里暂时不对性别进行保存
        spSex.setSelection(0,true);
        spSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                String[] sexes = getResources().getStringArray(R.array.sex);
                sex = sexes[pos];
                MainActivity.user.setSex(sex);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });
        //头像选择
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiImageSelectorActivity.startSelect(getActivity(),
                        RESULT_CHOOSE_PHOTO, 1, MultiImageSelectorActivity.MODE_SINGLE);
            }
        });

        setLayoutListener();
        //新建一个菜单
        setHasOptionsMenu(true);
        //保存数据，发送给服务器
        commitFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SendData().execute();
            }
        });
        checkImage(); //看看是不是可以放头像

        return view;
    }

    /**
     * 用Butterknife出问题
     * @param view
     */
    private void bindView(View view){
        llSpend = (LinearLayout) view.findViewById(R.id.ll_spend);
        llAler = (LinearLayout) view.findViewById(R.id.ll_aler);
        spSex = (Spinner)view.findViewById(R.id.sp_sex);
        etName=(EditText)view.findViewById(R.id.et_name);
        etMail=(EditText)view.findViewById(R.id.et_mail);
        commitFab = (FloatingActionButton)view.findViewById(R.id.commitFab);
        avatar = (CircleImageView)view.findViewById(R.id.avatar);
    }

    /**
     * 通知MainActivity切换fragment
     */
    private void setLayoutListener(){
        llSpend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new ListEvent("fragment_spend"));
            }
        });
        llAler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new ListEvent("fragment_aler"));
            }
        });
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i(TAG, "onCreateOptionsMenu()");
        menu.clear();
        inflater.inflate(R.menu.menu_account, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            //相当于Fragment的onResume
            Log.i(TAG,"v");
            checkImage();
        } else {
            //相当于Fragment的onPause，如果数据有改动，就传给服务器
            Log.i(TAG,"in");
            ((MainActivity)getActivity()).menuMultipleActions.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 因为EventBus好像出了点问题，所以这里就自定义接口然后进行回调
     */
    public void refresh(){
        updateImage(picPath);
    }


    private void checkImage(){
        if(MainActivity.aCache.getAsString("avatar") != null){
            picPath = MainActivity.aCache.getAsString("avatar");
            if(!picPath.equals(""))
                updateImage(picPath);
        }
    }

    /**
     * 刷新头像
     * @param image
     */
    private void updateImage(String image){
        Picasso.with(getActivity())
                .load(new File(image))
                .resize(200, 200)
                .centerCrop()
                .into(avatar);
    }


    class SendData extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            Gson gson = new Gson();
            String json = gson.toJson(MainActivity.user,User.class);
            return InternetUtil.postInfo(json,"user");
        }

        @Override
        protected void onPostExecute(Boolean f) {
            if(f){
                EventBus.getDefault().post(new MessageEvent("更新账户信息成功"));
            }
            else{
                EventBus.getDefault().post(new MessageEvent("更新账户信息失败，请检查网络"));
            }
        }
    }
    @Override
    public String toString() {
        return "fragment_account";
    }
}
