/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/

package org.picketlink.idm.test.support.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.TransactionException;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.picketlink.idm.test.support.IOTools;
import org.picketlink.idm.test.support.XMLTools;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.jboss.logging.Logger;

import javax.transaction.Synchronization;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Properties;
import java.io.InputStream;
import java.net.URL;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 7821 $
 */
public class HibernateSupport
{

   /** . */
   protected Logger log = Logger.getLogger(HibernateSupport.class);

   /** . */
   protected String jndiName;

   /** . */
   protected Collection mappings;

   /** . */
   protected Config config;

   /** . */
   protected SessionFactory factory;

   /** . */
   protected Session session;

   /** . */
   protected Configuration cfg;

   /** . */
   protected DataSourceConfig dsc;

   /** . */
   //protected Settings settings;

   public Collection getMappings()
   {
      return mappings;
   }

   public void setMappings(Collection mappings)
   {
      this.mappings = mappings;
   }

   public Config getConfig()
   {
      return config;
   }

   public void setConfig(Config config)
   {
      this.config = config;
   }

   public DataSourceConfig getDataSourceConfig()
   {
      return dsc;
   }

   public void setDataSourceConfig(DataSourceConfig dsc)
   {
      this.dsc = dsc;
   }

   public SessionFactory getSessionFactory()
   {
      return factory;
   }

   public String getJNDIName()
   {
      return jndiName;
   }

   public void setJNDIName(String jndiName)
   {
      this.jndiName = jndiName;

      //
      if (jndiName == null)
      {
         log = org.jboss.logging.Logger.getLogger(HibernateSupport.class);
      }
      else
      {
         log = org.jboss.logging.Logger.getLogger(jndiName);
      }
   }

   protected void createConfiguration()
   {
      Configuration cfg = new Configuration();

      //
      for (Iterator i = mappings.iterator(); i.hasNext();)
      {
         String mapping = (String)i.next();
         log.debug("Adding mapping " + mapping);
         cfg.addResource(mapping, Thread.currentThread().getContextClassLoader());
      }

      //
      Properties props = new Properties();
      for (Iterator i = config.properties.entrySet().iterator();i.hasNext();)
      {
         Map.Entry entry = (Map.Entry)i.next();
         String key = (String)entry.getKey();
         String value = (String)entry.getValue();
         log.debug("Adding property " + key + " = " + value);
         cfg.setProperty(key, value);
      }
      cfg.addProperties(props);

      // todo : make this configurable somehow
      //cfg.setProperty("hibernate.connection.datasource", "java:/DefaultDS");

      //
      if (jndiName != null)
      {
         log.debug("Setting jndi name to " + jndiName);
         cfg.setProperty("hibernate.session_factory_name", jndiName);
      }

      if (dsc != null)
      {
         cfg.setProperty("hibernate.connection.url", dsc.getConnectionURL());
         cfg.setProperty("hibernate.connection.driver_class", dsc.getDriverClass());
         cfg.setProperty("hibernate.connection.username", dsc.getUserName());
         cfg.setProperty("hibernate.connection.password", dsc.getPassword());
      }

      //
      //this.settings = cfg.buildSettings();
      this.cfg = cfg;
   }

   protected void createSessionFactory()
   {
      factory = cfg.buildSessionFactory();
   }

   protected void createSchema()
   {
      SchemaExport export = new SchemaExport(cfg);
      export.create(false, true);
   }

   protected void destroySchema()
   {
      SchemaExport export = new SchemaExport(cfg);
      export.drop(false, true);
   }

   protected void destroySessionFactory()
   {
      factory.close();
   }

   protected void destroyConfiguration()
   {
      config = null;
   }

   public void create() throws Exception
   {
   }

   public void start() throws Exception
   {
      createConfiguration();
      createSessionFactory();
      createSchema();
   }

   public void stop() throws Exception
   {
      try
      {
         Session currentSession = getCurrentSession();

         //
         if (currentSession != null)
         {
            // Commit any pending transaction
            if (commitTransaction())
            {
               System.out.println("Warning : commited a transaction for the test case");
            }
         }
      }
      catch (HibernateException e)
      {
         e.printStackTrace();
      }

      //
      destroySchema();
      destroySessionFactory();
      destroyConfiguration();
   }

   public void destroy()
   {
   }

   /** Try to commit the transaction and return true if the commit was succesful */
   public boolean commitTransaction()
   {
      Session currentSession = getCurrentSession();
      if (currentSession == null)
      {
         throw new IllegalStateException("No current session");
      }
      if (currentSession.getTransaction() == null)
      {
         throw new IllegalStateException("No current transaction");
      }
      try
      {
         Transaction tx = currentSession.getTransaction();
         if (tx.isActive())
         {
            tx.commit();
            return true;
         }
         else
         {
            return false;
         }
      }
      catch (TransactionException e)
      {
         e.printStackTrace();
         return false;
      }
   }

   /** Rollback the transaction and return true if the rollback was succesful */
   public void rollbackTransaction()
   {
      Session currentSession = getCurrentSession();
      if (currentSession == null)
      {
         throw new IllegalStateException("No current session");
      }
      if (currentSession.getTransaction() == null)
      {
         throw new IllegalStateException("No current transaction");
      }
      try
      {
         Transaction tx = currentSession.getTransaction();
         tx.rollback();
      }
      catch (TransactionException e)
      {
      }
   }

   public Session getCurrentSession()
   {
      try
      {
         if (session != null)
         {
            return session;
         }

         //
         session = factory.getCurrentSession();
         Transaction tx = session.getTransaction();
         tx.registerSynchronization(new Synchronization()
         {
            public void beforeCompletion()
            {
            }

            public void afterCompletion(int i)
            {
               session = null;
            }
         });

         return session;
      }
      catch (HibernateException e)
      {
         return null;
      }
   }

   public Session openSession()
   {
      if (getCurrentSession() != null)
      {
         throw new IllegalStateException("Cannot have more than one active session");
      }
      session = factory.openSession();
      Transaction tx = session.beginTransaction();
      tx.registerSynchronization(new Synchronization()
      {
         public void beforeCompletion()
         {
         }

         public void afterCompletion(int i)
         {
            session = null;
         }
      });
      return session;
   }

   public static class Config
   {

      /** . */
      private String name;

      /** . */
      private Map properties;

      public Config(String name, Map properties)
      {
         this.name = name;
         this.properties = properties;
      }

      public String getName()
      {
         return name;
      }

      public Map getProperties()
      {
         return properties;
      }
   }

   /** . */
   private static Map configs;

   public static Map fromXML(URL url) throws Exception
   {
      Map configs = new LinkedHashMap();
      InputStream in = null;
      try
      {
         in = IOTools.safeBufferedWrapper(url.openStream());
         Document doc = XMLTools.getDocumentBuilderFactory().newDocumentBuilder().parse(in);
         for (Iterator i = XMLTools.getChildrenIterator(doc.getDocumentElement(), "configuration"); i.hasNext();)
         {
            Element childElt = (Element)i.next();

            //
            Element configurationNameElt = XMLTools.getUniqueChild(childElt, "configuration-name", true);
            String configurationName = XMLTools.asString(configurationNameElt);

            //
            Map properties = new HashMap();
            Element propertiesElt = XMLTools.getUniqueChild(childElt, "properties", false);
            if (propertiesElt != null)
            {
               for (Iterator j = XMLTools.getChildrenIterator(propertiesElt, "property");j.hasNext();)
               {
                  Element propertyElt = (Element)j.next();
                  Element nameElt = XMLTools.getUniqueChild(propertyElt, "name", true);
                  Element valueElt = XMLTools.getUniqueChild(propertyElt, "value", true);
                  String name = XMLTools.asString(nameElt);
                  String value = XMLTools.asString(valueElt);
                  properties.put(name, value);
               }
            }

            //
            Config config = new Config(configurationName, properties);
            configs.put(configurationName, config);
         }
         return configs;
      }
      finally
      {
         IOTools.safeClose(in);
      }
   }

   public synchronized static Config getConfig(String name, String hibernates) throws Exception
   {
      if (configs == null)
      {
         URL url = Thread.currentThread().getContextClassLoader().getResource(hibernates);
         configs = fromXML(url);

         // Remove and merge default with all
         Config defaultCfg = (Config)configs.remove("default");

         //
         for (Iterator i = configs.values().iterator();i.hasNext();)
         {
            Config cfg = (Config)i.next();
            if ("default".equals(cfg.getName()) == false)
            {
               Map tmp = new HashMap(defaultCfg.properties);
               tmp.putAll(cfg.properties);
               cfg.properties = tmp;
            }
         }

      }

      //
      return (Config)configs.get(name);
   }

   public synchronized static Config getConfig(String name) throws Exception
   {
      return getConfig(name, "hibernates.xml");
   }
}
