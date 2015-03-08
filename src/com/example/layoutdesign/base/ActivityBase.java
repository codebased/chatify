package com.example.layoutdesign.base;

import com.example.layoutdesign.R;

import com.example.layoutdesign.DataSource.PreferenceDataSource;
import com.example.layoutdesign.fragment.AddUserFragment;
import com.example.layoutdesign.fragment.ChatFragment;
import com.example.layoutdesign.fragment.UserProfileFragment;
import com.example.layoutdesign.interfaceport.SmackManager;

import android.annotation.SuppressLint;
import android.app.ActionBar;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

public class ActivityBase extends Base {

	private ProgressDialog progressDialog;
	private boolean showBar;
	private boolean showDisplayHome;
	public ActivityBase()
	{
		super();
		this.showBar = this.showDisplayHome = true;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_common, menu);
		
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setIconifiedByDefault(true);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
	    //MenuItem item= menu.findItem(R.id.menu_profile);
	    //depending on you conditions, either enable/disable
	    //byte[] b=SmackManager.getOwnVCard().getAvatar();		
		//Drawable image =  new BitmapDrawable(BitmapFactory.decodeByteArray(b, 0, b.length));
		//item.setIcon(image);
	    super.onPrepareOptionsMenu(menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
		case android.R.id.home:
			this.finish();
			return true;
		
		case R.id.menu_search:
			onSearchRequested();
			return true;
		case R.id.menu_profile:
			
			onProfileRequested();
			return true;
	
		default: 
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_chat);
		if ( f != null ){
			if ( ((ChatFragment)f).myOnKeyDown(keyCode) ) {
				return super.onKeyDown(keyCode, event);		
			} else {
				return false;	
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	protected void onProfileRequested() {
		UserProfileFragment newFragment = new UserProfileFragment();
		loadFragment(newFragment);
	}

	protected void onAddContact()
	{
		AddUserFragment newFragment = new AddUserFragment();
		loadFragment(newFragment);
	}

	protected void showProgress(String msg) {
		if (this.progressDialog != null && this.progressDialog.isShowing())
			dismissProgress();

		this.progressDialog = ProgressDialog.show(this, getResources().getString(R.string.app_name), msg);
	}

	protected void dismissProgress() {
		if (this.progressDialog != null) {
			this.progressDialog.dismiss();
			this.progressDialog = null;
		}
	}

	protected void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	@SuppressLint("NewApi")
	protected void toggleActionBar(){

		ActionBar actionbar = getActionBar();
		if ( showBar )
			actionbar.hide();
		else 
			actionbar.show();

		showBar = !showBar;
	}

	@SuppressLint("NewApi")
	protected void toggleHomeUp(){
		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(showDisplayHome);

		showDisplayHome = !showDisplayHome;
	}

	public void loadFragment(Fragment fragment){
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack so the user can navigate back
		transaction.replace(R.id.content_frame, fragment);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	}
}