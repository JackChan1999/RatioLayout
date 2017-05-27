<img src="art/RadioLayout.jpg" width="400" />

我们经常碰到服务器返回的图片比例大小是一样的，但是分辨力却是不一样的。这时候，就会遇到显示效果的问题。例如，图1和图2都是宽高比例相等，但是分辨率大小不一样的图片，应该按照比例显示，使用等比例显示控件后，图2的显示效果如图3所示，和图1的显示效果是一致的，解决了宽高比相等或接近但分辨率大小不一样而造成的显示效果不一致的问题

## 解决方法一

自定义控件，自定义ViewGroup继承FrameLayout ，重写onMeasure()方法，根据图片宽高比例和获取到图片的宽度（图片的宽度是具体值或者match_parent，match_parent宽度就是屏幕宽度）计算出图片的高度，然后唤醒重新测量ImageView。没有继承ImageView而是FrameLayout ，是因为自定义ViewGroup更加通用，不仅可以用于ImageView的等比例显示，还可以用于其他的控件（如Button）的等比例显示

```java
package com.google.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.google.widget.R;

public class RatioLayout extends FrameLayout {

    private float mPicRatio;//图片的宽高比
    public static final int RELATIVE_WIDTH = 0;//控件的宽度固定，根据比例求出高度
    public static final int RELATIVE_HEIGHT = 1;//控件的高度固定，根据比例求出宽度
    private int mRelative = RELATIVE_WIDTH;

    public RatioLayout(Context context) {
        this(context, null);
    }

    public RatioLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RatioLayout);
        for (int i = 0; i < array.getIndexCount(); i++) {
            switch (i) {
                case R.styleable.RatioLayout_ratio:
                    mPicRatio = array.getFloat(i, 2.43f);
                    break;
                case R.styleable.RatioLayout_relative:
                    mRelative = array.getInt(i, 0);
                    break;
            }
        }
        array.recycle();
    }

    public void setPicRatio(float picRatio) {
        mPicRatio = picRatio;
    }

    public void setRelative(int relative) {
        mRelative = relative;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int childWidth = widthSize - getPaddingLeft() - getPaddingRight();
        int childHeight = heightSize - getPaddingBottom() - getPaddingTop();

        if (widthMode == MeasureSpec.EXACTLY && mPicRatio != 0 && mRelative == RELATIVE_WIDTH) {
            //修正高度的值
            childHeight = (int) (childWidth / mPicRatio + 0.5f);
        } else if (heightMode == MeasureSpec.EXACTLY && mPicRatio != 0 && mRelative ==
                RELATIVE_HEIGHT) {
            //修正宽度的值
            childWidth = (int) (childHeight * mPicRatio + 0.5f);
        }
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
        measureChildren(childWidthMeasureSpec, childHeightMeasureSpec);
        setMeasuredDimension(childWidth + getPaddingLeft() + getPaddingRight(), childHeight +
                getPaddingBottom() + getPaddingTop());

       /* widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth + getPaddingLeft() +
                getPaddingRight(), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight + getPaddingBottom() +
                getPaddingTop(), MeasureSpec.EXACTLY);
        super.measure(widthMeasureSpec, heightMeasureSpec);*/
    }
}

```

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:google="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="10dp"
        android:orientation="vertical">

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="分辨率：444x183，宽高比2.426"
            android:textSize="20sp"/>

        <ImageView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="15dp"
            android:background="#4887EE"
            android:scaleType="fitXY"
            android:src="@mipmap/recommend_05"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="分辨率：828x341，宽高比2.428"
            android:textSize="20sp"/>

        <ImageView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="#4887EE"
            android:scaleType="fitXY"
            android:src="@mipmap/recommend_32"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="15dp"
            android:text="按比例显示"
            android:textSize="20sp"/>
        <com.google.widget.view.RatioLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="#4887EE"
            google:ratio="2.43">

            <ImageView
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:src="@mipmap/recommend_32"/>
        </com.google.widget.view.RatioLayout>

    </LinearLayout>
</ScrollView>
```
## 解决方法二

设置图片请求监听，拿到图片的实际宽度后，根据宽高比计算出ImageView的高度，然后通过setLayoutParams()重新设置ImageView的参数

```java
public class ImageUtils {

    /**
     * 自适应宽度加载图片。保持图片的长宽比例不变，通过修改imageView的高度来完全显示图片。
     * Note: ImageView android:layout_width="match_parent"
     */
    public static void loadIntoUseFitWidth(Context context, final String imageUrl, int errorImageId, final ImageView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(final GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (imageView == null) {
                            return false;
                        }
                        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                setLayoutParams(resource, imageView);
                                imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            }
                        });
                        return false;
                    }
                })
                .placeholder(errorImageId)
                .error(errorImageId)
                .into(imageView);
    }

    private static void setLayoutParams(GlideDrawable resource, ImageView imageView) {
        if (imageView.getScaleType() != ImageView.ScaleType.FIT_XY) {
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        // 获取容器实际存放图片的宽度
        int vw = imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
        // 计算出容器实际存放图片宽度与图片资源宽度的比例
        float scale = (float) vw / (float) resource.getIntrinsicWidth();
        // 依据比例算出容器实际存放图片高度值
        int vh = Math.round(resource.getIntrinsicHeight() * scale);
        // 计算容器的高度
        params.height = vh + imageView.getPaddingTop() + imageView.getPaddingBottom();
        imageView.setLayoutParams(params);
    }

}
```