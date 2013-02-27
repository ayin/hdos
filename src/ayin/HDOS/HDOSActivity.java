package ayin.HDOS;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HDOSActivity extends Activity {
	byte curLevel=0;
	Context cont;
	TextView textView;
	View mainView;
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    cont = this;
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
	    					WindowManager.LayoutParams. FLAG_FULLSCREEN);
	    
	    initGame();
	    
	    LayoutInflater flater = this.getLayoutInflater();
	    mainView = flater.inflate(R.layout.main, null);  
	    textView = (TextView)flater.inflate(R.layout.about, null);
	    //textView.setTextSize(data.width/(16*data.density));
	    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, data.width/16);
	    
	    setContentView(mainView);
	    data.curView= 1 ;

	    Button btn1=(Button)findViewById(R.id.btn1);    
        btn1.setOnClickListener(new View.OnClickListener(){ 
                public void onClick(View v) 
                { 
                	setContentView(new selectView(cont));
                	data.curView= 3;
                } 
        }); 
        Button btn2=(Button)findViewById(R.id.btn2);    
        btn2.setOnClickListener(new View.OnClickListener() 
        { 
                public void onClick(View v) 
                { 
                	textView.setText(R.string.game_info);
                	setContentView(textView);
                	data.curView = 2;
                } 
        }); 
        Button btn3=(Button)findViewById(R.id.btn3);    
        btn3.setOnClickListener(new View.OnClickListener() 
        { 
                public void onClick(View v) 
                { 
                	textView.setText(R.string.us_info);
                	setContentView(textView);
                	data.curView= 2;
                } 
        });
        Button btn4=(Button)findViewById(R.id.btn4);    
        btn4.setOnClickListener(new View.OnClickListener() 
        { 
                public void onClick(View v) 
                { 
                	onDestroy();
            		System.exit(0);
                } 
        });
        
        new MyAd(this);
	}
	float x,y;
	public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction())
        {
        	case MotionEvent.ACTION_DOWN:
        		this.x =  event.getX();
        		this.y = event.getY();
        		break;
        	case MotionEvent.ACTION_MOVE:
        		break;
        	case MotionEvent.ACTION_UP:
        		if(data.curView==1)
        			break;
        		float x =  event.getX();
        		float y = event.getY();
        		if((Math.abs(this.y - y) < (data.height>>2)))
        		{
        			if(x - this.x > (data.width>>2))
        			{
        				setContentView(mainView);
        				data.curView=1;
        			}
        			else if(x - this.x < 0-(data.width>>2))
        			{
        				setContentView(mainView);
        				data.curView=1;
        			}
        		}
        		break;
        	}
        return true;
    }
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//按下键盘上返回按钮
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(data.curView==4){
				setContentView(new selectView(cont));
				data.curView=3;
			}else if(data.curView==1){
				onDestroy();
        		System.exit(0);
			}else{
				setContentView(mainView);
				data.curView=1;
			}
			return true;
		}else{		
			return super.onKeyDown(keyCode, event);
		}
	}
	public void initGame(){
		DisplayMetrics dm=new DisplayMetrics();  
        getWindowManager().getDefaultDisplay().getMetrics(dm); 
        data.width = dm.widthPixels;
        data.height = dm.heightPixels;
        data.density = dm.density;
        data.mainActivity = this;
		
        try{
        	FileInputStream os =this.openFileInput("ayin.txt");
        	data.level=(byte)os.read();
        	if(data.level>35)data.level=0;
        	os.close();
        }catch(IOException e){
        }
	}
	public void saveRecord(byte i){
		if(data.level==34)return;
		if(i>=data.level){
			data.level++;
			try{
				FileOutputStream os = this.openFileOutput("ayin.txt", MODE_PRIVATE);  
				os.write(i+1);
				os.close();
			}catch(IOException e){
			}	
		}
	}
	public void startGameView(byte index){
		setContentView(R.layout.game);
		LinearLayout game = (LinearLayout)findViewById(R.id.game);
		GameView mGameView = new GameView(this,index);
		game.addView(mGameView);
		game.invalidate();
		new MyAd(this);
	}
	
	
	public void start(byte index){
		if(index<=data.level){
			curLevel=index;
			
			startGameView(index);
			//LinearLayout ads = (LinearLayout)findViewById(R.id.adLayout);
			//AdViewLayout adViewLayout = new AdViewLayout(this, "SDK20132226100223tbr1jqkrcz6h9oq"); 
			//ads.addView(adViewLayout); 
			//ads.invalidate(); 
			
			//setContentView(new GameView(this,index));
			data.curView=4;
		}
	}
    protected Dialog onCreateDialog (int id){
    	AlertDialog.Builder builder = new Builder(this);
    	switch(id){
    		case 0:
    			builder.setMessage("进行下一关？") 
    			.setCancelable(false) 
    			.setPositiveButton("确定", new DialogInterface.OnClickListener() { 
    				public void onClick(DialogInterface dialog, int id) {
    					curLevel++;
    					if(curLevel>34)curLevel=0;
    					//setContentView(new GameView(cont,curLevel));
    					startGameView(curLevel);
    				} 
    			}) 
    			.setNegativeButton("取消", new DialogInterface.OnClickListener() { 
    				public void onClick(DialogInterface dialog, int id) { 
    					setContentView(new selectView(cont));
    					data.curView=3;
    				} 
    			});
    		break;
    		case 1:
    			builder.setMessage("重新开始？") 
    			.setCancelable(false) 
    			.setPositiveButton("确定", new DialogInterface.OnClickListener() { 
    				public void onClick(DialogInterface dialog, int id) {
    					//setContentView(new GameView(cont,curLevel));
    					startGameView(curLevel);
    				}
    			}) 
    			.setNegativeButton("放弃", new DialogInterface.OnClickListener() { 
    				public void onClick(DialogInterface dialog, int id) { 
    					setContentView(new selectView(cont));
    					data.curView=3;
    				} 
    			});
    		break;
    		default:break;
    	}
    	AlertDialog alert = builder.create();
    	return alert;
    }
}