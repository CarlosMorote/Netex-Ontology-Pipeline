package Parsers;

import DataManager.Ontology.Namespaces;
import DataManager.Ontology.RDFManager;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.impl.StatementImpl;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.*;
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
        Element ServiceFrame = new Element("ServiceFrame");
        mapScheduleStopPoints(ServiceFrame);
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

        // Operator
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

            Element organisationType = new Element("OrganisationType");
            organisationType.setText("operator");
            operator.addContent(organisationType);

            operator.addContent(customerServiceContactDetails);
            operator.addContent(compNum);
            operator.addContent(name);

            current.addContent(operator);
        }

        // Authority
        itera = rdf.listStatements(null, RDF.type, Namespaces.AUTHORITY_resource);
        while(itera.hasNext()){
            currentResource = rdf.getResource(itera.nextStatement().getSubject().toString());

            Element authority = new Element("Authority");
            authority.setAttribute("id", currentResource.getProperty(RDFS.label).getObject().toString());

            Element name = new Element("Name");
            name.setText(currentResource.getProperty(SKOS.prefLabel).getObject().toString());
            authority.addContent(name);

            Element companyNumber = new Element("CompanyNumber");
            companyNumber.setText(currentResource.getProperty(SKOS.notation).getObject().toString());
            authority.addContent(companyNumber);

            Element organisationType = new Element("OrganisationType");
            organisationType.setText("authority");
            authority.addContent(organisationType);

            current.addContent(authority);
        }

        current.addContent(organizations);
        System.out.println("Organizations mapped");

        return current;
    }

    private Element mapScheduleStopPoints(Element current){
        Element scheduledStopPoints = new Element("scheduledStopPoints");

        StmtIterator iterator = rdf.listStatements(null, RDF.type, Namespaces.SCHEDULE_STOP_POINT_resource);
        Resource currentResource;
        while(iterator.hasNext()){
            currentResource = rdf.getResource(iterator.nextStatement().getSubject().toString());

            Element ScheduledStopPoint = new Element("ScheduledStopPoint");
            String id = currentResource.getProperty(RDFS.label).getObject().toString();
            ScheduledStopPoint.setAttribute("id", id);

            Element name = new Element("Name");
            name.setText(currentResource.getProperty(SchemaDO.name).getObject().toString());
            ScheduledStopPoint.addContent(name);

            Element ValidityBetween = new Element("ValidityBetween");
            StmtIterator from_iterator = rdf.listStatements(
                    rdf.createResource(Namespaces.JOURNEYS+"/Resource/ScheduledStopPoint/"+id),
                    Namespaces.hasValidity,
                    (String) null
            );
            Resource currentResource_2;
            while(from_iterator.hasNext()){
                currentResource_2 = rdf.getResource(from_iterator.nextStatement().getSubject().toString());

                Element FromDate = new Element("FromDate");
                FromDate.setText(currentResource_2.getProperty(Namespaces.hasValidity).getObject().toString());

                ValidityBetween.addContent(FromDate);
            }

            ScheduledStopPoint.addContent(ValidityBetween);
            scheduledStopPoints.addContent(ScheduledStopPoint);
        }

        current.addContent(scheduledStopPoints);

        System.out.println("ScheduleStopPoints");

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
