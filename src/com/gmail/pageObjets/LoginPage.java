package com.gmail.pageObjets;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.gmail.utility.ParentPage;

public class LoginPage extends ParentPage {
	
	WebDriver driver;
	
	@FindBy(id="Email") WebElement userNameTextbox;
	@FindBy(name="signIn") WebElement nextButton;
	@FindBy(id="Passwd") WebElement passwordTextbox;
	@FindBy(id="signIn") WebElement signInButton;
	
	public LoginPage(WebDriver driver) {
		super(driver);
		this.driver = driver;
		PageFactory.initElements(this.driver, this);
	}
	
	public void setUserName(String uname) {
		enterText(userNameTextbox, uname);
	}
	
	public void setPassword(String password) {
		/*waitForElement(passwordTextbox, ExpectedCondtion.VISIBLE);
		passwordTextbox.sendKeys(password);*/
		enterText(passwordTextbox, password);
	}
	
	public void clickNextButton() {
		clickOnElement(nextButton);
	}
	
	public HomePage clickSignInButton() {
		/*waitForElement(signInButton, ExpectedCondtion.CLICKABLE);
		signInButton.click();*/
		clickOnElement(signInButton);
		return new HomePage(this.driver);
	}
}