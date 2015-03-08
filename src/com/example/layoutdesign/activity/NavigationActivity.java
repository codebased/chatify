package com.example.layoutdesign.activity;

import java.util.List;

import com.example.layoutdesign.R;
import com.example.layoutdesign.DataSource.ChatContactDataSource;
import com.example.layoutdesign.Model.ChatContact; 
import com.example.layoutdesign.base.ActivityBase;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class NavigationActivity extends ActivityBase {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navigation_contacts);
		
		OnItemClickListener listItemClicked = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Intent intent = new Intent();
				Bundle bundle = new Bundle();

		        bundle.putString("chatcontactid", arg0.getItemAtPosition(arg2).toString());
		         
		        intent.putExtras(bundle);
		        startActivity(intent); 
				
			}
		};
			
		ChatContactDataSource c = new ChatContactDataSource(this);
		List<ChatContact> chatContacts = c.getWordMatches("", null);
		ChatContact[] cc = chatContacts.toArray(new ChatContact[chatContacts.size()]);
		
		ArrayAdapter<ChatContact> adapter = new ArrayAdapter<ChatContact>(this,
				android.R.layout.simple_dropdown_item_1line, cc);
		
		ListView chatcontactListView = (ListView) findViewById(R.id.list_contacts);
		chatcontactListView.setTextFilterEnabled(true);
		chatcontactListView.setAdapter(adapter);
		chatcontactListView.setOnItemClickListener(listItemClicked);
		
//		AutoCompleteTextView chatcontactautoListView = (AutoCompleteTextView) findViewById(R.id.navigation_contacts);
//		chatcontactautoListView.setThreshold(10);
//		chatcontactautoListView.setAdapter(adapter);
//		chatcontactautoListView.setOnItemClickListener(listItemClicked);
	}



}
