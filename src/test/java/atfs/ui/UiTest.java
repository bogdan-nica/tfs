package atfs.ui;

import interfaces.iUi;
import org.junit.Test;
import syntax.FeedData;

import static org.junit.Assert.*;

public class UiTest {

    @Test
    public void testExample1UsingRun() {
        iUi ui = new Ui();

        /**
         * below the approach that is using just one call
         */
        ui = ui.run("SET server:google;url.root:https\\://www.${server}.com;")
               .run("SET url:${url.root};")
               .run("SET exp.xpath://*[contains(.,'Google')];")
               .run("START Chrome")
               .run("GET ${url}|SYNC?MAX 15sec,UNTIL ${exp.xpath}")
               .run("ASSERT THIS CONTAINS ${exp.xpath}|SYNC?Failed to load page");

        int res = ui.getResults();
        System.out.println("TEST RETURN: " + res);
        assertTrue(res == 0);
    }

    @Test
    public void testExample1UsingAtomicActions() {
        iUi ui = new Ui();

        /**
         * below the approach that is using just one call
         */
        ui = ui.set("server:google;url.root:https\\://www.${server}.com;url:${url.root};")
                .set("exp.xpath://*[contains(.,'Google')];")
                .start("Chrome")
                .get("${url}", "MAX 15sec,UNTIL ${exp.xpath}", false)
                .assertUi("THIS CONTAINS ${exp.xpath}", "Failed to load page", false);

        int res = ui.getResults();
        System.out.println("TEST RETURN: " + res);
        assertTrue(ui.getResults() == 0);

        //Pause for given time and close the browser:
        ui.pause("30sec")
                .stop();
    }


    @Test
    public void testExample2UsingRun() {
        iUi ui = new Ui();

        ui = ui.run("")
                .run("");

        int res = ui.getResults();
        System.out.println("TEST RETURN: " + res);
        assertTrue(res == 0);
    }

    @Test
    public void testExample2UsingAtomicActions() {
        iUi ui = new Ui();

        ui = ui.set("")
                .start("");//....

        int res = ui.getResults();
        System.out.println("TEST RETURN: " + res);
        assertTrue(res == 0);

        //Pause for given time and close the browser:
        ui.pause("30sec")
                .stop();
    }

    @Test
    public void testExample3UsingRun() {
        iUi ui = new Ui();

        ui = ui.run("")
                .run("");

        int res = ui.getResults();
        System.out.println("TEST RETURN: " + res);
        assertTrue(res == 0);
    }

    @Test
    public void testExample3UsingAtomicActions() {
        iUi ui = new Ui();

        ui = ui.set("")
                .start("");//....

        int res = ui.getResults();
        System.out.println("TEST RETURN: " + res);
        assertTrue(res == 0);

        //Pause for given time and close the browser:
        ui.pause("30sec")
                .stop();
    }

    @Test
    public void find() {
        FeedData fd =new FeedData("FIND  //*[contains(.,'Bet Id')]|SYNC?bet.id,REGEX .+\\?Bet Id.+\\?[>](\\d+)[<].+");
    }

    public void testTemplate() {

        iUi ui = new Ui();

        ui = ui.run("")
                .run("");

        //OR:
        ui = ui.set("")
                .start("");//....

        int res = ui.getResults();
        System.out.println("TEST RETURN: " + res);
        assertTrue(res == 0);

        //Pause for given time and close the browser:
        ui.run("PAUSE 30sec")
          .stop();
        //OR:
        ui.pause("30sec")
          .stop();
    }

}