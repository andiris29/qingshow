package com.focosee.qingshow.constants.code;

/**
 * Created by focosee on 15/2/9.
 */
public enum PeopleTypeInU01PersonalActivity {

    MYSELF(0), OTHERS(1), NOREMOVEITEM(3);

    public int index;

    private PeopleTypeInU01PersonalActivity(int index){
        this.index = index;
    }

    public int getIndex(){
        return this.index;
    }

}