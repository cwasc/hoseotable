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

import devflow.hoseotable.ClassActivity.procGetComment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

class ViewHolder {
	TextView tvScreenName;
	TextView tvCreatedAt;
	TextView tvText;
	TextView tvDelete;
}

class ViewWrapper{
	View base;
	TextView tvScreenName = null;
	TextView tvCreatedAt = null;
	TextView tvText = null;
	TextView tvDelete = null;
	String strMeta;
	
	ViewWrapper(View base){
		this.base = base;
	}
	
	TextView getText() {
		if(tvText == null){
			tvText = (TextView)base.findViewById(R.id.list_view_row_text);
		}
		return tvText;
	}
	
	TextView getCreatedAt() {
		if(tvCreatedAt == null){
			tvCreatedAt = (TextView)base.findViewById(R.id.list_view_row_create_at);
		}
		return tvCreatedAt;
	}

	TextView getScreenName() {
		if(tvScreenName == null){
			tvScreenName = (TextView)base.findViewById(R.id.list_view_row_user_screen_name);
		}
		return tvScreenName;
	}
	TextView getTvDelete() {
		if(tvDelete == null){
			tvDelete = (TextView)base.findViewById(R.id.tvDelete);
		}
		return tvDelete;
	}
}

class DownHolder {
	String nick;
}

public class tweetRowAdapter extends ArrayAdapter<TweetInfo> {
	private Context mContext;
	@SuppressWarnings("unused")
	private int mResource;
	private ArrayList<TweetInfo> mList;
	@SuppressWarnings("unused")
	private LayoutInflater mInflater;
	private ViewWrapper wrapper = null;
	
	public tweetRowAdapter(Context context, int layoutResource,
			ArrayList<TweetInfo> objects) {
		super(context, layoutResource, objects);
		this.mContext = context;
		this.mResource = layoutResource;
		this.mList = objects;
		this.mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		//ViewHolder holder = null;
		final TweetInfo tweet = mList.get(position);

		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.lst_view_row, null);
			wrapper = new ViewWrapper(convertView);

		} else {
			wrapper = (ViewWrapper)convertView.getTag();
		}

		if (tweet != null) {
			
			wrapper.getScreenName().setText(tweet.getFrom_user());
			if(tweet.getFrom_user().equals("관리자")) wrapper.getScreenName().setVisibility(8);
												else wrapper.getScreenName().setVisibility(0); 
			wrapper.getCreatedAt().setText(tweet.getCreated_at());
			wrapper.getText().setText(tweet.getText());
			wrapper.getCreatedAt().setText(tweet.getCreated_at());
			wrapper.strMeta = tweet.getMetadata(); 
			if(tweet.getFrom_user().equalsIgnoreCase(MainActivity.usrInfo.get_ID())){
				
				wrapper.getTvDelete().setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						new procDeleteComment().execute(MainActivity.usrInfo.get_ID(), ClassActivity.ClassCode, tweet.getText().toString(),tweet.getCreated_at() );
						mList.remove(position);
						ClassActivity.mAdapter.notifyDataSetChanged();
					}
				});
				wrapper.getTvDelete().setVisibility(0);
			}else{
				wrapper.getTvDelete().setVisibility(8);
			}
			
			if(tweet.isSex())
				wrapper.getScreenName().setTextColor(Color.rgb(241, 110, 170));
			else
				wrapper.getScreenName().setTextColor(Color.rgb(0, 174, 240));
			
			

		}

		convertView.setTag(wrapper);
		return convertView;
	}
	

	public class procDeleteComment extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {

			try {
				HttpClient httpclient = getThreadSafeClient();

				HttpPost httpGet = new HttpPost("http://query.devflow.kr/hoseo/delete_comment.php");
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
				nameValuePairs.add(new BasicNameValuePair("author", params[0]));
				nameValuePairs.add(new BasicNameValuePair("class", params[1]));
				nameValuePairs.add(new BasicNameValuePair("data", params[2]));
				nameValuePairs.add(new BasicNameValuePair("date", params[3]));
				
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
					//Toast.makeText(mContext, "삭제가 완료되었습니다.",
					//	Toast.LENGTH_SHORT).show();
					ShowToast(0,"삭제가 완료되었습니다.");
					
				}else{
					//Toast.makeText(mContext, "삭제에 실패하였습니다.",
					//		Toast.LENGTH_SHORT).show();
					ShowToast(0,"삭제에 실패하였습니다.");
				}
				
			}
			super.onPostExecute(result);
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
		 LayoutInflater infl = ((Activity)mContext).getLayoutInflater();
		 View layout = infl.inflate(R.layout.alert_toast ,(ViewGroup)((Activity)mContext).findViewById(R.id.toast_layout_id) );
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
		 
		 Toast toast = new Toast( mContext );
		 toast.setGravity( Gravity.CENTER_VERTICAL , 0, 0);
		 toast.setDuration( Toast.LENGTH_LONG );
		 toast.setView(layout);
		 toast.show();
		 
	 }
	
}

