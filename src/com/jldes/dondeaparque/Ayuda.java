package com.jldes.dondeaparque;


import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class Ayuda extends SherlockActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ayuda);
		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.fondoabar));
		getSupportActionBar().setIcon(
				getResources().getDrawable(R.drawable.titulo));
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break; 
 
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onBackPressed() {
		finish();
	}
}
