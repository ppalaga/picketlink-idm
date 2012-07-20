/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.picketlink.idm.test.support.hibernate;

import org.picketlink.idm.test.support.IOTools;
import org.picketlink.idm.test.support.XMLTools;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Template
 *
 * @author <a href="mailto:bdawidow@redhat.com">Boleslaw Dawidowicz</a>
 */
public class DataSourceConfig
{

   /** . */
   private String name;

   /** . */
   private String connectionURL;

   /** . */
   private String driverClass;

   /** . */
   private String userName;

   /** . */
   private String password;

   public DataSourceConfig(String name, String connectionURL, String driverClass, String userName, String password)
   {
      this.name = name;
      this.connectionURL = connectionURL;
      this.driverClass = driverClass;
      this.userName = userName;
      this.password = password;
   }

   public DataSourceConfig()
   {

   }

   public String getName()
   {
      return name;
   }

   public String getConnectionURL()
   {
      return connectionURL;
   }

   public String getDriverClass()
   {
      return driverClass;
   }

   public String getUserName()
   {
      return userName;
   }

   public String getPassword()
   {
      return password;
   }

   public String toString()
   {
      return "Datasource[" + name + "]";
   }


   public void setName(String name)
   {
      this.name = name;
   }

   public void setConnectionURL(String connectionURL)
   {
      this.connectionURL = connectionURL;
   }

   public void setDriverClass(String driverClass)
   {
      this.driverClass = driverClass;
   }

   public void setUserName(String userName)
   {
      this.userName = userName;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }

//      public static MultiValuedTestParameterValue fromXML2(URL url) throws Exception
//      {
//         Config[] configs = fromXML(url);
//         List list = Arrays.asList(configs);
//         return new MultiValuedTestParameterValue(list);
//      }

   public static DataSourceConfig[] fromXML(URL url) throws Exception
   {
      ArrayList configs = new ArrayList();
      InputStream in = null;
      try
      {
         in = IOTools.safeBufferedWrapper(url.openStream());
         Document doc = XMLTools.getDocumentBuilderFactory().newDocumentBuilder().parse(in);
         for (Iterator i = XMLTools.getChildrenIterator(doc.getDocumentElement(), "datasource"); i.hasNext();)
         {
            Element childElt = (Element)i.next();

            // Parse the datasource name, taking in account the deprecated display-name element
            Element nameElt = XMLTools.getUniqueChild(childElt, "datasource-name", false);
            if (nameElt == null)
            {
               System.out.println("XML element datasource-name is not present, trying deprecated display-name element instead, you should fix your datasources.xml");
               nameElt = XMLTools.getUniqueChild(childElt, "display-name", true);
            }

            // Parse the rest of the configuration
            Element connectionURLElt = XMLTools.getUniqueChild(childElt, "connection-url", true);
            Element driverClassElt = XMLTools.getUniqueChild(childElt, "driver-class", true);
            Element userNameElt = XMLTools.getUniqueChild(childElt, "user-name", true);
            Element passwordElt = XMLTools.getUniqueChild(childElt, "password", true);
            String name = XMLTools.asString(nameElt);
            String connectionURL = XMLTools.asString(connectionURLElt);
            String driverClass = XMLTools.asString(driverClassElt);
            String userName = XMLTools.asString(userNameElt);
            String password = XMLTools.asString(passwordElt);
            DataSourceConfig dsCfg = new DataSourceConfig(
               name,
               connectionURL,
               driverClass,
               userName,
               password);
            configs.add(dsCfg);
         }
         return (DataSourceConfig[])configs.toArray(new DataSourceConfig[configs.size()]);
      }
      finally
      {
         IOTools.safeClose(in);
      }
   }

   public static DataSourceConfig obtainConfig(String datasources, String dataSourceName) throws Exception
   {
      URL url = Thread.currentThread().getContextClassLoader().getResource(datasources);

      DataSourceConfig[] configs = fromXML(url);

      for (DataSourceConfig config : configs)
      {
         if (config.getName().equals(dataSourceName))
         {
            return config;
         }
      }

      throw new IllegalStateException("Could not obtain Config for {datasourceName:datasources} - {" + dataSourceName + ":" + datasources + "}" );

   }
}