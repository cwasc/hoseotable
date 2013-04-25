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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;


public class ClassActivity extends Activity implements AmbilWarnaDialog.OnAmbilWarnaListener {
	private PullToRefreshListView mPullRefreshListView;
	private ArrayList<TweetInfo> mTweetList;
	private Button btnColor;
	private String NoticeMessage = "<strong>이곳에 강의 관련이야기를 나누세요. 선후배사이 존중해주세요!</strong>";
	private static int nColor = 0;
	public static tweetRowAdapter mAdapter;
	public static String ClassCode;
	public static int page = 1;
	private AmbilWarnaDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setContentView(R.layout.class_acitivity);
		
		TextView tvClassName = (TextView)findViewById(R.id.tvClassName);
		TextView tvClassCode = (TextView)findViewById(R.id.tvClassNumber);
		TextView tvClassRoom = (TextView)findViewById(R.id.tvClassRoom);
		TextView tvEmail = (TextView)findViewById(R.id.TvEmail);
		TextView tvProfessor = (TextView)findViewById(R.id.tvProfessor);
		TextView tvContract = (TextView)findViewById(R.id.tvContract);
		TextView tvCredit = (TextView)findViewById(R.id.tvCredit);
		Button btnWrite = (Button)findViewById(R.id.btnWrite);
		btnColor = (Button)findViewById(R.id.btnColor);
		final EditText etComment = (EditText)findViewById(R.id.etMessage);

		
		final Intent MyIntent = this.getIntent();
		
		if(MyIntent.getBooleanExtra("isBoard" , false)) {
			

			((LinearLayout)findViewById(R.id.AllL)).setVisibility(8);
			btnColor.setVisibility(8);
			tvClassCode.setVisibility(8);
			tvClassName.setText(MyIntent.getStringExtra("classname"));
			NoticeMessage = "<strong>" + MyIntent.getStringExtra("message") + "</strong>";
			
		}else{
		
			tvClassName.setText(MyIntent.getStringExtra("classname"));
			tvClassCode.setText(MyIntent.getStringExtra("classcode"));
			tvClassRoom.setText(MyIntent.getStringExtra("classroom"));
			tvEmail.setText(MyIntent.getStringExtra("proemail"));
			tvProfessor.setText(MyIntent.getStringExtra("professor"));		
			tvContract.setText(MyIntent.getStringExtra("contract"));		
			tvCredit.setText(MyIntent.getStringExtra("credit"));	
			
			if(MyIntent.getStringExtra("chapel") != null){
				TextView tvChapel = (TextView)findViewById(R.id.txtChapel);
				tvChapel.setText(MyIntent.getStringExtra("chapel"));
				tvChapel.setVisibility(1);
			}
			
		}
		
		ClassCode = MyIntent.getStringExtra("classcode");

		btnWrite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new procUpload().execute(MainActivity.usrInfo.get_ID(),MyIntent.getStringExtra("classcode"),etComment.getText().toString());
				etComment.setText("");
			}
		});
		
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		


		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh() {
				mPullRefreshListView.setLastUpdatedLabel(DateUtils
						.formatDateTime(getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL));
				page = 1;
				new procGetComment().execute();
			}

			@Override
			public void onPullUpToRefresh() {
				// TODO Auto-generated method stub
				mPullRefreshListView.setLastUpdatedLabel(DateUtils
						.formatDateTime(getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL));
				page++;
				new procGetComment().execute();
			}

		});

		
		ListView actualListView = mPullRefreshListView.getRefreshableView();
				
		mTweetList = new ArrayList<TweetInfo>();
		mAdapter = new tweetRowAdapter(this, R.layout.lst_view_row,
				mTweetList);
		actualListView.setAdapter(mAdapter);
		
		mPullRefreshListView.setMode(Mode.BOTH);
		
		SharedPreferences pref = getSharedPreferences("hoseotable",	android.content.Context.MODE_PRIVATE);
		
		nColor = pref.getInt(MyIntent.getStringExtra("metaname") , Color.rgb(51 , 181 , 229));
				
		btnColor.setBackgroundColor(nColor);
		
		btnColor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				dialog = new AmbilWarnaDialog(ClassActivity.this, nColor , ClassActivity.this);
				dialog.show();
				
			}
		});

		Intent Ret1 = new Intent(ClassActivity.this, MainActivity.class);
		Ret1.putExtra("refresh",nColor );

		setResult(RESULT_OK, Ret1);
		
		new procGetComment().execute();
	}
	

	public class procUpload extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {

			try {
				HttpClient httpclient = getThreadSafeClient();

				HttpPost httpGet = new HttpPost("http://query.devflow.kr/hoseo/send_comment.php");
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
				nameValuePairs.add(new BasicNameValuePair("author", params[0]));
				nameValuePairs.add(new BasicNameValuePair("class", params[1]));
				nameValuePairs.add(new BasicNameValuePair("data", params[2]));				
				
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
					//Toast.makeText(ClassActivity.this, "전송이 완료되었습니다.",
					//	Toast.LENGTH_SHORT).show();
					ShowToast(0,"전송이 완료되었습니다.");
					new procGetComment().execute();
				}else if( result.indexOf("3") != -1){
					//Toast.makeText(ClassActivity.this, "전송에 실패하였습니다.",
					//		Toast.LENGTH_SHORT).show();
					ShowToast(3,"사용자는 서버에서 밴당하셨습니다. \n메뉴-설정-밴조회를 통해서 확인하세요.");
				}else{
					ShowToast(0,"전송에 실패하였습니다.");
				}
				
			}
			super.onPostExecute(result);
		}

	}

	public class procGetComment extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {

			try {
				HttpClient httpclient = getThreadSafeClient();

				HttpPost httpGet = new HttpPost("http://query.devflow.kr/hoseo/list_comment.php");
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("class", ClassCode));
				nameValuePairs.add(new BasicNameValuePair("page", page + ""));
				
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
			try {
				if( result != null ) {
					
					JSONArray cmtList;
				
					cmtList = new JSONObject(result).getJSONArray("comment");
					
					
					
					TweetInfo twt = new TweetInfo();
					
					if(page == 1){
						mAdapter.clear();
						twt.setFrom_user("관리자");
						twt.setText(Html.fromHtml(NoticeMessage));
						twt.setCreated_at("욕설 또는 타인에게 불쾌한 행동을 하시면 글쓰기가 제한됩니다.");
						mTweetList.add(twt);
						
					}
					
					for (int i = 0; i < cmtList.length(); i++) {
						if(cmtList.getJSONObject(i).get("author").equals("blank")) continue;
						TweetInfo tweet = new TweetInfo();
	
						JSONObject c = cmtList.getJSONObject(i);
	
						tweet.setFrom_user(c.getString("author"));
						tweet.setText(Html.fromHtml(c.getString("data")));
						tweet.setCreated_at(c.getString("date"));
						tweet.setSex(c.getString("sex").equals("남") ? false : true);
						mTweetList.add(tweet);
					}
					mAdapter.notifyDataSetChanged();
					mPullRefreshListView.onRefreshComplete();
				}
				super.onPostExecute(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

	 public void ShowToast(int noti_type, String Messagess){
		 LayoutInflater infl = getLayoutInflater();
		 View layout = infl.inflate(R.layout.alert_toast ,(ViewGroup) findViewById(R.id.toast_layout_id) );
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
		 
		 Toast toast = new Toast( getApplicationContext() );
		 toast.setGravity( Gravity.CENTER_VERTICAL , 0, 0);
		 toast.setDuration( Toast.LENGTH_LONG );
		 toast.setView(layout);
		 toast.show();
		 
	 }

	@Override
	public void onCancel(AmbilWarnaDialog dialog)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onOk(AmbilWarnaDialog dialog, int color)
	{
		nColor = color;
		btnColor.setBackgroundColor(color);
		SharedPreferences pref = getSharedPreferences("hoseotable",	android.content.Context.MODE_PRIVATE);
		SharedPreferences.Editor edt = pref.edit();
		Intent MyIntent = this.getIntent();
		
		Intent Ret1 = new Intent(ClassActivity.this, MainActivity.class);
		Ret1.putExtra("refresh",nColor );

		setResult(RESULT_OK, Ret1);

		edt.putInt(MyIntent.getStringExtra("metaname") , nColor);
		edt.commit();
		
	}
	
}
