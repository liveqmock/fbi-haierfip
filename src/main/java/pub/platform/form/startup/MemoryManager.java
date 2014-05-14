package pub.platform.form.startup;

public class MemoryManager extends Thread
{
    public void run() {
        while ( true ) {
            try {
                long total = Runtime.getRuntime().totalMemory();
                long free = Runtime.getRuntime().freeMemory();
                double percent = (double) free / (double) total;
                if ( ( (free / (1024 * 1024)) < 100) || (percent < 0.4)) {
                    System.gc();
                }
                Thread.sleep(120000);
            } catch (Exception e) {

            }
        }
    }
    public static void main(String[] args)
    {
    }
}