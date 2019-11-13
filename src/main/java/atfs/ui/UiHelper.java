package atfs.ui;

import lib.Common;
import lib.RegexHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import syntax.CannotCompare;
import syntax.PassFail;
import syntax.WaitMaxUntil;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class UiHelper {

    public static boolean waitForUntilElem(WebDriver driver, WaitMaxUntil wait, boolean isVisible, boolean isParent) {
        int count = 0;

        Long sec = wait.getMax() / 1000;
        WebDriverWait wdw = new WebDriverWait(driver, sec);
        WebElement we = null;

        we = wdw.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(wait.getUntil())));
        return we != null;
    }

    public static boolean waitForUntilElem(WebDriver driver, WaitMaxUntil wait, int increment, boolean isVisible, boolean isParent) {
        int count = 0;
        int countmax = ((Long) (wait.getMax() / increment)).intValue();

        Long sec = wait.getMax() / 1000;
        WebElement we=null;
        System.out.println("Waiting " + increment + " loops " + countmax);
        driver.manage().timeouts().implicitlyWait(increment, TimeUnit.MILLISECONDS);
        while (count < countmax) {
            System.out.print("-");
            we = findElement(wait.getUntil(), driver, isVisible, isParent, true);
            if (we != null) break;

            count++;
            Common.pause(increment);
        }
        System.out.println(Common.LF);
        return we != null;
    }

    public static WebElement findElement(String find, WebDriver driver,boolean isVisible, boolean isParent, boolean isWaiting){
        WebElement elem = null;
        try {
            if (find.startsWith("//")) {
                if (find.matches(".+?(\\[\\d+\\]).*")) {
                    //Find multiple matches:
                    String newfind = RegexHelper.findString(find, RegexHelper.rxXpath)
                            .replace(RegexHelper.findString(find, ".+?(\\[\\d+\\]).*"), "");
                    String strindex = RegexHelper.findString(find, ".+?\\[(\\d+)\\].*");
                    int index = Integer.parseInt(strindex);
                    List<WebElement> elems = driver.findElements(By.xpath(newfind));
                    if (elems.size() > index) {
                        elem = elems.get(index);
                        if(elem != null){return elem;}
                        else{
                            Verbose(newfind,isWaiting);
                            return null;
                        }
                    } else {
                        System.out.println("Collection size is " + elems.size() + " for xpath: " + find);
                    }
                } else {
                    elem = driver.findElement(By.xpath(find));
                    if(isParent) {
                        elem = elem.findElement(By.xpath("./.."));
                    }
                    if(isVisible){
                        if(elem.isDisplayed())return elem;
                    }else {
                        if (isWaiting && elem == null) {
                            Verbose(find,isWaiting);
                            return null;
                        }
                        else{return elem;}
                    }

                }
                return null;
            }

            String key = findIdType(find);
            String value = RegexHelper.findString(find, RegexHelper.rxElemValue(key), 2);
            if (value.isEmpty()) return null;

            switch (key) {
                case "id":
                    return driver.findElement(By.id(value));
                case "className":
                case "class":
                    return driver.findElement(By.className(value));
                case "cssSelector":
                    return driver.findElement(By.cssSelector(value));
                case "name":
                    return driver.findElement(By.name(value));
                case "tagName":
                    return driver.findElement(By.tagName(value));
                default:
                    Verbose(find,isWaiting);
                    return elem;
            }
        } catch (Exception ex) {
            return null;
        }
    }


    public static PassFail compareThis(String first, String operator, String second, WebDriver driver, boolean isVisible, boolean isParent) {
        PassFail pf = new PassFail();
        switch (first) {
            case Common.THIS:
                System.out.println("FOUND xpath: " + second);
                WebElement elem = findElement(second, driver, isVisible,  isParent, false);
                switch (operator) {
                    case Common.CONTAINS:
                        pf.setPassed(elem != null);
                        if(!pf.IsPassed()){
                            pf.setReasonFailed(first + " " + operator + " " + second + " NOT TRUE");
                        }
                        break;
                    case "!CONTAINS":
                        pf.setPassed(elem == null);
                        if(!pf.IsPassed()){
                            pf.setReasonFailed(first + " " + operator + " " + second + " NOT TRUE");
                        }
                        break;
                    default:
                        String msg = "BOGDAN SAIS IMPLEMENT ME";
                        pf.setReasonFailed(msg);
                        throw new CannotCompare(msg);
                }
                break;
            default:
                String msg = "BOGDAN SAIS IMPLEMENT ME in assertUi ";
                pf.setReasonFailed(msg);
                throw new CannotCompare(msg);
        }
        return pf;
    }

    /**
     * This must be the condition after replacing all keys
     * @param condition after replacing all keys
     * @return
     */
    public static boolean isXPath(String condition) {
        String[] found = RegexHelper.findAllStrings(condition, RegexHelper.rxFeedIfElseCondition);

        if (found.length != 4) {
            throw new CannotCompare(condition);
        }
        String first = found[1].trim();
        String operator = found[2].trim();
        String second = found[3].trim();
        if(first.startsWith("//")){
            return first.matches(RegexHelper.rxXpath);
        }
        if(second.startsWith("//")){
            return second.matches(RegexHelper.rxXpath);
        }
        return false;
    }

    private static String findIdType(String find) {
        String res = "";
        res = RegexHelper.findString(find, RegexHelper.rxElemType, 1);
        return res.toLowerCase();
    }

    private static void Verbose(String find, boolean isWaiting){
        if (!isWaiting) {
            String msg = "CANNOT FIND element using:  " + find;
            System.out.println(msg);
        } else System.out.print("-");
    }
}
