package com.jldes.dondeaparque;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;

public class Ayuda extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_ayuda);
		Resources res = getResources();
		TabHost tab = (TabHost)findViewById(android.R.id.tabhost);
		tab.setup();
		TabHost.TabSpec spec=tab.newTabSpec("tabayuda");
		spec.setContent(R.id.Ayuda);
		spec.setIndicator("Ayuda");
		tab.addTab(spec);
		tab.setCurrentTab(0);
	}
	public void onBackPressed(){
		finish();
	}
}
