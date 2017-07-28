package com.example.tomoaki.maptest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;


public class CompassView extends View {
    private Bitmap bmp = null;
    private float arc = 45;
    private float kakudo=0;
    //ActivityからContext(この場合描写先、ということ？)だけを指定してインスタンスを作成したときに呼び出される
    //コンストラクタ
    public CompassView(Context cont) {
        super(cont);
    }
    //xmlファイルにuri+クラス名でタグを作って、アクティビティに追加するときに呼び出されるコンストラクタ
    //AttributeSetというのはタグ内にあるlayout_width=""などらしい
    public CompassView(Context cont, AttributeSet attr) {
        super(cont,attr);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.arrowsbe);
    }

    public void changeArc(float a){
        kakudo=a;
    }

    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawColor(Color.WHITE);
        Rect destRect1 = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
        Rect srcRect1 = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
        canvas.rotate(arc, canvas.getWidth() / 2,canvas.getHeight() / 2);
        canvas.drawBitmap(bmp, srcRect1, destRect1, null);


    }

    public void MoveArc(float a){

        if(a>=0) {
            arc = (-a)+kakudo;
        }else{
            arc = (-360 - a)+kakudo;
        }
        invalidate();
    }

    //xmlでlayout_width,layout_heightを指定すると指定したサイズに応じた大きさになります
    //(ただしspでなくて画素とかのような気がする……？)
    //コンストラクタだけでActivityから呼び出した(=AttributeSetなしで呼び出した)ときは、widthMeasureSpecなどは画面の縦幅、横幅になるみたいです。
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize,heightSize);
    }


}