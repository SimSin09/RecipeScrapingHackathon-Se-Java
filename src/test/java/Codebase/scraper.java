package Codebase;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class scraper {

    public static void main(String[] args) throws IOException, InvalidFormatException {

        WebDriver driver = setupandgetdriver();
        for (int k = 65; k < 66; k++) {
            char ch = (char) k;
            launchRecipeURL(ch, driver);
            int lastPageNumber = getLastPageNumber(driver);
            StringBuilder link = new StringBuilder(String.format("https://www.tarladalal.com/RecipeAtoZ.aspx?beginswith=%s&pageindex=", (char) k));
            for (int j = 1; j < lastPageNumber; j++) {
                driver.get(link.toString() + j);
                driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                getReceipeCardValues(driver, ch);
                if (j >= 1) {
                    driver.quit();
                    driver = setupandgetdriver();
                }
            }

        }
    }

    public static String getRecipeURL(char ch) {
        String url = String.format("https://www.tarladalal.com/RecipeAtoZ.aspx?beginswith=%c&pageindex=1", ch);
        return url;
    }

    public static void launchRecipeURL(char ch, WebDriver driver) {
        String launchURL = getRecipeURL(ch);
        driver.get(launchURL);
    }

    public static int getLastPageNumber(WebDriver driver) {
        int lastPageNumber = 0;
        try {
            List<WebElement> pageList = driver.findElements(By.xpath("//*[@id=\"maincontent\"]/div[1]/div[2]/a"));
            int pageListSize = pageList.size();
            String lastPageNumberinStringFormat = pageList.get(pageListSize - 1).getText();
            lastPageNumber = Integer.parseInt(lastPageNumberinStringFormat);
            System.out.println(lastPageNumber);
            return lastPageNumber;
        } catch (Exception e) {
        }
        return lastPageNumber;
    }

    public static void getReceipeCardValues(WebDriver driver, char ch) {
        List<WebElement> recipelist = driver.findElements(By.xpath("//span[@class='rcc_recipename']"));
        for (int i = 1; i < recipelist.size(); i++) {
            //Identify and capture the title
            String title = recipelist.get(i).getText();
            System.out.println("Name of the recipe :" + title);
            if (title.contains(("Flower Arrangements")) || title.contains("Vegetable Carvings")) {
                System.out.println("Not a Recipe");
            } else {
                recipelist.get(i).click();

                try {
                    Recipe recipe = extractData(driver, ch);
                    recipe.setTitle(title);
                    ExcelWriter.writeToExcel(recipe);
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
                driver.navigate().back();
                recipelist = driver.findElements(By.xpath("//span[@class='rcc_recipename']"));

            }
        }

    }

    public static Recipe extractData(WebDriver driver, char ch) throws Exception {
        Recipe recipe = new Recipe();
        //Identify and capture category
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        WebElement category = driver.findElement(By.cssSelector("#show_breadcrumb > div"));
        recipe.setCategory(category.getText().substring(33));
        System.out.println("Category of the recipe :" + category.getText().substring(33));
        //Identify and capture ingredients
        WebElement ingredients = driver.findElement(By.id("rcpinglist"));
        recipe.setIngredients(ingredients.getText());
        System.out.println("Ingredients of the recipe :" + ingredients.getText());
        //Identify and capture the steps
        WebElement steps = driver.findElement(By.id("recipe_small_steps"));
        recipe.setSteps(steps.getText());
        System.out.println("Steps: " + steps.getText());
        //Identify and capture Nutrients
        Boolean isnutrientpresent = driver.findElements(By.xpath("//table[@id='rcpnutrients']")).size() > 0;
        if (isnutrientpresent) {
            WebElement nutrients = driver.findElement(By.xpath("//table[@id='rcpnutrients']"));
            recipe.setNutrients(nutrients.getText());
            System.out.println("Nutrient value :" + nutrients.getText());
        } else {
            System.out.println("No nutrient value Found for this recipe");
        }
        //Identify and capture the image
        WebElement imagelink = driver.findElement(By.id("ctl00_cntrightpanel_imgRecipe"));
        recipe.setImagelink(imagelink.getAttribute("src"));
        System.out.println("image link is:" + imagelink.getAttribute("src"));
        //Identify and capture the URL
        String url = driver.getCurrentUrl();
        recipe.setUrl(url);
        recipe.setAlphabet(String.valueOf(ch));
        System.out.println("Recipe link is:" + url);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        return recipe;
    }

    public static WebDriver setupandgetdriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
        options.setHeadless(true);
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        WebDriver driver = new ChromeDriver(options);
        return driver;
    }
}
