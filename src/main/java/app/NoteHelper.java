package app;

import java.sql.*;
import java.text.SimpleDateFormat;

import org.json.*;
import util.DBConnector;

public class NoteHelper 
{
	private NoteHelper()
	{
		
	}
	
	private static NoteHelper NH;
	private PicHelper PH = PicHelper.GetPicHelper();
	
	private Connection conn=null;
	private PreparedStatement pres=null;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static NoteHelper GetNoteHelper()
	{
		if(NH==null)
		{
			NH=new NoteHelper();
		}
		return NH;
	}
	
	
	//全部Note
	public JSONObject GetNote()
	{
		Note note=null;
		JSONArray NoteList=new JSONArray();
		ResultSet rs=null;
		try
		{
			conn=DBConnector.GetConnection();
			String sql="call sa.GetAllNote()";
			pres=conn.prepareStatement(sql);
			rs = pres.executeQuery();
			
			while(rs.next())
			{
				String NoteID=rs.getString("NoteID");
				String NoteName=rs.getString("NoteName");
				String NoteDescription=rs.getString("NoteDescription");
				String UploadTime=dateFormat.format(rs.getTimestamp("UploadTime"));
				String Tag=rs.getString("Tag");
				int Like=rs.getInt("Like");
				String Account=rs.getString("Account");
				String Cover=PH.GetCover(NoteID);
				String Name=rs.getString("Name");
				note=new Note(NoteID,NoteName,NoteDescription,UploadTime,Tag,Like,Cover,Account,Name);
				NoteList.put(note.BrowseNote());
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
		response.put("NoteList",NoteList);
		
		return response;
	}
	
	//取得自己的筆記
	public JSONObject GetOwnNote(String A)
	{
		Note note=null;
		JSONArray NoteList=new JSONArray();
		ResultSet rs=null;
		int row=0;
		try
		{
			conn=DBConnector.GetConnection();
			String sql="call sa.GetOwnNote(?)";
			pres=conn.prepareStatement(sql);
			pres.setString(1,A);
			rs = pres.executeQuery();
			while(rs.next())
			{
				String NoteID=rs.getString("NoteID");
				String NoteName=rs.getString("NoteName");
				String NoteDescription=rs.getString("NoteDescription");
				String UploadTime=dateFormat.format(rs.getTimestamp("UploadTime"));
				String Tag=rs.getString("Tag");
				int Like=rs.getInt("Like");
				String Account=rs.getString("Account");
				String Cover=PH.GetCover(NoteID);
				String Name=rs.getString("Name");
				note=new Note(NoteID,NoteName,NoteDescription,UploadTime,Tag,Like,Cover,Account,Name);
				NoteList.put(note.BrowseNote());
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
			JSONObject SearchNull = new JSONObject();
			SearchNull.put("NoteID","暫無筆記");
			NoteList.put(SearchNull);
		}
		JSONObject response=new JSONObject();
		response.put("NoteList",NoteList);
		
		return response;
	}
	
	//關鍵字搜尋
	public JSONObject GetNote(String word)
	{
		Note note=null;
		JSONArray NoteList=new JSONArray();
		ResultSet rs=null;
		int row=0;
		try
		{
			conn=DBConnector.GetConnection();
			String sql="call sa.SearchByWord(?)";
			pres=conn.prepareStatement(sql);
			pres.setString(1,word);
			rs = pres.executeQuery();
			while(rs.next())
			{
				String NoteID=rs.getString("NoteID");
				String NoteName=rs.getString("NoteName");
				String NoteDescription=rs.getString("NoteDescription");
				String UploadTime=dateFormat.format(rs.getTimestamp("UploadTime"));
				String Tag=rs.getString("Tag");
				int Like=rs.getInt("Like");
				String Account=rs.getString("Account");
				String Cover=PH.GetCover(NoteID);
				String Name=rs.getString("Name");
				note=new Note(NoteID,NoteName,NoteDescription,UploadTime,Tag,Like,Cover,Account,Name);
				NoteList.put(note.BrowseNote());
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
			JSONObject SearchNull = new JSONObject();
			SearchNull.put("NoteID","查無結果");
			NoteList.put(SearchNull);
		}
		JSONObject response=new JSONObject();
		response.put("NoteList",NoteList);
		
		return response;
	}
	
	//查看筆記畫面
	public JSONObject GetNoteInfo(String NoteID,String Account)
	{
		Note note=null;
		JSONArray NoteData=new JSONArray();
		ResultSet rs=null;
		String ID=null;
		String NoteName=null;
		String NoteDescription=null;
		String UploadTime=null;
		String Tag=null;
		int Like=0;
		int Public=0;
		String A=null;
		String Name=null;
		int good=0;
		
		try
		{
			conn=DBConnector.GetConnection();
			String sql="call sa.GetNoteInfo(?)";
			pres=conn.prepareStatement(sql);
			pres.setString(1,NoteID);
			rs = pres.executeQuery();
			while(rs.next())
			{
				ID=rs.getString("NoteID");
				NoteName=rs.getString("NoteName");
				NoteDescription=rs.getString("NoteDescription");
				UploadTime=dateFormat.format(rs.getTimestamp("UploadTime"));
				Tag=rs.getString("Tag");
				Like=rs.getInt("Like");
				Public=rs.getInt("Public");
				A=rs.getString("Account");
				Name=rs.getString("Name");
			}
			DBConnector.close(rs, pres, conn);
			
			conn=DBConnector.GetConnection();
			sql="select count(*) from Like_tbl where NoteID=? and Account=?";
			pres=conn.prepareStatement(sql);
			pres.setString(1,NoteID);
			pres.setString(2,Account);
			rs = pres.executeQuery();
			while(rs.next()) {
				int count=rs.getInt("count(*)");
				if(count==1)
				{
					good=1;
				}
			}
			note=new Note(ID,NoteName,NoteDescription,UploadTime,Tag,Like,Public,A,good,Name);
			NoteData.put(note.NoteInfo());
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
		response.put("NoteData",NoteData);
		
		return response;
		
	}
	
	//按讚數排序
	public JSONObject SearchByLike()
	{
		Note note=null;
		JSONArray NoteList=new JSONArray();
		ResultSet rs=null;
		try
		{
			conn=DBConnector.GetConnection();
			String sql="call sa.SearchNoteByLike()";
			pres=conn.prepareStatement(sql);
			rs = pres.executeQuery();
			
			while(rs.next())
			{
				String NoteID=rs.getString("NoteID");
				String NoteName=rs.getString("NoteName");
				String NoteDescription=rs.getString("NoteDescription");
				String UploadTime=dateFormat.format(rs.getTimestamp("UploadTime"));
				String Tag=rs.getString("Tag");
				int Like=rs.getInt("Like");
				String Account=rs.getString("Account");
				String Cover=PH.GetCover(NoteID);
				String Name=rs.getString("Name");
				note=new Note(NoteID,NoteName,NoteDescription,UploadTime,Tag,Like,Cover,Account,Name);
				NoteList.put(note.BrowseNote());
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
		response.put("NoteList",NoteList);
		
		return response;
	}
	
	//按讚+關鍵字
	public JSONObject SearchByLike(String word)
	{
		Note note=null;
		JSONArray NoteList=new JSONArray();
		ResultSet rs=null;
		int row=0;
		try
		{
			conn=DBConnector.GetConnection();
			String sql="call sa.SearchByLike(?)";
			pres=conn.prepareStatement(sql);
			pres.setString(1,word);
			rs = pres.executeQuery();
			while(rs.next())
			{
				String NoteID=rs.getString("NoteID");
				String NoteName=rs.getString("NoteName");
				String NoteDescription=rs.getString("NoteDescription");
				String UploadTime=dateFormat.format(rs.getTimestamp("UploadTime"));
				String Tag=rs.getString("Tag");
				int Like=rs.getInt("Like");
				String Account=rs.getString("Account");
				String Cover=PH.GetCover(NoteID);
				String Name=rs.getString("Name");
				note=new Note(NoteID,NoteName,NoteDescription,UploadTime,Tag,Like,Cover,Account,Name);
				NoteList.put(note.BrowseNote());
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
			JSONObject SearchNull = new JSONObject();
			SearchNull.put("NoteID","查無結果");
			NoteList.put(SearchNull);
		}
		JSONObject response=new JSONObject();
		response.put("NoteList",NoteList);
		
		return response;
	}
	
	//指定年分搜尋
	public JSONObject SearchByYear(int year)
	{
		Note note=null;
		JSONArray NoteList=new JSONArray();
		ResultSet rs=null;
		int row=0;
		try
		{
			conn=DBConnector.GetConnection();
			String sql="call sa.SearchNoteByYear(?)";
			pres=conn.prepareStatement(sql);
			pres.setInt(1,year);
			rs = pres.executeQuery();
			while(rs.next())
			{
				String NoteID=rs.getString("NoteID");
				String NoteName=rs.getString("NoteName");
				String NoteDescription=rs.getString("NoteDescription");
				String UploadTime=dateFormat.format(rs.getTimestamp("UploadTime"));
				String Tag=rs.getString("Tag");
				int Like=rs.getInt("Like");
				String Account=rs.getString("Account");
				String Cover=PH.GetCover(NoteID);
				String Name=rs.getString("Name");
				note=new Note(NoteID,NoteName,NoteDescription,UploadTime,Tag,Like,Cover,Account,Name);
				NoteList.put(note.BrowseNote());
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
			JSONObject SearchNull = new JSONObject();
			SearchNull.put("NoteID","查無結果");
			NoteList.put(SearchNull);
		}
		JSONObject response=new JSONObject();
		response.put("NoteList",NoteList);
		
		return response;
	}
	
	//年分+關鍵字
	public JSONObject SearchByYear(String word,int year)
	{
		Note note=null;
		JSONArray NoteList=new JSONArray();
		ResultSet rs=null;
		int row=0;
		try
		{
			conn=DBConnector.GetConnection();
			String sql="call sa.SearchByYear(?,?)";
			pres=conn.prepareStatement(sql);
			pres.setString(1,word);
			pres.setInt(2,year);
			rs = pres.executeQuery();
			while(rs.next())
			{
				String NoteID=rs.getString("NoteID");
				String NoteName=rs.getString("NoteName");
				String NoteDescription=rs.getString("NoteDescription");
				String UploadTime=dateFormat.format(rs.getTimestamp("UploadTime"));
				String Tag=rs.getString("Tag");
				int Like=rs.getInt("Like");
				String Account=rs.getString("Account");
				String Cover=PH.GetCover(NoteID);
				String Name=rs.getString("Name");
				note=new Note(NoteID,NoteName,NoteDescription,UploadTime,Tag,Like,Cover,Account,Name);
				NoteList.put(note.BrowseNote());
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
			JSONObject SearchNull = new JSONObject();
			SearchNull.put("NoteID","查無結果");
			NoteList.put(SearchNull);
		}
		JSONObject response=new JSONObject();
		response.put("NoteList",NoteList);
		
		return response;
	}
	
	public JSONObject CreateNote(Note note)
	{
		conn=DBConnector.GetConnection();
		String sql="call sa.CreateNote(?,?,?,?,?)";
		String NoteID="";
		ResultSet rs=null;
		try
		{
			pres=conn.prepareStatement(sql);
			pres.setString(1, note.GetNoteName());
			pres.setString(2, note.GetNoteDescription());
			pres.setString(3,note.GetTag());
			pres.setInt(4,note.GetPublic());
			pres.setString(5, note.GetAccount());
			rs=pres.executeQuery();
			while(rs.next()) 
			{
				NoteID=rs.getString("NoteID");
			};
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
		System.out.println(NoteID);
		
		JSONObject response = new JSONObject();
		response.put("NoteID",NoteID);
		
		return response;
	}
	
	public JSONObject UpdateNote(Note note)
	{
		conn=DBConnector.GetConnection();
		String sql="call sa.UpdateNote(?,?,?,?,?)";
		try
		{
			pres=conn.prepareStatement(sql);
			pres.setString(1, note.GetNoteID());
			pres.setString(2, note.GetNoteName());
			pres.setString(3,note.GetNoteDescription());
			pres.setString(4,note.GetTag());
			pres.setInt(5, note.GetPublic());
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
		response.put("message", "更新筆記");
		response.put("sql",pres.toString());
		
		return response;
	}
	
	public JSONObject DeleteByNoteID(String NoteID)
	{
		String sql="";
		//ResultSet rs=null;
		try
		{
			conn=DBConnector.GetConnection();
			sql="call sa.DeleteNote(?)";
			pres=conn.prepareStatement(sql);
			pres.setString(1,NoteID);
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
		response.put("message", "刪除筆記");
		response.put("sql",pres.toString());
		return response;
	}
	
	public boolean LikeOrNot(String Account,String NoteID)
	{
		int row=0;
		ResultSet rs = null;
		try
		{
			conn = DBConnector.GetConnection();
			String sql="select count(*) from sa.Like_tbl where Account=? and NoteID=?";
			pres=conn.prepareStatement(sql);
			pres.setString(1,Account);
			pres.setString(2,NoteID);
			
			rs=pres.executeQuery();
			while(rs.next())
			{
				row=rs.getInt(1);
			}
			
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
			return false;
		}
		return true;
	}
	
	public JSONObject Like(String Account,String NoteID)
	{
		conn=DBConnector.GetConnection();
		String sql="call sa.Like(?,?)";
		try
		{
			pres=conn.prepareStatement(sql);
			pres.setString(1, Account);
			pres.setString(2, NoteID);
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
		response.put("message", "按讚筆記");
		response.put("sql",pres.toString());
		
		return response;
	}
	
	public JSONObject DisLike(String Account,String NoteID)
	{
		conn=DBConnector.GetConnection();
		String sql="call sa.DisLike(?,?)";
		try
		{
			pres=conn.prepareStatement(sql);
			pres.setString(1, Account);
			pres.setString(2, NoteID);
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
		response.put("message", "取消按讚筆記");
		response.put("sql",pres.toString());
		
		return response;
	}

	
}
