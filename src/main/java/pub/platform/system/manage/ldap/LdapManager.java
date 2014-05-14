package pub.platform.system.manage.ldap;

import  java.util.*;
import  javax.naming.*;
import  javax.naming.directory.*;



public class LdapManager {
     public LdapManager() {
     }

     public DirContext init()
    {
         Hashtable  env  =  new  Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY,Env.INITCTX);
        env.put(Context.PROVIDER_URL,Env.MY_SERVICE);
        env.put(Context.SECURITY_AUTHENTICATION,  "simple");
        env.put(Context.SECURITY_PRINCIPAL,Env.MGR_DN);
        env.put(Context.SECURITY_CREDENTIALS,Env.MGR_PW);
        env.put(InitialContext.REFERRAL,"ignore");
        DirContext ctx = null;
        try
        {
              ctx  =  new  InitialDirContext(env);
              System.out.println("认证成功!");
              return ctx;
        }
        catch(AuthenticationException  e)
        {
              System.out.println("认证失败!");
              return null;
        }
        catch(Exception  e)
        {
              System.out.println("认证错误!");
              e.printStackTrace();
              return null;
        }
    }

    /**
      *
      * @exception javax.naming.NameAlreadyBoundException
      * @exception javax.naming.NamingException
      * @param ctx DirContext
      * @return void
      */
     public void Query(DirContext ctx)
     {
          try
          {
               SearchControls  constraints  =  new  SearchControls();
               constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
               NamingEnumeration  en  =  ctx.search(Env.MY_SEARCHBASE, Env.MY_FILTER, constraints);
               NamingEnumeration  ae=null;
               NamingEnumeration vals=null;



               while  (en!= null && en.hasMoreElements())
               {


                      Object  obj  =  en.nextElement();
                      if(obj  instanceof  SearchResult)
                      {
                              SearchResult  si  =  (SearchResult)  obj;
                              System.out.println("name:  "  +  si.getName());
                                Attributes  attrs  =  si.getAttributes();
                                if  (attrs  ==  null)
                                {
                                        System.out.println("No  a" +"ttributes");
                                }
                                else
                                {
                                        for  (ae  =  attrs.getAll();  ae.hasMoreElements();)
                                        {
                                                Attribute  attr  =  (Attribute) ae.next();
                                                String  attrId  =  attr.getID();

                                                for  (vals  = attr.getAll();vals.hasMoreElements();)
                                                {
                                                        System.out.print(attrId  +  ":  ");
                                                      Object  o  =  vals.nextElement();
                                                      if(o  instanceof  byte[])
                                                              System.out.println(new  String((byte[])o));
                                                      else
                                                              System.out.println(o);
                                                }
                                        }
                                }
                      }
                      else
                      {
                              System.out.println(obj);
                      }
                      System.out.println("****************************************************************************************");
               }
          }
          catch(NameAlreadyBoundException e)
          {
               System.out.println("Name is already be banding:"+e);
          }
          catch(NamingException e)
          {
               System.out.println("Name is error:"+e);
          }
     }

     public void Add(DirContext ctx,String newUserName,String newUserPass)
     {
          try
          {
               BasicAttributes  attrs  =  new  BasicAttributes();
               BasicAttribute  objclassSet  =  new  BasicAttribute("objectclass");
               objclassSet.add("person");
               objclassSet.add("top");
               objclassSet.add("organizationalPerson");
               objclassSet.add("inetOrgPerson");
               attrs.put(objclassSet);
               attrs.put("sn",  newUserName);
               attrs.put("uid",  newUserName);
               attrs.put("userPassword",newUserPass);
               ctx.createSubcontext("cn=" + newUserName+",cn=users,dc=sohipc,dc=com", attrs);
          }
          catch(NameAlreadyBoundException e)
          {
               System.out.println("Name is already be banding:"+e);
          }
          catch(NamingException e)
          {
               System.out.println("Name is error:"+e);
          }
     }

     public void Delete(DirContext ctx,String oldUserName)
     {
          try
          {
               ctx.destroySubcontext("cn=" + oldUserName+",cn=users,dc=sohipc,dc=com");
          }
          catch(NameAlreadyBoundException e)
          {
               System.out.println("Name is already be banding:"+e);
          }
          catch(NamingException e)
          {
               System.out.println("Name is error:"+e);
          }
     }

     public void Modify(DirContext ctx,String oldUserName,String newUserPass)
     {
          try
          {
               ModificationItem  modificationItem[]  =  new  ModificationItem[1];
               //modificationItem[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute("displayName", newDisplayName));
               //ctx.modifyAttributes("cn="  +  account,  modificationItem);
               modificationItem[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute("userPassword", newUserPass));
               ctx.modifyAttributes("cn=" + oldUserName+",cn=users,dc=sohipc,dc=com",  modificationItem);
          }
          catch(NameAlreadyBoundException e)
          {
               System.out.println("Name is already be banding:"+e);
          }
          catch(NamingException e)
          {
               System.out.println("Name is error:"+e);
          }
     }

     public void Destory(DirContext ctx)
     {

        if(ctx  !=  null)
        {
              try
              {
                    ctx.close();
              }
              catch  (NamingException  e)
              {
                    e.printStackTrace();
              }
        }
     }


     public static void main(String[] args)
     {
          LdapManager mgr=new LdapManager();
          DirContext ctx = mgr.init();
          //mgr.Add(ctx,"sihet","751205");
          //mgr.Modify(ctx,"sohi","751205");
          mgr.Query(ctx);
          mgr.Destory(ctx);
     }




}

