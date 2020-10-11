package LocalDemo;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class L10nTest {
	private WebDriver driver;

	@BeforeClass
	public void setupClass() {
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\driver\\chromedriver.exe");
		driver = new ChromeDriver();
	}

	@AfterTest
	public void teardown() {
		if (driver != null) {
			driver.quit();
		}
	}

	@Test(dataProvider = "LocaleLanguages")
	public void test(String locale) throws IOException, InterruptedException {
		getLocalTest(locale);

	}

	@DataProvider(name = "LocaleLanguages")
	public String[] LocaleLanguages() {
		return new String[] { "es-us" };
	}

	@SuppressWarnings("rawtypes")
	void getLocalTest(String lang) throws IOException, InterruptedException {
		{
			FileReader reader;
			reader = new FileReader(System.getProperty("user.dir") + "\\" + "links.properties");
			Properties p = new Properties();
			p.load(reader);
			Set set = p.entrySet();
			Iterator itr = set.iterator();
			int page = 1;
			while (itr.hasNext()) {
				Map.Entry entry = (Map.Entry) itr.next();
				System.out.println("Page No: " + page);
				L10nSteps LocalizationSteps = new L10nSteps();
				LocalizationSteps.webPageCheck(driver, "https://www.att.com/" + lang + entry.getKey(), lang);
				page++;
			}
		}
	}

}
