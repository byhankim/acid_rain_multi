package com.acid.rain.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import com.acid.rain.dao.AcidRainDAO;
import com.acid.rain.vo.AcidRain;
import com.acid.rain.vo.DrawWord;
import com.acid.rain.vo.Message;

public class Com extends Thread{
	
	DBServer server;
	Socket s;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	
	//user 닉네임을 위해서
	String name;
	static int nameCnt = 0;	//이 객체가 여러번 만들어져도 static이라 하나야
	
	int panelState = 0;
	
	boolean onAir = true;
	
	//DrawList생성
	ArrayList<DrawWord> drawWordList;
	Random random;
	int currentLevel = 1;
	
	//모든 state == isReady인 유저들에게 DrawWordList를날려줘야지
	
	public Com(DBServer server, Socket s){
		name = "user" + ++nameCnt; //default 네임 표시용
		this.server = server;
		this.s = s;
		//랜덤도 여기서 그냥 초기화하자^^
		random = new Random();
		
		try{
			ois = new ObjectInputStream( s.getInputStream() );
			oos = new ObjectOutputStream( s.getOutputStream() );
			//이것만 해주면 magical sequence 내부적으로 보내서 해결댐
			// 최초 접속시 접속을 알리는 그것인가! 조금 찾아보면 좋겠다.
			// 4byte 'magical' squence AC ED 00 05 를 보내야 네트워크에서 통신이 가능하다.
			oos.flush();
			
		} catch (IOException e){
			System.out.println("Com 생성자 오류: " + e);
		}
	}
	
	// 각각 클라로 쏴주는 부분 /////////////////////////////////////
	
	public void sendMessage(int msgType, String str){
		try{
			//보내기용 템프 메세지 객체
			Message message = new Message();
				
			message.setType(msgType);
			message.setuListString(str);
			oos.writeObject(message);
			
		}catch(IOException e){
			System.out.println("sendMessage 에러: " + e);
		}
	}
	
	public void sendEntryMessage(int msgType, String entry){
		try{
			//보내기용 템프 메세지 객체
			Message message = new Message();
			
			message.setType(msgType);
			message.setEntryString(entry);
			System.out.println("EntryCheck 대장정 5(이제 클라로 보낸다!) entry: " + entry);
			oos.writeObject(message);
			
		} catch(IOException e){
			System.out.println("send entry 에러: " + e);
		}
	}
	
	// DrawWordList쏴주는부분~
	public void sendDWList(int msgType, ArrayList<DrawWord> dwList){
		try{
			Message message = new Message();
			
			message.setType(msgType);
			message.setDWList(dwList);
			oos.writeObject(message);
		} catch(IOException e){
			System.out.println("sendDrawMessageError: " + e);
		}
	}
	
	// panel state 변경해주기
	public void sendPanelState(int msgType, int panelState){
		try{
			Message message = new Message();
			
			message.setType(msgType);
			message.setPanelState(panelState);
			oos.writeObject(message);
			
		}catch(IOException e){
			System.out.println("sendPanelState Error: " + e);
		}
	}
	
	// =========================================================
	// 				C	R	U	D
	// =========================================================
	
	//이름 바꿔버리기~
	void myNameIs(AcidRain acidrain){
		name = acidrain.getUsername();
	}
	
	void insertUser(AcidRain acidrain){
		
		AcidRainDAO dao = new AcidRainDAO();
		dao.insertUser(acidrain);
		
		//com이름바꾸기
		myNameIs(acidrain);
	}
	
	Message selectWords(AcidRain acidrain){
		AcidRainDAO dao = new AcidRainDAO();
		
		Message msg = new Message();
		msg = dao.selectWords(acidrain);
//		System.out.println("select 대장정 9: selectword하고나서 리턴값 받아옴");
		//acidrain의 word만 받은상태. 이제 drawword로 가공하자
		
		return msg;
	}
	
	void updateUserScore(AcidRain acidrain){
		AcidRainDAO dao = new AcidRainDAO();
		
		dao.updateUserScore(acidrain);
	}
	
	void updateUserName(AcidRain acidrain, String oldName){
		AcidRainDAO dao = new AcidRainDAO();
		
		dao.updateUserName(acidrain, oldName);
		
		//com이름바꾸기
		myNameIs(acidrain);
	}
	
	void deleteUser(AcidRain acidrain){
		AcidRainDAO dao = new AcidRainDAO();
		
		dao.deleteUser(acidrain);
	}
	
	void selectWordTypeName(AcidRain acidrain){
		AcidRainDAO dao = new AcidRainDAO();
		
		Message msg = null;
		
		msg = dao.selectWordTypeName(acidrain);

		try{
			oos.writeObject(msg);
			System.out.println(msg.getList().size());
		}catch(IOException e){
			System.out.println("서버에서 받아온 typename에러 : " + e);
		}
	}
	
	
	
	/////////////////////////////////////////////////////////////
	
	public void run(){
		
		Message msg = null;
		int msgType = 0;
		String entryTemp = "";
		
		//시작하자마자 이름 보내고 싶다
		//server.sendUserList(this);
		
		//server.sendUserList2All(11);
		
		try{
			while(onAir){
				msg = (Message) ois.readObject();
				msgType = msg.getType();
				
				switch(msgType){
				case 0:
					insertUser(msg.getAcidrain());
					server.sendUserList2All(11);
					break;
				case 1:
					Message tempMsg = msg;
					int typeIdx = 0;
					panelState = msg.getPanelState();
					// 요기서 현재 접속중인 pState == isready 인 애들의
					// cnt++하고 typeName을 계속 갱신한다
					typeIdx = server.checkPanelStateAndTypeName(tempMsg);
					tempMsg.getAcidrain().setTypeidx(typeIdx);
					
					tempMsg = selectWords(tempMsg.getAcidrain());
//					System.out.println("select 대장정 8: selectwords하고난 tempMsg");
					server.createDrawWordList(tempMsg.getList());
					server.sendDrawWordList2All();
					break;
				case 2:
					updateUserScore(msg.getAcidrain());
					break;
				case 22:
					updateUserName(msg.getAcidrain(), msg.getNameString());
					server.sendUserList2All(11);
					break;
				case 3:
					deleteUser(msg.getAcidrain());
					break;
				case 4:
					selectWordTypeName(msg.getAcidrain());
//					server.sendUserList(this);
					server.sendUserList2All(11);
					break;
				case 9:
					server.exitcom(this);
					server.sendUserList2All(11);
					onAir = false;	//while을 벗어나야 catch걸리기전에 꺼버리지
					break;
				case 33: //패널의 state값 받아온다
					panelState = msg.getPanelState();
//					server.checkIfAllPanelIsReady(panelState);
					break;
				case 34:
					entryTemp = msg.getEntryString();
					//여기서 단어를 각각 클라이언트로 쏴주는데
					//synchronized를 활용하여
					//값에 접근중이면 기다리도록 처리를 해 주어야 한다. 아님 곰보SSSsss
					server.sendInputEntry2All(14, entryTemp);
					break;
				}//switch문 끝
				
			}// while문 끝
			

			if(oos != null){
				try{
					oos.close();	//출력 객체 종료
				}catch(IOException e){
				}
			}

			if(ois != null){
				try{
					ois.close();	//입력 객체 종료
				}catch(IOException e){
				}
			}
			if(s != null){
				try{
					s.close();		//소켓 객체 종료
				}catch(IOException e){
				}
			}
			

		} catch (IOException e) {
			System.out.println(e);
		} catch (ClassNotFoundException e) {
			System.out.println(e);
		}
		
	}

	public int getPanelState() {
		return panelState;
	}

	public void setPanelState(int panelState) {
		this.panelState = panelState;
	}
	
	
}
