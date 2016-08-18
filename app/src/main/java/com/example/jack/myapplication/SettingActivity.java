package com.example.jack.myapplication;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import java.util.List;

/**
 * Created by Jack on 2016/8/17.
 */
public class SettingActivity extends PreferenceActivity {

    private static boolean mail;
    private static boolean like;
    private static boolean allergic;
    private static boolean vibrate;
    private static String frequency = "";
    private static String ringtone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        //这里把更新后的值全部发送给服务器

    }


    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }
    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || AllergicFragment.class.getName().equals(fragmentName);
    }

    /**
     * 用来设置邮箱和彩泥喜欢
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        private SwitchPreference switch_mail;
        private SwitchPreference switch_like;
        private ListPreference list_fre;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            initView();
            init();

            list_fre.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    //1表示一周，2表示两周，3表示一个月
                    frequency =  PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(),"1");
                    //在这里我就去保存需要所需要的值就可以了
                    // For list preferences, look up the correct display value in
                    // the preference's 'entries' list.
                    ListPreference listPreference = (ListPreference) preference;
                    int index = listPreference.findIndexOfValue(frequency);
                    Log.i("ListPreference",listPreference.getEntries()[index]+"");
                    // Set the summary to reflect the new value.
                    preference.setSummary(
                            index >= 0
                                    ? listPreference.getEntries()[index]
                                    : null);
                    return true;
                }
            });
            switch_mail.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {

                    mail = PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(),true);

                    return true;
                }
            });
            switch_like.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                   like =  PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(),true);

                    return true;
                }
            });
        }


        private void init(){
            mail = PreferenceManager
                    .getDefaultSharedPreferences(switch_mail.getContext())
                    .getBoolean(switch_mail.getKey(),true);

            frequency = PreferenceManager
                    .getDefaultSharedPreferences(list_fre.getContext())
                    .getString(list_fre.getKey(),"1");

            like = PreferenceManager
                    .getDefaultSharedPreferences(switch_like.getContext())
                    .getBoolean(switch_like.getKey(),true);

            int index = Integer.valueOf(frequency);
            switch_mail.setDefaultValue(mail);
            list_fre.setSummary(
                    index >= 0
                            ? list_fre.getEntries()[index]
                            : null);
        }
        private void initView(){
            switch_mail = (SwitchPreference) findPreference("mail");
            list_fre = (ListPreference)  findPreference("frequency_list");
            switch_like = (SwitchPreference) findPreference("like_switch");
        }
    }

    /**
     * 用来设置过敏源
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AllergicFragment extends PreferenceFragment {
        private SwitchPreference switch_allergic;
        private SwitchPreference switch_vibrate;
        private RingtonePreference allergic_ringtone;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_allergic);
            initView();
            init();

            switch_allergic.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    allergic =  PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(),true);

                    return true;
                }
            });

            switch_vibrate.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    vibrate =  PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(),true);

                    return true;
                }
            });

            allergic_ringtone.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    String stringValue = o.toString();
                    ringtone = o.toString();
                    if (TextUtils.isEmpty(stringValue)) {
                        // Empty values correspond to 'silent' (no ringtone).
                        preference.setSummary("静音");

                    } else {
                        Ringtone ringtone = RingtoneManager.getRingtone(
                                preference.getContext(), Uri.parse(stringValue));

                        if (ringtone == null) {
                            // Clear the summary if there was a lookup error.
                            preference.setSummary(null);
                        } else {
                            // Set the summary to reflect the new ringtone display
                            // name.
                            String name = ringtone.getTitle(preference.getContext());
                            preference.setSummary(name);
                        }
                    }
                    return true;
                }
            });
        }

        private void initView(){
            switch_allergic = (SwitchPreference) findPreference("allergic");
            switch_vibrate = (SwitchPreference)  findPreference("allergic_vibrate");
            allergic_ringtone = (RingtonePreference) findPreference("allergic_ringtone");
        }

        private void init(){
            allergic = PreferenceManager
                    .getDefaultSharedPreferences(switch_allergic.getContext())
                    .getBoolean(switch_allergic.getKey(),true);

            ringtone = PreferenceManager
                    .getDefaultSharedPreferences(allergic_ringtone.getContext())
                    .getString(allergic_ringtone.getKey(),"");

            vibrate = PreferenceManager
                    .getDefaultSharedPreferences(switch_vibrate.getContext())
                    .getBoolean(switch_vibrate.getKey(),true);
        }


    }
}
