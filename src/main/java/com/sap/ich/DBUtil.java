package com.sap.ich;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.Properties;

/**
 * Created by C5251560 on 02/06/2017.
 */
public class DBUtil {
   private static final String USER_PNID = "PNCONTRACTMAN20160811";
   private static final Logger LOGGER = LogManager.getLogger(new Object(){}.getClass().getEnclosingClass().getName());

   public static void executeSqlStatement(String sqlStatement, String username, String password) {
      Connection connection = getConnection(username, password);
      if (connection != null) {
         try {
            LOGGER.info("Connection to HANA successful!");
            LOGGER.info("Preparing callable statement...");

            CallableStatement callableStatement = connection.prepareCall(sqlStatement);
            Boolean failedToExecute = callableStatement.execute();
            if (failedToExecute) {
               LOGGER.error("Call to stored procedure failed to execute.");
            } else {
               LOGGER.info("Successfully executed callable statement -> " + sqlStatement);
            }
         } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new IllegalStateException("SQL script failed!");
         }
      }
   }

   public static void executeSqlBatchStatement(String[] batchRequest, String username, String password) {
      Connection connection = getConnection(username, password);
      LOGGER.info("Preparing batch request");
      if (connection != null) {
         try {
            LOGGER.info("Connection to HANA successful!");
            LOGGER.info("Preparing callable statement...");
            Statement statement = connection.createStatement();
            for (String request : batchRequest) {
               statement.addBatch(request);
            }
            Boolean failedToExecute = false;
            int[] batchResponses = statement.executeBatch();
            for (int response : batchResponses) {
               // a successful batch request returns 0 or more for the num of affected rows
               // -2 is returned for a successful batch request with an unknown num of affected rows
               if (response < 0 && response != -2) {
                  failedToExecute = true;
               }
            }
            if (failedToExecute) {
               LOGGER.error("Call to stored procedure failed to execute.");
            } else {
               LOGGER.info("Successfully executed callable batch requests -> " + batchRequest);
            }
         } catch (SQLException e) {
            LOGGER.info("Batch request failed");
            LOGGER.error(e.getMessage());
            throw new IllegalStateException("SQL script failed!");
         }
      }
   }

   public static ResultSet executeSqlQuery(String sqlQuery, String username, String password) {
      Connection connection = getConnection(username, password);
      if (connection != null) {
         try {
            LOGGER.info("Connection to HANA successful!");
            LOGGER.info("Preparing callable query...");
            CallableStatement callableStatement = connection.prepareCall(sqlQuery);
            ResultSet resultSet = callableStatement.executeQuery();
            return resultSet;
         } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new IllegalStateException("SQL script failed!");
         }
      }
      return null;
   }

   public static void executeDeleteSqlQuery(String sqlQuery, String username, String password) {
      Connection connection = getConnection(username, password);
      if (connection != null) {
         try {
            LOGGER.info("Connection to HANA successful!");
            LOGGER.info("Preparing callable query...");
            CallableStatement callableStatement = connection.prepareCall(sqlQuery);
            callableStatement.executeQuery();
         } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new IllegalStateException("SQL script failed!");
         }
      }
   }

   public static int executeUpdateQuery(String sqlQuery, String username, String password) {
      Connection connection = getConnection(username, password);
      if (connection != null) {
         try {
            LOGGER.info("Connection to HANA successful!");
            LOGGER.info("Preparing callable query...");
            CallableStatement callableStatement = connection.prepareCall(sqlQuery);
            callableStatement.executeUpdate();
            int count = callableStatement.getUpdateCount();
            return count;
         } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new IllegalStateException("SQL script failed!");
         }
      }
      return 0;
   }

   protected static Connection getConnection(String username, String password) {
      Connection connection = null;
      try {
         Class.forName("com.sap.db.jdbc.Driver");
         Properties info = new Properties();
         info.put("user", username);
         info.put("password", password);
         //String jdbcUrl = System.getProperty("jdbc.url") + "?allowMultiQueries=true";
         String jdbcUrl = "jdbc:sap://localhost:30015/?databaseName=&allowMultiQueries=true";
         connection = DriverManager.getConnection(jdbcUrl, info);
      } catch (SQLException e) {
         LOGGER.info("Connection Failed.");
         LOGGER.info(e.getMessage());
      } catch (ClassNotFoundException e) {
         LOGGER.info("Class not found: " + e.getMessage());
      }
      return connection;
   }

   public static String[] prepareBatchFromFile(String filePath) {
      LOGGER.info("Preparing to read file at: " + filePath);
      String lineFromFile;
      StringBuffer batchStatement = new StringBuffer();
      try {
         FileReader fileReader = new FileReader(new File(filePath));
         BufferedReader bufferedReader = new BufferedReader(fileReader);
         while ((lineFromFile = bufferedReader.readLine()) != null) {
            batchStatement.append(lineFromFile);
         }
         bufferedReader.close();
      } catch (Exception e) {
         LOGGER.info("Could not find file.\n" + e.toString());
      }
      String[] batchRequest = batchStatement.toString().split(";");
      return batchRequest;
   }

   public static ResultSet executeSqlProceedure(String sqlQuery, String username, String password) {
      Connection connection = getConnection(username, password);
      if (connection != null) {
         try {
            LOGGER.info("Connection to HANA successful!");
            LOGGER.info("Preparing callable query...");
            CallableStatement callableStatement = connection.prepareCall(sqlQuery);

            boolean hadResults = callableStatement.execute();
            if(!hadResults){
               return null;
            }
            while (hadResults) {
               ResultSet resultSet = callableStatement.getResultSet();
               hadResults = callableStatement.getMoreResults();
               return resultSet;
            }

         } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new IllegalStateException("SQL script failed!");
         }
      }
      return null;
   }
}
