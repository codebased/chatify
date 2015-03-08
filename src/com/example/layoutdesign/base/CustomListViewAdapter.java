package com.example.layoutdesign.base;

import java.util.ArrayList;

import com.example.layoutdesign.R;
import com.example.layoutdesign.Model.ListModel;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.util.Log; 
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListViewAdapter extends BaseAdapter  implements OnClickListener {
    
    private FragmentActivity activity;
    private IListBase list;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    ListModel tempValues=null;
    int i=0;
    private int layoutID;
    
    /*************  CustomAdapter Constructor *****************/
    public CustomListViewAdapter(FragmentActivity a, IListBase l, ArrayList d,Resources resLocal) {
    	this(a,l,d,resLocal, R.layout.listitemformat);
    }

    /*************  CustomAdapter Constructor *****************/
    public CustomListViewAdapter(FragmentActivity a, IListBase l, ArrayList d,Resources resLocal, int layoutID) {
    	
    	/********** Take passed values **********/
    	activity = a;
    	list = l;
        data=d;
        res = resLocal;
        this.layoutID = layoutID;
        
        /***********  Layout inflator to call external xml layout () **********************/
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {
    	if(data.size()<=0)
    		return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
         return position;
    }
    
    /********* Create a holder to contain inflated xml file elements ***********/
    public static class ViewHolder{
        public TextView text;
        public TextView text1;
        public TextView textWide;
        public ImageView image;
       

    }

    /*********** Depends upon data size called for each row , Create each ListView row ***********/
    public View getView(int position, View convertView, ViewGroup parent) {
    	
        View vi=convertView;
        ViewHolder holder;
        
        if(convertView==null){ 
        	
        	/********** Inflate tabitem.xml file for each row ( Defined below ) ************/
            vi = inflater.inflate(this.layoutID, null); 
            
            /******** View Holder Object to contain tabitem.xml file elements ************/
            holder=new ViewHolder();
            holder.text=(TextView)vi.findViewById(R.id.text);
            holder.text1=(TextView)vi.findViewById(R.id.text1);
            holder.image=(ImageView)vi.findViewById(R.id.image);
            
            
           /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        }
        else  
            holder=(ViewHolder)vi.getTag();
        
        if(data.size()<=0)
        {
        	//holder.text.setText("No Data");
        	  holder.text.setVisibility(View.GONE);
        	  holder.text1.setVisibility(View.GONE);
	          holder.image.setVisibility(View.GONE);
	          
        }
        else
        {
        	/***** Get each Model object from Arraylist ********/
	        tempValues=null;
	        tempValues = (ListModel) data.get(position);
	        
	        /************  Set Model values in Holder elements ***********/
	        if(tempValues.getText().isEmpty())
	        {
	        	if(tempValues.getSpanText()==null)
	        	{
	        		holder.text.setVisibility(View.GONE);
	        		holder.image.setVisibility(View.GONE);
	 	        	
	  	        }
	        	  else{
	        		  holder.text.setText(tempValues.getSpanText());
		      	      holder.text.setVisibility(View.VISIBLE);
		      	      
	        	  }
	        }
	        else{
	        	
	        	holder.text.setText(tempValues.getText());
		        holder.text.setVisibility(View.VISIBLE);
		     }
	          	
	        if(tempValues.getText1().isEmpty())
	        {
	        
	        	if(tempValues.getSpanText1()==null)
	  	        {
	        		  holder.text1.setVisibility(View.GONE);
	        		  holder.image.setVisibility(View.VISIBLE);
	        		  
	      	    }
	        	  else{
	        		  
	        		  holder.text1.setText(tempValues.getSpanText1());
		      	        holder.text1.setVisibility(View.VISIBLE);
	        	  }
	        }
	        else{
	        	
	        	holder.text1.setText(tempValues.getText1());
		        holder.text1.setVisibility(View.VISIBLE);
	        }
	     
	       //  holder.image.setImageResource(res.getIdentifier("com.androidexample.customlistview:drawable/"+tempValues.getImage(),null,null));
	        if(tempValues.getImage1()==null)
	        {
	        	holder.image.setVisibility(View.GONE);
	        	
	        }else{
	        holder.image.setImageBitmap(tempValues.getImage1());
	        }
	         /******** Set Item Click Listner for LayoutInflater for each row ***********/
	         vi.setOnClickListener(new OnItemClickListener(position));
	         vi.setOnLongClickListener(new OnLongItemClickListener(position));
        }
        
        return vi;
    }
    
    @Override
    public void onClick(View v) {
            Log.v("CustomAdapter", "=====Row button clicked");
    }
      
    
    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements OnClickListener{           
        private int mPosition;
        
        OnItemClickListener(int position){
        	 mPosition = position;
        }
        
        @Override
        public void onClick(View arg0) {
        	IListBase sct = (IListBase)list;
        	sct.itemClicked(mPosition);
        }               
    }
    
    /********* Called when Item click in ListView ************/
    private class OnLongItemClickListener  implements OnLongClickListener{           
        private int mPosition;
        
        OnLongItemClickListener(int position){
        	 mPosition = position;
        }
        
        @Override
        public boolean onLongClick(View v) {
            
            return false;
        }           
    }
}