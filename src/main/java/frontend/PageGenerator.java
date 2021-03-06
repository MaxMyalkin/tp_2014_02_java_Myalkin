package frontend;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

@CreatedBy( name = "max" , date = "15.02.14" )
public class PageGenerator {
    private static final String TML_DIR = "tml";
    private static final Configuration CFG = new Configuration();

    public static String getPage(String filename, Map<String, Object> data) {
        Writer stream = new StringWriter();
        try {
            CFG.setDirectoryForTemplateLoading(new File(TML_DIR));
            CFG.setDefaultEncoding("UTF-8");
            Template template = CFG.getTemplate(filename);
            template.process(data, stream);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
        return stream.toString();
    }
}