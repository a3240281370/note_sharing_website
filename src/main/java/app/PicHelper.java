package app;

import java.sql.*;
import org.json.*;
import util.DBConnector;

public class PicHelper 
{
	private PicHelper()
	{
		
	}
	
	private static PicHelper PH;
	
	private Connection conn=null;
	private PreparedStatement pres=null;
	
	public static PicHelper GetPicHelper()
	{
		if(PH==null)
		{
			PH=new PicHelper();
		}
		return PH;
	}
	
	public String GetCover(String NoteID) {
		String CoverURL="";
		ResultSet rs=null;
		try
		{
			conn=DBConnector.GetConnection();
			String sql="select PicURL from pic where NoteID=? and Sequence=1";
			pres=conn.prepareStatement(sql);
			pres.setString(1, NoteID);
			rs=pres.executeQuery();
			
			while(rs.next())
			{
				CoverURL=rs.getString("PicURL");
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
		
		return CoverURL;
	}
	
	public JSONObject GetPic(String NoteID)
	{
		Pic pic=null;
		JSONArray PicList=new JSONArray();
		ResultSet rs=null;
		
		try
		{
			conn=DBConnector.GetConnection();
			String sql="call sa.GetPic(?)";
			pres=conn.prepareStatement(sql);
			pres.setString(1, NoteID);
			rs=pres.executeQuery();
			
			while(rs.next())
			{
				String PicURL=rs.getString("PicURL");
				int Sequence=rs.getInt("Sequence");
				pic=new Pic(PicURL,Sequence);
				PicList.put(pic.GetPicData());
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
		response.put("PicList",PicList);
		
		return response;
	}
	
	public  JSONObject CreatePic(String[] PicURL,String NoteID)
	{
		conn=DBConnector.GetConnection();
		try
		{
			int index=1;
			for(String url:PicURL)
			{
				String sql="insert into sa.Pic (`PicID`,`PicURL`,`Sequence`,`NoteID`) values (MD5(CONCAT(sysdate(6), RAND())),?,?,?)";
				pres=conn.prepareStatement(sql);
				pres.setString(1, url);
				pres.setInt(2, index);
				pres.setString(3, NoteID);
				pres.executeUpdate();
				System.out.println(sql);
				System.out.println(pres.toString());
				index++;
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
            DBConnector.close(pres, conn);
        }
		
		JSONObject response = new JSONObject();
		response.put("pic","新增照片成功");
		
		return response;
	}
}
