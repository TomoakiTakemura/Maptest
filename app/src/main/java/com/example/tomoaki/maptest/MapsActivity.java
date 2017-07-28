package com.example.tomoaki.maptest;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import android.Manifest;

//compass

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.support.v4.app.NavUtils;

import android.location.Criteria;
import android.location.Location;

import java.util.ArrayList;
import java.util.*;

import com.google.android.gms.maps.model.CameraPosition;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener, SensorEventListener{

    //GPS
    private GoogleMap mMap;
    private LocationManager locationManager;

    //コンパス
    private SensorManager mSensorManager;   // センサマネージャ
    private Sensor mAccelerometer;  // 加速度センサ
    private Sensor mMagneticField;  // 磁気センサ


    //TextView textView1 = (TextView) findViewById(R.id.debug1);
    //TextView textView2 = (TextView) findViewById(R.id.debug2);
    //TextView textView3 = (TextView) findViewById(R.id.debug3);
    //TextView textView4 = (TextView) findViewById(R.id.debug4);
    //TextView textView5 = (TextView) findViewById(R.id.debug5);
    //TextView textView6 = (TextView) findViewById(R.id.debug6);
    CompassView cv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //GPS処理部
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        cv = (CompassView) findViewById(R.id.compassView);

        // Fine か Coarseのいずれかのパーミッションが得られているかチェックする
        // 本来なら、Android6.0以上かそうでないかで実装を分ける必要がある
        if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            /* fine location のリクエストコード（値は他のパーミッションと被らなければ、なんでも良い）*/
            final int requestCode = 1;

            // いずれも得られていない場合はパーミッションのリクエストを要求する
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode );
            return;
        }

        // 位置情報を管理している LocationManager のインスタンスを生成する
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String locationProvider = null;

        // GPSが利用可能になっているかどうかをチェック
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationProvider = LocationManager.GPS_PROVIDER;
        }
        // GPSプロバイダーが有効になっていない場合は基地局情報が利用可能になっているかをチェック
        else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }
        // いずれも利用可能でない場合は、GPSを設定する画面に遷移する
        else {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            return;
        }

        /* 位置情報の通知するための最小時間間隔（ミリ秒） */
        final long minTime = 1000;
        /* 位置情報を通知するための最小距離間隔（メートル）*/
        final long minDistance = 1;

        // 利用可能なロケーションプロバイダによる位置情報の取得の開始
        // FIXME 本来であれば、リスナが複数回登録されないようにチェックする必要がある
        locationManager.requestLocationUpdates(locationProvider, minTime, minDistance, this);
        // 最新の位置情報
        Location location = locationManager.getLastKnownLocation(locationProvider);

        //if (location != null) {
          //  TextView textView = (TextView) findViewById(R.id.debug3);
           // textView.setText(String.valueOf( "onCreate() : " + location.getLatitude()) + "," + String.valueOf(location.getLongitude()));
        //}

        SearchPort(location);
        //コンパスのonCreate
        // センサーを取り出す
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorManager.registerListener(
                this, this.mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(
                this, this.mMagneticField, SensorManager.SENSOR_DELAY_NORMAL);

    }


    public void SearchPort(Location l){
        Distance d = new Distance();
        int port = d.searchNearPort((float) l.getLatitude(),(float) l.getLongitude());
        TextView textView = (TextView) findViewById(R.id.portID);
        TextView KView = (TextView) findViewById(R.id.distance);
        PortList p =new PortList();
        List<TakaPort> list = p.getPortList();
        float kyori = d.DistanceCalc((float) l.getLatitude(),(float) l.getLongitude(),(float) list.get(port).getLat(),(float) list.get(port).getLng());

        Log.d("DistanceTest",String.valueOf(kyori));
        textView.setText(list.get(port).getName());
        KView.setText(String.valueOf(kyori));

    }

    //GPS
    //位置情報が通知されるたびにコールバックされるメソッド
    @Override
    public void onLocationChanged(Location location){
        //TextView textView = (TextView) findViewById(R.id.debug3);
       // textView.setText(String.valueOf("onLocationChanged() : " + location.getLatitude()) + ":" + String.valueOf(location.getLongitude()));

        //利用可否判定
        TextView usableText = (TextView) findViewById(R.id.usableText);
        AreaCheck check = new AreaCheck();
        if (check.check(location.getLatitude(),location.getLongitude()))
            usableText.setText(String.valueOf("利用可"));
        else
            usableText.setText(String.valueOf("利用不可"));

        SearchPort(location);

    }

    //ロケーションプロバイダが利用不可能になるとコールバックされるメソッド
    @Override
    public void onProviderDisabled(String provider) {
        //ロケーションプロバイダーが使われなくなったらリムーブする必要がある
    }

    //ロケーションプロバイダが利用可能になるとコールバックされるメソッド
    @Override
    public void onProviderEnabled(String provider) {
        //プロバイダが利用可能になったら呼ばれる
    }

    //ロケーションステータスが変わるとコールバックされるメソッド
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // 利用可能なプロバイダの利用状態が変化したときに呼ばれる
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        List<LatLng> LLList = new ArrayList<LatLng>();
        PortList list = new PortList();
        for ( int i = 1; i <= list.getSize(); ++i ) {
            LatLng ll = new LatLng(list.getPortByNum(i).getLat(), list.getPortByNum(i).getLng());
            LLList.add(ll);

            mMap.addMarker(new MarkerOptions().position(ll).title(list.getPortByNum(i).getName()));
        }
        //現在地を表示
        LatLng defPos = new LatLng(36.3242936, 139.0073642);//たかちゃり範囲の中心らへん
        CameraPosition pos = new CameraPosition(defPos, 16.0f, 0.0f, 0.0f); //CameraUpdate
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }


    //compass

    // 使わない
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    // 加速度センサの値
    private float[] mAccelerometerValue = new float[3];
    // 磁気センサの値
    private float[] mMagneticFieldValue = new float[3];
    // 磁気センサの更新がすんだか
    private boolean mValidMagneticFiled = false;

    // センサーの値が変更された
    public void onSensorChanged(SensorEvent event) {

        // センサーごとの処理
        switch (event.sensor.getType()) {
            // 加速度センサー
            case Sensor.TYPE_ACCELEROMETER:
                // cloneで配列がコピーできちゃうんだね。へえ
                this.mAccelerometerValue = event.values.clone();
                break;
            // 磁気センサー
            case Sensor.TYPE_MAGNETIC_FIELD:
                this.mMagneticFieldValue = event.values.clone();
                this.mValidMagneticFiled = true;
                break;
        }

        // 値が更新された角度を出す準備ができた
        if (this.mValidMagneticFiled) {
            // 方位を出すための変換行列
            float[] rotate = new float[16]; // 傾斜行列？
            float[] inclination = new float[16];    // 回転行列

            // うまいこと変換行列を作ってくれるらしい
            SensorManager.getRotationMatrix(
                    rotate, inclination,
                    this.mAccelerometerValue,
                    this.mMagneticFieldValue);

            // 方向を求める
            float[] orientation = new float[3];
            this.getOrientation(rotate, orientation);

            // デグリー角に変換する
            float degreeDir = (float)Math.toDegrees(orientation[0]);
           // TextView textView4 = (TextView) findViewById(R.id.debug4);
           // textView4.setText(String.valueOf(degreeDir));
            cv.MoveArc(degreeDir);
            //Log.i("onSensorChanged", "角度:" + degreeDir);
        }
    }


    // ////////////////////////////////////////////////////////////
    // 画面が回転していることを考えた方角の取り出し
    public void getOrientation(float[] rotate, float[] out) {

        // ディスプレイの回転方向を求める(縦もちとか横持ちとか)
        Display disp = this.getWindowManager().getDefaultDisplay();
        // ↓コレを使うためにはAPIレベルを8にする必要がある
        int dispDir = disp.getRotation();

        // 画面回転してない場合はそのまま
        if (dispDir == Surface.ROTATION_0) {
            SensorManager.getOrientation(rotate, out);

            // 回転している
        } else {

            float[] outR = new float[16];

            // 90度回転
            if (dispDir == Surface.ROTATION_90) {
                SensorManager.remapCoordinateSystem(
                        rotate, SensorManager.AXIS_Y,SensorManager.AXIS_MINUS_X, outR);
                // 180度回転
            } else if (dispDir == Surface.ROTATION_180) {
                float[] outR2 = new float[16];

                SensorManager.remapCoordinateSystem(
                        rotate, SensorManager.AXIS_Y,SensorManager.AXIS_MINUS_X, outR2);
                SensorManager.remapCoordinateSystem(
                        outR2, SensorManager.AXIS_Y,SensorManager.AXIS_MINUS_X, outR);
                // 270度回転
            } else if (dispDir == Surface.ROTATION_270) {
                SensorManager.remapCoordinateSystem(
                        outR, SensorManager.AXIS_MINUS_Y,SensorManager.AXIS_MINUS_X, outR);
            }
            SensorManager.getOrientation(outR, out);
        }
    }
}
