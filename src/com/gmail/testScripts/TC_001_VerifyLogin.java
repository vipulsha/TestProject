package com.gmail.testScripts;

import org.testng.Assert;
import org.testng.annotations.Test;

import atu.testng.reports.ATUReports;
import atu.testng.reports.logging.LogAs;
import atu.testng.reports.utils.Utils;
import atu.testng.selenium.reports.CaptureScreen;
import atu.testng.selenium.reports.CaptureScreen.ScreenshotOf;

import com.gmail.pageObjets.HomePage;
import com.gmail.pageObjets.LoginPage;
import com.gmail.utility.ParentTest;

public class TC_001_VerifyLogin extends ParentTest {

	{
		System.setProperty("atu.reporter.config", "atu.properties");
	}

	@Test
	public void verifyLogin() {
		try {
			ATUReports.setAuthorInfo("Batch 3", Utils.getCurrentTime(),"1.0");
			LoginPage loginPage = new LoginPage(driver);

			loginPage.setUserName(getData("emailId"));
//			ATUReports.add("Entered User Name", LogAs.PASSED, null);
			ATUReports.add("Entered User Name", getData("emailId"), LogAs.PASSED, null);
			
			loginPage.clickNextButton();
			ATUReports.add("Clicked on Next Button", LogAs.PASSED, null);

			loginPage.setPassword(getData("password"));
//			ATUReports.add("Entered Password", LogAs.PASSED, null);
			ATUReports.add("Entered Password", getData("password"), LogAs.PASSED, null);
			
			HomePage homePage = loginPage.clickSignInButton();
			ATUReports.add("Clicked on SignIn Button", LogAs.PASSED, null);

			Assert.assertTrue(homePage.isUserLoggedIn(getData("emailId")), "Error: User is not logged in");
			ATUReports.add("Verified User Is Logged In", LogAs.PASSED, null);
		} catch(AssertionError error) {
			ATUReports.add("Failed", LogAs.FAILED, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
			Assert.fail(error.getMessage());
			System.out.println("Exception: "+error.getMessage());
		} catch(Exception e) {
			Assert.fail(e.getMessage());
			System.out.println("Exception: "+e.getMessage());
		}
	}
}