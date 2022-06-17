package Parsers.FromRDFToXML;

import DataManager.Ontology.Namespaces;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SchemaDO;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.rutebanken.netex.model.ServiceLink;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

public class line_data_XML {

    public Model rdf;
    public Document xml;
    public Resource line_resource;
    public Random random;
    public DateFormat dateFormat;

    public line_data_XML(Model rdf, Document xml, Resource line_resource) {
        this.rdf = rdf;
        this.xml = xml;
        this.line_resource = line_resource;
        this.random = new Random();
        dateFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public Document initLineData(){
        Element root = new Element("PublicationDelivery", Namespace.getNamespace("http://www.netex.org.uk/netex"));
        root.setAttribute("version", "1.13:NO-NeTEx-networktimetable:1.3");
        xml.setRootElement(root);
        //root.addNamespaceDeclaration(Namespace.getNamespace("http://www.netex.org.uk/netex"));
        root.addNamespaceDeclaration(Namespace.getNamespace("gis", "http://www.opengis.net/gml/3.2"));
        root.addNamespaceDeclaration(Namespace.getNamespace("siri", "http://www.siri.org.uk/siri"));

        Namespace ns = root.getNamespace();

        Element PublicationTimestamp = new Element("PublicationTimestamp", ns);
        PublicationTimestamp.setText("2022-05-26T00:05:35.012");
        //PublicationTimestamp.setText(LocalDateTime.now().minusDays(3).toString()); // WARNING: PUEDE NO SER CORRECTO
        root.addContent(PublicationTimestamp);

        Element ParticipantRef = new Element("ParticipantRef", ns);
        ParticipantRef.setText("RB");
        root.addContent(ParticipantRef);

        Element Description = new Element("Description", ns);
        Description.setText(line_resource.getProperty(SchemaDO.name).getObject().toString());
        root.addContent(Description);

        return xml;
    }

    public Document generate(){
        initLineData();
        Element root = xml.getRootElement();

        Namespace ns = root.getNamespace();

        Element dataObjects = new Element("dataObjects", ns);
        root.addContent(dataObjects);

        Element CompositeFrame = new Element("CompositeFrame", ns);
        //CompositeFrame.setAttribute("created", LocalDateTime.now().minusDays(3).toString());
        CompositeFrame.setAttribute("created", "2022-04-08T10:32:34.29");
        CompositeFrame.setAttribute("id", String.valueOf(Math.abs(random.nextInt())));
        CompositeFrame.setAttribute("version", "1");
        dataObjects.addContent(CompositeFrame);

        Element validityConditions = new Element("validityConditions", ns);
        mapValidityConditions(validityConditions, ns);
        CompositeFrame.addContent(validityConditions);

        Element codespaces = new Element("codespaces", ns);
        mapCodespaces(codespaces, ns);
        CompositeFrame.addContent(codespaces);

        Element FrameDefaults = new Element("FrameDefaults", ns);
        mapFrameDefaults(FrameDefaults, ns);
        CompositeFrame.addContent(FrameDefaults);

        Element frames = new Element("frames", ns);
        mapServiceFrame(frames, ns);
        mapTimetableFrame(frames, ns);
        CompositeFrame.addContent(frames);

        return xml;
    }

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

    private Element mapTimetableFrame(Element frames, Namespace ns) {
        Element TimetableFrame = new Element("TimetableFrame", ns);
        TimetableFrame.setAttribute("id", String.valueOf(Math.abs(random.nextInt())));
        TimetableFrame.setAttribute("version", "1");

        Element vehicleJourneys = new Element("vehicleJourneys", ns);

        //StmtIterator iterator = rdf.listStatements(null, RDF.type, Namespaces.VEHICLE_JOURNEY_resource);
        StmtIterator iterator = rdf.listStatements(null, Namespaces.onLine, line_resource);
        while (iterator.hasNext()){
            Resource serviceJourney_resource = rdf.getResource(iterator.nextStatement().getSubject().toString());
            Element ServiceJourney = new Element("ServiceJourney", ns);
            ServiceJourney.setAttribute("id", serviceJourney_resource.getProperty(RDFS.label).getObject().toString());
            ServiceJourney.setAttribute("publication", "public");
            NetexParserFromRDF.mapVersion(serviceJourney_resource, ServiceJourney);

            Element Name = new Element("Name", ns);
            Name.setText(serviceJourney_resource.getProperty(SchemaDO.name).getObject().toString());
            ServiceJourney.addContent(Name);

            Element PrivateCode = new Element("PrivateCode", ns);
            PrivateCode.setText(serviceJourney_resource.getProperty(Namespaces.hasPrivateCode).getObject().toString());
            ServiceJourney.addContent(PrivateCode);

            Element dayTypes = new Element("dayTypes", ns);
            Element DayTypeRef = new Element("DayTypeRef", ns);
            DayTypeRef.setAttribute("ref", serviceJourney_resource.getProperty(Namespaces.workedOn).getProperty(RDFS.label).getObject().toString());
            dayTypes.addContent(DayTypeRef);
            ServiceJourney.addContent(dayTypes);

            Statement journeyPattern_stmt = serviceJourney_resource.getProperty(Namespaces.followsJourneyPattern);
            if(journeyPattern_stmt != null){
                Element JourneyPatternRef = new Element("JourneyPatternRef", ns);
                JourneyPatternRef.setAttribute("ref", journeyPattern_stmt.getProperty(RDFS.label).getObject().toString());
                NetexParserFromRDF.mapVersion(rdf.getResource(journeyPattern_stmt.getObject().toString()), JourneyPatternRef);
                ServiceJourney.addContent(JourneyPatternRef);
            }

            Statement line_stms = serviceJourney_resource.getProperty(Namespaces.onLine);
            if(line_stms != null){
                Element LineRef = new Element("LineRef", ns);
                LineRef.setAttribute("ref", line_stms.getProperty(RDFS.label).getObject().toString());
                NetexParserFromRDF.mapVersion(rdf.getResource(line_stms.getObject().toString()),LineRef);
                ServiceJourney.addContent(LineRef);
            }

            Statement operator_stms = serviceJourney_resource.getProperty(Namespaces.runBy);
            if(operator_stms != null){
                Element OperatorRef = new Element("OperatorRef", ns);
                OperatorRef.setAttribute("ref", operator_stms.getProperty(RDFS.label).getObject().toString());
                ServiceJourney.addContent(OperatorRef);
            }

            Map<Long, Element> timetabledMap = new HashMap<Long, Element>();
            int c = 0;
            Element passingTimes = new Element("passingTimes", ns);
            StmtIterator iterator1 = rdf.listStatements(serviceJourney_resource, Namespaces.passesAt, (Resource) null);
            while(iterator1.hasNext()){
                String tim = null;
                Resource TimetabledPassingTime_resource = rdf.getResource(iterator1.nextStatement().getObject().toString());
                Element TimetabledPassingTime = new Element("TimetabledPassingTime", ns);
                TimetabledPassingTime.setAttribute("id", TimetabledPassingTime_resource.getProperty(RDFS.label).getObject().toString());
                NetexParserFromRDF.mapVersion(TimetabledPassingTime_resource, TimetabledPassingTime);

                Statement departureTime_stmt = TimetabledPassingTime_resource.getProperty(Namespaces.departureTime);
                if(departureTime_stmt != null){
                    tim = departureTime_stmt.getObject().toString();

                    Element DepartureTime = new Element("DepartureTime", ns);
                    DepartureTime.setText(tim);
                    TimetabledPassingTime.addContent(DepartureTime);
                }

                Statement arrivalTime_stmt = TimetabledPassingTime_resource.getProperty(Namespaces.arrivalTime);
                if(arrivalTime_stmt != null){
                    tim = arrivalTime_stmt.getObject().toString();

                    Element ArrivalTime = new Element("ArrivalTime", ns);
                    ArrivalTime.setText(tim);
                    TimetabledPassingTime.addContent(ArrivalTime);
                }

                Statement offset_stmt = TimetabledPassingTime_resource.getProperty(Namespaces.departureOffset);
                if(offset_stmt != null) {
                    Element DepartureDayOffset = new Element("DepartureDayOffset", ns);
                    DepartureDayOffset.setText(offset_stmt.getObject().toString());
                    TimetabledPassingTime.addContent(DepartureDayOffset);
                }

                Statement offset_arrival_stmt = TimetabledPassingTime_resource.getProperty(Namespaces.arrivalOffset);
                if(offset_arrival_stmt != null) {
                    Element ArrivalDayOffset = new Element("ArrivalDayOffset", ns);
                    ArrivalDayOffset.setText(offset_arrival_stmt.getObject().toString());
                    TimetabledPassingTime.addContent(ArrivalDayOffset);
                }

                Element StopPointInJourneyPatternRef = new Element("StopPointInJourneyPatternRef", ns);
                StopPointInJourneyPatternRef.setAttribute("ref", TimetabledPassingTime_resource.getProperty(Namespaces.passesAt).getProperty(RDFS.label).getObject().toString());
                NetexParserFromRDF.mapVersion(rdf.getResource(TimetabledPassingTime_resource.getProperty(Namespaces.passesAt).getObject().toString()),
                        StopPointInJourneyPatternRef);
                TimetabledPassingTime.addContent(StopPointInJourneyPatternRef);

                if(tim != null) {
                    try {
                        timetabledMap.put(dateFormat.parse(tim).getTime() / 1000L + c, TimetabledPassingTime);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }

                c++;

                //passingTimes.addContent(TimetabledPassingTime);
            }

            SortedSet<Long> keys = new TreeSet<>(timetabledMap.keySet());
            for (Long key : keys)
                passingTimes.addContent(timetabledMap.get(key));


            ServiceJourney.addContent(passingTimes);
            vehicleJourneys.addContent(ServiceJourney);
        }

        TimetableFrame.addContent(vehicleJourneys);
        frames.addContent(TimetableFrame);
        return frames;
    }

    private Element mapServiceFrame(Element current, Namespace ns){
        Element ServiceFrame = new Element("ServiceFrame", ns);
        ServiceFrame.setAttribute("id", String.valueOf(Math.abs(random.nextInt())));
        ServiceFrame.setAttribute("version", "1");

        mapRoutes(ServiceFrame, ns);
        mapLines(ServiceFrame, ns);
        mapJourneyPattern(ServiceFrame, ns);

        current.addContent(ServiceFrame);
        return current;
    }

    private Element mapRoutes(Element current, Namespace ns){
        Element routes = new Element("routes", ns);

        Resource route;
        StmtIterator iterator = rdf.listStatements(null, Namespaces.onLine, line_resource.getURI());
        while(iterator.hasNext()){
            route = rdf.getResource(iterator.nextStatement().getSubject().toString());

            Element Route = new Element("Route", ns);
            Route.setAttribute("id", route.getProperty(RDFS.label).getObject().toString());
            NetexParserFromRDF.mapVersion(route, Route);

            Element Name = new Element("Name", ns);
            Name.setText(route.getProperty(SchemaDO.name).getObject().toString());
            Route.addContent(Name);

            Element ShortName = new Element("ShortName", ns);
            ShortName.setText(route.getProperty(SchemaDO.additionalName).getObject().toString());
            Route.addContent(ShortName);

            Element LineRef = new Element("LineRef", ns);
            LineRef.setAttribute("ref", line_resource.getProperty(RDFS.label).getObject().toString());
            NetexParserFromRDF.mapVersion(line_resource, LineRef);
            Route.addContent(LineRef);

            Element DirectionType = new Element("DirectionType", ns);
            DirectionType.setText(route.getProperty(Namespaces.allowedLineDirections).getObject().toString());
            Route.addContent(DirectionType);

            Element pointsInSequence = new Element("pointsInSequence", ns);

            StmtIterator iterator1 = rdf.listStatements(null, Namespaces.madeUpOf, route);
            Resource PoR;
            while(iterator1.hasNext()){
                Element PointOnRoute = new Element("PointOnRoute", ns);

                PoR = iterator1.nextStatement().getSubject();
                PointOnRoute.setAttribute("id",
                        PoR.getProperty(RDFS.label).getObject().toString()
                );
                PointOnRoute.setAttribute("order",
                        PoR.getProperty(Namespaces.order).getObject().toString()
                );
                NetexParserFromRDF.mapVersion(PoR, PointOnRoute);
                Element RoutePointRef = new Element("RoutePointRef", ns);
                RoutePointRef.setAttribute("ref",
                        rdf.listStatements(null, Namespaces.aViewOf, PoR).nextStatement().getSubject().getProperty(RDFS.label).getObject().toString()
                );
                PointOnRoute.addContent(RoutePointRef);
                pointsInSequence.addContent(PointOnRoute);
            }

            Route.addContent(pointsInSequence);
            routes.addContent(Route);
        }

        current.addContent(routes);
        return current;
    }

    private Element mapLines(Element current, Namespace ns){
        Element lines = new Element("lines", ns);

        Element Line = new Element("Line", ns);
        Line.setAttribute("id", line_resource.getProperty(RDFS.label).getObject().toString());
        NetexParserFromRDF.mapVersion(line_resource, Line);

        Element Name = new Element("Name", ns);
        Name.setText(line_resource.getProperty(SchemaDO.name).getObject().toString());
        Line.addContent(Name);

        Element TransportMode = new Element("TransportMode", ns);
        TransportMode.setText(line_resource.getProperty(Namespaces.hasTransportMode).getObject().toString());
        Line.addContent(TransportMode);

        Element TransportSubmode = new Element("TransportSubmode", ns);
        Element BusSubmode = new Element("BusSubmode", ns);
        BusSubmode.setText("localBus");
        TransportSubmode.addContent(BusSubmode);
        Line.addContent(TransportSubmode);

        Element PublicCode = new Element("PublicCode", ns);
        PublicCode.setText(line_resource.getProperty(Namespaces.hasPublicCode).getObject().toString());
        Line.addContent(PublicCode);

        Element PrivateCode = new Element("PrivateCode", ns);
        PrivateCode.setText(line_resource.getProperty(Namespaces.hasPrivateCode).getObject().toString());
        Line.addContent(PrivateCode);

        Statement runBy = line_resource.getProperty(Namespaces.runBy);
        if(runBy != null){
            Element OperatorRef = new Element("OperatorRef", ns);
            OperatorRef.setAttribute("ref",
                    runBy.getProperty(RDFS.label).getObject().toString()
            );
            Line.addContent(OperatorRef);
        }

        Statement representedGroup = line_resource.getProperty(Namespaces.representedByGroup);
        if(representedGroup != null){
            Element RepresentedByGroupRef = new Element("RepresentedByGroupRef", ns);
            RepresentedByGroupRef.setAttribute("ref",
                    representedGroup.getProperty(RDFS.label).getObject().toString());
            Line.addContent(RepresentedByGroupRef);
        }

        lines.addContent(Line);
        current.addContent(lines);
        return current;
    }

    private Element mapJourneyPattern(Element current, Namespace ns){
        Element journeyPatterns = new Element("journeyPatterns", ns);

        StmtIterator routes = rdf.listStatements(null, Namespaces.onLine, line_resource.getURI());
        Resource route;
        while(routes.hasNext()){
            route = routes.nextStatement().getSubject();

            Element JourneyPattern = new Element("JourneyPattern", ns);
            Resource journey_resource = rdf.listStatements(null, Namespaces.onRoute, route.getURI()).nextStatement().getSubject();
            JourneyPattern.setAttribute("id", journey_resource.getProperty(RDFS.label).getObject().toString());
            NetexParserFromRDF.mapVersion(journey_resource, JourneyPattern);

            Element Name = new Element("Name", ns);
            Name.setText(journey_resource.getProperty(SchemaDO.name).getObject().toString());
            JourneyPattern.addContent(Name);

            Element RouteRef = new Element("RouteRef", ns);
            RouteRef.setAttribute("ref", route.getProperty(RDFS.label).getObject().toString());
            NetexParserFromRDF.mapVersion(route, RouteRef);
            JourneyPattern.addContent(RouteRef);

            //pointsInSequence
            Element pointsInSequence = new Element("pointsInSequence", ns);

            StmtIterator points = rdf.listStatements(journey_resource, Namespaces.journeyPatternMadeUpOf, (Resource) null);
            while(points.hasNext()){
                Resource point_resource = rdf.getResource(points.nextStatement().getObject().toString());
                Element StopPointInJourneyPattern = new Element("StopPointInJourneyPattern", ns);
                StopPointInJourneyPattern.setAttribute("order", point_resource.getProperty(Namespaces.order).getObject().toString());
                StopPointInJourneyPattern.setAttribute("id", point_resource.getProperty(RDFS.label).getObject().toString());
                NetexParserFromRDF.mapVersion(point_resource, StopPointInJourneyPattern);

                Element ScheduledStopPointRef = new Element("ScheduledStopPointRef", ns);
                ScheduledStopPointRef.setAttribute("ref",
                        ((Resource) point_resource.getProperty(Namespaces.scheduledStopPoint).getObject()).getProperty(RDFS.label).getObject().toString()
                );

                Statement forAlighting_statement = point_resource.getProperty(Namespaces.forAlighting);
                if(forAlighting_statement != null){
                    Element ForAlighting = new Element("ForAlighting", ns);
                    ForAlighting.setText(forAlighting_statement.getObject().toString());
                    StopPointInJourneyPattern.addContent(ForAlighting);
                }

                Statement forBoarding_stmt = point_resource.getProperty(Namespaces.forBoarding);
                if(forBoarding_stmt != null){
                    Element ForBoarding = new Element("ForBoarding", ns);
                    ForBoarding.setText(forBoarding_stmt.getObject().toString());
                    StopPointInJourneyPattern.addContent(ForBoarding);
                }

                Statement destinationDisplayRef_statement = point_resource.getProperty(Namespaces.hasDestinationDisplay);
                if(destinationDisplayRef_statement != null){
                    Element DestinationDisplayRef = new Element("DestinationDisplayRef", ns);
                    DestinationDisplayRef.setAttribute("ref",
                            ((Resource)destinationDisplayRef_statement.getObject()).getProperty(RDFS.label).getObject().toString()
                    );
                    StopPointInJourneyPattern.addContent(DestinationDisplayRef);
                }

                StopPointInJourneyPattern.addContent(ScheduledStopPointRef);
                pointsInSequence.addContent(StopPointInJourneyPattern);
            }

            JourneyPattern.addContent(pointsInSequence);

            Element linksInSequence = new Element("linksInSequence", ns);

            StmtIterator links = rdf.listStatements(journey_resource, Namespaces.hasPointsInJourneyPattern, (Resource) null);
            while(links.hasNext()){
                Resource serviceLink_resource = rdf.getResource(links.nextStatement().getObject().toString());
                Element ServiceLinkInJourneyPattern = new Element("ServiceLinkInJourneyPattern", ns);
                ServiceLinkInJourneyPattern.setAttribute("order", serviceLink_resource.getProperty(Namespaces.order).getObject().toString());
                ServiceLinkInJourneyPattern.setAttribute("id", serviceLink_resource.getProperty(RDFS.label).getObject().toString());
                NetexParserFromRDF.mapVersion(serviceLink_resource, ServiceLinkInJourneyPattern);

                Element ServiceLinkRef = new Element("ServiceLinkRef", ns);
                ServiceLinkRef.setAttribute("ref",
                        serviceLink_resource.getProperty(Namespaces.aViewOf).getProperty(RDFS.label).getObject().toString());

                ServiceLinkInJourneyPattern.addContent(ServiceLinkRef);
                linksInSequence.addContent(ServiceLinkInJourneyPattern);
            }

            JourneyPattern.addContent(linksInSequence);

            journeyPatterns.addContent(JourneyPattern);
        }

        current.addContent(journeyPatterns);
        return current;
    }
}















