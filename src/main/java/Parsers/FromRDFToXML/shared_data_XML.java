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

import java.util.Random;

/**
 * Class which encapsules all the methods to generate the shared file(s)
 */
public class shared_data_XML {
    public Model turtle;
    public Document xml;
    public Random random;

    /**
     * Class constructor
     *
     * @param turtle   Model which contains the information
     * @param xml      Document where the data it is going to be parsed and then saved
     */
    public shared_data_XML(Model turtle, Document xml) {
        this.turtle = turtle;
        this.xml = xml;
        random = new Random();
    }

    /**
     * Generates a template for the xml file. As well as the root.
     *
     * @return Document with the basic schema
     */
    public Document initSharedXML(){
        // Root
        Element root = new Element("PublicationDelivery", Namespace.getNamespace("http://www.netex.org.uk/netex"));
        root.setAttribute("version", "1.13:NO-NeTEx-networktimetable:1.3");
        xml.setRootElement(root);
        root.addNamespaceDeclaration(Namespace.getNamespace("gis", "http://www.opengis.net/gml/3.2"));
        root.addNamespaceDeclaration(Namespace.getNamespace("siri", "http://www.siri.org.uk/siri"));

        Namespace ns = root.getNamespace();

        Element PublicationTimestamp = new Element("PublicationTimestamp", ns);
        PublicationTimestamp.setText("2022-05-26T00:05:52.614");
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

    /**
     * Generates the rest of the specified schema
     *
     * @return The full parsed document (xml)
     */
    public Document generate(){
        initSharedXML();
        Element root = this.xml.getRootElement();

        Namespace ns = root.getNamespace();

        Element dataObjects = new Element("dataObjects", ns);

        Element CompositeFrame = new Element("CompositeFrame", ns);
        CompositeFrame.setAttribute("id", String.valueOf(Math.abs(random.nextInt())));
        CompositeFrame.setAttribute("created", "2022-04-08T10:32:34.29");
        CompositeFrame.setAttribute("version", "1");

        Element validityConditions = new Element("validityConditions", ns);
        mapValidityConditions(validityConditions, ns);

        Element codespaces = new Element("codespaces", ns);
        mapCodespaces(codespaces, ns);

        Element FrameDefaults = new Element("FrameDefaults", ns);
        mapFrameDefaults(FrameDefaults, ns);

        Element frames = new Element("frames", ns);
        Element ResourceFrame = new Element("ResourceFrame", ns);
        ResourceFrame.setAttribute("id", String.valueOf(Math.abs(random.nextInt())));
        ResourceFrame.setAttribute("version", "1");
        mapOrganizations(ResourceFrame, ns);
        Element ServiceFrame = new Element("ServiceFrame", ns);
        ServiceFrame.setAttribute("id", String.valueOf(Math.abs(random.nextInt())));
        ServiceFrame.setAttribute("version", "1");
        mapNetwork(ServiceFrame, ns);
        mapScheduleStopPoints(ServiceFrame, ns);
        mapRoutePoints(ServiceFrame, ns);
        mapDestinadionDisplays(ServiceFrame, ns);
        mapStopAssignments(ServiceFrame, ns);
        Element serviceLinks = new Element("serviceLinks", ns);
        ServiceFrame.addContent(serviceLinks);

        Element ServiceCalendarFrame = new Element("ServiceCalendarFrame", ns);
        ServiceCalendarFrame.setAttribute("id", String.valueOf(Math.abs(random.nextInt())));
        ServiceCalendarFrame.setAttribute("version", "1");
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

    /**
     * Map defaults. TODO
     *
     * @param current   Actual element which is being build
     * @param ns        Namespace of the document
     * @return
     */
    private Element mapFrameDefaults(Element current, Namespace ns){
        Element DefaultLocale = new Element("DefaultLocale", ns);

        Element TimeZone = new Element("TimeZone", ns);
        TimeZone.setText("Europe/Oslo");
        DefaultLocale.addContent(TimeZone);

        Element DefaultLanguage = new Element("DefaultLanguage", ns);
        DefaultLanguage.setText("no");
        DefaultLocale.addContent(DefaultLanguage);

        current.addContent(DefaultLocale);
        return current;
    }

    /**
     * Map codespaces. TODO
     *
     * @param current   Actual element which is being build
     * @param ns        Namespace of the document
     * @return
     */
    private Element mapCodespaces(Element current, Namespace ns){
        String[] cod = new String[]{"ost", "nsr"};

        for(String c:cod){
            Element Codespace = new Element("Codespace", ns);
            Codespace.setAttribute("id", c);

            Element Xmlns = new Element("Xmlns", ns);
            Xmlns.setText(c.toUpperCase());
            Codespace.addContent(Xmlns);

            Element XmlnsUrl = new Element("XmlnsUrl", ns);
            XmlnsUrl.setText("http://www.rutebanken.org/ns/" + c);
            Codespace.addContent(XmlnsUrl);

            current.addContent(Codespace);
        }

        return current;
    }

    /**
     * Map ValidityConditions. TODO
     *
     * @param current   Actual element which is being build
     * @param ns        Namespace of the document
     * @return
     */
    private Element mapValidityConditions(Element current, Namespace ns){
        Element AvailabilityCondition = new Element("AvailabilityCondition", ns);
        AvailabilityCondition.setAttribute("id", String.valueOf(Math.abs(random.nextInt())));
        AvailabilityCondition.setAttribute("version", "1");

        Element FromDate = new Element("FromDate", ns);
        FromDate.setText("2022-05-23T00:00:00");
        AvailabilityCondition.addContent(FromDate);
        Element ToDate = new Element("ToDate", ns);
        ToDate.setText("2023-05-25T00:00:00");
        AvailabilityCondition.addContent(ToDate);

        current.addContent(AvailabilityCondition);
        return current;
    }

    /**
     * Map StopAssignments
     *
     * @param serviceFrame   Actual element which is being build
     * @param ns             Namespace of the document
     * @return
     */
    private Element mapStopAssignments(Element serviceFrame, Namespace ns) {
        Element stopAssignments = new Element("stopAssignments", ns);

        StmtIterator iterator = turtle.listStatements(null, RDF.type, Namespaces.PASSENGER_STOP_ASSIGNMENT_resource);
        while (iterator.hasNext()) {
            Resource passenger_resource = turtle.getResource(iterator.nextStatement().getSubject().toString());
            Element PassengerStopAssignment = new Element("PassengerStopAssignment", ns);
            PassengerStopAssignment.setAttribute("id", passenger_resource.getProperty(RDFS.label).getObject().toString());
            PassengerStopAssignment.setAttribute("order", passenger_resource.getProperty(Namespaces.order).getObject().toString());
            NetexParserFromRDF.mapVersion(passenger_resource, PassengerStopAssignment);

            Element ScheduledStopPointRef = new Element("ScheduledStopPointRef", ns);
            ScheduledStopPointRef.setAttribute("ref",
                    passenger_resource.getProperty(Namespaces.forStopPoint).getProperty(RDFS.label).getObject().toString()
            );
            NetexParserFromRDF.mapVersion(
                    turtle.getResource(passenger_resource.getProperty(Namespaces.forStopPoint).getObject().toString()),
                    ScheduledStopPointRef
            );

            Statement quay_aux = passenger_resource.getProperty(Namespaces.forQuay);
            if(quay_aux != null){
                Element QuayRef = new Element("QuayRef", ns);
                QuayRef.setAttribute("ref", quay_aux.getProperty(RDFS.label).getObject().toString());
                PassengerStopAssignment.addContent(QuayRef);
            }

            PassengerStopAssignment.addContent(ScheduledStopPointRef);
            stopAssignments.addContent(PassengerStopAssignment);
        }

        serviceFrame.addContent(stopAssignments);
        return stopAssignments;
    }

    /**
     * Map Network
     *
     * @param serviceFrame   Actual element which is being build
     * @param ns            Namespace of the document
     * @return
     */
    private Element mapNetwork(Element serviceFrame, Namespace ns) {

        StmtIterator iterator = turtle.listStatements(null, RDF.type, Namespaces.NETWORK_resource);
        while (iterator.hasNext()){
            Resource network_resource = turtle.getResource(iterator.nextStatement().getSubject().toString());
            Element Network = new Element("Network", ns);
            Network.setAttribute("id", network_resource.getProperty(RDFS.label).getObject().toString());
            NetexParserFromRDF.mapVersion(network_resource, Network);

            Element Name = new Element("Name", ns);
            Name.setText(network_resource.getProperty(SchemaDO.name).getObject().toString());
            Network.addContent(Name);

            Element AuthorityRef = new Element("AuthorityRef", ns);
            AuthorityRef.setAttribute("ref", network_resource.getProperty(Namespaces.authorizedBy).getProperty(RDFS.label).getObject().toString());
            NetexParserFromRDF.mapVersion(
                    turtle.getResource(network_resource.getProperty(Namespaces.authorizedBy).getObject().toString()),
                    AuthorityRef
            );
            Network.addContent(AuthorityRef);

            Element groupsOfLines = new Element("groupsOfLines", ns);
            StmtIterator iterator1 = turtle.listStatements(network_resource, Namespaces.networkMadeUpOf, (Resource) null);
            while (iterator1.hasNext()){
                Resource group_resource = turtle.getResource(iterator1.nextStatement().getObject().toString());
                Element GroupOfLines = new Element("GroupOfLines", ns);
                NetexParserFromRDF.mapVersion(group_resource, GroupOfLines);
                GroupOfLines.setAttribute("id", group_resource.getProperty(RDFS.label).getObject().toString());

                Element Name1 = new Element("Name", ns);
                Name1.setText(group_resource.getProperty(SchemaDO.name).getObject().toString());
                GroupOfLines.addContent(Name1);

                groupsOfLines.addContent(GroupOfLines);
            }

            Network.addContent(groupsOfLines);
            serviceFrame.addContent(Network);
        }

        return null;
    }

    /**
     * Map DayTypeAssignment
     *
     * @param serviceCalendarFrame   Actual element which is being build
     * @param ns                     Namespace of the document
     * @return
     */
    private Element mapDayTypeAssignments(Element serviceCalendarFrame, Namespace ns) {
        Element dayTypeAssignments = new Element("dayTypeAssignments", ns);

        StmtIterator iterator = turtle.listStatements(null, RDF.type, Namespaces.DAY_TYPE_ASSIGNMENT_resource);
        while (iterator.hasNext()){
            Resource dayTypeAssigment_resouce = turtle.getResource(iterator.nextStatement().getSubject().toString());
            Element DayTypeAssignment = new Element("DayTypeAssignment", ns);
            DayTypeAssignment.setAttribute("id", dayTypeAssigment_resouce.getProperty(RDFS.label).getObject().toString());
            DayTypeAssignment.setAttribute("order", dayTypeAssigment_resouce.getProperty(Namespaces.order).getObject().toString());
            NetexParserFromRDF.mapVersion(dayTypeAssigment_resouce, DayTypeAssignment);

            Element DayTypeRef = new Element("DayTypeRef", ns);
            DayTypeRef.setAttribute("ref",
                    dayTypeAssigment_resouce.getProperty(Namespaces.specifying).getProperty(RDFS.label).getObject().toString()
            );
            NetexParserFromRDF.mapVersion(
                    turtle.getResource(dayTypeAssigment_resouce.getProperty(Namespaces.specifying).getObject().toString()),
                    DayTypeRef
            );
            DayTypeAssignment.addContent(DayTypeRef);

            Statement date_stmt = dayTypeAssigment_resouce.getProperty(Namespaces.date);
            if(date_stmt != null){
                Element Date = new Element("Date", ns);
                Date.setText(date_stmt.getObject().toString());
                DayTypeAssignment.addContent(Date);
            }

            Statement OperatingPeriodRef_stmt = dayTypeAssigment_resouce.getProperty(Namespaces.definedBy);
            if(OperatingPeriodRef_stmt != null){
                Element OperatingPeriodRef = new Element("OperatingPeriodRef", ns);
                OperatingPeriodRef.setAttribute("ref", OperatingPeriodRef_stmt.getProperty(RDFS.label).getObject().toString());
                DayTypeAssignment.addContent(OperatingPeriodRef);
            }

            Statement isAvailable_stmt = dayTypeAssigment_resouce.getProperty(Namespaces.isAvailable);
            if(isAvailable_stmt != null){
                Element isAvailable = new Element("isAvailable", ns);
                isAvailable.setText(isAvailable_stmt.getObject().toString());
                DayTypeAssignment.addContent(isAvailable);
            }

            dayTypeAssignments.addContent(DayTypeAssignment);
        }

        serviceCalendarFrame.addContent(dayTypeAssignments);
        System.out.println("DayTypeAssignments mapped");
        return serviceCalendarFrame;
    }

    /**
     * Map OperatingPeriod
     *
     * @param serviceCalendarFrame   Actual element which is being build
     * @param ns                     Namespace of the document
     * @return
     */
    private Element mapOperatingPeriods(Element serviceCalendarFrame, Namespace ns) {
        Element operatingPeriods = new Element("operatingPeriods", ns);

        StmtIterator iterator = turtle.listStatements(null, RDF.type, Namespaces.OPERATING_PERIOD_resource);
        while(iterator.hasNext()){
            Resource operatingPeriod_resource = turtle.getResource(iterator.nextStatement().getSubject().toString());
            Element OperatingPeriod = new Element("OperatingPeriod", ns);
            OperatingPeriod.setAttribute("id", operatingPeriod_resource.getProperty(RDFS.label).getObject().toString());
            NetexParserFromRDF.mapVersion(operatingPeriod_resource, OperatingPeriod);

            Element FromDate = new Element("FromDate", ns);
            FromDate.setText(operatingPeriod_resource.getProperty(Namespaces.startingAt).getObject().toString());
            OperatingPeriod.addContent(FromDate);

            Element ToDate = new Element("ToDate", ns);
            ToDate.setText(operatingPeriod_resource.getProperty(Namespaces.endingAt).getObject().toString());
            OperatingPeriod.addContent(ToDate);

            operatingPeriods.addContent(OperatingPeriod);
        }

        serviceCalendarFrame.addContent(operatingPeriods);
        System.out.println("OperatingPeriods mapped");
        return serviceCalendarFrame;
    }

    /**
     * Map DayType
     *
     * @param serviceCalendarFrame   Actual element which is being build
     * @param ns                     Namespace of the document
     * @return
     */
    private Element mapDayType(Element serviceCalendarFrame, Namespace ns) {
        Element dayTypes = new Element("dayTypes", ns);
        StmtIterator iterator = turtle.listStatements(null, RDF.type, Namespaces.DAY_TYPE_resource);
        while(iterator.hasNext()){
            Resource daytype_resource = turtle.getResource(iterator.nextStatement().getSubject().toString());

            Element DayType = new Element("DayType", ns);
            DayType.setAttribute("id", daytype_resource.getProperty(RDFS.label).getObject().toString());
            NetexParserFromRDF.mapVersion(daytype_resource, DayType);

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

    /**
     * Map Organizations
     *
     * @param current   Actual element which is being build
     * @param ns        Namespace of the document
     * @return
     */
    private Element mapOrganizations(Element current, Namespace ns){
        Element organisations = new Element("organisations", ns);

        // Operator
        StmtIterator itera = turtle.listStatements(null, RDF.type, Namespaces.OPERATOR_resource);
        Resource currentResource;
        while(itera.hasNext()){
            currentResource = turtle.getResource(itera.nextStatement().getSubject().toString());

            Element operator = new Element("Operator", ns);
            operator.setAttribute("id", currentResource.getProperty(RDFS.label).getObject().toString());
            NetexParserFromRDF.mapVersion(currentResource, operator);

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
        itera = turtle.listStatements(null, RDF.type, Namespaces.AUTHORITY_resource);
        while(itera.hasNext()){
            currentResource = turtle.getResource(itera.nextStatement().getSubject().toString());

            Element authority = new Element("Authority", ns);
            authority.setAttribute("id", currentResource.getProperty(RDFS.label).getObject().toString());
            NetexParserFromRDF.mapVersion(currentResource, authority);

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

    /**
     * Map ScheduledStopPoints
     *
     * @param current   Actual element which is being build
     * @param ns        Namespace of the document
     * @return
     */
    private Element mapScheduleStopPoints(Element current, Namespace ns){
        Element scheduledStopPoints = new Element("scheduledStopPoints", ns);

        StmtIterator iterator = turtle.listStatements(null, RDF.type, Namespaces.SCHEDULE_STOP_POINT_resource);
        Resource currentResource;
        while(iterator.hasNext()){
            currentResource = turtle.getResource(iterator.nextStatement().getSubject().toString());

            Element ScheduledStopPoint = new Element("ScheduledStopPoint", ns);
            String id = currentResource.getProperty(RDFS.label).getObject().toString();
            ScheduledStopPoint.setAttribute("id", id);
            NetexParserFromRDF.mapVersion(currentResource, ScheduledStopPoint);

            Element name = new Element("Name", ns);
            name.setText(currentResource.getProperty(SchemaDO.name).getObject().toString());
            ScheduledStopPoint.addContent(name);

            Element ValidityBetween = new Element("ValidityBetween", ns);
            StmtIterator from_iterator = turtle.listStatements(
                    turtle.createResource(Namespaces.JOURNEYS+"/Resource/ScheduledStopPoint/"+id),
                    Namespaces.hasValidity,
                    (String) null
            );
            Resource currentResource_2;
            Boolean validity_non_empty = from_iterator.hasNext();
            while(from_iterator.hasNext()){
                currentResource_2 = turtle.getResource(from_iterator.nextStatement().getSubject().toString());

                Element FromDate = new Element("FromDate", ns);
                FromDate.setText(currentResource_2.getProperty(Namespaces.hasValidity).getObject().toString());

                ValidityBetween.addContent(FromDate);
            }

            if(validity_non_empty)
                ScheduledStopPoint.addContent(ValidityBetween);
            scheduledStopPoints.addContent(ScheduledStopPoint);
        }

        current.addContent(scheduledStopPoints);

        System.out.println("ScheduleStopPoints mapped");

        return current;
    }

    /**
     * Map RoutePoints
     *
     * @param current   Actual element which is being build
     * @param ns        Namespace of the document
     * @return
     */
    private Element mapRoutePoints(Element current, Namespace ns){
        Element routePoints = new Element("routePoints", ns);

        StmtIterator iterator = turtle.listStatements(null, RDF.type, Namespaces.ROUTE_POINT_resource);
        Resource currentResource;
        while(iterator.hasNext()){
            currentResource = turtle.getResource(iterator.nextStatement().getSubject().toString());

            Element RoutePoint = new Element("RoutePoint", ns);
            RoutePoint.setAttribute("id", currentResource.getProperty(RDFS.label).getObject().toString());
            NetexParserFromRDF.mapVersion(currentResource, RoutePoint);

            Element projections = new Element("projections", ns);
            Element PointProjection = new Element("PointProjection", ns);
            PointProjection.setAttribute(
                    "id",
                    currentResource.getProperty(Namespaces.hasPointProjection).getObject().toString()
            );
            PointProjection.setAttribute("version", "271");

            Element ProjectedPointRef = new Element("ProjectedPointRef", ns);
            ProjectedPointRef.setAttribute(
                    "ref",
                    currentResource.getProperty(Namespaces.scheduledStopPoint).getObject().toString()
            );
            ProjectedPointRef.setAttribute("version", "1");

            PointProjection.addContent(ProjectedPointRef);
            projections.addContent(PointProjection);
            RoutePoint.addContent(projections);
            routePoints.addContent(RoutePoint);
        }

        current.addContent(routePoints);
        System.out.println("RoutePoints mapped");
        return current;
    }

    /**
     * Map DestinationDisplays
     *
     * @param current   Actual element which is being build
     * @param ns        Namespace of the document
     * @return
     */
    private Element mapDestinadionDisplays(Element current, Namespace ns){
        Element destinationDisplays = new Element("destinationDisplays", ns);

        StmtIterator iterator = turtle.listStatements(null, RDF.type, Namespaces.DESTINATION_DISPLAY_resource);
        Resource currentResource;
        while(iterator.hasNext()){
            currentResource = turtle.getResource(iterator.nextStatement().getSubject().toString());

            Element DestinationDisplay = new Element("DestinationDisplay", ns);
            DestinationDisplay.setAttribute("id", currentResource.getProperty(RDFS.label).getObject().toString());
            DestinationDisplay.setAttribute("version", "1");

            Element FrontText = new Element("FrontText", ns);
            FrontText.setText(currentResource.getProperty(Namespaces.frontText).getObject().toString());

            DestinationDisplay.addContent(FrontText);
            destinationDisplays.addContent(DestinationDisplay);
        }

        current.addContent(destinationDisplays);
        return current;
    }
}



















