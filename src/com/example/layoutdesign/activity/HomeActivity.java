package com.example.layoutdesign.activity;

import java.util.ArrayList;

import com.example.layoutdesign.R;
import com.example.layoutdesign.DataSource.UserDataSource;
import com.example.layoutdesign.Model.ListModel;
import com.example.layoutdesign.base.CustomListViewAdapter;
import com.example.layoutdesign.base.ListActivityBase;
import com.example.layoutdesign.fragment.ChatFragment;
import com.example.layoutdesign.fragment.RecentChatFragment;

import common.lib.widget.RefreshableListView;
import common.lib.widget.RefreshableListView.OnRefreshListener;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class HomeActivity extends ListActivityBase {
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;
	private CharSequence drawerTitle;
	private CharSequence title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		
		this.setListData(null);
		this.listView = ( RefreshableListView )findViewById( R.id.list );
		this.listAdapter = new CustomListViewAdapter( this, this, listValues,getResources(), R.layout.navigationlistitemformat);
		this.listView.setAdapter( this.listAdapter );

		title = drawerTitle = this.getTitle();

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(33, 127, 188)));
		
		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		drawerToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				drawerLayout,         /* DrawerLayout object */
				R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
				R.string.drawer_open,  /* "open drawer" description for accessibility */
				R.string.drawer_close  /* "close drawer" description for accessibility */
				) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(title);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(drawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);

		if (savedInstanceState == null) {
			RecentChatFragment newFragment = new RecentChatFragment();
			this.loadFragment(newFragment);
		}
		
		((RefreshableListView) this.listView).setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh(RefreshableListView listView) {
				setListData(null);
				listAdapter.notifyDataSetInvalidated();
				listAdapter.notifyDataSetChanged();
				   listView.completeRefreshing();
			}
		});
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		//        // If the nav drawer is open, hide action items related to the content view
		//        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
		//        menu.findItem(R.id.menu_search).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch(item.getItemId()) {
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		this.title = title;
		getActionBar().setTitle(this.title);
	}

	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void itemClicked(int position) {

		ChatFragment fragment = (ChatFragment)
				getSupportFragmentManager().findFragmentById(R.id.fragment_chat);

		if (fragment != null) {
			// If chat frag is available, we're in two-pane layout...
			// Call a method in the ArticleFragment to update its content
			fragment.updateChatView(position,this.listValues.get(position).getText());

		} else {
			
			// If the frag is not available, we're in the one-pane layout and must swap frags...
			// Create fragment and give it an argument for the selected user
			ChatFragment newFragment = new ChatFragment();
			Bundle args = new Bundle();
			args.putInt(ChatFragment.ARG_POSITION, position);
			args.putString(ChatFragment.ARG_RECEIVER_NAME,this.listValues.get(position).getText()); 
			newFragment.setArguments(args);
		    
		    this.loadFragment(newFragment);
		}  

		// update selected item and title, then close the drawer
		this.listView.setItemChecked(position, true);
		setTitle(this.listValues.get(position).getText());
		drawerLayout.closeDrawer(this.listView);
	}

	@Override
	public void setListData(Bundle data) {
		UserDataSource dataSource = new UserDataSource(this);
		this.listValues = new ArrayList<ListModel>(dataSource.getUsers(this)); 
	}
}