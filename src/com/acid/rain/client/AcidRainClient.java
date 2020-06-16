package com.acid.rain.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.acid.rain.vo.AcidRain;
import com.acid.rain.vo.DrawWord;
import com.acid.rain.vo.Message;

public class AcidRainClient {
	
	private JFrame f;
	private JPanel cPanel, csPanel, csnPanel, cssPanel,
			ePanel, ecPanel, ecnPanel, esPanel,
			nPanel, nwPanel;
	private JTextField tfTypeSelect;
	private JTextField tfEntry, tfChat, tfUsername;	//단어입력란, (추후 추가할)챗 입력란
//	private MyTextArea taScreen;			//게임 본화면 표시할 text area
	private JTextArea taList, taTypename;			//테스트용 ta
	private JButton btn, btnSignUp, btnStart;
	
	//나의 점수와 이름
	private int score;
	private String name;
	private String oldName;
	private int nameNeverChanged = 1;//true, 0: false
	private int myState;
	
	private int myUserNum = 0;
	
	//상대방(들)의 점수
	private ArrayList<AcidRain> others; //이름, 점수
	private ArrayList<String> users = new ArrayList<String>();
//	private HashMap<String, Integer> users;
	
	//콘솔 출력 thread 테스트용 리스트와 쓰레드
	private ArrayList<String> typeList;
	private ArrayList<String> wList = new ArrayList<String>();
	private ThreadTest ttest;
	
	//이미지 배경화면 설ㅈ어 관련
	private Image img;
	private AcidRainClientPanel ap;
	
	// 통신 관련 변수
	private Socket s;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	//쓰레드
	private boolean startFlag = false;
	
	
	//JDBC 3 tier 연동
	ArrayList<AcidRain> rList; //단어 받아오는 리스트
	
	
	ActionListener al = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			switch(cmd){
			case "G":
				isReady();
				break;
			case "E":	//단어입력후 엔터 or 버튼클릭
				EnterWords();
				break;
			case "T":	//transfer chat : 챗 보내기 // no need 4 now
				//beginGame();
				break;
			case "I":
				configNameInsertUpdate();
				break;
			case "U":
//				showUserTable();
				break;
				
			//////////////////////////////////////////////////////////
			// CRUD //
//			insertUser();
//			selectWords();	
//			updateUserScore();	
//			updateUserName();
//			deleteUser();	 
//			selectWordTypeName();	//readerThread시작전에만드는거
			}
		}
	};
	public void configNameInsertUpdate(){
		switch(nameNeverChanged){
		case 0:
			rename();
			updateUserName();
			break;
		case 1:
			insertUser();
			nameNeverChanged = 0;
			break;
		}
	}
	
	//Panel한테 보내는거
	
	public void sendPanelDrawWordList(ArrayList<DrawWord> dwList){
		System.out.println("서버에서 dwList를 받았다 3");
		ap.setDrawWordList(dwList);
		//dwList의 word사이즈가 20이면 panel의 state를 received로 바꾼다
		//그리고 그걸 써버로 보낸다
		//써버에서 모든 com들의 panelState가 received면 start로 state바꿔준다
		//그래야 start할수있다
		ap.setPanelState(AcidRainClientPanel.PANEL_STATE_START_SIGN_FIRED);
	}
	
	//panel한테 받아온 panelState 서버에 보내준다
	public void sendPanelStateToServer(int panelState){
		Message msg = new Message();
		msg.setType(33);
		msg.setPanelState(panelState);
		
		try{
			oos.writeObject(msg);
			System.out.println("서버로 나의 panelState를 보내었습니다 허허..");
		}catch(IOException e){
			System.out.println("panelState전송 오류:" + e);
		}
	}
	
	public void setPanelState(int panelState){
		ap.setPanelState(panelState);
	}
	
	//////////////////////////////////////////////////////////////
	//				C	R	U	D	
	//////////////////////////////////////////////////////////////
	
	// 아주 작고 귀여운(??) wList db->server->readerThread->여기까지 온거 저장용
	public void assignWordList(ArrayList<String> wList){
		this.wList = wList;
		System.out.println("wList size: " + wList.size());
		System.out.println("this.wList.size: " + this.wList.size());
	}
	
	void insertUser(){
		
		name = tfUsername.getText();
		
		AcidRain rain = new AcidRain();
		rain.setUsername(name);
		rain.setIp(getLocalIP());

		// 3티어로 가자!
		// 서버 소켓 연동
		Message msg = new Message();
		msg.setType(0);	// 0 : INSERT
		msg.setAcidrain(rain);
		
		try {
			oos.writeObject(msg);
			System.out.println("전송 성공");
		} catch (IOException e) {
			System.out.println("오브젝트 전송 오류 " + e);
		}
		
		tfUsername.setText("");
	}
	
	
	void selectWords(){
		// 0: insert 1:select 2:update 3:delete
		int typeidx = 0;
		
		for(int i = 0; i < typeList.size(); i++){
			if(tfTypeSelect.getText().equals(typeList.get(i))){
				typeidx = i + 1;
			}
		}
		System.out.println("select 대장정 2: typeidx: " + typeidx);
		
		AcidRain acidrain = new AcidRain();
		
		acidrain.setTypeidx(1); //select
		acidrain.setTypeidx(typeidx);
		
		Message msg = new Message();
		msg.setType(1);
		msg.setAcidrain(acidrain);
		msg.setPanelState(ap.getPanelState());
		System.out.println("select 대장정 3, msg의 typeidx: " + msg.getAcidrain().getTypeidx());
		
		try{
			oos.writeObject(msg);
			System.out.println("select 대장정 4");
			System.out.println("MSG sent well!");
			
			//워드를 받았으면 
		}catch(IOException e){
			System.out.println("MSG sent error: " + e);
		}
		
	}
	
	void updateUserScore(){
		//유저업데이트
	}
	
	void updateUserName(){
		//유저이름 업데이트
		AcidRain acidrain = new AcidRain(); //typeidx아니야!!
		acidrain.setUsername(name);
		System.out.println("DB로 보내기전 name: " + name + ", oldName: " + oldName);
		
		Message msg = new Message();
		msg.setType(22);
		msg.setAcidrain(acidrain);
		msg.setNameString(oldName);	//새로바꾸려는 
		
		try{
			oos.writeObject(msg);
			System.out.println("updateUserName MSG sent well");
			
		}catch(IOException e){
			System.out.println("updateUserName MSG sent error: " + e);
		}
		
		
		//유저이름리스트를갈아엎어야하는데
		//......................
		
	}
	
	void deleteUser(){
		//유저딜리트
	}
	
	// default word type //이건 처음 한 번만
	void selectWordTypeName(){
		AcidRain acidrain = new AcidRain();
		acidrain.setTypeidx(4);//select
		//acidrain.set
		
		Message msg = new Message();
		msg.setType(4);	//select type name
		msg.setAcidrain(acidrain);//객체화시켜서 보낼거야
		
		String tempR = null;//템프
		
		try{
			oos.writeObject(msg);	// 보드말고 메세지로 객체화시켜보낸다
			System.out.println("MSG sent well!");
			
			msg = (Message) ois.readObject();
			
			rList = msg.getList();
			System.out.println("rList is empty?" + (rList == null ? "O" : "X"));
			
			for(int i = 0; i < rList.size(); i++){
				tempR = rList.get(i).getTypename();
				System.out.println("tempR : " + tempR);
			}
			
		} catch(IOException e){
			System.out.println("msg selectDefault error: " + e);
		} catch (ClassNotFoundException e) {
			System.out.println("msg 잘못 받음! selectDefault error: " + e);
		}
		
		typeList = new ArrayList<String>();
		
		for(int i = 0; i < rList.size(); i++){
			System.out.println(rList.get(i).getTypename());
			taTypename.append(rList.get(i).getTypename() + "\n");
			
			////////////////////////////////////////////////
			typeList.add(i, rList.get(i).getTypename());
			////////////////////////////////////////////////
		}
	}

	// user 더하기 빼기 업뎃하기 리스트 보여주기
	void addUser(String name){
		users.add(name); //이름과 기본점수 0을 넣는다(l8er 수정)
	}
	
	void deleteUser(String name){
		users.remove(name);
	}
	
	void updateUserName(String newName){
		if(users.isEmpty()) return;	// 텅빔이면 걍 나감
		System.out.println("화면상에서 newName으로 바꾼다");
		System.out.println("newNAme바꾸기전 구이름: " + oldName);
		for(int i = 0; i < users.size(); i++){
			if(users.get(i).equals(oldName)){
				users.set(i, newName);
			}
		}
		System.out.println("화면상에서 newName으로 바꿨다");
	}
	
	void updateUserScore(String name, int newScore){
		//users.replace(name, newScore);
	}
	
	void rename(){ //이거랑 update랑 insert랑 연결시켜야되나?
		//nameNeverChange가 true면 insert랑 연결, 아니면 updateName이랑 연결
		if(name.isEmpty()) return;

		oldName = name;
		name = tfUsername.getText().trim();
		System.out.println("바꾸기전 현재 이름: " + oldName);
		System.out.println("바꿀 이름: " + name);
		
		updateUserName(name);

		setFrameName(name);
		
		//다 끝났으면 다시 공백으로
		tfUsername.setText("");
	}
	
	//이름보여줘
	public void showUserList(String[] nameList){
		//리스트 일단 지워놓고
		taList.setText("");
		String[] list = nameList;
		for(String name: list){
			taList.append(name + "\n");
		}
	}
	
	
	// ==========================================================
	
	void isReady(){
//		myState = Message.IS_READY;
		
		ap.setPanelState(AcidRainClientPanel.PANEL_STATE_ISREADY);
		System.out.println("select 대장정 1");
		selectWords();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
		
//		new Thread(){
//			public void run(){
//				while(true){
//					if(ap.getWList().size() != 0){
//						ap.startAniThread();
//						System.out.println("break전");
//						break;
//					}
//					
//					try{
//						System.out.println(wList.size());
//						sleep(100);
//					}catch(InterruptedException e){ }
//				}
//				System.out.println("while 벗어났어break후");
//			}
//		}.start();
		
		ap.startAniThread();
		
		//버튼 끝날때까지 비활성화
		btnStart.setEnabled(false);
	}
	
	// ============================================================
	
	//버튼 활성화
	public void gameIsOver(){
		JOptionPane.showMessageDialog(f, "Round Over!", "Weak Acid Rain Alert", JOptionPane.INFORMATION_MESSAGE);
		btnStart.setEnabled(true);
	}
	
	
	
	void EnterWords(){
		String input = tfEntry.getText();
		//
		//쓰레드가 돌아가고있을때를 체크하고 돌고있을대만 아래 메소드를
		//실행하고 싶으면 어떻게할까? ==> 몰라 이눔아
		//
		if(input == null){	//널이면 리턴, 시작 안했으면 리턴
			tfEntry.setText("");
			return;
		}
		System.out.println("EntryCheck 대장정 1, input : " + input);
		//해당 단어를 메세지에 담아 server로 보낸다!
		Message entryMsg = new Message();
		entryMsg.setType(34);
		System.out.println("EntryCheck 대장정 2, 이제 서버로 보낸다!");
		entryMsg.setEntryString(input);
		
		try{
			oos.writeObject(entryMsg);
		} catch(IOException e){
			System.out.println("entryMsg send error: " + e);
		}
		
		
		tfEntry.setText("");
	}
	
	public void matchWord(String inputEntry){
		ap.matchWord(inputEntry);	//텍스트를 받아온 후 보내버린다(비교하러)
	}
	
	public AcidRainClient(){
		
		setGUI();
		
		//클라 시작 즉시 서버에 접속하여 DB와 통신 준비한다
		try{
			s = new Socket("49.173.165.42", 12345);
			oos = new ObjectOutputStream( s.getOutputStream() );
			// 오브젝트로 통신하기위한 magical sequence
			oos.flush();
			ois = new ObjectInputStream( s.getInputStream() );
			//wordType 갖고와서 넣어준다
			selectWordTypeName();
			System.out.println("readerThread 시작전");
			////////// READER THREAD 시작한다 //////////
			new ReaderThreadClient(this, ois).start();
			System.out.println("readerThread 시작함!");
			
			myState = Message.IS_CONNEDTED;
			
		}catch(IOException e){
			System.out.println("클라 접속 오류: " + e);
		}
		
	}

	public String getLocalIP() {
		
		String localIP = "127.0.0.1";
		
		try {
			localIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			System.out.println("Local IP 얻기 실패" + e);
		}
		
		return localIP;
		
	}
	
	public void exitClient(){
		//프로토콜 보내기용 메세지
		Message msg = new Message();
		msg.setType(9);
		
		try{
			if(oos != null){
				oos.writeObject(msg);
			}
		} catch(IOException e){
			
		} finally{
			if(ois != null){
				try{
					ois.close();
				} catch(IOException e){}
			}
			if(oos != null){
				try{
					ois.close();
				} catch(IOException e){}
			}
			if(s != null){
				try{
					s.close();
				} catch(IOException e){}
			}
		}
		
		f.setVisible(false);
		System.exit(0);
	}
	
	
	public void setFrameName(String name){
		this.name = name;
		f.setTitle(String.format("%s's excitement packed Weak Acid Rain (o^0^)==o", this.name));
		
	}
	
	
	void setGUI(){
		
		f = new JFrame("Weak Adic Rain Game Client (o^0^)==o");
		f.setBounds(new Rectangle(0,0,700,600));
		f.setResizable(false);
		
		//north panel
		nPanel = new JPanel(new BorderLayout());
		nwPanel = new JPanel(new BorderLayout());
		tfUsername = new JTextField();
		tfUsername.setBorder(BorderFactory.createLineBorder(new Color(244,  244, 242), 5));
		tfUsername.setBackground(new Color(254, 254, 252));
		nwPanel.add(new JLabel(" NAME "), BorderLayout.WEST);
		nwPanel.add(tfUsername, BorderLayout.CENTER);
		btnSignUp = new JButton("등록");
		btnSignUp.setActionCommand("I");
		btnSignUp.addActionListener(al);
		
		nPanel.add(nwPanel, BorderLayout.CENTER);
		nPanel.add(btnSignUp, BorderLayout.EAST);
		
		
		
		// center panel
		cPanel = new JPanel(new BorderLayout());	
		ap = new AcidRainClientPanel(this);
		ap.setBorder(BorderFactory.createLineBorder(Color.GRAY, 5));
		
		
		// cs panels
		csPanel = new JPanel(new GridLayout(2, 1));
		csPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 238), 5));
		csnPanel = new JPanel(new BorderLayout()); //CSN
		
		btn = new JButton("ENTER");
		btn.setActionCommand("E");
		btn.addActionListener(al);
		
		//tfEntry에 ActionListener추가하여 엔터 인식하도록
		tfEntry = new JTextField();	
		tfEntry.setActionCommand("E");
		tfEntry.addActionListener(al);
		tfEntry.setBorder(BorderFactory.createLineBorder(new Color(222, 200, 222), 5));
		tfEntry.setBackground(new Color(243, 220, 242));
		
		csnPanel.add(new JLabel("Write here >"), BorderLayout.WEST);
		csnPanel.add(tfEntry, BorderLayout.CENTER);
		csnPanel.add(btn, BorderLayout.EAST);
		
		cssPanel = new JPanel(new BorderLayout());	//CSS
		
		btn = new JButton("ENTER");
		btn.setActionCommand("T"); //transfer chat
		btn.addActionListener(al);
		tfChat = new JTextField();
		tfChat.setBorder(BorderFactory.createLineBorder(new Color(212, 222, 222), 5));
		tfChat.setBackground(new Color (232, 242, 242));
		cssPanel.add(new JLabel("Enter chat >"), BorderLayout.WEST);
		cssPanel.add(tfChat, BorderLayout.CENTER);
		cssPanel.add(btn, BorderLayout.EAST);
		
		csPanel.add(csnPanel);
		csPanel.add(cssPanel);
		
		
		
		// east panel
		ePanel = new JPanel(new BorderLayout());
		taList = new JTextArea();
		taList.setBorder(BorderFactory.createLineBorder(new Color(222, 233, 232), 7));
		taList.setBackground(new Color(242, 253, 252));
		taList.setEditable(false);
		
		btnStart = new JButton("START GAME");
		btnStart.setActionCommand("G");
		btnStart.addActionListener(al);
		
		ePanel.add(taList, BorderLayout.CENTER);
		ePanel.add(btnStart, BorderLayout.SOUTH);
		ePanel.setBorder(BorderFactory.createLineBorder(new Color(232, 220, 210), 5));
		
		ecPanel = new JPanel(new GridLayout(2, 1)); //east Center
		
		ecnPanel = new JPanel(new BorderLayout());
		ecnPanel.add(new JLabel("~ ~ ~ players list ~ ~ ~"), BorderLayout.NORTH);
		ecnPanel.add(taList, BorderLayout.CENTER);
		
		ecPanel.add(ecnPanel);
		taTypename = new JTextArea();
		taTypename.setBackground(new Color(238, 238, 248));
		taTypename.setBorder(BorderFactory.createLineBorder(new Color(223,  223, 233 ), 5));
		ecPanel.add(taTypename);
		
		esPanel = new JPanel(new GridLayout(2, 1));
		esPanel.setBorder(BorderFactory.createLineBorder(new Color(244, 255, 232)));
		
		tfTypeSelect = new JTextField();
		tfTypeSelect.setActionCommand("G");
		tfTypeSelect.addActionListener(al);
		tfTypeSelect.setBorder(BorderFactory.createLineBorder(new Color(222, 230, 210), 5));
		tfTypeSelect.setBackground(new Color(232, 240, 220));
		esPanel.add(tfTypeSelect);
		esPanel.add(btnStart);
		
		ePanel.add(ecPanel, BorderLayout.CENTER);
		ePanel.add(esPanel, BorderLayout.SOUTH);
		
		
		// add 'em all onto cpanel
//		cPanel.add(taScreen, BorderLayout.CENTER);
		cPanel.add(ap, BorderLayout.CENTER);
		cPanel.add(csPanel, BorderLayout.SOUTH);

		
		// f
		f.add(nPanel, BorderLayout.NORTH);
		f.add(cPanel, BorderLayout.CENTER);
		f.add(ePanel, BorderLayout.EAST);
		
		
		
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		f.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				int opNum = JOptionPane.showConfirmDialog(f, "Are you sure you want to exit the game?", 
						"Quit", JOptionPane.OK_CANCEL_OPTION);
				if(opNum == JOptionPane.OK_OPTION){
					exitClient();
				}
			}
			
		});
		f.setVisible(true);
	}
	
	public void printOnMyConsole(String s){
		System.out.println("전달받은 스트링: " + s);
	}
	
	public static void main(String[] args){
		new AcidRainClient(); //
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
}
