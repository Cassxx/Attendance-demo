package com.example.wuzhihan.finalwork;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TopBarView extends RelativeLayout implements OnClickListener {

	private RelativeLayout mLayout; // parent

	private ImageView mReturnBtn; // 左边返回按钮
	private TextView mTitle; // 中间标题
	private TextView mRightBtn; // 右边的
	private TextView mLeftBtn; // 左边的
	private ImageView mMenu; // 右边图标

	public TopBarView(Context context) {
		this(context, null);

		// TODO Auto-generated constructor stub
	}

	public TopBarView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public TopBarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.topBar, defStyle, 0);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		int layoutResourceId = a.getResourceId(
				R.styleable.topBar_topbar_layout, R.layout.top_bar_layout);

		boolean returnVisiable = a.getBoolean(
				R.styleable.topBar_topbar_return_visiable, true);
		boolean rightBtnVisiable = a.getBoolean(
				R.styleable.topBar_topbar_right_text_visiable, false);
		boolean leftBtnVisiable = a.getBoolean(
				R.styleable.topBar_topbar_left_text_visiable, false);
		boolean menuBtnVisiable = a.getBoolean(
				R.styleable.topBar_topbar_right_image_visiable, false);

		String title = a.getString(R.styleable.topBar_topbar_title);
		String rightText = a.getString(R.styleable.topBar_topbar_right_text);
		String leftText = a.getString(R.styleable.topBar_topbar_left_text);

		// Drawable titleDrawable =
		// a.getDrawable(R.styleable.topBar_topbar_title_src);
		Drawable rightDrawable = a
				.getDrawable(R.styleable.topBar_topbar_right_image_src);

		a.recycle();
		mLayout = (RelativeLayout) inflater.inflate(layoutResourceId, this,
				true);
		mReturnBtn = (ImageView) findViewById(R.id.topbar_back_btn);
		mTitle = (TextView) findViewById(R.id.topbar_title);
		mRightBtn = (TextView) findViewById(R.id.topbar_right);
		mLeftBtn = (TextView) findViewById(R.id.topbar_left);
		mMenu = (ImageView) findViewById(R.id.topbar_menu_btn);

		mReturnBtn.setOnClickListener(this);
		mTitle.setOnClickListener(this);
		mRightBtn.setOnClickListener(this);
		mLeftBtn.setOnClickListener(this);
		mMenu.setOnClickListener(this);

		if (returnVisiable)
			mReturnBtn.setVisibility(View.VISIBLE);
		else {
			mReturnBtn.setVisibility(View.GONE);
		}

		if (rightBtnVisiable) {
			mRightBtn.setVisibility(View.VISIBLE);
			mMenu.setVisibility(View.GONE);
			if (!TextUtils.isEmpty(rightText))
				mRightBtn.setText(rightText);
		}

		if (leftBtnVisiable) {
			mLeftBtn.setVisibility(View.VISIBLE);
			if (!TextUtils.isEmpty(leftText))
				mLeftBtn.setText(leftText);
		}

		if (!TextUtils.isEmpty(title)) {
			mTitle.setText(title);
			// if (titleDrawable != null) {
			// mTitle.setCompoundDrawablesWithIntrinsicBounds(null, null,
			// titleDrawable, null);
			// }
		}

		if (menuBtnVisiable) {
			mRightBtn.setVisibility(View.GONE);
			mMenu.setVisibility(View.VISIBLE);
			if (rightDrawable != null) {
				mMenu.setImageDrawable(rightDrawable);
			}
		}
	}

	/**
	 * 设置左边的返回按钮是否可见
	 * 
	 * @param v
	 */
	public void setReturnBtnVisiable(int v) {
		mReturnBtn.setVisibility(v);
	}

	/**
	 * 修改返回按钮图标
	 */
	public void setReturnBtnIcon(int resId) {
		mReturnBtn.setImageResource(resId);
	}

	/**
	 * 设置中间的标题文本
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		mTitle.setText(title);
	}

	/**
	 * 设置右边的TextView是否可见
	 * 
	 * @param v
	 */
	public void setRightBtnVisiable(int v) {
		mRightBtn.setVisibility(v);
	}

	/**
	 * 设置左边的TextView是否可见
	 * 
	 * @param v
	 */
	public void setLeftBtnVisiable(int v) {
		mLeftBtn.setVisibility(v);
	}

	/**
	 * 
	 * @author jrjin
	 * @time 2014-9-18 下午10:20:49
	 * @param v
	 */
	public void setMenuBtnVisiable(int v) {
		mMenu.setVisibility(v);
	}

	/**
	 * 设置右边的TextView的文本
	 * 
	 * @param text
	 */
	public void setRightText(String text) {
		mRightBtn.setText(text);
	}

	/**
	 * 设置右边的TextView的文本
	 * 
	 * @param text
	 */
	public void setLeftText(String text) {
		mLeftBtn.setText(text);
	}

	/**
	 * 
	 * @author jrjin
	 * @time 2014-9-18 下午10:21:49
	 * @param src
	 */
	public void setMenuSrc(int src) {
		mMenu.setImageResource(src);
	}

	public void setSettingsVisiable(int v) {
		mMenu.setVisibility(v);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mReturnBtn) {
			Context context = getContext();
			if (context instanceof Activity) {
//				HcAppState.getInstance().removeActivity((Activity) context);
				((Activity) context).finish();
			}
		}
	}

	public void setReturnViewListener(OnClickListener listener) {
		mReturnBtn.setOnClickListener(listener);
	}

	public void setTitleViewListener(OnClickListener listener) {
		mTitle.setOnClickListener(listener);
	}

	public void setRightViewListener(OnClickListener listener) {
		mRightBtn.setOnClickListener(listener);
	}

	public void setLeftViewListener(OnClickListener listener) {
		mLeftBtn.setOnClickListener(listener);
	}

	public void setMenuListener(OnClickListener listener) {
		mMenu.setOnClickListener(listener);
	}

	public void setOnClickListener(OnClickListener listener, int id) {
		if (id == R.id.topbar_back_btn) {
			setReturnViewListener(listener);
		} else if (id == R.id.topbar_left) {
			setLeftViewListener(listener);
		} else if (id == R.id.topbar_right) {
			setRightViewListener(listener);
		} else if (id == R.id.topbar_title) {
			setTitleViewListener(listener);
		} else if (id == R.id.topbar_menu_btn) {
			setMenuListener(listener);
		}
		/*
		 * switch (id) { case R.id.topbar_back_btn:
		 * setReturnViewListener(listener); break; case R.id.topbar_left:
		 * setLeftViewListener(listener); break; case R.id.topbar_right:
		 * setRightViewListener(listener); break; case R.id.topbar_title:
		 * setTitleViewListener(listener); break; case R.id.topbar_menu_btn:
		 * setMenuListener(listener); break;
		 * 
		 * default: break; }
		 */
	}
}
