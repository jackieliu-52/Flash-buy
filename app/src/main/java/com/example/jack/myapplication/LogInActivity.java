package com.example.jack.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SignUpCallback;
import com.mikepenz.iconics.context.IconicsLayoutInflater;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * Created by Jack on 2016/8/10.
 */
public class LogInActivity  extends AppCompatActivity  {
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    /**
     * A dummy authentication store containing known user names and passwords.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "15111076742:233333", "13142390858:233333"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private View mGetPwd;
    //验证码
    private String code;
    AVUser avUser = new AVUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();  //手机号填充事件
        mPasswordView = (EditText) findViewById(R.id.password);
        //这里自动进行了缓存
        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser != null) {
            // 跳转到首页
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            //doNothing
        }

        Button ib_login =(Button) findViewById(R.id.ib_login);
        //Button的响应事件
        ib_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mGetPwd = findViewById(R.id.get_pwd);
        mGetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取验证码
                String mobile = mEmailView.getText().toString();
                if(isEmailValid(mobile)){
                    avUser.setEmail("878923730@qq.com");
                    avUser.setUsername("jack");
                    avUser.setPassword("123456");
                    avUser.setMobilePhoneNumber(mobile);

                    AVOSCloud.requestSMSCodeInBackground(avUser.getMobilePhoneNumber(), new RequestMobileCodeCallback() {
                        @Override
                        public void done(AVException e) {
                            // 发送失败可以查看 e 里面提供的信息
                            if(e == null){
                                mPasswordView.requestFocus();
                            }
                            else
                            {
                                Log.e("Home.OperationVerify",e.getMessage());
                            }
                        }
                    });
                }
                else {
                    mEmailView.setError("手机号错误");
                    mEmailView.requestFocus();
                }
            }
        });
    }

    /**
     * 自动填充手机号码
     */
    private void populateAutoComplete() {
        if (true) {
            return;
        }
    }

    /**
     * 判断是否是手机号
     * @param mobiles
     * @return
     */
    private boolean isEmailValid(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 判断是否是6位验证码
     * @param code
     * @return
     */
    private boolean isPasswordValid(String code) {
        return code.length() == 6;
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError("验证码不得为空");
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("手机号不得为空");
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError("请输入正确的手机号码");
            focusView = mEmailView;
            cancel = true;
        }
        //如果取消了,那么聚焦于那个有问题的TextView
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }



    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private boolean flag;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
            flag = false;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            //身份认证的过程(网络版)
            AVUser.signUpOrLoginByMobilePhoneInBackground(avUser.getMobilePhoneNumber(), mPassword, new LogInCallback<AVUser>() {
                @Override
                public void done(AVUser user, AVException e) {
                    // 如果 e 为空就可以表示登录成功了，并且 user 是一个全新的用户
                    if(e == null){
                        flag = true;
                        avUser.signUpInBackground(new SignUpCallback() {
                            public void done(AVException e) {
                                if (e == null) {
                                    Toast.makeText(getBaseContext(),"注册成功", Toast.LENGTH_SHORT).show();
                                    // successfully
                                } else {
                                    Toast.makeText(getBaseContext(),"您已经注册过了", Toast.LENGTH_SHORT).show();
                                    flag = true;
                                    Log.i("Home.OperationVerify",e.getMessage());
                                    // failed
                                }
                            }
                        });
                    }else {
                        if(!mPassword.equals("233333")) {
                            Toast.makeText(getBaseContext(), "验证码不对", Toast.LENGTH_SHORT).show();
                            Log.e("Home.DoOperationVerify", e.getMessage());
                        }
                    }
                }
            });

            if(flag) return true;
            //身份认证的过程(本地版)
            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // 如果之前没有注册过，那么可以注册一个新的账号

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //sucess应该是上一个传过来的
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                mPasswordView.setError("验证码错误");
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
