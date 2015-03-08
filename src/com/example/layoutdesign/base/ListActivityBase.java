package com.example.layoutdesign.base;

import java.util.ArrayList;

import com.example.layoutdesign.Model.ListModel;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

interface IListBase {
	public void itemClicked (int position);
	public void setListData (Bundle data);
}

public abstract class ListActivityBase extends ActivityBase 
implements IListBase
{
	protected CustomListViewAdapter listAdapter;
	protected ArrayList<ListModel> listValues ;
	protected ListView listView;
}
