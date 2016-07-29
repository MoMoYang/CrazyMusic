package com.imooc.guestmusic.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imooc.guestmusic.data.Const;
import com.imooc.guestmusic.model.IAlertDailogButtonListener;
import com.imooc.guestmusic.model.IWordButtonClickListener;
import com.imooc.guestmusic.model.Song;
import com.imooc.guestmusic.model.WordButton;
import com.imooc.guestmusic.myui.MyGridView;
import com.imooc.guestmusic.util.MyLog;
import com.imooc.guestmusic.util.Myplayer;
import com.imooc.guestmusic.util.Util;
import com.imoocmusic.guestmusic.R;

public class MainActivity extends Activity implements IWordButtonClickListener{
	
	public final static String TAG  = "MainActivity";
	//��״̬.��ȷ�����󡢲�����
	public final static int STATUS_ANSWER_RIGHT = 1;
	public final static int STATUS_ANSWER_WRONG = 2;
	public final static int STATUS_ANSWER_LACK = 3;
	
	//��ʼ�����ؽ���
	private View mPassView;
	
	//��ʼ��Բ�̶���
	private Animation mPanAnim;
	private LinearInterpolator mPanLin;
	//��ʼ�����˿�ʼ����
	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;
	//��ʼ�����˽�������
	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;
	
	//��ʼ�����Ű�ť
	private ImageButton mBtnPlayStart;
	//��ʼ��Բ�̺Ͳ���
	private ImageView mViewPan;
	private ImageView mViewPanBar;
	//�жϵ�ǰԲ���Ƿ�������
	private boolean mIsRunning = false;
	//���ֿ�����
	private ArrayList<WordButton> mAllWords;
	private MyGridView mMyGridView;
	private ArrayList<WordButton> mBtnSelectWords;
	//��ѡ�����ֿ�UI����
	private LinearLayout mViewWordsContainer;
	//��ǰ�ص�����
	private TextView mCurrentStagePassView;
	private TextView mCurrentStageView;
	//��ǰ��������
	private TextView mCurrentSongNamePassView;
	
	//��ǰ�ĸ���
	private Song mCurrentSong; 
	//��ǰ�ص�����
	private int mCurrentStageIndex = -1;
	//��ʼ����ǰ�������
	private int mCurrentCoins = Const.TOTAL_COINS;
	//���View
	private TextView mViewCurrentCoins;
	private final static int ID_DIALOG_DELETE_WORD = 1;
	private final static int ID_DIALOG_TIP_ANSWER = 2;
	private final static int ID_DIALOG_LACK_COINS = 3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//ʵ����Բ�̺Ͳ���
		mViewPan = (ImageView) findViewById(R.id.imageView1);
		mViewPanBar = (ImageView) findViewById(R.id.imageView2);
		
		mMyGridView = (MyGridView) findViewById(R.id.gridview);
		mViewCurrentCoins = (TextView) findViewById(R.id.txt_bar_coins);
		mViewCurrentCoins.setText(mCurrentCoins + "");
		//ע�������
		mMyGridView.registOnWordButtonClick(this);
		mViewWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);
		//ΪԲ����Ӷ���
		mPanAnim = AnimationUtils.loadAnimation(this,R.anim.rotate);
		mPanLin = new LinearInterpolator();
		mPanAnim.setInterpolator(mPanLin);
		//Բ��ת������
		mPanAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
								
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
								
			}
			//Բ��ת������������ת����ȥ����
			@Override
			public void onAnimationEnd(Animation animation) {
				mViewPanBar.startAnimation(mBarOutAnim);				
			}
		});
		//Ϊ���˿�ʼ��Ӷ���
		mBarInAnim = AnimationUtils.loadAnimation(this,R.anim.rotate_45);
		mBarInLin = new LinearInterpolator();
		mBarInAnim.setFillAfter(true);
		mBarInAnim.setInterpolator(mBarInLin);
		//���˲�������
		mBarInAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
								
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
								
			}
			//���˲���������Բ��ת��
			@Override
			public void onAnimationEnd(Animation animation) {
				mViewPan.startAnimation(mPanAnim);				
			}
		});
		//Ϊ���˽�����Ӷ���
		mBarOutAnim = AnimationUtils.loadAnimation(this,R.anim.rotate_d_45);
		mBarOutLin = new LinearInterpolator();
		mBarOutAnim.setFillAfter(true);
		mBarOutAnim.setInterpolator(mBarOutLin);
		mBarOutAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				mIsRunning = false;
				mBtnPlayStart.setVisibility(View.VISIBLE);
			}
		});
		//ʵ�������Ű�ť
		mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
		//������Ű�ť�����˲���
		mBtnPlayStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {	
				handlePlayButton();
			}
		});
		//��ʼ����Ϸ����
		initCurrentStageData();
		//����ɾ����ť�¼�
		handleDeleteWord();
		//������ʾ��ť�¼�
		handleTipAnswer();

	}
		/**
		 * ����Բ���м�Ĳ��Ű�ť
		 */
		private void handlePlayButton(){
			if(mViewPanBar!=null){
				if(!mIsRunning){
					mIsRunning = true;
					mViewPanBar.startAnimation(mBarInAnim);
					mBtnPlayStart.setVisibility(View.INVISIBLE);
					
					//��������
					Myplayer.playSong(MainActivity.this, mCurrentSong.getSongFileNmae());
					
				}
			}
			
		}
		@Override
		public void onPause(){
			mViewPan.clearAnimation();
			//��ͣ����
			Myplayer.stopTheSong(MainActivity.this);
			super.onPause();
		}
		
		private Song loadStageSongInfo(int stageIndex){
			Song song = new Song();
			String[] stage = Const.SONG_INFO[stageIndex];
			song.setSongFileNmae(stage[Const.INDEX_FILE_NAME]);
			song.setSongName(stage[Const.INDEX_SONG_NAME]);
			return song;
			
		}
		
		@Override
		public void onWordButtonClick(WordButton wordButton) {
			setSelectWord(wordButton);		
			//��ô�״̬
			int checkResult = checkTheAnswer();
			//����
			if(checkResult == STATUS_ANSWER_RIGHT){
				//���ز�����
				for(int i = 0; i < mBtnSelectWords.size(); i++){
					mBtnSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);
				}
				handlePaseEvent();
			}else if(checkResult == STATUS_ANSWER_WRONG){
				//���д�����ʾ����˸��ʾ
				sparkTheWords();
			}else if(checkResult == STATUS_ANSWER_LACK){
				//������״̬������������ɫΪ����ɫ
				for(int i = 0; i < mBtnSelectWords.size(); i++){
					mBtnSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);
				}
			}
		}
		//���ô�
		private void setSelectWord(WordButton wordButton){
			for(int i=0;i < mBtnSelectWords.size();i++){
				if(mBtnSelectWords.get(i).mWordString.length() == 0){
					//�������ֿ����ݼ��ɼ���
					mBtnSelectWords.get(i).mViewButton.setText(wordButton.mWordString);
					mBtnSelectWords.get(i).mIsVisiable = true;
					mBtnSelectWords.get(i).mWordString = wordButton.mWordString;
					//��������
					mBtnSelectWords.get(i).mIndex = wordButton.mIndex;
					
					MyLog.d(TAG,mBtnSelectWords.get(i).mIndex + "");
					//���ô�ѡ���ֿ�ɼ���
					setButtonVisiable(wordButton,View.INVISIBLE);
					break;
				}
			}
		}
		//������ؽ��漰�¼�
		private void handlePaseEvent(){
			//��ʾ���ؽ���
			mPassView = (LinearLayout)this.findViewById(R.id.pass_view);
			mPassView.setVisibility(View.VISIBLE);
			//ֹͣδ��ɶ���
			mViewPan.clearAnimation();
			//ֹͣ���ڲ��ŵ�����
			Myplayer.stopTheSong(MainActivity.this);
			//��Ч����
			Myplayer.playTone(MainActivity.this, Myplayer.INDEX_STONE_COIN);
			//��ǰ�ص�����
			mCurrentStagePassView = (TextView) findViewById(R.id.text_current_stage_pass);
			if(mCurrentStagePassView != null){
				mCurrentStagePassView.setText((mCurrentStageIndex + 1) + ""); 	
			}
			//��ʾ��������
			mCurrentSongNamePassView = (TextView) findViewById(R.id.text_current_song_name_pass);
			if(mCurrentSongNamePassView != null){
				mCurrentSongNamePassView.setText(mCurrentSong.getSongName()); 	
			}
			//��һ�ذ�������
			ImageButton btnPass = (ImageButton) findViewById(R.id.btn_next);
			btnPass.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if(judegAppPassed()){
						//ȫ��ͨ�أ����뵽ͨ�ؽ���
						Util.startActivity(MainActivity.this,AllPassView.class);
					}else{
						//��ӽ������
						mViewCurrentCoins.setText((mCurrentCoins + 100) + "");
						mCurrentCoins = mCurrentCoins + 100;
						//������һ��
						mPassView.setVisibility(View.INVISIBLE);

						//���عؿ�����
						initCurrentStageData();
					}
				}
			});
			
		}
		/**
		 * �ж��Ƿ�ͨ��
		 * @return
		 */
		private boolean judegAppPassed(){
			return (mCurrentStageIndex == Const.SONG_INFO.length -1);
		}
		//�����
		private void clearTheAnswer(WordButton wordButton){
			wordButton.mViewButton.setText("");
			wordButton.mWordString = "";
			wordButton.mIsVisiable = false;
			//���ô�ѡ��ɼ���
			setButtonVisiable(mAllWords.get(wordButton.mIndex),View.VISIBLE);
		}
		private void setButtonVisiable(WordButton button,int visibility){
			button.mViewButton.setVisibility(visibility);
			button.mIsVisiable = (visibility == View.VISIBLE) ? true :false;
			
			MyLog.d(TAG,button.mIsVisiable + "");
		}
		/**
		 * ���ص�ǰ�ص�����
		 */
		@SuppressLint("NewApi")
		private void initCurrentStageData(){
			//��ȡ��ǰ�صĸ�����Ϣ
			mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);
			//��ʼ����ѡ���
			mBtnSelectWords = initWordSelect();
			LayoutParams params = new LayoutParams(120,120);
			//���ԭ���Ĵ�
			mViewWordsContainer.removeAllViews();
			//�����µĴ𰸿�
			for(int i=0;i < mBtnSelectWords.size(); i++){
				mViewWordsContainer.addView(mBtnSelectWords.get(i).mViewButton, params);
			}
			//��ǰ�ص�����
			mCurrentStageView = (TextView) findViewById(R.id.text_current_stage);
			if(mCurrentStageView != null){
				mCurrentStageView.setText((mCurrentStageIndex + 1) + "");
			}
			//�������
			mAllWords = initAllWord();
			//��������
			mMyGridView.updateData(mAllWords);
			
			//��ʼ������ʾ
			handlePlayButton();
		}
		//��ʼ����ѡ���ֿ�
		private ArrayList<WordButton> initAllWord(){
			ArrayList<WordButton> data = new ArrayList<WordButton>();
			//������д�ѡ����
			String[] words = generateWords();
			for(int i=0; i < MyGridView.COUNTS_WORDS; i++){
				WordButton button = new WordButton();
				button.mWordString = words[i];
				data.add(button);
			}
			return data;
		}
		
		//��ʼ����ѡ�����ֿ�
		private ArrayList<WordButton> initWordSelect(){
			ArrayList<WordButton> data = new ArrayList<WordButton>();
			for(int i=0; i < mCurrentSong.getNameLength(); i++){
				View view = Util.getView(MainActivity.this, R.layout.self_ui_gridview_item);
				
				final WordButton holder = new WordButton();
				holder.mViewButton = (Button) view.findViewById(R.id.item_btn);
				holder.mViewButton.setTextColor(Color.WHITE);
				holder.mViewButton.setText("");
				holder.mIsVisiable = false;
				holder.mViewButton.setBackgroundResource(R.drawable.game_wordblank);
				holder.mViewButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						clearTheAnswer(holder);
					}
				});
				data.add(holder);
			}
			return data;
		}
		//�����������
		private char getRandomChar(){
			String str = "";
			int hightPos;
			int lowPos;
			Random random = new Random();
			hightPos = (176 + Math.abs(random.nextInt(39)));
			lowPos = (161 + Math.abs(random.nextInt(93)));
			byte[] b = new byte[2];
			b[0] = (Integer.valueOf(hightPos)).byteValue();
			b[1] = (Integer.valueOf(lowPos)).byteValue();
			
			try {
				str = new String(b, "GBK");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return str.charAt(0);
		}
		//����24�����𰸺���
		private String[] generateWords(){
			Random random = new Random();
			String[] words = new String[MyGridView.COUNTS_WORDS];
			//�������
			for(int i=0;i < mCurrentSong.getNameLength();i++){
				words[i] = mCurrentSong.getNameCharacters()[i] + "";
			}
			//����������ɵ�����
			for(int i = mCurrentSong.getNameLength();i < MyGridView.COUNTS_WORDS;i++){
				words[i] = getRandomChar() + "";
			}
			//��������˳��
			//���ȴ�����Ԫ����ѡȡһ��Ԫ�����һ��Ԫ�ؽ��н�����
			//Ȼ���ڵڶ�������֮��ѡȡһ��Ԫ����ڶ�����ȥ���н�����
			//���������ѭ���ģ�ֱ��ѭ�������һ��Ԫ��Ϊֹ
			
			for(int i = MyGridView.COUNTS_WORDS - 1; i >= 0; i--){
				int index = random.nextInt(i+ 1);
				
				String buf = words[index];
				words[index] = words[i];
				words[i] = buf;
			}
			return words;
		}
		//����
		private int checkTheAnswer(){
			//�ȼ�鳤��
			for(int i = 0; i < mBtnSelectWords.size(); i++){
				//����пգ�˵���𰸲�����
				if(mBtnSelectWords.get(i).mWordString.length() == 0){
					return STATUS_ANSWER_LACK;
				}
			}
			//������ȷ���
			StringBuffer sb =  new StringBuffer();
			for(int i = 0;i < mBtnSelectWords.size();i++){
				sb.append(mBtnSelectWords.get(i).mWordString);
			}
			return (sb.toString().equals(mCurrentSong.getSongName())) ?
					STATUS_ANSWER_RIGHT : STATUS_ANSWER_WRONG;
		}
		//�𰸴���ִ�з�������˸����
		private void sparkTheWords(){
			//���������
			TimerTask task = new TimerTask() {
				boolean  mChange = false;
				int mSpardTimes = 0;
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						public void run() {
							//��ʾ��˸����
							if(++mSpardTimes > 6){
								return;
							}
							//ִ����˸�߼���������ʾ��ɫ�Ͱ�ɫ����							
							for(int i = 0; i < mBtnSelectWords.size(); i++){
								mBtnSelectWords.get(i).mViewButton.setTextColor
								(mChange ? Color.RED : Color.WHITE);
							}
							mChange = !mChange;
						}
					});
				}
			};
			Timer timer = new Timer();
			timer.schedule(task, 1, 150);
		}
		/**
		 * �Զ�ѡ��һ����
		 */
		private void tipAnswer(){
			//���ٽ������
			if(!handleCoins(-getTipCoins())){
				//���������������ʾ�Ի���
				showConfigDialog(ID_DIALOG_LACK_COINS);
				return;
			}
			boolean tipWord = false;
			for(int i = 0; i < mBtnSelectWords.size(); i++){
				if(mBtnSelectWords.get(i).mWordString.length() == 0){
					//���ݵ�ǰ�𰸹�����ѡ���Ӧ�����ֲ�����
					onWordButtonClick(findIsAnswerWord(i));
					tipWord = true;
					break;
				}
			}
			//û���ҵ��������Ĵ�
			if(!tipWord){
				//��˸������ʾ�û�
				sparkTheWords();
			}
			
		}
		/**
		 * �ҵ�һ��������
		 * @param index ��ǰ��Ҫ����𰸿������
		 * @return
		 */
		private WordButton findIsAnswerWord(int index){
			WordButton buf = null;
			for(int i = 0; i < MyGridView.COUNTS_WORDS; i++){
				buf = mAllWords.get(i);
				if(buf.mWordString.equals("" + mCurrentSong.getNameCharacters()[index])){
					return buf;
				}
			}
			return null;
		}
		/**
		 * ɾ������
		 */
		private void deleteOneWord(){
			//���ٽ��
			if(!handleCoins(-getDeleteWordCoins())){
				//��Ҳ�������ʾ��ʾ�Ի���
				showConfigDialog(ID_DIALOG_LACK_COINS);
				return;
			}
			//�����������Ӧ��WordButton����Ϊ���ɼ�
			setButtonVisiable(findNotAnswerWord(), View.INVISIBLE);
		}
		/**
		 * �ҵ�һ�����Ǵ𰸵����֣����ҵ�ǰ�ǿɼ���
		 * @return
		 */
		private WordButton findNotAnswerWord(){
			Random random = new Random();
			WordButton buf = null;
			while(true){
				int index = random.nextInt(MyGridView.COUNTS_WORDS);
				buf = mAllWords.get(index);
				if(buf.mIsVisiable && !isTheAnswerWord(buf)){
					return buf;
				}
			}
		}
		/**
		 * �ж�ĳ�������Ƿ�Ϊ��
		 * @param wordButton
		 * @return
		 */
		private boolean isTheAnswerWord(WordButton wordButton){
			boolean result = false;
			for(int i = 0; i < mCurrentSong.getNameLength();i++){
				if(wordButton.mWordString.equals
						("" + mCurrentSong.getNameCharacters()[i])){
					result = true;
					break;
				}
			}
			return  result;
		}
		
		/**
		 * ���ӻ��߼���ָ�������Ľ��
		 * @param data
		 * @return true ���ӻ��߼��ٳɹ���false ʧ��
		 */
		private boolean handleCoins(int data){
			//�жϵ�ǰ�ܵĽ�������ɷ����
			if(mCurrentCoins + data >= 0){
				mCurrentCoins += data;
				
				mViewCurrentCoins.setText(mCurrentCoins + "");
				return true;
			}else{
				//��Ҳ���
				return false;
			}
			
		}
		/**
		 * �������ļ����ȡɾ��������Ҫ�õĽ��
		 * @return
		 */
		private int getDeleteWordCoins(){
			return this.getResources().getInteger(R.integer.pay_delete_word);
		}
		/**
		 * �������ļ�����ʾ��������Ҫ�Ľ��
		 * @return
		 */
		private int getTipCoins(){
			return this.getResources().getInteger(R.integer.pay_tip_answer);
		}
		/**
		 * ����ɾ����ѡ�����¼�
		 */
		private void handleDeleteWord(){
			 ImageButton button = (ImageButton) findViewById(R.id.btn_delete_word);
			 button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					showConfigDialog(ID_DIALOG_DELETE_WORD);					
				}
			});
		}
		/**
		 * ������ʾ��ť�¼�
		 */
		private void handleTipAnswer(){
			 ImageButton button = (ImageButton) findViewById(R.id.btn_tip_answer);
			 button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					showConfigDialog(ID_DIALOG_TIP_ANSWER);					
				}
			});
		} 
		//�Զ���AlertDialog�¼���Ӧ
		//ɾ�������
		private IAlertDailogButtonListener mBtnOkDeleteWordListener = 
				new IAlertDailogButtonListener() {
			@Override
			public void onClick() {
				//ִ���¼�
				deleteOneWord();
				
			}
		};
		//����ʾ
		private IAlertDailogButtonListener mBtnOkTipAnswerListener = 
				new IAlertDailogButtonListener() {
			@Override
			public void onClick() {
				//ִ���¼�
				tipAnswer();
				
			}
		};
		//��Ҳ���
		private IAlertDailogButtonListener mBtnOkLackCoinsListener = 
				new IAlertDailogButtonListener() {
			@Override
			public void onClick() {
				//ִ���¼�
				
			}
		};
		/**
		 * ��ʾ�Ի���
		 * @param id
		 */
		private void showConfigDialog(int id){
			switch (id) {
			case ID_DIALOG_DELETE_WORD:
				Util.showDialog(MainActivity.this, 
						"ȷ�ϻ���" + getDeleteWordCoins() + "�����ȥ��һ�������?",
						mBtnOkDeleteWordListener);
				break;
			case ID_DIALOG_TIP_ANSWER:
				Util.showDialog(MainActivity.this, 
						"ȷ�ϻ���" + getTipCoins() + "����һ��һ��������ʾ?",
						mBtnOkTipAnswerListener);
				break;
			case ID_DIALOG_LACK_COINS:
				Util.showDialog(MainActivity.this, 
						"��Ҳ��㣬ȥ�̵겹��?",
						mBtnOkLackCoinsListener);
				break;

			}
		}
}
