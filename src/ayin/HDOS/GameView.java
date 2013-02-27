/* author：			YinDongyang
 * modify time：		20110914
 * 
 * 1、线程中变量未加锁    导致OnDraw中可能出现的冲突   比如explosionBmp[9]和moveLeft等
 * 2、触摸效果   真机上稍微点击一下中间就会包括移动动作，而模拟器上不会，所以可以在移动中加入判定。
 * 
 * modift time:		20130226
 * delete ads and compatible to the MX
 */
 
package ayin.HDOS;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {
	Context cont;
    float x=0,y=0,dx=0,dy=0;
    Bitmap scaleBmp[]=new Bitmap[3],brickBmp;
    Bitmap explosionBmp[] = new Bitmap[10];
    GameView gameview;

    float colorM[][]={
			{-1.9804f,2.9804f,0,0,0, -0.4902f ,1.4902f,0,0,0,0.9804f,0.0196f,0,0,0,-15,20,0,0,0},
			{-2.1569f,3.1569f,0,0,0,-0.1373f,1.1373f,0,0,0,-3.5294f,4.5294f,0,0,0,-15,20,0,0,0},
			{1,0,0,0,0,-4,5,0,0,0,-4,5,0,0,0,-15,20,0,0,0},
			{1,0,0,0,0,0,1,0,0,0,-4,5,0,0,0,-15,20,0,0,0},
			{-0.4510f,1.4510f,0,0,0,-3.4706f,4.4706f,0,0,0,0.3137f,0.6863f,0,0,0,-15,20,0,0,0}};

    byte curMap[][] = new byte[7][6];
    byte index=0;
    byte state = 0;
    float density;
    byte moveLeft=3;
    
    float temp=0;
	int XOff,Size;
	float textOff,textSize;
	float scaleXOff,scaleYOff;
	public GameView(Context context,byte index) {
        super(context);
        cont=context;
        gameview=this;
        this.index = index;
        
        moveLeft = data.moves[index];
        for(byte i=0;i<6;i++)
        	for(byte j=0;j<7;j++)
        		curMap[j][i]=data.map[index][j][i];
        
        density = data.density;
        new Thread(new MyThread()).start();
        
        scaleBmp[0] = BitmapFactory.decodeResource(getResources(), R.drawable.a0);
        scaleBmp[1] = BitmapFactory.decodeResource(getResources(), R.drawable.a1);
        scaleBmp[2] = BitmapFactory.decodeResource(getResources(), R.drawable.a2);
        
        brickBmp = BitmapFactory.decodeResource(getResources(), R.drawable.b);
        
        explosionBmp[0] = BitmapFactory.decodeResource(getResources(), R.drawable.c1);
        explosionBmp[1] = BitmapFactory.decodeResource(getResources(), R.drawable.c2);
        explosionBmp[2] = BitmapFactory.decodeResource(getResources(), R.drawable.c3);
        explosionBmp[3] = BitmapFactory.decodeResource(getResources(), R.drawable.c4);
        explosionBmp[4] = BitmapFactory.decodeResource(getResources(), R.drawable.c5);
        explosionBmp[5] = BitmapFactory.decodeResource(getResources(), R.drawable.c6);
        explosionBmp[6] = BitmapFactory.decodeResource(getResources(), R.drawable.c7);
        explosionBmp[7] = BitmapFactory.decodeResource(getResources(), R.drawable.c8);
        explosionBmp[8] = BitmapFactory.decodeResource(getResources(), R.drawable.c9);
        //to solve the bug,maybe not useful
        explosionBmp[9] = BitmapFactory.decodeResource(getResources(), R.drawable.c9);
        
        temp = (data.height-16)/7;
        Size = data.width/6;
        if(Size>temp)Size=(int)temp;
        XOff=(data.width-Size*6)>>1;
        //textSize = data.width/(16*data.density);
        textSize = data.width/16;
        textOff = (data.width-textSize*10)/2;
        //temp = 49*density;
        temp = 49;
        
        scaleXOff=((int)(scaleBmp[0].getWidth()*Size/temp)-(Size<<1))>>1;
        scaleYOff=((int)(scaleBmp[0].getHeight()*Size/temp)-Size)>>1;
        dx=-scaleXOff;
        dy=-scaleYOff;
    }
	long curFrame=0;
	protected void onDraw(Canvas canvas) {
	    canvas.drawColor(0xff333333);
    	for(byte i=0;i<7;i++)
    		for(byte j=0;j<6;j++)
    			if(curMap[i][j]!=-1)
    				canvas.drawBitmap(brickBmp,
    					new Rect((int)(curMap[i][j]*temp),0,(int)(curMap[i][j]*temp+temp),(int)temp),
    					new Rect(XOff+j*Size,i*Size,XOff+j*Size+Size,i*Size+Size),
    					null);
    	//canvas.drawBitmap(scaleBmp[(int)curFrame],dx, dy, null);
    	canvas.drawBitmap(scaleBmp[(int)curFrame],new Rect(0,0,116,63),new RectF(dx,dy,dx+Size*2+scaleXOff*2,dy+Size+scaleYOff*2),null);
    	Paint p = new Paint();
    	p.setColor(0xffffffff);
    	p.setTextSize(textSize);//pixel
    	canvas.drawText("第  "+(index+1)+" 关        "+"剩余 "+moveLeft+" 步", textOff, Size*7+textSize, p);
    	if(state == 1){
    		for(byte i=0;i<30;i++){
    			if(explosionIndex[i]!=9){
    	    		Paint mPaint = new Paint();
    	        	ColorMatrix cm = new ColorMatrix();
    	        	cm.set(colorM[cBMP[i]]);
    	        	mPaint.setColorFilter(new ColorMatrixColorFilter(cm));
    				canvas.drawBitmap(explosionBmp[explosionIndex[i]],
    						new Rect(0,0,49,48),
    						new RectF(XY[i][0],XY[i][1],XY[i][0]+Size,XY[i][1]+Size),mPaint);
    			}
    		}
    	}else if(state==3){
    		data.mainActivity.saveRecord(data.mainActivity.curLevel);
    		((HDOSActivity)cont).showDialog(0);
    		state = 0;
    	}else if(state==4){
    		state = 0;
    		((HDOSActivity)cont).showDialog(1);
    	}
    }
    public boolean check1(){
    	boolean rtn = false;
    	byte i,j;
    	for(j=0;j<7;j++){
    		byte t = 1,temp = curMap[j][0];
    		for(i=1;i<6;i++){
    			if(curMap[j][i]!=temp){
    				temp = curMap[j][i];
    				t = 1;
    			}else t++;
    			if(t>=3&&temp!=-1){
    				curMap[j][i] = -1;
    				new DThread(i,j,temp).start();
    				if(curMap[j][i-1]!=-1){
    					curMap[j][i-1] = -1;
    					new DThread((byte)(i-1),j,temp).start();
    					curMap[j][i-2] = -1;
    					new DThread((byte)(i-2),j,temp).start();
    				}
    				rtn = true;
    			}
    		}
    	}
    	for(i=0;i<6;i++){
    		byte t = 1,temp = curMap[0][i];
    		for(j=1;j<7;j++){
    			if(curMap[j][i]!=temp){
    				temp = curMap[j][i];
    				t = 1;
    			}else t++;
    			if(t>=3&&temp!=-1){
    				curMap[j][i] = -1;
    				new DThread(i,j,temp).start();
    				if(curMap[j-1][i]!=-1){
    					curMap[j-1][i] = -1;
    					new DThread(i,(byte)(j-1),temp).start();
    					curMap[j-2][i] = -1;
    					new DThread(i,(byte)(j-2),temp).start();
    				}
    				rtn = true;
    			}
    		}
    	}
    	return rtn;
    }
    public boolean check2(){
    	boolean rtn = false;
    	for(byte i=0;i<6;i++){
    		byte t = 0;
    		for(byte j=6;j>=0;j--){
    			byte temp = curMap[j][i];
    			if(temp == -1)t++;
    			if(temp!=-1&&t!=0){
    				curMap[j+t][i]=temp;curMap[j][i] = -1;
    				rtn = true;
    			}
    		}
    	}
    	return rtn;
    }
    public boolean CheckSucceed(){
    	byte i=0;
    	for(i=0;i<6;i++)
    		if(curMap[6][i]==-1)
    			continue;
    		else break;
    	if(i==6)return true;
    	else return false;
    }
    Handler myHandler = new Handler() {
        public synchronized void handleMessage(Message msg) {
            switch (msg.what) {
            	case 1:
            		gameview.invalidate();
            		break;
            	default:break;
            }
            super.handleMessage(msg);
        }
    };

    float XY[][] = new float[30][2];
    byte explosionIndex[] = {9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9};
    byte cBMP[]={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    byte num = 0;
    public class DThread extends Thread{
    	float x,y;
    	byte m =0;
    	public DThread(byte a,byte b,byte c){
			num++;
			state = 1;
			while(m<30){
				if(explosionIndex[m]==9){
					cBMP[m] = c;
					explosionIndex[m] = 0;
					XY[m][0] = XOff+a*Size;
					XY[m][1] = b*Size;
					break;
				}else m++;
			}
    	}
		public void run() {
			while (explosionIndex[m] < 9) {
		    	try {
		    		Message message=new Message();
		    		message.what=1;
		    		Thread.sleep(100);
		    		myHandler.sendMessage(message);
		    	} catch (InterruptedException e) {
		    		e.printStackTrace();
		    	}
		    	explosionIndex[m] ++;
			}
			num--;if(num==0)state = 2;
    	}
    }
    public class MyThread implements Runnable{
    	long animFrame[]={0,1,2,1,0};
        int pos=0;
		public void run() {
			while (true) {
		    	try {
		    		Message message=new Message();
		    		message.what=1;
		    		Thread.sleep(200);
		    		pos++;if(pos==5)pos=0;
		    		curFrame=animFrame[pos];
		    		myHandler.sendMessage(message);
		    	} catch (InterruptedException e) {
		    		e.printStackTrace();
		    	}
			}
    	}
    }
    public class Check implements Runnable{	
		public void run() {
			while(check1()){
				while(state !=2);
				if(check2())continue;
				else break;
			}
			while(check2()){
				if(check1())
					while(state !=2);
				else break;
			}
			if(CheckSucceed()){state=3;return;}
			if(moveLeft <= 0)state=4;
    	}
    }
    float xx,yy;
    public boolean onTouchEvent (MotionEvent event){
		x = event.getX();
		y = event.getY();
    	switch (event.getAction())   
    	{ 
    		case MotionEvent.ACTION_DOWN:
    			this.xx = x;
    	        this.yy = y;
    			break;
    		case MotionEvent.ACTION_MOVE:
    			if(y<dy||y>dy+Size)dy=(int)(y/Size)*Size-scaleYOff;
    			if(x<dx)dx=(int)((x-XOff)/Size)*Size-scaleXOff;
    			if(x>dx+Size*2)dx = ((int)((x-XOff)/Size)-1)*Size-scaleXOff;
    			if(dx>XOff+Size*4-scaleXOff)dx = XOff+Size*4-scaleXOff;
    			if(dy>Size*6-scaleYOff)dy = Size*6-scaleYOff;
    			this.invalidate ();
    			break;
    		case MotionEvent.ACTION_UP:
    			if(Math.abs(this.yy - y) < textSize&&Math.abs(this.xx - x)<textSize){
    				if(x>dx+scaleXOff&&x<dx+Size*2+scaleXOff&&y>dy+scaleYOff&&y<dy+Size+scaleYOff){
    					int m,n=(int)(y/Size);
    					if(x-dx>=XOff+Size+scaleXOff)m=(int)((x-XOff)/Size)-1;
    					else m=(int)((x-XOff)/Size);
    					byte t=curMap[n][m];
    					curMap[n][m] = curMap[n][m+1];
    					curMap[n][m+1] = t;
    					if(t!=-1||curMap[n][m]!=-1)
    						moveLeft--;
    					if(moveLeft<=0)moveLeft=0;//to solve the quickly touch -1 bug.
    					this.invalidate ();
    					new Thread(new Check()).start();
    				}
    			}
    			break;  
    	}
    	return true;
    }
}
