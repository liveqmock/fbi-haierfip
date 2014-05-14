//Source file: e:\\java\\zt\\platform\\form\\util\\InstanceSequencer.java

package pub.platform.form.util;

import java.io.Serializable;

/**
 * ÊµÀýÐòºÅ²úÉúÆ÷
 *
 * @author qingdao tec
 * @version 1.0
 */
public class InstanceSequencer implements Serializable {
    private long sequencer=0;

    /**
     * @return String
     * @roseuid 3F72194F02FD
     */
    public synchronized String getSequence() {
      try{
        sequencer++;
      }
      catch(Exception ex){
        sequencer=0;
      }
      return sequencer+"";
    }

    /**
     * @return String
     * @roseuid 3F72311801E0
     */
    public synchronized String getCurrentSequence() {
     return sequencer+"";
    }
}
