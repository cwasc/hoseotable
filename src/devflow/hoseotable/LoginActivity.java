package devflow.hoseotable;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		SharedPreferences pref = getApplicationContext().getSharedPreferences("hoseotable",
				android.content.Context.MODE_PRIVATE);

//		Intent Privacyactivity = new Intent(LoginActivity.this, PrivacyActivity.class);
//		startActivityForResult(Privacyactivity, 1);
		
		Intent MyIntent = this.getIntent();

		final EditText tID = (EditText)findViewById(R.id.txtID);
		tID.setText(MyIntent.getStringExtra("id"));

		Button button = (Button)findViewById(R.id.btnLogin);
		button.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			
			EditText tPW = (EditText)findViewById(R.id.txtPW);
			CheckBox chkAuto = (CheckBox)findViewById(R.id.chkAuto);
						
			Intent Ret1 = new Intent(LoginActivity.this, MainActivity.class);
			Ret1.putExtra("id",tID.getText().toString());
			Ret1.putExtra("pw", tPW.getText().toString());
			Ret1.putExtra("auto", chkAuto.isChecked());

			InputMethodManager inputManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(tPW.getWindowToken(),0);
			//화상 키보드를 내립니다.
			
			setResult(RESULT_OK, Ret1);

			finish();
			
		}});
	}
	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
//		if (resultCode == RESULT_OK) {
//			if (requestCode == 1) {
//
//
//			}else{
//				setResult(RESULT_FIRST_USER);
//				finish();
//			}
//			
//		}else{
//			setResult(RESULT_FIRST_USER);
//			finish();
//		}
//	}


}
