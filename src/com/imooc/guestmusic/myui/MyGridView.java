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
	//��������WordButton������
	private ArrayList<WordButton> mArrayList = new ArrayList<WordButton>();
	//����һ��MyGridAdapter����ʼ��
	private MyGridAdapter mAdapter = new MyGridAdapter();
	
	private Context mContext;
	//�������ֿ���ض���
	private Animation mScaleAnimation;
	
	private IWordButtonClickListener mWordButtonListener;
	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;		
		this.setAdapter(mAdapter);	
		
	}
	
	public void updateData(ArrayList<WordButton> list){
		mArrayList = list;
		//������������Դ
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
				//���������л�ȡһ��holder����
				holder = mArrayList.get(position);
				//���ض���
				mScaleAnimation = AnimationUtils.loadAnimation(mContext, R.anim.scale);
				mScaleAnimation.setStartOffset(position * 100);
				//ȡ��holder�����ֵ
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
			//ΪmViewButton������ΪmWordString
			holder.mViewButton.setText(holder.mWordString);
			
			//���Ŷ���
			v.startAnimation(mScaleAnimation);
			return v;
		}
	}
	//ע��������
	public void registOnWordButtonClick(IWordButtonClickListener Listener){
		mWordButtonListener = Listener;
	}
}
