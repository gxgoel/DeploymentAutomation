package com.sap.ich;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DBUpdate {
	

           public static List executeQuery(String query, String userName, String Password) throws Exception{
               ResultSet res = DBUtil.executeSqlQuery(query,userName,Password);
               List<List> retValue = new ArrayList<>();
               while(res.next()){
                   List<String> rowData = new ArrayList<>();
                   int i = 1;
                   while(true) {
                       try {
                           rowData.add(res.getObject(i).toString());
                           //System.out.println(res.getObject(i));
                           i = i + 1;
                       }catch (Exception e){
                           break;
                       }
                   }
                   //retValue += "|";
                   retValue.add(rowData);
               }
               /*while( res.next() ) {
                   int i = 1;
                   while(true) {
                       try {
                           retValue += "|" + res.getObject(i).toString();
                           //System.out.println(res.getObject(i));
                           i = i + 1;
                       }catch (Exception e){
                           break;
                       }
                   }
                   retValue += "|";
               }*/
               return retValue;
           }

    public static int deleteQuery(String query, String userName, String Password, String limit) throws Exception{
        String tableName = query.split(" ")[2];
        String where_clause = query.toLowerCase().split(" where ")[1];
        String select_query = "select count(*) from "+ tableName + " where "+where_clause;
        ResultSet res = DBUtil.executeSqlQuery(select_query,userName,Password);
        res.next();
        int retValue = Integer.valueOf(res.getObject(1).toString());
        if(!limit.toUpperCase().equals("NONE") && retValue == Integer.parseInt(limit)){
            DBUtil.executeDeleteSqlQuery(query, userName, Password);
            return 1;
        }else{
            return 0;
        }

    }
           public static String executeProceedure(String query, String userName, String Password) throws Exception {
               ResultSet res = DBUtil.executeSqlProceedure(query, userName, Password);
               String retValue = null;
               if (res != null) {
                   while (res.next()) {
                       int i = 1;
                       while (true) {
                           try {
                               retValue += res.getObject(i).toString();
                               //System.out.println(res.getObject(i));
                               i = i + 1;
                           } catch (Exception e) {
                               break;
                           }
                       }
                   }
               }
               return retValue;
           }

    public static int executeUpdateQuery(String query, String userName, String Password, String limit) throws Exception{
               String tableName = query.split(" ")[1];
               String where_clause = query.toLowerCase().split(" where ")[1];
               String select_query = "select count(*) from "+ tableName + " where "+where_clause;
        ResultSet res = DBUtil.executeSqlQuery(select_query,userName,Password);
        res.next();
        int retValue = Integer.valueOf(res.getObject(1).toString());
        if(!limit.toUpperCase().equals("NONE") && retValue == Integer.parseInt(limit)){
            int res1 = DBUtil.executeUpdateQuery(query, userName, Password);
            return res1;
            //log saying that 1 row is impacted
        }
        else{
            return 0;
            //log saying that no update happened as the query will impact multiple rows
        }
    }

}
