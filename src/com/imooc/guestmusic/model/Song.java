package com.imooc.guestmusic.model;

public class Song {
	//��������
	private String mSongName;
	//�������ļ���
	private String mSongFileNmae;
	//�������ֳ���
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
