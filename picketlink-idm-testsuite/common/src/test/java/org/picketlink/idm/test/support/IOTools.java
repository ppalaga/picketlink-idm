/*
* JBoss, a division of Red Hat
* Copyright 2006, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
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

package org.picketlink.idm.test.support;


import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;

/**
 * IO tools.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class IOTools
{

   /** The logger. */
   private static final Logger log = Logger.getLogger(IOTools.class);

   /** . */
   private static final Object[] EMPTY_ARGS = new Object[0];

   /** . */
   private static final Class[] EMPTY_PARAMETER_TYPES = new Class[0];

   /**
    * <p>Attempt to close an object. Null argument value is authorized and no operation will be performed in that
    * use case.</p>
    *
    * <p>It will try to obtain a <code>close()</code> method by reflection and it
    * will be invoked only if the method is public and not static. If the method is called, any <code>Error</code>
    * or <code>RuntimeException</code> will be rethrown, any other kind of throwable will not be rethrown in any form.</p>
    *
    * @param closable the object to close
    */
   public static void safeClose(Object closable)
   {
      if (closable != null)
      {
         try
         {
            Method m = closable.getClass().getMethod("close", EMPTY_PARAMETER_TYPES);
            if (Modifier.isStatic(m.getModifiers()))
            {
               log.warn("close() method on closable object is static");
               return;
            }
            m.invoke(closable, EMPTY_ARGS);
         }
         catch (NoSuchMethodException e)
         {
            log.warn("The closable object does not have a close() method", e);
         }
         catch (IllegalAccessException e)
         {
            log.warn("Cannot access close() method on closable object", e);
         }
         catch (InvocationTargetException e)
         {
            Throwable t = e.getCause();

            //
            if (t instanceof RuntimeException)
            {
               log.error("The close() method threw a runtime exception", t);
               throw (RuntimeException)t;
            }
            else if (t instanceof Error)
            {
               log.error("The close() method threw an error", t);
               throw (Error)t;
            }
            else if (t instanceof Exception)
            {
               log.error("The close() method threw an exception", t);
            }
            else
            {
               log.error("The close() method threw an unexpected throwable", t);
            }
         }
      }
   }

   /**
    * <p>Attempt to close an <code>OutputStream</code>. Null argument value is authorized and no operation will be performed in that
    * use case.</p>
    *
    * @param out the stream to close
    */
   public static void safeClose(OutputStream out)
   {
      if (out != null)
      {
         try
         {
            out.close();
         }
         catch (IOException e)
         {
            log.error("Error while closing outstream", e);
         }
      }
   }

   /**
    * <p>Attempt to close an <code>InputStream</code>. Null argument value is authorized and no operation will be performed in that
    * use case.</p>
    *
    * @param in the stream to close
    */
   public static void safeClose(InputStream in)
   {
      if (in != null)
      {
         try
         {
            in.close();
         }
         catch (IOException e)
         {
            log.error("Error while closing inputstream", e);
         }
      }
   }

   /**
    * <p>Attempt to close an <code>Reader</code>. Null argument value is authorized and no operation will be performed in that
    * use case.</p>
    *
    * @param reader the stream to close
    */
   public static void safeClose(Reader reader)
   {
      if (reader != null)
      {
         try
         {
            reader.close();
         }
         catch (IOException e)
         {
            log.error("Error while closing reader", e);
         }
      }
   }

   /**
    * <p>Attempt to close an <code>Writer</code>. Null argument value is authorized and no operation will be performed in that
    * use case.</p>
    *
    * @param writer the stream to close
    */
   public static void safeClose(Writer writer)
   {
      if (writer != null)
      {
         try
         {
            writer.close();
         }
         catch (IOException e)
         {
            log.error("Error while closing writer", e);
         }
      }
   }

   /**
    * Check that the provided input stream is buffered. If the argument is already an instance of <code>BufferedInputStream</code>
    * no operation will be performed, otherwise a instance of <code>BufferedInputStream</code> will be created and returned.
    *
    * If the provided argument is null, the null value is returned.
    *
    * @param in the stream
    * @return a buffered wrapper
    */
   public static BufferedInputStream safeBufferedWrapper(InputStream in)
   {
      if (in != null)
      {
         if (in instanceof BufferedInputStream)
         {
            return (BufferedInputStream)in;
         }
         else
         {
            return new BufferedInputStream(in);
         }
      }
      else
      {
         return null;
      }
   }

   /**
    * Check that the provided output stream is buffered. If the argument is already an instance of <code>BufferedOutputStream</code>
    * no operation will be performed, otherwise a instance of <code>BufferedOutputStream</code> will be created and returned.
    *
    * If the provided argument is null, the null value is returned.
    *
    * @param out the stream
    * @return a buffered wrapper
    */
   public static BufferedOutputStream safeBufferedWrapper(OutputStream out)
   {
      if (out != null)
      {
         if (out instanceof BufferedOutputStream)
         {
            return (BufferedOutputStream)out;
         }
         else
         {
            return new BufferedOutputStream(out);
         }
      }
      else
      {
         return null;
      }
   }
}
