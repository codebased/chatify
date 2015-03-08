package com.example.layoutdesign.fragment;

 
import com.example.layoutdesign.R;
 

import com.example.layoutdesign.Model.ListModel;
import com.example.layoutdesign.base.ListFragmentBase;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AddUserFragment extends ListFragmentBase {
    public final static String ARG_POSITION = "position";
    public final static String ARG_RECEIVER_NAME = "receivername";
    int currentPosition = -1;
    String currentReceiverName = "";

    public AddUserFragment() {
		// Empty constructor required for fragment subclasses
	}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {

        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            this.currentPosition= savedInstanceState.getInt(ARG_POSITION);
            this.currentReceiverName= savedInstanceState.getString(ARG_RECEIVER_NAME);
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_adduser, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if (args != null) {
            // Set chat view based on argument passed in
            this.updateChatView(args.getInt(ARG_POSITION), args.getString(ARG_RECEIVER_NAME));
        } else if (this.currentPosition != -1) {
            // Set chat view based on saved instance state defined during onCreateView
        	this.updateChatView(this.currentPosition, this.currentReceiverName);
        }
    }

    public void updateChatView(int position, String receiverName) {
        

        this.currentPosition = position;
        this.currentReceiverName = receiverName;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        outState.putInt(ARG_POSITION, this.currentPosition);
        outState.putString(ARG_RECEIVER_NAME, this.currentReceiverName);
    }

	@Override
	public void itemClicked(int position) {
		// TODO Auto-generated method stub
		((com.example.layoutdesign.base.Base)getActivity()).showAlert(this.listValues.get(position).getText());
	
	}

	@Override
	public void setListData(Bundle data) {
		// TODO Auto-generated method stub
		ListModel lm = new ListModel();
		lm.setText("Raja");
		ListModel lm2 = new ListModel();
		lm2.setText("Raja");
		this.listValues.add(lm);
		this.listValues.add(lm2);
		
	}
}