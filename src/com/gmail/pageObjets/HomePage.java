package com.gmail.pageObjets;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gmail.utility.ParentPage;

public class HomePage extends ParentPage {
	
	WebDriver driver;
	@FindBy(xpath="//div[text()='COMPOSE']") WebElement composeButton;
	
	public HomePage(WebDriver driver) {
		super(driver);
		this.driver = driver;
		PageFactory.initElements(this.driver, this);
	}
	
	public boolean isUserLoggedIn(String emailId) {
		try {
			// Check for title 
			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(ExpectedConditions.titleContains(emailId));
			
			// Verify Compose Button
			if (composeButton.isDisplayed())
				return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return false;
	}
}