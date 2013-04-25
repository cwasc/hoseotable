package devflow.hoseotable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class NoteActivity extends Activity
{

	private Context mContext = null;
	private int iClass = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_activity);
		
		final Intent MyIntent = this.getIntent();
		mContext = this.getApplicationContext();
		
		iClass = MyIntent.getIntExtra("current" , 0);
		
		final SharedPreferences pref = mContext.getSharedPreferences("hoseotable",
				android.content.Context.MODE_PRIVATE);
		final SharedPreferences.Editor pedit = pref.edit();
			
		final EditText etNote = (EditText)findViewById(R.id.etNote);
		Button btnSave = (Button)findViewById(R.id.btnSave);
		
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{				
				pedit.putString(iClass + "_note" , etNote.getText().toString());
				pedit.commit();
				
				CDialog.ShowToast(NoteActivity.this , 0 , "저장하였습니다.");
								
			}
		});
		
		etNote.setText(pref.getString(iClass + "_note" , ""));
		
		
		
		
	}
	

}
