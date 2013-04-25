package devflow.hoseotable;


/**
 * <p>서버와 통신을 위해 작성된 기본 사용자 정보 </p>
 * @author 안계성
 */
public class UserInformation {
	private String _ID;			//학번
	private String _NAME;		//이름
	private String _SEX;			//성
	private String _DEPART;	//학과
	
	public String get_ID() {
		return _ID;
	}
	public void set_ID(String _ID) {
		this._ID = _ID;
	}
	public String get_NAME() {
		return _NAME;
	}
	public void set_NAME(String _NAME) {
		this._NAME = _NAME.substring(1);
	}
	public String get_SEX() {
		return _SEX;
	}
	public void set_SEX(String _SEX) {
		this._SEX = _SEX.substring(1);
	}
	public String get_DEPART() {
		return _DEPART;
	}
	public void set_DEPART(String _DEPART) {
		this._DEPART = _DEPART.substring(1);
	}
	
}
