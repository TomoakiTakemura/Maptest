package com.example.tomoaki.maptest;

/**
 * Created by tomoaki on 2017/07/28.
 */

public class TakaPort {

    //要素
    int number;
    boolean use;
    String name;
    double lat;
    double lng;

    //コンストラクタ
    TakaPort(int Num, boolean Use, String Name, double Lat, double Lng){
        number = Num;
        use = use;
        name = Name;
        lat = Lat;
        lng = Lng;
    }

    //メソッド
    boolean canUse() {
        return use;
    }
    String getName(){
        return name;
    }
    double getLat(){
        return lat;
    }
    double getLng(){
        return lng;
    }
}
