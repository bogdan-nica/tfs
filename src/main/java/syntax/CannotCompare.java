package syntax;


/**
 *
 * @author Bogdan.Nica
 *
 *exception class to be used in comparisons that are failing
 * reduce verbose reports the failure
 * We want the framework to continue testing if needed.
 *
 * TODO:    in progress
 *
 */
public class CannotCompare  extends RuntimeException {

    //private static final long serialVersionUID = ;

    public CannotCompare(String message) {
        System.out.println(message);
    }
}