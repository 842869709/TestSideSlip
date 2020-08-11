package com.yxd.sidesliplinearlayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建时间：2020/8/6
 * 编写人：czy_yangxudong
 * 功能描述：用来存储打开的SideSlipView
 */
class SideSlipViewUtil {

    private SideSlipViewUtil(){

    }

    private static SideSlipViewUtil sideSlipUtil;
    public static SideSlipViewUtil getInstance(){
        if (sideSlipUtil==null){
            sideSlipUtil=new SideSlipViewUtil();
        }
        return sideSlipUtil;
    }

    private  List<SideSlipView> list=new ArrayList<>();

    protected void addItem(SideSlipView sideSlipView){
        list.add(sideSlipView);
    }

    protected  void removeItem(SideSlipView sideSlipView){
        list.remove(sideSlipView);
    }

    protected  int getListSize(){
        return list.size();
    }

}
