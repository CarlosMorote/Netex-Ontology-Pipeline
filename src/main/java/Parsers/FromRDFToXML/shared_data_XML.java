package Parsers.FromRDFToXML;

import DataManager.Ontology.Namespaces;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.time.LocalDateTime;

public class shared_data_XML {
    public Model rdf;
    public Document xml;

    public shared_data_XML(Model rdf, Document xml) {
        this.rdf = rdf;
        this.xml = xml;
    }

    public Document initSharedXML(){
        // Root
        Element root = new Element("PublicationDelivery", Namespace.getNamespace("http://www.netex.org.uk/netex"));
        xml.setRootElement(root);
        //root.addNamespaceDeclaration(Namespace.getNamespace("http://www.netex.org.uk/netex"));
        root.addNamespaceDeclaration(Namespace.getNamespace("gis", "http://www.opengis.net/gml/3.2"));
        root.addNamespaceDeclaration(Namespace.getNamespace("siri", "http://www.siri.org.uk/siri"));

        //Description
        Element description = new Element("Description");
        description.setText("Shared data used across line files");
        root.addContent(description);

        return xml;
    }

    public Document generate(){
        initSharedXML();
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
        mapRoutePoints(ServiceFrame);
        mapDestinadionDisplays(ServiceFrame);

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

        return xml;
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

        System.out.println("ScheduleStopPoints mapped");

        return current;
    }

    private Element mapRoutePoints(Element current){
        Element routePoints = new Element("routePoints");

        StmtIterator iterator = rdf.listStatements(null, RDF.type, Namespaces.ROUTE_POINT_resource);
        Resource currentResource;
        while(iterator.hasNext()){
            currentResource = rdf.getResource(iterator.nextStatement().getSubject().toString());

            Element RoutePoint = new Element("RoutePoint");
            RoutePoint.setAttribute("id", currentResource.getProperty(RDFS.label).getObject().toString());

            Element projections = new Element("projections");
            Element PointProjection = new Element("PointProjection");
            PointProjection.setAttribute(
                    "id",
                    currentResource.getProperty(Namespaces.hasPointProjection).getObject().toString()
            );

            Element ProjectedPointRef = new Element("ProjectedPointRef");
            ProjectedPointRef.setAttribute(
                    "id",
                    currentResource.getProperty(Namespaces.scheduledStopPoint).getObject().toString()
            );

            PointProjection.addContent(ProjectedPointRef);
            projections.addContent(PointProjection);
            RoutePoint.addContent(projections);
            routePoints.addContent(RoutePoint);
        }

        current.addContent(routePoints);
        System.out.println("RoutePoints mapped");
        return current;
    }

    private Element mapDestinadionDisplays(Element current){
        Element destinationDisplays = new Element("destinationDisplay");

        StmtIterator iterator = rdf.listStatements(null, RDF.type, Namespaces.DESTINATION_DISPLAY_resource);
        Resource currentResource;
        while(iterator.hasNext()){
            currentResource = rdf.getResource(iterator.nextStatement().getSubject().toString());

            Element DestinationDisplay = new Element("DestinationDisplay");
            DestinationDisplay.setAttribute("id", currentResource.getProperty(RDFS.label).getObject().toString());

            Element FrontText = new Element("FrontText");
            FrontText.setText(currentResource.getProperty(Namespaces.frontText).getObject().toString());

            DestinationDisplay.addContent(FrontText);
            destinationDisplays.addContent(DestinationDisplay);
        }

        current.addContent(destinationDisplays);
        return current;
    }
}



















