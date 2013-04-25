    
/**
 * @author �Ȱ輺
 *
 * <b> JSoup ���̺귯���� ���� ������ <a href="http://jsoup.org/">[����]</a>�� �������ּ���.</b><br>
 * <b> TwoDScrollView ��Ʈ�ѿ� ���� ������ <a href="http://blog.gorges.us/2010/06/android-two-dimensional-scrollview/">[����]</a>�� �������ּ���.</b><br>
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


    //Activity ������ ����ϴ� ���������Դϴ�.
	//ProgressDialog dialogLoading;
	//Dialog dialogLoading;
	
	HttpClient httpclient;
	TextView[] tvClass;
	ClassInformation[] ciClasses;
	String UpdateText = "\n* ä�� �¼� ��ȸ��� �߰� .\n\n* ���� �۾� ���� ���� ��� �߰�.\n";
	int versionName = 0;
	boolean isLoadedClass = false;
	
    //Activity ���� �޼����Դϴ�.
    @Override
    public void onCreate(Bundle savedInstanceState) {
    //Activity�� ���涧 ȣ��˴ϴ�.
        super.onCreate(savedInstanceState);
        boolean isWorked = false;
        mContext = this;
        setContentView(R.layout.main);
        
        
        //dialogLoading.setView(view)
        
        // adView �����
        
        usrInfo = new UserInformation();
        adView = new AdView(this, AdSize.SMART_BANNER  , "a1513c503e5149f ");

        // android:id="@+id/mainLayout" �Ӽ���
        // ������ ������ �����Ͽ� LinearLayout ã��
        LinearLayout layout = (LinearLayout)findViewById(R.id.AdLayout);
        
        
        // ã�� LinearLayout�� adView�� �߰�
        layout.addView(adView);

        // �⺻ ��û�� �����Ͽ� ����� �Բ� ��û�� �ε�
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
    			ciActivity.putExtra("classname","�����Խ���");
    			ciActivity.putExtra("message","�����Խ��� �Դϴ�. �����Ӱ� �̾߱��ϼ���.");
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

		if (strId.equals("x") || strPw.equals("x")) { // ���̵� Ȥ�� ��й�ȣ�� �⺻���̶��
														// �α��ο�Ƽ��Ƽ ����
			Intent loginAct = new Intent(MainActivity.this, LoginActivity.class);
			loginAct.putExtra("id", strId.equals("x") ? "" : strId);
			startActivityForResult(loginAct, 1);
			

		} else { // �ƴϸ�
			
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
			
			//dialogLoading = ShowDialog("�ð�ǥ �ٿ�ε���...\n��ø� ��ٷ� �ּ���.");// ProgressDialog.show(this, null, "�ҷ�������..", true);
			CDialog.showLoading(this,"�ð�ǥ �ٿ�ε���...\n��ø� ��ٷ� �ּ���.");

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

					if (System.currentTimeMillis() - lastUpdate >= 86400000 && isNetworkAvailable(MainActivity.mContext) ) { // 86400000(ms) �� 24�ð��Դϴ�.
						isLoginForTable = true;
						new async_Login().execute(strId, strPw);
						// 24�ð� ������ ������ �ð�ǥ�� �Ľ��մϴ�.
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
					        	smt_name = "1�б�";
					        }else if (smt_value.equals("11")){
					        	smt_name = "�ϰ�����б�";
					        }else if (smt_value.equals("20")){
					        	smt_name = "2�б�";
					        }else if (smt_value.equals("21")){
					        	smt_name = "��������б�";
					        }
					        
							TextView tvThis = (TextView)findViewById(R.id.tvNow );
							tvThis.setText(pref.getString("year", Integer.toString(numYear+1900)) + "�⵵\n" + smt_name);

							for (int i = 0; i < 5; i++) {
								for (int j = 0; j < 16; j++) {
									// txt[����][����] �Դϴ�. (�����ϱ� main.xml)
									tmpID = getResources().getIdentifier(
											"txt" + j + "" + i, "id",
											getPackageName());
									// getIdentifier������ ������
									// http://developer.android.com/reference/android/content/res/Resources.html#getIdentifier(java.lang.String,
									// java.lang.String, java.lang.String) ��
									// �������ּ���.
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
						} catch (JSONException e) { // �����Ͱ� �ջ�.
							e.printStackTrace();
							//Toast.makeText(MainActivity.this,
							//		"�����Ͱ� �ջ�Ǿ����ϴ�. �ٽ� �ҷ��ɴϴ�.",
							//		Toast.LENGTH_SHORT).show();
							CDialog.ShowToast(MainActivity.this, 2, "�����Ͱ� �ջ�Ǿ����ϴ�. �ٽ� �ҷ��ɴϴ�.");
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
        	//�ÿ��� ���ǵ� ScrollViewListener�Դϴ�. ��ũ���� �ٲ𶧸��� ȣ��˴ϴ�.
			@Override
			public void onScrollChanged(TwoDScrollView scrollView, int x, int y,
					int oldx, int oldy) {
				top_col.scrollTo(svTable.getScrollX() , svTable.getScrollY());
				left_row.scrollTo(svTable.getScrollX() , svTable.getScrollY());
				//��ܿ��� �����൵ mainscroll�� ���� �����̵��� �����մϴ�.
			}
		});
        

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "����");
		menu.add( 0, 1, 2, "����");
		menu.add(0, 2, 1, "�ǰ� ����");

		
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



	// �ٸ� Activity���� �Ѿ�ö� ���� ������ �̸��� �ɴϴ�.
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		SharedPreferences pref = mContext.getSharedPreferences("hoseotable",
				android.content.Context.MODE_PRIVATE);
		SharedPreferences.Editor pedit = pref.edit();
		if (resultCode == RESULT_OK) { //�� ��ȯ ������
			if (requestCode == 1) { // �α���
				
		        //dialogLoading =   ProgressDialog.show( this , null, "�α�����.." ,true);
				//dialogLoading = ShowDialog("��Ʈ��ݿ� �α�����...\n��ø� ��ٷ� �ּ���.");
				CDialog.showLoading(this,"��Ʈ��ݿ� �α�����...\n��ø� ��ٷ� �ּ���.");
		        //�α����������� ��Ȳ�� ���̾�α׷� ǥ�����ݴϴ�.

				new async_Login().execute(data.getStringExtra("id"),		data.getStringExtra("pw"));
				//LoginActivity���� �Ѿ�� �����ͷ� �α����� �õ��մϴ�.
				
				Context context = getApplicationContext();

				SharedPreferences.Editor editor = pref.edit();
				editor.putString("id", data.getStringExtra("id"));

				if (data.getBooleanExtra("auto", false)) {
					editor.putString("pw", data.getStringExtra("pw"));
				} else {
					editor.putString("pw", "x");
				}

				editor.commit();

			} else if (requestCode == 2) { // ��������
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
			} else if (requestCode == 3) { // ���μ���
				if(data.getStringExtra("refresh").equalsIgnoreCase("true")){

					Intent refresh = new Intent(this, MainActivity.class);
					startActivity(refresh);
					this.finish();
					
				}
			}else if (requestCode == 4) { // ���� ������
				
				NoteTargetView.setText(pref.getString(NoteTargetString + "_note" , ""));
				
				
			} else if (requestCode == 99) {
				pedit.putBoolean("agree", true);
				pedit.commit();
			}
			
		}else if(resultCode == RESULT_FIRST_USER){
			finish();
		}else if(resultCode == RESULT_CANCELED) {
			if (requestCode == 1) { //�α��� �� ��ҵǾ������� Activity�� �ݴ´�.
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
			}else if (requestCode == 4) { // ���� ������
				
				NoteTargetView.setText(pref.getString(NoteTargetString + "_note" , ""));
				
			}else if (requestCode == 99 ) {
				finish();
			}
			//else { <- �� ���� ������ ���ø����̼��� ������� �ʾƾ��ϱ� ������ �����ʴ´�.
		}
	}


    //��Ʈ��� �α��� ��û�ϴ� AsyncTask�Դϴ�. execute�� ID�� PW�� �μ��� �޽��ϴ�.
    public class async_Login extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                httpclient = getThreadSafeClient();
                
                usrInfo.set_ID(params[0]);
                
                HttpPost httppost = new HttpPost("http://intranet.hoseo.ac.kr/Login");
                //�α����� POST �������� �����մϴ�.

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("strCommand", "LOGIN"));
                nameValuePairs.add(new BasicNameValuePair("strId", params[0]));
                nameValuePairs.add(new BasicNameValuePair("strPassword", params[1]));
                //�Ķ���͵��� NameValuePair ����Ʈ�� �ֽ��ϴ�.
                
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //NameValuePair�� post���� ���뿡 �Է��մϴ�. �ʿ信 ���� UrlEncodedFormEntity()�� ���ڵ��� �־�� �� �� �ֽ��ϴ�.

                HttpResponse response = httpclient.execute(httppost);
                //httpost ���� ��, ����� ������ �޾ƿɴϴ�.

                
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "EUC-KR"), 8);

                StringBuilder sb = new StringBuilder();
                sb.append(reader.readLine() + "\n");
                String line = "0"; //NULL ��� �ƹ� ���ڳ� �����߽��ϴ�.
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                reader.close();
                //���� ������ StringBuilder�� ���پ� ������ �ֽ��ϴ�.
                //[����] �ǵڿ� ����(\n, ascii 13)�� ���Ե˴ϴ�

                return sb.toString();
                // HTML�� ��ȯ
                
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) { 
            	//�α��ΰ��
            	if( result.indexOf("�ʱ��н�����") == -1 ) {
            		//��������� ����� �������� ������ ����˴ϴ�.
            		//�����ϸ� �α��� ȭ������ ���ư��ŵ��.
            		if(isLoginForTable){
	            		new async_Table().execute();          			
            		}else{
            			new async_classinfor().execute();
            			new async_getinfor().execute();
            		}
            	}else{
            		//����
            		CDialog.hideLoading();
            		Intent loginAct = new Intent(MainActivity.this, LoginActivity.class);
        			startActivityForResult(loginAct, 1);
        			//Toast.makeText(MainActivity.this, "�й� �Ǵ� ��й�ȣ�� �߸��Ǿ����ϴ�.", Toast.LENGTH_SHORT).show();
        			CDialog.ShowToast(MainActivity.this,2, "�й� �Ǵ� ��й�ȣ�� �߸��Ǿ����ϴ�.");
            	}
            } else {
            	CDialog.hideLoading();
            	CDialog.ShowToast(MainActivity.this,2, "CODE : 0x00e ������ �߻��Ͽ����ϴ�.");
            	
            }
        }
    }
  
  
    //�ð�ǥ�� �Ľ��ϴ� AsyncTask�Դϴ�.
    public class async_Table extends AsyncTask<Void, Void, TableInfo> {
        @Override
        protected TableInfo doInBackground(Void... params) {
            try {
            	
            	/* �ð�ǥ ��¹���� ����Ǿ����ϴ�. �� �г�, �б⸣ ��ȸ�� �� �ְԵǾ� ���ÿ��� �߰��� �Ͽ����ϴ�.
            	 * 
            	 * Sug030rServlet�� �μ����� �þ���ϴ�.
            	 * 
            	 * strCommand=LIST
            	 *  �� ��ɾ� �̸��Դϴ�.
            	 * strYear=2012
            	 *  �� �г⵵
            	 * strSmt=10
            	 *  �� 10 : 1�б�, 
            	 *    11 : �ϰ�����б�,
            	 *    20 : 2�б�,
            	 *    21 : ��������б�
            	 *    
            	 * 2012-12-05 �߰���.
            	 * 
            	 * ���� �ҽ��ڵ�
                HttpGet httpGet = new HttpGet("http://intranet.hoseo.ac.kr/Sug030rServlet"); 
                HttpResponse response = httpclient.execute(httpGet);
				*/
            	
            	/*  ���ð� 15������ 16���� �þ���ϴ�. Ȯ�� & ���� ; 13-01-15
            	 *    ��κ� ������Ʈ �ҽ����� ���� ���� ����.
            	 */
            	
            	//�α��� ������ ���Ͽ� HttpClient�� �ʱ�ȭ���� �ʽ��ϴ�.
            	
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
                //ROWSPAN���� ����ϱ⿡ �����صӴϴ�.
                
                TableInfo bufTable = new TableInfo();
                //�ð�ǥ�� ���θ���ϴ�.
                
                Element tbody = Jsoup.parse(sb.toString()).getElementsByClass("baseTB").get(0); 
                //�ð�ǥ ������ ����ִ� ���̺��� �����ɴϴ�. Class ���� baseTB�Դϴ�.
                
                /* getElementsByTag("tr") �� select("tr")�� ��ü�Ͽ� ����Ͻ� �� �ְ�
                 	���� getElementsByClass("baseTB")�� select(".basetb")�� ����Ͻ� �� �ֽ��ϴ�. */
                
                Elements trs = tbody.getElementsByTag("tr"); 
                //TR�� ������Դϴ�. �� �ð�ǥ�� �����θ� ���� �Ǿ��ֱ⶧���� ���� ���� �������ּž��մϴ�.
                
                /*
                 * [����] ���̺� ����		[����] JSoup������ ���̺� ����
                 * 
                 *  baseTB					<-- tbody
                 *    �� tr						<-- tbody.trs.get(0)
                 *        �� td				<-- tbody.trs.get(0).get(0)
                 *        �� td				<-- tbody.trs.get(0).get(1)
                 *    �� tr						<-- tbody.trs.get(1)
                 *    �� tr						<-- tbody.trs.get(2)
                 *    
                 */
                
                for(int i = -1;  i <  trs.size() - 2 ; i++ ){ // ���� ( -1�� �ʱ�ȭ �Ǵ� ������ ������� �����Դϴ� )
                	//tr�� rowspan ũ�⸸ŭ �������� ���ÿ����� ���� ������ �̾�޽��ϴ�.
                	inc_week = 0;
                	for(Element td : trs.get(i+1).children() ) { //���� �� �ش� �ð� ����
                		
                		if(td.className().equalsIgnoreCase("titleTD"))	continue;
                		// �������(�ð�ǥ��)�� ó������ �ʰ� �Ѿ�ϴ�.
                		
                		do{
                			inc_week++;
                		}while( bufTable.getClass(inc_week, i).isExtend ) ;
                		// ����ġ�� ���ǽð��� isExtend(�̹�������)�̸� ���� �ַ� �Ѿ�ϴ�.
		
                		if(  td.text().equals("") ){
                			bufTable.putClass(inc_week, i, td.text() ,false, "");
                		}else{
                			bufTable.putClass(inc_week, i, td.text() ,true, td.text());
                    		for(int j = 1; j < Integer.valueOf(td.attr("rowspan")) ; j++ )
                    			bufTable.putClass(inc_week, i + j, "",  true, td.text());
                    		 // 2�ð� �̻�����Ͻ� ���������� isExtend�� true�� �������ݴϴ�.
                		}
                		
                		// ����ġ�� ������ �ֽ��ϴ�.
                		

                	}
                	
                }

                return bufTable;
                //������� �ð�ǥ�� ��ȯ�մϴ�.
                
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
		        	smt_name = "1�б�";
		        }else if (smt_value.equals("11")){
		        	smt_name = "�ϰ�����б�";
		        }else if (smt_value.equals("20")){
		        	smt_name = "2�б�";
		        }else if (smt_value.equals("21")){
		        	smt_name = "��������б�";
		        }
		        
				TextView tvThis = (TextView)findViewById(R.id.tvNow );
				tvThis.setText(pref.getString("year", Integer.toString(numYear+1900)) + "�⵵\n" + smt_name);

				
				if(pref.getInt("last-version", 0) < versionName){
					CDialog.showAlert(MainActivity.this, UpdateText);
			        SharedPreferences.Editor editor = pref.edit();
					editor.putInt("last-version", versionName);
					editor.commit();
				}
            	
            	 for(int i=0; i<5;i++ ){
            		 for(int j=0;j<16;j++){
            			 tcTemp = result.getClass(i+1, j);
            			 //txt[����][����]  �Դϴ�. (�����ϱ� main.xml)
            			 tmpID = getResources().getIdentifier( "txt" + j + "" + i, "id", getPackageName());
            			 //getIdentifier������ ������ http://developer.android.com/reference/android/content/res/Resources.html#getIdentifier(java.lang.String, java.lang.String, java.lang.String) �� �������ּ���.
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
            		//Toast.makeText(context, "�ð�ǥ�� �ƹ��͵� �����ϴ�. �������� �б⸦ �����غ�����." , Toast.LENGTH_LONG).show();
            		CDialog.ShowToast(MainActivity.this,2, "�ð�ǥ�� �ƹ��͵� �����ϴ�.\n�޴� - �������� �б⸦ �����غ�����.");
            	}else{

					SharedPreferences.Editor editor = pref.edit();
					editor.putString("cache", result.toJSON().toString());
					editor.putLong("cache-date", System.currentTimeMillis());
					//���߿� �����ε��� ���� �ð�ǥ�� ĳ������
					editor.commit();
					new async_classinfor().execute();
					new async_getinfor().execute();
            	}
				
            } else {
            	CDialog.hideLoading();
            	Context context = getApplicationContext();
            	CDialog.ShowToast(MainActivity.this,2, "�ð�ǥ�� �ҷ����� �� ������ �߻��Ͽ����ϴ�.");
            	//Toast.makeText(context, "�ð�ǥ�� �ҷ����� �� ������ �߻��Ͽ����ϴ�." , Toast.LENGTH_LONG).show();
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
        	 tvCha.setText("* ä�� �¼� : "+Chapel + " [BETA]");
        	 
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
        		CDialog.ShowToast(MainActivity.this, 3, "�ֽŹ��� ������Ʈ�� �ֽ��ϴ�.\n���Ͽ��� ������Ʈ ���ּ���!");
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
			CDialog.ShowToast(MainActivity.this,1, "��� �� �ٽ� �õ����ּ���.");
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
	        			ciActivity.putExtra("classroom", "���ǽ� : " + tcTemp.ClsInfor.classroom);
	        			ciActivity.putExtra("credit", tcTemp.ClsInfor.complete_type + " " + tcTemp.ClsInfor.credit + "����");
	        			ciActivity.putExtra("professor", "������ : " + tcTemp.ClsInfor.professor);
	        			ciActivity.putExtra("proemail", tcTemp.ClsInfor.Email);
	        			ciActivity.putExtra("contract", "����ó : " + tcTemp.ClsInfor.contract);
	        			
	        			if(tcTemp.ClsInfor.name.indexOf("ä��") != -1 ){
	        				ciActivity.putExtra("chapel", "ä�� �¼� : " + Chapel);
	        			}
	        			
	        			String TEMP_MetaName = ((TABLE_CLASS)newTextVew.getTag()).MetaName;
	        			ciActivity.putExtra("metaname" , TEMP_MetaName);
	        			TargetView = TEMP_MetaName;
	        			
	        			startActivityForResult(ciActivity, 2);
	        			return;
            		}
            	}else{
        			//Toast.makeText(this, "���������� �����ϴ�" , Toast.LENGTH_SHORT).show();
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
	 
	 //�б⸦ ���� ��¥�� ���� ��ȯ���ش�. �� 1,2 �б��� �ϳ�.
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