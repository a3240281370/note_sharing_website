package app;

import java.sql.*;
import org.json.*;
import util.DBConnector;

public class UserHelper 
{
	
	private UserHelper() 
	{
		
	}
	
	private static UserHelper UH;
	
	private Connection conn=null;
	private PreparedStatement pres=null;
	
	public static UserHelper GetUserHelper()
	{
		if(UH==null)
		{
			UH=new UserHelper();
		}
		return UH;
	}
	
	//Admin管理畫面
	public JSONObject GetUser() 
	{
		User user=null;
		JSONArray UserList=new JSONArray();
		ResultSet rs=null;
		
		try
		{
			conn=DBConnector.GetConnection();
			String sql="call sa.GetAllUser()";
			pres=conn.prepareStatement(sql);
			rs = pres.executeQuery();
			
			while(rs.next())
			{
				String Account=rs.getString("Account");
				String Name=rs.getString("Name");
				String Password=rs.getString("Password");
				int Lock=rs.getInt("Lock");
				user = new User(Account,Name,Password,Lock);
				UserList.put(user.GetUserData());
			}
		}
		catch (SQLException e) 
		{
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } 
		catch (Exception e) 
		{
            e.printStackTrace();
        } 
		finally 
		{
            DBConnector.close(rs, pres, conn);
        }
		
		JSONObject response=new JSONObject();
		response.put("UserList",UserList);
		
		return response;
	}

	//用戶修改資訊畫面
	public JSONObject GetUser(String A) 
	{
		User user=null;
		JSONArray UserList=new JSONArray();
		ResultSet rs=null;
		
		try
		{
			conn=DBConnector.GetConnection();
			String sql="select * from `sa`.`user` where Account=?";
			pres=conn.prepareStatement(sql);
			pres.setString(1,A);
			rs = pres.executeQuery();
			while(rs.next())
			{
				String Account=rs.getString("Account");
				String Name=rs.getString("Name");
				String Password=rs.getString("Password");
				int Lock=rs.getInt("Lock");
				
				user = new User(Account,Name,Password,Lock);
				UserList.put(user.GetUserData());
			}
		}
		catch (SQLException e) 
		{
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } 
		catch (Exception e) 
		{
            e.printStackTrace();
        } 
		finally 
		{
            DBConnector.close(rs, pres, conn);
        }
		
		JSONObject response=new JSONObject();
		response.put("UserList",UserList);
		
		return response;
	}
	
	//管理員搜尋
	public JSONObject SearchUser(String keyword)
	{
		User user=null;
		JSONArray UserList=new JSONArray();
		ResultSet rs=null;
		int row=0;
		try
		{
			conn=DBConnector.GetConnection();
			String sql="call sa.SearchAccount(?);";
			pres=conn.prepareStatement(sql);
			pres.setString(1,keyword);
			rs = pres.executeQuery();
			while(rs.next())
			{
				String Account=rs.getString("Account");
				String Name=rs.getString("Name");
				String Password=rs.getString("Password");
				int Lock=rs.getInt("Lock");
				row++;
				user = new User(Account,Name,Password,Lock);
				UserList.put(user.GetUserData());
			}
		}
		catch (SQLException e) 
		{
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } 
		catch (Exception e) 
		{
            e.printStackTrace();
        } 
		finally 
		{
            DBConnector.close(rs, pres, conn);
        }
		if(row==0) {
			JSONObject SearchNull = new JSONObject();
			SearchNull.put("UserList","查無結果");
			UserList.put(SearchNull);
		}
		JSONObject response=new JSONObject();
		response.put("UserList",UserList);
		
		return response;
	}
	
	public boolean LoginCheck(String Account,String Password)
	{
		boolean check=false;
		ResultSet rs=null;
		try
		{
			conn = DBConnector.GetConnection();
			String sql="select * from `sa`.`user` where Account=? and Password=?";
			pres=conn.prepareStatement(sql);
			pres.setString(1,Account);
			pres.setString(2, Password);
			
			rs=pres.executeQuery();
			if(rs.next())
			{
				String A=rs.getString("Account");
				String P=rs.getString("Password");
				if(A.equals(Account) && P.equals(Password))
				{
					check=true;
				}
			}
		}
		catch (SQLException e)
		{
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } 
		catch (Exception e) 
		{
            e.printStackTrace();
        } 
		finally 
		{
            DBConnector.close(rs, pres, conn);
        }
		
		return check;
	}
	
	public boolean CheckAccount(User user) 
	{
		int row=0;
		ResultSet rs = null;
		try
		{
			conn = DBConnector.GetConnection();
			String sql="select count(*) from `sa`.`user` where Account=?";
			pres=conn.prepareStatement(sql);
			pres.setString(1,user.GetAccount());
			
			rs=pres.executeQuery();
			rs.next();
			row=rs.getInt("count(*)");
			System.out.println(row);
		}
		catch (SQLException e)
		{
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } 
		catch (Exception e) 
		{
            e.printStackTrace();
        } 
		finally 
		{
            DBConnector.close(rs, pres, conn);
        }
		
		if(row==0)
		{
			return true;
		}
		return false;
	}
	
	public boolean CheckName(User user) 
	{
		int row=-1;
		ResultSet rs = null;
		try
		{
			conn = DBConnector.GetConnection();
			String sql="select count(*) from `sa`.`user` where Name=?";
			pres=conn.prepareStatement(sql);
			pres.setString(1,user.GetName());
			
			rs=pres.executeQuery();
			rs.next();
			row=rs.getInt("count(*)");
			System.out.println(row);
		}
		catch (SQLException e)
		{
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } 
		catch (Exception e) 
		{
            e.printStackTrace();
        } 
		finally 
		{
            DBConnector.close(rs, pres, conn);
        }
		
		if(row==0)
		{
			return true;
		}
		return false;
	}
	
	public JSONObject CreateUser(User user)
	{
		conn=DBConnector.GetConnection();
		String sql="";
		try
		{
			sql="call sa.CreateAccount(?,?,?)";
			pres=conn.prepareStatement(sql);
			pres.setString(1,user.GetAccount());
			pres.setString(2,user.GetName());
			pres.setString(3,user.GetPassword());
			pres.executeQuery();
			System.out.println(pres.toString());
		}
		catch (SQLException e) 
		{
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } 
		catch (Exception e) 
		{
            e.printStackTrace();
        } 
		finally 
		{
            DBConnector.close(pres, conn);
        }
		
		JSONObject response = new JSONObject();
		response.put("message", "創建用戶");
		response.put("sql",pres.toString());
		
		return response;
	}
	
	//使用者更新資料
	public JSONObject UpdateUserInfo(User user)
	{
		conn=DBConnector.GetConnection();
		String sql="call sa.UpdateAccount(?,?,?)";
		
		try
		{
			pres=conn.prepareStatement(sql);
			pres.setString(1,user.GetAccount());
			pres.setString(2, user.GetName());
			pres.setString(3,user.GetPassword());
			pres.executeQuery();
			System.out.println(pres.toString());
		}
		catch (SQLException e) 
		{
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } 
		catch (Exception e) 
		{
            e.printStackTrace();
        } 
		finally 
		{
            DBConnector.close(pres, conn);
        }
		
		JSONObject response = new JSONObject();
		response.put("message", "更新用戶");
		response.put("sql",pres.toString());
		
		return response;
	}
	
	//管理員刪除會員
	public JSONObject DeleteByAccount(String Account)
	{
		String sql="";
		try 
		{
			conn=DBConnector.GetConnection();
			sql="call sa.DeleteUser(?)";
			pres=conn.prepareStatement(sql);
			pres.setString(1,Account);
			pres.executeQuery();
		}
		catch (SQLException e) 
		{
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } 
		catch (Exception e) 
		{
            e.printStackTrace();
        } 
		finally 
		{
            DBConnector.close(pres, conn);
        }
		
		JSONObject response = new JSONObject();
		response.put("message", "刪除用戶");
		response.put("sql",pres.toString());
		return response;
	}
	
	public boolean CheckLock(String Account)
	{
		int lock=0;
		conn=DBConnector.GetConnection();
		String sql="select User.Lock from User where Account=?";
		ResultSet rs=null;
		try
		{
			pres=conn.prepareStatement(sql);
			pres.setString(1, Account);
			rs=pres.executeQuery();
			rs.next();
			lock=rs.getInt("Lock");
		}
		catch (SQLException e) 
		{
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } 
		catch (Exception e) 
		{
            e.printStackTrace();
        } 
		finally
		{
			DBConnector.close(pres, conn);
		}
		
		if(lock==1)
		{
			return true;
		}
		return false;
	}
	
	//管理員封鎖會員
	public JSONObject Lock(String Account)
	{
		conn=DBConnector.GetConnection();
		String sql="update User set User.Lock=1 where User.Account=?";
		try
		{
			pres=conn.prepareStatement(sql);
			pres.setString(1, Account);
			pres.executeUpdate();
			System.out.println(pres.toString());
		}
		catch (SQLException e) 
		{
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } 
		catch (Exception e) 
		{
            e.printStackTrace();
        } 
		finally
		{
			DBConnector.close(pres, conn);
		}
		
		JSONObject response = new JSONObject();
		response.put("sql",pres.toString());
		
		return response;
	}
	
	//管理員解除封鎖
	public JSONObject UnLock(String Account)
	{
		conn=DBConnector.GetConnection();
		String sql="update User set User.Lock=0 where User.Account=?";
		try
		{
			pres=conn.prepareStatement(sql);
			pres.setString(1, Account);
			pres.executeUpdate();
			System.out.println(pres.toString());
		}
		catch (SQLException e) 
		{
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } 
		catch (Exception e) 
		{
            e.printStackTrace();
        } 
		finally
		{
			DBConnector.close(pres, conn);
		}
		
		JSONObject response = new JSONObject();
		response.put("sql",pres.toString());
		
		return response;
	}

}