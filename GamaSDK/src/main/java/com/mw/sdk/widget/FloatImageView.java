package com.mw.sdk.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.mw.sdk.callback.FloatViewMoveListener;

public class FloatImageView extends androidx.appcompat.widget.AppCompatImageView {

	private Context context;

	public FloatImageView(Context context) {
		super(context);
		this.context = context;
	}
	
	public FloatImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	
	public FloatImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	boolean move=false;//是否已经移动
	float basex,basey;//整个移动过程中初始位置的横纵坐标
	private long starTime;//移动开始时间
	private long endTime;//移动的结束时间
	
	
	int dexX,dexY=0;//整个移动过程中横纵坐标移动的位置
	
	@Override
	public synchronized boolean onTouchEvent(MotionEvent event){
		
		float movex,movey=0;//相对移动坐标
		final int action = event.getAction();  //玩家的手势动作
		float starx=0;//移动过程中某一时刻view的横坐标
		float stary=0;//移动过程中某一时刻view的纵坐标
		switch (action) {  
		 	case MotionEvent.ACTION_DOWN:  
		 		
		 		if(!move){
		 			
		 			getStarLocal();
		 			move=true;
		 			basex=starx=event.getX();
		 			basey=stary=event.getY();
		 			movex=movey=0;
		 			starTime=event.getEventTime();
		 			endTime=0;
		 			dexX=dexY=0;
		 			
		 		}
		 		break;  
		  
		    case MotionEvent.ACTION_MOVE:  
		    	
		    	if(!allowScrolling){
		    		allowScrolling=true;
		    		
		 		}else{
		 			
		 			movex=event.getX()-starx;
		 			movey=event.getY()-stary;
		 			dexX+=movex;
		 			dexY+=movey;
		 			starx=event.getX();
		 			stary=event.getY();
		 			if(allowMove)
		 				updateWindowPosition((int)(movex-basex),(int)(movey-basey),false);
		 			allowScrolling=false;
		 		}
		        break;  
		          
		    case MotionEvent.ACTION_UP:  
		    	
		    	movex=event.getX()-starx;
	 			movey=event.getY()-stary;
	 			dexX+=movex;
	 			dexY+=movey;
	 			endTime=event.getEventTime();
	 			getEndLocal();
	 			if(allowMove){
	 				updateWindowPosition((int)(movex-basex),(int)(movey-basey),true);
	 			}
	 			starx=stary=0;
	 			movex=movey=0;
	 			move=false;
	 			
	 			if(Math.abs(endTime-starTime)<200){
	 				
	 				if(Math.abs(endLocal[0]-starLocal[0])<2&&Math.abs(endLocal[1]-starLocal[1])<2)
	 					return super.onTouchEvent(event);
	 			}
	 			
	 			return true;
		
		    }  
		    
		return super.onTouchEvent(event);   
		
	}
	
	FloatViewMoveListener listener;

	//设置监听事件
	public void setFloatViewMoveListener(FloatViewMoveListener listener){
		this.listener=listener;
	}

	private boolean allowMove=true;
	//是否允许windowmanager移动
	public void allowMove(boolean allowMove){
		this.allowMove=allowMove;
	}
	
	private boolean allowScrolling=false;
	//修改windowmanager的坐标
	public synchronized void updateWindowPosition(int x,int y,boolean isEndMove){
		
		if(isEndMove){
			
				int local=0;
				if(endLocalUsed){
					local=endLocal[0];
				}else{
					local=0;
				}
				
				if(local< sceenWidth /2){
					listener.move(this,0, y,true);
					
				}
				else{
					listener.move(this, sceenWidth, y,true);
					
				}
				endLocalUsed=false;
			
		}else{
			listener.move(this,x, y,false);
		}

	}
	
	int[] starLocal=new int[2];
	private void getStarLocal(){
		this.getLocationOnScreen(starLocal);
		
	}
	
	private boolean endLocalUsed=false;
	int[] endLocal=new int[2];
	private void getEndLocal(){
		endLocalUsed=true;
		this.getLocationOnScreen(endLocal);
		
	}

	public void setSceenHeight(int sceenHeight) {
		this.sceenHeight = sceenHeight;
	}

	public void setSceenWidth(int sceenWidth) {
		this.sceenWidth = sceenWidth;
	}

	private int sceenWidth =0;
	private int sceenHeight = 0;

}

