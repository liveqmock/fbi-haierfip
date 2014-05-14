package pub.platform.utils;


public class Debug {
    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_WARNING = 1;
    public static final int TYPE_ERROR = 2;
    public static final int TYPE_SQL = 3;
    public static final boolean isDebugMode = true;

    private static boolean isDebug = true;


    public static void debug(String message) {
        if ( isDebug ) {
            System.out.println(message);
        }
    }

    /**
     *
     * @param typ : MESSAGE or ERROR or WARNING
     * @param message
     */
    public static void debug(int typ, String message) {
        if ( isDebug ) {
          if(typ == Debug.TYPE_ERROR) System.out.println("[ERROR]" +message);
            else if(typ == Debug.TYPE_WARNING) System.out.println("[WARNING]" +message);
              else if(typ == Debug.TYPE_MESSAGE) System.out.println("[MESSAGE]" +message);
                else if(typ == Debug.TYPE_SQL) System.out.println("[SQL]" +message);
                  else System.out.println("[OTHER]" +message);

        }
    }


    public static void print(String message) {
        if (isDebug) {
            System.out.print(message);
        }
    }

    public static void println(String message) {
        if ( isDebug ) {
            System.out.println(message);
        }
    }

}
