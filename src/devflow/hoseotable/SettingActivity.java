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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView.OnEditorActionListener;

public class SettingActivity extends Activity implements AmbilWarnaDialog.OnAmbilWarnaListener {

	private Context mContext;
	private Paint mPaint;
	private int current_color = 0;
	private int old_arg = 0;
	private AmbilWarnaDialog dialog;
	private LinearLayout inClass;
	private LinearLayout noClass;
	private LinearLayout foreColor;
	HttpClient httpclient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_activity);

		mPaint = new Paint();
		mPaint.setColor(Color.BLACK);

		mContext = getApplicationContext();
	
		inClass = (LinearLayout) findViewById(R.id.inclass);
		noClass = (LinearLayout) findViewById(R.id.noclass);
		foreColor = (LinearLayout)findViewById(R.id.foreColor);

		SharedPreferences pref = this.getSharedPreferences("hoseotable",
				android.content.Context.MODE_PRIVATE);

		final int color_noclass = pref.getInt("color_no",
				Color.rgb(125, 125, 125));
		final int color_class = pref.getInt("color_class",
				Color.rgb(68, 193, 240));
		final int color_fore = pref.getInt("color_fore",
				Color.WHITE);	

		inClass.setBackgroundColor(color_class);
		noClass.setBackgroundColor(color_noclass);
		foreColor.setBackgroundColor(color_fore);
		
		foreColor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				current_color = 3;
				dialog = new AmbilWarnaDialog(SettingActivity.this, color_fore, SettingActivity.this);
				dialog.show();
			}
		});

		inClass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				current_color = 1;
				dialog = new AmbilWarnaDialog(SettingActivity.this, color_class, SettingActivity.this);
				dialog.show();
			}
		});

		noClass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				current_color = 2;
				dialog = new AmbilWarnaDialog(SettingActivity.this, color_class, SettingActivity.this);
				dialog.show();
			}
		});
		
		Button btnBan = (Button) findViewById(R.id.btnBan);
		btnBan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				new async_checkban().execute();
				
			}
		});

		Button btnReset = (Button) findViewById(R.id.btnReset);

		btnReset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Context context = getApplicationContext();

				SharedPreferences pref = context.getSharedPreferences(
						"hoseotable", android.content.Context.MODE_PRIVATE);

				SharedPreferences.Editor editor = pref.edit();
				editor.putString("cache", "no-cached");
				editor.putLong("cache-date", 0);
				editor.putString("id", "x");
				editor.putString("pw", "x");

				editor.commit();

				v.setEnabled(false);
			}
		});

		Button btnClearCache = (Button) findViewById(R.id.BtnClearCache);
		btnClearCache.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Context context = getApplicationContext();

				SharedPreferences pref = context.getSharedPreferences(
						"hoseotable", android.content.Context.MODE_PRIVATE);

				SharedPreferences.Editor editor = pref.edit();
				editor.putString("cache", "no-cached");
				editor.putLong("cache-date", 0);

				editor.commit();
				
				Intent Ret1 = new Intent(SettingActivity.this, MainActivity.class);
				Ret1.putExtra("refresh","true");
				setResult(RESULT_OK, Ret1);

				v.setEnabled(false);
			}
		});
		final TextView Alpha_value = (TextView) findViewById(R.id.txtAlphaValue);
		SeekBar alpha_slider = (SeekBar) findViewById(R.id.AlphaSlider);
		int trans_value = pref.getInt("transparent", 0);
		alpha_slider.setProgress(trans_value);
		Alpha_value.setText("투명도 : " + (int) ((double) trans_value * 100 / 255)
				+ "%");
		alpha_slider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				Context context = getApplicationContext();

				SharedPreferences pref = context.getSharedPreferences(
						"hoseotable", android.content.Context.MODE_PRIVATE);

				SharedPreferences.Editor editor = pref.edit();
				editor.putInt("transparent", seekBar.getProgress());

				editor.commit();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				Alpha_value.setText("투명도 : "
						+ (int) ((double) progress * 100 / 255) + "%");

			}
		});
		
		EditText etYear = (EditText) findViewById(R.id.etYear );
		etYear.setText(pref.getString("year", Integer.toString( new Date().getYear() + 1900)));
		
		Spinner smt = (Spinner) findViewById(R.id.Smt );
		
		String[] items = {"1학기", "하계계절학기", "2학기", "동계계절학기"};
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        smt.setAdapter(aa);
        
        String smt_value = pref.getString("smt", getSmt());
        if(smt_value.equals("10")){
        	old_arg = 0;smt.setSelection(0);
        }else if (smt_value.equals("11")){
        	old_arg = 1;smt.setSelection(1);
        }else if (smt_value.equals("20")){
        	old_arg = 2;smt.setSelection(2);
        }else if (smt_value.equals("21")){
        	old_arg = 3;smt.setSelection(3);
        }
	
        etYear.addTextChangedListener(new TextWatcher(){
            @Override
			public void afterTextChanged(Editable s) {
				Context context = getApplicationContext();
				
				SharedPreferences pref = context.getSharedPreferences(
						"hoseotable", android.content.Context.MODE_PRIVATE);

				SharedPreferences.Editor editor = pref.edit();
				editor.putString("year", s.toString());
				editor.putLong("cache-date", 150);

				Intent Ret1 = new Intent(SettingActivity.this, MainActivity.class);
				Ret1.putExtra("refresh","true");
				setResult(RESULT_OK, Ret1);
				
				editor.commit();
            }
            @Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
			public void onTextChanged(CharSequence s, int start, int before, int count){}
        }); 
		smt.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if(arg2 == old_arg) {
					//변경사항이 없으면 저장하지 않는다.
				}else{
					Context context = getApplicationContext();
	
					SharedPreferences pref = context.getSharedPreferences(
							"hoseotable", android.content.Context.MODE_PRIVATE);
	
					SharedPreferences.Editor editor = pref.edit();
					
					switch(arg2){
					case 0:
						editor.putString("smt", "10");
						break;
					case 1:
						editor.putString("smt", "11");
						break;
					case 2:
						editor.putString("smt", "20");
						break;
					case 3:
						editor.putString("smt", "21");
						break;
					}
					
					editor.putLong("cache-date", 150);
					
					Intent Ret1 = new Intent(SettingActivity.this, MainActivity.class);
					Ret1.putExtra("refresh","true");
					setResult(RESULT_OK, Ret1);
					
					editor.commit();
					
					old_arg = arg2;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		

	}


    @Override
    public void onOk(AmbilWarnaDialog dialog, int color) {
		// TODO Auto-generated method stub
		SharedPreferences pref = this.getSharedPreferences("hoseotable",
				android.content.Context.MODE_PRIVATE);

		SharedPreferences.Editor editor = pref.edit();

		if (current_color == 1) { // in
			editor.putInt("color_class", color);
			inClass.setBackgroundColor(color);
		} else if (current_color == 2 )  { // no
			editor.putInt("color_no", color);
			noClass.setBackgroundColor(color);
		}else{ //fore
			editor.putInt("color_fore", color);
			foreColor.setBackgroundColor(color);
		}

		editor.commit();
    }
 
	
	 //학기를 지금 날짜에 맞춰 반환해준다. 단 1,2 학기중 하나.
	 public static String getSmt() {
		 int numMonth = (new Date().getMonth())+1;
		 if(2 < numMonth && 7 > numMonth)
			return "10";
		 else
			return "20";
			 
	 }
	 
	    public class async_checkban extends AsyncTask<String, String, String> {
	        @Override
	        protected String doInBackground(String... params) {
	            try {
	            	httpclient = getThreadSafeClient();
	            	
	                HttpPost httppost = new HttpPost("http://query.devflow.kr/hoseo/check_ban.php");

	                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	                nameValuePairs.add(new BasicNameValuePair("author", MainActivity.usrInfo.get_ID()));

	                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	                HttpResponse response = httpclient.execute(httppost);
	                
	                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);

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
	        	if(result != null){

	        		
	        		JSONArray cmtList;
					
					try {
						cmtList = new JSONObject(result).getJSONArray("result");
						
						if(cmtList.getJSONObject(0).get("reason").equals("none")){
							CDialog.ShowToast(SettingActivity.this, 1, "밴되지 않았습니다.");
						}else{
							CDialog.ShowToast(SettingActivity.this, 1, cmtList.getJSONObject(0).get("reason") + "라는 이유로 " + cmtList.getJSONObject(0).get("date") + "에 밴 되셨습니다. \n\n부당한 밴이시면, 학번과 함께 오류보고로 보내 주시기바랍니다. " );
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						CDialog.ShowToast(SettingActivity.this, 1, "밴되지 않았습니다.");
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
			return client;
		}


		@Override
		public void onCancel(AmbilWarnaDialog dialog) {
			// TODO Auto-generated method stub
			
		}

}
