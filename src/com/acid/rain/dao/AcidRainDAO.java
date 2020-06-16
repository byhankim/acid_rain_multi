package com.acid.rain.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.acid.rain.vo.AcidRain;
import com.acid.rain.vo.Message;

public class AcidRainDAO {
	// DAO : Data access Object
	// VO : Value OBject
	
	String insertSQL = "insert into users(username, ip) values(?, ?);";
	
	String selectSQL = "SELECT words.word FROM wordtype inner join words "
			+ "ON wordtype.typeidx = words.typeidx WHERE wordtype.typeidx = ?";
	
	String updateSQL = "update users set userscoretot = userscoretot + ? where username = ?;";
	
	String updateNameSQL = "update users set username = ? where username = ?;";
	
	String deleteSQL = "delete from users where username = ?;";
	
	String selectTypeNameSQL = "SELECT typename FROM wordtype WHERE 1 = ?;";
	
	public void insertUser(AcidRain acidrain){
		
		Connection conn = null;
		PreparedStatement stmt = null;
		
		//jdbc했으면 finally로 닫는다
		
		try{
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(insertSQL);
			stmt.setString(1, acidrain.getUsername());
			stmt.setString(2, acidrain.getIp());
			
			//execute는 DDL할떄 쓴다
			int cnt = stmt.executeUpdate();
			System.out.println("insert: " + (cnt == 1 ? "성공" : "실패"));
			
		}catch(SQLException e){
			System.out.println("insertSQL error" + e);
		}finally{
			JDBCUtil.close(stmt, conn);
		}
		
	}
	
	// select words
	public Message selectWords(AcidRain acidrain){
		Connection conn = null; //import할때 java.sql.Connection해야대!!!!
		PreparedStatement stmt = null;

		ArrayList<AcidRain> list = null;
		AcidRain rain = null;
		
		//message에 담아서 보내자 얘두!
		
		Message msg = null;
		
		try{
			System.out.println("aa: " + acidrain.getTypeidx());
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(selectSQL);
			stmt.setInt(1, acidrain.getTypeidx());
			
			// exe query: select하나만
			// exe update: insert, delete, update
			
			ResultSet rst = stmt.executeQuery();
			
			msg = new Message();
			list = new ArrayList<AcidRain>();
			
			while(rst.next()){
				rain = new AcidRain();
				rain.setWord(rst.getString("word"));
				System.out.println(rst.getString("word"));
				list.add(rain);
			}
			
			msg.setList(list);
		}catch(SQLException e){
			System.out.println("selectSQL error: " + e);
		}finally{
			JDBCUtil.close(stmt, conn);
		}
		
		return msg;
		
	}
	
	
	//udpate
	public void updateUserScore(AcidRain acidrain){

		Connection conn = null; //import할때 java.sql.Connection해야대!!!!
		PreparedStatement stmt = null;
		
		int score = 0;
		
		try{
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(updateSQL);
			stmt.setInt(1, score);
			stmt.setString(2, acidrain.getUsername());
			
			int cnt = stmt.executeUpdate();
			System.out.println("update " + (cnt == 0 ? "failed" : "succeeded"));
			
		}catch(SQLException e){
			System.out.println("acidrain update error: " + e); //디버깅용
		}finally{
			JDBCUtil.close(stmt, conn);
		}
		
	}
	
	//update user name
	public void updateUserName(AcidRain acidrain, String oldName){

		Connection conn = null; //import할때 java.sql.Connection해야대!!!!
		PreparedStatement stmt = null;
		
		//String newName = "";
		
		try{
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(updateNameSQL);
			stmt.setString(1, acidrain.getUsername());
			stmt.setString(2, oldName);
			
			int resultCnt = stmt.executeUpdate();
			System.out.println("nameupdate " + (resultCnt == 0 ? "FAILED!" : "SUCCESS!"));
			
		} catch(SQLException e){
			
		} finally{
			JDBCUtil.close(stmt, conn);
		}
		
	}
	
	public void deleteUser(AcidRain acidrain){

		Connection conn = null; //import할때 java.sql.Connection해야대!!!!
		PreparedStatement stmt = null;


		try{
			conn = JDBCUtil.getConnection(); //유틸이갖고있는 커넥션 갖고와
			stmt = conn.prepareStatement(deleteSQL);	//인자로 insert구문을줘서 stmt얻기
			stmt.setString(1, acidrain.getUsername());
			
			// exe query : select하나만	
			// exe update: insert, delete, update
			
			int cnt = stmt.executeUpdate();	//숫자하나나온다->영향받은 레코드의 갯수
			
			System.out.println("delete" + (cnt == 0 ? "실패" : "성공"));
			
		}catch(SQLException e){
			System.out.println("board delete error: " + e); //디버깅용
		}finally{
			JDBCUtil.close(stmt, conn);
			// insert도 열닫, delete도 여닫, update도 여닫!
		}
	}
	
	
	
	

	// select word typename
	public Message selectWordTypeName(AcidRain acidrain){

		
		System.out.println("dao 여기까진 왔다 1");
		Connection conn = null; //import할때 java.sql.Connection해야대!!!!
		PreparedStatement stmt = null;

		ArrayList<AcidRain> list = null;
		AcidRain rain = null;
		
		//message에 담아서 보내자 얘두!
		
		Message msg = null;
		
		try{
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(selectTypeNameSQL);
			stmt.setString(1, "1");
			
			// exe query: select하나만
			// exe update: insert, delete, update
			
			ResultSet rst = stmt.executeQuery();
			
			System.out.println("resultset: " + (rst == null ? "null" : "not null"));
			
			System.out.println("dao 여기까진 왔다 2");
			
			msg = new Message();
			list = new ArrayList<AcidRain>();
			
			
			while(rst.next()){
				rain = new AcidRain();
				rain.setTypename(rst.getString(1));
				list.add(rain);	////// ADD를해야지
				//System.out.println(rst.getString(1));	//나중에 인덱스 이름 뽑아보기!!!
				System.out.println(rain.getTypename());
			}
			
			msg.setList(list);
			

			System.out.println("dao 여기까진 왔다 3");
			
		}catch(SQLException e){
			System.out.println("selecttypenameSQL error: " + e);
		}finally{
			JDBCUtil.close(stmt, conn);
		}
		
		return msg;
		
	}
	
	
	
	
	
	
	
	
	
}
