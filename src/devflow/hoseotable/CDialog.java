/*
 *  Custom ProgressDialog & AlertDialog의 사용을 위한 클래스입니다.
 *  Author : 안계성
 */

package devflow.hoseotable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CDialog {
	private static Dialog m_loadingDialog = null;
	private static Dialog m_alertDialog = null;
	
	
	
	public static void showAlert(Context context, String Message) {
		if( m_alertDialog == null ){
			m_alertDialog = new Dialog(context, R.style.TransDialog);

			LayoutParams params = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(10,10,10,10);
			lp.gravity = Gravity.CENTER;
			
			
			TextView tvMessage = new TextView(context);			
			tvMessage.setText(Message);
			tvMessage.setLayoutParams(lp);
			
			ImageView ivIcon = new ImageView(context);
			ivIcon.setImageResource(R.drawable.ic_menu_notifications);
			ivIcon.setLayoutParams(lp);
			
			TextView tvClose = new TextView(context);	
			tvClose.setText("[ 이곳을 터치하여 닫기 ]");
			tvClose.setLayoutParams(lp);
			
			
			LinearLayout ll = new LinearLayout(context);
			ll.setBackgroundColor(Color.rgb(125, 125, 125));
			ll.setOrientation(1);
			ll.addView(ivIcon);
			ll.addView(tvMessage);
			ll.addView(tvClose);
			
			ll.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CDialog.hideAlert();
				}
			});

			m_alertDialog.addContentView(ll, params);
			m_alertDialog.setCancelable(true);
					
			
		}
		m_alertDialog.show();
		
	}
	
	public static void showLoading(Context context, String Message) {
		if (m_loadingDialog == null) {
			m_loadingDialog = new Dialog(context, R.style.TransDialog);
			ProgressBar pb = new ProgressBar(context);
			pb.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.rotate));
			TextView tvMessage = new TextView(context);
			tvMessage.setText(Message);
			LayoutParams params = new LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			LinearLayout ll = new LinearLayout(context);
			ll.setOrientation(1);
			ll.addView(pb);
			ll.addView(tvMessage);
			m_loadingDialog.addContentView(ll, params);
			m_loadingDialog.setCancelable(false);
		}

		m_loadingDialog.show();
	}
	
	public static void hideLoading() {
		if (m_loadingDialog != null) {
			m_loadingDialog.dismiss();
			m_loadingDialog = null;
		}
	}
	
	public static void hideAlert() {
		if (m_alertDialog != null) {
			m_alertDialog.dismiss();
			m_alertDialog = null;
		}
	}
	 public static void ShowToast(Activity context, int noti_type, String Messagess){
		 LayoutInflater infl = context.getLayoutInflater();
		 View layout = infl.inflate(R.layout.alert_toast ,(ViewGroup)context.findViewById(R.id.toast_layout_id) );
		 TextView text = (TextView) layout.findViewById(R.id.text );
		 text.setText(Messagess);
		 ImageView icons = (ImageView)layout.findViewById(R.id.imgnType);
		 
		 switch(noti_type){
		 case 1:
			 icons.setImageResource(R.drawable.indicator_input_error);
		 case 2:
			 icons.setImageResource(R.drawable.indicator_input_error);
		 case 3:
		 }
		 
		 Toast toast = new Toast( context.getApplicationContext() );
		 toast.setGravity( Gravity.CENTER_VERTICAL , 0, 0);
		 toast.setDuration( Toast.LENGTH_LONG );
		 toast.setView(layout);
		 toast.show();
		 
	 }
	
}

