package controller;
import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.json.*;

import app.Comment;
import app.CommentHelper;
import tools.JsonReader;


@WebServlet("/api/Comment.do")
public class CommentController extends HttpServlet {

    private CommentHelper CH = CommentHelper.GetCommentHelper();

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        JsonReader jsr = new JsonReader(request);
        JSONObject jso = jsr.getObject();
        
        // input包括NoteId.Name.Content
        String NoteID = jso.getString("NoteID");
        String Content = jso.getString("Content");
        String Account = jso.getString("Account");

        Comment comment = new Comment(Content,Account,NoteID);
        JSONObject data = CH.CreateComment(comment);
        
        JSONObject resp = new JSONObject();
        resp.put("status", "200");
        resp.put("message", "成功! 新增評論...");
        resp.put("response", data);

        jsr.response(resp, response);
    }


    public void doDelete(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        JsonReader jsr = new JsonReader(request);
        JSONObject jso = jsr.getObject();
        String CommentID = jso.getString("CommentID");
        JSONObject query = CH.DeleteComment(CommentID);

        JSONObject resp = new JSONObject();
        resp.put("status", "200");
        resp.put("message", "評論移除成功！");
        resp.put("response", query);
        jsr.response(resp, response);
    }
}
