package Parsers;

import DataManager.Ontology.Namespaces;
import DataManager.Ontology.RDFManager;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.impl.StatementImpl;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.VCARD4;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

public class NetexParserFromRDF {

    public Model rdf;
    public Document xml;
    public String out_path;
    public XMLOutputter outputter;

    public NetexParserFromRDF(String rdf_path, String out_path){
        this.rdf = RDFDataMgr.loadModel(rdf_path);
        this.out_path = out_path;
        outputter = new XMLOutputter(Format.getPrettyFormat());
        this.initXML();
    }

    private void initXML(){
        this.xml = new Document();
    }

    private void generate_sharedData() throws IOException {
        this.xml = XMLStructures.initSharedXML(xml);
        Element root = this.xml.getRootElement();

        Element dataObjects = new Element("dataObjects");

        Element CompositeFrame = new Element("CompositeFrame");
        CompositeFrame.setAttribute("id", "");
        CompositeFrame.setAttribute("created", LocalDateTime.now().toString());

        Element validityConditions = new Element("validityConditions");

        Element codespaces = new Element("codespaces");

        Element FrameDefaults = new Element("FrameDefaults");

        Element frames = new Element("frames");
        Element ResourceFrame = new Element("ResourceFrame");
        mapOrganizations(ResourceFrame);
        Element ServiceFrame = new Element("ServiceFrame"); //SEGUIR POR AQUÍ
        Element ServiceCalendarFrame = new Element("ServiceCalendarFrame");

        frames.addContent(ResourceFrame);
        frames.addContent(ServiceFrame);
        frames.addContent(ServiceCalendarFrame);

        CompositeFrame.addContent(frames);
        CompositeFrame.addContent(FrameDefaults);
        CompositeFrame.addContent(codespaces);
        CompositeFrame.addContent(validityConditions);

        dataObjects.addContent(CompositeFrame);
        root.addContent(dataObjects);

        String p = out_path + "_shared_data.xml";
        outputter.output(xml, new FileOutputStream(p));
        System.out.println("File "+p+" generated");
    }

    private Element mapOrganizations(Element current){
        Element organizations = new Element("organizations");

        StmtIterator itera = rdf.listStatements(null, RDF.type, Namespaces.OPERATOR_resource);
        Resource currentResource;
        while(itera.hasNext()){
            currentResource = rdf.getResource(itera.nextStatement().getSubject().toString());

            Element operator = new Element("Operator");
            operator.setAttribute("id", currentResource.getProperty(RDFS.label).getObject().toString());

            Element name = new Element("Name");
            name.setText(currentResource.getProperty(VCARD4.hasName).getObject().toString());

            Element compNum = new Element("CompanyNumber");
            compNum.setText(currentResource.getProperty(SKOS.notation).getObject().toString());

            Element customerServiceContactDetails = new Element("CustomerServiceContactDetails");
            Element url = new Element("Url");
            url.setText(currentResource.getProperty(VCARD4.hasURL).getObject().toString());
            customerServiceContactDetails.addContent(url);

            operator.addContent(customerServiceContactDetails);
            operator.addContent(compNum);
            operator.addContent(name);

            current.addContent(operator);
        }

        current.addContent(organizations);

        return current;
    }

    private void generate_stops(){

    }

    private void generate_Line(){

    }

    private void generate_Lines(){
        //Listar lineas e iterar sobre generate_Line
    }

    public void parse(){
        try {
            generate_sharedData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
