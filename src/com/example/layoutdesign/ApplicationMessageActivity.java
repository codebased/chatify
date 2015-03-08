package com.example.layoutdesign;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

import android.widget.TextView;

public class ApplicationMessageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_application_message);
		Intent intent = getIntent();
		TextView appMessage = (TextView) this.findViewById(R.id.applicationMessage);
		appMessage.setText(intent.getStringExtra("applicationMessage"));
	}
}
