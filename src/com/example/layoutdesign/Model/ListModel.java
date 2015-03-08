package com.example.layoutdesign.Model;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Spanned;

public class ListModel {

	private  String text;
	private  String image;
	private  String text1;
	private Bitmap image1;
	private Spanned spanText;
	private Spanned spanText1;

	public Bundle extraData;

	public ListModel(){
		this.text = this.image = this.text1 = "";
	}

	public void setText(String text)
	{
		this.text = text;
	}
	public void setSpanText(Spanned sp)
	{
		this.spanText = sp;
	}
	public void setSpanText1(Spanned sp)
	{
		this.spanText1 = sp;
	}

	public void setImage(String Image)
	{
		this.image = Image;
	}
	
	public void setImage1(Bitmap image1)
	{
		this.image1 = image1;
	}

	public void setText1(String text1)
	{
		this.text1 = text1;
	}

	public Spanned getSpanText()
	{
		return this.spanText;
	}
	public Spanned getSpanText1()
	{
		return this.spanText1;
	}

	public String getText()
	{
		return this.text;
	}

	public String getImage()
	{
		return this.image;
	}
	
	public Bitmap getImage1()
	{
		return this.image1;
	}

	public String getText1()
	{
		return this.text1;
	}    
}