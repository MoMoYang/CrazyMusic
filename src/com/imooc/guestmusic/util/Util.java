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

//����һ��LayoutInflater��Ϊ������ʹ��
public class Util {
	
	private static AlertDialog mAlertDialog;
	
	public static View getView(Context Context, int layoutId){
		LayoutInflater inflater = (LayoutInflater)Context.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(layoutId, null);
		return layout;
	}
	/**
	 * ҳ����ת
	 * @param context
	 * @param desti
	 */
	public static void startActivity(Context context, Class desti){
		Intent intent = new Intent();
		intent.setClass(context, desti);
		context.startActivity(intent);
		
		//�رյ�ǰ��Activity
		((Activity)context).finish();
	}
	/**
	 * ��ʾ�Զ���Ի���
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
				//�رնԻ���
				if(mAlertDialog != null){
					mAlertDialog.cancel();
				}
				//�¼��ص�
				if(Listener != null){
					Listener.onClick();
				}
				//������Ч
				Myplayer.playTone(context, Myplayer.INDEX_STONE_ENTER);

			}
		});
		
		btnCancelView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						//�رնԻ���
						if(mAlertDialog != null){
							mAlertDialog.cancel();
						}
						//������Ч
						Myplayer.playTone(context, Myplayer.INDEX_STONE_CANCEL);
					}
				});
		//ΪDialog����view
		builder.setView(dialogView);
		mAlertDialog = builder.create();
		
		//��ʾ�Ի���
		mAlertDialog.show();
	}
}
