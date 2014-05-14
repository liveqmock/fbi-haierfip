package pub.platform.utils;

public class EncryptData {

     private static boolean loaded = false;

     /**
      *  Gets the desKeyInfo attribute of the EncryptData object
      *
      *@param  keyAsiInfo  Description of the Parameter
      *@param  aKeyBcd     Description of the Parameter
      *@return             The desKeyInfo value
      */
     public native synchronized byte[] getDesKeyInfo(byte[] keyAsiInfo, byte[] aKeyBcd);

     /**
      *  Description of the Method
      *
      *@param  text         Description of the Parameter
      *@param  macKeyBlk16  Description of the Parameter
      *@param  bmkKey       Description of the Parameter
      *@return              Description of the Return Value
      */
     public native synchronized byte[] macData(byte[] text, byte[] macKeyBlk16, byte[] bmkKey);

     /**
      *  Description of the Method
      *
      *@param  flag  Description of the Parameter
      *@param  text  Description of the Parameter
      *@param  Key   Description of the Parameter
      *@return       Description of the Return Value
      */
     public native synchronized byte[] des(int flag, byte[] text, byte[] Key);

     /**
      *  Description of the Method
      *
      *@param  passwd  Description of the Parameter
      *@return         Description of the Return Value
      */
     //public native synchronized byte[] enPasswd(byte[] passwd);
     public synchronized byte[] enPasswd(byte[] passwd){
         return passwd;
     }
     /**
      *  Description of the Method
      *
      *@param  enpasswd  Description of the Parameter
      *@return           Description of the Return Value
      */
     public native synchronized byte[] dePasswd(byte[] enpasswd);

     /**
      *  Description of the Method
      *
      *@param  bmk  Description of the Parameter
      *@return      Description of the Return Value
      */
     public native synchronized byte[] enBmkKey(byte[] bmk);

     /**
      *  Description of the Method
      *
      *@param  enbmk  Description of the Parameter
      *@return        Description of the Return Value
      */
     public native synchronized byte[] deBmkKey(byte[] enbmk);

     /**
      *  The main program for the EncryptData class
      *
      *@param  args  The command line arguments
      */
     public static void main(String[] args) {
          EncryptData test = new EncryptData();

          String macdata = "12345678901234567890";
          String mackey = "ABC123ACD456BND7";
          String bmkkey = "12345678901234567890123456789012";

          String desdata = "12345678";
          String deskey = "1234567890123456";

          byte[] aa = test.macData(macdata.getBytes(), mackey.getBytes(), bmkkey.getBytes());
          System.out.println(new String(aa));

          byte[] result = test.des(1, desdata.getBytes(), deskey.getBytes());

          for(int i = 0; i < result.length; i++) {
               System.out.print("|" + (result[i] < 0 ? result[i] + 256 : result[i]));
          }
          System.out.println();
          byte[] old = test.des(0, result, deskey.getBytes());
          System.out.println(new String(old));
          String pwd = "000000";
          byte[] enpwd = test.enPasswd(pwd.getBytes());
          System.out.println(new String(enpwd));
          System.out.println(new String(test.dePasswd("D6D294A925E8DC95".getBytes())));
          byte[] enbmk = test.enBmkKey(bmkkey.getBytes());
          System.out.println(new String(enbmk));
          System.out.println(new String(test.deBmkKey(enbmk)));
     }

     /**
      *  Constructor for the EncryptData object
      */
     public EncryptData() {
          if(!loaded) {
               try {
                    //System.loadLibrary("bankinfoencrypt");
                    loaded = true;
               } catch(Throwable e) {
                    System.out.println("¼ÓÔØÊ§°Ü£º" + e.getMessage());
               }
          }
     }

     /**
      *  Gets the macData attribute of the EncryptData object
      *
      *@param  rawText  Description of the Parameter
      *@return          The macData value
      */
     public byte[] getMacData(byte[] rawText) {
          System.out.println("Raw text is " + new String(rawText));
          byte[] out = macData(rawText, KeyManager.getMacKey(), KeyManager.getBmkKey());
          System.out.println("Mac text is " + new String(out));
          return out;
     }
}