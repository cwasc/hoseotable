package devflow.hoseotable;


/**
 * <p>������ ����� ���� �ۼ��� �⺻ ����� ���� </p>
 * @author �Ȱ輺
 */
public class UserInformation {
	private String _ID;			//�й�
	private String _NAME;		//�̸�
	private String _SEX;			//��
	private String _DEPART;	//�а�
	
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
