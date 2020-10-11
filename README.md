# Localization Testing Selenium

For Automated Localization Testing, generally, we get the strings from the resource file then compare that with the extracted texts from the website using selenium or other tools.

## The problem with this approach is 
- One needs to write XPath or any other locator strategies to read individual strings from the web page is a bit cumbersome.
- Every time the content of the page changes, we need to update the locators.
- One needs to load the resource files for each language is again time taking.

As a part of this solution, we can use machine learning and natural language processing. 
We can harness the power of very powerful tool [Lingua](https://github.com/pemistahl/lingua).

# Quick Info on Lingua
- this library tries to solve language detection of very short words and phrases
- makes use of both statistical and rule-based approaches
- Language Detector for more than 70 languages
- works within every Java 6+ application and on Android
- no additional training of language models necessary
- offline usage without having to connect to an external service or API

## So my solution is:

- Keep all the target url in properties file and read one by one.

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

- Extract all the text from the page using generic XPath

        List<WebElement> allTextElement = driver
              .findElements(By.xpath("//*[string-length(normalize-space(text())) > 0]"));
	
- Use Lingua, to check the translated text

    	final static LanguageDetector detector = LanguageDetectorBuilder
			.fromLanguages(FRENCH, SPANISH, GERMAN, PORTUGUESE, ENGLISH).build();
      	Language detectedLanguage = detector.detectLanguageOf(tempStr);
        
        public boolean localCheck(String tempStr) {
          Language detectedLanguage = detector.detectLanguageOf(tempStr);
          if (detectedLanguage.toString().equals("ENGLISH")) {
            return true;
          } else
            return false;
        }

- Upon run, it will create a json file for the untranslated text for the particular page. Using the XPath, one can quickly naviagte to the element and analsysis if the untranslated text is expected. If it is expected, then marked flag valid as false and create a folder input_pt-br(input_lang in root) and keep it. From the second run itself it will ignore those expected texts.
	```sh
		{
		  "https://www.lumen.com/pt-br/about/4th-industrial-revolution.html": [
		    {
		      "valid": "Y",
		      "xpath": "(//*[string-length(normalize-space(text())) > 0])[437]",
		      "index": 437,
		      "value": "Video Player is loading."
		    }
		  ]
		}
	```		
### <a name="library-dependency-maven"></a> Using Maven to use Lingua

```
<dependency>
    <groupId>com.github.pemistahl</groupId>
    <artifactId>lingua</artifactId>
    <version>1.0.2</version>
</dependency>
```

### Which languages are supported? 

Currently, the following 74 languages are supported by Lingua:

- A
  - Afrikaans
  - Albanian
  - Arabic
  - Armenian
  - Azerbaijani
- B
  - Basque
  - Belarusian
  - Bengali
  - Norwegian Bokmal
  - Bosnian
  - Bulgarian
- C
  - Catalan
  - Chinese
  - Croatian
  - Czech
- D
  - Danish
  - Dutch
- E
  - English
  - Esperanto
  - Estonian
- F
  - Finnish
  - French
- G
  - Ganda
  - Georgian
  - German
  - Greek
  - Gujarati
- H
  - Hebrew
  - Hindi
  - Hungarian
- I
  - Icelandic
  - Indonesian
  - Irish
  - Italian
- J
  - Japanese
- K
  - Kazakh
  - Korean
- L
  - Latin
  - Latvian
  - Lithuanian
- M
  - Macedonian
  - Malay
  - Marathi
  - Mongolian
- N
  - Norwegian Nynorsk
- P
  - Persian
  - Polish
  - Portuguese
  - Punjabi
- R
  - Romanian
  - Russian
- S
  - Serbian
  - Shona
  - Slovak
  - Slovene
  - Somali
  - Sotho
  - Spanish
  - Swahili
  - Swedish
- T
  - Tagalog
  - Tamil
  - Telugu
  - Thai
  - Tsonga
  - Tswana
  - Turkish
- U
  - Ukrainian
  - Urdu
- V
  - Vietnamese
- W
  - Welsh
- X
  - Xhosa
- Y
  - Yoruba
- Z
  - Zulu   

