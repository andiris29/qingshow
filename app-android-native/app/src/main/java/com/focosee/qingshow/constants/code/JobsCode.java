package com.focosee.qingshow.constants.code;

/**
 * Created by focosee on 15/2/9.
 */
public enum JobsCode {

    WORKER("职员", 0), STUDENT("学生", 1), MODEL("模特", 2), ACTOR("演员", 3), DESIGNER("设计师", 4);

    private String name;
    private int index;

    private JobsCode (String name, int index) {
        this.name = name;
        this.index = index;
    }

    public int getIndex(){
        return this.index;
    }

    // 覆盖方法
    @Override
    public String toString() {
        return this.index + "_" + this.name;
    }


}
