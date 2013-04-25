package devflow.hoseotable;

public class ClassInformation {
 public String name;
 public String code;
 public String div_num;
 public String complete_type;
 public String credit;
 public String grade;
 public String professor;
 public String contract;
 public String Email;
 public String classroom;
 
 //과목명	학수번호	분반	이수구분	학점	성적	담당교수	연락처	이메일	강의실
 
 ClassInformation(String n_name,String n_code,String n_div_num, String n_complete_type, String n_credit, String n_grade, String n_professor, String n_contract,String n_Email, String n_classroom){
	 name = n_name;
	 code = n_code;
	 div_num = n_div_num;
	 complete_type = n_complete_type;
	 credit = n_credit;
	 grade = n_grade;
	 professor = n_professor;
	 contract = n_contract;
	 Email= n_Email;
	 classroom = n_classroom;
 }
}
