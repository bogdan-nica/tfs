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
         * NOTE: except syntax chars : ;
         */
        ui = ui.run("SET server:google;url.root:https\\://www.${server}.com;")
               .run("SET url:${url.root};")
               .run("SET exp.xpath://*[contains(.,'Google')];")
               .run("START Chrome")
               .run("GET ${url}|SYNC?MAX 15sec,UNTIL ${exp.xpath}")
               .run("ASSERT THIS CONTAINS ${exp.xpath}|SYNC?Failed to load page")
               .run("PAUSE 5sec")
               .run("STOP Chrome");

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
        System.out.println("Browser and chromedriver will close after 5 sec!");
        ui = ui.pause("5sec")
               .stop();
    }

    @Test
    public void testGoogleSearchUsingRun() {
        iUi ui = new Ui();
        //NOTE: below use straight values, no SET values type ${}
        //      however repetition of the same string in
        //      multiple placesis not a good approach
        ui = ui.run("START Chrome")
                .run("GET https://www.google.com/|SYNC?MAX 15sec,UNTIL //*[contains(.,'Google')]")
                .run("ASSERT THIS CONTAINS //*[contains(.,'Google')]|SYNC?Failed to load page")
                .run("TEXT mobile integration workgroup|SYNC?//input[@name=\"q\"]")
                .run("CLICK //input[@type=\"submit\"]|SYNC?MAX 20sec,UNTIL //*[contains(.,'mobile integration')]")
                .run("ASSERT THIS CONTAINS //*[contains(.,'mobile integration workgroup')]|SYNC?Failed to perform search");

        int res = ui.getResults();
        System.out.println("TEST RETURN: " + res);
        assertTrue(res == 0);

        System.out.println("Browser and chromedriver will close after 5 sec!");
        ui = ui.run("PAUSE 5sec")
               .run("STOP Chrome");
    }

    @Test
    public void testGoogleSearchUsingAtomicActions() {
        iUi ui = new Ui();

        ui = ui.set("url:https\\://www.google.com/;exp.xpath://*[contains(.,'Google')];")
                .set("look.for:mobile integration workgroup")
                .start("Chrome")
                .get("${url}", "MAX 15sec,UNTIL ${exp.xpath}", false)
                .assertUi("THIS CONTAINS ${exp.xpath}", "Failed to load page", false)
                .text("${look.for}", "//input[@name=\"q\"]", false)
                .click("//input[@type=\"submit\"]",
                        "MAX 20sec,UNTIL //*[contains(.,'${look.for}')]",false)
                .assertUi("THIS CONTAINS //*[contains(.,'${look.for}')]",
                        "Failed to perform search",false)
                .pause("5sec")
                .stop();


        int res = ui.getResults();
        System.out.println("TEST RETURN: " + res);
        assertTrue(res == 0);
    }

    @Test
    public void testRitchieBrosUsingRun() {
        iUi ui = new Ui();

        /**NOTE: reusing the begining of test1 and change "server" and "exp.xpath"
         * there is an automated gmail search script using the same approach
         * the mail class is not included in this project
         */
        ui = ui.run("SET server:rbauction;url.root:https\\://www.${server}.com;")
                .run("SET url:${url.root};")
                .run("SET exp.xpath://*[contains(.,'auction')];")
                .run("SET int.year:2019 - 2;")
                .run("START Chrome")
                .run("GET ${url}|SYNC?MAX 15sec,UNTIL ${exp.xpath}")
                .run("ASSERT THIS CONTAINS ${exp.xpath}|SYNC?Failed to load page")
                .run("SET user.email:bogdan.nica.van@gmail.com;user.pass:Bogdan1234")
                .run("CLICK //a[contains(.,'Sign In')]|SYNC?MAX 20sec,UNTIL //*[contains(@id,'_login')]")
                .run("TEXT ${user.email}|SYNC?//*[contains(@id,'_login')]")//*[@id="_58_login"]
                .run("TEXT ${user.pass}|SYNC?//*[contains(@id,'_password')]")
                .run("CLICK //input[@title='Sign In']|SYNC?MAX 15sec,UNTIL //*[contains(.,'Hello b')]")
                .run("ASSERT THIS CONTAINS //*[contains(.,'Hello b')]|SYNC?Failed to login")
                .run("TEXT excavator|SYNC?//*[@id=\"simple-keyword-search\"]")//*[@id="simple-keyword-search"]
                .run("CLICK //*[@id=\"keyword-submit\"]|SYNC?MAX 25sec,UNTIL //*[contains(.,'excavator')],VISIBLE")//*[@id="keyword-submit"]
                .run("ASSERT THIS CONTAINS //button[contains(.,'Add to Watchlist')]|SYNC?Failed to search")
                //.run("CLICK //label[contains(.,'CATERPILLAR')]|SYNC?MAX 25sec,UNTIL //*[contains(.,'${int.year}') and contains(.,'CATERPILLAR')],VISIBLE")//*[@id="manufacturer_year_dt"]/div/div/div/div[1]/input[1]
                //.run("TEXT ${int.year}|SYNC?//*[contains(.,'1980')],CLEAR")
                //Or just cgi param
                .run("SET relative:caterpillar\\?keywords=excavator&manufacturer_name=CATERPILLAR&manufacturer_year_dt=${int.year},2204;")
                .run("SET exc.xpath://a[contains(.,'${int.year} CATERPILLAR')];expected://*[contains(.,'Meter Reads')];")//*[@id="11576181_ci_title"]/a
                .run("GET ${url.root}/${relative}|SYNC?MAX 25sec,UNTIL ${exc.xpath},VISIBLE")
                //.run("PAUSE 8sec")
                .run("CLICK ${exc.xpath}|SYNC?MAX 20sec,UNTIL ${expected},VISIBLE")
                .run("ASSERT THIS CONTAINS ${expected}");

        int res = ui.getResults();
        System.out.println("TEST RETURN: " + res);
        assertTrue(res == 0);

        System.out.println("Browser and chromedriver will close after 5 sec!");
        ui = ui.run("PAUSE 5sec")
               .run("STOP Chrome");
    }

    @Test
    public void testRitchieBrosUsingAtomicActions() {
        iUi ui = new Ui();

        /**NOTE: reusing the begining of test1 and change "server" and "exp.xpath"
         * there is an automated gmail search script using the same approach
         * the mail class is not included in this project
         */
        ui = ui.set("server:rbauction;url.root:https\\://www.${server}.com;")
                .set("url:${url.root};")
                .set("exp.xpath://*[contains(.,'auction')];")
                .set("int.year:2019 - 2;")
                .start("Chrome")
                .get("${url}","MAX 15sec,UNTIL ${exp.xpath}",false)
                .assertUi("THIS CONTAINS ${exp.xpath}","Failed to load page", false)
                .set("user.email:bogdan.nica.van@gmail.com;user.pass:Bogdan1234")
                .click("//a[contains(.,'Sign In')]","MAX 20sec,UNTIL //*[contains(@id,'_login')]",false)
                .text("${user.email}","//*[contains(@id,'_login')]", false)//*[@id="_58_login"]
                .text("${user.pass}","//*[contains(@id,'_password')]",false)
                .click("//input[@title='Sign In']","MAX 15sec,UNTIL //*[contains(.,'Hello b')]",false)
                .assertUi("THIS CONTAINS //*[contains(.,'Hello b')]","Failed to login",false)
                .text("excavator","//*[@id=\"simple-keyword-search\"]",false)//*[@id="simple-keyword-search"]
                .click("//*[@id=\"keyword-submit\"]","MAX 25sec,UNTIL //*[contains(.,'excavator')],VISIBLE",false)//*[@id="keyword-submit"]
                .assertUi("THIS CONTAINS //button[contains(.,'Add to Watchlist')]","Failed to search", false)
                .set("relative:caterpillar\\?keywords=excavator&manufacturer_name=CATERPILLAR&manufacturer_year_dt=${int.year},2204;")
                .set("exc.xpath://a[contains(.,'${int.year} CATERPILLAR')];expected://*[contains(.,'Meter Reads')];")//*[@id="11576181_ci_title"]/a
                .get("${url.root}/${relative}","MAX 25sec,UNTIL ${exc.xpath},VISIBLE",false)
                //.run("PAUSE 8sec")
                .click("${exc.xpath}","MAX 20sec,UNTIL ${expected},VISIBLE",false)
                .assertUi("THIS CONTAINS ${expected}", "failed to find item",false);

        int res = ui.getResults();
        System.out.println("TEST RETURN: " + res);
        assertTrue(res == 0);

        System.out.println("Browser and chromedriver will close after 5 sec!");
        ui = ui.pause("5sec")
              .stop();
    }

    @Test
    public void find() {
        //this can be used to find strings in the page's source.
        // It can be extended to find strings in DOM as well
        FeedData fd = new FeedData("FIND  //*[contains(.,'Bet Id')]|SYNC?bet.id,REGEX .+\\?Bet Id.+\\?[>](\\d+)[<].+");
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