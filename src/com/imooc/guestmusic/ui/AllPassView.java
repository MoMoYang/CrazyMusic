package com.imooc.guestmusic.ui;

import com.imoocmusic.guestmusic.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
/**
 * 通关界面处理
 * @author Guoguo
 *
 */
public class AllPassView extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.allpass_view);
		
		//隐藏右上角金币按钮
		FrameLayout view = (FrameLayout)findViewById(R.id.layout_bar_coin);
		view.setVisibility(View.INVISIBLE);
	}
}
