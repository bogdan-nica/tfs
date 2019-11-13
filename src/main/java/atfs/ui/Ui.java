package atfs.ui;

import atfs.Tfs;
import interfaces.iUi;
import lib.Common;
import lib.FileHelper;
import lib.NowHelper;
import lib.RegexHelper;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import syntax.*;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

public class Ui extends Tfs implements iUi {

    public Ui() {
        initialize();
    }

    @Override
    protected void initialize() {
        super.initialize();
        isClear = false;
        isParent = false;
        isVisible = false;
        Common.getChromeVersionFromFile();
    }

    @Override
    public Ui click(FeedData feed) {
        reset();
        String data = super.replaceKeys(
                Common.restoreExceptedChars(
                        Common.replaceExceptedChars(feed.getData())));
        WaitMaxUntil wait =  new WaitMaxUntil(
                super.replaceKeys(feed.getInstruct()));
        //String condition = super.replaceKeys(super.getConditionFromAstfCommand());
        String instruct = super.replaceKeys(feed.getInstruct());
        try {
            if (wait.getIsSet()) {
                if (wait.getMax() > 0) {
                    clickElem(data, instruct);

                    if(UiHelper.waitForUntilElem(_driver,wait, 500,getIsVisible(), getIsParent())){
                        System.out.println("FOUND " + wait.getUntil());
                    }else {System.out.println("NOT FOUND " + wait.getUntil());}

                }else{
                    _driver.manage().timeouts().implicitlyWait(wait.getMax(), TimeUnit.MILLISECONDS);
                    clickElem(data, instruct);
                }
            }else {
                clickElem(data, instruct);
            }
        } catch (Exception ex) {
            String msg = "COULDN'T CLICK elem at: "
                    + Common.restoreExceptedChars(feed.getLine())
                    +Common.LF + ex.getMessage();
            System.out.println(msg);
        }
        if(wait.getUntil().isEmpty()){
            Common.pause(wait.getMax());
        }
        return this;
    }

    @Override
    public iUi click(String elemXpath, String instruct, boolean isAsync) {
        FeedData fd = createFeed(CLICK, elemXpath, isAsync, instruct);
        return click(fd);
    }

    private void clickElem(String xpath, String instruct) {
        WebElement elem = UiHelper.findElement(xpath, _driver, getIsVisible(), getIsParent(), false);
        if (elem != null) {
            Offset off = new Offset(instruct);
            if (off.getIsSet()) {
                int x = elem.getSize().getWidth();
                int y = elem.getSize().getHeight();

                int offX = (x / 2) + off.getX();
                int offY = (y / 2) + off.getY();
                String msg = String.format("Elem dimensions: width: %d | height: %d  |  offset x: %d | y: %d)",
                        x, y, offX, offY);
                System.out.println(msg);
                Actions act = new Actions(_driver);
                act.moveToElement(elem).moveByOffset(offX, offY).click().perform();
            } else {
                elem.click();
            }
        } else {
            System.out.println("COULDN'T FIND elem for xpath " + xpath);
        }
    }

    @Override
    public Ui text(FeedData feed) {
        reset();
        String data = super.replaceKeys(feed.getData());
        StringAndKeys sk = analizeTextInstruct(feed);
        //String condition = super.replaceKeys(super.getConditionFromAstfCommand());
        try {
            WebElement elem = UiHelper.findElement(sk.getString(), _driver,getIsVisible(), getIsParent(), false);
            if(elem != null) {
                if (getIsClear()) {
                    elem.clear();
                    elem.click();
                }
                elem.sendKeys(data);
            }else {
                System.out.println("CANNOT FIND elem with: " + sk.getString());
            }
        } catch (Exception ex) {
            String msg = "COULDN'T TEXT elem at: "
                    + Common.restoreExceptedChars(feed.getLine())
                    + Common.LF + ex.getMessage();
            System.out.println(msg);
        }
        return this;
    }

    @Override
    public iUi text(String text, String elemXpath, boolean isAsync) {
        FeedData fd = createFeed(CLICK, text, isAsync, elemXpath);
        return text(fd);
    }

    private StringAndKeys analizeTextInstruct(FeedData feed){
        String instruct = super.replaceKeys(
                Common.replaceExceptedChars(feed.getInstruct()));
        return new StringAndKeys(instruct);
    }

    /**
     * EX FIND  //*[contains(.,'Bet Id')]|SYNC?bet.id,REGEX .+\?Bet Id.+\?[>](\d+)[<].+
     * @param feed
     * @return
     */
    @Override
    public Ui find(FeedData feed) {
        reset();
        String xpath = feed.getData();
        String instruct = TfsVars.replaceKeys(Common.replaceExceptedChars(feed.getInstruct()), super.locals);
        StringAndKeys sk = new StringAndKeys(instruct);
        try {
            String regex = "";
            if (sk.getIsSet()) {
                String key = sk.getString();
                if (sk.getKeys().length > 0) {
                    for (String val : sk.getKeys()) {
                        if (val.trim().startsWith(Common.REGEX)) {
                            regex = val.replace(Common.REGEX, "").trim();
                        }
                    }
                }

                WebElement elem = UiHelper.findElement(xpath, _driver, getIsVisible(), getIsParent(), false);
                if (elem != null) {
                    String innercode = elem.getAttribute("innerHTML");
                    String value = RegexHelper.findString(innercode, regex);
                    super.setLocal(key + ":" + value);
                }else {
                    System.out.println("CANNOT FIND elem with: " + xpath);
                    super.setLocal(key + ":" + Common.NOT_FOUND);
                }
            } else {
                String msg = "SYNTAX ERROR: at line" + feed.getLine();
                throw new TfsSyntaxError(msg);
            }
        } catch (Exception ex) {
            String msg = String.format("COULDN'T FIND elem at: %1$s %2$s %3$s",Common.restoreExceptedChars(feed.getLine()),Common.LF, ex.getMessage());
            System.out.println(msg);
            ex.printStackTrace();
        }
        return this;
    }

    @Override
    public iUi find(String elemXpath, String instruct, boolean isAsync) {
        FeedData fd = createFeed(CLICK, elemXpath, isAsync, instruct);
        return find(fd);
    }

    @Override
    public Ui get(FeedData feed) {
        reset();
        String url = super.replaceKeys(feed.getData());
        WaitMaxUntil wait = new WaitMaxUntil(
                super.replaceKeys(feed.getInstruct()));
        if (wait.getIsSet()){
            if (wait.getMax() > 0) {
                _driver.get(url);

                if(UiHelper.waitForUntilElem(_driver,wait,500,getIsVisible(), getIsParent())){
                    System.out.println("FOUND " + wait.getUntil());
                }else {System.out.println("NOT FOUND " + wait.getUntil());}

            }else {
                _driver.manage().timeouts().implicitlyWait(wait.getMax(), TimeUnit.MILLISECONDS);
                _driver.get(url);
            }
        }else {
            _driver.get(url);
        }
        return this;
    }

    @Override
    public Ui get(String url, String instruct, boolean isAsync) {
        FeedData fd = createFeed(CLICK, url, isAsync, instruct);
        return get(fd);
    }

    @Override
    public Ui pause(FeedData feed) {
        long val = NowHelper.getMillisec(super.replaceKeys(feed.getData()));
        Common.pause(val);
        return null;
    }

    @Override
    public Ui pause(String input) {
        long val = NowHelper.getMillisec(super.replaceKeys(input));
        Common.pause(val);
        return this;
    }

    @Override
    public Ui set(String input) {
        super.setLocal(input);
        return this;
    }

    @Override
    public Ui start(FeedData feed) {
        String browser = super.replaceKeys(feed.getData());
        return start(browser);
    }

    @Override
    public Ui start(String browser) {
        //java.awt.Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        switch(browser.toLowerCase()){
            case "chrome":
                openChrome();
                break;
            case "firefox":
                String path = "./resources/drivers/geckodriver-v0.24.0-win64/geckodriver.exe";
                System.setProperty("webdriver.firefox.marionette",path);
                _driver = new FirefoxDriver();
                break;
        }
        _driver.manage().window().maximize();
        return this;
    }

    private void openChrome() {
        boolean found = Common.chromeVersion > 0;
        if (!found) {
            startChromeFindVersion();
        } else {
            String path = String.format("%s/chromedriver_win32_%d/chromedriver.exe",
                                        Common.relPath,Common.chromeVersion);
            System.setProperty("webdriver.chrome.driver", path);
            _driver = new ChromeDriver();
        }
        _driverSessionId = ((ChromeDriver) _driver).getSessionId().toString();
        System.out.println(" | " + _driverSessionId);
    }

    /**
     * it finds the Chrome version and sets the value in a file to be used
     * for future tests.
     * this should run only once
     */
    private void startChromeFindVersion(){
        int minVersion = 74;
        int maxVersion = 79;
        int i = minVersion;
        while (i < maxVersion) {
            String path =  String.format("%s/chromedriver_win32_%d/chromedriver.exe", Common.relPath, i);
            System.setProperty("webdriver.chrome.driver", path);
            try {
                _driver = new ChromeDriver();
                Common.chromeVersion = i;
                String pathVersion = FileHelper.getAbsolute(path.split("src/")[0] + "chromeVersion.txt");
                FileHelper.write(((Integer)i).toString(),pathVersion, false);
                String msg = String.format("FOUND browser version: %d\nA file was created at: %s",i,pathVersion);
                System.out.println(msg);
                break;
            } catch (Exception ex) { i++; }
        }
    }

    @Override
    public Ui stop() {

        _driver.close();
        //killDriver();
        return this;
    }

    private String urlRoot;
    public Ui urlRoot(String input) {
        urlRoot = input;
        return this;
    }

    @Override
    public int getResults() {
        int res = 0;
        for (PassFail pf : super.reports) {
            if (!pf.IsPassed()) {
                res += 1;
                System.out.println(pf.getReasonFailed());
            }
        }
        return res;
    }

    @Override
    public Ui run(String input) {
        FeedData fd = new FeedData(input);
        switch (fd.getCommand()) {
            case ASSERT:
                return assertUi(fd);
            case CLICK:
                return click(fd);
            case FIND:
                return find(fd);
            case GET:
                return get(fd);
            case PAUSE:
                return pause(fd);
            case SET:
                return set(fd.getData());
            case START:
                return start(fd);
            case STOP:
                return stop();
            case TEXT:
                return text(fd);
            default:
                String msg = "SYNTAX ERROR: Command not found in line: " + fd.getLine();
                throw new TfsSyntaxError(msg);
        }
    }

    @Override
    public Ui assertUi(FeedData feed) {
        String compare = feed.getData();
        String messageFailed = super.replaceKeys(Common.restoreExceptedChars(
                Common.replaceExceptedChars(feed.getInstruct())));
        String condition = super.replaceKeys(Common.restoreExceptedChars(
                Common.replaceExceptedChars(super.getConditionFromAstfCommand(feed))));
        PassFail pf = new PassFail();
        try {
            String msg = ASSERT;
            if (compare.contains(Common.THIS)) {

                String[] found = RegexHelper.findAllStrings(
                        super.replaceKeys(Common.restoreExceptedChars(Common.replaceExceptedChars(compare))),
                        RegexHelper.rxAssertXpath);

                String first = super.replaceKeys(Common.restoreExceptedChars(
                        Common.replaceExceptedChars(found[1].trim())));
                String operator = found[2].trim();
                String second = super.replaceKeys(Common.restoreExceptedChars(
                        Common.replaceExceptedChars(found[3].trim())));
                if(second.matches(RegexHelper.rxXpath) || second.matches("(" + RegexHelper.rxBy + ")")){
                    pf = UiHelper.compareThis(first, operator, second,_driver, getIsVisible(), getIsParent());
                }else {
                    pf.setPassed(Compare.analiseCondition(this, feed.getData()));
                }
            } else {
                pf.setPassed(Compare.analiseCondition(this, feed.getData()));
            }
            super.reports.add(pf);
            if (pf.IsPassed()) {
                System.out.println(msg + " PASSED");
            } else {
                //System.out.println(msg + " FAILED: " + messageFailed);
                pf.setReasonFailed(msg + " FAILED: " + messageFailed);
            }
        } catch (Exception ex) {
            String msg = "COULDN'T FIND elem at: " + Common.restoreExceptedChars(feed.getLine())
                    + Common.LF + ex.getMessage();
            pf.setReasonFailed(msg);
            System.out.println(msg);
        }
        return this;
    }

    private static String template = "%1$s %2$s|%3$s?%4$s";
    private static FeedData createFeed(String command,
                                       String data,
                                       boolean isAsync,
                                       String instruct) {
        String condition = FeedData.SYNC;
        if (isAsync) condition = FeedData.ASYNC;
        String strFeed = String.format(template, command, data, condition, instruct);
        FeedData fd = new FeedData(strFeed);
        return fd;
    }

    @Override
    public iUi assertUi(String statement, String instruct, boolean isAsync) {
        FeedData fd = createFeed(ASSERT, statement, isAsync, instruct);
        return assertUi(fd);
    }

    @Override
    public WebElement findElement(String xpath, int maxWait) {
        if (maxWait > 0) {
            _driver.manage().timeouts().implicitlyWait(maxWait, TimeUnit.MILLISECONDS);
        }
        WebElement elem = UiHelper.findElement(xpath, _driver, getIsVisible(), getIsParent(), false);
        return elem;
    }

    public WebDriver getDriver(){return _driver;}

    private void reset(){

        isClear = false;
        isParent = false;
        isVisible = false;

    }
    private boolean isClear;

    public void setIsClear(boolean value) {
        isClear = value;
    }

    public boolean getIsClear() {
        return isClear;
    }

    private boolean isParent;

    public void setIsParent(boolean value) {
        isParent = value;
    }

    public boolean getIsParent() {
        return isParent;
    }

    private boolean isVisible;

    public void setIsVisible(boolean value) {
        isVisible = value;
    }

    public boolean getIsVisible() {
        return isVisible;
    }

    private static final String CLICK = "CLICK",
            ASSERT = "ASSERT",
            FIND = "FIND",
            GET = "GET",
            PAUSE = "PAUSE",
            SET = "SET",
            START = "START",
            STOP = "STOP",
            TEXT = "TEXT";

}