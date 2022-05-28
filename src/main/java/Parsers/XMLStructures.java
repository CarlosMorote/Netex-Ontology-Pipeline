package Parsers;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

public class XMLStructures {
    public static Document initSharedXML(Document xml){
        // Root
        Element root = new Element("PublicationDelivery", Namespace.getNamespace("http://www.netex.org.uk/netex"));
        xml.setRootElement(root);
        //root.addNamespaceDeclaration(Namespace.getNamespace("http://www.netex.org.uk/netex"));
        root.addNamespaceDeclaration(Namespace.getNamespace("gis", "http://www.opengis.net/gml/3.2"));
        root.addNamespaceDeclaration(Namespace.getNamespace("siri", "http://www.siri.org.uk/siri"));
        //root.setAttribute(new Attribute("xmlns", "http://www.netex.org.uk/netex"));
        //root.setAttribute(new Attribute("xmlns:gis", "http://www.opengis.net/gml/3.2"));
        //root.setAttribute(new Attribute("xmlns:siri", "http://www.siri.org.uk/siri"));

        return xml;
    }
}
