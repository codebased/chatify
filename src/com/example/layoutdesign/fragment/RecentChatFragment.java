package com.example.layoutdesign.fragment;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.example.layoutdesign.R;
import com.example.layoutdesign.DataSource.ChatHistoryDataSource;
import com.example.layoutdesign.Model.ChatHistory;
import com.example.layoutdesign.Model.ListModel;
import com.example.layoutdesign.base.CustomListViewAdapter;
import com.example.layoutdesign.base.ListFragmentBase;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class RecentChatFragment extends ListFragmentBase {
	private Bitmap[] emoticons;
	private static final int NO_OF_EMOTICONS = 54;
	public RecentChatFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {

		// If activity recreated (such as from screen rotate), restore
		// the previous article selection set by onSaveInstanceState().
		// This is primarily necessary when in the two-pane layout.
		if (savedInstanceState != null) {
		}

		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_recentchat, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		this.updateRecentChatView();

	}

	private void updateRecentChatView() {
		this.setListData(null);
		this.listView = ( ListView ) getActivity().findViewById( R.id.list );
		this.listAdapter = new CustomListViewAdapter(getActivity(), this, listValues,
				getActivity().getResources(), R.layout.recentchatlistitemformat);
		this.listView.setAdapter( this.listAdapter );

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void itemClicked(int position) {
		ChatFragment fragment = (ChatFragment)
				getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_chat);

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

			((com.example.layoutdesign.base.ActivityBase)getActivity()).loadFragment(newFragment);
		}  
	}
	private class ImageGetter implements Html.ImageGetter {
		 
		public Drawable getDrawable(String source) {
		        int id = Integer.parseInt(source.replace(".png", ""));
		        Drawable d = new BitmapDrawable(getResources(),emoticons[id-1]);
		       d.setBounds(0,0,d.getIntrinsicWidth(),d.getIntrinsicHeight());
		       return d;
		     }
		};
		
	@Override
	public void setListData(Bundle data) {
		if ( this.listValues == null ){
			this.listValues = new ArrayList<ListModel>();
		}

		this.listValues.clear();

		readEmoticons();
			ChatHistoryDataSource chatHistoryDS = new ChatHistoryDataSource(getActivity());
			List<ChatHistory> historyList = chatHistoryDS.getRecentChatSummary();
			ListModel m;
			for(ChatHistory ch: historyList){
				m = new ListModel();
				if(ch.getReceiver()==null){
				
				}else{
					if(ch.getReceiver().isEmpty())
					{
					}
					else{
				
				m.setText(ch.getReceiver());
				
					}
				}
				
				if(ch.getText()==null){
					
				}else{
					if(ch.getText().isEmpty())
					{
					}
					else{
				Spanned sp=Html.fromHtml(ch.getText(),new ImageGetter(),null);
				m.setSpanText1(sp);
				
					}
					}
				this.listValues.add(m);	
			}
		
	}
	
	/**
	 * Reading all emoticons in local cache
	 */
	private void readEmoticons () {

		emoticons = new Bitmap[NO_OF_EMOTICONS];
		for (short i = 0; i < NO_OF_EMOTICONS; i++) {			
			emoticons[i] = getImage((i+1) + ".png");
		}

	}
	/**
	 * For loading smileys from assets
	 */
	private Bitmap getImage(String path) {
		AssetManager mngr = getActivity().getAssets();
		InputStream in = null;
		try {
			in = mngr.open("emoticons/" + path);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Bitmap temp = BitmapFactory.decodeStream(in, null, null);
		return temp;
	}
}