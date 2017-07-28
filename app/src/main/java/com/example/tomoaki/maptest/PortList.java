package com.example.tomoaki.maptest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomoaki on 2017/07/28.
 */

public class PortList {
    List<TakaPort> portList;
    PortList(){
        portList.add(new TakaPort(1, true, "JR高崎駅西口", 36.3225719, 139.0118223));
        portList.add(new TakaPort(2, false, "駅西口ペデストリアンデッキ下", 36.3225881, 139.0115346));
        portList.add(new TakaPort(3, true, "駅西口広場", 36.3223523,	139.0111997));
        portList.add(new TakaPort(4, true, "セントラルホテル前", 36.3224374, 139.0095816));
        portList.add(new TakaPort(5, true, "シンフォニーロード", 36.3221983, 139.0053618));
        portList.add(new TakaPort(6, true, "高崎市役所", 36.3218099, 139.0038035));
        portList.add(new TakaPort(7, true, "中央図書館前", 36.3254525, 139.0025184));
        portList.add(new TakaPort(8, true, "もてなし広場", 36.3256457, 139.0039838));
        portList.add(new TakaPort(9, true, "スズラン前", 36.3243912, 139.0047788));
        portList.add(new TakaPort(10, true, "さやもーる", 36.325258, 139.0057065));
        portList.add(new TakaPort(11, true, "シネマテークたかさき", 36.3238018, 139.071572));
        portList.add(new TakaPort(12, true, "連雀町交差点", 36.3243796, 139.0069993));
        portList.add(new TakaPort(13, true, "群馬銀行高崎田町支店", 36.3270311, 139.0066654));
        portList.add(new TakaPort(14, true, "慈光通り", 36.3244188, 139.0081151));
        portList.add(new TakaPort(15, false, "高島屋前", 36.3236654, 139.0115638));
        portList.add(new TakaPort(16, true, "大信寺前", 36.3254704, 139.009074));
    }

    void addPortList(TakaPort port){
        portList.add(port);
    }
    TakaPort getPortByNum(int num){
        return portList.get(num-1);
    }
}
