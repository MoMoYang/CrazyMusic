package com.imooc.guestmusic.util;

import com.imooc.guestmusic.model.IAlertDailogButtonListener;
import com.imoocmusic.guestmusic.R;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.widget.ImageButton;
import android.widget.TextView;

//声明一个LayoutInflater作为工具类使用
public class Util {
	
	private static AlertDialog mAlertDialog;
	
	public static View getView(Context Context, int layoutId){
		LayoutInflater inflater = (LayoutInflater)Context.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(layoutId, null);
		return layout;
	}
	/**
	 * 页面跳转
	 * @param context
	 * @param desti
	 */
	public static void startActivity(Context context, Class desti){
		Intent intent = new Intent();
		intent.setClass(context, desti);
		context.startActivity(intent);
		
		//关闭当前的Activity
		((Activity)context).finish();
	}
	/**
	 * 显示自定义对话框
	 */
	public static void showDialog(final Context context, 
			String message, 
			final IAlertDailogButtonListener Listener){
		
		View dialogView = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialogView = getView(context, R.layout.dialog_view);
		
		ImageButton btnOkView = (ImageButton)dialogView.findViewById
				(R.id.btn_dialog_ok);
		ImageButton btnCancelView = (ImageButton)dialogView.findViewById
				(R.id.btn_dialog_cancel);
		TextView txtMessage = (TextView)dialogView.findViewById
				(R.id.text_dialog_message);
		
		txtMessage.setText(message);
		
		btnOkView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//关闭对话框
				if(mAlertDialog != null){
					mAlertDialog.cancel();
				}
				//事件回调
				if(Listener != null){
					Listener.onClick();
				}
				//播放音效
				Myplayer.playTone(context, Myplayer.INDEX_STONE_ENTER);

			}
		});
		
		btnCancelView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						//关闭对话框
						if(mAlertDialog != null){
							mAlertDialog.cancel();
						}
						//播放音效
						Myplayer.playTone(context, Myplayer.INDEX_STONE_CANCEL);
					}
				});
		//为Dialog设置view
		builder.setView(dialogView);
		mAlertDialog = builder.create();
		
		//显示对话框
		mAlertDialog.show();
	}
}
