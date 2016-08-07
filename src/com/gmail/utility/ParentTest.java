package com.gmail.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;


import atu.testng.reports.ATUReports;

public class ParentTest {

	protected WebDriver driver;
	static Properties configProp = null;
	protected Properties testDataProp = null;
	
	String PORT = "587";
	String STARTTLS = "true";
	String HOST = "smtp.gmail.com";
	String PASSWORD = "samaritan123";
	String FROM = "smtautomation1@gmail.com";
	String USER = "smtautomation1@gmail.com";

//	String PASSWORD = "afour@123";
//	String FROM = "computenexttestuser@gmail.com";
//	String USER = "computenexttestuser@gmail.com";
	
	String OUTPUT_ZIP_FILE = "";
	String SOURCE_FOLDER = "";
	List<String> fileList = new ArrayList<String>();
	private String toList[] = null;

	
	// Gets executed automatically when class is loaded
	static {
		if (configProp == null) {
			try {
				configProp = new Properties();
				configProp.load(new FileInputStream(new File("Config//Config.properties")));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	@BeforeClass
	public void setUp() {
		// Initialize driver
		String browserType = configProp.getProperty("browser").trim().toUpperCase();
		switch (browserType) {
		case "CHROME":
			System.setProperty("webdriver.chrome.driver", "Drivers\\chromedriver.exe");
			driver = new ChromeDriver();
			break;
		case "FF":
			driver = new FirefoxDriver();
			break;
		case "IE":
			System.setProperty("webdriver.ie.driver", "Drivers\\IEDriverServer.exe");
			driver = new InternetExplorerDriver();
			break;
		default :
			System.out.println("Error: Broser is incorrect or not specified in config file");
		}
		
		driver.manage().window().maximize();
		driver.get("http://gmail.com");
		
		ATUReports.setWebDriver(driver);
		
		// Read Test Data
		String fileName = this.getClass().getSimpleName();	// Will give current class name
		readTestData(fileName);
	}

	@AfterClass
	public void tearDown() {
		driver.quit();
	}

	public void readTestData(String fileName) {
		try {
			File dataFile = new File("DataRepository//"+fileName+".properties");
			if (dataFile.exists()) {
				testDataProp = new Properties();
				testDataProp.load(new FileInputStream(dataFile));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getData(String key) throws Exception {
		if (testDataProp == null) {
			throw new Exception("Error: Test data file doesn't exist");
		}
		
		String val = (String) testDataProp.get(key);
		if (val == null) {
			return "";
		}
		return testDataProp.get(key).toString();
	}
	
	public static void main(String[] args) throws Exception {
		ParentTest parentTest = new ParentTest();
		parentTest.afterSuite();
	}
	
	
	public void afterSuite() throws Exception {
		// Send report in email
		try {
			System.out.println("Sending mail...");
			Properties props = new Properties();
			props.put("mail.smtp.port", PORT);
			props.setProperty("mail.user", USER);
			props.setProperty("mail.host", HOST);
			props.setProperty("mail.password", PASSWORD);
			props.put("mail.smtp.starttls.enable", STARTTLS);
			props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
			props.setProperty("mail.transport.protocol", "smtp");
			
			Session mailSession = Session.getDefaultInstance(props, null);
			MimeMessage message = new MimeMessage(mailSession);

			// Get Recepients List
			toList = configProp.get("emailIds").toString().trim().split(",");
			if (toList.length != 0) {
				// Add recepients
				for(int i=0;i<toList.length;i++) {
					message.addRecipient(RecipientType.TO, new InternetAddress(toList[i]));
				}

				message.addFrom(new InternetAddress[] { new InternetAddress(FROM) });
				MimeMultipart mp = new MimeMultipart();

/*				MimeBodyPart mbp = new MimeBodyPart();
				FileDataSource fds = new FileDataSource(System.getProperty("user.dir")+"\\Reports\\index.html");
				mbp.setDataHandler(new DataHandler(fds));
				mp.addBodyPart(mbp);
*/
/*				// Attach ComputeNext Logo
				if(new File(DataController.getBaseDirPath()+"\\TestReport\\logo.png").exists()){
					MimeBodyPart imgPart = new MimeBodyPart();
					DataSource source = new FileDataSource(new File(DataController.getBaseDirPath()+"\\TestReport\\logo.png"));
					imgPart.setDataHandler(new DataHandler(source));
					imgPart.setHeader("Content-ID", "<logoImage>");
					mp.addBodyPart(imgPart);
				}*/

				/*// Attach Pie chart
				if(new File(DataController.getBaseDirPath()+"\\TestReport\\chart.png").exists()){
					MimeBodyPart imgPart = new MimeBodyPart();
					DataSource source = new FileDataSource(new File(DataController.getBaseDirPath()+"\\TestReport\\chart.png"));
					imgPart.setDataHandler(new DataHandler(source));
					imgPart.setHeader("Content-ID", "<pieChart>");
					mp.addBodyPart(imgPart);
				}*/

				// Create zip file of Test results
				zipIt(System.getProperty("user.dir")+"//Reports", System.getProperty("user.dir")+"//Reports//Results.zip");

				// Attach zip file
				MimeBodyPart mbp1 = new MimeBodyPart();
				FileDataSource fds1 = new FileDataSource(System.getProperty("user.dir")+"\\Reports\\Results.zip");
				mbp1.setDataHandler(new DataHandler(fds1));
				mbp1.setFileName("Results.zip");
				mp.addBodyPart(mbp1);

				message.setContent(mp);
				message.setSentDate(new Date());
				Date sentDate = message.getSentDate();
				SimpleDateFormat format = new SimpleDateFormat("dd-MMM-YYYY");
				String date = format.format(sentDate);
				//message.setSubject("["+hostName+"]"+" Automation Test Pass Report ["+table.get("TestSuite")+"] - " + sentDate.getDate()+" "+sentDate.getgetMonth()+" "+sentDate.getYear());
				message.setSubject("Gmail Test Automation Report ["+date+"]"); // Set Mail Subject

				Transport transport1 = mailSession.getTransport("smtp");
				transport1.connect(HOST, USER, PASSWORD);  // Check authenticate
				transport1.sendMessage(message,message.getRecipients(Message.RecipientType.TO));			
				for(int i = 0; i<toList.length; i++) {
					System.out.println("Mail Send to "+toList[i]);
				}
				transport1.close();
			}
		} catch (Exception e) {
			throw new Exception("Exception while sending report");
		}
	}

    /**
     * Zip it
     * @param zipFile output ZIP file location
     */
    public void zipIt(String sourceFolder,String zipFile){

    	OUTPUT_ZIP_FILE=zipFile;
    	SOURCE_FOLDER=sourceFolder;

    	// For deleting old zip files
    	File oldFile = new File(zipFile);
    	oldFile.delete();
    	
    	byte[] buffer = new byte[1024];

    	generateFileList(new File(sourceFolder));

    	try{

    		FileOutputStream fos = new FileOutputStream(zipFile);
    		ZipOutputStream zos = new ZipOutputStream(fos);

    		System.out.println("Output to Zip : " + zipFile);

    		for(String file : this.fileList){

//    			if(file.contains("DataTables")||file.contains("testreports") || file.contains("Screenshots"))
//    			if(! file.contains("Reports.zip"))
    			{ 
    				System.out.println("File Added : " + file);
    				ZipEntry ze= new ZipEntry(file);
    				zos.putNextEntry(ze);

    				FileInputStream in =  new FileInputStream(SOURCE_FOLDER + File.separator + file);

    				int len;
    				while ((len = in.read(buffer)) > 0) {
    					zos.write(buffer, 0, len);
    				}

    				in.close();
    			}
    		}

    		zos.closeEntry();
    		//remember close it
    		zos.close();

    		System.out.println("Done");
    	}catch(IOException ex){
    		ex.printStackTrace();   
    	}
   }

    /**
     * Traverse a directory and get all files,
     * and add the file into fileList  
     * @param node file or directory
     */
    public void generateFileList(File node){
    	//add file only
	if(node.isFile()){
		fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
	}
		
	if(node.isDirectory()){
		String[] subNote = node.list();
		for(String filename : subNote){
			generateFileList(new File(node, filename));
		}
	}
 
    }

    /**
     * Format the file path for zip
     * @param file file path
     * @return Formatted file path
     */
    private String generateZipEntry(String file){
    	String s = file.substring(SOURCE_FOLDER.length(), file.length());
    	return file.substring(SOURCE_FOLDER.length(), file.length());
    }
}