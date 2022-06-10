package Parsers.FromRDFToXML;

import DataManager.Ontology.Namespaces;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
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

        Namespace ns = root.getNamespace();

        Element PublicationTimestamp = new Element("PublicationTimestamp", ns);
        PublicationTimestamp.setText(LocalDateTime.now().toString());
        root.addContent(PublicationTimestamp);

        Element ParticipantRef = new Element("ParticipantRef", ns);
        ParticipantRef.setText("RB");
        root.addContent(ParticipantRef);

        //Description
        Element description = new Element("Description", ns);
        description.setText("Shared data used across line files");
        root.addContent(description);

        return xml;
    }

    public Document generate(){
        initSharedXML();
        Element root = this.xml.getRootElement();

        Namespace ns = root.getNamespace();

        Element dataObjects = new Element("dataObjects", ns);

        Element CompositeFrame = new Element("CompositeFrame", ns);
        CompositeFrame.setAttribute("id", "");
        CompositeFrame.setAttribute("created", LocalDateTime.now().toString());
        CompositeFrame.setAttribute("version", "1");

        Element validityConditions = new Element("validityConditions", ns);

        Element codespaces = new Element("codespaces", ns);

        Element FrameDefaults = new Element("FrameDefaults", ns);

        Element frames = new Element("frames", ns);
        Element ResourceFrame = new Element("ResourceFrame", ns);
        ResourceFrame.setAttribute("id", "");
        mapOrganizations(ResourceFrame, ns);
        Element ServiceFrame = new Element("ServiceFrame", ns);
        ServiceFrame.setAttribute("id", "");
        mapScheduleStopPoints(ServiceFrame, ns);
        mapRoutePoints(ServiceFrame, ns);
        mapDestinadionDisplays(ServiceFrame, ns);

        Element ServiceCalendarFrame = new Element("ServiceCalendarFrame", ns);
        ServiceCalendarFrame.setAttribute("id", "");
        mapDayType(ServiceCalendarFrame, ns);
        mapOperatingPeriods(ServiceCalendarFrame, ns);
        mapDayTypeAssignments(ServiceCalendarFrame, ns);

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

    private Element mapDayTypeAssignments(Element serviceCalendarFrame, Namespace ns) {
        return serviceCalendarFrame;
    }

    private Element mapOperatingPeriods(Element serviceCalendarFrame, Namespace ns) {
        Element operatingPeriods = new Element("operatingPeriods", ns);

        StmtIterator iterator = rdf.listStatements(null, RDF.type, Namespaces.OPERATING_PERIOD_resource);
        while(iterator.hasNext()){
            Resource operatingPeriod_resource = rdf.getResource(iterator.nextStatement().getSubject().toString());
            Element OperatingPeriod = new Element("OperatingPeriod", ns);
            OperatingPeriod.setAttribute("id", operatingPeriod_resource.getProperty(RDFS.label).getObject().toString());

            Element FromDate = new Element("FromDate", ns);
            FromDate.setText(operatingPeriod_resource.getProperty(Namespaces.startingAt).getObject().toString());
            OperatingPeriod.addContent(FromDate);

            Element ToDate = new Element("ToDate", ns);
            ToDate.setText(operatingPeriod_resource.getProperty(Namespaces.endingAt).getObject().toString());
            OperatingPeriod.addContent(ToDate);

            operatingPeriods.addContent(OperatingPeriod);
        }

        serviceCalendarFrame.addContent(operatingPeriods);
        return serviceCalendarFrame;
    }

    private Element mapDayType(Element serviceCalendarFrame, Namespace ns) {
        Element dayTypes = new Element("dayTypes", ns);
        StmtIterator iterator = rdf.listStatements(null, RDF.type, Namespaces.DAY_TYPE_resource);
        while(iterator.hasNext()){
            Resource daytype_resource = rdf.getResource(iterator.nextStatement().getSubject().toString());

            Element DayType = new Element("DayType", ns);
            DayType.setAttribute("id", daytype_resource.getProperty(RDFS.label).getObject().toString());

            Statement days = daytype_resource.getProperty(Namespaces.daysOfWeek);
            if(days != null){
                Element properties = new Element("properties", ns);
                Element PropertyOfDay = new Element("PropertyOfDay", ns);
                Element DaysOfWeek = new Element("DaysOfWeek", ns);

                DaysOfWeek.setText(days.getObject().toString());

                PropertyOfDay.addContent(DaysOfWeek);
                properties.addContent(PropertyOfDay);
                DayType.addContent(properties);
            }

            dayTypes.addContent(DayType);
        }

        serviceCalendarFrame.addContent(dayTypes);
        System.out.println("dayTypes mapped");
        return serviceCalendarFrame;
    }

    private Element mapOrganizations(Element current, Namespace ns){
        Element organisations = new Element("organisations", ns);

        // Operator
        StmtIterator itera = rdf.listStatements(null, RDF.type, Namespaces.OPERATOR_resource);
        Resource currentResource;
        while(itera.hasNext()){
            currentResource = rdf.getResource(itera.nextStatement().getSubject().toString());

            Element operator = new Element("Operator", ns);
            operator.setAttribute("id", currentResource.getProperty(RDFS.label).getObject().toString());

            Element name = new Element("Name", ns);
            name.setText(currentResource.getProperty(VCARD4.hasName).getObject().toString());

            Element compNum = new Element("CompanyNumber", ns);
            compNum.setText(currentResource.getProperty(SKOS.notation).getObject().toString());

            Element customerServiceContactDetails = new Element("CustomerServiceContactDetails", ns);
            Element url = new Element("Url", ns);
            url.setText(currentResource.getProperty(VCARD4.hasURL).getObject().toString());
            customerServiceContactDetails.addContent(url);

            Element organisationType = new Element("OrganisationType", ns);
            organisationType.setText("operator");
            operator.addContent(organisationType);

            operator.addContent(customerServiceContactDetails);
            operator.addContent(compNum);
            operator.addContent(name);

            organisations.addContent(operator);
        }

        // Authority
        itera = rdf.listStatements(null, RDF.type, Namespaces.AUTHORITY_resource);
        while(itera.hasNext()){
            currentResource = rdf.getResource(itera.nextStatement().getSubject().toString());

            Element authority = new Element("Authority", ns);
            authority.setAttribute("id", currentResource.getProperty(RDFS.label).getObject().toString());

            Element name = new Element("Name", ns);
            name.setText(currentResource.getProperty(SKOS.prefLabel).getObject().toString());
            authority.addContent(name);

            Element companyNumber = new Element("CompanyNumber", ns);
            companyNumber.setText(currentResource.getProperty(SKOS.notation).getObject().toString());
            authority.addContent(companyNumber);

            Element organisationType = new Element("OrganisationType", ns);
            organisationType.setText("authority");
            authority.addContent(organisationType);

            organisations.addContent(authority);
        }

        current.addContent(organisations);
        System.out.println("Organizations mapped");

        return current;
    }

    private Element mapScheduleStopPoints(Element current, Namespace ns){
        Element scheduledStopPoints = new Element("scheduledStopPoints", ns);

        StmtIterator iterator = rdf.listStatements(null, RDF.type, Namespaces.SCHEDULE_STOP_POINT_resource);
        Resource currentResource;
        while(iterator.hasNext()){
            currentResource = rdf.getResource(iterator.nextStatement().getSubject().toString());

            Element ScheduledStopPoint = new Element("ScheduledStopPoint", ns);
            String id = currentResource.getProperty(RDFS.label).getObject().toString();
            ScheduledStopPoint.setAttribute("id", id);

            Element name = new Element("Name", ns);
            name.setText(currentResource.getProperty(SchemaDO.name).getObject().toString());
            ScheduledStopPoint.addContent(name);

            Element ValidityBetween = new Element("ValidityBetween", ns);
            StmtIterator from_iterator = rdf.listStatements(
                    rdf.createResource(Namespaces.JOURNEYS+"/Resource/ScheduledStopPoint/"+id),
                    Namespaces.hasValidity,
                    (String) null
            );
            Resource currentResource_2;
            while(from_iterator.hasNext()){
                currentResource_2 = rdf.getResource(from_iterator.nextStatement().getSubject().toString());

                Element FromDate = new Element("FromDate", ns);
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

    private Element mapRoutePoints(Element current, Namespace ns){
        Element routePoints = new Element("routePoints", ns);

        StmtIterator iterator = rdf.listStatements(null, RDF.type, Namespaces.ROUTE_POINT_resource);
        Resource currentResource;
        while(iterator.hasNext()){
            currentResource = rdf.getResource(iterator.nextStatement().getSubject().toString());

            Element RoutePoint = new Element("RoutePoint", ns);
            RoutePoint.setAttribute("id", currentResource.getProperty(RDFS.label).getObject().toString());

            Element projections = new Element("projections", ns);
            Element PointProjection = new Element("PointProjection", ns);
            PointProjection.setAttribute(
                    "id",
                    currentResource.getProperty(Namespaces.hasPointProjection).getObject().toString()
            );

            Element ProjectedPointRef = new Element("ProjectedPointRef", ns);
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

    private Element mapDestinadionDisplays(Element current, Namespace ns){
        Element destinationDisplays = new Element("destinationDisplays", ns);

        StmtIterator iterator = rdf.listStatements(null, RDF.type, Namespaces.DESTINATION_DISPLAY_resource);
        Resource currentResource;
        while(iterator.hasNext()){
            currentResource = rdf.getResource(iterator.nextStatement().getSubject().toString());

            Element DestinationDisplay = new Element("DestinationDisplay", ns);
            DestinationDisplay.setAttribute("id", currentResource.getProperty(RDFS.label).getObject().toString());

            Element FrontText = new Element("FrontText", ns);
            FrontText.setText(currentResource.getProperty(Namespaces.frontText).getObject().toString());

            DestinationDisplay.addContent(FrontText);
            destinationDisplays.addContent(DestinationDisplay);
        }

        current.addContent(destinationDisplays);
        return current;
    }
}



















