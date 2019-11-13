package atfs;

import com.sun.javafx.util.Utils;
import interfaces.iTfs;
import lib.Common;
import org.openqa.selenium.WebDriver;
import syntax.FeedData;
import syntax.PassFail;
import syntax.TfsVars;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Tfs implements iTfs {

    public abstract iTfs run(String input);

    /**
     * stores variables that can be used in the future
     */
    protected ConcurrentHashMap<String, Object> locals;
    protected List<PassFail> reports;
    protected WebDriver _driver;

    /**
     * holds all chromedrivers that are presently open,
     * to prevent closing one that didn't finissh the test
     */
    protected ConcurrentHashMap<Integer, String> drvrs;

    protected String _driverSessionId = "";


    public Tfs() {
        initialize();
    }

    protected void initialize() {
        //prepare collections for parallel access:
        this.locals = Common.calculateSegmentSize(Common.NUM_CORES);
        this.drvrs = Common.calculateSegmentSize(Common.NUM_CORES);
        reports = new ArrayList<>();
    }

    /**
     * it returns the first string that is not SYNC or ASYNC in condition field
     * @return
     */
    protected String getConditionFromAstfCommand(FeedData feed){
        String res = "";
        String[]split = Utils.split(feed.getCondition(), ",");
        for(String value : split){
            if(value.equals(FeedData.ASYNC) || value.equals(FeedData.SYNC))continue;

            return value;
        }

        return res;
    }

    public Tfs setLocal(String input){
        TfsVars.setVarsFromString(input,locals);
        return this;
    }


    @Override
    public String replaceKeys(String text) {
        text = TfsVars.replaceKeys(text, locals);
        return text;
    }

    protected static void pause(long miliseconds) {
        try {
            Thread.sleep(miliseconds);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}
