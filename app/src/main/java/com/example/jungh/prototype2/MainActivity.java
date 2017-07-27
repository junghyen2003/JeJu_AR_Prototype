package com.example.jungh.prototype2;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // 프리뷰화면
    private MainSurfaceView mSurfaceView;
    // 오버레이 화면
    private SurfaceOverlayView mSurfaceOverlayView;
    LinearLayout mcoverlayout;
    // 오버레이 화면 크기를 넘겨주기 위함
    public int mwidth;
    public int mheight;

    // 위치정보 관련
    LocationManager locationManager;
    double latitude; // 위도
    double longitude; // 경도
    double altitude; // 고도
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            altitude = location.getAltitude();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {
            Toast.makeText(MainActivity.this, "GPS 사용 가능하게 설정되었습니다.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String s) {
            Toast.makeText(MainActivity.this, "GPS 사용 여부를 확인해주세요.", Toast.LENGTH_SHORT).show();
        }
    };
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.jungh.prototype2.R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(com.example.jungh.prototype2.R.id.toolbar);
        setSupportActionBar(toolbar);

        SurfaceView surface = (SurfaceView)findViewById(com.example.jungh.prototype2.R.id.surfaceView);
        mSurfaceView = new MainSurfaceView(this, surface);
        ((LinearLayout)findViewById(com.example.jungh.prototype2.R.id.surface_main)).addView(mSurfaceView);

        mSurfaceOverlayView = new SurfaceOverlayView(this);
        SeekBar mSeekBar;
        mSeekBar = (SeekBar)findViewById(com.example.jungh.prototype2.R.id.seekBar);
        mSeekBar.setProgress(50);
        mSurfaceOverlayView.setmVisibleDistance(50);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(seekBar.getProgress() < 100) {
                    mSurfaceOverlayView.setmVisibleDistance(seekBar.getProgress());
                    mSurfaceOverlayView.invalidate();
                } else {
                    mSurfaceOverlayView.setmVisibleDistance(5000); // seekbar 값이 100 이상이면 거리 무제한(5000km)
                    mSurfaceOverlayView.invalidate();
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        ((FrameLayout)findViewById(com.example.jungh.prototype2.R.id.content_main)).addView(mSurfaceOverlayView);

        final DrawerLayout drawer = (DrawerLayout) findViewById(com.example.jungh.prototype2.R.id.drawer_layout);
        // Drawer Menu가 SurfaceView 위에 뜨도록 해결
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                drawer.bringChildToFront(drawerView);
                drawer.requestLayout();
            }
            @Override
            public void onDrawerOpened(View drawerView) {
            }
            @Override
            public void onDrawerClosed(View drawerView) {
            }
            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
        //
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, com.example.jungh.prototype2.R.string.navigation_drawer_open, com.example.jungh.prototype2.R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(com.example.jungh.prototype2.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // GPS 관련
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, locationListener);
        // -----------------------------------------------------------------------------------------
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        mcoverlayout = (LinearLayout)findViewById(com.example.jungh.prototype2.R.id.coverLayout);
        mwidth = mcoverlayout.getWidth();
        mheight = mcoverlayout.getHeight();
        // coverLayout width, height 크기를 넘겨줌
        mSurfaceOverlayView.setOverlaySize(mwidth, mheight);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.example.jungh.prototype2.R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.example.jungh.prototype2.R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == com.example.jungh.prototype2.R.id.nav_camera) {
            // Handle the camera action
        } else if (id == com.example.jungh.prototype2.R.id.nav_restaurant) {
            mSurfaceOverlayView.setTheme("restaurant");
        } else if (id == com.example.jungh.prototype2.R.id.nav_tourist) {
            mSurfaceOverlayView.setTheme("tourist");
        } else if (id == com.example.jungh.prototype2.R.id.nav_shopping) {
            mSurfaceOverlayView.setTheme("shopping");
        } else if (id == com.example.jungh.prototype2.R.id.nav_restroom){
            mSurfaceOverlayView.setTheme("restroom");
        } else if (id == com.example.jungh.prototype2.R.id.nav_share) {

        } else if (id == com.example.jungh.prototype2.R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(com.example.jungh.prototype2.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
