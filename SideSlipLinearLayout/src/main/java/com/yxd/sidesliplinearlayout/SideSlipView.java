package com.yxd.sidesliplinearlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;


/**
 * 创建时间：2020/7/13
 * 编写人：czy_yangxudong
 * 功能描述：自定义侧滑item控件
 * 注意：不能继承viewgroup，不然需要重写onLayout方法，重写onLayout会造成此view作为父布局的第
 *       二个及以后的view的时候不显示，直接继承LinearLayout则没相关问题。
 *
 * 此自定义view写法不同于SideSlipLinearLayout使用ViewDragHelper的思路。
 * 此自定义view没写详细的逻辑及回调，请勿使用，直接用SideSlipLinearLayout即可
 */
public class SideSlipView extends LinearLayout {

    private View rightView;
    private int rightWith;
    private int downX;
    private Scroller scroller;
    private GestureDetector gd;

    public SideSlipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        //如果x方向位移大于y方向位移 则不给父控件处理触摸事件
        gd = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return distanceX > distanceY;
            }
        });
    }

    //当布局完全填充进来的时候回调
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        rightView = getChildAt(1);
        rightWith = rightView.getLayoutParams().width;
        Log.i("test", "rightWith=" + rightWith);
        //Log.i("test","rightHeight="+rightHeight);

        scroller = new Scroller(getContext());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //不断地获取触摸事件，并判断是否符合我们对“手势”的定义
        if(gd.onTouchEvent(event)){
            requestDisallowInterceptTouchEvent(true);
        }/*else{
            //闭合
            int dscrollx = -getScrollX();;
            isOpen = false;
            scroller.startScroll(getScrollX(), 0, dscrollx, 0);
            invalidate();
        }*/

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getX();
                int dX = moveX - downX;

                //Log.i("test","dx="+dX);
                //防止滚动越界
                //此方法获取的是上次滚动到的x坐标
                int scrollX = getScrollX();

                //此值表示的是下次将要滚动到的x坐标  scrollX是个负数 dx是正数
                int x = scrollX - dX;

                if (x >= 0 && x <= rightWith) {
                    //相对运动  这个方法移动的是手机屏幕 而不是屏幕内的布局 所以向右滑布局会向左动 所以要用-dx
                    scrollBy(-dX, 0);
                }

                downX = moveX;


                break;
            case MotionEvent.ACTION_UP:

                //获取抬手后滚动到的x坐标  正值
                int upScrollX = getScrollX();
                Log.i("test","upScrollX="+upScrollX);
                //展开或者闭合 需要滚动的x的距离
                int dScrollx;

                if (upScrollX < rightWith / 2) {
                    //闭合
                    dScrollx = -upScrollX;
                    isOpen = false;
                } else {
                    //展开
                    dScrollx = rightWith - upScrollX;
                    isOpen = true;
                }
                scroller.startScroll(upScrollX, 0, dScrollx, 0);
                invalidate();
                break;
            default:
                break;
        }

        return true;
    }

    private boolean isOpen = false;

    //Scroller 需要配合此方法使用
    @Override
    public void computeScroll() {
        super.computeScroll();
        //获取新的滚动位置，如果返回值为true表示还没有滚动结束
        //该方法每调用一次就会获取一个新的位置
        if (scroller.computeScrollOffset()) {
            //获取 期望位置
            int currX = scroller.getCurrX();
            //Log.i("test","currX="+currX);
            //滚动到期望位置
            scrollTo(currX, 0);

            //重绘  为了在还没滚动到最终位置前持续调用本方法
            invalidate();
        }
    }

    private void setOpen() {
        if (isOpen) {
            //闭合
            scroller.startScroll(rightWith, 0, 0, 0);
            isOpen = false;
        } else {
            //展开
            scroller.startScroll(0, 0, rightWith, 0);
            isOpen = true;
        }
        invalidate();
    }

}
