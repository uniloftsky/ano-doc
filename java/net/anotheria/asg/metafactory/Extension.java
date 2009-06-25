package net.anotheria.asg.metafactory;

import net.anotheria.asg.service.ASGService;

public enum Extension {
	NONE,
	LOCAL,
	REMOTE,
	DOMAIN,
	INMEMORY,
	CMS,
	FEDERATION,
	DB,
	
	PERSISTENCE,
	JDBC,
	
	EDITORINTERFACE,
	
	FIXTURE
	;
	
	
	
	public String toExt(){
		return toString().toLowerCase();
	}
	
	public String toName(Class<? extends ASGService> clazz){
		return toName(clazz.getName());
	}

	public String toName(String clazzName){
		return this == NONE ? clazzName: 
			clazzName+"."+toExt();
	}
}
