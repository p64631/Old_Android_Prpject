package ndhu.csie.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class explant extends Activity{
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     
      //設定螢幕顯示狀態
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_FULLSCREEN);
         setContentView(R.layout.explant);
        
        Button backbutton =(Button)findViewById(R.id.back);
		backbutton.setOnClickListener(new OnClickListener(){
          public void onClick(View arg0){
        	Intent intent = new Intent();  
            intent.setClass(explant.this, ProjectActivity.class);
            startActivity(intent);
            explant.this.finish();
          }
        });
	}
}
