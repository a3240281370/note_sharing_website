package app;

import org.json.*;

public class User 
{
	private String Account;
	private String Name;
	private String Password;
	private int Lock;
	
	public User(String Account,String Name,String Password,int Lock) 
	{
		this.Account=Account;
		this.Name=Name;
		this.Password=Password;
		this.Lock=Lock;
	}
	
	public String GetAccount()
	{
		return this.Account;
	}
	
	public String GetName()
	{
		return this.Name;
	}
	
	public String GetPassword()
	{
		return this.Password;
	}
	
	public int GetLock() 
	{
		return this.Lock;
	}
	
	public JSONObject GetUserData() 
	{
		JSONObject user = new JSONObject();
		user.put("Account",GetAccount());
		user.put("Name",GetName());
		user.put("Password", GetPassword());
		user.put("Lock",GetLock());
		
		return user;
	}
}