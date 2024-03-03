package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.*;

import app.NotificationHelper;
import tools.JsonReader;

/**
 * Servlet implementation class NotifiactionController
 */
@WebServlet("/api/Notification.do")
public class NotifiactionController extends HttpServlet {
    
	private NotificationHelper NTH= NotificationHelper.GetNotificationHelper();

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		JsonReader jsr = new JsonReader(request);
		String Account = jsr.getParameter("Account");
		
		JSONObject notification=NTH.GetNotification(Account);
		JSONObject resp = new JSONObject();
        resp.put("status", "200");
        resp.put("message", "新通知");
        resp.put("notification", notification);
        
        jsr.response(resp, response);
	}

}
