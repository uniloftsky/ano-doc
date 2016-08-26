package net.anotheria.asg.generator;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class that generates comments.
 * @author another
 */
public class CommentGenerator extends AbstractGenerator{
	/**
	 * Copyright string in the comments.
	 */
	public static final String COPYRIGHT = "Copyright (C) 2005 - 2010 Anotheria.net, www.anotheria.net";
	public static final String RIGHTS    = "All Rights Reserved.";
	
	/**
	 * Constant for comments in generated code.
	 */
	public static final String NOTICE_1 = "Don't edit this code, if you aren't sure";
	/**
	 * Constant for comments in generated code.
	 */
	public static final String NOTICE_2 = "that you do exactly know what you are doing!";
	/**
	 * Constant for comments in generated code.
	 */
	public static final String NOTICE_3 = "It's better to invest time in the generator, as into the generated code.";

	/**
	 * Constant for comments in generated code.
	 */
	public static final String LINE_PRE = "*** "; 
	/**
	 * Constant for comments in generated code.
	 */
	public static final String LINE_POST = " ***";
	/**
	 * Constant for comments in generated code.
	 */
	public static final int LINES_ADD_LENGTH = LINE_POST.length()+LINE_PRE.length();
	/**
	 * Constant for comments in generated code.
	 */
	public static final String SEPARATOR = "*";
	
	/**
	 * Constant for comments in generated code.
	 */
	public static final String COMM_START = "/**";
	/**
	 * Constant for comments in generated code.
	 */
	public static final String COMM_END   = " */";
	
	/**
	 * Returns a java file comment for given class.
	 * @param className the name of the commented class.
	 * @return return the content of a comment.
	 * @deprecated use generateJavaTypeComment(String className, IGenerator generator) instead
	 */
	public static final String generateJavaTypeComment(String className){
		return generateJavaTypeComment(className, (String)null);
	}

	/**
	 * Returns a java file comment for given class and a generator.
	 * @param className the name of the commented class.
	 * @param generator the name/class of the generator which generated the code.
	 * @return return the content of a comment.
	 */
	public static final String generateJavaTypeComment(String className, IGenerator generator){
		return generateJavaTypeComment(className, "Generator: "+generator.getClass().getName());
	}
	
	/**
	 * Generates and returns java file comment for given class. Includes additional line(s) supplied by the user.
	 * @param className the class to comment
	 * @param additionalInfo an info string to add to the content
	 * @return the comment for the class.
	 */
	public static final String generateJavaTypeComment(String className, String additionalInfo){
		
		List<String> lines = new ArrayList<String>();		
		if (!className.endsWith(".java"))
			className += ".java";
		
		lines.add(null);
		lines.add(className);
		if (additionalInfo!=null && additionalInfo.length()>0)
			lines.add(additionalInfo);
		lines.add("generated by "+Generator.getProductString()+", Version: "+Generator.getVersionString());
		lines.add(COPYRIGHT);
		lines.add(RIGHTS);
		lines.add(null);
		lines.add(NOTICE_1);
		lines.add(NOTICE_2);
		lines.add(NOTICE_3);
		lines.add(null);
		
		
		int longestLineLength = findLongestLineLength(lines);
		longestLineLength+=LINES_ADD_LENGTH;
		String separator = "";
		for (int i=0; i<longestLineLength; i++)
			separator+=SEPARATOR;
		
		String ret = "";
		ret += COMM_START+CRLF;
		for (int i=0; i<lines.size(); i++){
			String line = lines.get(i);
			if (line == null){
				ret += " "+separator + CRLF;
			}else{
				while(line.length()<(longestLineLength-LINES_ADD_LENGTH))
					line += " ";
				ret += " "+LINE_PRE+line+LINE_POST+CRLF;
			}
		}
		ret += COMM_END+CRLF;
		ret += CRLF;
		
		return ret;
		
	}
	
	/**
	 * Returns the length of the longest comment line for beautifuying.
	 * @param lines a list of string objects
	 * @return the length of the longest line
	 */
	private static int findLongestLineLength(List<String> lines){
		int length = 0;
		for (int i=0; i<lines.size(); i++){
			String line = lines.get(i);
			if (line==null)
				continue;
			int l = line.length();
			if (l>length)
				length = l;
		}
		return length;
	}
}