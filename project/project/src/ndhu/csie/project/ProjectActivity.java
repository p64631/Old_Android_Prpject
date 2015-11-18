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
    	//�]�w�ù���ܪ��A 	
    	requestWindowFeature(Window.FEATURE_NO_TITLE);//�������D��
    	//�O���ù����G
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.home);
        //�������s
		Button explantbutton =(Button)findViewById(R.id.explant);
		explantbutton.setOnClickListener(new OnClickListener(){
          public void onClick(View arg0){
        	Intent intent = new Intent();  
            intent.setClass(ProjectActivity.this, explant.class);
            startActivity(intent);
          }
        });
        //�i�J�Ӽh���
        Button startbutton =(Button)findViewById(R.id.start);
		startbutton.setOnClickListener(new OnClickListener(){
          public void onClick(View arg0){
        	Intent intent = new Intent();  
            intent.setClass(ProjectActivity.this, home.class);
            startActivity(intent);
          }
        });  
		//���}���s
		Button exitbutton =(Button)findViewById(R.id.exit);
		exitbutton.setOnClickListener(new OnClickListener(){
          public void onClick(View arg0){
            ProjectActivity.this.finish();
            System.exit(0);
          }
        });
    }
}