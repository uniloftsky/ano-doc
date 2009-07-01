package net.anotheria.asg.generator.view;

import java.util.List;

import net.anotheria.asg.generator.AbstractGenerator;
import net.anotheria.asg.generator.Context;
import net.anotheria.asg.generator.GeneratedJSPFile;
import net.anotheria.asg.generator.Generator;
import net.anotheria.asg.generator.GeneratorDataRegistry;
import net.anotheria.asg.generator.meta.MetaContainerProperty;
import net.anotheria.asg.generator.meta.MetaDocument;
import net.anotheria.asg.generator.meta.MetaModule;
import net.anotheria.asg.generator.view.meta.MetaDialog;
import net.anotheria.asg.generator.view.meta.MetaModuleSection;
import net.anotheria.asg.generator.view.meta.MetaSection;
import net.anotheria.asg.generator.view.meta.MetaView;
import net.anotheria.util.StringUtils;

/**
 * Generator for the JSP files used for generations of the view jsps. 
 * @author lrosenberg
 */
public abstract class AbstractJSPGenerator extends AbstractGenerator{
	
	public static final String FOOTER_SELECTION_QUERIES = "Queries";
	public static final String FOOTER_SELECTION_CMS     = "CMS";

	/*
	 * The context for the generation.
	 */
	private Context context;
	
	/**
	 * Generates the header for all jsp files. 
	 * @return
	 */
	protected String getBaseJSPHeader(){
		String ret = "";
		ret += "<%@ page"+CRLF;
		ret += "\tcontentType=\"text/html;charset="+GeneratorDataRegistry.getInstance().getContext().getEncoding()+"\" session=\"true\""+CRLF;
		
		ret += "%><%@ taglib uri=\"/tags/struts-bean\" prefix=\"bean\""+CRLF;
		ret += "%><%@ taglib uri=\"/tags/struts-html\" prefix=\"html\""+CRLF;
		ret += "%><%@ taglib uri=\"/tags/struts-logic\" prefix=\"logic\""+CRLF;
		ret += "%><%@ taglib uri=\"/WEB-INF/tld/anoweb.tld\" prefix=\"ano\""+CRLF;
		ret += "%>"+CRLF;
		return ret;
	}
	
	/**
	 * Generates the header for jsp files that generate xml exports.
	 * @return
	 */
	protected String getBaseXMLHeader(){
		String ret = "";
		ret += "<%@ page"+CRLF;
		ret += "\tcontentType=\"text/xml;charset="+GeneratorDataRegistry.getInstance().getContext().getEncoding()+"\" session=\"true\""+CRLF;
		
		ret += "%><%@ taglib uri=\"/WEB-INF/tld/struts-bean.tld\" prefix=\"bean\""+CRLF;
		ret += "%><%@ taglib uri=\"/WEB-INF/tld/struts-html.tld\" prefix=\"html\""+CRLF;
		ret += "%><%@ taglib uri=\"/WEB-INF/tld/struts-logic.tld\" prefix=\"logic\""+CRLF;
		ret += "%><%@ taglib uri=\"/WEB-INF/tld/anoweb.tld\" prefix=\"ano\""+CRLF;
		ret += "%>";
		return ret;
	}

	/**
	 * Generates the header for jsp files that generate csv exports.
	 * @return
	 */
	protected String getBaseCSVHeader(){
		String ret = "";
		ret += "<%@ page"+CRLF;
		ret += "\tcontentType=\"application/msexcel;charset="+GeneratorDataRegistry.getInstance().getContext().getEncoding()+"\" session=\"true\""+CRLF;
		
		ret += "%><%@ taglib uri=\"/WEB-INF/tld/struts-bean.tld\" prefix=\"bean\""+CRLF;
		ret += "%><%@ taglib uri=\"/WEB-INF/tld/struts-html.tld\" prefix=\"html\""+CRLF;
		ret += "%><%@ taglib uri=\"/WEB-INF/tld/struts-logic.tld\" prefix=\"logic\""+CRLF;
		ret += "%><%@ taglib uri=\"/WEB-INF/tld/anoweb.tld\" prefix=\"ano\""+CRLF;
		ret += "%>";
		return ret;
	}
	
	private String getFooterLink(String selection, String link, String linkCaption){
		if (selection!=null && selection.equals(linkCaption)){
			return "<strong>"+linkCaption+"</strong>"; 
		}
		return "&nbsp;<a href=\"<ano:tslink>"+link+"</ano:tslink>\">"+linkCaption+"</a>";

	}

	/**
	 * Generates a footer file which is included by other jsps.
	 * @param view the vie to generate the footer for.
	 * @param selection ?
	 * @param name the name of the file.
	 * @return
	 */
	protected GeneratedJSPFile generateFooter(MetaView view, String selection, String name){
		
		GeneratedJSPFile jsp = new GeneratedJSPFile();
		startNewJob(jsp);
		
		jsp.setPackage(GeneratorDataRegistry.getInstance().getContext().getPackageName(MetaModule.SHARED)+".jsp");
		jsp.setName(name);
		
		append(getBaseJSPHeader());
		
		appendString("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		increaseIdent();
		appendString("<tr>");
		increaseIdent();

		appendString("<td>");
		increaseIdent();
		
		appendString(getFooterLink(selection, defineLinkToViewCms(view), FOOTER_SELECTION_CMS));
		appendString("&nbsp;|&nbsp;");
		//appendString("<a href=\""+defineLinkToViewQueries(view)+"\">Queries</a>");
		appendString(getFooterLink(selection, defineLinkToViewQueries(view), FOOTER_SELECTION_QUERIES));
		
		decreaseIdent();
		appendString("</td>");
		appendString("<td>");
		appendIncreasedString("<jsp:include page="+quote(SharedJspFooterGenerator.getSharedJspFooterPageName())+" flush="+quote("false")+"/>");
		appendString("</td>");
		appendString("<td align="+quote("right")+">");
		increaseIdent();
		
		appendString("Generated by "+Generator.getProductString()+" V "+Generator.getVersionString());
		//String timestamp = NumberUtils.makeISO8601TimestampString(System.currentTimeMillis());
		String timestamp = "Timestamp unset";
		appendString(" on "+timestamp+"&nbsp;|&nbsp;");
		appendString("<a href=\"<ano:tslink>logout</ano:tslink>\">Logout</a>&nbsp;");
		
		decreaseIdent();
		appendString("</td>");
		decreaseIdent();
		appendString("</tr>");
		decreaseIdent();
		appendString("</table>");
		
		append(getBaseJSPFooter());
			
		return jsp;
	}
	
	private String defineLinkToViewQueries(MetaView view){
		String ret = "#";
		List<MetaSection> sections = view.getSections();
		for (int i=0; i<sections.size(); i++){
			MetaSection s = sections.get(i);
			if (s instanceof MetaModuleSection){
				MetaModuleSection ms = (MetaModuleSection)s;
				MetaDocument targetDoc = ms.getDocument();
				if (targetDoc.getLinks().size()>0){
					return StrutsConfigGenerator.getShowQueriesPath(targetDoc);
				}
			}
		}
		return ret;
	}
	
	/**
	 * Returns image tag for the duplicate entry image.
	 * @return
	 */
	protected String getDuplicateImage(){
		return getDuplicateImage("duplicate");
	}

	private String defineLinkToViewCms(MetaView view){
		String ret = "#";
		List<MetaSection> sections = view.getSections();
		for (int i=0; i<sections.size(); i++){
			MetaSection s = sections.get(i);
			if (s instanceof MetaModuleSection){
				MetaDocument targetDoc = ((MetaModuleSection)s).getDocument();
				return StrutsConfigGenerator.getShowCMSPath(targetDoc);
			}
		}
		return ret;
	}

	/**
	 * Returns the basic footer used in all jsps.
	 * @return content of the base jsp footer.
	 */
	protected String getBaseJSPFooter(){
		String ret = "<!-- generated by "+Generator.getProductString()+" V "+Generator.getVersionString()+", visit www.anotheria.net for details -->";
		return ret;
	}
	
	/**
	 * Returns the name of the jsp file for the show page for the document.
	 * @param doc
	 * @return
	 */
	public static String getShowPageName(MetaDocument doc){
		return "Show"+doc.getMultiple();
	}
	
	public static String getSearchResultPageName(){
		return "SearchResult";
	}

	/**
	 * Returns the page name for the version info page.
	 * @return
	 */
	public static String getVersionInfoPageName(){
		return "VersionInfo";
	}

	/**
	 * Returns the page name for the version info page for the given document.
	 * @return
	 */
	public static String getVersionInfoPageName(MetaDocument doc){
		return getVersionInfoPageName();
	}

	
	public static String getShowQueriesPageName(MetaDocument doc){
		return "Show"+doc.getMultiple()+"Queries";
	}
	
	/**
	 * Returns the name of the jsp file for the csv page export for the document.
	 * @return
	 */
	public static String getExportAsCSVPageName(MetaDocument doc){
		return "Show"+doc.getMultiple()+"AsCSV";
	}

	/**
	 * Returns the name of the jsp file for the xml page export for the document.
	 * @return
	 */
	public static String getExportAsXMLPageName(MetaDocument doc){
		return "Show"+doc.getMultiple()+"AsXML";
	}

	/**
	 * Returns the name of the edit page for this document.
	 * @return
	 */
	public static String getEditPageName(MetaDocument doc){
		return "Edit"+doc.getName()+"Dialog";
	}
	
	public static String getDialogName(MetaDialog dialog, MetaDocument doc){
		return dialog.getName()+doc.getName()+"Dialog";
	}
	
	public static String getLinksToMePageName(MetaDocument doc){
		return "LinksTo"+doc.getName();
	}

	public static String getContainerPageName(MetaDocument doc, MetaContainerProperty table){
		return "Show"+doc.getName()+StringUtils.capitalize(table.getName());
	}
	
	protected static String generateTimestampedLinkPath(String path){
		return "<ano:tslink>"+path+"</ano:tslink>";
	}

	

	protected String getCurrentImagePath(String imageName){
		return getImagePath(imageName, GeneratorDataRegistry.getInstance().getContext());
	}
	
	public static String getImagePath(String imageName, Context context){
		return context.getApplicationURLPath()+"/img/"+imageName;
	}

	protected String getCurrentCSSPath(String stylesheetName){
		return getCSSPath(stylesheetName, GeneratorDataRegistry.getInstance().getContext());
	}
	
	protected String getCurrentJSPath(String jsName){
		return getJSPath(jsName, GeneratorDataRegistry.getInstance().getContext());
	}
	
	protected String getCurrentYUIPath(String yuiName){
		return getYUIPath(yuiName, context);
	}

	public static String getCSSPath(String stylesheetName, Context context){
		return context.getApplicationURLPath()+"/css/"+stylesheetName;
	}

	public static String getJSPath(String jsName, Context context){
		return context.getApplicationURLPath()+"/js/"+jsName;
	}

	public static String getYUIPath(String yuiName, Context context){
		return context.getApplicationURLPath()+"/yui/"+yuiName;
	}

	public static String getPackage(MetaModule mod){
		return GeneratorDataRegistry.getInstance().getContext().getJspPackageName(mod);
	}

	public static String getPackage(MetaDocument doc){
		return GeneratorDataRegistry.getInstance().getContext().getJspPackageName(doc);
	}

	/**
	 * Returns the tag for the delete image in the overview. 
	 * @return
	 */
	protected String getDeleteImage(){
		return getDeleteImage("delete");
	}

	/**
	 * Returns the tag for the version info image in the overview. Note: for now it returns a 'V' text.
	 * @return
	 */
	protected String getVersionImage(){
		return "V";//getDeleteImage("delete");
	}

	/**
	 * Returns the tag for the edit image in the overview.
	 * @return
	 */
	protected String getEditImage(){
		return getEditImage("edit");
	}

	/**
	 * Returns the tag for the delete image in the overview with the given alt tag. 
	 * @return
	 */
	protected String getDeleteImage(String alt){
		return getImage("del",alt);
	}

	/**
	 * Returns the tag for the move to the top image in the list view with the given alt tag. 
	 * @return
	 */
	protected String getTopImage(String alt){
		return getImage("top",alt);
	}

	/**
	 * Returns the tag for the move to the bottom image in the list view with the given alt tag. 
	 * @return
	 */
	protected String getBottomImage(String alt){
		return getImage("bottom",alt);
	}

	/**
	 * Returns the tag for the move up image in the list view with the given alt tag. 
	 * @return
	 */
	protected String getUpImage(String alt){
		return getImage("up",alt);
	}

	/**
	 * Returns the tag for the move down image in the list view with the given alt tag. 
	 * @return
	 */
	protected String getDownImage(String alt){
		return getImage("down",alt);
	}

	/**
	 * Returns the tag for the duplicate image in the overview with the given alt tag. 
	 * @return
	 */
	protected String getDuplicateImage(String alt){
		return getImage("duplicate",alt);
	}

	protected String getEditImage(String alt){
		return getImage("edit",alt);
	}
	
	/**
	 * Returns the img tag to embed an image into the jsp.
	 * @param name name of the gif file containing the image.
	 * @param alt alternative description (alt tag).
	 * @return
	 */
	protected String getImage(String name, String alt){
		return "<img src=\""+GeneratorDataRegistry.getInstance().getContext().getApplicationURLPath()+"/img/"+name+".gif"+"\" border=\"0\" alt="+quote(alt)+" title="+quote(alt)+">";
	}

	public Context getContext() {
		return GeneratorDataRegistry.getInstance().getContext();
	}

	public void setContext(final Context aContext) {
		context = aContext;
	}

	/**
	 * Generates pragmas for a given view.
	 * @param view the view to generate pragmas for.
	 */
	protected void generatePragmas(MetaView view){
	    generatePragmas();
	}
	
	/**
	 * Generates generic pragmas.
	 * Adds the generated content to the current job.
	 */
	protected void generatePragmas(){
		appendString("<META http-equiv=\"pragma\" content=\"no-cache\">");
		appendString("<META http-equiv=\"Cache-Control\" content=\"no-cache, must-revalidate\">");
		appendString("<META name=\"Expires\" content=\"0\">");
		appendString("<META http-equiv=\"Content-Type\" content=\"text/html; charset="+GeneratorDataRegistry.getInstance().getContext().getEncoding()+"\">");
	}
	
	protected void openTag(String tag, String params){
		appendString("<"+tag+(params.length()>0 ? " ":"")+params+">");
	}

	protected void closeTag(String tag, String params){
		appendString("</"+tag+(params.length()>0 ? " ":"")+params+">");
	}

	protected void openTR(String additionalParams){
		openTag("tr", additionalParams);
		increaseIdent();
	}
	
	protected void closeTR(String additionalParams){
		decreaseIdent();
		closeTag("tr", additionalParams);
	}

	protected void openTR(){
		openTR("");
	}
	
	protected void closeTR(){
		closeTR("");
	}
	
	/**
	 * Opens a table cell.
	 */
	protected void openTD(){
		openTD("");
	}
	
	protected void closeTD(){
		closeTD("");
	}
	
	/**
	 * Opens a table cell.
	 * @param additionalParams additional parameters.
	 */
	protected void openTD(String additionalParams){
		openTag("td", additionalParams);
	}
	
	protected void closeTD(String additionalParams){
		closeTag("td", additionalParams);
	}
}