package com.example.tomoaki.maptest;

import android.util.Log;

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


public class compass extends Activity implements SensorEventListener{
// ↓せんさーの値が更新されたら取り出すために必要

        private SensorManager mSensorManager;   // センサマネージャ
        private Sensor mAccelerometer;  // 加速度センサ
        private Sensor mMagneticField;  // 磁気センサ

        private SurfaceViewExt mSurfaceViewExt; // サーフェイスビュー


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);

            // センサーを取り出す
            this.mSensorManager = (SensorManager)this.getSystemService(SENSOR_SERVICE);
            this.mAccelerometer = this.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            this.mMagneticField = this.mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            this.mSurfaceViewExt = (SurfaceViewExt)this.findViewById(R.id.surfaceView1);
        }

//        @Override
//        public boolean onCreateOptionsMenu(Menu menu) {
//            getMenuInflater().inflate(R.menu.activity_try_compass00, menu);
//            return true;
//        }

        // ////////////////////////////////////////////////////////////
        // ポーズ
        @Override
        protected void onPause() {
            super.onPause();

            // リスナーの登録解除
            this.mSensorManager.unregisterListener(this);
        }

        // ////////////////////////////////////////////////////////////
        // 再開
        @Override
        protected void onResume() {
            super.onResume();

            // リスナーの登録
            this.mSensorManager.registerListener(
                    this, this.mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            this.mSensorManager.registerListener(
                    this, this.mMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
        }


        // 使わない
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    // 加速度センサの値
    private float[] mAccelerometerValue = new float[3];
    // 磁気センサの値
    private float[] mMagneticFieldValue = new float[3];
    // 磁気センサの更新がすんだか
    private boolean mValidMagneticFiled = false;

    // ////////////////////////////////////////////////////////////
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
            //Log.i("onSensorChanged", "角度:" + degreeDir);

            // アローを回転させる
            this.mSurfaceViewExt.setArrowDir(degreeDir);
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



// ////////////////////////////////////////////////////////////
// ここから下は表示したいだけのためのモノだからコンパスとはあまり関係がない
// ////////////////////////////////////////////////////////////


//////////////////////////////////////////////////////////////
//サーフェイスビューの拡張
class SurfaceViewExt extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private final static int ONE_FRAME_TICK = 1000 / 25;    // 1フレームの時間
    private final static int MAX_FRAME_SKIPS = 5;           // 時間が余ったとき最大何回フレームをスキップするか


    private SurfaceHolder mSurfaceHolder;   // サーフェイスホルダー
    private Thread mMainLoop;   // メインのゲームループの様なモノ

    // 画像を表示するためのモノ
    private final Resources mRes = this.getContext().getResources();
    private Bitmap mBitmap;
    private float mArrowDir;    // 矢印の方向


    // ////////////////////////////////////////////////////////////
    // コンストラクタ
    public SurfaceViewExt(Context context) {
        super(context);

        // Bitmapをロードする
        this.loadBitmap();

        // SurfaceViewの初期化
        this.initSurfaceView(context);
    }

    // ////////////////////////////////////////////////////////////
    // コンストラクタ
    public SurfaceViewExt(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Bitmapをロードする
        this.loadBitmap();

        // SurfaceViewの初期化
        this.initSurfaceView(context);
    }

    // ////////////////////////////////////////////////////////////
    // コンストラクタ
    public SurfaceViewExt(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Bitmapをロードする
        this.loadBitmap();

        // SurfaceViewの初期化
        this.initSurfaceView(context);
    }

    // ////////////////////////////////////////////////////////////
    // Bitmapをロードする
    private void loadBitmap() {

        // 画像のロード
        this.mBitmap = BitmapFactory.decodeResource(this.mRes, R.drawable.arrow);

    }

    // ////////////////////////////////////////////////////////////
    // 角度をセットする
    public void setArrowDir(float dir) {
        this.mArrowDir = -dir;
    }

    // ////////////////////////////////////////////////////////////
    // SurfaceViewの初期化
    private void initSurfaceView(Context context) {
        // サーフェイスホルダーを取り出す
        this.mSurfaceHolder = this.getHolder();
        // コールバック関数を登録する
        this.mSurfaceHolder.addCallback(this);
    }

    public void onDraw() {
        Canvas canvas = this.mSurfaceHolder.lockCanvas();
        this.draw(canvas);
        this.mSurfaceHolder.unlockCanvasAndPost(canvas);
    }
    // ////////////////////////////////////////////////////////////
    // 描画処理
    public void draw(final Canvas canvas) {

        // Arrowを表示
        Paint paint = new Paint();
        canvas.save();
        canvas.translate(this.mScreenCenter[0] - 256, this.mScreenCenter[1] - 256);
        canvas.scale(2, 2);
        canvas.rotate(this.mArrowDir, 128, 128);
        canvas.drawBitmap(this.mBitmap, 0, 0, paint);
        canvas.restore();



        // 文字を表示
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setTextSize(20);
        canvas.save();
        canvas.translate(this.mScreenCenter[0] - 30, this.mScreenCenter[1]);
        canvas.rotate(this.mArrowDir, 0, 0);
        canvas.drawText("N:" + String.format("%02.1f", this.mArrowDir), 1, 100, paint);
        canvas.restore();

    }

    private int[] mScreenCenter = { 0, 0 };
    // ////////////////////////////////////////////////////////////
    // サーフェイスサイズの変更があったときとかに呼ばれる
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

        // センター位置
        this.mScreenCenter[0] = width / 2;
        this.mScreenCenter[1] = height / 2;

    }

    // ////////////////////////////////////////////////////////////
    // サーフェイスが作られたときに呼ばれる
    public void surfaceCreated(SurfaceHolder holder) {
        // ワーカースレッドを作る
        this.mMainLoop = new Thread(this);
        this.mMainLoop.start();
    }

    // ////////////////////////////////////////////////////////////
    // サーフェイスが破棄された時に呼ばれる
    public void surfaceDestroyed(SurfaceHolder holder) {
        this.mMainLoop = null;
    }


    // ////////////////////////////////////////////////////////////
    // 毎フレーム呼ばれるやつ
    public void move() {
    }

    //////////////////////////////////////////////////////////////
    //毎フレーム呼ばれるやつ
    //ちょっと上等なメインループ
    public void run() {
        Canvas canvas;
        long beginTime; // 処理開始時間
        long pastTick;  // 経過時間
        int sleep = 0;
        int frameSkipped;   // 何フレーム分スキップしたか

        // フレームレート関連
        int frameCount = 0;
        long beforeTick = 0;
        long currTime = 0;
        String tmp = "";

        // 文字書いたり
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setTextSize(60);

        int count = 0;
        // スレッドが消滅していない間はずっと処理し続ける
        while (this.mMainLoop != null) {
            canvas = null;

            // フレームレートの表示
            frameCount++;
            currTime = System.currentTimeMillis();
            if (beforeTick + 1000 < currTime) {
                beforeTick = currTime;
                tmp = "" + frameCount;
                frameCount = 0;
            }

            try {

                synchronized (this.mSurfaceHolder) {
                    canvas = this.mSurfaceHolder.lockCanvas();
                    // キャンバスとれなかった
                    if (canvas == null)
                        continue;


                    canvas.drawColor(Color.rgb(93, 233, 120));

                    // 現在時刻
                    beginTime = System.currentTimeMillis();
                    frameSkipped = 0;

                    // ////////////////////////////////////////////////////////////
                    // ↓アップデートやら描画やら
                    this.move();
                    canvas.save();
                    this.draw(canvas);
                    canvas.restore();
                    // ////////////////////////////////////////////////////////////

                    // 経過時間
                    pastTick = System.currentTimeMillis() - beginTime;

                    // 余っちゃった時間
                    sleep = (int)(ONE_FRAME_TICK - pastTick);

                    // 余った時間があるときは待たせる
                    if (0 < sleep) {
                        try {
                            Thread.sleep(sleep);
                        } catch (Exception e) {}
                    }

                    // 描画に時間係過ぎちゃった場合は更新だけ回す
                    while (sleep < 0 && frameSkipped < MAX_FRAME_SKIPS) {
                        // ////////////////////////////////////////////////////////////
                        // 遅れた分だけ更新をかける
                        this.move();
                        // ////////////////////////////////////////////////////////////
                        sleep += ONE_FRAME_TICK;
                        frameSkipped++;
                    }
                    canvas.drawText("FPS:" + tmp, 1, 100, paint);
                }
            } finally {
                // キャンバスの解放し忘れに注意
                if (canvas != null) {
                    this.mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }


}
