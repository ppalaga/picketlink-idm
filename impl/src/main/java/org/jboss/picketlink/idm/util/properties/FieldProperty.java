package org.jboss.picketlink.idm.util.properties;

import java.lang.reflect.Field;


public interface FieldProperty<V> extends Property<V> 
{
    Field getAnnotatedElement();
}
