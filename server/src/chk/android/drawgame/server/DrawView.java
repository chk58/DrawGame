package chk.android.drawgame.server;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class DrawView extends View {
    private static final int DEFAULT_BACKGOUND = Color.WHITE;
    private static final int DEFAULT_DRAWCOLOR = Color.BLACK;
    private static final int DEFAULT_STROKE_WIDTH = 5;

    private Bitmap mBoard;
    private Canvas mBoardCanvas;
    private Paint mPaint;
    private float mMoveX;
    private float mMoveY;
    private int mWidth;
    private int mHeight;

    public DrawView(Context context) {
        this(context, null, 0);
    }

    public DrawView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setBackgroundColor(DEFAULT_BACKGOUND);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(DEFAULT_DRAWCOLOR);
        mPaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);

        mWidth = MainActivity.sWindowSize.x
                - context.getResources().getDimensionPixelOffset(R.dimen.info_container_width);
        mHeight = MainActivity.sWindowSize.y
                - context.getResources().getDimensionPixelOffset(R.dimen.action_container_height);

        mBoard = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mBoardCanvas = new Canvas(mBoard);
        mBoardCanvas.drawColor(DEFAULT_BACKGOUND);
    }

    public void setDrawColor(int color) {
        mPaint.setColor(color);
    }

    public void setStrokeWidth(int width) {
        mPaint.setStrokeWidth(width);
    }

    public void clear() {
        mBoardCanvas.drawColor(DEFAULT_BACKGOUND);
        invalidate();
    }

    public Bitmap getBitmap() {
        return mBoard;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBoard, 0, 0, null);
    }

    public void drawPoint(float xScale, float yScale) {
        float x = xScale * mWidth;
        float y = yScale * mHeight;
        mBoardCanvas.drawPoint(x, y, mPaint);
        invalidate();
        mMoveX = x;
        mMoveY = y;
    }

    public void drawLine(float xScale, float yScale) {
        float x = xScale * mWidth;
        float y = yScale * mHeight;
        mBoardCanvas.drawLine(mMoveX, mMoveY, x, y, mPaint);
        invalidate();
        mMoveX = x;
        mMoveY = y;
    }

    public void onDestory() {
        mBoard.recycle();
    }
}
