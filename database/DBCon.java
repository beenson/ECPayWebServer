package database;

import Config.DBConfig;
import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class DBCon {

	@Getter
	private static DBCon instance = new DBCon();

	private Connection con = null;
	private String jdbcName = "com.mysql.cj.jdbc.Driver";
	private ResultSet rs = null;
	private PreparedStatement pstmt = null;
	public DBCon() {
		this.connect();
	}

	public static Connection getConnection() {
		DBCon dbc = new DBCon();
		return dbc.con;
	}
	
	/**
	 * 
	 */
	public void connect() {
		try {
			Class.forName(jdbcName); //載入jdbc驅動程式
			con = DriverManager.getConnection("jdbc:mysql://"+ DBConfig.getHost() +":"+DBConfig.getPort()+"/"+DBConfig.getDbName()+"?characterEncoding=UTF-8&" + "user="+DBConfig.getUsername()+"&password="+DBConfig.getPassword()); //驅動程式管理器，取得mysql連線
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ResultSet exec(String sql) {
		try {
			pstmt= con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			if (sql.toUpperCase().startsWith("SELECT")) {
				rs = pstmt.executeQuery(sql);
			}else {
				pstmt.executeUpdate(sql);
				rs = null;
			}
		}catch (SQLException ex){
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}       
		return rs;
	}
	
	public void close() {       
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
			if (con != null) {
				con.close();
				con = null;
			}
		}catch (SQLException ex){
			ex.printStackTrace();
		}
	}
}
