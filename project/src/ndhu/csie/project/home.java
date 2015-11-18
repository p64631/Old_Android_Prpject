package ndhu.csie.project;
import ndhu.csie.flood.CSE1F;
import ndhu.csie.flood.CSE2F;
import ndhu.csie.flood.CSE3F;
import ndhu.csie.flood.CSE4F;
import ndhu.csie.project.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;


public class home extends Activity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//螢幕常亮    
        setContentView(R.layout.main);
        
        ///////////////////// 1 F ///////////////////////////
        Button buttonCSE1F =(Button)findViewById(R.id.CSE1F);
		buttonCSE1F.setOnClickListener(new OnClickListener(){
          public void onClick(View arg0){
        	Intent intent = new Intent();  
            intent.setClass(home.this, CSE1F.class);
            Bundle bundle = new Bundle();			//建立Bundle物件
            bundle.putInt("Change_floor",1);		//寫入資料到Bundle中
            bundle.putInt("from",0);				//寫入資料到Bundle中
            intent.putExtras(bundle);				//將Bundle指定到Intent
            startActivity(intent);
          }
        });
		///////////////////// 2 F ///////////////////////////
        Button buttonCSE2F =(Button)findViewById(R.id.CSE2F);
		buttonCSE2F.setOnClickListener(new OnClickListener(){
          public void onClick(View arg0){
        	Intent intent = new Intent();  
            intent.setClass(home.this, CSE2F.class);
            Bundle bundle = new Bundle();			//建立Bundle物件
            bundle.putInt("Change_floor",2);		//寫入資料到Bundle中
            bundle.putInt("from",0);				//寫入資料到Bundle中
            intent.putExtras(bundle);				//將Bundle指定到Intent
            startActivity(intent);
          }
        });		
		///////////////////// 3 F ///////////////////////////
        Button buttonCSE3F =(Button)findViewById(R.id.CSE3F);
		buttonCSE3F.setOnClickListener(new OnClickListener(){
          public void onClick(View arg0){
        	Intent intent = new Intent();  
            intent.setClass(home.this, CSE3F.class);
            Bundle bundle = new Bundle();			//建立Bundle物件
            bundle.putInt("Change_floor",3);		//寫入資料到Bundle中
            bundle.putInt("from",0);				//寫入資料到Bundle中
            intent.putExtras(bundle);				//將Bundle指定到Intent
            startActivity(intent);
          }
        });		
		///////////////////// 4 F ///////////////////////////
        Button buttonCSE4F =(Button)findViewById(R.id.CSE4F);
		buttonCSE4F.setOnClickListener(new OnClickListener(){
          public void onClick(View arg0){
        	Intent intent = new Intent();  
            intent.setClass(home.this, CSE4F.class);
            Bundle bundle = new Bundle();			//建立Bundle物件
            bundle.putInt("Change_floor",4);		//寫入資料到Bundle中
            bundle.putInt("from",0);				//寫入資料到Bundle中
            intent.putExtras(bundle);				//將Bundle指定到Intent
            startActivity(intent);
          }
        });	
		//返回
		Button backbutton =(Button)findViewById(R.id.back);
		backbutton.setOnClickListener(new OnClickListener(){
          public void onClick(View arg0){
        	Intent intent = new Intent();  
            intent.setClass(home.this, ProjectActivity.class);
            startActivity(intent);
            home.this.finish();
          }
        });
    }  	
}
