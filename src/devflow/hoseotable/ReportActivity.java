package devflow.hoseotable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ReportActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_activity);
		
		Button btnSend = (Button)findViewById(R.id.btnSend);
		final EditText etEmail = (EditText)findViewById(R.id.etEmail);
		final EditText etContent = (EditText)findViewById(R.id.etContent);
		
		
		btnSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.setEnabled(false);
				try {
					PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
					new procUpload().execute(MainActivity.usrInfo.get_ID(), etEmail.getText().toString(), etContent.getText().toString(), "v" + pInfo.versionName + "(build " + pInfo.versionCode + ")");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
	}
	
	public class procUpload extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {

			try {
				HttpClient httpclient = getThreadSafeClient();

				HttpPost httpGet = new HttpPost("http://query.devflow.kr/hoseo/sned_report_alpha.php");
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
				nameValuePairs.add(new BasicNameValuePair("user", params[0]));
				nameValuePairs.add(new BasicNameValuePair("email", params[1]));
				nameValuePairs.add(new BasicNameValuePair("content", params[2]));
				nameValuePairs.add(new BasicNameValuePair("data", params[3]));
				
				httpGet.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
				
				HttpResponse response = httpclient.execute(httpGet);

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								response.getEntity().getContent(), "UTF-8"),8);
				
				StringBuilder sb = new StringBuilder();
				sb.append(reader.readLine() + "\n");
				String line = "0";
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				
				reader.close();
				return sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			if( result != null ) {
				if(result.indexOf("1") != -1 ){
//				Toast.makeText(ReportActivity.this, "전송이 완료되었습니다. 소중한 의견 감사합니다.",
//						Toast.LENGTH_SHORT).show();
					CDialog.ShowToast(ReportActivity.this , 3, "전송이 완료되었습니다.\n소중한 의견 감사합니다.");
				}else{
					CDialog.ShowToast(ReportActivity.this , 1, "전송이 실패하였습니다.");
				}
				
			}
			super.onPostExecute(result);
			finish();
		}

	}
	
	public static DefaultHttpClient getThreadSafeClient() {

		DefaultHttpClient client = new DefaultHttpClient();
		ClientConnectionManager mgr = client.getConnectionManager();
		HttpParams params = client.getParams();
		client = new DefaultHttpClient(new ThreadSafeClientConnManager(params,

		mgr.getSchemeRegistry()), params);
		return client;
	}

}
