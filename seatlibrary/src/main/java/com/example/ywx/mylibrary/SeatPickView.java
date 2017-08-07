package com.example.ywx.mylibrary;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ywx on 2017/8/4.
 * 图书馆座选择控件
 */

public class SeatPickView extends View implements ScaleGestureDetector.OnScaleGestureListener,GestureDetector.OnDoubleTapListener,GestureDetector.OnGestureListener{
    //座位为空
    private static final int SEAT_EMPTY=0;
    //座位被预约
    private static final int SEAT_SELECTED=1;
    //座位正在使用
    private static final int SEAT_STUDING=2;
    //座位暂时离开
    private static final int SEAT_LEAVE=3;
    //长延时
    private static final int DELAY_LONG=1000;
    //当前x方向移动距离
    float translateX ;
    //当前y方向移动距离
    float translateY ;
    //最大最小缩放比例
    private float maxScale,minScale;
    //水平座位间隔
    private int spaceX;
    //竖直座位间隔
    private int spaceY;
    //控件宽度
    private int mWidth;
    //控件高度
    private int mHeight;
    //进行界面缩放平移的matrix
    private Matrix matrix;
    //各个座位状态的bitmap图片
    private Bitmap seatLock,seatChecked,seatEmpty,seatLeave,table;
    //图片高度和宽度
    private float seatHeight,seatWidth;
    //缩放手势检测
    private ScaleGestureDetector scaleGestureDetector;
    //手势检测
    private GestureDetector gestureDetector;
    //当前缩放值
    private float scale;
    //座位的数据源
    private List<SeatInfo>seatList=new ArrayList<>();
    //行列的个数
    private int rowSize=0,columSize=0;
    //绘制行列数的画笔、背景画笔、缩略图矩形框画笔
    private Paint textPaint,bcPaint,smallTextPaint,mapRectPiant;
    //记录每一行的座位数
    private int[] eachRow=new int[100];
    //当前选择的座位数
    private int selectedCount;
    //最大能选择的座位数量
    private int maxSelectedCount;
    //选择到达最大座位数的回调接口
    private OnSelectedMaxListener onSelectedMaxListener;
    //座位点击监听
    private OnSelectListener onSelectListener;
    //已选择的座位列表
    private List<SeatInfo>selectedSeatList=new ArrayList<>();
    //座位号字体大小
    private float seatNumTextSize;
    //缩略图的缩放比例
    private float mapWidthScale,mapHeightScale;
    //当前缩放比例下整个布局的大小
    private float realWidth,realHeight;
    //是否显示缩略图
    private boolean isShowMap=false;
    //计算缩略图关闭前手指点击次数
    private int runInTime=0;
    //是否是缩略图关闭后第一次点击
    private boolean isFirstClick=true;
    //允许超出的边界的范围
    private float offset;

    public SeatPickView(Context context) {
        this(context,null);
    }

    public SeatPickView(Context context,  AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SeatPickView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化
        init(context,attrs,defStyleAttr);
    }

    /**
     * 进行控件初始化
     */
    private void init(Context context,AttributeSet attrs, int defStyleAttr) {
        TypedArray ta=context.obtainStyledAttributes(attrs,R.styleable.SeatPickView);
        //获取用户设置的最大选取的座位数,默认为5
        maxSelectedCount=ta.getInt(R.styleable.SeatPickView_max_seat,5);
        matrix=new Matrix();
        //解析图片
        seatLock= BitmapFactory.decodeResource(getResources(),R.mipmap.seat_selected);
        seatChecked=BitmapFactory.decodeResource(getResources(),R.mipmap.seat_selecting);
        seatEmpty=BitmapFactory.decodeResource(getResources(),R.mipmap.seat_empty);
        seatLeave=BitmapFactory.decodeResource(getResources(),R.mipmap.seat_leave);
        table=BitmapFactory.decodeResource(getResources(),R.mipmap.table);

        //设置间距
        spaceX=100;
        spaceY=340;

        //允许超出的边界范围
        offset=250;

        //设置缩放监听器
        scaleGestureDetector=new ScaleGestureDetector(context,this);
        gestureDetector=new GestureDetector(context,this);
        gestureDetector.setOnDoubleTapListener(this);

        //当前缩放比例，默认0.2
        scale=0.2f;

        //规定最大和最小缩放比例
        maxScale=0.8f;
        minScale=0.2f;

        //设置默认移动距离为50
        translateX=50;
        translateY=50;

        //初始化画笔
        textPaint=new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(30);
        textPaint.setColor(Color.WHITE);
        textPaint.setStrokeWidth(1);

        bcPaint=new Paint();
        bcPaint.setAntiAlias(true);
        bcPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        bcPaint.setColor(Color.parseColor("#80000000"));

        //初始化选择的座位数为0
        selectedCount=0;

        seatNumTextSize=75f;
        smallTextPaint=new Paint();
        smallTextPaint.setAntiAlias(true);
        smallTextPaint.setTextSize(seatNumTextSize);
        smallTextPaint.setColor(Color.BLACK);
        textPaint.setStrokeWidth(1);

        mapRectPiant=new Paint();
        mapRectPiant.setAntiAlias(true);
        mapRectPiant.setStyle(Paint.Style.STROKE);
        mapRectPiant.setStrokeWidth(1);
        mapRectPiant.setColor(Color.RED);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        int widhMode=MeasureSpec.getMode(widthMeasureSpec);

        int heightSize=MeasureSpec.getSize(heightMeasureSpec);
        int heightMode=MeasureSpec.getMode(heightMeasureSpec);

        //如果控件宽度指定为wrap_content就让它等于屏幕宽度
        if(widhMode==MeasureSpec.AT_MOST){
            widthSize=getResources().getDisplayMetrics().widthPixels;
        }

        //如果控件高度指定为wrap_content就让它等于屏幕高度一半
        if(heightMode==MeasureSpec.AT_MOST){
            heightSize=getResources().getDisplayMetrics().heightPixels/2;
        }

        mWidth=widthSize;
        mHeight=heightSize;
        //设置最后测量的宽高
        setMeasuredDimension(widthSize,heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制座位并绘制座位坐标
        drawSeat(canvas);

        //绘制桌子
        drawTable(canvas);

        //绘制列文本
        drawText(canvas);

        //绘制缩略图
        if(isShowMap) {
            drawMap(canvas);
        }
    }

    /**
     * 绘制缩略图矩形框
     * @param canvas
     */
    private void drawMapRect(Canvas canvas) {
        //获取长宽比例中较小的
        float mapScale=Math.min(mapWidthScale,mapHeightScale);
        RectF rect=new RectF();
        rect.top=-translateY*mapScale;
        rect.left=-translateX*mapScale;
        rect.bottom=rect.top+mHeight*mapScale;
        rect.right=rect.left+mWidth*mapScale;
        canvas.drawRect(rect,mapRectPiant);
    }

    /**
     * 绘制缩略图
     * @param canvas
     */
    private void drawMap(Canvas canvas) {
        //定义缩略图的宽高都为控件高度的9分之4
        int mapWidth=mWidth/3;
        int mapHeight=mHeight/2;
        //获得缩略图的缩放比例
        mapWidthScale=mapWidth/realWidth;
        mapHeightScale=mapHeight/realHeight;
        //获取长宽比例中较小的，保持缩略图座位比例
        float mapScale=Math.min(mapWidthScale,mapHeightScale);
        //初始化背景矩形
        RectF rect=new RectF();
        rect.left=0;
        rect.top=0;
        rect.bottom=mapHeight;
        rect.right=mapWidth;

        canvas.drawRect(rect,bcPaint);

        //与绘制座位一样只是缩放比例改变
        Matrix tempMatrix=new Matrix();
        if(seatList.size()!=0) {
            //遍历座位数组，绘制座位
            for (SeatInfo seat : seatList) {
                float top = seat.getRow() * seatHeight * scale*mapScale + seat.getRow() * spaceY*scale*mapScale;
                float left = seat.getColums() * seatWidth *scale* mapScale + seat.getColums() * spaceX*scale*mapScale;
                tempMatrix.setTranslate(left, top);
                //座位大小等于当前座位大小再乘上缩略图缩放比例
                tempMatrix.postScale(scale*mapScale, scale*mapScale, left, top);
                switch (seat.getSeatstatue()){
                    case SEAT_EMPTY:
                        if(!seat.isChoose()) {
                            canvas.drawBitmap(seatEmpty, tempMatrix, null);
                        }else {
                            canvas.drawBitmap(seatChecked, tempMatrix, null);
                        }
                        break;
                    case SEAT_SELECTED:
                    case SEAT_STUDING:
                        canvas.drawBitmap(seatLock,tempMatrix,null);
                        break;
                    case SEAT_LEAVE:
                        canvas.drawBitmap(seatLeave,tempMatrix,null);
                        break;

                }
            }
        }


        //绘制缩略图桌子
        for(int i=0;i<rowSize;i++) {
            float top = i * seatHeight * scale *mapScale + i * spaceY*scale*mapScale + seatHeight * scale*mapScale + spaceY*scale*mapScale / 6;
            if(i%2==0) {
                for (int j = 0; j <= Math.max(eachRow[i],eachRow[i+1]); j += 2) {
                    float left = j * seatWidth * scale*mapScale + j * spaceX*scale*mapScale  + (seatWidth * scale*mapScale+spaceX*scale*mapScale) / 2;
                    tempMatrix.setTranslate(left, top);
                    tempMatrix.postScale(scale*mapScale, scale*mapScale, left, top);
                    canvas.drawBitmap(table, tempMatrix, null);
                }
            }
        }

        //绘制缩略图矩形框
        drawMapRect(canvas);
    }

    /**
     * 绘制桌子
     * @param canvas
     */
    private void drawTable(Canvas canvas) {
        Matrix tempMatrix=new Matrix();
        for(int i=0;i<rowSize;i++) {
            float top = i * seatHeight * scale + i * spaceY*scale + translateY + seatHeight * scale + spaceY*scale / 6;
            if(i%2==0) {
                for (int j = 0; j <=Math.max(eachRow[i],eachRow[i+1]); j += 2) {
                    float left = j * seatWidth * scale + j * spaceX*scale + translateX + (seatWidth * scale+spaceX*scale) / 2;
                    tempMatrix.setTranslate(left, top);
                    tempMatrix.postScale(scale, scale, left, top);
                    canvas.drawBitmap(table, tempMatrix, null);
                }
            }
        }
    }

    /**
     * 绘制列文本
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        RectF rect=new RectF();
        rect.right=mWidth;
        rect.left=mWidth-textPaint.measureText("10");
        rect.top=0;
        rect.bottom=rowSize*(seatHeight*scale+spaceY*scale);
        canvas.drawRect(rect,bcPaint);
        for(int i=0;i<rowSize;i++){
            float top = i* seatHeight * scale + i * spaceY*scale + translateY+(seatHeight*scale)/4*3;
            canvas.drawText(i+1+"",mWidth-textPaint.measureText(i+1+""),top,textPaint);
        }
    }

    /**
     * 绘制座位并绘制座位坐标
     * @param canvas
     */
    private void drawSeat(Canvas canvas){
        smallTextPaint.setTextSize(seatNumTextSize*scale);
        //变化矩阵
        Matrix tempMatrix=new Matrix();
        //获取图片的宽高
        seatHeight=seatChecked.getHeight();
        seatWidth=seatChecked.getWidth();
        if(seatList.size()!=0) {
            //遍历座位数组，绘制座位
            for (SeatInfo seat : seatList) {
                rowSize=Math.max(rowSize,seat.getRow()+1);
                columSize=Math.max(columSize,seat.getColums()+1);
                float top = seat.getRow() * seatHeight * scale + seat.getRow() * spaceY*scale + translateY;
                float left = seat.getColums() * seatWidth * scale + seat.getColums() * spaceX*scale + translateX;
                tempMatrix.setTranslate(left, top);
                tempMatrix.postScale(scale, scale, left, top);
                switch (seat.getSeatstatue()){
                    case SEAT_EMPTY:
                        if(!seat.isChoose()) {
                            canvas.drawBitmap(seatEmpty, tempMatrix, null);
                        }else {
                            canvas.drawBitmap(seatChecked, tempMatrix, null);
                        }
                        break;
                    case SEAT_SELECTED:
                    case SEAT_STUDING:
                        canvas.drawBitmap(seatLock,tempMatrix,null);
                        break;
                    case SEAT_LEAVE:
                        canvas.drawBitmap(seatLeave,tempMatrix,null);
                        break;

                }
                float textWidth=smallTextPaint.measureText((seat.getRow()+1)+","+(seat.getColums()+1));
                canvas.drawText((seat.getRow()+1)+","+(seat.getColums()+1),left+seatWidth*scale/2-textWidth/2,top+seatHeight*scale/2,smallTextPaint);
            }

            //初始化每一行默认为0个座位
            for(int i=0;i<rowSize;i++){
                eachRow[i]=0;
            }

            //遍历每一行，得到每一行的座位数
            for(int i=0;i<rowSize;i++){
                int max=0;
                for (SeatInfo seat : seatList) {
                    if(seat.getRow()==i){
                        max=Math.max(max,seat.getColums());
                    }
                }
                eachRow[i]=max;
            }
        }
        //绘制完座位获取整个布局大小
        realWidth=columSize*(seatWidth*scale+spaceX*scale);
        realHeight=rowSize*(seatHeight*scale+spaceY*scale);
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        float scaleFactor=scaleGestureDetector.getScaleFactor();
        //当缩放比例不超过最大并且不小于最小缩放比例，可以进行缩放
        if(scale*scaleFactor<=maxScale&&scale*scaleFactor>=minScale){
            scale=scale*scaleFactor;
            //缩放后进行位置矫正
            translateX-=(scaleFactor-1)*(-translateX);
            translateY-=(scaleFactor-1)*(-translateY);
        }
        invalidate();
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        //进行缩放后如果超出了边界，回到边界
        if(translateX<-(realWidth-mWidth)+offset*scale) {
            ValueAnimator valueX = ValueAnimator.ofFloat(translateX, Math.min(0,-(realWidth-mWidth))).setDuration(500);
            valueX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    translateX= (float) valueAnimator.getAnimatedValue();
                    invalidate();
                }
            });
            valueX.start();
        }else if(translateX>offset*scale){
            ValueAnimator valueX = ValueAnimator.ofFloat(translateX, 0).setDuration(500);
            valueX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    translateX= (float) valueAnimator.getAnimatedValue();
                    invalidate();
                }
            });
            valueX.start();
        }
        if(translateY<-(realHeight-mHeight+offset*scale)) {
            ValueAnimator valueY = ValueAnimator.ofFloat(translateY,Math.min(0,-(realHeight-mHeight))).setDuration(500);
            valueY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    translateY = (float) valueAnimator.getAnimatedValue();
                    invalidate();
                }
            });
            valueY.start();
        }else if(translateY>offset*scale){
            ValueAnimator valueY = ValueAnimator.ofFloat(translateY,0).setDuration(500);
            valueY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    translateY = (float) valueAnimator.getAnimatedValue();
                    invalidate();
                }
            });
            valueY.start();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        runInTime++;
        //如果完成一次点击并且是上次缩略图关闭后的第一次点击就开启延时关闭缩略图
        if(runInTime==2&&isFirstClick) {
            isFirstClick=false;
            delayCloseMap(DELAY_LONG);
        }
        if(gestureDetector.onTouchEvent(event)){
            return true;
        }
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        //获取当前点击的列数
        int clickY=(int)((-translateX+motionEvent.getX())/(seatWidth*scale+spaceX*scale));
        //获取当前点击的行数
        int clickX=(int)((-translateY+motionEvent.getY())/(seatHeight*scale+spaceY*scale));

        //改变座位状态
        chooseSeat(clickX,clickY);
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        //手指触摸屏幕显示缩略图
        isShowMap=true;
        invalidate();
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        //边界判断
        if(translateX-v<=offset*scale&&translateX-v>=-(realWidth-mWidth+offset*scale)){
            translateX=translateX-v;
        }
        Log.d("offset*scale",offset*scale+"");
        Log.d("translateY",translateY+"");
        if(translateY-v1<=offset*scale&&translateY-v1>=-(realHeight-mHeight+offset*scale)){
            translateY=translateY-v1;
        }
        invalidate();
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    public void setSeatList(List<SeatInfo> seatList) {
        this.seatList = seatList;
        invalidate();
    }

    private void chooseSeat(int x,int y){
        for(SeatInfo seat:seatList){
            //获取当前点击坐标的座位并且该座位是空闲状态
            if(seat.getColums()==y&&seat.getRow()==x&&seat.getSeatstatue()==0){
                //如果当前已经选中，则选择座位数减一，否则加一
                if(seat.isChoose()){
                    //遍历找到当前取消的座位和在已选座位列表中的位置并删除
                    for(int i=0;i<selectedSeatList.size();i++){
                        if(selectedSeatList.get(i).getColums()==y&&selectedSeatList.get(i).getRow()==x){
                            //回调点击取消接口
                            if(onSelectListener!=null){
                                onSelectListener.onRemove(selectedSeatList.get(i));
                            }
                            selectedSeatList.remove(i);
                            break;
                        }
                    }
                    selectedCount--;
                }else {
                    selectedSeatList.add(seat);
                    selectedCount++;
                    //点击当前座位将数量加一后如果不超过最大座位数就回调增加座位的点击回调
                    if(selectedCount<=maxSelectedCount){
                        if(onSelectListener!=null){
                            onSelectListener.onSelect(seat);
                        }
                    }
                }
                //如果当前选中的座位数小于等于最大可选数量，就响应，否则不响应
                if(selectedCount<=maxSelectedCount) {
                    //点击有效就显示缩略图并短延时关闭
                    seat.setChoose(!seat.isChoose());
                    invalidate();
                }else {
                    selectedSeatList.remove(selectedSeatList.size()-1);
                    selectedCount--;
                    //达到上限回调接口
                    if(onSelectedMaxListener!=null){
                        onSelectedMaxListener.onSelectedMax(selectedCount);
                    }
                }
                return ;
            }
        }
    }

    public void setSelectedCount(int selectedCount) {
        this.selectedCount = selectedCount;
    }

    public void setMaxSelectedCount(int maxSelectedCount) {
        this.maxSelectedCount = maxSelectedCount;
    }

    public int getSelectedCount() {
        return selectedCount;
    }

    public void setOnSelectedMaxListener(OnSelectedMaxListener onSelectedMaxListener) {
        this.onSelectedMaxListener = onSelectedMaxListener;
    }

    public List<SeatInfo> getSelectedSeatList() {
        return selectedSeatList;
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    /**
     * 延迟关闭缩略图
     */
    private void delayCloseMap(final int time){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //如果当该关闭的时候当前点击次数超过一次，再延时
                if(runInTime>2){
                    runInTime=0;
                    //再次延时
                    postDelayed(this,time);
                }
                //如果当前点击小于等于一次了就执行关闭缩略图
                else{
                    runInTime=0;
                    //设置为第一次点击方便下一次点击判断
                    isFirstClick=true;
                    isShowMap = false;
                    invalidate();
                }
            }
        },time);
    }
}
