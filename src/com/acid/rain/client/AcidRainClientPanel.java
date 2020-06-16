package com.acid.rain.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;

import com.acid.rain.vo.DrawWord;

public class AcidRainClientPanel extends JPanel{
	
	private ArrayList<String> typenameList;	//타입이름 저장용 리스트
	private ArrayList<String> wList;	//단어 저장용 리스트
	private ArrayList<DrawWord> dwList;	//단어 + x, y coord 저장용 리스트
	
	//패널 쓰레드 pause같은거 조정용
	public static int PANEL_STATE_CLOSED = 0;
	public static int PANEL_STATE_OPEN = 1;
	public static int PANEL_STATE_ISREADY = 2;
	public static int PANEL_STATE_START_SIGN_FIRED = 3;
	public static int PANEL_STATE_INGAME = 4;
	
	private int panelState = PANEL_STATE_CLOSED;
	
	
	private int xCoord, yCoord = -15;	//초기값 
	private int level = 1; //레벨 (1~3까지있는걸로 하자 or not exist)
	
	private Random random = new Random(); //랜덤한 자리선정을위한..
	
	Font font = new Font("아리따-돋움(TTF)-SemiBold", Font.PLAIN, 22);
	
	//애니메이션 이너쓰레드 돌리기용
	private boolean onAir = false;
	private AniThread at;
	private DrawWord word;
	private int deltaY;
	private Color c = new Color(245,245,255);
	
	//쓰레드 종료용
	private int wordCnt;	//DrawWord 갯수 카운트용
	private AcidRainClient client;
	
	
	//생성자
	public AcidRainClientPanel(AcidRainClient client){
		 this.client = client;
		 panelState = PANEL_STATE_OPEN;
	}
	
	public void setDrawWordList(ArrayList<DrawWord> dwList){
		this.dwList = new ArrayList<DrawWord>();
		wList = new ArrayList<String>();
		
		String str = "";
		
		this.dwList = dwList;
		
		for(int i = 0; i < this.dwList.size(); i++){
			str = this.dwList.get(i).getText();
			wList.add(str);
		}
		if(!wList.isEmpty()){
			//panelState = PANEL_STATE_START_SIGN_FIRED;
			System.out.println("panelstate: " + panelState);
		}
		System.out.println("난 a패널이고 나의 dwList길이는: " + this.dwList.size());
	}
	
	
	//애니메이션 쓰레드
	public void startAniThread(){
		if(!onAir){
			onAir = true;
			at = new AniThread();
			
			System.out.println("스타트 사인 주어졌다!");
			at.start();	//start한다
			
		}else{
			onAir = !onAir;
		}
	}
	
	//list 비교용
	public void matchWord(String s){
		for(int i = 0; i < dwList.size(); i++){
			if(s.equals(dwList.get(i).getText())){
				dwList.remove(i);
				System.out.println(s + " 입력!");
			}
			checkEmptyList();
		}
	}
	
	//바닥에 닿으면?
	public void wordTouchedHeight(){
		for(int i = 0; i < dwList.size(); i++){
			if(dwList.get(i).getY() >= getHeight()){
				System.out.println(dwList.get(i).getText()+ " 땅에 닿음!");
				dwList.remove(i);
				//클라를통해 서버에 send!
			}
			checkEmptyList();
		}
	}
	
	
	
	//list empty 테스트용
	public void checkEmptyList(){
		// 리스트 없을시 리턴
		if(dwList.isEmpty()){
			System.out.println("텅빔");
			repaint();
			client.gameIsOver();	//클라를 통해 게임오버 joptionpane띄우기
			panelState = PANEL_STATE_OPEN;
			onAir = !onAir;
		}
	}
	
//	public void testAssignWList(){
//		for(int i = 0; i < this.wList.size(); i++){
//			xCoord = random.nextInt(500);
//			yCoord = random.nextInt(600) - 600; //맞나?
//			deltaY = random.nextInt(10) + 10;
//			
//			//alignment : 정렬
//			
//			word = new DrawWord(xCoord, yCoord, this.wList.get(i), deltaY);
//			dwList.add(word);
//			
////			System.out.println(dwList.get(i).getText());
//		}			
//	}
	
	class AniThread extends Thread{
		
		int dx;
		int dy = 20;
		
		public AniThread(){
		}
		
		public void run(){	//여기서는 내려주는거랑 계속 그려주기만 하면됨
			while(onAir){
				if(panelState == PANEL_STATE_START_SIGN_FIRED){
					//ready일때만(서버에서 줘야한다 서버에서!!!)
					//panelState = PANEL_STATE_INGAME;
					drawWords();
					
					//리스트 수량 확인
					wordTouchedHeight();
					
					repaint();
					
					try{
						sleep(500);
					} catch(InterruptedException e){}
				}
				
			}
		}
		
	}
	
	public void drawWords(){
		for(int i = 0; i < dwList.size(); i++){
			DrawWord temp = dwList.get(i);
			temp.yAxisMover();
			dwList.set(i, temp);
		}
	}
	
	void setGraphicsSetting(Graphics g){
		g.setColor(c);
		g.fillRect(0, 0, getWidth(), getHeight());
	}
	
	// =============   페  인  트  ================
	@Override
	public void paint(Graphics g) {
		setGraphicsSetting(g);
		
		g.setFont(font);
		g.setColor(new Color(200, 170, 220));
		if(dwList != null){
			for(int i = 0; i < dwList.size(); i++){
				g.drawString(dwList.get(i).getText(), 
						dwList.get(i).getX(), dwList.get(i).getY());
			}
		}
	}
	
	@Override
	public void update(Graphics g) {
		
		paint(g);	//내가 오버라이딩 하면 호출해야 그림이 그려진다
	}
	
	// ------------------------------------------
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public ArrayList<String> getWList() {
		return wList;
	}

	public int getPanelState() {
		return panelState;
	}

	public void setPanelState(int panelState) {
		this.panelState = panelState;
	}

	
	
	
	
}
