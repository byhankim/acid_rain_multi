package com.acid.rain.vo;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable{	//직렬화하여 보낼 객체
	//사실 얘도 최상위 하나 만들어놓고 용도별로 다르게
	//쓰려면 변수추가할게 아니라 상속시켜서 사용해야함
	public static int IS_CONNEDTED = 1;
	public static int IS_READY = 2;
	public static int IS_PLAYING_GAME = 3;
	public static int HAS_FINISHED_GAME = 4;
	
	
	private static final long serialVersionUID = -3546158357126396496L;
	
	private int type = 0;
	private AcidRain acidrain;
	
	//select를 위한 ArrayList보관용
	private ArrayList<AcidRain> list;
	
	//유저리스트 위한 String 보관용
	private ArrayList<String> uList;
	private String uListString;
	
	//이름체인지를 위한 String 보관
	private String nameString;
	private String entryString;
	
	//DrawWord List를 클라로 보내기위한 리스트
	private ArrayList<DrawWord> dwList;
	private int panelState;
	
	//네트워크를 위한 각각의 myState보관
	private int state;

	
	// ================ GETTER SETTER ====================
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public AcidRain getAcidrain() {
		return acidrain;
	}

	public void setAcidrain(AcidRain acidrain) {
		this.acidrain = acidrain;
	}

	public ArrayList<AcidRain> getList() {
		return list;
	}

	public void setList(ArrayList<AcidRain> list) {
		this.list = list;
	}
	
	
	public ArrayList<String> getuList() {
		return uList;
	}

	public void setuList(ArrayList<String> uList) {
		this.uList = uList;
	}
	
	
	public String getuListString() {
		return uListString;
	}

	public void setuListString(String uListString) {
		this.uListString = uListString;
	}
	
	public String getNameString() {
		return nameString;
	}

	public void setNameString(String nameString) {
		this.nameString = nameString;
	}
	

	public String getEntryString() {
		return entryString;
	}

	public void setEntryString(String entryString) {
		this.entryString = entryString;
	}
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	public ArrayList<DrawWord> getDWList() {
		return dwList;
	}

	public void setDWList(ArrayList<DrawWord> dwList) {
		this.dwList = dwList;
	}
	
	public int getPanelState() {
		return panelState;
	}

	public void setPanelState(int panelState) {
		this.panelState = panelState;
	}

	// toString
	@Override
	public String toString() {
		return "Message [type=" + type + ", acidrain=" + acidrain + ", list=" + list + "]";
	}
	
	
}
