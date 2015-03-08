package com.example.layoutdesign.Model;

import java.util.ArrayList;
import java.util.List;

public class ContactNumber {

	private long ID;
	private String name;
	private List<String> emails;
	private List<String> phones;
	
	public void setID(long id)
	{
		this.ID = id;
	}
	
	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return this.name;
	}
	
	public List<String> getEmail()
	{
		return this.emails;
	}
	
	public List<String> getPhoneNumber()
	{
		return this.phones;
	}
	
	public ContactNumber(){
		this.emails = new ArrayList<String>();
		this.phones = new ArrayList<String>();
	}
}
	
	 