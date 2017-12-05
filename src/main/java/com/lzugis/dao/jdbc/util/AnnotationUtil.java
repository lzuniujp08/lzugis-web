package com.lzugis.dao.jdbc.util;

import com.lzugis.dao.jdbc.annotation.JColumn;
import com.lzugis.dao.jdbc.annotation.JIgnore;
import com.lzugis.dao.jdbc.annotation.JKey;
import com.lzugis.dao.jdbc.annotation.JTable;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class AnnotationUtil {

	public static String table(Class clazz){
		JTable jtableAnnotation = (JTable) clazz.getAnnotation(JTable.class);
		String tableName = null;
		if(jtableAnnotation!=null){
			tableName = jtableAnnotation.value();	
		}
		return tableName;
	}
	public static String table(Object bean){
		if (bean == null)
			throw new IllegalArgumentException("实体对象不能为空！");
		Class clz = bean.getClass();
		return table(clz);
	}
	
	public static Object[] field(Object bean, boolean ispart){
		Class clazz = bean.getClass();
		Field[] fields = clazz.getDeclaredFields();
		List keys = new ArrayList();
		//Map values = new HashMap();
		List vals = new ArrayList();
		try {
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				if(field.getAnnotation(JIgnore.class)!=null){
					continue;
				}				
				field.setAccessible(true);
				
				if(isStaticOrFinal(field)){
					continue;
				}
				
				if(ispart){
					PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(bean,field.getName());
					if(pd == null){
						continue;
					}
					if(field.get(bean)==null){
						continue;
					}
				}
				//String key = getFieldName(field);
				keys.add(getFieldName(field));
				vals.add(field.get(bean));
				//values.put(key, field.get(bean));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Object[] obj = new Object[2];
		obj[0]=keys;
		obj[1]=vals;
		return obj;
	}
	
	public static String getFieldName(Field field){
		String fieldName =field.getName();
		JColumn jColumn = field.getAnnotation(JColumn.class);
		if(jColumn!=null){
			if(jColumn.name()!=null&&!"".equals(jColumn.name().trim())){
				fieldName = jColumn.name();
			}
		}
		return fieldName;
	}
	
	public static Object[] updatefield(Object bean, boolean ispart){
		Class clazz = bean.getClass();
		Field[] fields = clazz.getDeclaredFields();
		List keys = new ArrayList();
		//Map values = new HashMap();
		List ids = new ArrayList();
		try {
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				if(field.getAnnotation(JIgnore.class)!=null){
					continue;
				}				
				field.setAccessible(true);
				
				if(isStaticOrFinal(field)){
					continue;
				}
				
				if(ispart){
					PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(bean,field.getName());
					if(pd == null){
						continue;
					}
					if(field.get(bean)==null){
						continue;
					}
				}
				//String key = getFieldName(field);
				if(field.getAnnotation(JKey.class)!=null){
					ids.add(getFieldName(field));
				}else{
					keys.add(getFieldName(field));
				}
				//values.put(key, field.get(bean));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Object[] obj = new Object[2];
		obj[0]=keys;
		obj[1]=ids;
		return obj;
	}
	
	private static boolean isStaticOrFinal(Field field){
		return Modifier.isFinal(field.getModifiers())|| Modifier.isStatic(field.getModifiers());
	}
}
