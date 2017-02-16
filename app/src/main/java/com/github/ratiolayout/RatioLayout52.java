package com.github.ratiolayout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class RatioLayout52 extends FrameLayout {
	// 按照宽高比例去显示
	private float ratio = 2.43f; // 比例值

	public void setRatio(float ratio) {
		this.ratio = ratio;
	}

	public RatioLayout52(Context context) {
		super(context);
	}

	public RatioLayout52(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// 参数1 命名控件 参数2 属性的名字 参数3 默认的值
		float ratio = attrs.getAttributeFloatValue(
				"http://schemas.android.com/apk/res/com.itheima.googleplay",
				"ratio", 2.43f);
		setRatio(ratio);
	}

	public RatioLayout52(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	// 测量当前布局
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// widthMeasureSpec 宽度的规则 包含了两部分 模式 值
		int widthMode = MeasureSpec.getMode(widthMeasureSpec); // 模式
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);// 宽度大小
		int width = widthSize - getPaddingLeft() - getPaddingRight();// 去掉左右两边的padding

		int heightMode = MeasureSpec.getMode(heightMeasureSpec); // 模式
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);// 高度大小
		int height = heightSize - getPaddingTop() - getPaddingBottom();// 去掉上下两边的padding

		if (widthMode == MeasureSpec.EXACTLY
				&& heightMode != MeasureSpec.EXACTLY) {
			// 修正一下 高度的值 让高度=宽度/比例
			height = (int) (width / ratio + 0.5f); // 保证4舍五入
		} else if (widthMode != MeasureSpec.EXACTLY
				&& heightMode == MeasureSpec.EXACTLY) {
			// 由于高度是精确的值 ,宽度随着高度的变化而变化
			width = (int) ((height * ratio) + 0.5f);
		}
		// 重新制作了新的规则
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.EXACTLY,
				width + getPaddingLeft() + getPaddingRight());
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.EXACTLY,
				height + getPaddingTop() + getPaddingBottom());

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
