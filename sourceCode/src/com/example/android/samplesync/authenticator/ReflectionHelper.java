package com.example.android.samplesync.authenticator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;



public class ReflectionHelper {

    private static final String TAG ="loader";
	
	public static Field getField(Class cls, String field) {
		try {
			Field fld = cls.getDeclaredField(field);
			if (fld != null)
				fld.setAccessible(true);
			return fld;
		} catch (Exception e) {
			//if(DEBUG)
			//	Log.e(TAG,e.getMessage(), e);
			return null;
		}
	}
	
	public static Field getField(String clzz,String field){
	    try{
	        Class clazz = Class.forName(clzz);
	        if(clazz != null){
	            return getField(clazz,field);
	        }
	    }catch(Exception e){
	        
	    }
	    return null;
	}
	
	public static int getStaticInt(String clzz,String field,int defaultVal){
	    Field fld = getField(clzz,field);
	    if(fld != null){
	        Object obj = getObject(fld,null);
	        if(obj != null){
	            try{
	                return (Integer)obj;
	            }catch(Exception e){
	                
	            }
	        }
	    }
	    return defaultVal;
	}

	public static Object getObject(Field fld, Object instance) {
		try {
			Object obj = fld.get(instance);
			return obj;
		} catch (Exception e) {
			//if(DEBUG)
			//	Log.e(TAG,e.getMessage(), e);
			return null;
		}
	}
	
	
	public static Class getClass(String name){
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {

				Log.e(TAG,e.getMessage(), e);
			return null;
		}
	}
	
	public static Object invokeStaticMethod(Class cls,Class[] type,String methodname,Object[] args){
		try{
			Method method = cls.getDeclaredMethod(methodname, type);
			if(method == null)
				return null;
			method.setAccessible(true);
			return method.invoke(null, args);
		}catch(Exception e){

				Log.e(TAG,"", e);
			return null;
		}
	}
	
	public static Object invokeStaticMethod(String clsName,Class[] type,String methodname,Object[] args){
        Class clzz = null;
        try{
            clzz = Class.forName(clsName);
        }catch(Exception e){
            clzz = null;
        }
        if(clzz == null)
            return null;
	    return invokeStaticMethod(clzz ,type ,methodname ,args);
    }
	
	public static Object invokeNonStaticMethod(Object obj,Class[] type,String methodname,Object[] args){
        try{
            Method method = obj.getClass().getDeclaredMethod(methodname, type);
            if(method == null)
                return null;
            method.setAccessible(true);
            return method.invoke(obj, args);
        }catch(Exception e){

                Log.e(TAG,e.getMessage(), e);
            return null;
        }
    }
	
	
	/**
	 * 因为在4.1之后，加上了userId参数，但这个参数其实对我们意义不大，所以这里如果检测到参数个数不匹配，就自动添加个0
	 * @param obj
	 * @param type
	 * @param methodname
	 * @param args
	 * @return
	 */
    public static Object invokeAdaptiveNonStaticMethod(Object obj, String methodname, Object[] args) {
        if(methodname == null)
            return null;
        Method method = null;
        try {
            Method[] methods = obj.getClass().getDeclaredMethods();
            int len = methods.length;
            for (int i =0 ;i<len;i++){
                if(methodname.equals(methods[i].getName())){
                    method = methods[i];
                    break;
                }
            }
            Object[] objs = null;
            
            int paramLen = method.getParameterTypes().length;
            if(paramLen == args.length){
                objs = args;
            }else{
                objs = new Object[paramLen];
                objs[objs.length - 1 ] = 0;
                for(int i = 0 ; i< paramLen;i++){
                    objs[i] = args[i];
                }
            }
            method.setAccessible(true);
            Object res = method.invoke(obj, objs);
            
            if(res != null)
                return res;
        } catch (Exception e1) {

                Log.e(TAG,"", e1);
            return null;
        }
        return null;
    }
    
    public static Object invokeAdaptiveStaticMethod(Class clzz, String methodname, Object[] args) {
        if(methodname == null)
            return null;
        Method method = null;
        try {
            Method[] methods = clzz.getDeclaredMethods();
            int len = methods.length;
            for (int i =0 ;i<len;i++){
                if(methodname.equals(methods[i].getName())){
                    method = methods[i];
                    break;
                }
            }
            Object[] objs = null;
            
            int paramLen = method.getParameterTypes().length;
            if(paramLen == args.length){
                objs = args;
            }else{
                objs = new Object[paramLen];
                objs[objs.length - 1 ] = 0;
                for(int i = 0 ; i< args.length;i++){
                    objs[i] = args[i];
                }
            }
            method.setAccessible(true);
            Object res = method.invoke(null, objs);
            
            if(res != null)
                return res;
        } catch (Exception e1) {

                Log.e(TAG,"", e1);
            return null;
        }
        return null;
    }
}
