package Parsers.FromRDFToXML;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.jdom2.Document;

public class line_data_XML {

    public Model rdf;
    public Document xml;
    public Resource line_resource;

    public line_data_XML(Model rdf, Document xml, Resource line_resource) {
        this.rdf = rdf;
        this.xml = xml;
        this.line_resource = line_resource;
    }

    public Document generate(){

        return xml;
    }
}
