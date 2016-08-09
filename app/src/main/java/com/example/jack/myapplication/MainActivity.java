package com.example.jack.myapplication;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jack.myapplication.Fragment.Fragment1;
import com.example.jack.myapplication.Fragment.Fragment2;
import com.example.jack.myapplication.Fragment.Fragment_account;
import com.example.jack.myapplication.Fragment.Fragment_buy;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.mrengineer13.snackbar.SnackBar;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialize.util.UIUtils;

import com.example.jack.myapplication.R;
import java.util.ArrayList;
import java.util.List;

import me.next.slidebottompanel.SlideBottomPanel;


public class MainActivity extends AppCompatActivity {
    private static final int PROFILE_SETTING = 1;

    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    //底部窗口
    private SlideBottomPanel sbv;
    private ListView lv_cart;
    private ArrayList<String> cart;

    //Fragments
    private Fragment mContent = null; //当前显示的Fragment
    private Fragment1 f1 = null;
    private Fragment2 f2 = null;
    private Fragment_account fragment_account= null;
    private Fragment_buy fragment_buy= null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //打印内存
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        Log.i("内存",activityManager.getMemoryClass() + "");
        setContentView(R.layout.activity_main1);

        CreateDrawer(savedInstanceState);
        CreateBottomPanel();
        CreateButton();

        setListen();

        //设置默认fragment
        if (savedInstanceState == null) {
            f2 = Fragment2.GetInstance();
            mContent = f2;
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, f2,f2.toString()).commit(); //默认fragment
            result.setSelection(2, false);   //默认的选中DrawerItem
        }
        else{
            f1 = (Fragment1) getSupportFragmentManager().findFragmentByTag("f1");
            f2 = (Fragment2) getSupportFragmentManager().findFragmentByTag("f2");
            getSupportFragmentManager().beginTransaction()
                    .show(f2)
                    .hide(f1)
                    .commit();
        }

    }

    private void setListen(){
        ImageView iv_check_out=(ImageView)findViewById(R.id.iv_checkout);
        iv_check_out.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new SnackBar.Builder(MainActivity.this)
                        .withMessage("结账")
                        .withStyle(SnackBar.Style.INFO)
                        .withDuration((short)2000)
                        .show();
            }
        });
    }
    private void CreateBottomPanel() {
        sbv = (SlideBottomPanel) findViewById(R.id.sbv);
        lv_cart = (ListView) findViewById(R.id.list_cart);

        cart = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            cart.add("Item " + i);
        }
        for(String temp : cart)
            Log.i("cart",temp);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,R.layout.list_item,cart);
        lv_cart.setAdapter(adapter);
       // lv_cart.setAdapter(new ArrayAdapter<>(this, ,cart));
    }


    private void CreateButton(){
        final FloatingActionButton star = (FloatingActionButton) findViewById(R.id.star);
        star.setColorNormalResId(R.color.pink);
        star.setColorPressedResId(R.color.pink_pressed);
        star.setIcon(R.mipmap.ic_fab_star);
        FloatingActionButton actionC = new FloatingActionButton(getBaseContext());

        actionC.setTitle("Hide/Show Action above");
        actionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star.setVisibility(star.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });

        final FloatingActionsMenu menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.floating_button);
        menuMultipleActions.addButton(actionC);

        final FloatingActionButton cart = (FloatingActionButton) findViewById(R.id.cart);
        cart.setIcon(R.drawable.ic_add_shopping_cart_black_24dp);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sbv.displayPanel();    //打开下面的面板
            }
        });
    }


    private void CreateDrawer(Bundle savedInstanceState){
        // Handle Toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.drawer_item_compact_header);   //设置标题


        // Create a few sample profile
        final IProfile profile = new ProfileDrawerItem().withName("Jack").withEmail("878923730@gmail.com").withIcon(R.drawable.profile).withIdentifier(233);


        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(true)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        profile,
                        //don't ask but google uses 14dp for the add account icon in gmail but 20dp for the normal icons (like manage account)
                        new ProfileSettingDrawerItem().withName("Add Account").withDescription("Add new GitHub Account").withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_plus).actionBar().paddingDp(5).colorRes(R.color.material_drawer_dark_primary_text)).withIdentifier(PROFILE_SETTING),
                        new ProfileSettingDrawerItem().withName("Manage Account").withIcon(GoogleMaterial.Icon.gmd_settings)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        if(profile.getIdentifier() == 233)  //进入信息设置板块
                        {
                            fragment_account = Fragment_account.GetInstance();
                            switchContent(mContent,fragment_account);
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_buy).withIcon(FontAwesome.Icon.faw_gamepad).withIdentifier(3),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_custom).withIcon(FontAwesome.Icon.faw_eye).withIdentifier(2),
                        new SectionDrawerItem().withName(R.string.drawer_item_section_header),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_question).withEnabled(false),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_github),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_bullhorn)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null ) {
                            switch ((int)drawerItem.getIdentifier()){
                                case 1:
                                    f1 = Fragment1.GetInstance();
                                    switchContent(mContent,f1);
                                    new SnackBar.Builder(MainActivity.this)
                                            .withMessage("Fragment1")
                                            .withStyle(SnackBar.Style.INFO)
                                            .withDuration((short)2000)
                                            .show();
                                    break;
                                case 2:
                                    f2 = Fragment2.GetInstance();
                                    switchContent(mContent,f2);
                                    new SnackBar.Builder(MainActivity.this)
                                            .withMessage("Fragment2")
                                            .withStyle(SnackBar.Style.INFO)
                                            .withDuration((short)2000)
                                            .show();
                                    break;
                                case 3:
                                    fragment_buy = Fragment_buy.GetInstance();
                                    switchContent(mContent,fragment_buy);
                                    new SnackBar.Builder(MainActivity.this)
                                            .withMessage("fragment_buy")
                                            .withStyle(SnackBar.Style.INFO)
                                            .withDuration((short)2000)
                                            .show();
                                    break;
                            }

                           // startSupportActionMode(new ActionBarCallBack());
                           // findViewById(R.id.action_mode_bar).setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(MainActivity.this, R.attr.colorPrimary, R.color.material_drawer_primary));
                        }

                        if (drawerItem instanceof Nameable) {
                            String title = ((Nameable) drawerItem).getName().getText(MainActivity.this);
                            toolbar.setTitle(title);
                        }

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();


        result.updateBadge(1, new StringHolder(10 + ""));
        //set the back arrow in the toolbar
    //    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    public void switchContent(Fragment from, Fragment to) {
        if (mContent != to) {
            mContent = to;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    android.R.anim.fade_in, android.R.anim.fade_out);
            /*
            if(from.toString().equals("f1")){
                //关闭f1
                transaction.remove(from);
                if(!to.isAdded())
                    transaction.add(R.id.fragment_container, to,to.toString()).commit();
                else
                    transaction.show(to).commit();
                return;
            }
*/
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from).add(R.id.fragment_container, to,to.toString()).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.scan:
                Toast.makeText(MainActivity.this , "scan!" , Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this,TestActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (sbv.isPanelShowing()) {
            sbv.hide();
            return;
        }
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }



    class ActionBarCallBack implements ActionMode.Callback {

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //如果超过sdk-21版本
                getWindow().setStatusBarColor(UIUtils.getThemeColorFromAttrOrRes(MainActivity.this, R.attr.colorPrimaryDark, R.color.material_drawer_primary_dark));
            }

            mode.getMenuInflater().inflate(R.menu.cab, menu);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
    }
}
