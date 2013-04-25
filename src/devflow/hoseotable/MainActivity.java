    
/**
 * @author 안계성
 *
 * <b> JSoup 라이브러리에 대한 사항은 <a href="http://jsoup.org/">[여기]</a>를 참조해주세요.</b><br>
 * <b> TwoDScrollView 컨트롤에 대한 사항은 <a href="http://blog.gorges.us/2010/06/android-two-dimensional-scrollview/">[여기]</a>를 참조해주세요.</b><br>
 * 
 */

package devflow.hoseotable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;


public class MainActivity extends Activity implements OnClickListener {

	private AdView adView;
	private static Context mContext;
	public static UserInformation usrInfo;
	private String TargetView = "";
	private TextView NoteTargetView = null;
	private String NoteTargetString = "";
	public String Chapel = "";
	public boolean isLogon = false;
	public boolean isLoginForTable = true;
	public static boolean isAvNew = false;
	
	public static String table_color[] = {"#33B5E5" , "#5cc6ed"}; //, "#4985D6", "#7BA7E1", "#8EB4E6", "#A9C5EB", "#62A9FF", "#75B4FF", "#86BCFF", "#99C7FF", "#A8CFFF", "#7373FF", "#8282FF", "#C88E8E", "#9191FF" , "#A8A8FF"};


    //Activity 내에서 사용하는 지역변수입니다.
	//ProgressDialog dialogLoading;
	//Dialog dialogLoading;
	
	HttpClient httpclient;
	TextView[] tvClass;
	ClassInformation[] ciClasses;
	String UpdateText = "\n* 채플 좌석 조회기능 추가 .\n\n* 위젯 글씨 색상 변경 기능 추가.\n";
	int versionName = 0;
	boolean isLoadedClass = false;
	
    //Activity 관련 메서드입니다.
    @Override
    public void onCreate(Bundle savedInstanceState) {
    //Activity가 생길때 호출됩니다.
        super.onCreate(savedInstanceState);
        boolean isWorked = false;
        mContext = this;
        setContentView(R.layout.main);
        
        
        //dialogLoading.setView(view)
        
        // adView 만들기
        
        usrInfo = new UserInformation();
        adView = new AdView(this, AdSize.SMART_BANNER  , "a1513c503e5149f ");

        // android:id="@+id/mainLayout" 속성이
        // 지정된 것으로 가정하여 LinearLayout 찾기
        LinearLayout layout = (LinearLayout)findViewById(R.id.AdLayout);
        
        
        // 찾은 LinearLayout에 adView를 추가
        layout.addView(adView);

        // 기본 요청을 시작하여 광고와 함께 요청을 로드
        adView.loadAd(new AdRequest());
        
        HttpClient TempClient = getThreadSafeClient();
        TempClient.getParams().setParameter("http.protocol.expect-continue", false);
        TempClient.getParams().setParameter("http.connection.timeout", 1000);
        TempClient.getParams().setParameter("http.socket.timeout", 1000);
        
        try {
			TempClient.execute(new HttpGet("http://query.devflow.kr/hoseo/procupload_drx.php") );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
                
        
        tvClass = new TextView[165];
        
       	 for(int i=0; i<5;i++ ){
    		 for(int j=0;j<16;j++){
    			 int tmpID = getResources().getIdentifier( "txt" + j + "" + i, "id", getPackageName());
    			 tvClass[ Integer.valueOf( j + "" + i ) ] = (TextView)findViewById(tmpID);
    			 tvClass[ Integer.valueOf( j + "" + i ) ].setOnClickListener(this);
    		 }
    	 }
       	 
       	 TextView tvFree = (TextView)findViewById(R.id.txtFree);
       	 
       	tvFree.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
    			Intent ciActivity = new Intent(MainActivity.this, ClassActivity.class);
    			ciActivity.putExtra("classname","자유게시판");
    			ciActivity.putExtra("message","자유게시판 입니다. 자유롭게 이야기하세요.");
    			ciActivity.putExtra("classcode" , "freeboard");
    			ciActivity.putExtra("isBoard" , true);
    			
    			
    			startActivityForResult(ciActivity, 6);
				
			}
		});

		Context context = getApplicationContext();

		SharedPreferences pref = context.getSharedPreferences("hoseotable",
				android.content.Context.MODE_PRIVATE);

		
		String strId = pref.getString("id", "x");
		String strPw = pref.getString("pw", "x");
		
//		if(!(pref.getBoolean("agree", false)) )
//		{
//			Intent AgreeAct = new Intent(MainActivity.this, PrivacyActivity.class);
//			startActivityForResult(AgreeAct, 99);
//			return;
//		}

		if (strId.equals("x") || strPw.equals("x")) { // 아이디 혹은 비밀번호가 기본값이라면
														// 로그인엑티비티 생성
			Intent loginAct = new Intent(MainActivity.this, LoginActivity.class);
			loginAct.putExtra("id", strId.equals("x") ? "" : strId);
			startActivityForResult(loginAct, 1);
			

		} else { // 아니면
			
			try {
				versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0 ).versionCode;
			} catch (NameNotFoundException e1) {
				e1.printStackTrace();
			}
			
			if(pref.getInt("last-version", 0) < versionName){
				CDialog.showAlert(this, UpdateText);
		        SharedPreferences.Editor editor = pref.edit();
				editor.putInt("last-version", versionName);
				editor.commit();
			}
			
			usrInfo.set_ID(pref.getString("id", "x"));
			
			//dialogLoading = ShowDialog("시간표 다운로드중...\n잠시만 기다려 주세요.");// ProgressDialog.show(this, null, "불러오는중..", true);
			CDialog.showLoading(this,"시간표 다운로드중...\n잠시만 기다려 주세요.");

			Handler hdr = new Handler() {
				@Override
				public void handleMessage(Message msg) {

					SharedPreferences pref = MainActivity.mContext
							.getSharedPreferences("hoseotable",
									android.content.Context.MODE_PRIVATE);

					String strId = pref.getString("id", "x");
					String strPw = pref.getString("pw", "x");
					boolean toggleColor = false;

					Long lastUpdate = pref.getLong("cache-date", 0);

					if (System.currentTimeMillis() - lastUpdate >= 86400000 && isNetworkAvailable(MainActivity.mContext) ) { // 86400000(ms) 는 24시간입니다.
						isLoginForTable = true;
						new async_Login().execute(strId, strPw);
						// 24시간 지났을 때에만 시간표를 파싱합니다.
					} else {
						isLoginForTable = false;
						new async_Login().execute(strId, strPw);
						JSONObject cachedTable;
						try {
							cachedTable = new JSONObject(pref.getString(
									"cache", ""));
							JSONObject cachedClass = null;
							TABLE_CLASS tcTemp = null;
							TextView tvTemp = null;
							int tmpID = 0;
							int numYear = new Date().getYear();
							
					        String smt_value = pref.getString("smt", getSmt());
					        
					        String smt_name = "";
					        
					        if(smt_value.equals("10")){
					        	smt_name = "1학기";
					        }else if (smt_value.equals("11")){
					        	smt_name = "하계계절학기";
					        }else if (smt_value.equals("20")){
					        	smt_name = "2학기";
					        }else if (smt_value.equals("21")){
					        	smt_name = "동계계절학기";
					        }
					        
							TextView tvThis = (TextView)findViewById(R.id.tvNow );
							tvThis.setText(pref.getString("year", Integer.toString(numYear+1900)) + "년도\n" + smt_name);

							for (int i = 0; i < 5; i++) {
								for (int j = 0; j < 16; j++) {
									// txt[교시][요일] 입니다. (참고하기 main.xml)
									tmpID = getResources().getIdentifier(
											"txt" + j + "" + i, "id",
											getPackageName());
									// getIdentifier에대한 사항은
									// http://developer.android.com/reference/android/content/res/Resources.html#getIdentifier(java.lang.String,
									// java.lang.String, java.lang.String) 를
									// 참고해주세요.
									tvTemp = (TextView) findViewById(tmpID);
									i++;
									cachedClass = cachedTable
											.getJSONArray(
													i == 1 ? "MON"
															: i == 2 ? "TUE"
																	: i == 3 ? "WED"
																			: i == 4 ? "THU"
																					: i == 5 ? "FRI"
																							: "SAT")
											.getJSONObject(j);
									i--;
									tcTemp = new TABLE_CLASS(cachedClass);
									tvTemp.setText(tcTemp.ClassName);

									if (tcTemp.isExtend)
									{
//										if( tcTemp.ClassName.equals("") ) {
//											tvTemp.setBackgroundColor(Color.parseColor(table_color[toggleColor ? 1 : 0]));
//										}else{
//											toggleColor = !toggleColor;
//											tvTemp.setBackgroundColor(Color.parseColor(table_color[toggleColor ? 1 : 0]));
//										}
										tvTemp.setBackgroundColor(pref.getInt(tcTemp.MetaName , Color.parseColor(table_color[0])));

									}else{
										String tmpString = pref.getString( j + "" + i + "_note" , "");
										tvTemp.setText( tmpString );
										tvTemp.setTextColor(Color.rgb(1 , 1 , 1));
										
									}
									//Color.rgb(51,	181, 229)
									tvTemp.setTag(tcTemp);

									CDialog.hideLoading();

								}
							}
						} catch (JSONException e) { // 데이터가 손상.
							e.printStackTrace();
							//Toast.makeText(MainActivity.this,
							//		"데이터가 손상되었습니다. 다시 불러옵니다.",
							//		Toast.LENGTH_SHORT).show();
							CDialog.ShowToast(MainActivity.this, 2, "데이터가 손상되었습니다. 다시 불러옵니다.");
							new async_Login().execute(strId, strPw);
						}
					}

					// new async_Login().execute(strId, strPw);

				}
			};

			hdr.sendEmptyMessageDelayed(0, 777);

		}

        final TwoDScrollView svTable = (TwoDScrollView)findViewById(R.id.mainscroll);
        final HorizontalScrollView top_col = (HorizontalScrollView)findViewById(R.id.TopCol);
        final ScrollView left_row = (ScrollView)findViewById(R.id.leftCol);

        svTable.setScrollViewListener(new ScrollViewListener() {
        	//시용자 정의된 ScrollViewListener입니다. 스크롤이 바뀔때마다 호출됩니다.
			@Override
			public void onScrollChanged(TwoDScrollView scrollView, int x, int y,
					int oldx, int oldy) {
				top_col.scrollTo(svTable.getScrollX() , svTable.getScrollY());
				left_row.scrollTo(svTable.getScrollX() , svTable.getScrollY());
				//상단열과 좌측행도 mainscroll과 같이 움직이도록 구현합니다.
			}
		});
        

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "설정");
		menu.add( 0, 1, 2, "종료");
		menu.add(0, 2, 1, "의견 제시");

		
		return super.onCreateOptionsMenu(menu);
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case 0:
			Intent stAct = new Intent(MainActivity.this, SettingActivity.class);
			startActivityForResult(stAct, 3);
			break;
		case 1:
			finish();
			break;
		case 2:
			Intent singoIntent = new Intent(MainActivity.this, ReportActivity.class);
			startActivity(singoIntent);
		}
		return super.onOptionsItemSelected(item);
	}



	// 다른 Activity에서 넘어올때 값을 가지고 이리로 옵니다.
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		SharedPreferences pref = mContext.getSharedPreferences("hoseotable",
				android.content.Context.MODE_PRIVATE);
		SharedPreferences.Editor pedit = pref.edit();
		if (resultCode == RESULT_OK) { //잘 반환 됬으면
			if (requestCode == 1) { // 로그인
				
		        //dialogLoading =   ProgressDialog.show( this , null, "로그인중.." ,true);
				//dialogLoading = ShowDialog("인트라넷에 로그인중...\n잠시만 기다려 주세요.");
				CDialog.showLoading(this,"인트라넷에 로그인중...\n잠시만 기다려 주세요.");
		        //로그인진행중인 상황을 다이얼로그로 표시해줍니다.

				new async_Login().execute(data.getStringExtra("id"),		data.getStringExtra("pw"));
				//LoginActivity에서 넘어온 데이터로 로그인을 시도합니다.
				
				Context context = getApplicationContext();

				SharedPreferences.Editor editor = pref.edit();
				editor.putString("id", data.getStringExtra("id"));

				if (data.getBooleanExtra("auto", false)) {
					editor.putString("pw", data.getStringExtra("pw"));
				} else {
					editor.putString("pw", "x");
				}

				editor.commit();

			} else if (requestCode == 2) { // 강의정보
				if( data.getIntExtra("refresh",-1) > -1 ) {
				}else{
					for (int i = 0; i < 5; i++) {
						for (int j = 0; j < 16; j++) {
							int tmpID = getResources().getIdentifier("txt" + j + "" + i, "id",getPackageName());
							TextView tvTemp = (TextView) findViewById(tmpID);
							if ( ((TABLE_CLASS)tvTemp.getTag()).MetaName.equals(TargetView) ) {
								tvTemp.setBackgroundColor( data.getIntExtra("refresh", 000)  );
								Log.d("ActivtyResult", TargetView );
							}
						}
					}
				}
			} else if (requestCode == 3) { // 개인설정
				if(data.getStringExtra("refresh").equalsIgnoreCase("true")){

					Intent refresh = new Intent(this, MainActivity.class);
					startActivity(refresh);
					this.finish();
					
				}
			}else if (requestCode == 4) { // 개인 스케쥴
				
				NoteTargetView.setText(pref.getString(NoteTargetString + "_note" , ""));
				
				
			} else if (requestCode == 99) {
				pedit.putBoolean("agree", true);
				pedit.commit();
			}
			
		}else if(resultCode == RESULT_FIRST_USER){
			finish();
		}else if(resultCode == RESULT_CANCELED) {
			if (requestCode == 1) { //로그인 후 취소되었을때만 Activity를 닫는다.
				finish();
			}else if (requestCode == 2 ) {
				if( data.getIntExtra("refresh",-1) > -1 ) {
				}else{
					for (int i = 0; i < 5; i++) {
						for (int j = 0; j < 16; j++) {
							int tmpID = getResources().getIdentifier("txt" + j + "" + i, "id",getPackageName());
							TextView tvTemp = (TextView) findViewById(tmpID);
							if ( ((TABLE_CLASS)tvTemp.getTag()).MetaName.equals(TargetView) ) {
								tvTemp.setBackgroundColor( data.getIntExtra("refresh", 000)  );
								Log.d("ActivtyResult-Fail", TargetView );
							}
						}
					}
				}
			}else if (requestCode == 4) { // 개인 스케쥴
				
				NoteTargetView.setText(pref.getString(NoteTargetString + "_note" , ""));
				
			}else if (requestCode == 99 ) {
				finish();
			}
			//else { <- 그 외의 사항은 어플리케이션이 종료되지 않아야하기 때문에 닫지않는다.
		}
	}


    //인트라넷 로그인 요청하는 AsyncTask입니다. execute에 ID와 PW를 인수로 받습니다.
    public class async_Login extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                httpclient = getThreadSafeClient();
                
                usrInfo.set_ID(params[0]);
                
                HttpPost httppost = new HttpPost("http://intranet.hoseo.ac.kr/Login");
                //로그인은 POST 형식으로 전송합니다.

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("strCommand", "LOGIN"));
                nameValuePairs.add(new BasicNameValuePair("strId", params[0]));
                nameValuePairs.add(new BasicNameValuePair("strPassword", params[1]));
                //파라미터들을 NameValuePair 리스트에 넣습니다.
                
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //NameValuePair를 post전송 내용에 입력합니다. 필요에 따라 UrlEncodedFormEntity()에 인코딩을 넣어야 할 수 있습니다.

                HttpResponse response = httpclient.execute(httppost);
                //httpost 전송 후, 응답된 내용을 받아옵니다.

                
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "EUC-KR"), 8);

                StringBuilder sb = new StringBuilder();
                sb.append(reader.readLine() + "\n");
                String line = "0"; //NULL 대신 아무 문자나 삽입했습니다.
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                reader.close();
                //받은 내용을 StringBuilder에 한줄씩 꺼내어 넣습니다.
                //[참고] 맨뒤에 새줄(\n, ascii 13)이 삽입됩니다

                return sb.toString();
                // HTML을 반환
                
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) { 
            	//로그인결과
            	if( result.indexOf("초기패스워드") == -1 ) {
            		//편법이지만 충분히 오류없을 것으로 예상됩니다.
            		//실패하면 로그인 화면으로 돌아가거든요.
            		if(isLoginForTable){
	            		new async_Table().execute();          			
            		}else{
            			new async_classinfor().execute();
            			new async_getinfor().execute();
            		}
            	}else{
            		//실패
            		CDialog.hideLoading();
            		Intent loginAct = new Intent(MainActivity.this, LoginActivity.class);
        			startActivityForResult(loginAct, 1);
        			//Toast.makeText(MainActivity.this, "학번 또는 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show();
        			CDialog.ShowToast(MainActivity.this,2, "학번 또는 비밀번호가 잘못되었습니다.");
            	}
            } else {
            	CDialog.hideLoading();
            	CDialog.ShowToast(MainActivity.this,2, "CODE : 0x00e 오류가 발생하였씁니다.");
            	
            }
        }
    }
  
  
    //시간표를 파싱하는 AsyncTask입니다.
    public class async_Table extends AsyncTask<Void, Void, TableInfo> {
        @Override
        protected TableInfo doInBackground(Void... params) {
            try {
            	
            	/* 시간표 출력방식이 변경되었습니다. 각 학년, 학기르 조회할 수 있게되어 어플에도 추가를 하였습니다.
            	 * 
            	 * Sug030rServlet에 인수값이 늘어났습니다.
            	 * 
            	 * strCommand=LIST
            	 *  ㄴ 명령어 이름입니다.
            	 * strYear=2012
            	 *  ㄴ 학년도
            	 * strSmt=10
            	 *  ㄴ 10 : 1학기, 
            	 *    11 : 하계계절학기,
            	 *    20 : 2학기,
            	 *    21 : 동계계절학기
            	 *    
            	 * 2012-12-05 추가함.
            	 * 
            	 * 이전 소스코드
                HttpGet httpGet = new HttpGet("http://intranet.hoseo.ac.kr/Sug030rServlet"); 
                HttpResponse response = httpclient.execute(httpGet);
				*/
            	
            	/*  교시가 15개에서 16개로 늘어났습니다. 확인 & 수정 ; 13-01-15
            	 *    대부분 프로젝트 소스내의 교시 개수 수정.
            	 */
            	
            	//로그인 유지를 위하여 HttpClient를 초기화하지 않습니다.
            	
            	HttpPost httppost = new HttpPost("http://intranet.hoseo.ac.kr/Sug030rServlet"); 

				Context context = getApplicationContext();

				SharedPreferences pref = context.getSharedPreferences("hoseotable",
						android.content.Context.MODE_PRIVATE);

                int numYear = new Date().getYear();
                           
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("strCommand", "LIST"));
                nameValuePairs.add(new BasicNameValuePair("strYear", pref.getString("year", Integer.toString(1900 + numYear) )));
                nameValuePairs.add(new BasicNameValuePair("strSmt",  pref.getString("smt", getSmt() )));
                
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                
                HttpResponse response = httpclient.execute(httppost);
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "EUC-KR"),  8);
                StringBuilder sb = new StringBuilder();
                sb.append(reader.readLine() + "\n");
                String line = "0";
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                reader.close();
                
                int inc_week = 0;
                //ROWSPAN에서 사용하기에 선언해둡니다.
                
                TableInfo bufTable = new TableInfo();
                //시간표를 새로만듭니다.
                
                Element tbody = Jsoup.parse(sb.toString()).getElementsByClass("baseTB").get(0); 
                //시간표 내용이 들어있는 테이블을 가져옵니다. Class 값이 baseTB입니다.
                
                /* getElementsByTag("tr") 는 select("tr")로 대체하여 사용하실 수 있고
                 	또한 getElementsByClass("baseTB")를 select(".basetb")로 대신하실 수 있습니다. */
                
                Elements trs = tbody.getElementsByTag("tr"); 
                //TR은 행기준입니다. 즉 시간표가 행으로만 구분 되어있기때문에 열은 따로 구분해주셔야합니다.
                
                /*
                 * [참고] 테이블 구조		[참고] JSoup에서의 테이블 구조
                 * 
                 *  baseTB					<-- tbody
                 *    ㄴ tr						<-- tbody.trs.get(0)
                 *        ㄴ td				<-- tbody.trs.get(0).get(0)
                 *        ㄴ td				<-- tbody.trs.get(0).get(1)
                 *    ㄴ tr						<-- tbody.trs.get(1)
                 *    ㄴ tr						<-- tbody.trs.get(2)
                 *    
                 */
                
                for(int i = -1;  i <  trs.size() - 2 ; i++ ){ // 교시 ( -1로 초기화 되는 이유는 교시헤더 때문입니다 )
                	//tr의 rowspan 크기만큼 다음행의 교시에서는 이전 수업을 이어받습니다.
                	inc_week = 0;
                	for(Element td : trs.get(i+1).children() ) { //요일 별 해당 시간 수업
                		
                		if(td.className().equalsIgnoreCase("titleTD"))	continue;
                		// 교시헤더(시간표시)는 처리하지 않고 넘어갑니다.
                		
                		do{
                			inc_week++;
                		}while( bufTable.getClass(inc_week, i).isExtend ) ;
                		// 현위치의 강의시간이 isExtend(이미있으면)이면 다음 주로 넘어갑니다.
		
                		if(  td.text().equals("") ){
                			bufTable.putClass(inc_week, i, td.text() ,false, "");
                		}else{
                			bufTable.putClass(inc_week, i, td.text() ,true, td.text());
                    		for(int j = 1; j < Integer.valueOf(td.attr("rowspan")) ; j++ )
                    			bufTable.putClass(inc_week, i + j, "",  true, td.text());
                    		 // 2시간 이상수업일시 다음교시의 isExtend를 true로 설정해줍니다.
                		}
                		
                		// 현위치에 수업을 넣습니다.
                		

                	}
                	
                }

                return bufTable;
                //만들어진 시간표를 반환합니다.
                
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(TableInfo result) {
            if (result != null) {
            	boolean isInput = false;
            	TextView tvTemp = null;
            	TABLE_CLASS tcTemp = null;
            	int  tmpID = 0;
				int numYear = new Date().getYear();

				Context context = getApplicationContext();
				
				SharedPreferences pref = context.getSharedPreferences("hoseotable",	android.content.Context.MODE_PRIVATE);
				
		        String smt_value = pref.getString("smt", getSmt());
		        
		        String smt_name = "";
		        
		        if(smt_value.equals("10")){
		        	smt_name = "1학기";
		        }else if (smt_value.equals("11")){
		        	smt_name = "하계계절학기";
		        }else if (smt_value.equals("20")){
		        	smt_name = "2학기";
		        }else if (smt_value.equals("21")){
		        	smt_name = "동계계절학기";
		        }
		        
				TextView tvThis = (TextView)findViewById(R.id.tvNow );
				tvThis.setText(pref.getString("year", Integer.toString(numYear+1900)) + "년도\n" + smt_name);

				
				if(pref.getInt("last-version", 0) < versionName){
					CDialog.showAlert(MainActivity.this, UpdateText);
			        SharedPreferences.Editor editor = pref.edit();
					editor.putInt("last-version", versionName);
					editor.commit();
				}
            	
            	 for(int i=0; i<5;i++ ){
            		 for(int j=0;j<16;j++){
            			 tcTemp = result.getClass(i+1, j);
            			 //txt[교시][요일]  입니다. (참고하기 main.xml)
            			 tmpID = getResources().getIdentifier( "txt" + j + "" + i, "id", getPackageName());
            			 //getIdentifier에대한 사항은 http://developer.android.com/reference/android/content/res/Resources.html#getIdentifier(java.lang.String, java.lang.String, java.lang.String) 를 참고해주세요.
            			 tvTemp = (TextView)findViewById(tmpID);
            			 tvTemp.setText( tcTemp.ClassName );
            			 
            			 if(!tcTemp.ClassName.equalsIgnoreCase("")) isInput = true;

						if ( tcTemp.isExtend )
						{
							tvTemp.setBackgroundColor(pref.getInt(	tcTemp.MetaName ,	Color.parseColor(table_color[0])));
						}else{
							String tmpString = pref.getString( j + "" + i + "_note" , "");
							tvTemp.setText( tmpString );
							tvTemp.setTextColor(Color.rgb(1 , 1 , 1));
							
						}

            			 tvTemp.setTag(tcTemp);
            			 
            		 }
            	 }
            			 
            			 
            	CDialog.hideLoading();


            	if(!isInput){
            		//Toast.makeText(context, "시간표에 아무것도 없습니다. 설정에서 학기를 선택해보세요." , Toast.LENGTH_LONG).show();
            		CDialog.ShowToast(MainActivity.this,2, "시간표에 아무것도 없습니다.\n메뉴 - 설정에서 학기를 선택해보세요.");
            	}else{

					SharedPreferences.Editor editor = pref.edit();
					editor.putString("cache", result.toJSON().toString());
					editor.putLong("cache-date", System.currentTimeMillis());
					//나중에 빠른로딩을 위해 시간표를 캐쉬저장
					editor.commit();
					new async_classinfor().execute();
					new async_getinfor().execute();
            	}
				
            } else {
            	CDialog.hideLoading();
            	Context context = getApplicationContext();
            	CDialog.ShowToast(MainActivity.this,2, "시간표를 불러오는 중 오류가 발생하였습니다.");
            	//Toast.makeText(context, "시간표를 불러오는 중 오류가 발생하였습니다." , Toast.LENGTH_LONG).show();
            }
        }
    }

    public class async_classinfor extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                Context context = getApplicationContext();
				SharedPreferences pref = context.getSharedPreferences("hoseotable",	android.content.Context.MODE_PRIVATE);
				
                HttpPost httppost = new HttpPost("http://intranet.hoseo.ac.kr/Sug020rServlet");

                int numYear = new Date().getYear();
                
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("strCommand", "LIST"));
                nameValuePairs.add(new BasicNameValuePair("strYear", pref.getString("year", Integer.toString(1900 + numYear) )));
                nameValuePairs.add(new BasicNameValuePair("strSmt",  pref.getString("smt", getSmt() )));
                
                
                
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httppost);
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "EUC-KR"), 8);

                StringBuilder sb = new StringBuilder();
                sb.append(reader.readLine() + "\n");
                String line = "0";
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                reader.close();
	
	           	 Element tbody = Jsoup.parse(sb.toString()).getElementsByClass("baseTB").get(0);
	        	 Elements trs = tbody.getElementsByTag("tr"); 
	        	 
	        	 ciClasses = new ClassInformation[trs.size()]; 

	        	 for(int i = 1; i<trs.size(); i++){
	        		 Element tr = trs.get(i);
	        		 ciClasses[i-1] = new ClassInformation( tr.child(0).text().substring( 0, tr.child(0).text().length()-2) ,tr.child(1).text(),tr.child(2).text(),tr.child(3).text(),tr.child(4).text(),tr.child(5).text(),tr.child(6).text(),tr.child(7).text(),tr.child(8).text(),tr.child(9).text());
	        	 }
	        	 
	        	 // 
	        	 // http://intranet.hoseo.ac.kr/Sur020rServlet
	        	 //
	        	 
	        	 
	        	 HttpGet htpGet = new HttpGet("http://intranet.hoseo.ac.kr/Sur020rServlet");
	        	 
	        	 response = httpclient.execute(htpGet);
	        	 
	             reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "EUC-KR"), 8);

	             sb = new StringBuilder();
				 sb.append(reader.readLine() + "\n");
				 line = "0";
			 	 while ( ( line = reader.readLine() ) != null )
			 	 {
					 sb.append(line + "\n");
				 }
				 reader.close();
				 
				 String cpSeat = Jsoup.parse(sb.toString()).getElementsByClass("dataTDc").get(0).child(0).text();
				 String cpCheck = Jsoup.parse(sb.toString()).getElementsByClass("dataTDc").get(1).child(0).text();

				 Chapel cpObj  = new Chapel();
				 
				 Chapel = cpObj.getSeat( Integer.parseInt( cpSeat )); 
	        	 
                return null;
                
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
//        	
			 TextView tvCha = ((TextView)findViewById(R.id.tvMChapel));
        	 tvCha.setText("* 채플 좌석 : "+Chapel + " [BETA]");
        	 
	       	 for(int i=0; i<5;i++ ){
	    		 for(int j=0;j<16;j++){
	    			 	int tvPos = Integer.valueOf( j + "" + i );
	    				if(tvClass[tvPos] == null) continue;
	            		
	            		TABLE_CLASS tcTemp = (TABLE_CLASS)tvClass[tvPos].getTag();

	            		if(!tcTemp.isExtend) continue;

	    				int dashPos = tcTemp.MetaName.indexOf("-");
	    				String tcRealName = tcTemp.MetaName.substring(0, dashPos );
	    				
	    				for(int seek = 0; seek < ciClasses.length-1; seek++){  		
	    					if(ciClasses[seek].name.equalsIgnoreCase(tcRealName)){
	    						tcTemp.ClsInfor = ciClasses[seek];
	    						break;
	    					}
	    				}

	    		 }
	    	 }
	       	isLoadedClass = true;
        }
    }

    public class async_getinfor extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                HttpGet htpInfoGet = new HttpGet("http://intranet.hoseo.ac.kr/Sud020rServlet");

                HttpResponse response = httpclient.execute(htpInfoGet);
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "EUC-KR"), 8);

                StringBuilder sb = new StringBuilder();
                sb.append(reader.readLine() + "\n");
                String line = "0";
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                reader.close();
                
                Elements dataTDs = Jsoup.parse(sb.toString()).getElementsByClass("dataTD");

                usrInfo.set_NAME(dataTDs.get(1).text());
                usrInfo.set_SEX(dataTDs.get(5).text());
                usrInfo.set_DEPART(dataTDs.get(6).text());
                
                htpInfoGet = new HttpGet("http://query.devflow.kr/hoseo/last-version.txt");
                
                response = httpclient.execute(htpInfoGet);
                
                reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);

                sb = new StringBuilder();
                sb.append(reader.readLine());
                reader.close();
                
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                
                if(Integer.parseInt(sb.toString()) > pInfo.versionCode ){
                	isAvNew = true;
                	
                }
                
                HttpPost httppost = new HttpPost("http://query.devflow.kr/hoseo/register.php");
                
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
                nameValuePairs.add(new BasicNameValuePair("id", usrInfo.get_ID()));
                nameValuePairs.add(new BasicNameValuePair("name", usrInfo.get_NAME() ));
                nameValuePairs.add(new BasicNameValuePair("sex",  usrInfo.get_SEX() ));
                nameValuePairs.add(new BasicNameValuePair("depart", usrInfo.get_DEPART() ));
                nameValuePairs.add(new BasicNameValuePair("phone", Build.MODEL + "/" + Build.DEVICE + "/" + Build.BRAND ));
 
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs , "UTF-8"));
                
                response = httpclient.execute(httppost);
                
                reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);

                sb = new StringBuilder();
                sb.append(reader.readLine() + "\n");
                line = "0";
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
        	if(isAvNew)
        		CDialog.ShowToast(MainActivity.this, 3, "최신버전 업데이트가 있습니다.\n마켓에서 업데이트 해주세요!");
        }
    }
    
    
    public class async_cnctclass extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
            	
                
            	return null;
                
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }
    
	@Override
	public void onClick(View v) {
		if(!isLoadedClass) {
			CDialog.ShowToast(MainActivity.this,1, "잠시 후 다시 시도해주세요.");
			return;
		}
        TextView newTextVew = (TextView) v;
        
        for(TextView tempButton : tvClass)
        {
            if(tempButton == newTextVew )
            {            	
            	TABLE_CLASS tcTemp = (TABLE_CLASS)newTextVew.getTag();
            	if( tcTemp.isExtend ) {
            		if( tcTemp.ClsInfor == null ) {

            			
            		}else{
	        			Intent ciActivity = new Intent(MainActivity.this, ClassActivity.class);
	        			ciActivity.putExtra("classname", tcTemp.ClsInfor.name + "-" + tcTemp.ClsInfor.div_num);
	        			ciActivity.putExtra("classcode", tcTemp.ClsInfor.code );
	        			ciActivity.putExtra("classroom", "강의실 : " + tcTemp.ClsInfor.classroom);
	        			ciActivity.putExtra("credit", tcTemp.ClsInfor.complete_type + " " + tcTemp.ClsInfor.credit + "학점");
	        			ciActivity.putExtra("professor", "교수님 : " + tcTemp.ClsInfor.professor);
	        			ciActivity.putExtra("proemail", tcTemp.ClsInfor.Email);
	        			ciActivity.putExtra("contract", "연락처 : " + tcTemp.ClsInfor.contract);
	        			
	        			if(tcTemp.ClsInfor.name.indexOf("채플") != -1 ){
	        				ciActivity.putExtra("chapel", "채플 좌석 : " + Chapel);
	        			}
	        			
	        			String TEMP_MetaName = ((TABLE_CLASS)newTextVew.getTag()).MetaName;
	        			ciActivity.putExtra("metaname" , TEMP_MetaName);
	        			TargetView = TEMP_MetaName;
	        			
	        			startActivityForResult(ciActivity, 2);
	        			return;
            		}
            	}else{
        			//Toast.makeText(this, "강의정보가 없습니다" , Toast.LENGTH_SHORT).show();
        			//Log.d("aa","aaaa");
        			//	getResources().getResourceEntryName(newTextVew.getId())
            		Intent NoteAct = new Intent(MainActivity.this, NoteActivity.class);
            		NoteAct.putExtra("current" , Integer.parseInt(getResources().getResourceEntryName(newTextVew.getId()).replace("txt" , "") ));
            		NoteTargetView = newTextVew;
            		NoteTargetString = getResources().getResourceEntryName(newTextVew.getId()).replace("txt" , "");
        			startActivityForResult(NoteAct, 4);
        			return;
        			
            	}
            }
        }
	}
	
	public static DefaultHttpClient getThreadSafeClient() {

		DefaultHttpClient client = new DefaultHttpClient();
		ClientConnectionManager mgr = client.getConnectionManager();
		HttpParams params = client.getParams();
		client = new DefaultHttpClient(new ThreadSafeClientConnManager(params,

		mgr.getSchemeRegistry()), params);
		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Hoseo Table");
		return client;
	}
	
	 public static boolean isNetworkAvailable(Context context) {
	     ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	     if (connectivity != null) {
	        NetworkInfo[] info = connectivity.getAllNetworkInfo();
	        if (info != null) {
	           for (int i = 0; i < info.length; i++) {
	              if (info[i].getState() == NetworkInfo.State.CONNECTED) {
	                 return true;
	              }
	           }
	        }
	     }
	     return false;
	  }
	 
	 //학기를 지금 날짜에 맞춰 반환해준다. 단 1,2 학기중 하나.
	 public static String getSmt() {
		 int numMonth = (new Date().getMonth())+1;
		 if(2 < numMonth && 7 > numMonth)
			return "10";
		 else
			return "20";
			 
	 }
	 

//	 public ProgressDialog ShowDialog(String Messagess){
//		 LayoutInflater infl = getLayoutInflater();
//		 View layout = infl.inflate(R.layout.alert_dialog ,(ViewGroup) findViewById(R.id.dialog_layout_id) );
//		 TextView text = (TextView) layout.findViewById(R.id.d_text );
//		 text.setText(Messagess);
// 
//		 ProgressDialog tmp = new Dialog(); //ProgressDialog.show(this, null, Messagess, true);
//		 tmp.setView(layout);
//		 
//		 return tmp;
//
//		 
//	 }
}//MainActivity End