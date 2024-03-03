package app;

import org.json.*;

public class Note 
{
	private String NoteID;
	private String NoteName;
	private String NoteDescription;
	private String UploadTime;
	private String Tag;
	private int Like;
	private int Public;
	private String Account;
	private int good;
	private String Cover;
	private String Name;
	
	//瀏覽時Note的資訊
	public Note(String NoteID,String NoteName,String NoteDescription,String UploadTime,String Tag,int Like,String Cover,String Account,String Name) 
	{
		this.NoteID=NoteID;
		this.NoteName=NoteName;
		this.NoteDescription=NoteDescription;
		this.UploadTime=UploadTime;
		this.Tag=Tag;
		this.Like=Like;
		this.Cover=Cover;
		this.Account=Account;
		this.Name=Name;
	}
	
	//創建筆記時的建構子
	public Note(String NoteName,String NoteDescription,String Tag,int Public,String Account) 
	{
		this.NoteName=NoteName;
		this.NoteDescription=NoteDescription;
		this.Tag=Tag;
		this.Public=Public;
		this.Account=Account;
	}
	
	//查看筆記時的建構子
	public Note(String NoteID,String NoteName,String NoteDescription,String UploadTime,String Tag,int Like,int Public,String Account,int good,String Name) 
	{
		this.NoteID=NoteID;
		this.NoteName=NoteName;
		this.NoteDescription=NoteDescription;
		this.UploadTime=UploadTime;
		this.Tag=Tag;
		this.Like=Like;
		this.Public=Public;
		this.Account=Account;
		this.good=good;
		this.Name=Name;
	}
	
	//更新筆記時的建構子
	public Note(String NoteID,String NoteName,String NoteDescription,String Tag,int Public) 
	{
		this.NoteID=NoteID;
		this.NoteName=NoteName;
		this.NoteDescription=NoteDescription;
		this.Tag=Tag;
		this.Public=Public;
	}
	
	public String GetNoteID()
	{
		return this.NoteID;
	}
	
	public String GetNoteName()
	{
		return this.NoteName;
	}
	
	public String GetNoteDescription()
	{
		return this.NoteDescription;
	}
	
	public String GetUploadTime()
	{
		return this.UploadTime;
	}
	
	public String GetTag()
	{
		return this.Tag;
	}
	
	public int GetLike()
	{
		return this.Like;
	}
	
	public int GetPublic()
	{
		return this.Public;
	}
	
	public String GetAccount()
	{
		return this.Account;
	}
	
	public int GetGood()
	{
		return this.good;
	}
	
	public String GetCover() {
		return this.Cover;
	}
	
	public String GetName() {
		return this.Name;
	}
	
	public JSONObject BrowseNote()
	{
		JSONObject note = new JSONObject();
		note.put("NoteID", GetNoteID());
		note.put("NoteName", GetNoteName());
		note.put("NoteDescription", GetNoteDescription());
		note.put("UploadTime", GetUploadTime());
		note.put("Tag", GetTag());
		note.put("Like",GetLike());
		note.put("Cover", GetCover());
		note.put("Account",GetAccount());
		note.put("Name", GetName());
		return note;
	}
	
	public JSONObject NoteInfo()
	{
		JSONObject note = new JSONObject();
		note.put("NoteID", GetNoteID());
		note.put("NoteName", GetNoteName());
		note.put("NoteDescription", GetNoteDescription());
		note.put("UploadTime", GetUploadTime());
		note.put("Tag", GetTag());
		note.put("Like",GetLike());
		note.put("Account", GetAccount());
		note.put("good", GetGood());
		note.put("Name", GetName());
		return note;
	}
}
