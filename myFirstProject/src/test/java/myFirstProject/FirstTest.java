package myFirstProject;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FirstTest {
	
	
	@Test
	//public static void main(String[] args) {
	public void firstTest() {
		// TODO Auto-generated method stub
		Properties configFile = new Properties();
		String email, password;
		String baseUrl = "https://go.xero.com/";
		
		try {
			//Load stuffs from config file
			configFile.load(FirstTest.class.getClassLoader().getResourceAsStream("config.properties"));
			email = configFile.getProperty("email");
			password = configFile.getProperty("password");
			
			//Starts firefox web browser
			WebDriver driver = new FirefoxDriver();
			driver.get("https://login.xero.com/");
			
			//Logs into dashboard
			WebElement element = driver.findElement(By.id("email"));
			element.sendKeys(email);

			element = driver.findElement(By.id("password"));
			element.sendKeys(password);
			
			element = driver.findElement(By.id("submitButton"));
			element.click();
			driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
			
			//Navigate to Accounts page
			driver.get(baseUrl + "/Accounts/Receivable/Dashboard/");
			driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
			
			//Wait until dropdown control appears before launching Repeating invoice
			WebDriverWait wait = new WebDriverWait(driver, 10); 
			wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//*[text()[contains(.,'New')]]//span"))));
			driver.findElement(By.xpath("//*[text()[contains(.,'New')]]//span")).click();
			driver.findElement(By.linkText("Repeating invoice")).click();

			
			//Perform creation of DRAFT repeating invoice
			/**I opted using SendKeys on some controls but we could use checking on the dropdown boxes
			 * I assumed in the following steps that the input are correct and this test just checks
			 * the ability of the page to create a draft invoice
			 */
			
			driver.findElement(By.id("TimeUnit_toggle")).sendKeys("Month(s)");;
			driver.findElement(By.id("StartDate")).sendKeys("30 Sep 2014");;			
			driver.findElement(By.id("DueDateDay")).sendKeys("20");
			driver.findElement(By.id("saveAsDraft")).click();
			
			driver.findElement(By.xpath("//*[contains(@name,'PaidToName')]")).clear();
			driver.findElement(By.xpath("//*[contains(@name,'PaidToName')]")).sendKeys(configFile.getProperty("repeatinvoicename"));
			
			driver.findElement(By.xpath(".//*[@id='lineItems']//*[@class='x-grid3-body']/div[1]//*[contains(@class,'colPriceList')]")).click();
			driver.findElement(By.xpath(".//*[@id='lineItems']//*[contains(@class,'x-form-text x-form-field')]")).sendKeys("BOOK");
			driver.findElement(By.xpath(".//*[@id='lineItems']//*[contains(@class,'x-form-text x-form-field')]")).sendKeys(Keys.RETURN);
			driver.findElement(By.id("DueDateType_value")).clear();
			driver.findElement(By.id("DueDateType_value")).sendKeys("of the following month");
			
			//Hit Save
			driver.findElement(By.xpath("(//button[@type='button'])[3]")).click();

			//Here we check if the saved draft appears on the list
			List<WebElement> list = driver.findElements(By.xpath("//*[contains(text(),'" + configFile.getProperty("repeatinvoicename") + "')]"));
			assertTrue("Draft invoice not saved",list.size()==0);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
