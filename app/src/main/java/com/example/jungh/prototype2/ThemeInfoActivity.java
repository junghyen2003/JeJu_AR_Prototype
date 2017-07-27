package com.example.jungh.prototype2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jungh.prototype2.model.LocationDetailModel;
import com.example.jungh.prototype2.service.ResponseService;
import com.example.jungh.prototype2.youtube.YoutubeActivity;
import com.example.jungh.prototype2.model.LocationModel;
import com.example.jungh.prototype2.service.ServiceBuilder;

import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ThemeInfoActivity extends ActionBarActivity{

    // Layout Object
    ImageView location_main_imageView1;
    ImageView location_main_imageView2;
    ImageView location_main_imageView3;
    TextView location_summary_textView;
    TextView location_fee_textView;
    TextView location_operationTime_textView;
    ImageView location_toilets;
    ImageView location_wifi;

    DBHandler mDBHandler;
    DBRecord mDBRecord;
    Intent intent;
    String themename;
    String vr_theme;
    String vr_path;
    String location_name;
    double lat;
    double lon;
    int tb_locations_id;
    Button vrButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.jungh.prototype2.R.layout.activity_theme_info);
        //DBHandler 초기화
        mDBHandler = new DBHandler(this);

        // Themename 을 가져오기 위한 인텐트
        // mDBRecord는 선택된 테마의 Record
        intent = getIntent();
        themename = intent.getStringExtra("themename");
        mDBHandler.setmThemeName(themename);
        mDBHandler.searchRecord();
        mDBRecord = mDBHandler.getmSelectedDBRecord();

        // Record 값 셋팅
        lat = mDBRecord.getLatitude();
        lon = mDBRecord.getLongitude();
        location_name = mDBRecord.getName();
        tb_locations_id = mDBRecord.getTb_locations_id();
        vr_theme = mDBRecord.getVr_theme();
        vr_path = mDBRecord.getVr_path();

        //2017.03.18
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 사용
        setTitle(themename); // 타이틀 바 이름 변경
        ScrollView theme_info_scroll = (ScrollView)findViewById(com.example.jungh.prototype2.R.id.theme_info_scroll);
        theme_info_scroll.setVerticalScrollBarEnabled(true); // 스크롤 허용

        // vrButton
        vrButton = (Button)findViewById(com.example.jungh.prototype2.R.id.theme_info_vr_button);
        if(vr_theme.equals("vr_image")) {
            vrButton.setText("360 VR 이미지 보기");
        }
        else if(vr_theme.equals("video")){
            vrButton.setText("Youtube 동영상 보기");
        }

        vrButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vr_theme.equals("vr_image")) {
                    Intent i = new Intent(ThemeInfoActivity.this, VRActivity.class);
                    i.putExtra("themename", themename);
                    i.putExtra("vr_path",vr_path);
                    startActivity(i);
                }
                else if(vr_theme.equals("video")) {
                    Intent i = new Intent(ThemeInfoActivity.this, YoutubeActivity.class);
                    i.putExtra("location_name", location_name);
                    i.putExtra("vr_path",vr_path);
                    startActivity(i);
                }
            }
        });

        layout_init();
        setLocationObject(tb_locations_id);

    }

    public void layout_init(){
        location_main_imageView1 = (ImageView)findViewById(com.example.jungh.prototype2.R.id.location_main_imageView1);
        location_main_imageView2 = (ImageView)findViewById(com.example.jungh.prototype2.R.id.location_main_imageView2);
        location_main_imageView3 = (ImageView)findViewById(com.example.jungh.prototype2.R.id.location_main_imageView3);
        location_summary_textView = (TextView)findViewById(com.example.jungh.prototype2.R.id.location_summary_textView);
        location_fee_textView = (TextView)findViewById(com.example.jungh.prototype2.R.id.location_fee_textView);
        location_operationTime_textView = (TextView)findViewById(com.example.jungh.prototype2.R.id.location_operationTime_textView);
        location_toilets = (ImageView)findViewById(com.example.jungh.prototype2.R.id.location_toilets);
        location_wifi = (ImageView)findViewById(com.example.jungh.prototype2.R.id.location_wifi);
    }

    public void setLocationObject(int id){
        ResponseService responseService = ServiceBuilder.createService(ResponseService.class, "http://211.225.79.43");
        responseService.getLocationDetail(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<LocationModel>>() {
                    @Override
                    public void onCompleted() {
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onNext(Response<LocationModel> locationModelResponse) {
                        LocationDetailModel location = locationModelResponse.body().getData();
                        Glide.with(ThemeInfoActivity.this)
                                .load(location.getImages().get(0).toString())
                                .into(location_main_imageView1);
                        Glide.with(ThemeInfoActivity.this)
                                .load(location.getImages().get(1).toString())
                                .into(location_main_imageView2);
                        Glide.with(ThemeInfoActivity.this)
                                .load(location.getImages().get(2).toString())
                                .into(location_main_imageView3);
                        location_summary_textView.setText(location.getSummary());
                        location_fee_textView.setText(location.getFee());
                        location_operationTime_textView.setText(location.getOperatingTime());
                        if(!location.getToilet()) {
                            location_toilets.setVisibility(View.GONE);
                        }
                        if(!location.getHasWifi()){
                            location_wifi.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // 뒤로가기 버튼이 눌렸을 때
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
