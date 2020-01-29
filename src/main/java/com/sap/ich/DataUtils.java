package com.sap.ich;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.*;
import java.util.Properties;

public class DataUtils {

	public String convertResultSetToDelimitedString(ResultSet rs) throws SQLException, IOException {
		String result = "";

		int ncols = rs.getMetaData().getColumnCount();  

		System.out.println("ColumnCount: "+ncols);
		StringBuilder out = new StringBuilder(); 

		//Writer out = new OutputStreamWriter(new BufferedOutputStream(sb),"UTF_8");      

		for (int j=1; j<(ncols+1); j++) {     
			out.append(rs.getMetaData().getColumnName (j));       
			if (j<ncols) out.append("|"); else out.append("\r\n");      
		}
		int m =1;

		while (rs.next()) {   

			for (int k=1; k<(ncols+1); k++) {   

				out.append(rs.getString(k));    

				if (k<ncols) out.append("|"); else out.append("\r\n");    
			}   
			//System.out.println("No of rows"+m);   
			m++;   
		}  

		result = out.toString();
		return result;
	}

}
