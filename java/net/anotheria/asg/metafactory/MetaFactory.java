package net.anotheria.asg.metafactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.anotheria.asg.service.ASGService;

public class MetaFactory {
	
	private static final Map<String, ASGService> instances = new HashMap<String, ASGService>();
	
	private static Map<String, String> aliases = new HashMap<String, String>();
	
	private static Map<String, Class<? extends ServiceFactory<? extends ASGService>>> factoryClasses = new HashMap<String, Class<? extends ServiceFactory<? extends ASGService>>>();
	private static Map<String, ServiceFactory<? extends ASGService>> factories = new HashMap<String, ServiceFactory<? extends ASGService>>();
	
	
	
	
	public static <T extends ASGService> T create(Class<T> pattern, Extension extension)throws MetaFactoryException{
		return pattern.cast(create(extension.toName(pattern)));
	}

	public static <T extends ASGService> T create(Class<T> pattern)throws MetaFactoryException{
		return pattern.cast(create(pattern, Extension.NONE));
	}
///*
	private static <T extends ASGService> T create(String name) throws MetaFactoryException{
		
		ServiceFactory<T> factory = (ServiceFactory<T>)factories.get(name);
		if (factory!=null)
			return factory.create();
		
		Class<? extends ServiceFactory<T>> clazz = (Class<? extends ServiceFactory<T>>)factoryClasses.get(name);
		if (clazz==null)
			throw new FactoryNotFoundException(name); 
		
		synchronized (factories) {
			factory = (ServiceFactory<T>) factories.get(name);
			if (factory==null){
				try{
					factory = clazz.newInstance();
					factories.put(name, factory);
				}catch(IllegalAccessException e){
					throw new FactoryInstantiationError(clazz, name, e.getMessage());
				}catch(InstantiationException e){
					throw new FactoryInstantiationError(clazz, name, e.getMessage());
				}
			}
			
		}
		return factory.create();
	}
	//*/
	
	public static <T extends ASGService> T get(Class<T> pattern) throws MetaFactoryException{
		return get(pattern, Extension.NONE);
	}

	public static <T extends ASGService> T get(Class<T> pattern, Extension extension) throws MetaFactoryException{
		
		out("get called, pattern: "+pattern+", extension: "+extension);
		
		if (extension==null)
			extension = Extension.NONE;
		String name = extension.toName(pattern);
		out("name is "+name);
		
		name = resolveAlias(name);
		out("resolved alias to "+name);
		
		T instance = pattern.cast(instances.get(name));
		
		out("instance of "+name + " is: "+instance);
		
		if (instance!=null)
			return instance;
		
		synchronized (instances) {
			//double check
			//@SuppressWarnings("unchecked")
			instance = pattern.cast(instances.get(name));
			if (instance==null){
				
				out("creating new instance of "+name);
				
				instance = pattern.cast(create(name));
				
				out("created new instance of "+name+" ---> "+instance);
				
				instances.put(name, instance);
			}
			
		}
		
		return instance;
	}
	
	public static final String resolveAlias(String name){
		String alias = aliases.get(name);
		return alias == null ? name : resolveAlias(alias);
	}
	
	public static final String resolveAlias(Class<? extends ASGService> clazz){
		return resolveAlias(clazz.getName());
	}

	public static final void addAlias(String name, String alias){
		aliases.put(alias, name);
	}
	
	public static <T extends ASGService> void addAlias(Class<T> pattern, Extension nameExtension){
		addAlias(pattern, nameExtension, null);
	}
	
	public static <T extends ASGService> void addAlias(Class<T> pattern, Extension nameExt, Extension aliasExtension){
		if (nameExt==null)
			nameExt = Extension.NONE;
		if (aliasExtension==null)
			aliasExtension = Extension.NONE;
		addAlias(nameExt.toName(pattern), aliasExtension.toName(pattern));
	}
	
	public static <T extends ASGService> void addFactoryClass(Class<T> service, Extension extension, Class<? extends ServiceFactory<T>> factoryClass){
		addFactoryClass(extension.toName(service), factoryClass);
	}
	
//	public static <T extends ASGService, F extends ServiceFactory<T>> void addFactoryClass(String serviceClassName, Extension extension, Class<F> factoryClass){
//		addFactoryClass(extension.toName(serviceClassName), factoryClass);
//	}

	public static <T extends ASGService>  void addFactoryClass(String name, Class<? extends ServiceFactory<T>> factoryClass){
		factoryClasses.put(name, factoryClass);
	}
	
	private static void out(Object o){
		
		//System.out.println("[MetaFactory] "+o);
	}
	
	public static void debugDumpAliasMap(){
		Set<String> keys = aliases.keySet();
		for (String key : keys){
			System.out.println(key + " = "+aliases.get(key));
		}
	}
}
