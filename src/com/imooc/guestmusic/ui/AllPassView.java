package com.imooc.guestmusic.ui;

import com.imoocmusic.guestmusic.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
/**
 * ͨ�ؽ��洦��
 * @author Guoguo
 *
 */
public class AllPassView extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.allpass_view);
		
		//�������Ͻǽ�Ұ�ť
		FrameLayout view = (FrameLayout)findViewById(R.id.layout_bar_coin);
		view.setVisibility(View.INVISIBLE);
	}
}
