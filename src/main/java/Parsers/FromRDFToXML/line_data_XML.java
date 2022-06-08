package Parsers.FromRDFToXML;

import DataManager.Ontology.Namespaces;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SchemaDO;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.onebusaway.gtfs.model.Stop;

import java.time.LocalDateTime;

public class line_data_XML {

    public Model rdf;
    public Document xml;
    public Resource line_resource;

    public line_data_XML(Model rdf, Document xml, Resource line_resource) {
        this.rdf = rdf;
        this.xml = xml;
        this.line_resource = line_resource;
    }

    public Document initLineData(){
        Element root = new Element("PublicationDelivery", Namespace.getNamespace("http://www.netex.org.uk/netex"));
        xml.setRootElement(root);
        //root.addNamespaceDeclaration(Namespace.getNamespace("http://www.netex.org.uk/netex"));
        root.addNamespaceDeclaration(Namespace.getNamespace("gis", "http://www.opengis.net/gml/3.2"));
        root.addNamespaceDeclaration(Namespace.getNamespace("siri", "http://www.siri.org.uk/siri"));

        Namespace ns = root.getNamespace();

        Element PublicationTimestamp = new Element("PublicationTimestamp", ns);
        PublicationTimestamp.setText(LocalDateTime.now().toString()); // WARNING: PUEDE NO SER CORRECTO
        root.addContent(PublicationTimestamp);

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
        CompositeFrame.setAttribute("created", LocalDateTime.now().toString());
        CompositeFrame.setAttribute("id", "");
        dataObjects.addContent(CompositeFrame);

        Element validityConditions = new Element("ValidityConditions", ns);
        CompositeFrame.addContent(validityConditions);

        Element codespaces = new Element("codespaces", ns);
        CompositeFrame.addContent(codespaces);

        Element FrameDefaults = new Element("FrameDefaults", ns);
        CompositeFrame.addContent(FrameDefaults);

        Element frames = new Element("frames", ns);
        mapServiceFrame(frames, ns);
        CompositeFrame.addContent(frames);

        return xml;
    }

    private Element mapServiceFrame(Element current, Namespace ns){
        Element ServiceFrame = new Element("ServiceFrame", ns);
        ServiceFrame.setAttribute("id", "");

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

            Element Name = new Element("Name", ns);
            Name.setText(route.getProperty(SchemaDO.name).getObject().toString());
            Route.addContent(Name);

            Element ShortName = new Element("ShortName", ns);
            ShortName.setText(route.getProperty(SchemaDO.additionalName).getObject().toString());
            Route.addContent(ShortName);

            Element LineRef = new Element("LineRef", ns);
            LineRef.setAttribute("ref", line_resource.getProperty(RDFS.label).getObject().toString() );
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

        Element Name = new Element("Name", ns);
        Name.setText(line_resource.getProperty(SchemaDO.name).getObject().toString());
        Line.addContent(Name);

        Element TransportMode = new Element("TransportMode", ns);
        TransportMode.setText(line_resource.getProperty(Namespaces.hasTransportMode).getObject().toString());
        Line.addContent(TransportMode);

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

            Element Name = new Element("Name", ns);
            Name.setText(journey_resource.getProperty(SchemaDO.name).getObject().toString());
            JourneyPattern.addContent(Name);

            Element RouteRef = new Element("RouteRef", ns);
            RouteRef.setAttribute("ref", route.getProperty(RDFS.label).getObject().toString());
            JourneyPattern.addContent(RouteRef);

            //pointsInSequence
            Element pointsInSequence = new Element("pointsInSequence", ns);

            StmtIterator points = rdf.listStatements(journey_resource, Namespaces.journeyPatternMadeUpOf, (Resource) null);
            while(points.hasNext()){
                Resource point_resource = rdf.getResource(points.nextStatement().getObject().toString());
                Element StopPointInJourneyPattern = new Element("StopPointInJourneyPattern", ns);
                StopPointInJourneyPattern.setAttribute("order", point_resource.getProperty(Namespaces.order).getObject().toString());
                StopPointInJourneyPattern.setAttribute("id", point_resource.getProperty(RDFS.label).getObject().toString());

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

                // TODO: ServiceLinkRef

                linksInSequence.addContent(ServiceLinkInJourneyPattern);
            }

            JourneyPattern.addContent(linksInSequence);

            journeyPatterns.addContent(JourneyPattern);
        }

        current.addContent(journeyPatterns);
        return current;
    }
}















