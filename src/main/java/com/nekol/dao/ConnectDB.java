package com.nekol.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public abstract class ConnectDB {
    
        private String jdbcURL = "jdbc:mysql://localhost:3306/carodb?useSSL=false";
        private String jdbcUsername = "root";
        private String jdbcPassword = "68686868";
    
        protected Connection conn;
    
    public ConnectDB() {
        try {
             Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
            System.out.println("Connection to database success");
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Connection to database failed");
        }
    }
    

}