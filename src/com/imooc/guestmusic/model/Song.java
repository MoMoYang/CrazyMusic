package com.imooc.guestmusic.model;

public class Song {
	//歌曲名称
	private String mSongName;
	//歌曲的文件名
	private String mSongFileNmae;
	//歌曲名字长度
	private int mNameLength;
	
	public char[] getNameCharacters(){
		return mSongName.toCharArray();
	}
	
	public String getSongName() {
		return mSongName;
	}
	public void setSongName(String SongName) {
		this.mSongName = SongName;
		this.mNameLength = SongName.length();
	}
	
	public String getSongFileNmae() {
		return mSongFileNmae;
	}
	public void setSongFileNmae(String SongFileNmae) {
		this.mSongFileNmae = SongFileNmae;
	}
	
	public int getNameLength() {
		return mNameLength;
	}

	
}
