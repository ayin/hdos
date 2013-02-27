package ayin.HDOS;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public class selectView extends View {
	Bitmap lockBmp;
	int w,h,l,gap=0,ww=0,tt=0,textSize=0,textXOff=0,textYOff=0,XOff=0;
	public selectView(Context context){
		super(context);
		lockBmp = BitmapFactory.decodeResource(getResources(), R.drawable.lock);
		w=data.width/5;
		h=data.height/7;
		if(w<h)l=w;
		else l=h;
		tt=lockBmp.getHeight();
		ww=tt<<1;
		tt=(tt>>1)+(tt>>2);
		gap=(l-ww)>>1;
		ww=gap+ww;
		XOff=(data.width-5*l)>>1;
		
		textSize=(ww-gap)>>1;
		textXOff=gap+(textSize>>1);
		textYOff=textSize+textXOff;
		this.setBackgroundResource(R.drawable.back);
	}
	protected void onDraw(Canvas canvas) {
		byte i=0,j=0;
		Paint mPaint = new Paint();
		mPaint.setTextSize(textSize);
		for(i=0;i<5;i++)
			for(j=0;j<7;j++){
				mPaint.setColor(0xffffffff);
				canvas.drawRoundRect(new RectF(XOff+gap+i*l,gap+j*l,XOff+i*l+ww,j*l+ww), 5, 5, mPaint);
				mPaint.setColor(0xff333333);
				canvas.drawRoundRect(new RectF(XOff+gap+2+i*l,gap+2+j*l,XOff+i*l+ww-2,j*l+ww-2), 5, 5, mPaint);
				if(i+j*5>data.level)
					canvas.drawBitmap(lockBmp, XOff+l*i+ww-tt, l*j+ww-tt, null);
				else{
					mPaint.setColor(0xff0000ff);
					canvas.drawRoundRect(new RectF(XOff+gap+2+i*l,gap+2+j*l,XOff+i*l+ww-2,j*l+ww-2), 5, 5, mPaint);
				}	
				mPaint.setColor(0xffffffff);
				canvas.drawText(i+j*5+1+"",XOff+l*i+textXOff, l*j+textYOff, mPaint);
			}
	}
	byte index=0;
    float x,y;
    public boolean onTouchEvent (MotionEvent event){
    	switch (event.getAction())   
    	{ 
    		case MotionEvent.ACTION_DOWN:
    			this.x = event.getX();
    	        this.y = event.getY();
    			break;
    		case MotionEvent.ACTION_MOVE:
    			break;
    		case MotionEvent.ACTION_UP:
    			float x1 = event.getX();
    	        float y1 = event.getY();
    			if(Math.abs(this.y - y1) < textSize&&Math.abs(this.x - x1)<textSize){
    				byte a=(byte)((x1-gap-XOff)/l);
    				byte b=(byte)((y1-gap)/l);
    				if(x>XOff+gap+a*l&&x<XOff+ww+a*l&&y>gap+b*l&&y<ww+b*l&&a>=0&a<=4&&b>=0&&b<=6){
    					index=(byte)(a+b*5);
    					data.mainActivity.start(index);
    				}
    				break;
    			}
    			if((Math.abs(this.y - y1) < (data.height>>2)))
    			{
    				if(x1 - this.x > (data.width>>2))
    				{
    					data.mainActivity.setContentView(data.mainActivity.mainView);
    					data.curView = 1;
    				}
    				else if(x1 - this.x < 0-(data.width>>2))
    				{
    					data.mainActivity.setContentView(data.mainActivity.mainView); 
    					data.curView = 1;
    				}
    			}
    			break;
    	}
    	return true;
    }
}