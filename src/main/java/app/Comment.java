package app;

import org.json.*;

public class Comment {
	private String CommentID;
	private String Content;
	private String Time;
	private String Account;
	private String NoteID;
	private String Name;
	
	//新增留言的建構子
	public Comment(String Content,String Account,String NoteID)
	{
		this.Content=Content;
		this.Account=Account;
		this.NoteID=NoteID;
	}
	
	//查看留言的建構子
	public Comment(String CommentID,String Content,String Time,String Name, String Account)
	{
		this.CommentID=CommentID;
		this.Content=Content;
		this.Time=Time;
		this.Name=Name;
		this.Account=Account; 
	}
	
	public String GetCommentID()
	{
		return this.CommentID;
	}
	public String GetContent()
	{
		return this.Content;
	}
	
	public String GetTime()
	{
		return this.Time;
	}
	
	public String GetAccount()
	{
		return this.Account;
	}
	
	public String GetNoteID()
	{
		return this.NoteID;
	}
	
	public String GetName()
	{
		return this.Name;
	}
	
	public JSONObject GetCommentData()
	{
		JSONObject comment=new JSONObject();
		comment.put("CommentID", GetCommentID());
		comment.put("Content", GetContent());
		comment.put("Time", GetTime());
		comment.put("Name", GetName());
		comment.put("Account", GetAccount());
		return comment;
	}
}
