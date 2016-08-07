package com.gmail.utility;

import java.util.ArrayList;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ParentPage {

	WebDriver driver;
	WebDriverWait wait;

	public enum ExpectedCondtion {VISIBLE,CLICKABLE,INVISIBLE,SELECTED,NOT_SELECTED};

	public ParentPage(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(this.driver, 30);
	}

	public void waitForElement(WebElement element, ExpectedCondtion condtion) {
		switch (condtion) {
		case CLICKABLE:
			wait.until(ExpectedConditions.elementToBeClickable(element));
			break;
		case VISIBLE:
			wait.until(ExpectedConditions.visibilityOf(element));
			break;
		case INVISIBLE:
			//wait.until(ExpectedConditions.not(ExpectedConditions.visibilityOf(element)));
			ArrayList<WebElement> list = new ArrayList<>();
			list.add(element);
			wait.until(ExpectedConditions.invisibilityOfAllElements(list));
			break;
		case SELECTED:
			wait.until(ExpectedConditions.elementSelectionStateToBe(element, true));
			break;
		case NOT_SELECTED:
			wait.until(ExpectedConditions.elementSelectionStateToBe(element, false));
			break;
		}
	}
	
	public void enterText(WebElement element, String text) {
		waitForElement(element, ExpectedCondtion.VISIBLE);
		element.sendKeys(text);
	}
	
	public void clickOnElement(WebElement element) {
		waitForElement(element, ExpectedCondtion.CLICKABLE);
		element.click();
	}
}