package controller;
import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.json.*;

import app.CommentHelper;
import app.Note;
import app.NoteHelper;
import app.PicHelper;
import app.UserHelper;
import tools.JsonReader;

@WebServlet("/api/Note.do")
public class NoteController extends HttpServlet {

	private UserHelper UH = UserHelper.GetUserHelper();
    private NoteHelper NH =  NoteHelper.GetNoteHelper();
    private PicHelper PH = PicHelper.GetPicHelper();
    private CommentHelper CH = CommentHelper.GetCommentHelper();

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 提取 JSON 資料
        JsonReader jsr = new JsonReader(request);
        JSONObject jso = jsr.getObject();

        // 打印上傳的圖片路徑
        String NoteName = jso.getString("NoteName");
        String NoteDescription = jso.getString("NoteDescription");
        String Tag= jso.getString("Tag");
        int Public=jso.getInt("Public");
        String Account=jso.getString("Account");
        Note note = new Note(NoteName,NoteDescription,Tag ,Public,Account);
        JSONObject data = NH.CreateNote(note);
        
        JSONObject resp=new JSONObject();
        resp.put("status", "200");
		resp.put("message", "新增筆記");
        resp.put("response",data);
        //String resp = "{\"status\": \'200\', \"message\": \'筆記創建成功\', \'response\': \'\'}";
        jsr.response(resp, response);
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        JsonReader jsr = new JsonReader(request);
        String  NoteID= jsr.getParameter("NoteID");
        String Account=jsr.getParameter("Account");
        String Sort=jsr.getParameter("Sort");
        String Search=jsr.getParameter("Search");
        //String sortCondition = jso.optString("sortCondition", null);
        //String searchCondition = jso.optString("searchCondition", null);
        
        if(NoteID.isEmpty() && !Account.isEmpty() && Sort.isEmpty() && Search.isEmpty())
        {
        	//用戶資訊欄
        	JSONObject resp = new JSONObject();
        	JSONObject UserInfo=UH.GetUser(Account);
        	JSONObject UserNote=NH.GetOwnNote(Account);
        	resp.put("status", "200");
            resp.put("message", "個人筆記資料取得成功");
            resp.put("UserInfo", UserInfo);
            resp.put("UserNote", UserNote);
            jsr.response(resp, response);
        }
        else if (NoteID.isEmpty() && Account.isEmpty()) {
            // 組合搜索和排序功能
            JSONObject query;
            JSONObject resp = new JSONObject();
            // 有搜尋條件
            if(!Search.isEmpty()){
                switch (Sort) {
                    // 預設為最新的
                    case "0":
                        query = NH.GetNote(Search);
                        break;
                    case "1":
                        query = NH.SearchByLike(Search);
                        break;                 	
                    default:
                    	if(Sort.isEmpty()) {
                    		query = NH.GetNote(Search);
                    	}
                    	else {
                    		query = NH.SearchByYear(Search,Integer.parseInt(Sort));
                    	}
                    	break;
                }            
            }
            else{
                // 無搜尋條件
                switch (Sort) {
                    // 預設為最新的
                    case "0":
                        query = NH.GetNote();
                        break;
                    case "1":
                        query = NH.SearchByLike();
                        break;
                    default:
                        query = NH.SearchByYear(Integer.parseInt(Sort));
                        break;
                }
            }
            
            resp.put("status", "200");
            resp.put("message", "筆記資料取得成功");
            resp.put("response", query);
            jsr.response(resp, response);
        }
        else if(!NoteID.isEmpty() && !Account.isEmpty()){
            // 查看筆記內容
            // input=noteid
            // output=tag.notename.name.notedescription.圖片檔名陣列.留言作者、留言內容
            JSONObject NoteInfo = NH.GetNoteInfo(NoteID,Account);
            JSONObject PicUrl = PH.GetPic(NoteID);
            JSONObject Comment = CH.GetComment(NoteID);
            JSONObject resp = new JSONObject();
            resp.put("status", "200");
            resp.put("message", "筆記資料取得成功");
            resp.put("NoteInfo", NoteInfo);
            resp.put("PicUrl",PicUrl);
            resp.put("Comment", Comment);
            jsr.response(resp, response);
        }
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        // input=noteid
        // output=移除成功視窗      
        JsonReader jsr = new JsonReader(request);
        String  NoteID= jsr.getObject().getString("NoteID");
        JSONObject query = NH.DeleteByNoteID(NoteID);

        JSONObject resp = new JSONObject();
        resp.put("status", "200");
        resp.put("message", "筆記移除成功！");
        resp.put("response", query);
        jsr.response(resp, response);
    }

    public void doPut(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        JsonReader jsr = new JsonReader(request);
        JSONObject jso = jsr.getObject();
        JSONObject resp = new JSONObject();
        //按讚
        // input=like、筆記id、按讚人帳號
        // output=status=200
        if (jso.has("Like")) {
            // 按讚
            String NoteID = jso.getString("NoteID");
            String Account = jso.getString("Account");
            // LikeOrNot true按過讚
            if(NH.LikeOrNot(Account, NoteID))
            {
            	resp=NH.DisLike(Account,NoteID);
            }
            else
            {
            	resp=NH.Like(Account,NoteID);
            }
        } else {
            // 更新筆記資料
            // input=筆記id、標籤、描述、標題、圖片陣列
            // output=更新成功畫面
            String NoteID= jso.getString("NoteID");
            String NoteName = jso.getString("NoteName");
            String NoteDescription = jso.getString("NoteDescription");
            String Tag= jso.getString("Tag");
            int Public=jso.getInt("Public");

            Note note = new Note(NoteID,NoteName,NoteDescription,Tag,Public);
            JSONObject data = NH.UpdateNote(note);
            resp.put("status", "200");
            resp.put("message", "成功! 更新筆記資料...");
            resp.put("response", data);
        }

        
        /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
        jsr.response(resp, response);
    }
}
