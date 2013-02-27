package ayin.HDOS;

import com.adview.AdViewLayout;

import android.app.Activity;
import android.content.Context;
import android.widget.LinearLayout;

public class MyAd{
	int addPoint = 0;
	boolean pointflag = false;
	public MyAd(Context context){
		LinearLayout layout = (LinearLayout)((Activity)context).findViewById(R.id.adLayout); 

		AdViewLayout adViewLayout = new AdViewLayout((Activity)context, "SDK20132226100223tbr1jqkrcz6h9oq");
		layout.addView(adViewLayout); 
		layout.invalidate(); 
	}
}
