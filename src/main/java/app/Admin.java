package app;

import org.json.*;

public class Admin 
{
	private String Account;
	private String Password;
	
	public Admin(String Account,String Password)
	{
		this.Account=Account;
		this.Password=Password;
	}
	
	public String GetAccount()
	{
		return this.Account;
	}
	
	public String GetPassword()
	{
		return this.Password;
	}
	
	public JSONObject GetAdminData()
	{
		JSONObject admin=new JSONObject();
		admin.put("Account", GetAccount());
		admin.put("Password", GetPassword());
		
		return admin;
	}

}
