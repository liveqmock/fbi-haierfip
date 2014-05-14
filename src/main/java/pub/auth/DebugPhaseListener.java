package pub.auth;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DebugPhaseListener implements PhaseListener {

    public void afterPhase(PhaseEvent pe) {
        System.out.println("    after - " + pe.getPhaseId().toString());
        if (pe.getPhaseId() == PhaseId.RENDER_RESPONSE) {
            System.out.println("Done with Request!\n");
        }
    }

    public void beforePhase(PhaseEvent pe) {

        if (pe.getPhaseId() == PhaseId.RESTORE_VIEW) {
            String url = pe.getFacesContext().getExternalContext().getRequestServletPath();
            System.out.println("Processing new Request! " +
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + " " +
                    url);
        }
        System.out.print("before - " + pe.getPhaseId().toString());

    }

    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }
}
