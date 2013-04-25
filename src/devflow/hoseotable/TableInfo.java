package devflow.hoseotable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>강의의 확장<i>(2시간 이상의)</i>과 수업의 정보를 위하여 작성된 Class입니다. </p>
 * @author 안계성
 */
class TABLE_CLASS{
	public boolean isExtend = false; 
	public String ClassName = "";
	public String MetaName = "";
	public ClassInformation ClsInfor = null;
		
	public TABLE_CLASS() {
		
	}
	public TABLE_CLASS(JSONObject jsonData) throws JSONException {
		ClassName = jsonData.getString("ClassName");
		MetaName = jsonData.getString("MetaName");
		isExtend = jsonData.getBoolean("isExtend");
	}
}

/**
 * <p>강의 시간인지 확인을 하기위한 Class 입니다.</p>
 * @author 안계성
 */
class TimeClass {
	public Date startTime;
	public Date endTime;
	
	/**
	 * <p>강의 시간안에 Date가 있는지</p>
	 * @author 안계성
	 * @return 입력받은 Date가 해당 강의시간 내에 있으면 True
	 */
	public boolean isinRange(Date toCheck){
		return toCheck.after(startTime) && toCheck.before(endTime);
	}
	
	/**
	 * <p>1시간 안에 시간이 있는지 </p>
	 * @author 안계성
	 * @return 입력받은 Date가 해당 강의시간 1시간 내에 있으면 True
	 */
	public boolean isinRangeToHour(Date toCheck){
		Calendar toChk = Calendar.getInstance();
		toChk.setTime(startTime);
		toChk.add(Calendar.HOUR, 1);
		Date tmp = toChk.getTime();
		return toCheck.after(startTime) && toCheck.before(tmp);
	}


	/**
	 * <p>String형식의 Date 를 DATE로 만들어줍니다. </p>
	 * @author 안계성
	 * @return 입력받은 String의 형식을 Date형식으로 변환후 반환
	 */
    private Date toDate(String hhmm){
        final String[] hm = hhmm.split(":");
        final GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hm[0]));
        gc.set(Calendar.MINUTE, Integer.parseInt(hm[1]));
        gc.set(Calendar.SECOND, 0);
        gc.set(Calendar.MILLISECOND, 0);
        Date date = gc.getTime();
        return date;
    }

    
	TimeClass(){
		startTime = toDate("00:00");
		endTime = toDate("00:00:00");
	}
	
	TimeClass(String strStartTime, String strEndTime){
		startTime = toDate(strStartTime);
		endTime = toDate(strEndTime);
	}
	
}

/**
 * 호서대의 강의시간이 들어가있는 시간 목록 입니다.
 * @author 안계성
 */
class HoseoTime {
	public TimeClass innerVar[];
	
	HoseoTime() {
		innerVar = new TimeClass[17];
		innerVar[0] = new TimeClass("08:30", "09:20");
		innerVar[1] = new TimeClass("09:30", "10:20");
		innerVar[2] = new TimeClass("10:30", "11:20");
		innerVar[3] = new TimeClass("11:30", "12:20");
		innerVar[4] = new TimeClass("12:30", "13:20");
		innerVar[5] = new TimeClass("13:30", "14:20");
		innerVar[6] = new TimeClass("14:30", "15:20");
		innerVar[7] = new TimeClass("15:30", "16:20");
		innerVar[8] = new TimeClass("16:30", "17:20");
		innerVar[9] = new TimeClass("17:30", "18:20");
		innerVar[10] = new TimeClass("18:20", "19:05");
		innerVar[11] = new TimeClass("19:10", "19:55");
		innerVar[12] = new TimeClass("18:55", "20:40");
		innerVar[13] = new TimeClass("20:45", "21:30");
		innerVar[14] = new TimeClass("21:35", "22:20");
		innerVar[15] = new TimeClass("22:20", "23:05");
		innerVar[16] = new TimeClass("23:05", "23:50");
	}
	
	String getFormatTime(int numClass){
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
		return numClass + "교시 (" + sdf.format(innerVar[numClass].startTime) + " ~ " + sdf.format(innerVar[numClass].endTime) + ")";
	}

}

/**
 * 시간표 Class입니다.
 * @author 안계성
 */
public class TableInfo {
	public TABLE_CLASS MON[];
	public TABLE_CLASS TUE[];
	public TABLE_CLASS WED[];
	public TABLE_CLASS THU[];
	public TABLE_CLASS FRI[];
	public TABLE_CLASS SAT[];
	
	/**
	 * <p>시간표 생성자</p>
	 * @author 안계성
	 */
	public TableInfo(){
		MON = new TABLE_CLASS[16];
		TUE = new TABLE_CLASS[16];
		WED = new TABLE_CLASS[16];
		THU = new TABLE_CLASS[16];
		FRI = new TABLE_CLASS[16];
		SAT = new TABLE_CLASS[16];
		for(int i=0; i<16; i++){
			MON[i] = new TABLE_CLASS();
			TUE[i] = new TABLE_CLASS();
			WED[i] = new TABLE_CLASS();
			THU[i] = new TABLE_CLASS();
			FRI[i] = new TABLE_CLASS();
			SAT[i] = new TABLE_CLASS();
		}
	}
	
	/**
	 * <p>강의를 불러온다 </p>
	 * @author 안계성
	 * @return 요청에 맞는 TABLE_CLASS가 반환됨
	 */
	public TABLE_CLASS getClass(int week, int time){
		switch (week) {
		case 1:
			return MON[time];
		case 2:
			return TUE[time];
		case 3:
			return WED[time];
		case 4:
			return THU[time];
		case 5:
			return FRI[time];
		case 6:
			return SAT[time];
		default:
			return null; //안넣으면 에러퉽퉽
		}
	}
	
	/**
	 * <p>강의를 삽입합니다 </p>
	 * @author 안계성
	 */
	public void putClass(int week, int time, String ClassName, boolean iExtend, String... MetaName){
		switch (week) {
		case 1:
			MON[time].ClassName = ClassName;
			MON[time].isExtend = iExtend;
			MON[time].MetaName = MetaName[0];
			break;
		case 2:
			TUE[time].ClassName= ClassName;
			TUE[time].isExtend = iExtend;
			TUE[time].MetaName = MetaName[0];
			break;
		case 3:
			WED[time].ClassName= ClassName;
			WED[time].isExtend = iExtend;
			WED[time].MetaName = MetaName[0];
			break;
		case 4:
			THU[time].ClassName= ClassName;
			THU[time].isExtend = iExtend;
			THU[time].MetaName = MetaName[0];
			break;
		case 5:
			FRI[time].ClassName= ClassName;
			FRI[time].isExtend = iExtend;
			FRI[time].MetaName = MetaName[0];
			break;
		case 6:
			SAT[time].ClassName= ClassName;
			SAT[time].isExtend = iExtend;
			SAT[time].MetaName = MetaName[0];
			break;
		}
	}

	
	
	/**
	 * <p>시간표를 저장, 전송하기 편하게 하기 위해 JSON으로 파싱하여 반환합니다 </p>
	 * @author 안계성
	 * @return 시간표를 JSONObject로 반환합니다. 
	 * @throws 에러 발생시 StackTrace 출력 후, null이 발생됩니다.
	 */
	public JSONObject toJSON() {
		JSONObject tmp = new JSONObject();
		JSONObject joClass = null;
		JSONArray week = null;
		try {
			for (int i = 1; i < 7; i++) {
				week = new JSONArray();
				for (int j = 0; j < 16; j++) {
					joClass = new JSONObject();
					joClass.put("ClassName", getClass(i, j).ClassName);
					joClass.put("MetaName", getClass(i, j).MetaName);
					joClass.put ("isExtend", getClass(i, j).isExtend);
					week.put(j, joClass);
				}
				tmp.put(i == 1 ? "MON" : i == 2 ? "TUE" : i == 3 ? "WED"
						: i == 4 ? "THU" : i == 5 ? "FRI" : "SAT", week);
			}
			return tmp;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

		
	
}
