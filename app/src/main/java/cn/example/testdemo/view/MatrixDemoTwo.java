package cn.example.testdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import cn.example.testdemo.R;

/**
 * Author : ZSK
 * Date : 2020/3/12
 * Description :
 */
public class MatrixDemoTwo extends View {


    private static final String TAG = "MatrixSetRectToRectTest";

    private int mViewWidth, mViewHeight;

    private Bitmap mBitmap;             // 要绘制的图片
    private Matrix mRectMatrix;         // 测试etRectToRect用的Matrix



    public MatrixDemoTwo(Context context) {
        super(context,null);
    }

    public MatrixDemoTwo(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs,0);
    }

    public MatrixDemoTwo(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.def);
        mRectMatrix = new Matrix();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        RectF src= new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight() );
        RectF dst = new RectF(0, 0, mViewWidth, mViewHeight );

        // 核心要点
        mRectMatrix.setRectToRect(src,dst, Matrix.ScaleToFit.CENTER);

        // 根据Matrix绘制一个变换后的图片
        canvas.drawBitmap(mBitmap, mRectMatrix, new Paint());
    }
}
