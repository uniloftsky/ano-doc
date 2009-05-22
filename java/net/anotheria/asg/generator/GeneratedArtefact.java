package net.anotheria.asg.generator;

/**
 * Base class for artefacts generated by the ASG.
 * @author lrosenberg
 */
public abstract class GeneratedArtefact {
	
	public static final String CRLF = AbstractGenerator.CRLF;

	private String name;
	
	public abstract String createFileContent();
	public abstract String getFileType();
	public abstract String getPath();
	public abstract StringBuilder getBody();
	
	
	public String getName(){
		return name;
	}
	
	public void setName(String aName){
		name = aName;
	}
}
