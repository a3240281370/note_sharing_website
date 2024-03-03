package app;

import org.json.*;

public class Pic 
{
	private String PicID;
	private String PicURL;
	private String NoteID;
	private int Sequence;
	
	public Pic(String PicURL,int Sequence)
	{
		this.PicURL=PicURL;
		this.Sequence=Sequence;
	}
	
	public String GetPicID()
	{
		return this.PicID;
	}
	
	public String GetPicURL()
	{
		return this.PicURL;
	}
	
	public String GeNoteID()
	{
		return this.NoteID;
	}
	
	public int GetSequence() {
		return this.Sequence;
	}
	
	public JSONObject GetPicData()
	{
		JSONObject pic = new JSONObject();
		pic.put("PicURL",GetPicURL());
		pic.put("Sequence", GetSequence());
		return pic;
	}
}
