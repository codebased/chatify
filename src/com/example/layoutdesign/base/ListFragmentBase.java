package com.example.layoutdesign.base;

import java.util.ArrayList;
import android.support.v4.app.Fragment;
import android.widget.ListView;

import com.example.layoutdesign.Model.ListModel;

public abstract class ListFragmentBase extends Fragment
implements IListBase{
	protected CustomListViewAdapter listAdapter;
	protected ArrayList<ListModel> listValues ;
	protected ListView listView;
}
