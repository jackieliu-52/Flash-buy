package com.example.jack.myapplication;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jack.myapplication.Adapter.ItemAdapter;
import com.example.jack.myapplication.Fragment.Fragment1;
import com.example.jack.myapplication.Fragment.Fragment2;
import com.example.jack.myapplication.Fragment.Fragment_account;
import com.example.jack.myapplication.Fragment.Fragment_buy;
import com.example.jack.myapplication.Fragment.Fragment_item;
import com.example.jack.myapplication.Model.InternetItem;
import com.example.jack.myapplication.Model.Item;
import com.example.jack.myapplication.Model.LineItem;
import com.example.jack.myapplication.Model.Order;
import com.example.jack.myapplication.Model.User;
import com.example.jack.myapplication.Util.Constant;
import com.example.jack.myapplication.Util.Event.InternetEvent;
import com.example.jack.myapplication.Util.Event.ListEvent;
import com.example.jack.myapplication.Util.Event.MessageEvent;
import com.example.jack.myapplication.Util.Util;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.mrengineer13.snackbar.SnackBar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsContextWrapper;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Stack;

import me.next.slidebottompanel.SlideBottomPanel;

/**
 * test
 */
public class MainActivity extends AppCompatActivity  {
    private static final int PROFILE_SETTING = 1;
    public static Activity instance;
    private User user = new User(); //当前用户

    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    public Toolbar toolbar = null;


    //底部窗口
    private SlideBottomPanel sbv;
    private ListView lv_cart;
    private ArrayList<LineItem> cart;
    private TextView tv_total_cost;

    //Fragments
    private Stack<Fragment> fragments = null;   //作为一个fragments的栈
    private Fragment mContent = null; //当前显示的Fragment
    private Fragment1 f1 = null;
    private Fragment2 f2 = null;
    private Fragment_account fragment_account = null;
    private Fragment_buy fragment_buy = null;
    private Fragment_item fragment_item = null;

    @Override
    protected void onStart(){
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //使用Icon
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        EventBus.getDefault().post(new ListEvent("init"));
        //关闭上一个Activity
        instance.finish();

        user.setId("jack");
        //打印内存
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        Log.i("内存最大值",activityManager.getMemoryClass() + "");
        setContentView(R.layout.activity_main1);

        CreateDrawer(savedInstanceState);
        CreateBottomPanel();
        CreateButton();

        setListen();

        //设置默认fragment
        if (savedInstanceState == null) {
            fragments = new Stack<>();
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


                //生成新的订单，清空购物车，跳转支付页面
                Order order = new Order(cart,"订单号",user.getId(), Util.getCurrentDate(),"alipay","家润多",0,0);
                User.orders.add(order);
                lv_cart.setAdapter(null);
                cart = new ArrayList<>();
            }
        });
    }

    private void CreateBottomPanel() {
        sbv = (SlideBottomPanel) findViewById(R.id.sbv);
        lv_cart = (ListView) findViewById(R.id.list_cart);
        tv_total_cost = (TextView) findViewById(R.id.tv_total_cost);




        EventBus.getDefault().post(new InternetEvent("http://localhost:8080/Flash-buy/cart?cartNumber=" +
                "1&userId="+user.getId(),Constant.REQUEST_Cart));



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
        cart.setIcon(R.mipmap.ic_shopping_cart_black_24dp);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //先清空购物车
                lv_cart.setAdapter(null);
                EventBus.getDefault().post(new InternetEvent("http://localhost:8080/Flash-buy/cart?cartNumber=" +
                        "1&userId="+user.getId(),Constant.REQUEST_Cart));
                sbv.displayPanel();    //打开下面的面板
            }
        });
    }


    private void CreateDrawer(Bundle savedInstanceState){
        // Handle Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
                        new PrimaryDrawerItem().withName(R.string.drawer_item_buy).withIcon(FontAwesome.Icon.faw_shopping_cart).withIdentifier(3),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_custom).withIcon(FontAwesome.Icon.faw_eye).withIdentifier(2),
                        new SectionDrawerItem().withName(R.string.drawer_item_section_header),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(5),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_question).withEnabled(false).withIdentifier(6),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_github).withIdentifier(7),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_bullhorn).withIdentifier(8),
                        new DividerDrawerItem(),
                        new SwitchDrawerItem().withName("收藏夹").withIcon(Octicons.Icon.oct_tools).withChecked(true).withOnCheckedChangeListener(onCheckedChangeListener)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null ) {
                            switch ((int)drawerItem.getIdentifier()){
                                case 1:
                                    if(!(getSupportActionBar().isShowing()) )
                                        getSupportActionBar().show();
                                    f1 = Fragment1.GetInstance();
                                    switchContent(mContent,f1);
                                    new SnackBar.Builder(MainActivity.this)
                                            .withMessage("Fragment1")
                                            .withStyle(SnackBar.Style.INFO)
                                            .withDuration((short)2000)
                                            .show();
                                    break;
                                case 2:
                                    if(!(getSupportActionBar().isShowing()) )
                                        getSupportActionBar().show();
                                    f2 = Fragment2.GetInstance();
                                    switchContent(mContent,f2);
                                    new SnackBar.Builder(MainActivity.this)
                                            .withMessage("Fragment2")
                                            .withStyle(SnackBar.Style.INFO)
                                            .withDuration((short)2000)
                                            .show();
                                    break;
                                case 3:
                                    if(!(getSupportActionBar().isShowing()) )
                                        getSupportActionBar().show();
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

    /**
     * 是否开启收藏夹功能
     */
    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {

            new SnackBar.Builder(MainActivity.this)
                        .withMessage(""+ isChecked )
                        .withStyle(SnackBar.Style.INFO)
                        .withDuration((short)2000)
                        .show();
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
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    android.R.anim.fade_in, android.R.anim.fade_out);
            transaction.replace(R.id.fragment_container,to,to.toString()).commit();
            return;
        }
        if (mContent != to) {
            //将跳转的页面加入栈中
            fragments.push(from);
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
                transaction.hide(from).add(R.id.fragment_container, to,to.toString()).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
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
                Intent intent =new Intent(this,ScanActivity.class);
                intent.putExtra(Constant.REQUEST_SCAN_MODE, Constant.REQUEST_SCAN_MODE_ALL_MODE);
                startActivity(intent);
//                Intent intent = new Intent(MainActivity.this,TestActivity.class);
//                startActivity(intent);
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
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
            return;
        }
        if(!fragments.empty()){
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


            super.onBackPressed();

    }
    @Override
    protected void onStop(){

        super.onStop();

    }
    @Override
    protected void onDestroy(){
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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

                  //      EventBus.getDefault().post(new MessageEvent("Item",item));
                        Log.i("result","info:"+ info );
                        switchContent(mContent,fragment_item);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("result","Exception:"+ e.toString() );
                }
                break;
            case Constant.REQUEST_Cart:
                initCart(internetEvent.message); //初始化购物车

                break;
            default:
                break;
        }
    }

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
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }


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
    }


    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void getList(ListEvent listEvent){
        switch (listEvent.message){
            //cardView历史订单
            case "init":
                Item item1 = new Item();
                item1.setPrice(10);
                Item item2 = new Item();
                item2.setPrice(12);
                Item item3 = new Item();
                item3.setPrice(33);

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

                lineItems.add(lineItem1);
                lineItems.add(lineItem2);
                Order order1 = new Order(lineItems,"1","2","8/10","aliPay","家乐福",0,0);

                User.orders.add(order1);
                ArrayList<LineItem> lineItems1 = new ArrayList<>();
                lineItems1.addAll(lineItems);
                lineItems1.add(lineItem3);

                Order order2 = new Order(lineItems1,"2","2","8/12","weixin","沃尔玛",0,1);

                User.orders.add(order2);
                break;
            case "update":
                break;
            default:
                break;
        }

    }
}
