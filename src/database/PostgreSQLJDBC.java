package database;

import java.sql.*;
import java.util.*;

public class PostgreSQLJDBC
{
  class Blog
  {
    public String name;
  }

  public static void main(String[] args)
  {
    new PostgreSQLJDBC();
  }

  public PostgreSQLJDBC() 
  {
    Connection conn = null;
    LinkedList listOfBlogs = new LinkedList();

    // connect to the database
    conn = connectToDatabaseOrDie();

    // get the data
    populateListOfTopics(conn, listOfBlogs);
    
    // print the results
    printTopics(listOfBlogs);
  }
  
  private void printTopics(LinkedList listOfBlogs)
  {
    Iterator it = listOfBlogs.iterator();
    while (it.hasNext())
    {
      Blog blog = (Blog)it.next();
      System.out.println("name: " + blog.name );
    }
  }

  private void populateListOfTopics(Connection conn, LinkedList listOfBlogs)
  {
    try 
    {
      Statement st = conn.createStatement();
      ResultSet rs = st.executeQuery("SELECT * FROM messages");
      while ( rs.next() )
      {
        Blog blog = new Blog();
        blog.name= rs.getString("sender");
        listOfBlogs.add(blog);
      }
      rs.close();
      st.close();
    }
    catch (SQLException se) {
      System.err.println("Threw a SQLException creating the list of blogs.");
      System.err.println(se.getMessage());
    }
  }

  private Connection connectToDatabaseOrDie()
  {
    Connection conn = null;
    try
    {
      Class.forName("org.postgresql.Driver");
      String url = "jdbc:postgresql://localhost/messaging";
      conn = DriverManager.getConnection(url,"message", "message");
      System.out.println("se conecto");
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      System.exit(2);
    }
    return conn;
  }

}