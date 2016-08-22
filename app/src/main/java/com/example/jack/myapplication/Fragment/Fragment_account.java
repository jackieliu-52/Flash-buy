package com.example.jack.myapplication.Fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.example.jack.myapplication.Util.Event.ListEvent;
import com.example.jack.myapplication.Util.Event.MessageEvent;
import com.example.jack.myapplication.Util.InternetUtil;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;


/**
 * 设置账号信息，本来想用PreferFragment，但是不兼容v4下的Fragment
 * 只能手动绘制
 */
public class Fragment_account extends Fragment {
    final private String TAG = "Fragment_account";
    private Context mContext;
    /**
     * 单例对象实例
     */
    private static Fragment_account instance = null;

    ImageView ivAccount;
    EditText etName;
    EditText etMail;
    Spinner spSex;
    LinearLayout llAler;
    LinearLayout llSpend;

    private String name;
    private String sex;
    private String mail;
    private boolean isChanged = false;
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
                isChanged = true;
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
                isChanged = true;
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
                isChanged = true;
                String[] sexes = getResources().getStringArray(R.array.sex);
                sex = sexes[pos];
                MainActivity.user.setSex(sex);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });
        ivAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChanged = true;
                Toast.makeText(mContext,"你点击了头像",Toast.LENGTH_SHORT).show();
            }
        });

        setLayoutListener();
        //新建一个菜单
        setHasOptionsMenu(true);

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
        ivAccount=(ImageView)view.findViewById(R.id.iv_account);
        etName=(EditText)view.findViewById(R.id.et_name);
        etMail=(EditText)view.findViewById(R.id.et_mail);
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
            Log.i(TAG,"v");
            isChanged = false;
        } else {
            //相当于Fragment的onPause，如果数据有改动，就传给服务器
            Log.i(TAG,"in");
            if(isChanged){
                //传给服务器
                new SendData().execute();
            }
        }
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
