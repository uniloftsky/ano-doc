package net.anotheria.asg.generator.view.jsp;

import net.anotheria.asg.generator.Context;
import net.anotheria.asg.generator.FileEntry;
import net.anotheria.asg.generator.GeneratedJSPFile;
import net.anotheria.asg.generator.GeneratorDataRegistry;
import net.anotheria.asg.generator.meta.MetaModule;

public class IndexPageJspGenerator extends AbstractJSPGenerator {

	public FileEntry generate(Context context) {
		FileEntry page = new FileEntry(getIndexPagePathJSP(), getIndexPageJspName(),
				generateIndexPage().createFileContent());
		page.setType(".jsp");
		return page;
	}

	public static final String getIndexPageJspName() {
		return "WelcomePageMaf";
	}

	public static final String getSharedJspFooterPageName() {
		return getIndexPageJspName() + ".jsp";
	}
	
	public static final String getIndexJspFullName() {
		return getIndexPageJspPath() + "/" + getSharedJspFooterPageName();
	}
	
	
	public static final String getIndexPagePathJSP(){
		return  FileEntry.package2fullPath(GeneratorDataRegistry.getInstance().getContext().getPackageName(MetaModule.SHARED) + ".jsp");      
	}

	public static final String getIndexPageJspPath(){
	
		 return      FileEntry.package2fullPath(GeneratorDataRegistry.getInstance().getContext().getPackageName(MetaModule.SHARED)).substring(FileEntry.package2fullPath(GeneratorDataRegistry.getInstance().getContext().getPackageName(MetaModule.SHARED)).indexOf('/'))+"/jsp";
	}

	private GeneratedJSPFile generateIndexPage() {
		GeneratedJSPFile jsp = new GeneratedJSPFile();
		startNewJob(jsp);
		jsp.setName(getIndexPageJspName());

		resetIdent();

		append(getBaseJSPHeader());
		
		appendString("<!--  generated by IndexPageJspMafGenerator.generateIndexPage -->");	
		appendString("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		appendString("<head>");
		increaseIdent();
		appendString("<title>" + getIndexPageJspName() + "</title>");
		generatePragmas();
		appendString("<script type=\"text/javascript\" src=\""+getCurrentJSPath("jquery-1.4.min.js")+"\"></script>");
		appendString("<script type=\"text/javascript\" src=\""+getCurrentJSPath("anofunctions.js")+"\"></script>");
		appendString("<link href=\"" + getCurrentCSSPath("newadmin.css") + "\" rel=\"stylesheet\" type=\"text/css\">");
		decreaseIdent();
		appendString("</head>");
		appendString("<body>");
		
		appendString("<jsp:include page=\""+"../../shared/jsp/"+MenuJspGenerator.getMenuPageName()+"\" flush=\"true\"/>");
		appendString("<div class=\"right\">");
		increaseIdent();
		appendString("<div class=\"r_w\">");
		increaseIdent();
		appendString("<div class=\"main_area\">");
		increaseIdent();
			appendString("<div class=\"c_l\"><!-- --></div>");
			appendString("<div class=\"c_r\"><!-- --></div>");
			appendString("<div class=\"c_b_l\"><!-- --></div>");
			appendString("<div class=\"c_b_r\"><!-- --></div>");
			appendString("<h1>Welcome, <ano:write name=\"currentUserId\"/>!</h1>");
			
			appendString("<table cellpadding=\"0\" cellspacing=\"0\" width=\"48.8%\" class=\"pages_table welcome_table\">");
			appendString("<thead>");
			appendString("<tr>");
			appendString("<td colspan=\"4\"><h2>Last changes</h2></td>");
			appendString("</tr>");
			appendString("<tr>");
			appendString("<td>Document</td>");
			appendString("<td>Action</td>");
			appendString("<td>User name</td>");
			appendString("<td>Time</td>");
			appendString("</tr>");
			appendString("</thead>");
			appendString("<tbody>");
			appendString("<ano:iterate name=\"changes\" id=\"change\" type=\"net.anotheria.anosite.gen.shared.bean.DocumentChangeFB\" indexId="+quote("ind")+">");
			appendString("<tr class=\"<%=ind.intValue()%2==0 ? \"lineLight\" : \"lineDark\"%> highlightable\">");
			appendString("<td><a href=\"<ano:write name=\"change\" property=\"parentName\"/><ano:write name=\"change\" property=\"documentName\"/>Edit?pId=<ano:write name=\"change\" property=\"id\"/>\"><ano:write name=\"change\" property=\"documentName\"/></a></td>");
			appendString("<td><ano:write name=\"change\" property=\"action\"/></td>");
			appendString("<td><ano:write name=\"change\" property=\"userName\"/></td>");
			appendString("<td><ano:write name=\"change\" property=\"date\"/></td>");
			appendString("</tr>");
			appendString("</ano:iterate>");
			appendString("</tbody>");
			appendString("</table>");
			
			appendString("<div class=\"welcome_text\">");
			appendString("<p>You are on AnoSiteGenerator v.2.0 main page now. This system helps to create pages, layouts and content for site pages.</p>");
			appendString("</div>");
			appendString("<div class=\"clear\"><!-- --></div>");
			appendString("</div>");
			appendString("</div>");
			appendString("</div>");
			

		
		appendString("</body>");
		appendString("</html>");
		appendString("<!-- / generated by IndexPageJspMafGenerator.generateIndexPage -->");

		append(getBaseJSPFooter());

		return jsp;
	}

}
