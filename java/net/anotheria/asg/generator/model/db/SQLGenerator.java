package net.anotheria.asg.generator.model.db;

import java.util.ArrayList;
import java.util.List;

import net.anotheria.asg.generator.AbstractGenerator;
import net.anotheria.asg.generator.Context;
import net.anotheria.asg.generator.FileEntry;
import net.anotheria.asg.generator.GeneratedSQLFile;
import net.anotheria.asg.generator.GenerationJobManager;
import net.anotheria.asg.generator.GeneratorDataRegistry;
import net.anotheria.asg.generator.IGenerateable;
import net.anotheria.asg.generator.IGenerator;
import net.anotheria.asg.generator.meta.MetaDocument;
import net.anotheria.asg.generator.meta.MetaListProperty;
import net.anotheria.asg.generator.meta.MetaModule;
import net.anotheria.asg.generator.meta.MetaProperty;
import net.anotheria.asg.generator.meta.StorageType;

public class SQLGenerator extends AbstractGenerator implements IGenerator{
	
	public List<FileEntry> generate(List<MetaModule>  modules, Context context){
		ArrayList<FileEntry> ret = new ArrayList<FileEntry>();
		ArrayList<MetaDocument> documents = new ArrayList<MetaDocument>();
		for (MetaModule m : modules){
			if (m.getStorageType().equals(StorageType.DB)){
				ret.addAll(generate(m, context));
				documents.addAll(m.getDocuments());
			}
		}

		if (documents.size()>0)
			ret.addAll(generateAllScripts(documents));
		
		return ret;
	}
	
	private List<FileEntry> generateAllScripts(List<MetaDocument> documents){
		List<FileEntry> entries = new ArrayList<FileEntry>();
		
		GeneratedSQLFile allCreate = new GeneratedSQLFile("create_all");
		GeneratedSQLFile allDelete = new GeneratedSQLFile("delete_all");

		MetaProperty dao_created = new MetaProperty("dao_created", "long");
	    MetaProperty dao_updated = new MetaProperty("dao_updated", "long");

		String tableNames = "";
		for (MetaDocument doc : documents){
			GenerationJobManager.getCurrentJob().setBuilder(allCreate.getBody());
			generateSQLCreate(doc, dao_created, dao_updated);
			appendEmptyline();
			
			if (tableNames.length()>0)
				tableNames += ",";
			tableNames += getSQLTableName(doc);
		
			GenerationJobManager.getCurrentJob().setBuilder(allDelete.getBody());
			generateSQLDelete(doc);
			appendEmptyline();
		}
		
		
		GenerationJobManager.getCurrentJob().setBuilder(allCreate.getBody());
		appendString("GRANT ALL ON "+tableNames+" TO "+GeneratorDataRegistry.getInstance().getContext().getOwner()+" ; ");
		
		entries.add(new FileEntry(allCreate));
		entries.add(new FileEntry(allDelete));
		return entries;
	}
	
	public List<FileEntry> generate(IGenerateable gmodule, Context context){
		
		MetaModule mod = (MetaModule)gmodule;
		
		List<FileEntry> ret = new ArrayList<FileEntry>();
		
		List<MetaDocument> documents = mod.getDocuments();
		for (MetaDocument d: documents){
			ret.add(new FileEntry(generateDocumentCreate(d)));
		}
		
		return ret;
	}
	
	public String getCreateScriptName(MetaDocument doc){
		return "create_"+doc.getParentModule().getName().toLowerCase()+"_"+doc.getName().toLowerCase();
	}
	
	private GeneratedSQLFile generateDocumentCreate(MetaDocument doc){
		GeneratedSQLFile file = new GeneratedSQLFile(getCreateScriptName(doc));
		startNewJob(file);
		
		
	    MetaProperty dao_created = new MetaProperty("dao_created", "long");
	    MetaProperty dao_updated = new MetaProperty("dao_updated", "long");

		generateSQLCreate(doc, dao_created, dao_updated);
		
		return file;
	}
	
	private void generateSQLDelete(MetaDocument doc){
		appendString("DROP TABLE "+getSQLTableName(doc)+";");
	}
	
	private void generateSQLCreate(MetaDocument doc, MetaProperty... additionalProps){
		appendString("CREATE TABLE "+getSQLTableName(doc)+"(");
		increaseIdent();
		appendString("id int8 PRIMARY KEY,");
		for (int i=0; i<doc.getProperties().size(); i++){
			appendString(getSQLPropertyDefinition(doc.getProperties().get(i))+",");
		}
		for (int i=0; i<doc.getLinks().size(); i++){
			appendString(getSQLPropertyDefinition(doc.getLinks().get(i))+",");
		}
		for (int i=0; i<additionalProps.length-1; i++)
			appendString(getSQLPropertyDefinition(additionalProps[i])+",");
		appendString(getSQLPropertyDefinition(additionalProps[additionalProps.length-1]));
		
		decreaseIdent();
		appendString(");");
	}
	
	private String getSQLPropertyDefinition(MetaProperty p){
		return getAttributeName(p)+" "+getSQLPropertyType(p);
	}
	
	/**
	 * This method maps MetaProperties Types to SQL DataTypes.
	 * @param p
	 * @return
	 */
	private String getSQLPropertyType(MetaProperty p){
		if (p.getType().equals("string"))
			return "varchar";
		if (p.getType().equals("text"))
			return "varchar";
		if (p.getType().equals("long"))
			return "int8";
		if (p.getType().equals("int"))
			return "int";
		if (p.getType().equals("double"))
			return "double precision";
		if (p.getType().equals("float"))
			return "float4";
		if (p.getType().equals("boolean"))
			return "boolean";
		if (p instanceof MetaListProperty)
			return getSQLPropertyType(((MetaListProperty)p).getContainedProperty()) + "[]";
		return "UNKNOWN!";
	}

	private String getSQLTableName(MetaDocument doc){
		return doc.getName().toLowerCase();
	}
	
	private String getAttributeName(MetaProperty p){
		return p.getName().toLowerCase();
	}

}
