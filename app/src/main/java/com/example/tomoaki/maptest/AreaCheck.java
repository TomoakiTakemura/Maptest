package com.example.tomoaki.maptest;

/**
 * Created by hiro-orbital on 2017/07/28.
 */

public class AreaCheck {
    float ido_up;
    float ido_low;
    float keido_up;
    float keido_low;
    AreaCheck(){
        ido_up = 36.326308f;
        ido_low = 36.321588f;
        keido_up = 139.012157f;
        keido_low = 139.002510f;
    }
    public boolean check(double ido, double keido){
        boolean CheckResult;

        //1つ目のエリア判定
        if (ido < ido_up && ido >ido_low && keido < keido_up && keido > keido_low)
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
