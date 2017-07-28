package com.example.tomoaki.maptest;

import java.util.List;

import static java.lang.Math.PI;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by shot1 on 2017/07/28.
 */

public class Distance {


    public float DistanceCalc(float lat1,float lng1,float lat2,float lng2){

        float r = 6378.137f; // 赤道半径[km]

        lat1 = lat1 * (float)PI / 180f;
        lng1 = lng1 * (float)PI / 180;

        lat2 = lat2* (float)PI / 180;
        lng2 = lng2 * (float)PI / 180;

        // 2点間の距離[km]
        float distance = (float) (r * acos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(lng2 - lng1)));

        //System.out.printf("%f km", distance);

        return distance;

    }
//
    public float geoDirection(float lat1,float lng1,float lat2,float lng2) {
        // 緯度経度 lat1, lng1 の点を出発として、緯度経度 lat2, lng2 への方位
        // 北を０度で右回りの角度０～３６０度
        float Y =(float)( Math.cos(lng2 * Math.PI / 180) * Math.sin(lat2 * Math.PI / 180 - lat1 * Math.PI / 180));
        float X =(float)( Math.cos(lng1 * Math.PI / 180) * Math.sin(lng2 * Math.PI / 180) - Math.sin(lng1 * Math.PI / 180) * Math.cos(lng2 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180 - lat1 * Math.PI / 180));
        float dirE0 = (float) (180 * Math.atan2(Y, X) / Math.PI); // 東向きが０度の方向
        if (dirE0 < 0) {
            dirE0 = dirE0 + 360; //0～360 にする。
        }
        float dirN0 = (dirE0 + 90) % 360; //(dirE0+90)÷360の余りを出力 北向きが０度の方向
        return dirN0;
    }

    public int searchNearPort(float lat1,float lng1){

        PortList p =new PortList();
        List<TakaPort> list = p.getPortList();
        int minPortNum=0;


        for(int l = 0;l < list.size();l++){
            if(DistanceCalc(lat1,lng1,(float) list.get(minPortNum).getLat(),(float) list.get(minPortNum).getLng())>
                    DistanceCalc(lat1,lng1,(float) list.get(l).getLat(),(float) list.get(l).getLng()) ){
                minPortNum = l;
            }
        }

        return minPortNum+1;
    }


}
