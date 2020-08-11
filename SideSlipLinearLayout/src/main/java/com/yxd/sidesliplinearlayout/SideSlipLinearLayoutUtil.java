package com.yxd.sidesliplinearlayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建时间：2020/8/6
 * 编写人：czy_yangxudong
 * 功能描述：用来存储及操作打开的SideSlipLinearLayout
 */
class SideSlipLinearLayoutUtil {

    private SideSlipLinearLayoutUtil(){ }

    private static SideSlipLinearLayoutUtil sideSlipUtil;
    public static SideSlipLinearLayoutUtil getInstance(){
        if (sideSlipUtil==null){
            sideSlipUtil=new SideSlipLinearLayoutUtil();
        }
        return sideSlipUtil;
    }

    private  List<SideSlipLinearLayout> list=new ArrayList<>();

    /**
     * 添加 SideSlipLinearLayout
     * @param sideSlipLinearLayout
     */
    protected void addItem(SideSlipLinearLayout sideSlipLinearLayout){
        list.add(sideSlipLinearLayout);
    }

    /**
     * 移除单个 SideSlipLinearLayout
     * @param sideSlipLinearLayout
     */
    protected  void removeItem(SideSlipLinearLayout sideSlipLinearLayout){
        try {
            list.remove(sideSlipLinearLayout);
        }catch (Exception e){}
    }

    /**
     * 获取集合数量
     * @return
     */
    protected  int getListSize(){
        return list.size();
    }

    /**
     * 集合内的SideSlipLinearLayout都关闭
     * 并 清空集合
     */
    protected void clearItem(){
        if (list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                list.get(i).closeForUtil();
            }
        }
        list.clear();
    }

    /**
     * 获取此集合内的第一个SideSlipLinearLayout
     * 有也只有一个
     * @return
     */
    protected SideSlipLinearLayout getSideSlipLinearLayout(){
        if (list.size()>0){
            return list.get(0);
        }
        return null;
    }


}
