package com.yxd.sidesliplinearlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

/**
 * 创建时间：2020/7/23
 * 编写人：czy_yangxudong
 * 功能描述：自定义仿QQ列表侧滑view
 */
public class SideSlipLinearLayout extends LinearLayout {

    private ViewDragHelper viewDragHelper;
    private View leftView;
    private View rightView;
    private int rightWith;
    //private int leftWith;
    //标记展开状态
    private boolean isOpen=false;
    //标记手指是否有移动 判断是点击事件还是滑动
    private boolean isMove=false;
    //private MyBroadcastReceive receiver;
    private Context context;
    private GestureDetector gd;
    //标记手指按下的时候 （也就是手指抬起以前）是否有打开状态的item
    private boolean isHaveItem=false;

    public SideSlipLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    private void init() {
        viewDragHelper = ViewDragHelper.create(this,callback);
        //如果x方向位移大于y方向位移 则不给父控件处理触摸事件
        gd = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                Log.i("test","distanceX="+distanceX+" distanceY="+distanceY);
                if (Math.abs(distanceX) > Math.abs(distanceY)){
                    Log.i("test","左右");
                }else{
                    Log.i("test","上下");
                }
                return Math.abs(distanceX) > Math.abs(distanceY);
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //固定写法          是否应该拦截触摸事件
        if (viewDragHelper.shouldInterceptTouchEvent(ev)){
            return true;
        }else{
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //标记手指按下时候的y坐标
        float actionDownY=0;
        //标记事件被RecycleView（父布局）抢夺那一刻的手指的y坐标，两个值如果不同，则判定为手指滑动了，则不是点击事件
        float actionCancelY=0;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                actionDownY=event.getRawY();
                Log.i("test","ACTION_DOWN="+event.getRawY());

            case MotionEvent.ACTION_CANCEL:
                actionCancelY=event.getRawY();
                if (actionDownY!=actionCancelY){
                    isMove=true;
                }
                Log.i("test","ACTION_CANCEL="+event.getRawY());
                break;
        }


        /**
         * 因为放在RecyclerView中，RecyclerView先接收到事件
         * 当RecyclerView发现是上下移动的时候，就向当前布局发送了 MotionEvent.ACTION_CANCLE的事件
         * ACTION_CANCLE等同于ACTION_UP，所以接下来的就release
         * 解决办法：
         * 当发现是横向拖动的时候，我们就调用方法requestDisallowInterceptTouchEvent(true);，告知父控件不要抢夺事件。
         */
        //不断地获取触摸事件，并判断是否符合我们对“手势”的定义
        if(gd.onTouchEvent(event)){
            requestDisallowInterceptTouchEvent(true);
        }

        //把触摸事件交由viewDragHelper处理
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        leftView = getChildAt(0);
        rightView = getChildAt(1);
        //Log.i("test","content="+ leftView.toString()+"-----right="+ rightView.toString());
        //leftWith = getScreenWidth();
        //Log.i("test","contentWith="+ leftWith);
        rightWith = rightView.getLayoutParams().width;
        //Log.i("test","rightWith="+ rightWith);
    }


    private ViewDragHelper.Callback callback=new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            //Log.i("test","尝试捕获");
            //如果还有动画没执行完  就直接关闭条目，并屏蔽此触摸事件
            /*if (viewDragHelper.continueSettling(true)){
                close();
                return false;
            }else{
                return true;
            }*/

            return true;
        }

        /**
         * 当tryCaptureView返回true的时候调用
         *
         * @param capturedChild 被捕获的孩子
         * @param activePointerId  跟多点触摸有关，手指的id
         */
        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
            Log.i("test","捕获");
            //isMove=false;
            //当前view展开的时候 其他view是不可能展开的，所以只有当前view是关闭的情况下被触摸才通知其他view关闭
            if (!isOpen){

                if (SideSlipLinearLayoutUtil.getInstance().getListSize()>0){
                    isHaveItem=true;
                }else{
                    isHaveItem=false;
                }

                //sendBroadCast();
                SideSlipLinearLayoutUtil.getInstance().clearItem();

            }
        }


        /**
         * 当tryCaptureView返回true后，手指又移动了，将会产生让view移动，通过此方法控制view的移动
         * 这个方法的重要作用：1 控制view的移动范围，2 可以添加阻力效果
         * @param child  被捕获的view
         * @param left  建议值：view的left边的建议值=child.getLeft()+dx，，，child.getLeft()获取的是此view的上一个left值
         * @param dx  手指位置的变化量
         * @return  child的left边的位置
         */
        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            //Log.i("test","移动了");
            isMove=true;
            //Log.i("test","left="+left);
            if (child==leftView){
                if (left<-rightWith){
                    left=-rightWith;
                }else if(left>0){
                    left=0;
                }
            }else if (child==rightView){
                if (left>getWidth()){
                    left=getWidth();
                }else if(left<getWidth()-rightWith){
                    left=getWidth()-rightWith;
                }
            }

            //Log.i("test","百分比="+Math.abs(1.0f*left/rightWith));
            setpercent(Math.abs(1.0f*left/rightWith));

            return left;
        }


        /**
         * ViewDragHelper会根据clampViewPositionHorizontal和clampViewPositionVertical的返回值
         * 对view进行移动操作，如果移动了 此方法就会被调用，一般用来处理相关操作（导致其他变化发生的），比如两个view联动
         * 注意：此回调的 dx 和 dy 和上面的回调的 dx 和 dy 不一样，上面的 dx 和 dy 表示的是手指的移动的变化量
         * @param changedView  位置发生变化的view
         * @param left    changedView位置变化后的left  相当于changedView.getLeft()+dx
         * @param top     changedView位置变化后的top  相当于changedView.getTop()+dy
         * @param dx      changedView的left边的变化的值
         * @param dy      changedView的top边的变化的值
         */
        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            //Log.i("test","dx="+dx);
            if (changedView==leftView){
                //让rightView不跟随手指移动 而跟随leftView联动
                rightView.offsetLeftAndRight(dx);
            }else if (changedView==rightView){
                //让leftview不跟随手指移动 而跟随rightView联动
                leftView.offsetLeftAndRight(dx);
            }

            invalidate();
        }

        /**
         * 当手指离开屏幕的时候回调
         *
         * @param releasedChild  被释放的view
         * @param xvel   手指离开屏幕时候的水平方向的速度  单位是 像素/秒
         * @param yvel   手指离开屏幕时候的垂直方向的速度  单位是 像素/秒
         */
        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            Log.i("test","松手");

            //判断松手时rightView的left边的位置是偏左（关闭）还是偏右（打开）
            //如果是滑动事件
            if (isMove){
                if (leftView.getLeft()>-rightWith/2){
                    //关闭
                    close();
                }else if (leftView.getLeft()<=-rightWith/2){
                    //打开
                    open();
                }
            }else if (!isMove){
                //如果是单击事件
                //如果此item是打开状态  则 关闭
                if (leftView.getLeft()==-rightWith&&isOpen){
                    Log.i("test","这是点击事件  手指并没有移动");
                    //当手指按下到抬起没有移动，且条目是打开状态，就关闭条目
                    close();
                }else if(!isOpen){//如果是关闭状态
                    /**
                     *  此处有难点，当有其他item是打开状态的情况下，触摸事件也会走到这里，要想办法知道在手指按下之前是否有其他条目打开着在
                     *  解决办法 用isHaveItem标识
                     */
                    Log.i("test","没有移动"+" isMove="+isMove+" isOpen="+isOpen+" isHaveItem="+isHaveItem+" leftView.getLeft()"+leftView.getLeft()+" rightWith=-"+rightWith);
                    //如果手指指按下之前没有其他条目打开着在  那这就是单纯的点击事件，交给用户处理
                    if (!isHaveItem){
                        Log.i("test","此点击事件交给用户处理");
                        setClickContent();
                    }
                }
            }
            isMove=false;
            super.onViewReleased(releasedChild, xvel, yvel);
        }

        /**
         *一般返回正数即可
         * @param child 哪个view
         * @return   水平方向拖拽范围有多大
         */
        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return 1;
        }

        /**
         *一般返回正数即可
         * @param child 哪个view
         * @return   垂直方向拖拽范围有多大
         */
        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return 1;
        }
    };

    /**
     * 关闭
     */
    public void close() {
        SideSlipLinearLayoutUtil.getInstance().removeItem(this);
        /**
         * 固定写法，              是否能够平滑的移动
         * 判断一个view与指定的位置是否有差距
         * child  哪个view
         * finalLeft view的left边的参考位置
         * finalTop  view的top边的参考位置
         * return true表示不在指定位置上 false表示在指定位置上
         */
        if (viewDragHelper.smoothSlideViewTo(leftView,0,0)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
        Log.i("test","关");
        isOpen=false;
        setListening(isOpen);
    }

    /**
     * 关闭ForUtil
     */
    protected void closeForUtil() {
        /**
         * 固定写法，              是否能够平滑的移动
         * 判断一个view与指定的位置是否有差距
         * child  哪个view
         * finalLeft view的left边的参考位置
         * finalTop  view的top边的参考位置
         * return true表示不在指定位置上 false表示在指定位置上
         */
        if (viewDragHelper.smoothSlideViewTo(leftView,0,0)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
        Log.i("test","关");
        isOpen=false;
        setListening(isOpen);
    }


    /**
     * 打开
     */
    public void open() {
        /**
         * 如果 util里面获取的SideSlipLinearLayout不为空 且就是此view本身  那说明此view是打开状态
         * 就不重复执行打开，反正如果有打开的view 且不是此view自己，那就关闭，并从集合内删除
         * 避免对某一个view重复执行打开回调
         */
        if (SideSlipLinearLayoutUtil.getInstance().getSideSlipLinearLayout()!=null){
            if (SideSlipLinearLayoutUtil.getInstance().getSideSlipLinearLayout()!=this){
                SideSlipLinearLayoutUtil.getInstance().clearItem();
            }
        }

        if (viewDragHelper.smoothSlideViewTo(leftView,-rightWith,0)){
            //在下一帧刷新
            //因为父控件才知道mainView的所在位置 所以由draglayout去重绘
            ViewCompat.postInvalidateOnAnimation(this);
        }
        Log.i("test","开");
        isOpen=true;
        setListening(isOpen);

        SideSlipLinearLayoutUtil.getInstance().addItem(this);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        //固定写法  continueSettling会根据scroller移动一点点距离，如果还没到指定位置那就返回true 反之返回false
        if (viewDragHelper.continueSettling(true)){
            //继续重绘
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }


    //当此view附着于屏幕的时候  注册广播接收者
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.i("test","状态="+"附着");
        //registReceiver();
    }

    //当此view从屏幕分离的时候  注销广播接收者
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i("test","状态="+"分离");
       /* try {
            //注销广播
            context.unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
            throw new IllegalArgumentException("IllegalArgumentException");
        }*/

        //if (isOpen){
            //SideSlipLinearLayoutUtil.getInstance().removeItem(this);
        //}
    }


    // 注册广播接收者
    /*private void registReceiver() {
        receiver = new MyBroadcastReceive();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("SideSlipLinearLayout");//要接收的广播
        context.registerReceiver(receiver, intentFilter);//注册接收者
    }*/

    //发送广播
    /*private void sendBroadCast(){
        // 发送广播
        Intent intent = new Intent("SideSlipLinearLayout");
        //intent.putExtra("yxd", "123456");
        context.sendBroadcast(intent);
    }*/

    //接收广播
    /*private class MyBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.i("test", "这是使用动态的方法来创建的广播收受者" + intent.getStringExtra("yxd"));
            //Log.i("test", "这是使用动态的方法来创建的广播收受者" );
            SideSlipLinearLayoutUtil.getInstance().removeItem(SideSlipLinearLayout.this);
            if (isOpen){
                close();
            }
            //abortBroadcast();
        }
    }*/



    private OnSideSlipStateChangedListening onSideSlipStateChangedListening;
    public interface OnSideSlipStateChangedListening{
        //展开 关闭 状态
        void onSideSlipStateChanged(boolean isExpand);
        //侧滑的百分比
        void onSideSlip(float percent);
        //给用户实现的点击事件
        void onClickContent();
    }

    /**
     * 设置监听
     * @param onSideSlipStateChangedListening
     */
    public void setOnSideSlipStateChangedListening(OnSideSlipStateChangedListening onSideSlipStateChangedListening) {
        this.onSideSlipStateChangedListening = onSideSlipStateChangedListening;
    }

    //记录当前的状态  避免重复回调
    private boolean currentState=false;
    /**
     * 设置状态改变监听
     * @param b  状态 展开 或者 关闭
     */
    private void setListening(boolean b){
        if (currentState==b){
            return;
        }
        if (onSideSlipStateChangedListening!=null){
            onSideSlipStateChangedListening.onSideSlipStateChanged(b);
        }
        currentState=b;
    }

    /**
     * 设置接口回调百分比
     * @param f 滑动的百分比
     */
    private void setpercent(float f){
        if (onSideSlipStateChangedListening!=null){
            onSideSlipStateChangedListening.onSideSlip(f);
        }
    }

    /**
     * 点击事件
     */
    private void setClickContent(){
        if (onSideSlipStateChangedListening!=null){
            onSideSlipStateChangedListening.onClickContent();
        }
    }

}
