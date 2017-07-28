package com.example.tomoaki.maptest;

/**
 * Created by hiro-orbital on 2017/07/28.
 */

public class AreaCheck {
    public boolean AreaCheck(float ido, float keido){
        boolean CheckResult;

        //1つ目のエリア判定
        if (ido < 36.326308 && ido >36.321588 && keido < 139.012157 && keido > 139.002510)
            CheckResult = true;
            else{
                if (ido <36.3273144 && ido >36.326308 && keido <139.0068387 && keido >139.0066483)
                    CheckResult = true;
                else
                    CheckResult = false;
            }
            
        return CheckResult;

    }

}
