package LocalDemo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.github.pemistahl.lingua.api.Language;
import static com.github.pemistahl.lingua.api.Language.FRENCH;
import static com.github.pemistahl.lingua.api.Language.SPANISH;
import static com.github.pemistahl.lingua.api.Language.GERMAN;
import static com.github.pemistahl.lingua.api.Language.PORTUGUESE;
import static com.github.pemistahl.lingua.api.Language.ENGLISH;
import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import com.jayway.jsonpath.JsonPath;

public class L10nSteps {
	final static LanguageDetector detector = LanguageDetectorBuilder
			.fromLanguages(FRENCH, SPANISH, GERMAN, PORTUGUESE, ENGLISH).build();

	@SuppressWarnings({ "unchecked" })
	public void webPageCheck(WebDriver driver, String url, String lang) throws IOException, InterruptedException {
		driver.get(url);
		driver.manage().window().maximize();
		Thread.sleep(5000);
		scrollToPageEnd(driver);
		List<WebElement> allTextElement = driver
				.findElements(By.xpath("//*[string-length(normalize-space(text())) > 0]"));
		// System.out.println("No of Words: " + allTextElement.size());
		// System.out.println("Page Title: " + driver.getTitle());
		int i = 1;
		String fileName = driver.getTitle().replaceAll("[^a-zA-Z]+", " ") + ".json";
		String inputJson = getReadJson(fileName, lang);
		List<String> lt = new ArrayList<String>();
		if (!inputJson.equals("no file")) {
			lt = readJsonFileForNonLocalText(getReadJson(fileName, lang));
		}
		JSONArray localeJsonArray = new JSONArray();
		for (WebElement singleTextElement : allTextElement) {
			// System.out.println("No: " + i);
			// System.out.println(singleTextElement.getText());
			if (!singleTextElement.getText().equals("")) {
				JSONObject temp = new JSONObject();
				if (localCheck(singleTextElement.getText())) {
					if (lt.size() > 0) {
						if (getLocalText(singleTextElement.getText(), lt, getReadJson(fileName, lang)).equals("Y")) {
							localeJsonArray.add(setTemp(temp, singleTextElement.getText(), i));
						}
					} else {
						localeJsonArray.add(setTemp(temp, singleTextElement.getText(), i));
					}
				}
			}
			i = i + 1;
		}

		if (localeJsonArray.size() > 0) {
			createJSONFile(driver, localeJsonArray, lang);
		}
	}

	@SuppressWarnings("unchecked")
	JSONObject setTemp(JSONObject temp, String text, int i) {
		temp.put("value", text);
		temp.put("index", i);
		temp.put("valid", "Y");
		temp.put("xpath", "(//*[string-length(normalize-space(text())) > 0])" + "[" + i + "]");
		return temp;
	}

	@SuppressWarnings({ "unchecked", "resource" })
	void createJSONFile(WebDriver driver, JSONArray localListJson, String lang) {
		JSONObject arrayJsonToPublish = new JSONObject();
		arrayJsonToPublish.put(driver.getCurrentUrl(), localListJson);
		String directoryName = null;
		directoryName = System.getProperty("user.dir") + "\\" + lang + "\\";
		File directory = new File(directoryName);
		if (!directory.exists()) {
			directory.mkdir();
		}
		try {
			FileWriter file;
			file = new FileWriter(directory + "\\" + driver.getTitle().replaceAll("[^a-zA-Z]+", " ") + ".json");
			file.write(arrayJsonToPublish.toJSONString());
			file.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean localCheck(String tempStr) {
		Language detectedLanguage = detector.detectLanguageOf(tempStr);
		if (detectedLanguage.toString().equals("ENGLISH")) {
			return true;
		} else
			return false;
	}

	public List<String> readJsonFileForNonLocalText(String json) throws IOException {
		try {
			List<String> localText = JsonPath.read(json, "$..value");
			return localText;
		} catch (Exception e) {
			List<String> localText = Arrays.asList("N");
			return localText;
		}

	}

	public String getLocalText(String localText, List<String> listOfLocalText, String json) {
		try {
			List<String> authors = JsonPath.read(json, "$..valid");
			return authors.get(listOfLocalText.indexOf(localText));
		} catch (Exception e) {
			return "Y";
		}
	}

	public String getReadJson(String fileName, String lang) throws IOException {
		try {
			File file;
			file = new File(System.getProperty("user.dir") + "\\input_" + lang + "\\" + fileName);
			String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
			return json;
		} catch (Exception e) {
			return "no file";
		}
	}

	void scrollToPageEnd(WebDriver driver) throws InterruptedException {
		int default_term = 5;
		for (int i = 0; i <= default_term; i++) {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollBy(0,1500)");
			Thread.sleep(5000);
		}
	}

}
