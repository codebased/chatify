package com.example.layoutdesign.Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.Spanned;
 

public class ChatHistory {

	private String receiver;
	private String Text;
	private String Text1;
	private Date dateTime;
	private Spanned spanText;
	private Spanned spanText1;
	
	public String getReceiver(){
		return this.receiver;
	}

	public void setReceiver(String receiver)
	{
		this.receiver = receiver;
	}
	
	public String getText(){
		return this.Text;
	}
	public String getText1(){
		return this.Text1;
	}
	public void setText(String text)
	{
		this.Text = text;
	}
	public void setText1(String text1)
	{
		this.Text1 = text1;
	}
	
	public Spanned getSpanText()
	{
		return this.spanText;
	}
	public Spanned getSpanText1()
	{
		return this.spanText1;
	}
	public void setSpanText(Spanned sp)
	{
		this.spanText = sp;
	}
	public void setSpanText1(Spanned sp)
	{
		this.spanText1 = sp;
	}
	
	public Date getDateTime(){
		return this.dateTime;
	}

	public void setDateTime(Date d) {
		this.dateTime = d;
	}
	
	public void setDateTime(String d){

	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mmm/yyyy hh:mm:ss aa");

		try {
			this.dateTime = dateFormat.parse(d);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String toString()
	{
		return this.getText() + " " + this.dateTime.toString();
	}
}
