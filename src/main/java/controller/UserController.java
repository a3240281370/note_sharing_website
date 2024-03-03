package controller;
import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.json.*;

import app.NotificationHelper;
import app.User;
import app.UserHelper;
import tools.JsonReader;

@WebServlet("/api/User.do")
public class UserController extends HttpServlet {
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        // input=帳號、姓名、密碼
        // output=註冊成功畫面
        JsonReader jsr = new JsonReader(request);
        JSONObject jso = jsr.getObject();
        String Account = jso.getString("Account");
        String Password = jso.getString("Password");
        String Name = jso.getString("Name");
        /** 後端檢查是否有欄位為空值，若有則回傳錯誤訊息 */
        if(!Account.isEmpty() && !Password.isEmpty() && Name.equals("none"))
        {
        	if(!UH.LoginCheck(Account,Password)){
                String resp = "{\"status\": \'400\', \"message\": \'帳號或密碼錯誤\', \'response\': \'\'}";
                jsr.response(resp, response);
            }
            else{
            	if(UH.CheckLock(Account)) {
            		String resp = "{\"status\": \'400\', \"message\": \'此用戶已被封鎖，請洽管理員諮詢\', \'response\': \'\'}";
                    jsr.response(resp, response);
            	}
            	else {
            		String resp="";
                	//判斷有無新通知
                	if(NTH.GetNewStatus(Account))
                	{
                		resp="{\"status\": \'200\', \"message\": \'登入成功\', \'NewStatus\': \'1\'}";
                	}
                	else
                	{
                		resp="{\"status\": \'200\', \"message\": \'登入成功\', \'NewStatus\': \'0\'}";
                	} 
                    jsr.response(resp, response);
            	}
            }
        }
        else
        {
        	if(Account.isEmpty() || Password.isEmpty() || Name.isEmpty()) {
                String resp = "{\"status\": \'400\', \"message\": \'欄位不能有空值\', \'response\': \'\'}";
                jsr.response(resp, response);
            }
            else
            {
            	User user = new User(Account,Name,Password,0);
            	if(UH.CheckAccount(user))
            	{
            		if(UH.CheckName(user))
            		{
            			JSONObject data=UH.CreateUser(user);
                        JSONObject resp = new JSONObject();
                        resp.put("status", "200");
                        resp.put("message", "成功! 註冊會員資料...");
                        resp.put("response", data);
                        jsr.response(resp, response);
            		}
            		else
            		{
            			String resp = "{\"status\": \'400\', \"message\": \'新增帳號失敗，名稱重複！\', \'response\': \'\'}";
                        jsr.response(resp, response);
            		}
            	}
            	else
            	{
            		 String resp = "{\"status\": \'400\', \"message\": \'新增帳號失敗，此帳號重複！\', \'response\': \'\'}";
                     jsr.response(resp, response);
            	}
            }
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        JsonReader jsr = new JsonReader(request);
        String Name=jsr.getParameter("Name");
        String Account=jsr.getParameter("Account");
            if (Name.isEmpty() && Account.isEmpty()) {
	            // 檢視所有用戶
	            // input=null
	            // output=帳號、名字
                JSONObject query = UH.GetUser();
                JSONObject resp = new JSONObject();
                resp.put("status", "200");
                resp.put("message", "所有用戶資料取得成功");
                resp.put("response", query);
                jsr.response(resp, response);
            }
            else if(!Name.isEmpty() && Account.isEmpty()){
                // 搜尋用戶
                // input=姓名
                // output=帳號、姓名
                JSONObject query = UH.SearchUser(Name);
                JSONObject resp = new JSONObject();
                resp.put("status", "200");
                resp.put("message", "用戶資料取得成功");
                resp.put("response", query);
                jsr.response(resp, response);
            }
            else {
            	//用戶修改資訊畫面
            	JSONObject query = UH.GetUser(Account);
            	JSONObject resp = new JSONObject();
                resp.put("status", "200");
                resp.put("message", "個人資料取得成功");
                resp.put("response", query);
                jsr.response(resp, response);
            }
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        JsonReader jsr = new JsonReader(request);
        JSONObject jso = jsr.getObject();
        String Account = jso.getString("Account");
        
        JSONObject query = UH.DeleteByAccount(Account);
        JSONObject resp = new JSONObject();
        resp.put("status", "200");
        resp.put("message", "用戶移除成功！");
        resp.put("response", query);
        jsr.response(resp, response);
    }

    public void doPut(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
            JsonReader jsr = new JsonReader(request);
            JSONObject jso = jsr.getObject();    
            JSONObject resp = new JSONObject();

            if (jso.has("Lock")) {
                // 封鎖
                // input=帳號
                // output=成功封鎖用戶畫面
                String Account = jso.getString("Account");
                // CheckLock會回傳是否被封鎖
                //true 被封鎖
                if (!UH.CheckLock(Account)) {
                	JSONObject data=UH.Lock(Account);
                    resp.put("status", "200");
                    resp.put("message", "已封鎖用戶...");
                    resp.put("sql",data);
                } else {
                	JSONObject data=UH.UnLock(Account);
                    resp.put("status", "200");
                    resp.put("message", "解除封鎖用戶...");
                    resp.put("sql",data);
                }
            } 
            else {
                // 修改會員資料
                // input=新密碼、新名字、帳號原本的
                // output=更新成功畫面
                String Account = jso.getString("Account");
                String Password = jso.getString("Password");
                String Name = jso.getString("Name");

                User user = new User(Account, Name, Password,0);
                JSONObject data=UH.UpdateUserInfo(user);             
                resp.put("status", "200");
                resp.put("message", "成功! 更新會員資料...");
                resp.put("response", data);
                
            }       
        jsr.response(resp, response);
    }
}