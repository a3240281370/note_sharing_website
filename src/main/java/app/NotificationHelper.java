package app;

import java.sql.*;
import org.json.*;
import util.DBConnector;

public class NotificationHelper {

	private NotificationHelper() 
	{
		
	}
	
	private static NotificationHelper NTH;
	
	private Connection conn=null;
	private PreparedStatement pres=null;
	
	public static NotificationHelper GetNotificationHelper()
	{
		if(NTH==null)
		{
			NTH=new NotificationHelper();
		}
		return NTH;
	}
	
	//登入時會判斷是否有新通知
	public boolean GetNewStatus(String Account)
	{
		int row=0;
		ResultSet rs=null;
		try
		{
			conn=DBConnector.GetConnection();
			String sql="call sa.GetNewStatus(?)";
			pres=conn.prepareStatement(sql);
			pres.setString(1,Account);
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
            DBConnector.close(pres, conn);
        }
		
		if(row==0)
		{
			return false;
		}
		return true;
	}
	
	//點開通知欄
	public JSONObject GetNotification(String Account)
	{
		JSONObject notification=new JSONObject();
		JSONArray NotificationList=new JSONArray();
		ResultSet rs=null;
		int row=0;
		String sql="";
		try
		{
			conn=DBConnector.GetConnection();
			sql="call sa.GetNotification(?)";
			pres=conn.prepareStatement(sql);
			pres.setString(1,Account);
			rs=pres.executeQuery();
			
			while(rs.next())
			{
				JSONObject note=new JSONObject();
				String NoteID=rs.getString("NoteID");
				String NoteName=rs.getString("NoteName");
				note.put("NoteID",NoteID);
				note.put("NoteName",NoteName);
				NotificationList.put(note);
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
		if(row==0) 
		{
			notification.put("NoteID","無新通知");
			NotificationList.put(notification);
		}
		JSONObject response=new JSONObject();
		response.put("NotificationList",NotificationList);
		
		//將通知設為看過
		try {
			conn=DBConnector.GetConnection();
			sql="call sa.UpdateNotification(?)";
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
            DBConnector.close(rs, pres, conn);
        }
		
		return response;
	}
}
