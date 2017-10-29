package com.example.chouyuhsien.hwai_ibeacon_prj_1029;

/**
 * Created by Chou Yu Hsien on 2017/10/29.
 */

public class BLEList {
    public int aIcon;
    public String aDeviceName;
    public String aContent;
    public BLEList(int icon,String name,String content){
        this.aIcon = icon;
        this.aDeviceName = name;
        this.aContent = content;
    }

    public int getaIcon() {
        return aIcon;
    }

    public String getaDeviceName() {
        return aDeviceName;
    }

    public String getaContent() {
        return aContent;
    }

    public void setaContent(String aContent) {
        this.aContent = aContent;
    }

    public void setaDeviceName(String aDeviceName) {
        this.aDeviceName = aDeviceName;
    }

    public void setaIcon(int aIcon) {
        this.aIcon = aIcon;
    }
}
