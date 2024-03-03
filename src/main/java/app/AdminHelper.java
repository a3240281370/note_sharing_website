package app;

import java.sql.*;
import org.json.*;
import util.DBConnector;

public class AdminHelper 
{
	private AdminHelper()
	{
		
	}
	
	private static AdminHelper AH;
	
	private Connection conn=null;
	private PreparedStatement pres=null;
	
	public static AdminHelper GetAdminHelper()
	{
		if(AH==null)
		{
			AH=new AdminHelper();
		}
		return AH;
	}
	
	public JSONObject GetAdmin() 
	{
		Admin admin=null;
		JSONArray AdminList=new JSONArray();
		ResultSet rs=null;
		
		try
		{
			conn=DBConnector.GetConnection();
			String sql="select * from sa.Admin";
			pres=conn.prepareStatement(sql);
			rs = pres.executeQuery();
			
			while(rs.next())
			{
				String Account=rs.getString("Account");
				String Password=rs.getString("Password");
				admin = new Admin(Account,Password);
				AdminList.put(admin.GetAdminData());
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
		response.put("AdminList",AdminList);
		
		return response;
	}
	
	public JSONObject SearchAdmin(String keyword) 
	{
		Admin admin=null;
		JSONArray AdminList=new JSONArray();
		ResultSet rs=null;
		int row=0;
		try
		{
			conn=DBConnector.GetConnection();
			String sql="select * from sa.Admin where Admin.Account Like %?%";
			pres=conn.prepareStatement(sql);
			pres.setString(1,keyword);
			rs = pres.executeQuery();
			while(rs.next())
			{
				String Account=rs.getString("Account");
				String Password=rs.getString("Password");
				admin = new Admin(Account,Password);
				AdminList.put(admin.GetAdminData());
				row++;
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
			SearchNull.put("AdminList","查無結果");
			AdminList.put(SearchNull);
		}
		JSONObject response=new JSONObject();
		response.put("AdminList",AdminList);
		
		return response;
	}
	
	public boolean LoginCheck(String Account,String Password)
	{
		boolean check=false;
		ResultSet rs=null;
		try
		{
			conn = DBConnector.GetConnection();
			String sql="select * from `sa`.`admin` where Account=? and Password=?";
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
	
	public boolean CheckAccount(Admin admin)
	{
		int row=0;
		ResultSet rs=null;
		try
		{
			conn = DBConnector.GetConnection();
			String sql="select count(*) from `sa`.`admin` where Account=?";
			pres=conn.prepareStatement(sql);
			pres.setString(1,admin.GetAccount());
			
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
	
	public JSONObject CreateAdmin(Admin admin) 
	{		
		String sql="";
		try
		{
			conn=DBConnector.GetConnection();
			sql="insert into sa.Admin (`Account`,`Password`) values (?,?)";
			pres=conn.prepareStatement(sql);
			pres.setString(1,admin.GetAccount());
			pres.setString(2, admin.GetPassword());
			pres.execute();
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
		
		JSONObject response=new JSONObject();
		response.put("sql",pres.toString());
		return response;
	}
	
	public JSONObject UpdateAdmin(Admin admin)
	{
		String sql="";
		try
		{
			conn=DBConnector.GetConnection();
			sql="update sa.Admin set Password=? where Account=?";
			pres=conn.prepareStatement(sql);
			pres.setString(1,admin.GetPassword());
			pres.setString(2,admin.GetAccount());
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
		
		JSONObject response=new JSONObject();
		response.put("sql",pres.toString());
		return response;
	}
	
	public JSONObject DeleteAdmin(String Account) 
	{		
		String sql="";
		try
		{
			conn=DBConnector.GetConnection();
			sql="delete from sa.Admin where Account=?";
			pres=conn.prepareStatement(sql);
			pres.setString(1,Account);
			pres.executeUpdate();
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
		
		JSONObject response=new JSONObject();
		response.put("sql",pres.toString());
		return response;
	}
}
