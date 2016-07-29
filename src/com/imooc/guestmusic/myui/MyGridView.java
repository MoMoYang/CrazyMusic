package com.imooc.guestmusic.myui;

import java.util.ArrayList;

import com.imooc.guestmusic.model.IWordButtonClickListener;
import com.imooc.guestmusic.model.WordButton;
import com.imooc.guestmusic.util.Util;
import com.imoocmusic.guestmusic.R;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

public class MyGridView extends GridView{
	public final static int COUNTS_WORDS = 24;
	//创建容器WordButton存数据
	private ArrayList<WordButton> mArrayList = new ArrayList<WordButton>();
	//声明一个MyGridAdapter并初始化
	private MyGridAdapter mAdapter = new MyGridAdapter();
	
	private Context mContext;
	//定义文字框加载动画
	private Animation mScaleAnimation;
	
	private IWordButtonClickListener mWordButtonListener;
	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;		
		this.setAdapter(mAdapter);	
		
	}
	
	public void updateData(ArrayList<WordButton> list){
		mArrayList = list;
		//重新设置数据源
		setAdapter(mAdapter);
		
	}
	class MyGridAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mArrayList.size();
		}

		@Override
		public Object getItem(int position) {
			return mArrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup p) {
			final WordButton holder;
			
			if(v==null){
				v = Util.getView(mContext, R.layout.self_ui_gridview_item);
				//从容器当中获取一个holder对象
				holder = mArrayList.get(position);
				//加载动画
				mScaleAnimation = AnimationUtils.loadAnimation(mContext, R.anim.scale);
				mScaleAnimation.setStartOffset(position * 100);
				//取出holder对象后赋值
				holder.mIndex = position;
				holder.mViewButton = (Button) v.findViewById(R.id.item_btn);
				holder.mViewButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						mWordButtonListener.onWordButtonClick(holder);						
					}
				});				
				v.setTag(holder);
			}else{
				holder = (WordButton) v.getTag();
			}
			//为mViewButton赋内容为mWordString
			holder.mViewButton.setText(holder.mWordString);
			
			//播放动画
			v.startAnimation(mScaleAnimation);
			return v;
		}
	}
	//注册监听借口
	public void registOnWordButtonClick(IWordButtonClickListener Listener){
		mWordButtonListener = Listener;
	}
}
