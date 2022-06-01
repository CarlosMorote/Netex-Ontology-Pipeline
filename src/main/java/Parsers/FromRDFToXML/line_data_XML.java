package Parsers.FromRDFToXML;

import DataManager.Ontology.Namespaces;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SchemaDO;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

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

        Element PublicationTimestamp = new Element("PublicationTimestamp");
        PublicationTimestamp.setText(LocalDateTime.now().toString()); // WARNING: PUEDE NO SER CORRECTO
        root.addContent(PublicationTimestamp);

        Element Description = new Element("Description");
        Description.setText(line_resource.getProperty(SchemaDO.name).getObject().toString());
        root.addContent(Description);

        return xml;
    }

    public Document generate(){
        initLineData();
        Element root = xml.getRootElement();

        Element dataObjects = new Element("dataObjects");
        root.addContent(dataObjects);

        Element CompositeFrame = new Element("CompositeFrame");
        CompositeFrame.setAttribute("created", LocalDateTime.now().toString());
        CompositeFrame.setAttribute("id", "");
        dataObjects.addContent(CompositeFrame);

        Element validityConditions = new Element("ValidityConditions");
        CompositeFrame.addContent(validityConditions);

        Element codespaces = new Element("codespaces");
        CompositeFrame.addContent(codespaces);

        Element FrameDefaults = new Element("FrameDefaults");
        CompositeFrame.addContent(FrameDefaults);

        Element frames = new Element("frames");
        mapServiceFrame(frames);
        CompositeFrame.addContent(frames);

        return xml;
    }

    private Element mapServiceFrame(Element current){
        Element ServiceFrame = new Element("ServiceFrame");
        ServiceFrame.setAttribute("id", "");

        mapRoutes(ServiceFrame);
        mapLines(ServiceFrame);
        mapJourneyPattern(ServiceFrame);

        current.addContent(ServiceFrame);
        System.out.println("ServiceFrame mapped");
        return current;
    }

    private Element mapRoutes(Element current){
        Element routes = new Element("routes");

        Resource route;
        StmtIterator iterator = rdf.listStatements(null, Namespaces.onLine, line_resource.getURI());
        while(iterator.hasNext()){
            route = rdf.getResource(iterator.nextStatement().getSubject().toString());

            Element Route = new Element("Route");
            Route.setAttribute("id", route.getProperty(RDFS.label).getObject().toString());

            Element Name = new Element("Name");
            Name.setText(route.getProperty(SchemaDO.name).getObject().toString());
            Route.addContent(Name);

            Element ShortName = new Element("ShortName");
            ShortName.setText(route.getProperty(SchemaDO.additionalName).getObject().toString());
            Route.addContent(ShortName);

            Element LineRef = new Element("LineRef");
            LineRef.setAttribute("ref", line_resource.getProperty(RDFS.label).getObject().toString() );
            Route.addContent(LineRef);

            Element DirectionType = new Element("DirectionType");
            DirectionType.setText(route.getProperty(Namespaces.allowedLineDirections).getObject().toString());
            Route.addContent(DirectionType);

            Element pointsInSequence = new Element("pointsInSequence");

            StmtIterator iterator1 = rdf.listStatements(null, Namespaces.madeUpOf, route);
            Resource PoR;
            while(iterator1.hasNext()){
                Element PointOnRoute = new Element("PointOnRoute");

                PoR = iterator1.nextStatement().getSubject();
                PointOnRoute.setAttribute("id",
                        PoR.getProperty(RDFS.label).getObject().toString()
                );
                PointOnRoute.setAttribute("order",
                        PoR.getProperty(Namespaces.order).getObject().toString()
                );
                Element RoutePointRef = new Element("RoutePointRef");
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
        System.out.println("Routes mapped");
        return current;
    }

    private Element mapLines(Element current){
        return current;
    }

    private Element mapJourneyPattern(Element current){
        return current;
    }
}















