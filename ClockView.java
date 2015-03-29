package com.zsj.view;

import com.example.myclock.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;

@SuppressLint("HandlerLeak")
public class ClockView extends View {
	/**
	 * ����ͼƬ��Դ
	 */
	private Drawable clockDrawable;
	/**
	 * �������ĵ�ͼƬ��Դ
	 */
	private Drawable centerDrawable;
	/**
	 * ʱ��ͼƬ��Դ
	 */
	private Drawable hourDrawable;
	/**
	 * ����ͼƬ��Դ
	 */
	private Drawable minuteDrawable;
	/**
	 * ����ͼƬ��Դ
	 */
	/**
	 * ����
	 */
	private Paint paint;
	private Drawable seconddDrawable;

	/**
	 * �Ƿ����仯
	 */
	private boolean isChange;

	/**
	 * ʱ���࣬������ȡϵͳ��ʱ��
	 */
	private Time time;
	private Thread clockThread;
	public ClockView(Context context) {
		this(context, null);
	}

	public ClockView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * ��ʼ��ͼƬ��Դ�ͻ���
	 */
	public ClockView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MyClockStyleable, defStyle, 0);
		//��ȡ�Զ�������
		clockDrawable = ta.getDrawable(R.styleable.MyClockStyleable_clock);
		centerDrawable = ta.getDrawable(R.styleable.MyClockStyleable_center_clock);
		hourDrawable = ta.getDrawable(R.styleable.MyClockStyleable_hour);
		minuteDrawable = ta.getDrawable(R.styleable.MyClockStyleable_minute);
		seconddDrawable = ta.getDrawable(R.styleable.MyClockStyleable_second);
		//�ͷ���Դ����ע��TypedArray������һ��shared��Դ�����뱻��ʹ�ú���л��ա�
		ta.recycle();

		paint = new Paint();
		paint.setColor(Color.parseColor("#000000"));
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setFakeBoldText(true);
		paint.setAntiAlias(true);
		
		time = new Time();
		clockThread = new Thread() {
			public void run() {
				while(isChange){
					postInvalidate();
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		};
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//���õ�ǰ��ʱ��
		time.setToNow();
		//�����Զ���view ������λ��
		int viewCenterX = (getRight() - getLeft()) / 2;
		int viewCenterY = (getBottom() - getTop()) / 2;
		final Drawable dial = clockDrawable;
		//��ȡclockDrawable �ĸ߶ȺͿ��(��ͼƬ�ĸ߶ȺͿ��)
		int h = dial.getIntrinsicHeight();
		int w = dial.getIntrinsicWidth();
		if ((getRight() - getLeft()) < w || (getBottom() - getTop()) < h) {
			float scale = Math.min((float) (getRight() - getLeft()) / w,
					(float) (getBottom() - getTop()) / h);
			canvas.save();
			canvas.scale(scale, scale, viewCenterX, viewCenterY);
		}
		if (isChange) {
			//����Ҫ
			dial.setBounds(viewCenterX - (w / 2), viewCenterY
					- (h / 2), viewCenterX + (w / 2),
					viewCenterY + (h / 2));
		}
		dial.draw(canvas);
		canvas.save();
		
		if (isChange) {
			paint.setTextSize(40f);
			int textWidth = (int) paint.measureText("12");
			canvas.drawText("12", viewCenterX - (textWidth / 2), (float) (viewCenterY - (h / 3.5)), paint);
			int textWidth1 = (int) paint.measureText("6");
			canvas.drawText("6", viewCenterX - (textWidth1 / 2), (float) (viewCenterY + (h / 2.7)), paint);
			canvas.drawText("9", (float) (viewCenterX - (w / 2.7)), viewCenterY + 10, paint);
			canvas.drawText("3", (float) (viewCenterX + (w / 3)), viewCenterY + 10, paint);
		}
		canvas.save();
		//��canvas ��ʱ��
		canvas.rotate(time.hour / 12.0f * 360.0f, viewCenterX, viewCenterY);
		Drawable mHour = hourDrawable;
		h = mHour.getIntrinsicHeight();
		w= mHour.getIntrinsicWidth();
		if (isChange) {
			mHour.setBounds(viewCenterX - (w / 2), viewCenterY
					- h + 20, viewCenterX + (w / 2),
					viewCenterY + 10);
		}
		mHour.draw(canvas);
		canvas.restore();
		canvas.save();
		//��canvas ������
		canvas.rotate(time.minute / 60.0f * 360.0f, viewCenterX, viewCenterY);
		Drawable mMinute = minuteDrawable;
		if (isChange) {
			w = mMinute.getIntrinsicWidth();
			h = mMinute.getIntrinsicHeight();
			mMinute.setBounds(viewCenterX - (w / 2), viewCenterY - h, viewCenterX + (w / 2), viewCenterY + 10);
		}
		mMinute.draw(canvas);
		canvas.restore();
		canvas.save();
		//��canvas �����ĵ�
		Drawable mCenter = centerDrawable;
		if (isChange) {
			w = mCenter.getIntrinsicWidth();
			h = mCenter.getIntrinsicHeight();
			mCenter.setBounds(viewCenterX - (w / 2), viewCenterY
					- (h / 2), viewCenterX + (w / 2),
					viewCenterY + (h / 2));
		}
		mCenter.draw(canvas);
		canvas.save();
		//�� canvas ������
		canvas.rotate(time.second / 60.0f * 360.0f, viewCenterX, viewCenterY);
		Drawable mSecond = seconddDrawable;
		if (isChange) {
			w = mSecond.getIntrinsicWidth();
			h = mSecond.getIntrinsicHeight();
			mSecond.setBounds(viewCenterX - (w / 2), viewCenterY
					- h + 50, viewCenterX + (w / 2),
					viewCenterY + 50);
		}
		mSecond.draw(canvas);
		canvas.restore();
		canvas.save();
		//Log.e("TAG", "this is onDraw method");
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		isChange = true;
		clockThread.start();
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		isChange = false;
	}
}
