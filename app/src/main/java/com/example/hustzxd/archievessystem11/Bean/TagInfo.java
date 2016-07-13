package com.example.hustzxd.archievessystem11.Bean;

/**
 * 超高频标签的信息类
 * <p>
 * Created by buxiaoyao on 2016/7/13.
 */
public class TagInfo {
    private String EPC;

    private Integer num;//读取的次数

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getEPC() {
        return EPC;
    }

    public void setEPC(String EPC) {
        this.EPC = EPC;
    }
}
