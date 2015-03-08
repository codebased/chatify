package com.example.layoutdesign.fragment;


import java.util.ArrayList;
import java.util.List;

import com.example.layoutdesign.R;
import com.example.layoutdesign.DataSource.ChatHistoryDataSource;
import com.example.layoutdesign.DataSource.DefaultContentProvider;
import com.example.layoutdesign.Model.ChatHistory;
import com.example.layoutdesign.Model.ContactNumber;
import com.example.layoutdesign.Model.ListModel;
import com.example.layoutdesign.base.CustomListViewAdapter;
import com.example.layoutdesign.base.ListFragmentBase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class ContactListFragment extends ListFragmentBase {

	public ContactListFragment() {
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
		return inflater.inflate(R.layout.fragment_contactlist, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		this.updateContactListView();

	}

	private void updateContactListView() {
		this.setListData(null);
		this.listView = ( ListView ) getActivity().findViewById( R.id.list );
		this.listAdapter = new CustomListViewAdapter(getActivity(), this, listValues,
				getActivity().getResources(), R.layout.contactlistitemformat);
		this.listView.setAdapter( this.listAdapter );

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void itemClicked(int position) {
	}

	@Override
	public void setListData(Bundle data) {
		if ( this.listValues == null ){
			this.listValues = new ArrayList<ListModel>();
		}

		this.listValues.clear();

		 List<ContactNumber> contactNumbers =  DefaultContentProvider.getContacts(getActivity());
		 
		 for(ContactNumber number: contactNumbers)
		 {
			 ListModel item = new ListModel();
			 item.setText(number.getPhoneNumber().toString());
			 this.listValues.add(item);
		 }
	}
}