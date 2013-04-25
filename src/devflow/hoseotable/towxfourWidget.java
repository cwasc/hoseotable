package devflow.hoseotable;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.RemoteViews;



public class towxfourWidget extends AppWidgetProvider
{
	private static PendingIntent mSender;
	private static AlarmManager mManager;
	private static final int WIDGET_UPDATE_INTERVAL = 60000;
	private static HoseoTime Hoseotime = new HoseoTime();
	
	 @Override
	  public void onReceive(Context context, Intent intent)
	  {
	    super.onReceive(context, intent);
	    
	    String action = intent.getAction();
	    // 위젯 업데이트 인텐트를 수신했을 때
	    if(action.equals("android.appwidget.action.APPWIDGET_UPDATE"))
	    {
	      
	      long firstTime = System.currentTimeMillis() + WIDGET_UPDATE_INTERVAL;
	      mSender = PendingIntent.getBroadcast(context, 0, intent, 0);
	      mManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	      mManager.set(AlarmManager.RTC, firstTime, mSender);
	    }
	    // 위젯 제거 인텐트를 수신했을 때
	    else if(action.equals("android.appwidget.action.APPWIDGET_DISABLED"))
	    {
	    	removePreviousAlarm();
	    }
	  }
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	 {
		appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(
				context, getClass()));
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) // 현재 클래스로 등록된 모든 위젯의 리스트를 가져옴
		{
			int appWidgetId = appWidgetIds[i];

			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
	 }
	 
	public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId)
	 {
	     Date now = new Date();
	     JSONObject cachedTable;
	     
	     boolean isFound = false; 	//해당시간에 수업이 있을 경우
	     int numLastest = 0;			//다음시간을 확인을 위한 마지막으로 확인한 수업 시간
	     
	     RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_2x4_layout);

	     
 		SharedPreferences pref = context.getSharedPreferences("hoseotable",
 				android.content.Context.MODE_PRIVATE);

 		String strCached = pref.getString("cache", "not-cached"); //캐쉬된 시간표를 불러옵니다. 없을시에는 not-cached를 반환
 		
 		int color_noclass = pref.getInt("color_no", Color.rgb(125, 125, 125));
 		int color_class = pref.getInt("color_class", Color.rgb(68, 193, 240));
 		int color_fore = pref.getInt("color_fore", Color.WHITE);
 		
 		int trans_value = 255 - pref.getInt("transparent", 0);
 		
 		String alpha_noclass = String.format("%06X", (0xFFFFFF & color_noclass));
 		String alpha_class = String.format("%06X", (0xFFFFFF & color_class));
 		alpha_noclass = "#" +  String.format("%02x", trans_value) + alpha_noclass;
 		alpha_class = "#" +  String.format("%02x",trans_value) + alpha_class;
 		
 		color_noclass = Color.parseColor(alpha_noclass);
 		color_class = Color.parseColor(alpha_class);
 		
 		updateViews.setInt(R.id.txtClass, "setTextColor" , color_fore);
 		updateViews.setInt(R.id.txtNext, "setTextColor" , color_fore);
 		updateViews.setInt(R.id.txtCap, "setTextColor" , color_fore);
 		
		
 		if( strCached.equalsIgnoreCase("not-cached") || strCached.equals("") ) { 
 			updateViews.setInt(R.id.MainBack, "setBackgroundColor", Color.rgb(125, 125, 125));
			updateViews.setTextViewText(R.id.txtClass, "시간표를 동기화 해주세요.");
 		}else{
 			
			try {
				cachedTable = new JSONObject(strCached);
				Random rd = new Random();
				int dayofweek = now.getDay() + 1; // calendar와 차이가잇음.
				String strDOW =  dayofweek ==  Calendar.MONDAY ? "MON" : dayofweek ==  Calendar.TUESDAY ? "TUE" : dayofweek ==  Calendar.WEDNESDAY ? "WED" : dayofweek == Calendar.THURSDAY ? "THU" : dayofweek == Calendar.FRIDAY  ? "FRI" : "SAT" ;
				
				for(int l = 0; l < 15; l++){
					if(dayofweek == Calendar.SUNDAY ) break; //일요일이면 확인하지않고 나옵니다.
					if( Hoseotime.innerVar[l].isinRangeToHour(now)) { // 시간범위 안 이면,
						JSONObject buffClass = cachedTable.getJSONArray( strDOW ).getJSONObject(l);
						if(buffClass.getBoolean("isExtend")){	
							updateViews.setTextViewText(R.id.txtClass, buffClass.getString("MetaName"));
							updateViews.setTextViewText(R.id.txtCap, Hoseotime.getFormatTime(l));
							numLastest = l;
							isFound=true;
							break;
						}else{ // 지금 시간의 수업이 없다면.
							for(int k = l+1; k < 15; k++){ //이후에 시간을 찾아서
								buffClass = cachedTable.getJSONArray( strDOW ).getJSONObject(k);
								if(buffClass.getBoolean("isExtend")){	 //이후에 수업ㅇㅣ 존재한다면.
									updateViews.setTextViewText (R.id.txtClass, buffClass.getString("MetaName"));
									updateViews.setTextViewText(R.id.txtCap, Hoseotime.getFormatTime(k));
									numLastest = k;
									isFound=true;
									break;								
								}
							}
						}
							
					}
				}

				if(!isFound){
					updateViews.setInt(R.id.MainBack, "setBackgroundColor", color_noclass);
					updateViews.setTextViewText(R.id.txtClass, "더 이상 강의가 없습니다.");
				}else{ //수업이 있으면 다음시간도 있을 수 있음.
					updateViews.setInt(R.id.MainBack, "setBackgroundColor", color_class);
					isFound = false; //변수 재활용
					for(int k = numLastest+1; k < 15; k++){ //이후에 시간을 찾아서
						JSONObject buffClass = cachedTable.getJSONArray( strDOW ).getJSONObject(k);
						if(buffClass.getBoolean("isExtend")){	 //이후에 수업ㅇㅣ 존재한다면.
							//updateViews.setInt(R.id.MainBack, "setBackgroundColor", Color.rgb(255, 120, 11));
							updateViews.setTextViewText(R.id.txtNext, Hoseotime.getFormatTime(k) + " " + buffClass.getString("MetaName") );
																
							isFound=true;
							break;
						}
					}
				}
				
				if(!isFound) {
					updateViews.setTextViewText(R.id.txtNext, "더 이상 강의가 없습니다." );
				}
					

			} catch (JSONException e) { // 데이터가 손상.
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 		}
 		
 		
	     
 		Intent intent = new Intent(Intent.ACTION_MAIN);
 		intent.addCategory(Intent.CATEGORY_LAUNCHER);
 		intent.setComponent(new ComponentName(context, MainActivity.class));
 		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
 		updateViews.setOnClickPendingIntent(R.id.MainBack, pendingIntent);

	     appWidgetManager.updateAppWidget(appWidgetId, updateViews);
	}

	  public void removePreviousAlarm()
	  {
	    if(mManager != null && mSender != null)
	    {
	      mSender.cancel();
	      mManager.cancel(mSender);
	    }
	  }
}
