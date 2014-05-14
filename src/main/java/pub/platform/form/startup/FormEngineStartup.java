package pub.platform.form.startup;

import pub.platform.advance.utils.PropertyManager;
import pub.platform.form.config.*;


/**
 * @author ÇëÌæ»»
 * @version 1.0
 */
public class FormEngineStartup {

    /**
     * @param argv
     * @roseuid 3F73B39B0316
     */

    public static void init() {
        TableBeanManager.getTable("testtbl");
        FormBeanManager.getForm("000001");
        EnumerationType.getEnu("");
        PropertyManager.getProperty("-100");
        MemoryManager mm = new MemoryManager();
        mm.start();
    }

    public static void main(String[] argv) {
        init();
    }
}
