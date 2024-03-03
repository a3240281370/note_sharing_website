package util;
import java.sql.*;
import java.util.Properties;

public class DBConnector
{
	static final String driver = "com.mysql.cj.jdbc.Driver";
	static final String url = "jdbc:mysql://localhost:3306/sa";
	static final String user = "root";
	static final String password = "";
	
	static
	{
        try 
        {
            Class.forName(DBConnector.driver);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
	
	public DBConnector() 
	{
		
	}
	
	public static Connection GetConnection() 
	{
		Properties props = new Properties();
		props.setProperty("useUnicode", "true");
		props.setProperty("characterEncoding", "utf8");
		props.setProperty("user",DBConnector.user);
		props.setProperty("password",DBConnector.password);
		
		Connection conn=null;
		
		try 
		{
            conn = DriverManager.getConnection(DBConnector.url, props);
        } 
		catch (Exception e) 
		{
            e.printStackTrace();
        }
        return conn;
	}
	
	public static void close(Statement stm, Connection conn) 
	{
        try 
        {
            if (stm != null) stm.close();
            if (conn != null) conn.close();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
	
	public static void close(ResultSet rs, Statement stm, Connection conn) 
	{
        try 
        {
            if (rs != null) rs.close();
            if (stm != null) stm.close();
            if (conn != null) conn.close();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
	
	public static String[] stringToArray(String data, String delimiter) 
	{
	      String[] result;
	      result = data.split(delimiter);
	      return result;
    }
}

