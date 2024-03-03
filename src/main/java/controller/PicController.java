package controller;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import app.PicHelper;
import tools.JsonReader;

/**
 * Servlet implementation class PicController
 */
@MultipartConfig(location="C:\\Users\\user\\eclipse-workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\SA\\static")
@WebServlet("/api/Pic.do")
public class PicController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private PicHelper PH =PicHelper.GetPicHelper();  
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PicController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    // 上傳照片到指定資料夾，不確定是否行
	    ArrayList<String> picUrls = new ArrayList<>();
	    String NoteID = request.getParameter("NoteID");;

	    request.setCharacterEncoding("UTF-8");
	    
	    // Iterate over parts in the request
	    for (Part part : request.getParts()) {
	        // Check if the part is not the 'upload' field
	        if (!"upload".equals(part.getName())) {
	            // If it's a text field
	            if (part.getContentType() == null) {
	                
	                
	            } else {
	                // If it's a file field, process it as you were doing before
	                try {
	                    part.write(part.getSubmittedFileName());
	                    //picUrls.add("C:\\Users\\user\\eclipse-workspace\\SA\\src\\main\\webapp\\static\\" + NoteID+"\\" + part.getSubmittedFileName());
	                    picUrls.add("\\SA\\static\\"+ part.getSubmittedFileName());
	                } catch (IOException e) {
	                    throw new UncheckedIOException(e);
	                }
	            }
	        }
	    }

	    // Print or use the retrieved text values
	    System.out.println("NoteID: " + NoteID);

	    // Continue processing picUrls as needed
	    String[] picURLs = new String[picUrls.size()];
	    for (int i = 0; i < picUrls.size(); i++) {
	        picURLs[i] = picUrls.get(i);
	        System.out.println("Pic URL " + (i + 1) + ": " + picURLs[i]);
	    }
	    PH.CreatePic(picURLs, NoteID);
	    response.setContentType("application/json;charset=UTF-8");
	    response.getWriter().write("{\"status\":\"200\", \"message\": \"圖片上傳成功\", \"response\": \"\"}");
	}


}
