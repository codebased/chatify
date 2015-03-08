package com.example.layoutdesign.Model;

public class UserDetail {
	
	public String Name;
	public String Presence;
	public String Status;
	public String Nickname;
	public String UserName;
	public byte[] Image;
	
	public UserDetail()
	{
		this.Name=this.Presence=this.Status="";
	}
	
	public String getName(){
		return this.Name;
	}
	
	public String getPresence(){
		return this.Presence;
	}
	
	public String getNickname(){
		return this.Nickname;
	}
	
	public String getStatus(){
		return this.Status;
	}
	
	public String getUserName(){
		return this.UserName;
	}
	
	public byte[] getImage(){
		return this.Image;
	}
	
	public void setName(String Name)
	{
		this.Name = Name;
	}
	
	public void setUserName(String UserName)
	{
		this.UserName = UserName;
	}
	
	public void setNickname(String Nickname)
	{
		this.Nickname = Nickname;
	}
	
	public void setStatus(String Status)
	{
		this.Status = Status;
	}
	
	public void setPresence(String Presence)
	{
		this.Presence = Presence;
	}
	
	public void setImage(byte[] Image)
	{
		this.Image = Image;
	}
}
