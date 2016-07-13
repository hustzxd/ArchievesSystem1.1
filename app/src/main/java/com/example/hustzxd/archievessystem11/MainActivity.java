package com.example.hustzxd.archievessystem11;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.hustzxd.archievessystem11.Utils.Utils;
import com.example.hustzxd.archievessystem11.VolleyUtils.MyApplication;
import com.example.hustzxd.archievessystem11.constant.Constant;
import com.example.hustzxd.archievessystem11.fragments.CheckFragment;
import com.example.hustzxd.archievessystem11.fragments.HelloFragment;
import com.example.hustzxd.archievessystem11.fragments.LoginFragment;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private View mHeadView;
    private ImageView mImageView;
    private LoginFragment mLoginFragment;
    private HelloFragment mHelloFragment;
    private CheckFragment mCheckFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!Constant.isLogin) {
                        Snackbar.make(view, "请先登录", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        sendTag();
                    }
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mHeadView = navigationView.getHeaderView(0);
        mImageView = (ImageView) mHeadView.findViewById(R.id.iv_user_img);

        mImageView.setOnClickListener(this);

    }

    private void sendTag() {

        String url = Constant.URL_SEND_TAG;
        Log.d("sss-url", url);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {//s为请求返回的字符串数据
                        Log.i("sss-response", s);
                        Utils.toast(getApplicationContext(), "发送标签EPC成功");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.i("sss-errorResponse", volleyError.toString());
                    }
                }) {
            //改写 cookie
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (MyApplication.cookies != null && MyApplication.cookies.length() > 0) {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("cookie", MyApplication.cookies);
                    Log.d("sss-cookie", headers.toString());
                    return headers;
                }
                return super.getHeaders();
            }
        };
        //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
        request.setTag("sendTagPost");
        //将请求加入全局队列中
        MyApplication.getmQueues().add(request);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_check:
                if (mCheckFragment == null) {
                    mCheckFragment = new CheckFragment();
                }
                transaction.replace(R.id.fragment_content, mCheckFragment).commit();
                break;
            default:
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        switch (v.getId()) {
            case R.id.iv_user_img:
                if (Constant.isLogin) {
                    if (mHelloFragment == null) {
                        mHelloFragment = new HelloFragment();
                    }
                    transaction.replace(R.id.fragment_content, mHelloFragment);
                } else {
                    if (mLoginFragment == null) {
                        mLoginFragment = new LoginFragment();
                    }
                    transaction.replace(R.id.fragment_content, mLoginFragment);
                }
                transaction.commit();
                Toast.makeText(getApplicationContext(), "login", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
    }
}
