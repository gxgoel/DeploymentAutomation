package com.sap.ich;

import com.sap.partner.client.ParnerDirectoryAPICaller;
import com.sap.partner.client.PartnerDirectoryProcessor;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ICHDeployOps {
    static String resourcePath = System.getProperty("user.dir") + "\\src\\main\\resources\\";
    static final Logger logger = Logger.getLogger(ICHDeployOps.class);
    static String partnerDirectoryUserName;
    static String partnerDirectoryUserPassword;
    static String partnerDirectoryURL;
    static String dbUser;
    static  String dbPassword;

    public static void main(String[] arg) throws Exception{
            try (InputStream input = new FileInputStream(resourcePath + "config.properties")) {

                Properties prop = new Properties();

                // load a properties file
                prop.load(input);

                // get the property value and print it out
                partnerDirectoryUserName = prop.getProperty("partnerDirectoryUserName");
                partnerDirectoryUserPassword = prop.getProperty("partnerDirectoryUserPassword");
                partnerDirectoryURL = prop.getProperty("partnerDirectoryURL");
                dbUser = prop.getProperty("dbUser");
                dbPassword = prop.getProperty("dbPassword");

                ExcelUtils xls = new ExcelUtils();
                List<List<String>> xlsData = xls.getExcelData(resourcePath, "Automation_InputSheet.xlsx", "Input_Sheet");
                //ExcelUtils opxls = new ExcelUtils();
                boolean flag = false;
                String response[] = new String[7];
                int i = 1;
                xls.resetOutputSheet(resourcePath, "Automation_InputSheet.xlsx", "Output_Sheet");

                for (List<String> row : xlsData) {
                    response[0] = row.get(0);
                    response[1] = row.get(1);
                    response[2] = row.get(2);
                    response[3] = row.get(3);
                    response[4] = row.get(4);
                    response[5] = row.get(5);
                    String limit = row.get(3);
                    if(limit.indexOf(".") > -1)
                        limit = limit.split("\\.")[0];
            if (flag) {
                switch (row.get(4)) {
                    case "HCI":
                        response[6] = processAPICall(row.get(5), row.get(3), row.get(2), limit);
                        xls.setRowExcelData(resourcePath, "Automation_InputSheet.xlsx", "Output_Sheet", i, response);
                        break;
                    case "DB":
                        List<List<String>> content = processDBCall(row.get(5), row.get(2), limit);
                        response[6] = "";
                        System.out.println(content);
                        if(row.get(5).toUpperCase().equals("SELECT")) {
                            response[6] = "Refer the sheet Select_*";
                            xls.addToSheet(resourcePath, "Automation_InputSheet.xlsx", "Select_" + row.get(0), content);
                            xls.setRowExcelData(resourcePath, "Automation_InputSheet.xlsx", "Output_Sheet", i, response);
                        }else if(row.get(5).toUpperCase().equals("UPDATE") ||row.get(5).toUpperCase().equals("DELETE") ) {
                            response[6] = "Number of rows updated :" + content.get(0).get(0);
                            xls.setRowExcelData(resourcePath, "Automation_InputSheet.xlsx", "Output_Sheet", i, response);
                        } else{
                            xls.setRowExcelData(resourcePath, "Automation_InputSheet.xlsx", "Output_Sheet", i, response);
                        }
                        break;
                    case "PDUPDATE":
                        PartnerDirectoryProcessor PDProcessor = new PartnerDirectoryProcessor(partnerDirectoryUserName, partnerDirectoryUserPassword, resourcePath + row.get(3), partnerDirectoryURL);
                        PDProcessor.insertDataPartnerDirectory();
                        break;
                    default:
                        response[6] = "Results";
                        if(i==0)
                            xls.setRowExcelData(resourcePath, "Automation_InputSheet.xlsx", "Output_Sheet", i, response);
                        System.out.println("Incorrect Connection type");

                }

                i = i + 1;

            } else {
                flag = true;
            }
    }
}catch(Exception e){
    e.printStackTrace();
}

    }

    public static String processAPICall(String method, String condition, String data, String limit) throws Exception{
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(data);
        ParnerDirectoryAPICaller apiCaller;
        if(!condition.toUpperCase().equals("NONE")) {
            apiCaller = new ParnerDirectoryAPICaller(partnerDirectoryURL + json.get("endpoint") + "?" + condition, partnerDirectoryUserName, partnerDirectoryUserPassword);
        }else {
            apiCaller = new ParnerDirectoryAPICaller(partnerDirectoryURL + json.get("endpoint"), partnerDirectoryUserName, partnerDirectoryUserPassword);
        }
        String csrfToken = apiCaller.getCSRFToken();
        String response;
        HttpURLConnection conn = apiCaller.openConnectionWithCxrfToken(csrfToken);
            if(!method.toUpperCase().equals("DELETE")) {
                response = getResponse(conn, json.get("body").toString(), method);
                System.out.println(response);
            }
            else{
                response = getResponse(conn, null, method);
                System.out.println(response);
            }
        //}
        return response;
    }

    public static List<List<String>> processDBCall(String queryType, String query, String limit) throws Exception{
        System.out.println(query);
        List<List<String>> obj = new ArrayList<>();
        switch(queryType){
            case "SELECT":
                obj = DBUpdate.executeQuery(query, "CMO_DB","#Password3");
                break;
            case "UPDATE":
                List<String> tmp = new ArrayList<>();
                tmp.add(String.valueOf(DBUpdate.executeUpdateQuery(query,dbUser, dbPassword, limit)));
                obj.add(tmp);
                break;
            case "DELETE":
                List<String> tmp1 = new ArrayList<>();
                tmp1.add(String.valueOf(DBUpdate.deleteQuery(query, dbUser, dbPassword, limit)));
                obj.add(tmp1);
                break;
            case "PROCEEDURE":
                DBUpdate.executeProceedure(query, dbUser, dbPassword);
                break;
            default:
                break;
        }
        return obj;
    }

    public static String getResponse(HttpURLConnection httpURLconnection, String jsonBody, String method) throws Exception {
        int responseCode = 0;
        String responseMsg = "0";
        if (httpURLconnection == null) {
            logger.error("Error Getting Connection details");
            throw new Exception("Error Getting Connection details");
        } else {
            try {
                httpURLconnection.setDoOutput(true);
                httpURLconnection.setDoInput(true);
                httpURLconnection.setRequestMethod(method);
                httpURLconnection.setRequestProperty("Accept", "application/json");
                if(method.equals("POST") || method.equals("PUT")) {
                    httpURLconnection.setRequestProperty("Content-Type", "application/json");
                    httpURLconnection.setRequestProperty("Accept", "application/json");
                    OutputStream os = httpURLconnection.getOutputStream();
                    os.write(jsonBody.getBytes());
                    os.flush();
                    os.close();
                }
                responseCode = httpURLconnection.getResponseCode();
                responseMsg = String.valueOf(responseCode);
                if (responseCode == 201 || responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(httpURLconnection.getInputStream()));
                    StringBuffer response = new StringBuffer();

                    String inputLine;
                    while((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    in.close();
                    System.out.println(response);
                    logger.info("The Response is code is :: " + responseCode + "for this JSON Body is :: " + jsonBody);
                } else {
                    logger.error(method + " request not worked");
                    String errorResponse = readErrorString(httpURLconnection.getErrorStream());
                    logger.error("Error response is :: " + errorResponse);
                    responseMsg = responseMsg + " : "+ errorResponse;
                }
            } catch (ProtocolException var12) {
                logger.error("The Json Body is failing for this request :: " + jsonBody);
                logger.error("ProtocolException details :: " + var12.getMessage());
            } catch (IOException var13) {
                logger.error("The Json Body is failing for this request :: " + jsonBody);
                logger.error("IOException details :: " + var13.getMessage());
            } finally {

            }

            return responseMsg;
        }
    }

    private static String readErrorString(InputStream inputStream) {
        String result = null;
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream)));
            String inputLine = "";

            while((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }

            result = sb.toString();
        } catch (Exception var14) {
            logger.error("Error reading InputStream");
            result = null;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException var13) {
                    logger.error("Error closing InputStream");
                }
            }

        }

        return result;
    }


}

