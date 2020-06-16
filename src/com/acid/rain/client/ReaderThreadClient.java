package com.acid.rain.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import com.acid.rain.vo.AcidRain;
import com.acid.rain.vo.DrawWord;
import com.acid.rain.vo.Message;

public class ReaderThreadClient extends Thread{
	private AcidRainClient client;
	private ObjectInputStream ois;
	
	//생-성-자
	public ReaderThreadClient(AcidRainClient client, ObjectInputStream ois){
		this.client = client;
		this.ois = ois;
	}
	
	//////////////////////////////////////////////////////////////
	
	//			C 	R	U	D
	
	//////////////////////////////////////////////////////////////
	
	
//	ArrayList<String> selectWords(){
//		// 0: insert 1:select 2:update 3:delete
//		int typeidx = 0;
//		
//		for(int i = 0; i < typeList.size(); i++){
//			if(tfTypeSelect.getText().equals(typeList.get(i))){
//				typeidx = i + 1;
//			}
//		}
//		
//		AcidRain acidrain = new AcidRain();
//		
//		acidrain.setTypeidx(1); //select
//		acidrain.setTypeidx(typeidx);
//		
//		Message msg = new Message();
//		msg.setType(1);
//		msg.setAcidrain(acidrain);
//		
//		try{
//			oos.writeObject(msg);
//			System.out.println("MSG sent well!");
//			
//			msg = (Message) ois.readObject();
//			
//			ArrayList<AcidRain> alist = msg.getList();
//			
//			System.out.println("list size: " + alist.size());
//			
//			for(int i = 0; i < alist.size(); i++){
//				list.add(i, alist.get(i).getWord());
//			}
//			
//			//워드를 받았으면 
//		}catch(IOException e){
//			System.out.println("MSG sent error: " + e);
//		}catch(ClassNotFoundException e){
//			System.out.println("MSG receive error(select): " + e);
//		}
//		
//
//		
//		System.out.println("일단 여까지 왔어 4");
//		return list;
//	}
	
	public ArrayList<DrawWord> selectWords(Message msg){
		Message msgTemp = new Message();
		ArrayList<DrawWord> tempDWList = msg.getDWList();
		
		return tempDWList;
	}
	
	
	//내 단계에서 서버랑 주고받고 할란다!
	public void run(){
		
		Message msg = null;
		ArrayList<DrawWord> dwList = null;
		int msgType = 0;
		int panelState = 0;
		String inputEntry = "";
		
		try{
			while(true){
				msg = (Message) ois.readObject();
				
				msgType = msg.getType();
				// 5 유저리스트 refresh, 6: userScore refresh
				switch(msgType){
				case 101:
					dwList = selectWords(msg);
					client.sendPanelDrawWordList(dwList);
					break;
				case 11:
					String[] nameList = msg.getuListString().split(",");
					System.out.println(nameList);
					client.showUserList(nameList);
					break;
				case 12:	//서버에서 DrawWordList받아온다
					dwList = msg.getDWList();
					client.sendPanelDrawWordList(dwList);
					//13번에서 옮겨왔다 두줄!
					panelState = msg.getPanelState();
					client.setPanelState(panelState);
					break;
				case 13:	//server서 panelstate를 받아온다음 클라를통해 넣는다
					break;
				case 14:
					//여기서 entry를 받고 클라->패널로 matchword를...
					inputEntry = msg.getEntryString();
					client.matchWord(inputEntry);
					break;
				}// switch문 끝
			}// while문 끝
		} catch(IOException e){
			System.out.println("readerThread error: " + e);
		} catch (ClassNotFoundException e) {
			System.out.println("Msg readObject error: " + e);
		}
		
		
		
		
	}
	
	
}
