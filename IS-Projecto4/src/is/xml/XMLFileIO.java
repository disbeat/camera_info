
package is.xml;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * Class responsible to read and write XML files
 *
 * @author msimoes
 */
public class XMLFileIO {

    public static void writeToFile(Document doc, String file) throws IOException{
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        FileWriter writer = new FileWriter(file);
        outputter.output(doc, writer);
        writer.close();
    }

    public static Document readFromFile(String file) throws JDOMException, IOException{
        SAXBuilder builder = new SAXBuilder();
        return builder.build(file);
    }

	 public static Document readFromString(String xml) throws JDOMException, IOException{
        SAXBuilder builder = new SAXBuilder();
        return builder.build(new StringReader(xml));
    }
}
