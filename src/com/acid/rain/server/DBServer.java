package com.acid.rain.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import com.acid.rain.client.AcidRainClientPanel;
import com.acid.rain.vo.AcidRain;
import com.acid.rain.vo.DrawWord;
import com.acid.rain.vo.Message;

public class DBServer {
	
	ArrayList<Com> comList = new ArrayList<Com>();
	ArrayList<DrawWord> dwList; 
	Random random = new Random();
	int currentLevel = 1;
	
	//매번 만들어지는 클라이언트에 대응하는 소켓을 가진 Comm쓰레드
	//묶어서 관리할 객체
	
	ServerSocket ss;
	Socket s;
	
	//유저리스트 구하기
	public String getUserList(){
		String userList = "";
		StringBuilder sb = new StringBuilder();
		
		for(Com cm : comList){
			sb.append(cm.name).append(","); //반복문이죠?
		}
		
		userList = sb.toString();
		userList.substring(0, userList.length() - 1); //마지막한자에서 1뺀만큼( , 이거)
		
		return userList;
	}
	
	//서버서 단 한개의 DWList를만들어 각각 com에 전달해준다
	public void createDrawWordList(ArrayList<AcidRain> list){
		//리스트 초기화
		dwList = new ArrayList<DrawWord>();
		ArrayList<AcidRain> rList = list;
		
		int xCoord = 0;
		int yCoord = 0;
		int deltaY = 0;

		for(int i = 0; i < list.size(); i++){
			xCoord = random.nextInt(450);
			yCoord = random.nextInt(600) - 600;
			deltaY = random.nextInt(10) + 15 * currentLevel;

			dwList.add(new DrawWord(xCoord, yCoord, 
					rList.get(i).getWord(), deltaY));
		}
	}
	public void sendDrawWordList2All(){
		for(Com cm : comList){
			cm.sendDWList(101, dwList);
		}
	}
	
	
	// 유저리스트 쏘기
	public void sendUserList(Com com){
		// 서버 -> 클라에서 보낼때 약속을 하자
		
		// 각각 com을통해 보내야겠지? 뭘? 사용자 정보 리스트!
		com.sendMessage(11, getUserList());
	}
	
	// 유저 나갔을떄 arraylist에서 com객체 제거하기
	public void exitcom(Com com){
		comList.remove(com);
	}
	
	// 모두에게 뭔가 보내기 (원본은 protocol, string이었다)
	public void sendMsg2All(int protocol, String str){
		for(Com cm : comList){
			cm.sendMessage(6, str);
		}
	}
	
	public void sendInputEntry2All(int protocol, String str){
		System.out.println("EntryCheck 대장정 4: for문돌면서 각 서버에 보낸다!");
		for(Com cm : comList){
			cm.sendEntryMessage(14, str);
		}
	}
	
	public void sendUserList2All(int protocol){
		String ul = getUserList();
		for(Com cm : comList){
			cm.sendMessage(11, ul);
		}
	}
	//클라의 panelState를 받아온다
	public int checkPanelStateAndTypeName(Message msg){
		System.out.println("select 대장정 5.1(사용자갯수만큼체크): msg의 typeidx: " + msg.getAcidrain().getTypeidx());
		int pState = 0;
		int comSize = 0;
		int typeIdx = 0;
		for(Com cm : comList){
			if(cm.getPanelState() == AcidRainClientPanel.PANEL_STATE_ISREADY){
				comSize++;
				typeIdx = msg.getAcidrain().getTypeidx();
				System.out.println("select 대장정 5.2의 typeidx: " + typeIdx);
			}
		}
		if(comSize != comList.size()){
			System.out.println("모든 클라가 준비를 완료해야 합니다. 다시하세요.");
			return 0;
		}
		System.out.println("모든 클라가 준비를 완료하였습니다.");
		System.out.println("select 대장정 5.3 클라 panelStateANdTypeName끝나기 직전 idx: " + typeIdx);
		return typeIdx;
	}
	
	// 클라의 panelStater를 보내준다
	
	// 모든 패널에게 게임 시작하라고 보내기(우선 모든 패널들 검사해야함 panelState)
	
	
	//3티어로 돌려보자~!
	public DBServer(){
		ss = null;
		
		try{
			ss = new ServerSocket(12345);
			System.out.println("서버 생성 성공!");
		}catch(IOException e){
			System.out.println("서버 소켓 에러: " + e);
			return;	//서버소켓 생성 실패시 메소드 완전 종료
		}
		
		s = null;
		
		while(true){ 
			
			//클라 올때까지 기다리자구
			Com com = null;
			String remoteIP = "";
			try{
				System.out.println("서버가 접속을 대기중이다");
				s = ss.accept();
				remoteIP = s.getInetAddress().getHostAddress();
				
				System.out.println(remoteIP + "가 서버에 접속하였다");
				
				
				// 접속이 이루어진 부분
				// 주거니~ 받거니~ 주거니~ 받거니~
				
				//통신할거 쓰레드 분리해야지
				com = new Com(this, s);
				comList.add(com);	//리스트에 한놈 더한다
				com.start();	//컴 쓰레드 하나 만들엇으니 동작하쇼
				
			} catch(IOException e){
				System.out.println("소켓 생성 실패" + e);
			}
		}
	}
	
	public static void main(String[] args){
		new DBServer();
	}
	
	
}
