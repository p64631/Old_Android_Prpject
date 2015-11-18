package ndhu.csie.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.view.View.OnClickListener;

public class ProjectActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);     
    	//設定螢幕顯示狀態 	
    	requestWindowFeature(Window.FEATURE_NO_TITLE);//取消標題欄
    	//保持螢幕長亮
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.home);
        //說明按鈕
		Button explantbutton =(Button)findViewById(R.id.explant);
		explantbutton.setOnClickListener(new OnClickListener(){
          public void onClick(View arg0){
        	Intent intent = new Intent();  
            intent.setClass(ProjectActivity.this, explant.class);
            startActivity(intent);
          }
        });
        //進入樓層選擇
        Button startbutton =(Button)findViewById(R.id.start);
		startbutton.setOnClickListener(new OnClickListener(){
          public void onClick(View arg0){
        	Intent intent = new Intent();  
            intent.setClass(ProjectActivity.this, home.class);
            startActivity(intent);
          }
        });  
		//離開按鈕
		Button exitbutton =(Button)findViewById(R.id.exit);
		exitbutton.setOnClickListener(new OnClickListener(){
          public void onClick(View arg0){
            ProjectActivity.this.finish();
            System.exit(0);
          }
        });
    }
}