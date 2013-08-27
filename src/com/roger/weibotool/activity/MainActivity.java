package com.roger.weibotool.activity;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.roger.weibotool.R;
import com.roger.weibotool.util.AccessTokenKeeper;

public class MainActivity extends Activity implements OnClickListener {

	private Button mAuthBtn;
	private Button mToolsBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mAuthBtn = (Button) findViewById(R.id.main_authenticate_btn);
		mToolsBtn = (Button) findViewById(R.id.main_tools_btn);
		mAuthBtn.setOnClickListener(this);
		mToolsBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.main_authenticate_btn:
				goToAuthPage();
				break;
			case R.id.main_tools_btn:
				goToToolsPage();
				break;
			default:
		}
	}

	private void goToAuthPage() {

		Intent intent = new Intent();
		intent.setClass(this, AuthenticateActivity.class);
		startActivity(intent);
	}

	private void goToToolsPage() {
		String token = AccessTokenKeeper.readAccessToken(this).getToken();
		if (StringUtils.isEmpty(token)) {
			Toast.makeText(this, "Please Login...", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent();
		intent.setClass(this, ToolsActivity.class);
		startActivity(intent);
	}
}
