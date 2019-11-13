package interfaces;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import syntax.FeedData;

public interface iUi {

    iUi assertUi(FeedData feed);
    iUi assertUi(String statement, String instruct, boolean isAsync);

    iUi click(FeedData feed);
    iUi click(String elemXpath, String instruct, boolean isAsync);

    iUi text(FeedData feed);
    iUi text(String text, String elemXpath, boolean isAsync);

    iUi find(FeedData feed);
    iUi find(String elemXpath, String instruct, boolean isAsync);

    WebElement findElement(String xpath, int maxWait);

    WebDriver getDriver();

    iUi get(FeedData feed);
    iUi get(String url, String instruct, boolean isAsync);


    iUi pause(FeedData feed);
    iUi pause(String input);

    iUi run(String input);

    iUi set(String input);

    iUi start(String browser);

    iUi start(FeedData feed);

    iUi stop();

    iUi urlRoot(String input);

    int getResults();

    void setIsClear (boolean value);
    boolean getIsClear ();

    void setIsParent(boolean value);
    boolean getIsParent();

    void setIsVisible(boolean value);
    boolean getIsVisible();

}
