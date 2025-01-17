package net.anotheria.asg.generator.apputil;

import net.anotheria.asg.generator.*;
import net.anotheria.asg.generator.meta.MetaDocument;
import net.anotheria.asg.generator.meta.MetaModule;
import net.anotheria.asg.generator.view.action.ModuleActionsGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ykalapusha
 */
public class BasicServiceUtilGenerator extends AbstractGenerator {

    public List<FileEntry> generate(List<MetaModule> modules) {
        List<FileEntry> entries = new ArrayList<>();
        entries.add(new FileEntry(generateParsingUtilService(modules)));
        entries.add(new FileEntry(generateEnumModules(modules)));
        entries.add(new FileEntry(generateEnumDocuments(modules)));
        entries.add(new FileEntry(generateRestResourceForImages()));
        return entries;
    }

    private GeneratedClass generateEnumModules(List<MetaModule> modules) {
        GeneratedClass clazz = new GeneratedClass();
        startNewJob(clazz);
        appendGenerationPoint("generateEnumModules");

        clazz.setPackageName(GeneratorDataRegistry.getInstance().getContext().getPackageName(MetaModule.SHARED)+".util");
        clazz.addImport("net.anotheria.util.StringUtils");
        clazz.setType(TypeOfClass.ENUM);
        clazz.setName("ModuleName");
        startClassBody();

        for(MetaModule module: modules) {
            appendString("SERVICE_" + module.getName().toUpperCase() + "(\"" + module.getName() + "\"),");
        }
        appendStatement();
        emptyline();
        appendStatement("private String name");
        emptyline();
        appendString("ModuleName (String aName) {");
        increaseIdent();
        appendStatement("name = aName");
        closeBlockNEW();
        emptyline();
        appendString("public static ModuleName byValue (final String value) {");
        increaseIdent();
        appendString("if (StringUtils.isEmpty(value))");
        increaseIdent();
        appendStatement("throw new IllegalArgumentException(\"Value is not valid\")");
        decreaseIdent();
        appendString("for (ModuleName moduleName: ModuleName.values()) {");
        increaseIdent();
        appendString("if (moduleName.name.equals(value)) {");
        increaseIdent();
        appendStatement("return moduleName");
        closeBlockNEW();
        closeBlockNEW();
        appendStatement("throw new IllegalArgumentException(\"No such value in DataType\")");
        closeBlockNEW();

        return clazz;
    }

    private GeneratedClass generateEnumDocuments (List<MetaModule> modules) {
        GeneratedClass clazz = new GeneratedClass();
        startNewJob(clazz);
        appendGenerationPoint("generateEnumDocuments");

        clazz.setPackageName(GeneratorDataRegistry.getInstance().getContext().getPackageName(MetaModule.SHARED)+".util");
        clazz.addImport("net.anotheria.util.StringUtils");
        clazz.setType(TypeOfClass.ENUM);
        clazz.setName("DocumentName");
        startClassBody();

        for (MetaModule module: modules) {
            for (MetaDocument doc: module.getDocuments()) {
                appendString("DOCUMENT_" + module.getName().toUpperCase() + "_" + doc.getName().toUpperCase() + "(\"" + module.getName() + "_" + doc.getName() + "\"),");
            }
        }
        appendStatement();
        emptyline();
        appendStatement("private String name");
        emptyline();
        appendString("DocumentName (String aName) {");
        increaseIdent();
        appendStatement("name = aName");
        closeBlockNEW();
        emptyline();
        appendString("public static DocumentName byValue (final String value) {");
        increaseIdent();
        appendString("if (StringUtils.isEmpty(value))");
        increaseIdent();
        appendStatement("throw new IllegalArgumentException(\"Value is not valid\")");
        decreaseIdent();
        appendString("for (DocumentName documentName: DocumentName.values()) {");
        increaseIdent();
        appendString("if (documentName.name.equals(value)) {");
        increaseIdent();
        appendStatement("return documentName");
        closeBlockNEW();
        closeBlockNEW();
        appendStatement("throw new IllegalArgumentException(\"No such value in DataType\")");
        closeBlockNEW();

        return clazz;
    }

    private GeneratedClass generateParsingUtilService(List<MetaModule> modules) {

        GeneratedClass clazz = new GeneratedClass();
        startNewJob(clazz);
        appendGenerationPoint("generateParsingUtilService");

        clazz.setPackageName(GeneratorDataRegistry.getInstance().getContext().getPackageName(MetaModule.SHARED)+".util");
        clazz.addImport("net.anotheria.anosite.gen.shared.service.BasicService");
        clazz.addImport("net.anotheria.asg.exception.ASGRuntimeException");
        clazz.addImport("org.codehaus.jettison.json.JSONObject");
        clazz.addImport("org.codehaus.jettison.json.JSONArray");
        clazz.addImport("org.codehaus.jettison.json.JSONException");

        clazz.setName("ParserUtilService");
        clazz.setParent("BasicService");
        startClassBody();

        appendStatement("private static final ParserUtilService instance = new ParserUtilService()");
        emptyline();
        appendString("private ParserUtilService() { }");
        emptyline();
        appendString("public static ParserUtilService getInstance() {");
        increaseIdent();
        appendStatement("return instance");
        closeBlockNEW();
        emptyline();
        appendString("public void executeParsingDocuments (final JSONArray data) throws ASGRuntimeException, JSONException {");
        increaseIdent();
        appendString("for (int i = 0; i < data.length(); i++) {");
        increaseIdent();
        appendStatement("executeParsingDocument(data.getJSONObject(i))");
        closeBlockNEW();
        closeBlockNEW();
        emptyline();
        appendString("private void executeParsingDocument(final JSONObject data) throws ASGRuntimeException {");
        increaseIdent();
        appendStatement("final ModuleName moduleName");
        appendStatement("final DocumentName documentName");
        openTry();
        appendStatement("moduleName = ModuleName.byValue(data.getString(\"service\"))");
        appendStatement("documentName = DocumentName.byValue(data.getString(\"document\"))");
        appendCatch("JSONException");
        appendStatement("throw new ASGRuntimeException(\"Occurred problems with getting document metadata from json:\", e)");
        closeBlockNEW();
        appendStatement("executeParsingInServiceByName(moduleName, documentName, data)");
        closeBlockNEW();
        emptyline();
        appendString("protected void executeParsingInServiceByName(final ModuleName moduleName, final DocumentName documentName, final JSONObject dataObject) throws ASGRuntimeException {");
        increaseIdent();
        appendString("switch (moduleName) {");
        increaseIdent();
        for (MetaModule module: modules) {
            appendString("case SERVICE_" + module.getName().toUpperCase() + ":");
            increaseIdent();
            appendStatement(ModuleActionsGenerator.getServiceGetterCall(module) + ".executeParsingForDocument(documentName, dataObject)");
            appendStatement("break");
            decreaseIdent();
        }
        appendString("default:");
        increaseIdent();
        appendStatement("log.error(\"There is no needed module\")");
        appendStatement("throw new ASGRuntimeException(\"No such module\")");
        closeBlockNEW();
        closeBlockNEW();

        return clazz;
    }

    private GeneratedArtefact generateRestResourceForImages() {

        GeneratedClass clazz = new GeneratedClass();
        startNewJob(clazz);
        appendGenerationPoint("generateUploadImageResource");

        clazz.setPackageName(GeneratorDataRegistry.getInstance().getContext().getPackageName(MetaModule.SHARED)+".rest");

        clazz.addImport("org.glassfish.jersey.media.multipart.FormDataContentDisposition");
        clazz.addImport("org.glassfish.jersey.media.multipart.FormDataParam");
        clazz.addImport("javax.ws.rs.Consumes");
        clazz.addImport("javax.ws.rs.Produces");
        clazz.addImport("javax.ws.rs.POST");
        clazz.addImport("javax.ws.rs.Path");
        clazz.addImport("javax.ws.rs.core.Context");
        clazz.addImport("javax.ws.rs.core.MediaType");
        clazz.addImport("javax.ws.rs.core.Response");
        clazz.addImport("javax.ws.rs.core.UriInfo");
        clazz.addImport("java.io.File");
        clazz.addImport("java.io.FileOutputStream");
        clazz.addImport("java.io.IOException");
        clazz.addImport("java.io.InputStream");
        clazz.addImport("java.io.OutputStream");
        clazz.addImport("org.slf4j.Logger");
        clazz.addImport("org.slf4j.LoggerFactory");
        clazz.addImport("net.anotheria.webutils.filehandling.FileStorageConfig");
        clazz.addImport("net.anotheria.util.log.LogMessageUtil");

        clazz.addAnnotation("@Path(\"/asgimage\")");
        clazz.setName("UploadImageResource");
        startClassBody();

        appendStatement("private static final Logger LOGGER = LoggerFactory.getLogger(UploadImageResource.class)");
        emptyline();
        appendStatement("private static final FileStorageConfig CONFIG = FileStorageConfig.getInstance()");
        emptyline();
        appendString("@Context");
        appendStatement("private UriInfo context");
        emptyline();
        appendString("public UploadImageResource(){}");
        emptyline();
        appendString("@POST");
        appendString("@Path(\"/upload\")");
        appendString("@Consumes(MediaType.MULTIPART_FORM_DATA)");
        appendString("@Produces(MediaType.APPLICATION_JSON)");
        openFun("public Response uploadFile( @FormDataParam(\"file\") InputStream uploadedInputStream, @FormDataParam(\"file\") FormDataContentDisposition fileDetail)");
        appendString("if (uploadedInputStream == null || fileDetail == null)");
        increaseIdent();
        appendStatement("return Response.status(400).entity(\"Invalid form data\").build()");
        decreaseIdent();
        emptyline();
        appendStatement("String uploadedFileLocation = CONFIG.getDirectory() + File.separator + fileDetail.getFileName()");
        openTry();
        appendStatement("writeToFile(uploadedInputStream, uploadedFileLocation)");
        appendCatch("IOException");
        appendStatement("String failMsg = LogMessageUtil.failMsg(e)");
        appendStatement("LOGGER.error(failMsg)");
        appendStatement("return Response.status(500).entity(failMsg).build()");
        closeBlockNEW();
        appendStatement("return Response.status(201).build()");
        closeBlockNEW();
        emptyline();

        openFun("private void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) throws IOException");
        appendStatement("OutputStream out = new FileOutputStream(uploadedFileLocation)");
        appendStatement("int read = 0");
        appendStatement("byte[] bytes = new byte[4096];");
        appendString("while ((read = uploadedInputStream.read(bytes)) != -1) {");
        increaseIdent();
        appendStatement("out.write(bytes, 0, read)");
        closeBlockNEW();
        appendStatement("out.flush()");
        appendStatement("out.close()");
        closeBlockNEW();

        return clazz;
    }
}