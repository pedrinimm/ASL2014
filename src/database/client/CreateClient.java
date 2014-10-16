package database.client;

import java.sql.*;

public class CreateClient {

	private static final String myQuery= "INSERT INTO clients (name) VALUES (?) returning id";
	

	private static void getQuery(String name,Connection con){
		PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(myQuery);
            stmt.setString(1, name);
            ResultSet result = stmt.executeQuery();
            if (!result.next()) {
                throw new RuntimeException("Error creating Client SQLException.");
            }
            int ForResult = result.getInt(1);
            //return ForResult;
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }
	}
}
