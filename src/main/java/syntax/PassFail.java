package syntax;

/**
 *
 * @author Bogdan.Nica
 *
 * used for now in ASSERT mainly to report assert result
 *
 */
public class PassFail {

    private boolean isPassed = true;

    private Object ReasonFailed = new Object();

    public void setPassed(boolean passed) {
        isPassed = passed;
    }

    public void setReasonFailed(Object reasonFailed) {
        ReasonFailed = reasonFailed;
    }

    public boolean IsPassed() {
        return isPassed;
    }

    public Object getReasonFailed() {
        return ReasonFailed;
    }
}