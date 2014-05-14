/**
 *  PropertyManager.java August 01, 2000 Copyright (C) 2000 CoolServlets.com.
 *  All rights reserved. ===================================================================
 *  The Apache Software License, Version 1.1 Redistribution and use in source
 *  and binary forms, with or without modification, are permitted provided that
 *  the following conditions are met: 1. Redistributions of source code must
 *  retain the above copyright notice, this list of conditions and the following
 *  disclaimer. 2. Redistributions in binary form must reproduce the above
 *  copyright notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the distribution. 3.
 *  The end-user documentation included with the redistribution, if any, must
 *  include the following acknowledgment: "This product includes software
 *  developed by CoolServlets.com (http://www.coolservlets.com)." Alternately,
 *  this acknowledgment may appear in the software itself, if and wherever such
 *  third-party acknowledgments normally appear. 4. The names "Jive" and
 *  "CoolServlets.com" must not be used to endorse or promote products derived
 *  from this software without prior written permission. For written permission,
 *  please contact webmaster@coolservlets.com. 5. Products derived from this
 *  software may not be called "Jive", nor may "Jive" appear in their name,
 *  without prior written permission of CoolServlets.com. THIS SOFTWARE IS
 *  PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 *  NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *  PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL COOLSERVLETS.COM OR ITS
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 *  OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 *  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 *  OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  ==================================================================== This
 *  software consists of voluntary contributions made by many individuals on
 *  behalf of CoolServlets.com. For more information on CoolServlets.com, please
 *  see <http://www.coolservlets.com>.
 */

package pub.platform.advance.utils;

import java.util.*;
import java.io.*;

/**
 *  Manages properties for the entire Jive system. Properties are merely pieces
 *  of information that need to be saved in between server restarts. <p>
 *
 *  At the moment, properties are stored in a Java Properties file. In a version
 *  of Jive coming soon, the properties file format will move to XML. XML
 *  properties will allow hierarchical property structures which may mean the
 *  API of this class will have to change. <p>
 *
 *  Jive properties are only meant to be set and retrevied by core Jive classes.
 *  Therefore, skin writers should probably ignore this class. <p>
 *
 *  This class is implemented as a singleton since many classloaders seem to
 *  take issue with doing classpath resource loading from a static context.
 *
 *@author     sun
 *@created    2003年10月11日
 */
public class PropertyManager {

     private static PropertyManager manager = null;
     private static Object managerLock = new Object();
     private static String propsName = "/credit.properties";
     private Properties properties = null;
     private Object propertiesLock = new Object();
     private String resourceURI;
     private long propTime;

     /**
      *  Returns a Jive property
      *
      *@param  name  the name of the property to return.
      *@return       The property value
      *@returns      the property value specified by name.
      */
     public static String getProperty(String name) {
          if(manager == null) {
               synchronized(managerLock) {
                    if(manager == null) {
                         manager = new PropertyManager(propsName);
                    }
               }
          }
          String props = manager.getProp(name);
          if(props == null)
               return null;
          try {
               props = new String(props.getBytes("ISO-8859-1"));
          } catch(Exception e) {

          }
          return props;
     }

     /**
      *  Sets a Jive property.
      *
      *@param  name   the name of the property being set.
      *@param  value  the value of the property being set.
      */
     public static void setProperty(String name, String value) {
          if(manager == null) {
               synchronized(managerLock) {
                    if(manager == null) {
                         manager = new PropertyManager(propsName);
                    }
               }
          }
          manager.setProp(name, value);
     }

     /**
      *  Returns true if the properties are readable. This method is mainly
      *  valuable at setup time to ensure that the properties file is setup
      *  correctly.
      *
      *@return    Description of the Return Value
      */
     public static boolean propertyFileIsReadable() {
          if(manager == null) {
               synchronized(managerLock) {
                    if(manager == null) {
                         manager = new PropertyManager(propsName);
                    }
               }
          }
          return manager.propFileIsReadable();
     }

     /**
      *  Returns true if the properties are writable. This method is mainly
      *  valuable at setup time to ensure that the properties file is setup
      *  correctly.
      *
      *@return    Description of the Return Value
      */
     public static boolean propertyFileIsWritable() {
          if(manager == null) {
               synchronized(managerLock) {
                    if(manager == null) {
                         manager = new PropertyManager(propsName);
                    }
               }
          }
          return manager.propFileIsWritable();
     }

     /**
      *  Returns true if the jive.properties file exists where the path property
      *  purports that it does.
      *
      *@return    Description of the Return Value
      */
     public static boolean propertyFileExists() {
          if(manager == null) {
               synchronized(managerLock) {
                    if(manager == null) {
                         manager = new PropertyManager(propsName);
                    }
               }
          }
          return manager.propFileExists();
     }

     /**
      *  Singleton access only.
      *
      *@param  resourceURI  Description of the Parameter
      */
     private PropertyManager(String resourceURI) {
          this.resourceURI = resourceURI;
     }

     /**
      *  Gets a Jive property. Jive properties are stored in jive.properties. The
      *  properties file should be accesible from the classpath. Additionally, it
      *  should have a path field that gives the full path to where the file is
      *  located. Getting properties is a fast operation.
      *
      *@param  name  Description of the Parameter
      *@return       The prop value
      */
     public String getProp(String name) {
          //If properties aren't loaded yet. We also need to make this thread
          //safe, so synchronize...
          if(properties == null) {
               synchronized(propertiesLock) {
                    //Need an additional check
                    if(properties == null) {
                         loadProps();
                    }
               }
          }

          long curPropTime = (new File(propsName)).lastModified();
          if ( curPropTime != this.propTime ) {
               loadProps();
          }
          return properties.getProperty(name);
     }

     /**
      *  Sets a Jive property. Because the properties must be saved to disk every
      *  time a property is set, property setting is relatively slow.
      *
      *@param  name   The new prop value
      *@param  value  The new prop value
      */
     public void setProp(String name, String value) {
          //Only one thread should be writing to the file system at once.
          synchronized(propertiesLock) {
               //Create the properties object if necessary.
               if(properties == null) {
                    loadProps();
               }
               properties.setProperty(name, value);
               //Now, save the properties to disk. In order for this to work, the user
               //needs to have set the path field in the properties file. Trim
               //the String to make sure there are no extra spaces.
               String path = properties.getProperty("path").trim();
               OutputStream out = null;
               try {
                    out = new FileOutputStream(path);
                    properties.store(out, "jive.properties -- " + (new Date()));
               } catch(Exception ioe) {
                    System.err.println("There was an error writing jive.properties to " +
                                       path + ". " +
                                       "Ensure that the path exists and that the Jive process has permission " +
                                       "to write to it -- " + ioe);
                    ioe.printStackTrace();
               } finally {
                    try {
                         out.close();
                    } catch(Exception e) {}
                    propTime = (new File(propsName)).lastModified();
               }
          }
     }

     /**
      *  Loads Jive properties from the disk.
      */
     private void loadProps() {
          properties = new Properties();
          InputStream in = null;
          try {
               propTime = (new File(propsName)).lastModified();
               in = getClass().getResourceAsStream(resourceURI);
               properties.load(in);
          } catch(IOException ioe) {
               System.err.println(
                    "Error reading Jive properties in DbForumFactory.loadProperties() " +
                    ioe);
               ioe.printStackTrace();
          } finally {
               try {
                    in.close();
               } catch(Exception e) {}
          }
     }

     /**
      *  Returns true if the properties are readable. This method is mainly
      *  valuable at setup time to ensure that the properties file is setup
      *  correctly.
      *
      *@return    Description of the Return Value
      */
     public boolean propFileIsReadable() {
          try {
               InputStream in = getClass().getResourceAsStream(resourceURI);
               return true;
          } catch(Exception e) {
               return false;
          }
     }

     /**
      *  Returns true if the jive.properties file exists where the path property
      *  purports that it does.
      *
      *@return    Description of the Return Value
      */
     public boolean propFileExists() {
          String path = getProp("path");
          File file = new File(path);
          if(file.isFile()) {
               return true;
          } else {
               return false;
          }
     }

     /**
      *  Returns true if the properties are writable. This method is mainly
      *  valuable at setup time to ensure that the properties file is setup
      *  correctly.
      *
      *@return    Description of the Return Value
      */
     public boolean propFileIsWritable() {
          String path = getProp("path");
          File file = new File(path);
          if(file.isFile()) {
               //See if we can write to the file
               if(file.canWrite()) {
                    return true;
               } else {
                    return false;
               }
          } else {
               return false;
          }
     }

     /**
      *  Gets the allProperties attribute of the PropertyManager class
      *
      *@return    The allProperties value
      */
     public static Properties getAllProperties() {
          if(manager == null) {
               synchronized(managerLock) {
                    if(manager == null) {
                         manager = new PropertyManager(propsName);
                    }
               }
          }
          return manager.properties;
     }

     /**
      *  Gets the intProperty attribute of the PropertyManager class
      *
      *@param  name  Description of the Parameter
      *@return       The intProperty value
      */
     public static int getIntProperty(String name) {
          try {
               int property = Integer.parseInt(getProperty(name));
               return property;
          } catch(Exception ex) {
               ex.printStackTrace();
               return 0;
          }
     }

     /**
      *  Gets the longProperty attribute of the SetupManager class
      *
      *@param  name  Description of the Parameter
      *@return       The longProperty value
      */
     public static long getLongProperty(String name) {
          try {
               long property = Long.parseLong(getProperty(name));
               return property;
          } catch(Exception ex) {
               ex.printStackTrace();
               return 0;
          }
     }

     /**
      *  Gets the floatProperty attribute of the SetupManager class
      *
      *@param  name  Description of the Parameter
      *@return       The floatProperty value
      */
     public static float getFloatProperty(String name) {
          try {
               float property = Float.parseFloat(getProperty(name));
               return property;
          } catch(Exception ex) {
               ex.printStackTrace();
               return 0;
          }
     }

     /**
      *  Gets the doubleProperty attribute of the SetupManager class
      *
      *@param  name  Description of the Parameter
      *@return       The doubleProperty value
      */
     public static double getDoubleProperty(String name) {
          try {
               double property = Double.parseDouble(getProperty(name));
               return property;
          } catch(Exception ex) {
               ex.printStackTrace();
               return 0;
          }
     }

     /**
      *  Description of the Method
      *
      *@param  name  Description of the Parameter
      *@return       Description of the Return Value
      */
     public static Date getDateProperty(String name) {
          if(getCalendarProperty(name) == null) {
               return null;
          } else {
               return getCalendarProperty(name).getTime();
          }
     }

     /**
      *  Gets the calendarProperty attribute of the SetupManager class
      *
      *@param  name  Description of the Parameter
      *@return       The calendarProperty value
      */
     public static Calendar getCalendarProperty(String name) {
          String dateStr = getProperty(name);
          try {
               String[] theDate = dateStr.split("-");
               Calendar c = Calendar.getInstance();
               c.set(Integer.parseInt(theDate[0]),
                     Integer.parseInt(theDate[1]) - 1,
                     Integer.parseInt(theDate[2]));
               return c;
          } catch(Exception ex) {
               return null;
          }
     }

     /**
      *  Gets the property attribute of the PropertyManager class
      *
      *@param  key    Description of the Parameter
      *@param  argus  Description of the Parameter
      *@return        The property value
      */
     public static String getProperty(String key, String[] argus) {
          //将字符串分割成数组，注意字符串末尾是分隔符的情况。
          String src = getProperty(key);
          if(src != null) {
               try {
                    src = new String(src.getBytes("ISO-8859-1"));
               } catch(Exception e) {

               }
          }

          //String src = key;//测试用
          String dot = "%s";
          String sup = ""; //末尾是分隔符时要追加的字符
          if(src.endsWith(dot)) {
               sup = "a";
               src = src + sup;
          }
          String[] tmp = src.split(dot);
          if(sup.equals("a"))tmp[tmp.length - 1] = "";
          //%s个数与argus个数不符，退出
          if(tmp.length - 1 != argus.length) {
               return null;
          }
          //按顺序替换掉%s字符串
          else {
               StringBuffer sbf = new StringBuffer();
               for(int i = 0; i < argus.length; i++) {
                    sbf.append(tmp[i]);
                    sbf.append(argus[i]);
               }
               sbf.append(tmp[tmp.length - 1]);

               return sbf.toString().trim();
          }
     }

     public static void main(String[] args) {
//      //测试split
//      String test="%sdsasl;kgjidsfgoqeir%sjclkjsdifgjeworjioasdf%j ikertkfdgn dfa%s%%s";
//      String[] temp=test.split("%");
//      System.out.println("begin");
//      for(int i=0;i<temp.length;i++){
//        System.out.println("|"+temp[i]);
//      }
//      System.out.println("end");
//      //测试getProperty
//      String[] arr=new String[4];
//      arr[0]="0";
//      arr[1]="1";
//      arr[2]="2";
//      arr[3]="3";
//      System.out.println(getProperty(test,arr));
          System.out.println(getProperty("-101"));
     }

}
