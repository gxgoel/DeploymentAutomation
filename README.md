# ICHDeployment

Setup:

1. Check-out / download the project
2. Import the project to eclipse / IntelliJ as a maven project
3. Download & extract neo-java-web-sdk (Java Web Tomcat 8: neo-java-web-sdk-3.92.15.zip) from https://tools.hana.ondemand.com/


Execution:
1. Fill the input data in "resources/Automation_InputSheet.xlsx" and close the file
2. Update the properties under "resources/config.properties"
3. On command prompt cd to the directory where neo-java-web-sdk is downloaded.
3. Run the DB Connector by running "neo.bat open-db-tunnel -a <tenant id> -h hana.ondemand.com -u <user id> -i hanaxs"
     ex: neo.bat open-db-tunnel -a a629a2553 -h hana.ondemand.com -u i318199 -i hanaxs"
4. Run the command : mvn exec:java -Dexec.mainClass="com.sap.ich.ICHDeployOps"

Results:
Open file ""resources/Automation_InputSheet.xlsx" the Output_sheet will have the results of execution.
