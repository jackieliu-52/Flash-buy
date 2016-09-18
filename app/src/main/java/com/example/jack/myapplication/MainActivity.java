package com.example.jack.myapplication;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVUser;
import com.example.jack.myapplication.Adapter.ItemAdapter;
import com.example.jack.myapplication.Adapter.StarItemAdapter;
import com.example.jack.myapplication.Fragment.Fragment1;
import com.example.jack.myapplication.Fragment.Fragment2;
import com.example.jack.myapplication.Fragment.Fragment_account;
import com.example.jack.myapplication.Fragment.Fragment_aler;
import com.example.jack.myapplication.Fragment.Fragment_buy;
import com.example.jack.myapplication.Fragment.Fragment_item;
import com.example.jack.myapplication.Fragment.Fragment_sanzhuang;
import com.example.jack.myapplication.Fragment.Fragment_search;
import com.example.jack.myapplication.Fragment.Fragment_spend;
import com.example.jack.myapplication.Model.Aller_father;
import com.example.jack.myapplication.Model.BulkItem;
import com.example.jack.myapplication.Model.InternetItem;
import com.example.jack.myapplication.Model.Item;
import com.example.jack.myapplication.Model.LineItem;
import com.example.jack.myapplication.Model.Order;
import com.example.jack.myapplication.Model.TwoTuple;
import com.example.jack.myapplication.Model.User;
import com.example.jack.myapplication.Util.Cache.ACache;
import com.example.jack.myapplication.Util.Constant;
import com.example.jack.myapplication.Util.Event.ImageEvent;
import com.example.jack.myapplication.Util.Event.InternetEvent;
import com.example.jack.myapplication.Util.Event.ListEvent;
import com.example.jack.myapplication.Util.Event.MessageEvent;
import com.example.jack.myapplication.Util.Interface.NeedPageChanged;
import com.example.jack.myapplication.Util.Interface.refreshPic;
import com.example.jack.myapplication.Util.InternetUtil;
import com.example.jack.myapplication.Util.Palette.Palette;
import com.example.jack.myapplication.Util.Util;
import com.example.jack.myapplication.View.Transition.ItemTransition;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.mrengineer13.snackbar.SnackBar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.litesuits.common.assist.Toastor;
import com.litesuits.common.utils.ClipboardUtil;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.next.slidebottompanel.SlideBottomPanel;

/**
 * 主页
 */
public class MainActivity extends AppCompatActivity  {
    public static ACache aCache;
    public static boolean TESTMODE = false;  //默认不开启测试模式
    public static User user = new User(); //当前用户
    private Context mContext;

    SearchView mSearchView;
    private String mSearchText;  //搜索的字段
    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    public Toolbar toolbar = null;

    //Floating Button
    public FloatingActionsMenu menuMultipleActions;
    private CoordinatorLayout clContent;

    //底部窗口
    private SlideBottomPanel sbv;
    private ListView lv_cart;
    public static ArrayList<LineItem> cart;   //当前购物车
    public static List<BulkItem> bulkItems = new ArrayList<>();  //当前所有散装商品
    private TextView tv_total_cost;

    //Fragments
    private Stack<Fragment> fragments = null;   //作为一个f ragments的栈，来管理fragment的回退
    public Fragment mContent = null; //当前显示的Fragment
    private Fragment1 f1 = null;
    private Fragment2 f2 = null;
    private Fragment_account fragment_account = null;
    private Fragment_buy fragment_buy = null;
    private Fragment_sanzhuang fragment_sanzhuang = null;
    private Fragment_item fragment_item = null;

    public static final int DRAWER_BUY = 1;
    public static final int DRAWER_SMART = 2;
    public static final int DRAWER_HOME = 3;
    public static final int DRAWER_ABOUT = 4;
    public static final int DRAWER_LOGOUT = 5;
    public static final int DRAWER_SETTING = 6;

    private refreshPic mRefreshPic;  //刷新图片的接口，让fragment立刻刷新图片
    public NeedPageChanged mNeedPageChanged;  //改变Fragment_buy中viewpager的接口

    private Toast toast;

    private PaletteTask mPaletteTask;
    private Palette.Result mResult;  //结果

    @Override
    protected void onStart(){
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //使用sharedElement
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        //使用Icon库
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        EventBus.getDefault().register(this);
        aCache = ACache.get(this); //获得缓存实例
        mContext = this;
        toast = Toast.makeText(getApplicationContext(), "确定退出？", Toast.LENGTH_SHORT);

        super.onCreate(savedInstanceState);

        getUser();

        //打印内存
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        Log.i("内存最大值",activityManager.getMemoryClass() + "");
        setContentView(R.layout.activity_main1);

        CreateDrawer(savedInstanceState);
        CreateBottomPanel();
        CreateButton();


        EventBus.getDefault().post(new InternetEvent(InternetUtil.cartUrl,Constant.REQUEST_Cart));
//        EventBus.getDefault().post(new InternetEvent(InternetUtil.bulkUrl,Constant.REQUEST_Bulk));

        //设置默认fragment
        if (savedInstanceState == null) {
            fragments = new Stack<>();
            f2 = Fragment2.GetInstance();
            mContent = f2;

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, f2,f2.toString()).commit(); //默认fragment
            result.setSelection(2, false);   //默认的选中DrawerItem
        }
        else{
            if(f1 != null)
                f1 = (Fragment1) getSupportFragmentManager().findFragmentByTag("f1");
            if(f2 != null)
                f2 = (Fragment2) getSupportFragmentManager().findFragmentByTag("f2");
            if(fragment_account != null)
                fragment_account = (Fragment_account) getSupportFragmentManager().findFragmentByTag("fragment_account");
            if(fragment_buy != null)
                fragment_buy = (Fragment_buy)  getSupportFragmentManager().findFragmentByTag("fragment_buy");

            getSupportFragmentManager().beginTransaction()
                    .show(f2)
                    .hide(f1)
                    .hide(fragment_buy)
                    .hide(fragment_account)
                    .commit();
        }

    }

    /**
     * 得到当前的用户，注意区分网络版和本地版
     */
    private void getUser(){
        AVUser avUser = AVUser.getCurrentUser();
        if(avUser != null) {
            if(avUser.getMobilePhoneNumber() != null)
                user.setId(avUser.getMobilePhoneNumber());
            else
                user.setId("11111111111");
            if(avUser.getUsername() != null)
                user.setName(avUser.getUsername());
            else
                user.setName("jack");
        } else{
            user.setId("11111111111");
            user.setName("jack");
        }
      //  Log.i("username",avUser.getUsername() );
    }


    private void CreateBottomPanel() {
        sbv = (SlideBottomPanel) findViewById(R.id.sbv);
        lv_cart = (ListView) findViewById(R.id.list_cart);
        tv_total_cost = (TextView) findViewById(R.id.tv_total_cost);
        lv_cart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                if(position == 0)
                    return;
                //获得Item
                Item item = cart.get(position).getItem();
                new SnackBar.Builder(MainActivity.this)
                        .withMessage(item.getName())
                        .withStyle(SnackBar.Style.INFO)
                        .withDuration((short)2000)
                        .show();
            }
        });
        //底部面板的结账按钮
        ImageView iv_check_out = (ImageView)findViewById(R.id.iv_checkout);
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

                //支付前要进行判断，是否还有东西没有买
                boolean flag = false;
                if(Fragment_buy.planItems.size() == 0)
                    flag = true;
                if(!flag){
                    new MaterialDialog.Builder(mContext)
                            .title("温馨提醒")
                            .content("您的预购清单中还有商品没有购买，是否要支付")
                            .positiveText("是")
                            .negativeText("否")
                            .onAny(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (which == DialogAction.POSITIVE) {
                                        //生成新的订单，清空购物车，跳转支付                                                                                                                         页面
                                        Order order = new Order(cart,"订单号",user.getId(), Util.getCurrentDate(),"alipay","家润多",0,0);
                                        User.orders.add(order);
                                        lv_cart.setAdapter(null);
                                        cart = new ArrayList<>();
                                        Fragment_buy.planItems = new ArrayList<>();
                                        //跳转到订单详情进行支付
                                        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                                        intent.putExtra("order", order);
                                        startActivity(intent);
                                    }
                                }
                            })
                            .show();
                } else{
                    //生成新的订单，清空购物车，跳转支付页面
                    Order order = new Order(cart,"订单号",user.getId(), Util.getCurrentDate(),"alipay","家润多",0,0);
                    User.orders.add(order);
                    lv_cart.setAdapter(null);
                    cart = new ArrayList<>();
                    Fragment_buy.planItems = new ArrayList<>();
                    //跳转到订单详情进行支付
                    Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                    intent.putExtra("order", order);
                    startActivity(intent);
                }

            }
        });

    }


    private void CreateButton(){
        clContent = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        final FloatingActionButton star = (FloatingActionButton) findViewById(R.id.star);

        star.setColorNormalResId(R.color.menu_pink);
        star.setColorPressedResId(R.color.menu_pink_pressed);
        star.setIcon(R.mipmap.ic_fab_star);
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //关闭Menu
                menuMultipleActions.collapse();
                //打开收藏的商品
                lv_cart.setAdapter(null);
                getStarItems();
                sbv.displayPanel();    //打开下面的面板
            }
        });
        //Menu选项
        menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.floating_button);
        final FloatingActionButton cart = (FloatingActionButton) findViewById(R.id.cart);
        cart.setIcon(R.drawable.ic_shopping_cart_white_24px);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //关闭Menu
                menuMultipleActions.collapse();
                if(!TESTMODE) {
                    //先清空购物车
                    lv_cart.setAdapter(null);

                    EventBus.getDefault().post(new InternetEvent(InternetUtil.cartUrl, Constant.REQUEST_Cart));
                }
                sbv.displayPanel();    //打开下面的面板
            }
        });
    }

    /**
     * 得到Star的商品
     */
    private void getStarItems(){
        StarItemAdapter starItemAdapter = new StarItemAdapter(mContext,R.layout.star_item,User.starItems);
        lv_cart.setAdapter(starItemAdapter);
    }

    private void CreateDrawer(Bundle savedInstanceState){
        // Handle Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.drawer_item_compact_header);   //设置标题


        // Create a few sample profile
        final IProfile profile = new ProfileDrawerItem().withName(user.getName()).withEmail(user.getId()).withIcon(R.drawable.profile).withIdentifier(233);


        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(true)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        profile
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        if(profile.getIdentifier() == 233)  //进入信息设置板块
                        {
                            fragment_account = Fragment_account.GetInstance();
                            switchContent(mContent,fragment_account);
                            mRefreshPic = fragment_account;
                            menuMultipleActions.setVisibility(View.INVISIBLE);
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
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home).withIdentifier(DRAWER_HOME),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_buy).withIcon(FontAwesome.Icon.faw_shopping_cart).withIdentifier(DRAWER_BUY),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_custom).withIcon(FontAwesome.Icon.faw_eye).withIdentifier(DRAWER_SMART),
                        new SectionDrawerItem().withName(R.string.drawer_item_section_header),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(DRAWER_SETTING),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_log_out).withIcon(FontAwesome.Icon.faw_question).withIdentifier(DRAWER_LOGOUT),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_github).withIdentifier(DRAWER_ABOUT),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_bullhorn).withIdentifier(8),
                        new DividerDrawerItem(),
                        new SwitchDrawerItem().withName("测试模式").withIcon(Octicons.Icon.oct_tools).withChecked(false).withOnCheckedChangeListener(onCheckedChangeListener)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null ) {
                            switch ((int)drawerItem.getIdentifier()){
                                case DRAWER_HOME:
                                    if(!(getSupportActionBar().isShowing()) )
                                        getSupportActionBar().show();
                                    f1 = Fragment1.GetInstance();
                                    switchContent(mContent,f1);

                                    break;
                                case 2:
                                    if(!(getSupportActionBar().isShowing()) )
                                        getSupportActionBar().show();
                                    f2 = Fragment2.GetInstance();
                                    switchContent(mContent,f2);

                                    break;
                                case DRAWER_BUY:
                                    if(!(getSupportActionBar().isShowing()) )
                                        getSupportActionBar().show();
                                    fragment_buy = Fragment_buy.GetInstance();
                                    mNeedPageChanged = fragment_buy;
                                    switchContent(mContent,fragment_buy);
                                    break;
                                case DRAWER_SETTING:
                                    //打开设置
                                    Intent intent = new Intent(MainActivity.this,SettingActivity.class);
                                    startActivity(intent);
                                    break;
                                case DRAWER_LOGOUT:
                                    //登出
                                    AVUser.logOut();// 清除缓存用户对象
                                    break;
                                case DRAWER_ABOUT:
                                    break;
                                default:
                                    EventBus.getDefault().post(new MessageEvent("暂未开发"));
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

        //添加动态
        result.updateBadge(1, new StringHolder(10 + ""));
        //set the back arrow in the toolbar
    //    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    /**
     * 是否开启测试模式
     */
    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            TESTMODE = isChecked;
            new SnackBar.Builder(MainActivity.this)
                        .withMessage("测试模式："+ isChecked )
                        .withStyle(SnackBar.Style.INFO)
                        .withDuration((short)2000)
                        .show();
            if(isChecked){
                //测试
                testMode();
            }
            Log.i("material-drawer", "DrawerItem: " + ((Nameable) drawerItem).getName() + " - toggleChecked: " + isChecked);

        }
    };

    /**
     * 当fragment进行切换时，采用隐藏与显示的方法加载fragment以防止数据的重复加载
     * @param from
     * @param to
     */
    public void switchContent(Fragment from, Fragment to) {


        if(from instanceof Fragment_item && to instanceof Fragment_item){
            //不加入栈中
            mContent = to;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            transaction.replace(R.id.fragment_container,to,to.toString()).commit();
            return;
        }

        if (mContent != to) {
            //将跳转的页面加入栈中
            fragments.push(from);
            mContent = to;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    android.R.anim.fade_in, android.R.anim.fade_out);
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from).add(R.id.fragment_container, to,to.toString()).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        }
    }

     /**
     * sharedElement切换
     * @param from
     * @param to
     * @param view 共享的元素
     */
    public void switchContentBySharedElement(Fragment from, Fragment to,View view) {
        if(to instanceof Fragment_item){
            fragments.push(from);   //加入回退栈

            mContent = to;
            //设置sharedItem
            fragment_item.setSharedElementEnterTransition(new ItemTransition());
            mContent.setExitTransition(new Fade());

            fragment_item.setEnterTransition(new Fade());
            fragment_item.setSharedElementReturnTransition(new ItemTransition());

            getSupportFragmentManager().beginTransaction()
                    .addSharedElement(view, getResources().getString(R.string.image_transition))
                    .hide(from)
                    .add(R.id.fragment_container, to,to.toString())
                    .commitAllowingStateLoss();
        }else{
            Log.e("SharedElement","switchContentBySharedElement error");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        //搜索功能
        final MenuItem item = menu.findItem(R.id.ab_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(item);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ClipboardUtil.getLatestText(mContext);  //得到剪贴板内容，询问是否要搜索
                doSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSearchText = newText ;

                return true;
            }
        });
        return true;
    }

    /**
     * 搜索商品
     */
    private void doSearch(String query){
        Toastor toastor = new Toastor(this);
        toastor.showSingleLongToast(query);
        EventBus.getDefault().post(new InternetEvent(query,Constant.REQUEST_Search));
    }

    /**
     * 菜单选择
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.scan:
                Intent intent =new Intent(this,ScanActivity.class);
                intent.putExtra(Constant.REQUEST_SCAN_MODE, Constant.REQUEST_SCAN_MODE_ALL_MODE);
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
        Log.i("MainActivity","onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onBackPressed() {

        if (sbv.isPanelShowing()) {
            sbv.hide();
            return;
        }
        if (menuMultipleActions.isExpanded() ){
            menuMultipleActions.collapse();
            return;
        }
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
            return;
        }
        if(!fragments.empty()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!(getSupportActionBar().isShowing()) )
                        getSupportActionBar().show();
                }
            });
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    android.R.anim.fade_in, android.R.anim.fade_out);
            transaction.hide(mContent);

            //获得要被回退的fragment
            mContent = fragments.pop();
            transaction.show(mContent).commit(); // 隐藏当前的fragment
            //与此同时，还需要将drawer的选中Item改变

            if(mContent instanceof  Fragment2)
                result.setSelection(2, false);
            if(mContent instanceof  Fragment1)
                result.setSelection(1, false);
            if(mContent instanceof  Fragment_buy)
                result.setSelection(3, false);
            return;
        }
        Log.i("finish","1");

        quitToast();

    }
    private void quitToast() {
        if(null == toast.getView().getParent()){
            toast.show();
        }else{
            this.finish();
        }
    }

    /**
     * 因为有个Activity用到了WebView，但是根据网上说法，WebView可能没有正常地释放资源
     * 所以这里偷懒选择了这样一种方法来保证程序退出之后没有另外泄露
     */
    @Override
    protected void onDestroy(){
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        System.exit(0);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void getInfo(InternetEvent internetEvent) {
        switch (internetEvent.type){
            case Constant.REQUEST_INTERNET_BAR:
                String httpUrl = "http://apis.baidu.com/3023/barcode/barcode";
                String httpArg = "barcode=" + internetEvent.message;
                httpUrl = httpUrl + "?" + httpArg;
                //保存网页信息
                String info;

                try {
                    URL url = new URL(httpUrl);
                    HttpURLConnection connection = (HttpURLConnection) url
                            .openConnection();
                    connection.setRequestMethod("GET");
                    // 填入apikey到HTTP header
                    connection.setRequestProperty("apikey",  "ab7d6eef4f735da9892ee2c6682f5088");
                    connection.connect();
                    //网页返回的状态码
                    int code = connection.getResponseCode();

                    Log.i("result","状态码 ：" + code);
                    if(code == 200) {
                        InputStream is = connection.getInputStream();
                        info = Util.readStream(is);
                        //避免Unicode转义
                        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                        //将json转换为一个InternetItem
                        InternetItem internetItem = gson.fromJson(info,InternetItem.class);
                        Fragment_item.item = new Item(internetItem);  //获得一个Item
                        //首先实例化fragment
                        Fragment_item fragment_item = new Fragment_item();
                        switchContent(mContent,fragment_item);
                    }
                    else {
                        //没有网络不能跳转
                        EventBus.getDefault().post(new MessageEvent("查询商品失败，请检查网络"));
                    }


                } catch (Exception e) {
                    //没有网络不能跳转
                    EventBus.getDefault().post(new MessageEvent("查询商品失败，请检查网络"));
                    e.printStackTrace();
                    Log.i("result","Exception:"+ e.toString() );
                }
                break;
            case Constant.REQUEST_Cart:
                initCart(internetEvent.message); //初始化购物车
                break;
            case Constant.REQUEST_Bulk:
                EventBus.getDefault().post("刷新散装食品！");
                initBulk(internetEvent.message);  //初始化散装商品
                break;
            case Constant.REQUEST_Search:
                if(getSearchInfo(internetEvent.message)) {
                    //成功
                    Fragment_search fragment_search = new Fragment_search();
                    switchContent(mContent,fragment_search);
                }
                else{
                    //搜索没成功
                    EventBus.getDefault().post(new MessageEvent("搜索商品失败，请检查网络"));
                }
                break;
            case Constant.POST_Aller:
                Gson gson = new Gson();
                String json = gson.toJson(Fragment_aler.mAllergens, Aller_father.class);
                if(InternetUtil.postInfo(json,"aller?userId=9")){
                    //设置成功
                    EventBus.getDefault().post(new MessageEvent("设置过敏源完成"));
                }else {
                    //设置没有成功
                    EventBus.getDefault().post(new MessageEvent("设置过敏源失败，请检查网络"));
                }
                break;
            default:
                Log.e("getInfo()","getInfo()" + internetEvent.type);
                break;
        }
    }

    private boolean getSearchInfo(String name){
        String searchUrl = InternetUtil.searchUrl + name;
        try{
            URL temp = new URL(searchUrl);
            HttpURLConnection connection = (HttpURLConnection) temp
                    .openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            //网页返回的状态码
            int code = connection.getResponseCode();
            if(code == 200){
                InputStream is = connection.getInputStream();
                String json = Util.readStream(is);
                //把json转换成一个 Item数组，应该用Fragment_search的一个static变量保存结果
                if(json != null) {
                    if (!json.equals(""))
                        Fragment_search.items = Util.fromJsonArray(json, Item.class);
                    else
                        Fragment_search.items = new ArrayList<>();
                }
                return true;
            }
            else {
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            EventBus.getDefault().post(new MessageEvent("搜索商品失败，请检查网络"));
            return false;
        }
    }

    /**
     * 向服务器请求散装商品
     * @param url
     */
    private void initBulk(String url){
        String json;
        try {
            URL url1 = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) url1
                    .openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            //网页返回的状态码
            int code = connection.getResponseCode();
            if(code == 200) {
                InputStream is = connection.getInputStream();
                json = Util.readStream(is);

                if(json != null) {
                    //将json转换为一个Bulk对象的数组
                    if(!json.equals("")) {
                        bulkItems = Util.fromJsonArray(json, BulkItem.class);
                        //把散装商品添加入cart
                        for(BulkItem i:bulkItems){
                            cart.add(new LineItem(i));
                        }
                    }
                }


                double total_price = 0;
                for(LineItem lineItem:cart){
                    total_price += lineItem.getUnitPrice();
                }
                final double total = total_price;

                final ItemAdapter adapter = new ItemAdapter(this,R.layout.list_item,cart);

                //在主线程中去更新UI
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lv_cart.setAdapter(adapter);

                        tv_total_cost.setText("总价 ：" + total + "元");
                    }
                });
            }
            else {
                EventBus.getDefault().post(new MessageEvent("刷新散装商品失败，请检查网络"));
            }

        }
        catch (Exception e) {
            EventBus.getDefault().post(new MessageEvent("刷新散装商品失败，请检查网络"));
            e.printStackTrace();
        }
    }

    /**
     * 向服务器请求购物车信息
     * @param url
     */
    private void initCart(String url){
        cart = new ArrayList<>();
        //因为这里有个bug，所以我这里处理的时候先加了一个Item
        Item item1 = new Item();
        item1.setName("");
        item1.setImage("");
        item1.setPrice(0);
        LineItem lineItem1 = new LineItem();
        lineItem1.setItem(item1);
        lineItem1.setNum(1);
        //这里有一个小bug,第一个东西不能显示出来
        cart.add(lineItem1);

        String json;
        try {
            URL url1 = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) url1
                    .openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            //网页返回的状态码
            int code = connection.getResponseCode();
            Log.i("购物车查询","结果码" + code);
            if(code == 200) {
                InputStream is = connection.getInputStream();
                json = Util.readStream(is);
                //避免Unicode转义
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                //将json转换为一个Order

                Order order = gson.fromJson(json,Order.class);

                //将剩下的加入购物车
                cart.addAll(order.getLineItems());

                double total_price = 0;
                for(LineItem lineItem:cart){
                    for(Item tempItem:Fragment_buy.planItems){
                        if(lineItem.getItem().getName().equals(tempItem.getName())){
                            Fragment_buy.planItems.remove(tempItem); //移除预购商品
                        }
                    }
                    total_price += lineItem.getUnitPrice();
                }

                final double total = total_price;

                final ItemAdapter adapter = new ItemAdapter(this,R.layout.list_item,cart);

                //在主线程中去更新UI
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lv_cart.setAdapter(adapter);

                        tv_total_cost.setText("总价 ：" + total + "元");
                    }
                });
            }
            else {
                EventBus.getDefault().post(new MessageEvent("刷新购物车失败，请检查网络"));
            }

        }
        catch (Exception e) {
            EventBus.getDefault().post(new MessageEvent("刷新购物车失败，请检查网络"));
            e.printStackTrace();
        }

    }

    /**
     * 测试模式下的初始化
     */
    private void testMode(){
        cart = new ArrayList<>();
        //因为这里有个bug，所以我这里处理的时候先加了一个Item
        Item item1 = new Item();
        item1.setName("");
        item1.setImage("");
        item1.setPrice(0);
        LineItem lineItem = new LineItem();
        lineItem.setItem(item1);
        lineItem.setNum(1);
        //这里有一个小bug,第一个东西不能显示出来
        cart.add(lineItem);

//        Item item2 = new Item();
//        item2.setName("安慕希酸奶");
//        item2.setPrice(59.4);
//        item2.setImage("http://obsyvbwp3.bkt.clouddn.com/133.JPG");
//        item2.setIid("1330");
//        item2.setPid("13");
//        item2.setSource("中国");
//        item2.setSize("205g*12");
//
//        Item item3 = new Item();
//        item3.setName("三只松鼠夏威夷果");
//        item3.setImage("http://obsyvbwp3.bkt.clouddn.com/134.JPG");
//        item3.setPrice(5);
//        LineItem lineItem3 = new LineItem();
//        lineItem3.setItem(item3);
//        lineItem3.setNum(1);
//        cart.add(lineItem3);

        final ItemAdapter adapter = new ItemAdapter(this,R.layout.list_item,cart);

        //在主线程中去更新UI
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lv_cart.setAdapter(adapter);
                double total_price = 0;
                for(LineItem lineItem:cart){
                    total_price += lineItem.getUnitPrice();
                }
                tv_total_cost.setText(total_price + "元");
            }
        });

        //自己定义一些历史订单
        EventBus.getDefault().post(new ListEvent("init"));
        bulkItems = new ArrayList<>();
        //自定义散装商品
        BulkItem bulkitem = new BulkItem();
        bulkitem.setName("猕猴桃");
        bulkitem.setImage("http://obsyvbwp3.bkt.clouddn.com/mihoutao.jpg");
        bulkitem.setPrice(15);
        bulkitem.setWeight(0.255);
        bulkitem.setAttr1("闭光存储");
        //5天以前生产
        bulkitem.setProduceTime(Util.getBefoceTime(5));
        //获得到期时间
        bulkitem.jisuan();

        BulkItem bulkitem1 = new BulkItem();
        bulkitem1.setName("花生");
        bulkitem1.setImage("http://img2.imgtn.bdimg.com/it/u=1379469998,3665416882&fm=206&gp=0.jpg");
        bulkitem1.setPrice(5.00);
        bulkitem1.setWeight(2.33);
        bulkitem1.setAttr1("冷藏");
        bulkitem1.setShelfTime(10);
        //19天前生产
        bulkitem1.setProduceTime(Util.getBefoceTime(19));
        bulkitem1.jisuan();

        bulkItems.add(bulkitem);
        bulkItems.add(bulkitem1);

        //把散装商品添加入cart
        for(BulkItem i:bulkItems){
            cart.add(new LineItem(i));
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void snackBar(MessageEvent messageEvent){

        Snackbar.make(clContent, messageEvent.message, Snackbar.LENGTH_SHORT).show();
//        new SnackBar.Builder(MainActivity.this)
//                .withMessage(messageEvent.message)
//                .withStyle(SnackBar.Style.ALERT)
//                .withDuration((short)2000)
//                .show();
    }

    /**
     * 本来只想用来做初始化订单之类的信息，但是发现可能注册的监听事件太多，
     * 所以这里同时进行了切换服务
     * @param listEvent
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void getList(ListEvent listEvent){
        switch (listEvent.message){
            //cardView历史订单
            case "init":
                User.orders = new ArrayList<>();
                Item item1 = new Item();
                item1.setName("小熊酸奶机");
                item1.setImage("http://obsyvbwp3.bkt.clouddn.com/161.JPG");
                item1.setPrice(149);
                Item item2 = new Item();
                item2.setName("九阳智能电压力锅");
                item2.setImage("http://obsyvbwp3.bkt.clouddn.com/162.JPG");
                item2.setPrice(199);
                Item item3 = new Item();
                item3.setPrice(33);
                item3.setName("雀巢速溶咖啡");
                item3.setImage("http://obsyvbwp3.bkt.clouddn.com/171.JPG");

                Item item5 = new Item();
                item5.setName("香楠玫瑰鲜花饼");
                item5.setPrice(3.9);
                item5.setImage("http://obsyvbwp3.bkt.clouddn.com/1380.JPG");
                item5.setIid("1380");
                item5.setPid("13");
                item5.setSource("中国");
                item5.setSize("60g");

                Item item8 = new Item();
                item8.setName("致中和龟苓膏");
                item8.setPrice(14.3);
                item8.setImage("http://obsyvbwp3.bkt.clouddn.com/1382.JPG");
                item8.setIid("1382");
                item8.setPid("13");
                item8.setSource("中国");
                item8.setSize("80g");

                Item item9 = new Item();
                item9.setName("姚太太榴莲干");
                item9.setPrice(12.8);
                item9.setImage("http://obsyvbwp3.bkt.clouddn.com/1384.JPG");
                item9.setIid("1384");
                item9.setPid("13");
                item9.setSource("中国");
                item9.setSize("30g");

                ArrayList<LineItem> lineItems = new ArrayList<>();
                LineItem lineItem1 = new LineItem();
                lineItem1.setItem(item1);
                lineItem1.setNum(1);

                LineItem lineItem2 = new LineItem();
                lineItem2.setItem(item2);
                lineItem2.setNum(2);

                LineItem lineItem3 = new LineItem();
                lineItem3.setItem(item3);
                lineItem3.setNum(1);

                LineItem lineItem4 = new LineItem();
                lineItem4.setItem(item5);
                lineItem4.setNum(1);
                LineItem lineItem5 = new LineItem();
                lineItem5.setItem(item8);
                lineItem5.setNum(1);
                LineItem lineItem9 = new LineItem();
                lineItem9.setItem(item9);
                lineItem9.setNum(1);

                lineItems.add(lineItem1);
                lineItems.add(lineItem2);
                lineItems.add(lineItem4);
                Order order1 = new Order(lineItems,"1","2","8/10","aliPay","家乐福",0,0);

                User.orders.add(order1);
                ArrayList<LineItem> lineItems1 = new ArrayList<>();
                lineItems1.addAll(lineItems);
                lineItems1.add(lineItem3);
                lineItems1.add(lineItem5);
                lineItems1.add(lineItem9);

                Order order2 = new Order(lineItems1,"2","2","8/12","weixin","沃尔玛",0,1);

                User.orders.add(order2);
                break;
            case "update":
                break;
            case "fragment_spend":
                Fragment_spend fragment_spend = new Fragment_spend();
                switchContent(mContent,fragment_spend);
                break;
            case "fragment_aler":
                Fragment_aler fragment_aler = new Fragment_aler();
                switchContent(mContent,fragment_aler);
                break;
            case "fragment_sz":
                fragment_sanzhuang = Fragment_sanzhuang.GetInstance();
                switchContent(mContent,fragment_sanzhuang);
                break;
            case "fragment_buy":
                fragment_buy = Fragment_buy.GetInstance();
                switchContent(mContent,fragment_buy);
                break;
            case "fragment_item":
                //首先实例化fragment
                fragment_item =  Fragment_item.newInstance(Fragment_item.item.getImage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //隐藏toolbar
                        if(getSupportActionBar().isShowing() )
                            getSupportActionBar().hide();
                    }
                });
                //获取图片的调色
                mPaletteTask = new PaletteTask();
                mPaletteTask.execute((Void) null);
                //如果API高于21，就动画切换
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    switchContentBySharedElement(mContent,fragment_item,listEvent.mView);  //切换
                }else {
                    switchContent(mContent,fragment_item);
                }
                break;
            default:
                Log.e("getList()","cann't find fragment" + listEvent.message);
                break;
        }

    }

    /**
     * 获得图片的地址并且加载图片
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Fragment_account.RESULT_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> mSelectPath =
                        data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);

                for (String p : mSelectPath) {
                    Fragment_account.picPath = p;   //把图片传给它
                    aCache.put("avatar",p,365 * ACache.TIME_DAY); //保存图片
                    mRefreshPic.refresh();
                }
            }
        }
    }

    public class PaletteTask extends AsyncTask<Void, Void, Boolean> {
        //调色处理
        @Override
        protected Boolean doInBackground(Void... params) {
            Bitmap bitmap = Util.returnBitmap(Uri.parse(Fragment_item.item.getImage()));
            if(bitmap != null){
                mResult = Palette.extract(bitmap,0.5f);
                return true;
            }else
                return false;

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success){
                //设置背景颜色，应该需要在主线程中去运行
                //不知道为什么没作用
                fragment_item.getView().setBackgroundColor(mResult.getBackgroundColor());

            }else{
                //donothing
            }
        }
    }

}
