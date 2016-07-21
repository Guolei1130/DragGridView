package com.gl.draggridview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gl.ExplosionField.ExplosionField;
import com.gl.base.BaseDragAdapter;
import com.gl.tools.Util;

/**
 * Created by GeekZoo on 2016/3/10.
 */
public class DragGridView extends GridView {

    private static final String TAG = DragGridView.class.getSimpleName();

    private static final int DEFAULT_COLUMNS = 0 ;
    private static final double DEFAULT_SCALE = 1.2 ;
    private static final int DEFAULT_DRAG_BG_COLOR = Color.parseColor("#f5f5f5");
    private static final int DEFAULT_FIXED_NUMS = 2 ;
    private int mColumns ;
    private double mDragScale ;
    private int mDragColor;
    private int mFixed_nums;

    private ExplosionField mExplosionField;

    /**
     * 点击时X/Y位置
     **/
    public int downX;
    public int downY;
    /**
     * 点击时对应整个界面的X/Y位置
     **/
    public int windowX;
    public int windowY;
    /**
     * 屏幕上的X/Y位置
     **/
    private int win_view_x;
    private int win_view_y;
    /**
     * 拖动X/Y的距离
     **/
    int dragOffsetX;
    int dragOffsetY;

    public int dragPosition;
    /**
     * 开始拖动的ITEM的position
     **/
    private int startPosition;
    /**
     * UP后对应的ITEM的position
     **/
    private int endPosition;

    private int itemHeight;
    private int itemWidth;

    /**
     * 拖动时候对应的VIEW
     **/
    private View dragImageView = null;

    private WindowManager windowManager = null;
    private WindowManager.LayoutParams windowParams = null;


    private int holdPosition;
    private Vibrator mVibrator;

    private int mHorizontalSpacing = 1;
    private int mVerticalSpacing = 1;
    /**
     * 移动的时候最后动画的ID
     **/
    private String LastAnimationID;

    private boolean isMoving = false;

    private ViewGroup dragViewGroup;


    public DragGridView(Context context) {
        this(context, null);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.DragGridView,defStyleAttr,0);
        mColumns = a.getInt(R.styleable.DragGridView_columnscount,DEFAULT_COLUMNS);
        if (mColumns <= 2){
            throw new IllegalAccessError("you need set columnscount or columnscount should > 2");
        }
        mDragScale = (double) a.getFloat(R.styleable.DragGridView_scale_num, (float) DEFAULT_SCALE);
        mDragColor = a.getColor(R.styleable.DragGridView_item_background_color,DEFAULT_DRAG_BG_COLOR);
        mFixed_nums = a.getInt(R.styleable.DragGridView_fixed_num,DEFAULT_FIXED_NUMS);
        a.recycle();
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        View first_view = getChildAt(0);
        if (null != first_view){
            int childCount = getChildCount();
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth((float) 2.0);
            paint.setColor(Color.parseColor("#e5e5e5"));
            for (int i = 0; i < childCount; i++) {
                // TODO: 16-3-26 draw line
                View item = getChildAt(i);
                canvas.drawLine(item.getRight(), item.getTop(), item.getRight(), item.getBottom(), paint);
                canvas.drawLine(item.getLeft(), item.getBottom(), item.getRight(), item.getBottom(), paint);
            }
        }
    }

    private void init(Context context) {
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mHorizontalSpacing = Util.dip2px(context, mHorizontalSpacing);

        mExplosionField = ExplosionField.attach2Window((Activity) context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downX = (int) ev.getX();
            downY = (int) ev.getY();

            windowX = (int) ev.getX();
            windowY = (int) ev.getY();
            setOnItemClickListener(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean bool = true;
        if (dragImageView != null && dragPosition != AdapterView.INVALID_POSITION) {
            //  移动时候的对应x,y位置
            bool = super.onTouchEvent(ev);
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = (int) ev.getX();
                    downY = (int) ev.getY();

                    windowX = (int) ev.getX();
                    windowY = (int) ev.getY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    onDrag(x, y, (int) ev.getRawX(), (int) ev.getRawY());
                    if (!isMoving) {
                        onMove(x, y);
                    }
                    if (pointToPosition(x, y) != AdapterView.INVALID_POSITION) {
                        break;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    stopDrag();
                    onDrop(x, y);
                    ((BaseDragAdapter)getAdapter()).dragEnd();
                    ((BaseDragAdapter)getAdapter()).showAll();
                    requestDisallowInterceptTouchEvent(false);
                    break;
                default:

                    break;

            }
        }
        return super.onTouchEvent(ev);
    }


    /**
     * 在拖动的情况
     **/
    private void onDrop(int x, int y) {
        int tempPosition = pointToPosition(x, y);
        endPosition = tempPosition;
        BaseDragAdapter mDragAdapter = (BaseDragAdapter) getAdapter();
        mDragAdapter.notifyDataSetChanged();
    }

    /**
     * 在拖动的情况下
     */
    private void onDrag(int x, int y, int rawx, int rawy) {
        if (null != dragImageView) {
            windowParams.alpha = 1.f;
            windowParams.x = rawx - win_view_x;
            windowParams.y = rawy - win_view_y;
            windowManager.updateViewLayout(dragImageView, windowParams);
        }
    }

    /**
     * 长按点击监听
     *
     * @param ev
     */
    public void setOnItemClickListener(final MotionEvent ev) {
        setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int x = (int) ev.getX();
                int y = (int) ev.getY();

                startPosition = position;
                dragPosition = position;
                if (startPosition <= 1) {
                    return false;
                }
                dragViewGroup = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());
                TextView dragTextView = (TextView) dragViewGroup.findViewById(R.id.title);
                dragTextView.setSelected(true);
                dragTextView.setEnabled(false);
                itemHeight = dragViewGroup.getHeight();
                itemWidth = dragViewGroup.getWidth();
                if (dragPosition != AdapterView.INVALID_POSITION) {
                    win_view_x = windowX - dragViewGroup.getLeft();//VIEW相对自己的X，半斤
                    win_view_y = windowY - dragViewGroup.getTop();//VIEW相对自己的y，半斤
                    dragOffsetX = (int) (ev.getRawX() - x);//手指在屏幕的上X位置-手指在控件中的位置就是距离最左边的距离
                    dragOffsetY = (int) (ev.getRawY() - y);//手指在屏幕的上y位置-手指在控件中的位置就是距离最上边的距离
                    dragViewGroup.destroyDrawingCache();
                    dragViewGroup.setDrawingCacheEnabled(true);
                    dragViewGroup.setBackgroundColor(mDragColor);
                    Bitmap dragBitmap = Bitmap.createBitmap(dragViewGroup.getDrawingCache());
                    mVibrator.vibrate(50);//设置震动时间
                    startDrag(dragBitmap, (int) ev.getRawX(), (int) ev.getRawY());
                    /**
                     * 若用DragAdapter 则将下面这行注释取消
                     */
                    //hideDropItem();
//                    dragViewGroup.setVisibility(View.INVISIBLE);
                    ((BaseDragAdapter)getAdapter()).hidePosition(position);
                    isMoving = false;

                    requestDisallowInterceptTouchEvent(true);
                    return true;
                }

                return false;
            }
        });

        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "onItemClick: " + (view.getVisibility() == View.VISIBLE) );
            }
        });
//        setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.e("tag", "this is position->" + position);
//                ViewGroup dragViewGroup = (ViewGroup) getChildAt(position - getFirstVisiblePosition());
//                itemHeight = dragViewGroup.getHeight();
//                itemWidth = dragViewGroup.getWidth();
//                ViewGroup item = (ViewGroup) getChildAt(position);
//                item.setVisibility(View.INVISIBLE);
//                final int last = getAdapter().getCount()-1;
//                final int position_li = position;
//                mExplosionField.explode(dragViewGroup);
//                mExplosionField.setFinishLIstener(new ExplosionField.OnFinshListener() {
//                    @Override
//                    public void finish() {
//                        if (position_li == last) {
//
//                            ((BaseDragAdapter) getAdapter()).removePosition(position_li);
//
//                            return;
//                        }
//                        back(position_li);
//                    }
//                });
//
//            }
//        });
    }

    private void back(final int position) {

        float to_x = 0, to_y = 0;
        float x_vlaue = ((float) mHorizontalSpacing / (float) itemWidth) + 1.0f;
        //y_vlaue移动的距离百分比（相对于自己宽度的百分比）
        float y_vlaue = ((float) mVerticalSpacing / (float) itemHeight) + 1.0f;
        Log.e("tag", "这是  啊---》" + x_vlaue + ";" + y_vlaue);
        int count = getAdapter().getCount();
        for (int i = position +1 ; i < count ; i++) {
            if (i  % mColumns == 0){
                //
                to_x = x_vlaue * (mColumns-1);
                to_y = -y_vlaue;
            }else {
                to_x = -x_vlaue;
                to_y = 0;
            }
            Log.e("tag","这是移动参数--->"+to_x+";"+to_y);
            ViewGroup moveViewGroup = (ViewGroup) getChildAt(i);
            Animation moveAnimation = getMoveAnimation(to_x, to_y);
            moveViewGroup.startAnimation(moveAnimation);
            //如果是最后一个移动的，那么设置他的最后个动画ID为LastAnimationID
            if (holdPosition == endPosition) {
                LastAnimationID = moveAnimation.toString();
            }
            moveAnimation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    // TODO Auto-generated method stub
                    isMoving = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // TODO Auto-generated method stub
                    if (animation.toString().equalsIgnoreCase(LastAnimationID)) {
                        BaseDragAdapter mDragAdapter = (BaseDragAdapter) getAdapter();
                        mDragAdapter.removePosition(position);
                    }
                }
            });
        }

    }

    private void onMove(int x, int y) {
        // 拖动的VIEW下方的POSITION
        int dPosition = pointToPosition(x, y);
        // 判断下方的POSITION是否是最开始2个不能拖动的
        if (dPosition > mFixed_nums - 1) {
            if ((dPosition == -1) || (dPosition == dragPosition)) {
                return;
            }
            endPosition = dPosition;
            if (dragPosition != startPosition) {
                dragPosition = startPosition;
            }
            int movecount;
            //拖动的=开始拖的，并且 拖动的 不等于放下的
            if ((dragPosition == startPosition) || (dragPosition != endPosition)) {
                //移需要移动的动ITEM数量
                movecount = endPosition - dragPosition;
            } else {
                return;
            }

            int movecount_abs = Math.abs(movecount);

            if (dPosition != dragPosition) {
                //dragGroup设置为不可见
//                ViewGroup dragGroup = (ViewGroup) getChildAt(dragPosition);
//                dragViewGroup.setVisibility(View.INVISIBLE);
                float to_x = 1;// 当前下方positon
                float to_y;// 当前下方右边positon
                //x_vlaue移动的距离百分比（相对于自己长度的百分比）
                float x_vlaue = ((float) mHorizontalSpacing / (float) itemWidth) + 1.0f;
                //y_vlaue移动的距离百分比（相对于自己宽度的百分比）
                float y_vlaue = ((float) mVerticalSpacing / (float) itemHeight) + 1.0f;
                Log.d("x_vlaue", "x_vlaue = " + x_vlaue);
                for (int i = 0; i < movecount_abs; i++) {
                    to_x = x_vlaue;
                    to_y = y_vlaue;
                    //像左
                    if (movecount > 0) {
                        // 判断是不是同一行的
                        holdPosition = dragPosition + i + 1;
                        if (dragPosition / mColumns == holdPosition / mColumns) {
                            to_x = -x_vlaue;
                            to_y = 0;
                        } else if (holdPosition % mColumns == 0) {
                            to_x = (mColumns-1) * x_vlaue;
                            to_y = -y_vlaue;
                        } else {
                            to_x = -x_vlaue;
                            to_y = 0;
                        }
                    } else {
                        //向右,下移到上，右移到左
                        holdPosition = dragPosition - i - 1;
                        if (dragPosition / mColumns == holdPosition / mColumns) {
                            to_x = x_vlaue;
                            to_y = 0;
                        } else if ((holdPosition + 1) % mColumns == 0) {
                            to_x = -(mColumns-1) * x_vlaue;
                            to_y = y_vlaue;
                        } else {
                            to_x = x_vlaue;
                            to_y = 0;
                        }
                    }
                    ViewGroup moveViewGroup = (ViewGroup) getChildAt(holdPosition);

                    Animation moveAnimation = getMoveAnimation(to_x, to_y);

                    moveViewGroup.startAnimation(moveAnimation);
                    //如果是最后一个移动的，那么设置他的最后个动画ID为LastAnimationID
                    if (holdPosition == endPosition) {
                        LastAnimationID = moveAnimation.toString();
                    }
                    moveAnimation.setAnimationListener(new Animation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
                            // TODO Auto-generated method stub
                            isMoving = true;
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            // TODO Auto-generated method stub
                            if (animation.toString().equalsIgnoreCase(LastAnimationID)) {
                                BaseDragAdapter mDragAdapter = (BaseDragAdapter) getAdapter();
                                mDragAdapter.exchange(startPosition, endPosition);
                                startPosition = endPosition;
                                dragPosition = endPosition;
                                isMoving = false;
                            }
                        }
                    });
                }
            }
        }
    }

    private void stopDrag() {
        if (dragImageView != null) {
            windowManager.removeView(dragImageView);
            dragImageView = null;
        }
    }

    public void startDrag(Bitmap dragBitmap, int x, int y) {
        stopDrag();
        windowParams = new WindowManager.LayoutParams();
        windowParams.gravity = Gravity.TOP | Gravity.LEFT;
        windowParams.x = x - win_view_x;
        windowParams.y = y - win_view_y;
        windowParams.width = (int) (mDragScale * dragBitmap.getWidth());// 放大dragScale倍，可以设置拖动后的倍数
        windowParams.height = (int) (mDragScale * dragBitmap.getHeight());// 放大dragScale倍，可以设置拖动后的倍数
        this.windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        this.windowParams.format = PixelFormat.TRANSLUCENT;
        this.windowParams.windowAnimations = 0;
        ImageView iv = new ImageView(getContext());
        iv.setImageBitmap(dragBitmap);
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);// "window"
        windowManager.addView(iv, windowParams);
        dragImageView = iv;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    /**
     * 获取移动动画
     */
    public Animation getMoveAnimation(float toXValue, float toYValue) {
        TranslateAnimation mTranslateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0F,
                Animation.RELATIVE_TO_SELF, toXValue,
                Animation.RELATIVE_TO_SELF, 0.0F,
                Animation.RELATIVE_TO_SELF, toYValue);// 当前位置移动到指定位置
        mTranslateAnimation.setFillAfter(true);// 设置一个动画效果执行完毕后，View对象保留在终止的位置。
        mTranslateAnimation.setDuration(300L);
        return mTranslateAnimation;
    }
}
