package ndhu.csie.flood;

import ndhu.csie.project.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;


public class CSE1F extends Activity{	
	int start=0;//設定開始
	int stop=0;//設定暫停		
	int touch=0;//設定觸摸			
	private float mapX ;//X座標點
	private float mapY ;//Y座標點	
	int from;	//
	
	int QRfloor;
	String temp;		//暫存QRcode所有資訊
	String tempCheck;	//判斷QRcode是否是自己設計的
	String tempX;		//暫存QRcodeX座標值
	String tempY;		//暫存QRcodeY座標值
	String tempZ;		//暫存QRcode樓層位置
	String tempClass;	//暫存QRcode教室資訊
	Intent intent = new Intent();
	
	int winW,winH;//取得螢幕長寬	
	float winw,winh;//暫存螢幕長寬 用來縮放圖片	
	int bitmapw,bitmaph;//取得圖片長寬	
	float matrixw,matrixh;//矩陣換算質
	
	double startTime =0 ;//起始時間
	
	MyView mMyView = null;
	public void onCreate(Bundle s){
		super.onCreate(s);
		startTime = System.currentTimeMillis();
		//設置全屏顯示
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//取得螢幕大小
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();   
		winW  = display.getWidth();  
		winH = display.getHeight();
		//執行MyView
		mMyView = new MyView(this);
		setContentView(mMyView);
		//
		Bundle bundle = this.getIntent().getExtras();
	    from = bundle.getInt("from");
	    if(from == 0){ }
	    if(from == 1){
	    	temp = bundle.getString("temp");
	    	tempX = bundle.getString("tempX");
	    	tempY = bundle.getString("tempY");
	    	tempZ = bundle.getString("tempZ");
	    	tempClass = bundle.getString("tempClass");
	    	QRfloor = bundle.getInt("QRfloor");

	    	mapX = Float.valueOf(tempX);				//回傳X座標
	    	mapY = Float.valueOf(tempY);				//回傳Y座標
	    	start = 1;
	    	
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(tempClass);
			builder.setTitle("你的位置");
			builder.setPositiveButton("OK",new DialogInterface.OnClickListener(){      			 
				@Override
				public void onClick(DialogInterface dialog, int which) {}
			}).show();
	    }
		}	
	public class MyView extends SurfaceView implements Callback,Runnable,SensorEventListener{
		Paint mPaint=null;//繪圖 筆刷		
		SurfaceHolder mSurfaceHolder=null;//多重繪圖加速		
		Canvas mCanvas=null;//繪圖 畫布	
		Canvas mapCanvas=null;
		boolean mIsRunning=false;//是否在執行
		//取得Sensor資料
		private SensorManager mSersorManager=null;
		Sensor mSensor=null;	
		//切換樓層時使用
		private Bitmap maparrow;
		
		private Bitmap bitmap;//畫布	
		private Bitmap backmap,flootmap;//圖片加載
		private Bitmap newmap;
						
		double spentTime=0,spentTime1=0,spentTime2=0,v=0,s1=0,s2=0,S=0,startTime,t;
		double Fa=0,Fa1=0,Fa2=0,mFa=0,outax=0,outay=0,outaz=0,pi2=0.0175;
		//重力 磁場 感應 X軸Y軸
		private float ax,ay,az,ox,oz,oy;	
		private float imageX = 0f;
        private float imageY = 0f;
		
        public MyView(Context context){	
        	super(context);		
        	setFocusable(true);//設置View得控制焦點		
			setFocusableInTouchMode(true);//設置View獲得觸摸屏事件
			//得到SurfaceHolder
			mSurfaceHolder = getHolder();
			mSurfaceHolder.addCallback(this);
			//設定繪圖畫布
			bitmap = Bitmap.createBitmap(winW,winH, Bitmap.Config.ARGB_4444);
			//底層圖片
			backmap = BitmapFactory.decodeResource(getResources(),R.drawable.back);
			flootmap = BitmapFactory.decodeResource(getResources(),R.drawable.csea);
			maparrow = BitmapFactory.decodeResource(getResources(),R.drawable.ball);

 			//繪圖
    		mCanvas=new Canvas();         
    		mCanvas.setBitmap(bitmap);  
    		mapCanvas=new Canvas();         
    		mapCanvas.setBitmap(bitmap);  
    		
    		mPaint = new Paint();           
    		mPaint.setColor(Color.RED);
    		//取得圖片大小
			bitmapw = flootmap.getWidth();			
			bitmaph = flootmap.getHeight();
			//放大縮小		
			matrixw=winW /bitmapw;
			matrixh=winH/bitmaph;
			Matrix matrix = new Matrix();
			matrix.postScale(matrixh, matrixh);
			newmap = Bitmap.createBitmap(flootmap, 0, 0, bitmapw, bitmaph, matrix, true);
			//得到SensorManagery資料/
			mSersorManager =(SensorManager)getSystemService(SENSOR_SERVICE);
			mSensor = mSersorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			mSersorManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_GAME);
			mSersorManager.registerListener(this,mSersorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_UI);
        }
        
		private void Draw(){
			if(stop == 0){
				if(start == 1){
					mCanvas.drawBitmap(maparrow, mapX*matrixh,mapY*matrixh, mPaint);
					mapCanvas.drawCircle(mapX+10, mapY+10, 3, mPaint);
				}
			}
		}
		private void Drawball(){
			if(oz>30&&oz<120){maparrow = BitmapFactory.decodeResource(getResources(),R.drawable.ball4);}
			 if(oz>300||oz<30) {maparrow = BitmapFactory.decodeResource(getResources(),R.drawable.ball2);}
			 if(oz>120&&oz<210) {maparrow = BitmapFactory.decodeResource(getResources(),R.drawable.ball3);}
			 if (oz>210&&oz<300){maparrow = BitmapFactory.decodeResource(getResources(),R.drawable.ball);}
		}
		private void Drawtext(){
			//繪製背景
			mCanvas.drawBitmap(backmap, 0, 0, null);
			mCanvas.drawBitmap(newmap, 0, 0, null);
			mCanvas.drawBitmap(bitmap, 0, 0, null);

			mCanvas.drawText("X"+mapX, (float) (winW*0.8), 20, mPaint);
			mCanvas.drawText("Y"+mapY, (float) (winW*0.8), 40, mPaint);
			mCanvas.drawText("總路徑長"+S, (float) (winW*0.8), 60, mPaint);
		}
		
		public void run() {
			if(stop%2 == 0){
				while(mIsRunning){
					synchronized(mSurfaceHolder){
						mCanvas = mSurfaceHolder.lockCanvas();
						
						Drawball();
						Drawtext();
						Draw();
						
						mSurfaceHolder.unlockCanvasAndPost(mCanvas);
						}
					}
				}
			}
		public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {}		
		public void surfaceCreated(SurfaceHolder holder) {
			mIsRunning = true;
			new Thread(this).start();
		}		
		public void surfaceDestroyed(SurfaceHolder holder) {
			mIsRunning = false;
		}		
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
        
		public boolean onTouchEvent(MotionEvent event) {
        	if((start == 1 || start == 2) && stop == 0 && touch == 0){
	            if(event.getAction() == MotionEvent.ACTION_DOWN){
	                imageX = event.getX();
	                imageY = event.getY();
	                start = 1;
	                touch = 1;
	            }
	            /*else if(event.getAction() == MotionEvent.ACTION_MOVE){
	                imageX = event.getX();
	                imageY = event.getY();
	            }*/
	            else if(event.getAction() == MotionEvent.ACTION_UP){
	                imageX = event.getX();
	                imageY = event.getY();
	            }
	            mapX = imageX / matrixh;
	            mapY = imageY / matrixh;
        	}
         return true;
        }		
		public void onSensorChanged(SensorEvent event) {
			if(start == 1 && stop == 0){
				//兩個應該反過來
				 switch (event.sensor.getType()) {
	             case Sensor.TYPE_ACCELEROMETER:
	             	ax = event.values[0];
	             	ay = event.values[1];
	             	az = event.values[2];
	                 break;
	             case Sensor.TYPE_ORIENTATION:
	             	oz = event.values[0];
	             	ox = event.values[1];
	             	oy = event.values[2];
	                 break;
	                 }
				 
				//實際運算
	        	outax= ax;
	            outay= ay*Math.cos(ox*pi2) + az*Math.sin(ox*pi2);
	            outaz= -ay*Math.sin(ox*pi2) + az*Math.cos(ox*pi2);
	 
	            spentTime2 = System.currentTimeMillis() - startTime;
	            spentTime=spentTime2-spentTime1;
	            spentTime1 = spentTime2;
	            Fa=outay;
	
	            if(Fa1>Fa && Fa1>Fa2){
	            	if(Fa1<=0.2&&Fa>=-0.2){v=0;}
	            	if(Fa1>0.2){v=1.5;}
	            	if(Fa1>0.5){v=1.7;}
	                if(Fa1>1){v=1.8;}
	                if(Fa1>1.5){v=2;}
	                if(Fa1>2){v=2.5;}
	                if(Fa1>2.5){v=3;}
	                if(Fa1>3){v=4;}
	            } 
	            s1=v*(spentTime/1000);
	            Fa2=Fa1;
	            Fa1=Fa;
	            //限定外框4個
	            //工學院  : 長134.4m , 寬48m
	            //手機地圖610*203
	            //oz : 30~120 = D
	            //oz : 120~210 = A
	            //oz : 210~300 = B
	            //oz : 300~359 || 0~30 = C
	            if(mapX<=87){
	            	mapX =87;
				}
				if(mapX>=290){
					mapX =290;
				}
				if(mapY<=90){
					mapY =90;
				}
				if(mapY>=700){
					mapY =700;
				}
				//工學院B面
				if( (mapY<145)&&(oz>300||oz<30)){
					if(mapX==87){mapX=126;}
					mapY =90;
					s2=s1*4.3;//短邊換算
					 S=S+s1;
					 mapX += (s2);
				}
				if( (mapY<141)&&(oz>120&&oz<210)){
					if(mapX==290){mapX=265;}
					mapY =90;
					s2=s1*4.3;//短邊換算
					 S=S+s1;
					 mapX -=(s2);
				}
				
				//工學院A面
				if((mapX<146) && (oz>30&&oz<120)){
					if(mapY==90){mapY=120;}
					mapX=87;
					s2=s1*4.6;//長邊換算
					 S=S+s1;
					 mapY +=(s2);
				}
				if( (mapX<133)&&(oz>210&&oz<300)){
					 if(mapY==700){mapY=660;}
					 mapX=87;
					s2=s1*4.6;//長邊換算
					 S=S+s1;
					 mapY -=(s2);
				}
				
				//工學院C面
				if((mapX>239) &&(oz>30&&oz<120)){
					if(mapY==90){mapY=115;}
					mapX=290;
					s2=s1*4.6;//長邊換算
					 S=S+s1;
					 mapY +=(s2);
				}
				if( (mapX>262)&&(oz>210&&oz<300)){
					if(mapY==700){mapY=685;}
					mapX=290;
					s2=s1*4.6;//長邊換算
					 S=S+s1;
					 mapY -=(s2);
				}
				
				//工學院D面
				if( (mapY>628)&&(oz>300||oz<30)){
					if(mapX==87){mapX=110;}
					mapY =700;
					s2=s1*4.3;//短邊換算
					 S=S+s1;
					 mapX +=(s2);
				}
				if( (mapY>670)&&(oz>120&&oz<210)){
					if(mapX==290){mapX=275;}
					mapY =700;
					s2=s1*4.3;//短邊換算
					 S=S+s1;
					 mapX -=(s2);
				}
				
				//系辦
				if( ((mapY >= 260)&&(mapY < 350))&&(oz>300||oz<30)){
					mapY =310;
					s2=s1*4.3;//短邊換算
					 S=S+s1;
					 mapX +=(s2);
				}
				if( ((mapY >= 260)&&(mapY < 350))&&(oz>120&&oz<210)){
					mapY =310;
					s2=s1*4.3;//短邊換算
					 S=S+s1;
					 mapX -=(s2);
					}
				}
			}
		}

	//設定menu功能表狀態列 
		public static final int StartBtnID = Menu.FIRST;			//執行程式
		public static final int StopBtnID = Menu.FIRST + 1;			//暫停程式
		public static final int TouchBtnID = Menu.FIRST + 2;		//觸控位置
		public static final int ClearBtnID = Menu.FIRST + 3;		//清除路徑
		public static final int QRcodeBtnID= Menu.FIRST + 4;		//偵測QRcode
		public static final int ExitBtnID = Menu.FIRST + 5;			//終止程式
		//建立menu功能表項目 
	    public boolean onCreateOptionsMenu(Menu menu){
	    	//使用Menu中的add方法，參數分別為GroupID、MenuID、順序、要顯示的字串
	        menu.add(0, StartBtnID, 0, "Start");
	        menu.add(0, StopBtnID, 0, "Stop");
	        menu.add(0, TouchBtnID, 0, "Touch");
	        menu.add(0, ClearBtnID, 0, "Clear");
	        menu.add(0, QRcodeBtnID, 0, "QRcode");
	        menu.add(0, ExitBtnID, 0, "Exit");
	        return true;
	    }
	    //處理被選擇的項目
	    public boolean onOptionsItemSelected(MenuItem item){
	        super.onOptionsItemSelected(item);
	        //使用MenuItem中的方法getItemId()取得目前被選擇的項目
	        switch( item.getItemId() ) {
	        	case StartBtnID:
	        		 Startevent();        		
	        		 break;
	            case StopBtnID:
	           	 	 Stopevent();
	           	 	 break;
	            case TouchBtnID:
	           	 	 Touchevent();
	           	 	 break;
	        	case ClearBtnID:
	        		 Clearevent();
	        		 break;
	            case QRcodeBtnID:
	            	 QRcodeevent();
	            	 break;
	            case ExitBtnID:
	            	 Exitevent();
	            	 break;
	        }
	        return true;
	    }
	    
	    //執行Start事件
	    public void Startevent(){
	    	if(start == 0){
	        	//警告視窗
	    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    		//設定內文訊息
	    		builder.setMessage("觸控地圖上任一點設定你的起始座標");
	    		//設定訊息標題
	    		builder.setTitle("Start");
	    		//設定按鈕屬性及事件
	    		builder.setPositiveButton("OK",new DialogInterface.OnClickListener(){      			 
	    			@Override
	    			public void onClick(DialogInterface dialog, int which) {							
	    			}
	    			}).show();
	    		
	    		start = 2;
	    	}
	    }
	    
	    //執行Stop事件
	    public void Stopevent(){
	    	if(start == 1 && stop == 0){stop = 1;}
	    	else if(start == 1 && stop == 1){stop = 0;	}
	    	else{}
	    }
	    
	    //執行Touch事件
	    public void Touchevent(){
	    	touch = 0;
	    }
	    
	    //執行Clear事件
	    public void Clearevent(){
	    	Intent intent = new Intent();
	    	intent.setClass(CSE1F.this, CSE1F.class);
	    	Bundle bundle = new Bundle();			//建立Bundle物件
	        bundle.putInt("from",0);				//寫入資料到Bundle中
	        intent.putExtras(bundle);				//將Bundle指定到Intent
	    	startActivity(intent);
	    	CSE1F.this.finish();
	    }
	    
	    //執行QRcode事件
	    public void QRcodeevent(){
	    	QRcode();
	    }
	    private void QRcode(){
	    	//連結ZXING的API
	    	intent = new Intent("com.google.zxing.client.android.SCAN");	//開啟條碼掃描器
	    	startActivityForResult(intent, 1);	//要求回傳1
	    }
	    
	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//			super.onActivityResult(requestCode, resultCode, data);		
	    	requestCode = 1;
			if (requestCode == 1) {	//startActivityForResult回傳值
				if (resultCode == RESULT_OK) {								
					temp = data.getStringExtra("SCAN_RESULT");	//擷取整段QRcode的資訊
					tempCheck = temp.substring(0,2);			//取出QRcode的判斷碼
					tempX = temp.substring(2,5);				//取出QRcode的X座標
			    	tempY = temp.substring(5,8);				//取出QRcode的Y座標
			    	tempZ = temp.substring(8,9);				//取出QRcode的Z座標
			    	tempClass = temp.substring(9);				//取出QRcode的位置
			    	QRfloor = Integer.valueOf(tempZ);			//回傳Z座標
			    	
			    	if(tempCheck.equals("OK")){			    	
				    	if(QRfloor == 1){
				    		Intent intent = new Intent();  
				            intent.setClass(CSE1F.this, CSE1F.class);
				            Bundle bundle = new Bundle();				//建立Bundle物件
				            bundle.putString("temp",temp);				//寫入資料到Bundle中
				            bundle.putString("tempX",tempX);			//寫入資料到Bundle中
				            bundle.putString("tempY",tempY);			//寫入資料到Bundle中
				            bundle.putString("tempZ",tempZ);			//寫入資料到Bundle中
				            bundle.putString("tempClass",tempClass);	//寫入資料到Bundle中
				            bundle.putInt("QRfloor",QRfloor);			//寫入資料到Bundle中
				            bundle.putInt("from",1);					//寫入資料到Bundle中
				            intent.putExtras(bundle);					//將Bundle指定到Intent
				            startActivity(intent);
				            System.exit(0);
				    	}
				    	if(QRfloor == 2){
				    		Intent intent = new Intent();  
				            intent.setClass(CSE1F.this, CSE2F.class);
				            Bundle bundle = new Bundle();				//建立Bundle物件
				            bundle.putString("temp",temp);				//寫入資料到Bundle中
				            bundle.putString("tempX",tempX);			//寫入資料到Bundle中
				            bundle.putString("tempY",tempY);			//寫入資料到Bundle中
				            bundle.putString("tempZ",tempZ);			//寫入資料到Bundle中
				            bundle.putString("tempClass",tempClass);	//寫入資料到Bundle中
				            bundle.putInt("QRfloor",QRfloor);			//寫入資料到Bundle中
				            bundle.putInt("from",1);					//寫入資料到Bundle中
				            intent.putExtras(bundle);					//將Bundle指定到Intent
				            startActivity(intent);
				            System.exit(0);
				    	}
				    	if(QRfloor == 3){
				    		Intent intent = new Intent();  
				            intent.setClass(CSE1F.this, CSE3F.class);
				            Bundle bundle = new Bundle();				//建立Bundle物件
				            bundle.putString("temp",temp);				//寫入資料到Bundle中
				            bundle.putString("tempX",tempX);			//寫入資料到Bundle中
				            bundle.putString("tempY",tempY);			//寫入資料到Bundle中
				            bundle.putString("tempZ",tempZ);			//寫入資料到Bundle中
				            bundle.putString("tempClass",tempClass);	//寫入資料到Bundle中
				            bundle.putInt("QRfloor",QRfloor);			//寫入資料到Bundle中
				            bundle.putInt("from",1);					//寫入資料到Bundle中
				            intent.putExtras(bundle);					//將Bundle指定到Intent
				            startActivity(intent);
				            System.exit(0);
				    	}
				    	if(QRfloor == 4){
				    		Intent intent = new Intent();  
				            intent.setClass(CSE1F.this, CSE4F.class);
				            Bundle bundle = new Bundle();				//建立Bundle物件
				            bundle.putString("temp",temp);				//寫入資料到Bundle中
				            bundle.putString("tempX",tempX);			//寫入資料到Bundle中
				            bundle.putString("tempY",tempY);			//寫入資料到Bundle中
				            bundle.putString("tempZ",tempZ);			//寫入資料到Bundle中
				            bundle.putString("tempClass",tempClass);	//寫入資料到Bundle中
				            bundle.putInt("QRfloor",QRfloor);			//寫入資料到Bundle中
				            bundle.putInt("from",1);					//寫入資料到Bundle中
				            intent.putExtras(bundle);					//將Bundle指定到Intent
				            startActivity(intent);
				            System.exit(0);
				    	}
			    	}
			    	else{
			    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
						builder.setMessage("此條碼不適用於本程式，請重新確認");
						builder.setTitle("錯誤");
						builder.setPositiveButton("OK",new DialogInterface.OnClickListener(){      			 
							@Override
							public void onClick(DialogInterface dialog, int which) {}
						}).show();
			    	}
				}
			}
		}
	    
	  //執行Exit事件
	    public void Exitevent(){
	    	System.exit(0);
	    }
	}
