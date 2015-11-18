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
	int start=0;//�]�w�}�l
	int stop=0;//�]�w�Ȱ�		
	int touch=0;//�]�wĲ�N			
	private float mapX ;//X�y���I
	private float mapY ;//Y�y���I	
	int from;	//
	
	int QRfloor;
	String temp;		//�ȦsQRcode�Ҧ���T
	String tempCheck;	//�P�_QRcode�O�_�O�ۤv�]�p��
	String tempX;		//�ȦsQRcodeX�y�Э�
	String tempY;		//�ȦsQRcodeY�y�Э�
	String tempZ;		//�ȦsQRcode�Ӽh��m
	String tempClass;	//�ȦsQRcode�ЫǸ�T
	Intent intent = new Intent();
	
	int winW,winH;//���o�ù����e	
	float winw,winh;//�Ȧs�ù����e �Ψ��Y��Ϥ�	
	int bitmapw,bitmaph;//���o�Ϥ����e	
	float matrixw,matrixh;//�x�}�����
	
	double startTime =0 ;//�_�l�ɶ�
	
	MyView mMyView = null;
	public void onCreate(Bundle s){
		super.onCreate(s);
		startTime = System.currentTimeMillis();
		//�]�m�������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//���o�ù��j�p
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();   
		winW  = display.getWidth();  
		winH = display.getHeight();
		//����MyView
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

	    	mapX = Float.valueOf(tempX);				//�^��X�y��
	    	mapY = Float.valueOf(tempY);				//�^��Y�y��
	    	start = 1;
	    	
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(tempClass);
			builder.setTitle("�A����m");
			builder.setPositiveButton("OK",new DialogInterface.OnClickListener(){      			 
				@Override
				public void onClick(DialogInterface dialog, int which) {}
			}).show();
	    }
		}	
	public class MyView extends SurfaceView implements Callback,Runnable,SensorEventListener{
		Paint mPaint=null;//ø�� ����		
		SurfaceHolder mSurfaceHolder=null;//�h��ø�ϥ[�t		
		Canvas mCanvas=null;//ø�� �e��	
		Canvas mapCanvas=null;
		boolean mIsRunning=false;//�O�_�b����
		//���oSensor���
		private SensorManager mSersorManager=null;
		Sensor mSensor=null;	
		//�����Ӽh�ɨϥ�
		private Bitmap maparrow;
		
		private Bitmap bitmap;//�e��	
		private Bitmap backmap,flootmap;//�Ϥ��[��
		private Bitmap newmap;
						
		double spentTime=0,spentTime1=0,spentTime2=0,v=0,s1=0,s2=0,S=0,startTime,t;
		double Fa=0,Fa1=0,Fa2=0,mFa=0,outax=0,outay=0,outaz=0,pi2=0.0175;
		//���O �ϳ� �P�� X�bY�b
		private float ax,ay,az,ox,oz,oy;	
		private float imageX = 0f;
        private float imageY = 0f;
		
        public MyView(Context context){	
        	super(context);		
        	setFocusable(true);//�]�mView�o����J�I		
			setFocusableInTouchMode(true);//�]�mView��oĲ�N�̨ƥ�
			//�o��SurfaceHolder
			mSurfaceHolder = getHolder();
			mSurfaceHolder.addCallback(this);
			//�]�wø�ϵe��
			bitmap = Bitmap.createBitmap(winW,winH, Bitmap.Config.ARGB_4444);
			//���h�Ϥ�
			backmap = BitmapFactory.decodeResource(getResources(),R.drawable.back);
			flootmap = BitmapFactory.decodeResource(getResources(),R.drawable.csea);
			maparrow = BitmapFactory.decodeResource(getResources(),R.drawable.ball);

 			//ø��
    		mCanvas=new Canvas();         
    		mCanvas.setBitmap(bitmap);  
    		mapCanvas=new Canvas();         
    		mapCanvas.setBitmap(bitmap);  
    		
    		mPaint = new Paint();           
    		mPaint.setColor(Color.RED);
    		//���o�Ϥ��j�p
			bitmapw = flootmap.getWidth();			
			bitmaph = flootmap.getHeight();
			//��j�Y�p		
			matrixw=winW /bitmapw;
			matrixh=winH/bitmaph;
			Matrix matrix = new Matrix();
			matrix.postScale(matrixh, matrixh);
			newmap = Bitmap.createBitmap(flootmap, 0, 0, bitmapw, bitmaph, matrix, true);
			//�o��SensorManagery���/
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
			//ø�s�I��
			mCanvas.drawBitmap(backmap, 0, 0, null);
			mCanvas.drawBitmap(newmap, 0, 0, null);
			mCanvas.drawBitmap(bitmap, 0, 0, null);

			mCanvas.drawText("X"+mapX, (float) (winW*0.8), 20, mPaint);
			mCanvas.drawText("Y"+mapY, (float) (winW*0.8), 40, mPaint);
			mCanvas.drawText("�`���|��"+S, (float) (winW*0.8), 60, mPaint);
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
				//������ӤϹL��
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
				 
				//��ڹB��
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
	            //���w�~��4��
	            //�u�ǰ|  : ��134.4m , �e48m
	            //����a��610*203
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
				//�u�ǰ|B��
				if( (mapY<145)&&(oz>300||oz<30)){
					if(mapX==87){mapX=126;}
					mapY =90;
					s2=s1*4.3;//�u�䴫��
					 S=S+s1;
					 mapX += (s2);
				}
				if( (mapY<141)&&(oz>120&&oz<210)){
					if(mapX==290){mapX=265;}
					mapY =90;
					s2=s1*4.3;//�u�䴫��
					 S=S+s1;
					 mapX -=(s2);
				}
				
				//�u�ǰ|A��
				if((mapX<146) && (oz>30&&oz<120)){
					if(mapY==90){mapY=120;}
					mapX=87;
					s2=s1*4.6;//���䴫��
					 S=S+s1;
					 mapY +=(s2);
				}
				if( (mapX<133)&&(oz>210&&oz<300)){
					 if(mapY==700){mapY=660;}
					 mapX=87;
					s2=s1*4.6;//���䴫��
					 S=S+s1;
					 mapY -=(s2);
				}
				
				//�u�ǰ|C��
				if((mapX>239) &&(oz>30&&oz<120)){
					if(mapY==90){mapY=115;}
					mapX=290;
					s2=s1*4.6;//���䴫��
					 S=S+s1;
					 mapY +=(s2);
				}
				if( (mapX>262)&&(oz>210&&oz<300)){
					if(mapY==700){mapY=685;}
					mapX=290;
					s2=s1*4.6;//���䴫��
					 S=S+s1;
					 mapY -=(s2);
				}
				
				//�u�ǰ|D��
				if( (mapY>628)&&(oz>300||oz<30)){
					if(mapX==87){mapX=110;}
					mapY =700;
					s2=s1*4.3;//�u�䴫��
					 S=S+s1;
					 mapX +=(s2);
				}
				if( (mapY>670)&&(oz>120&&oz<210)){
					if(mapX==290){mapX=275;}
					mapY =700;
					s2=s1*4.3;//�u�䴫��
					 S=S+s1;
					 mapX -=(s2);
				}
				
				//�t��
				if( ((mapY >= 260)&&(mapY < 350))&&(oz>300||oz<30)){
					mapY =310;
					s2=s1*4.3;//�u�䴫��
					 S=S+s1;
					 mapX +=(s2);
				}
				if( ((mapY >= 260)&&(mapY < 350))&&(oz>120&&oz<210)){
					mapY =310;
					s2=s1*4.3;//�u�䴫��
					 S=S+s1;
					 mapX -=(s2);
					}
				}
			}
		}

	//�]�wmenu�\����A�C 
		public static final int StartBtnID = Menu.FIRST;			//����{��
		public static final int StopBtnID = Menu.FIRST + 1;			//�Ȱ��{��
		public static final int TouchBtnID = Menu.FIRST + 2;		//Ĳ����m
		public static final int ClearBtnID = Menu.FIRST + 3;		//�M�����|
		public static final int QRcodeBtnID= Menu.FIRST + 4;		//����QRcode
		public static final int ExitBtnID = Menu.FIRST + 5;			//�פ�{��
		//�إ�menu�\����� 
	    public boolean onCreateOptionsMenu(Menu menu){
	    	//�ϥ�Menu����add��k�A�ѼƤ��O��GroupID�BMenuID�B���ǡB�n��ܪ��r��
	        menu.add(0, StartBtnID, 0, "Start");
	        menu.add(0, StopBtnID, 0, "Stop");
	        menu.add(0, TouchBtnID, 0, "Touch");
	        menu.add(0, ClearBtnID, 0, "Clear");
	        menu.add(0, QRcodeBtnID, 0, "QRcode");
	        menu.add(0, ExitBtnID, 0, "Exit");
	        return true;
	    }
	    //�B�z�Q��ܪ�����
	    public boolean onOptionsItemSelected(MenuItem item){
	        super.onOptionsItemSelected(item);
	        //�ϥ�MenuItem������kgetItemId()���o�ثe�Q��ܪ�����
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
	    
	    //����Start�ƥ�
	    public void Startevent(){
	    	if(start == 0){
	        	//ĵ�i����
	    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    		//�]�w����T��
	    		builder.setMessage("Ĳ���a�ϤW���@�I�]�w�A���_�l�y��");
	    		//�]�w�T�����D
	    		builder.setTitle("Start");
	    		//�]�w���s�ݩʤΨƥ�
	    		builder.setPositiveButton("OK",new DialogInterface.OnClickListener(){      			 
	    			@Override
	    			public void onClick(DialogInterface dialog, int which) {							
	    			}
	    			}).show();
	    		
	    		start = 2;
	    	}
	    }
	    
	    //����Stop�ƥ�
	    public void Stopevent(){
	    	if(start == 1 && stop == 0){stop = 1;}
	    	else if(start == 1 && stop == 1){stop = 0;	}
	    	else{}
	    }
	    
	    //����Touch�ƥ�
	    public void Touchevent(){
	    	touch = 0;
	    }
	    
	    //����Clear�ƥ�
	    public void Clearevent(){
	    	Intent intent = new Intent();
	    	intent.setClass(CSE1F.this, CSE1F.class);
	    	Bundle bundle = new Bundle();			//�إ�Bundle����
	        bundle.putInt("from",0);				//�g�J��ƨ�Bundle��
	        intent.putExtras(bundle);				//�NBundle���w��Intent
	    	startActivity(intent);
	    	CSE1F.this.finish();
	    }
	    
	    //����QRcode�ƥ�
	    public void QRcodeevent(){
	    	QRcode();
	    }
	    private void QRcode(){
	    	//�s��ZXING��API
	    	intent = new Intent("com.google.zxing.client.android.SCAN");	//�}�ұ��X���y��
	    	startActivityForResult(intent, 1);	//�n�D�^��1
	    }
	    
	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//			super.onActivityResult(requestCode, resultCode, data);		
	    	requestCode = 1;
			if (requestCode == 1) {	//startActivityForResult�^�ǭ�
				if (resultCode == RESULT_OK) {								
					temp = data.getStringExtra("SCAN_RESULT");	//�^����qQRcode����T
					tempCheck = temp.substring(0,2);			//���XQRcode���P�_�X
					tempX = temp.substring(2,5);				//���XQRcode��X�y��
			    	tempY = temp.substring(5,8);				//���XQRcode��Y�y��
			    	tempZ = temp.substring(8,9);				//���XQRcode��Z�y��
			    	tempClass = temp.substring(9);				//���XQRcode����m
			    	QRfloor = Integer.valueOf(tempZ);			//�^��Z�y��
			    	
			    	if(tempCheck.equals("OK")){			    	
				    	if(QRfloor == 1){
				    		Intent intent = new Intent();  
				            intent.setClass(CSE1F.this, CSE1F.class);
				            Bundle bundle = new Bundle();				//�إ�Bundle����
				            bundle.putString("temp",temp);				//�g�J��ƨ�Bundle��
				            bundle.putString("tempX",tempX);			//�g�J��ƨ�Bundle��
				            bundle.putString("tempY",tempY);			//�g�J��ƨ�Bundle��
				            bundle.putString("tempZ",tempZ);			//�g�J��ƨ�Bundle��
				            bundle.putString("tempClass",tempClass);	//�g�J��ƨ�Bundle��
				            bundle.putInt("QRfloor",QRfloor);			//�g�J��ƨ�Bundle��
				            bundle.putInt("from",1);					//�g�J��ƨ�Bundle��
				            intent.putExtras(bundle);					//�NBundle���w��Intent
				            startActivity(intent);
				            System.exit(0);
				    	}
				    	if(QRfloor == 2){
				    		Intent intent = new Intent();  
				            intent.setClass(CSE1F.this, CSE2F.class);
				            Bundle bundle = new Bundle();				//�إ�Bundle����
				            bundle.putString("temp",temp);				//�g�J��ƨ�Bundle��
				            bundle.putString("tempX",tempX);			//�g�J��ƨ�Bundle��
				            bundle.putString("tempY",tempY);			//�g�J��ƨ�Bundle��
				            bundle.putString("tempZ",tempZ);			//�g�J��ƨ�Bundle��
				            bundle.putString("tempClass",tempClass);	//�g�J��ƨ�Bundle��
				            bundle.putInt("QRfloor",QRfloor);			//�g�J��ƨ�Bundle��
				            bundle.putInt("from",1);					//�g�J��ƨ�Bundle��
				            intent.putExtras(bundle);					//�NBundle���w��Intent
				            startActivity(intent);
				            System.exit(0);
				    	}
				    	if(QRfloor == 3){
				    		Intent intent = new Intent();  
				            intent.setClass(CSE1F.this, CSE3F.class);
				            Bundle bundle = new Bundle();				//�إ�Bundle����
				            bundle.putString("temp",temp);				//�g�J��ƨ�Bundle��
				            bundle.putString("tempX",tempX);			//�g�J��ƨ�Bundle��
				            bundle.putString("tempY",tempY);			//�g�J��ƨ�Bundle��
				            bundle.putString("tempZ",tempZ);			//�g�J��ƨ�Bundle��
				            bundle.putString("tempClass",tempClass);	//�g�J��ƨ�Bundle��
				            bundle.putInt("QRfloor",QRfloor);			//�g�J��ƨ�Bundle��
				            bundle.putInt("from",1);					//�g�J��ƨ�Bundle��
				            intent.putExtras(bundle);					//�NBundle���w��Intent
				            startActivity(intent);
				            System.exit(0);
				    	}
				    	if(QRfloor == 4){
				    		Intent intent = new Intent();  
				            intent.setClass(CSE1F.this, CSE4F.class);
				            Bundle bundle = new Bundle();				//�إ�Bundle����
				            bundle.putString("temp",temp);				//�g�J��ƨ�Bundle��
				            bundle.putString("tempX",tempX);			//�g�J��ƨ�Bundle��
				            bundle.putString("tempY",tempY);			//�g�J��ƨ�Bundle��
				            bundle.putString("tempZ",tempZ);			//�g�J��ƨ�Bundle��
				            bundle.putString("tempClass",tempClass);	//�g�J��ƨ�Bundle��
				            bundle.putInt("QRfloor",QRfloor);			//�g�J��ƨ�Bundle��
				            bundle.putInt("from",1);					//�g�J��ƨ�Bundle��
				            intent.putExtras(bundle);					//�NBundle���w��Intent
				            startActivity(intent);
				            System.exit(0);
				    	}
			    	}
			    	else{
			    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
						builder.setMessage("�����X���A�Ω󥻵{���A�Э��s�T�{");
						builder.setTitle("���~");
						builder.setPositiveButton("OK",new DialogInterface.OnClickListener(){      			 
							@Override
							public void onClick(DialogInterface dialog, int which) {}
						}).show();
			    	}
				}
			}
		}
	    
	  //����Exit�ƥ�
	    public void Exitevent(){
	    	System.exit(0);
	    }
	}
