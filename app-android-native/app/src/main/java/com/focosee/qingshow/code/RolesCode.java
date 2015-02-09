package com.focosee.qingshow.code;

/**
 * Created by focosee on 15/2/9.
 */
public enum RolesCode {
    COMM("普通用户", 0), MODEL("模特", 1), ADMIN("管理员", 2);

    // 成员变量
    private String name;
    private int index;

    // 构造方法
    private RolesCode(String name, int index) {
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