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
	//答案状态.正确、错误、不完整
	public final static int STATUS_ANSWER_RIGHT = 1;
	public final static int STATUS_ANSWER_WRONG = 2;
	public final static int STATUS_ANSWER_LACK = 3;
	
	//初始化过关界面
	private View mPassView;
	
	//初始化圆盘动画
	private Animation mPanAnim;
	private LinearInterpolator mPanLin;
	//初始化拨杆开始动画
	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;
	//初始化拨杆结束动画
	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;
	
	//初始化播放按钮
	private ImageButton mBtnPlayStart;
	//初始化圆盘和拨杆
	private ImageView mViewPan;
	private ImageView mViewPanBar;
	//判断当前圆盘是否在运行
	private boolean mIsRunning = false;
	//文字框容器
	private ArrayList<WordButton> mAllWords;
	private MyGridView mMyGridView;
	private ArrayList<WordButton> mBtnSelectWords;
	//已选择文字框UI容器
	private LinearLayout mViewWordsContainer;
	//当前关的索引
	private TextView mCurrentStagePassView;
	private TextView mCurrentStageView;
	//当前歌曲名称
	private TextView mCurrentSongNamePassView;
	
	//当前的歌曲
	private Song mCurrentSong; 
	//当前关的索引
	private int mCurrentStageIndex = -1;
	//初始化当前金币数量
	private int mCurrentCoins = Const.TOTAL_COINS;
	//金币View
	private TextView mViewCurrentCoins;
	private final static int ID_DIALOG_DELETE_WORD = 1;
	private final static int ID_DIALOG_TIP_ANSWER = 2;
	private final static int ID_DIALOG_LACK_COINS = 3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//实例化圆盘和拨杆
		mViewPan = (ImageView) findViewById(R.id.imageView1);
		mViewPanBar = (ImageView) findViewById(R.id.imageView2);
		
		mMyGridView = (MyGridView) findViewById(R.id.gridview);
		mViewCurrentCoins = (TextView) findViewById(R.id.txt_bar_coins);
		mViewCurrentCoins.setText(mCurrentCoins + "");
		//注册监听器
		mMyGridView.registOnWordButtonClick(this);
		mViewWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);
		//为圆盘添加动画
		mPanAnim = AnimationUtils.loadAnimation(this,R.anim.rotate);
		mPanLin = new LinearInterpolator();
		mPanAnim.setInterpolator(mPanLin);
		//圆盘转动过程
		mPanAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
								
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
								
			}
			//圆盘转动结束，拨杆转动出去动作
			@Override
			public void onAnimationEnd(Animation animation) {
				mViewPanBar.startAnimation(mBarOutAnim);				
			}
		});
		//为拨杆开始添加动画
		mBarInAnim = AnimationUtils.loadAnimation(this,R.anim.rotate_45);
		mBarInLin = new LinearInterpolator();
		mBarInAnim.setFillAfter(true);
		mBarInAnim.setInterpolator(mBarInLin);
		//拨杆拨动过程
		mBarInAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
								
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
								
			}
			//拨杆拨动结束，圆盘转动
			@Override
			public void onAnimationEnd(Animation animation) {
				mViewPan.startAnimation(mPanAnim);				
			}
		});
		//为拨杆结束添加动画
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
		//实例化播放按钮
		mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
		//点击播放按钮，拨杆拨动
		mBtnPlayStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {	
				handlePlayButton();
			}
		});
		//初始化游戏数据
		initCurrentStageData();
		//处理删除按钮事件
		handleDeleteWord();
		//处理提示按钮事件
		handleTipAnswer();

	}
		/**
		 * 处理圆盘中间的播放按钮
		 */
		private void handlePlayButton(){
			if(mViewPanBar!=null){
				if(!mIsRunning){
					mIsRunning = true;
					mViewPanBar.startAnimation(mBarInAnim);
					mBtnPlayStart.setVisibility(View.INVISIBLE);
					
					//播放音乐
					Myplayer.playSong(MainActivity.this, mCurrentSong.getSongFileNmae());
					
				}
			}
			
		}
		@Override
		public void onPause(){
			mViewPan.clearAnimation();
			//暂停音乐
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
			//获得答案状态
			int checkResult = checkTheAnswer();
			//检查答案
			if(checkResult == STATUS_ANSWER_RIGHT){
				//过关并奖励
				for(int i = 0; i < mBtnSelectWords.size(); i++){
					mBtnSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);
				}
				handlePaseEvent();
			}else if(checkResult == STATUS_ANSWER_WRONG){
				//进行错误提示，闪烁提示
				sparkTheWords();
			}else if(checkResult == STATUS_ANSWER_LACK){
				//不完整状态下设置文字颜色为白颜色
				for(int i = 0; i < mBtnSelectWords.size(); i++){
					mBtnSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);
				}
			}
		}
		//设置答案
		private void setSelectWord(WordButton wordButton){
			for(int i=0;i < mBtnSelectWords.size();i++){
				if(mBtnSelectWords.get(i).mWordString.length() == 0){
					//设置文字框内容及可见性
					mBtnSelectWords.get(i).mViewButton.setText(wordButton.mWordString);
					mBtnSelectWords.get(i).mIsVisiable = true;
					mBtnSelectWords.get(i).mWordString = wordButton.mWordString;
					//设置索引
					mBtnSelectWords.get(i).mIndex = wordButton.mIndex;
					
					MyLog.d(TAG,mBtnSelectWords.get(i).mIndex + "");
					//设置待选文字框可见性
					setButtonVisiable(wordButton,View.INVISIBLE);
					break;
				}
			}
		}
		//处理过关界面及事件
		private void handlePaseEvent(){
			//显示过关界面
			mPassView = (LinearLayout)this.findViewById(R.id.pass_view);
			mPassView.setVisibility(View.VISIBLE);
			//停止未完成动画
			mViewPan.clearAnimation();
			//停止正在播放的音乐
			Myplayer.stopTheSong(MainActivity.this);
			//音效播放
			Myplayer.playTone(MainActivity.this, Myplayer.INDEX_STONE_COIN);
			//当前关的索引
			mCurrentStagePassView = (TextView) findViewById(R.id.text_current_stage_pass);
			if(mCurrentStagePassView != null){
				mCurrentStagePassView.setText((mCurrentStageIndex + 1) + ""); 	
			}
			//显示歌曲名称
			mCurrentSongNamePassView = (TextView) findViewById(R.id.text_current_song_name_pass);
			if(mCurrentSongNamePassView != null){
				mCurrentSongNamePassView.setText(mCurrentSong.getSongName()); 	
			}
			//下一关按键处理
			ImageButton btnPass = (ImageButton) findViewById(R.id.btn_next);
			btnPass.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if(judegAppPassed()){
						//全部通关，进入到通关界面
						Util.startActivity(MainActivity.this,AllPassView.class);
					}else{
						//添加金币数量
						mViewCurrentCoins.setText((mCurrentCoins + 100) + "");
						mCurrentCoins = mCurrentCoins + 100;
						//进入下一关
						mPassView.setVisibility(View.INVISIBLE);

						//加载关卡数据
						initCurrentStageData();
					}
				}
			});
			
		}
		/**
		 * 判断是否通关
		 * @return
		 */
		private boolean judegAppPassed(){
			return (mCurrentStageIndex == Const.SONG_INFO.length -1);
		}
		//清除答案
		private void clearTheAnswer(WordButton wordButton){
			wordButton.mViewButton.setText("");
			wordButton.mWordString = "";
			wordButton.mIsVisiable = false;
			//设置待选框可见性
			setButtonVisiable(mAllWords.get(wordButton.mIndex),View.VISIBLE);
		}
		private void setButtonVisiable(WordButton button,int visibility){
			button.mViewButton.setVisibility(visibility);
			button.mIsVisiable = (visibility == View.VISIBLE) ? true :false;
			
			MyLog.d(TAG,button.mIsVisiable + "");
		}
		/**
		 * 加载当前关的数据
		 */
		@SuppressLint("NewApi")
		private void initCurrentStageData(){
			//读取当前关的歌曲信息
			mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);
			//初始化已选择框
			mBtnSelectWords = initWordSelect();
			LayoutParams params = new LayoutParams(120,120);
			//清空原来的答案
			mViewWordsContainer.removeAllViews();
			//增加新的答案框
			for(int i=0;i < mBtnSelectWords.size(); i++){
				mViewWordsContainer.addView(mBtnSelectWords.get(i).mViewButton, params);
			}
			//当前关的索引
			mCurrentStageView = (TextView) findViewById(R.id.text_current_stage);
			if(mCurrentStageView != null){
				mCurrentStageView.setText((mCurrentStageIndex + 1) + "");
			}
			//获得数据
			mAllWords = initAllWord();
			//更新数据
			mMyGridView.updateData(mAllWords);
			
			//开始播放提示
			handlePlayButton();
		}
		//初始化待选文字框
		private ArrayList<WordButton> initAllWord(){
			ArrayList<WordButton> data = new ArrayList<WordButton>();
			//获得所有待选文字
			String[] words = generateWords();
			for(int i=0; i < MyGridView.COUNTS_WORDS; i++){
				WordButton button = new WordButton();
				button.mWordString = words[i];
				data.add(button);
			}
			return data;
		}
		
		//初始化已选择文字框
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
		//生成随机汉字
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
		//生成24个带答案汉字
		private String[] generateWords(){
			Random random = new Random();
			String[] words = new String[MyGridView.COUNTS_WORDS];
			//存入歌名
			for(int i=0;i < mCurrentSong.getNameLength();i++){
				words[i] = mCurrentSong.getNameCharacters()[i] + "";
			}
			//存入随机生成的文字
			for(int i = mCurrentSong.getNameLength();i < MyGridView.COUNTS_WORDS;i++){
				words[i] = getRandomChar() + "";
			}
			//打乱文字顺序
			//首先从所有元素中选取一个元素与第一个元素进行交换，
			//然后在第二个数据之后选取一个元素与第二个数去进行交换，
			//这个过程是循环的，直到循环到最后一个元素为止
			
			for(int i = MyGridView.COUNTS_WORDS - 1; i >= 0; i--){
				int index = random.nextInt(i+ 1);
				
				String buf = words[index];
				words[index] = words[i];
				words[i] = buf;
			}
			return words;
		}
		//检查答案
		private int checkTheAnswer(){
			//先检查长度
			for(int i = 0; i < mBtnSelectWords.size(); i++){
				//如果有空，说明答案不完整
				if(mBtnSelectWords.get(i).mWordString.length() == 0){
					return STATUS_ANSWER_LACK;
				}
			}
			//检查答案正确与否
			StringBuffer sb =  new StringBuffer();
			for(int i = 0;i < mBtnSelectWords.size();i++){
				sb.append(mBtnSelectWords.get(i).mWordString);
			}
			return (sb.toString().equals(mCurrentSong.getSongName())) ?
					STATUS_ANSWER_RIGHT : STATUS_ANSWER_WRONG;
		}
		//答案错误执行方法，闪烁文字
		private void sparkTheWords(){
			//声明弱相关
			TimerTask task = new TimerTask() {
				boolean  mChange = false;
				int mSpardTimes = 0;
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						public void run() {
							//显示闪烁次数
							if(++mSpardTimes > 6){
								return;
							}
							//执行闪烁逻辑，交替显示红色和白色文字							
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
		 * 自动选择一个答案
		 */
		private void tipAnswer(){
			//减少金币数量
			if(!handleCoins(-getTipCoins())){
				//金币数量不够，显示对话框
				showConfigDialog(ID_DIALOG_LACK_COINS);
				return;
			}
			boolean tipWord = false;
			for(int i = 0; i < mBtnSelectWords.size(); i++){
				if(mBtnSelectWords.get(i).mWordString.length() == 0){
					//根据当前答案关条件选择对应的文字并填入
					onWordButtonClick(findIsAnswerWord(i));
					tipWord = true;
					break;
				}
			}
			//没有找到可以填充的答案
			if(!tipWord){
				//闪烁文字提示用户
				sparkTheWords();
			}
			
		}
		/**
		 * 找到一个答案文字
		 * @param index 当前需要填入答案框的索引
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
		 * 删除文字
		 */
		private void deleteOneWord(){
			//减少金币
			if(!handleCoins(-getDeleteWordCoins())){
				//金币不够，显示提示对话框
				showConfigDialog(ID_DIALOG_LACK_COINS);
				return;
			}
			//将这个索引对应的WordButton设置为不可见
			setButtonVisiable(findNotAnswerWord(), View.INVISIBLE);
		}
		/**
		 * 找到一个不是答案的文字，并且当前是可见的
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
		 * 判断某个文字是否为答案
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
		 * 增加或者减少指定数量的金币
		 * @param data
		 * @return true 增加或者减少成功，false 失败
		 */
		private boolean handleCoins(int data){
			//判断当前总的金币数量可否减少
			if(mCurrentCoins + data >= 0){
				mCurrentCoins += data;
				
				mViewCurrentCoins.setText(mCurrentCoins + "");
				return true;
			}else{
				//金币不够
				return false;
			}
			
		}
		/**
		 * 从配置文件里读取删除操作所要用的金币
		 * @return
		 */
		private int getDeleteWordCoins(){
			return this.getResources().getInteger(R.integer.pay_delete_word);
		}
		/**
		 * 从配置文件里提示操作所需要的金币
		 * @return
		 */
		private int getTipCoins(){
			return this.getResources().getInteger(R.integer.pay_tip_answer);
		}
		/**
		 * 处理删除待选文字事件
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
		 * 处理提示按钮事件
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
		//自定义AlertDialog事件响应
		//删除错误答案
		private IAlertDailogButtonListener mBtnOkDeleteWordListener = 
				new IAlertDailogButtonListener() {
			@Override
			public void onClick() {
				//执行事件
				deleteOneWord();
				
			}
		};
		//答案提示
		private IAlertDailogButtonListener mBtnOkTipAnswerListener = 
				new IAlertDailogButtonListener() {
			@Override
			public void onClick() {
				//执行事件
				tipAnswer();
				
			}
		};
		//金币不足
		private IAlertDailogButtonListener mBtnOkLackCoinsListener = 
				new IAlertDailogButtonListener() {
			@Override
			public void onClick() {
				//执行事件
				
			}
		};
		/**
		 * 显示对话框
		 * @param id
		 */
		private void showConfigDialog(int id){
			switch (id) {
			case ID_DIALOG_DELETE_WORD:
				Util.showDialog(MainActivity.this, 
						"确认花掉" + getDeleteWordCoins() + "个金币去掉一个错误答案?",
						mBtnOkDeleteWordListener);
				break;
			case ID_DIALOG_TIP_ANSWER:
				Util.showDialog(MainActivity.this, 
						"确认花掉" + getTipCoins() + "个金币获得一个文字提示?",
						mBtnOkTipAnswerListener);
				break;
			case ID_DIALOG_LACK_COINS:
				Util.showDialog(MainActivity.this, 
						"金币不足，去商店补充?",
						mBtnOkLackCoinsListener);
				break;

			}
		}
}
