import com.acid.rain.dao.AcidRainDAO;
import com.acid.rain.vo.AcidRain;
import com.acid.rain.vo.Message;

public class testDAO {
	
	public testDAO(){
		AcidRain rain = new AcidRain();
		rain.setTypeidx(4);
//		Message msg = new Message();
		
		
		
		AcidRainDAO dao = new AcidRainDAO();
//		msg = dao.selectWordTypeName(rain);
		
		dao.selectWords(rain);
//		System.out.println("msg는 : " + (msg == null ? "null" : "not null"));
		//System.out.println(msg.getAcidrain().getTypename());
		
		
		
	}
	
	public static void main(String[] args){
		new testDAO();
	}
	
}
