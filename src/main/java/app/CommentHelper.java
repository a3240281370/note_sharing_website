package app;

import java.sql.*;
import java.text.SimpleDateFormat;

import org.json.*;
import util.DBConnector;

public class CommentHelper 
{
	private CommentHelper()
	{
		
	}
	
	private static CommentHelper CH;
	
	private Connection conn=null;
	private PreparedStatement pres=null;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
	public static CommentHelper GetCommentHelper()
	{
		if(CH==null)
		{
			CH=new CommentHelper();
		}
		return CH;
	}
	
	public JSONObject GetComment(String NoteID)
	{
		Comment comment=null;
		JSONArray CommentList=new JSONArray();
		ResultSet rs=null;
		int row=0;
		try
		{
			conn=DBConnector.GetConnection();
			String sql="call sa.GetComment(?)";
			pres=conn.prepareStatement(sql);
			pres.setString(1,NoteID);
			rs=pres.executeQuery();
			while(rs.next())
			{
				String CommentID=rs.getString("CommentID");
				String Content=rs.getString("Content");
				String Time=dateFormat.format(rs.getTimestamp("Time"));
				String Name=rs.getString("Name");
				String Account=rs.getString("Account");
				comment=new Comment(CommentID,Content,Time,Name,Account);
				CommentList.put(comment.GetCommentData());
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
			JSONObject CommentNull = new JSONObject();
			CommentNull.put("CommentID","尚無留言");
			CommentList.put(CommentNull);
		}
		JSONObject response=new JSONObject();
		response.put("CommentList",CommentList);
		
		return response;
	}
	
	public JSONObject CreateComment(Comment comment)
	{
		String sql="";
		ResultSet rs=null;
		String CommentID="";
		String Name="";
		String Time="";		 
		try
		{
			conn=DBConnector.GetConnection();
			sql="call sa.CreateComment(?,?,?)";
			pres=conn.prepareStatement(sql);
			pres.setString(1,comment.GetContent());
			pres.setString(2,comment.GetAccount());
			pres.setString(3,comment.GetNoteID());
			rs=pres.executeQuery();
			while(rs.next()) {
				CommentID=rs.getString("CommentID");
				Name=rs.getString("Name");
				Time=dateFormat.format(rs.getTimestamp("Time"));									
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
		
		JSONObject response=new JSONObject();
		response.put("sql",pres.toString());
		response.put("CommentID", CommentID);
		response.put("Name", Name);
		response.put("Time", Time);					 
		return response;
	}

	public JSONObject DeleteComment(String CommentID)
	{
		String sql="";
		try
		{
			conn=DBConnector.GetConnection();
			sql="call sa.DeleteComment(?)";
			pres=conn.prepareStatement(sql);
			pres.setString(1,CommentID);
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
	
	
}
