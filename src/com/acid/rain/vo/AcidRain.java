package com.acid.rain.vo;

import java.io.Serializable;

public class AcidRain implements Serializable{
	
	
	private static final long serialVersionUID = 403131881182796609L;
	
	private int wordidx; //단어인덱스
	private String word;	//단어 그 자체
	private int typeidx;	//단어 타입 index
	private boolean wordflag; //단어삭제플래그
	private String ip;
	
	private String typename;	//단어타입
	
	//사용자
	private String username;
	
	
	// --------- 게터 세터 -----------------
	
	public int getWordidx() {
		return wordidx;
	}
	public void setWordidx(int wordidx) {
		this.wordidx = wordidx;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public int getTypeidx() {
		return typeidx;
	}
	public void setTypeidx(int typeidx) {
		this.typeidx = typeidx;
	}
	public boolean isWordflag() {
		return wordflag;
	}
	public void setWordflag(boolean wordflag) {
		this.wordflag = wordflag;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getTypename() {
		return typename;
	}
	public void setTypename(String typename) {
		this.typename = typename;
	}
	
	
	
}
