package Parsers;

import DataManager.Netex.NetexManager;
import DataManager.Ontology.Namespaces;
import DataManager.Ontology.RDFManager;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.*;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class OntologyParserFromNetex implements OntologyParserInterface {

    private RDFManager rdfManager;
    private NetexManager netexManager;
    private String[] classesToCast;
    private Collection information;

    public OntologyParserFromNetex(RDFManager RDFManager, NetexManager netexManager) {
        this.rdfManager = RDFManager;
        this.netexManager = netexManager;
        this.classesToCast = new String[]{
                "Authority", "Operator", "ScheduledStopPoint", "JourneyPattern","RoutePoint", "Route", "Line"
        };
    }

    public void castNetexToOntology(){
        for (String cls: this.classesToCast) {
            information = this.netexManager.getData(cls);
            information.stream().map(this::parse).collect(Collectors.toList());
            System.out.println(cls + " Mapped");
        }
    }


    @Override
    public Object parse(Object o){
        String method = String.format("map%s", o.getClass().getSimpleName());

        try {
            return this.getClass().getMethod(method, o.getClass()).invoke(this, o);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Resource mapAuthority(Authority authority) {
        String id = authority.getId();
        Resource authority_resource = this.rdfManager.rdf.createResource(Namespaces.CORE+"/Resource/Authority/"+id);
        this.rdfManager.addType(authority_resource, Namespaces.AUTHORITY_resource);
        authority_resource.addProperty(RDFS.label, id);
        authority_resource.addProperty(SKOS.notation, authority.getCompanyNumber());
        authority_resource.addProperty(SKOS.prefLabel, authority.getName().getValue());

        return authority_resource;
    }

    @Override
    public Resource mapOperator(Operator operator) {
        String id = operator.getId();
        Resource operator_resource = this.rdfManager.rdf.createResource(Namespaces.ORGANISATIONS+"/Resource/Operator/"+id);
        this.rdfManager.addType(operator_resource, Namespaces.OPERATOR_resource);
        operator_resource.addProperty(RDFS.label, id);
        operator_resource.addProperty(SKOS.notation, operator.getCompanyNumber());
        operator_resource.addProperty(VCARD4.hasName, operator.getName().getValue());
        operator_resource.addProperty(VCARD4.hasURL, operator.getCustomerServiceContactDetails().getUrl());

        return operator_resource;
    }

    @Override
    public Resource mapScheduledStopPoint(ScheduledStopPoint scheduledStopPoint) {
        String id = scheduledStopPoint.getId();
        Resource scheduledStopPoint_resource = this.rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/ScheduledStopPoint/"+id);
        this.rdfManager.addType(scheduledStopPoint_resource, Namespaces.SCHEDULE_STOP_POINT_resource);
        scheduledStopPoint_resource.addProperty(RDFS.label, id);
        scheduledStopPoint_resource.addLiteral(SchemaDO.name, scheduledStopPoint.getName().getValue());

        List<ValidBetween> validity = scheduledStopPoint.getValidBetween();
        if(!validity.isEmpty()){
            for(ValidBetween v: validity){
                if(v.getFromDate() != null)
                    scheduledStopPoint_resource.addLiteral(Namespaces.hasValidity, v.getFromDate().toString());
            }
        }

        return scheduledStopPoint_resource;
    }

    @Override
    public Resource mapJourneyPattern(JourneyPattern journeyPattern) {
        String id = journeyPattern.getId();
        Resource journeyPattern_resource = this.rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/ServiceJourneyPattern/"+id);
        this.rdfManager.addType(journeyPattern_resource, Namespaces.SERVICE_JOURNEY_PATTERN_resource);
        journeyPattern_resource.addProperty(RDFS.label, id);
        journeyPattern_resource.addProperty(SchemaDO.name, journeyPattern.getName().getValue());
        journeyPattern_resource.addProperty(Namespaces.onRoute, Namespaces.JOURNEYS+"/Resource/Route/"+journeyPattern.getRouteRef().getRef());

        journeyPattern.getPointsInSequence()
                .getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern()
                .forEach((point) -> {
                    mapStopPointInJourneyPattern((StopPointInJourneyPattern) point, journeyPattern_resource);
                });


        return journeyPattern_resource;
    }

    @Override
    public Resource mapStopPointInJourneyPattern(StopPointInJourneyPattern point, Resource journeyPattern_resource) {
        String id_point = point.getId();
        Resource stopPointInJourneyPattern_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/StopPointsInJourneyPattern/"+id_point);
        journeyPattern_resource.addProperty(Namespaces.journeyPatternMadeUpOf, stopPointInJourneyPattern_resource);
        rdfManager.addType(stopPointInJourneyPattern_resource, Namespaces.STOP_POINT_IN_JOURNEY_PATTERN_resource);

        Boolean forAlighting = point.isForAlighting();
        if(forAlighting != null)
            stopPointInJourneyPattern_resource.addLiteral(Namespaces.forAlighting, forAlighting);

        JAXBElement<? extends ScheduledStopPointRefStructure> scheduledStopPoint = point.getScheduledStopPointRef();
        if(scheduledStopPoint != null){
            Resource stop_resource = this.rdfManager.rdf.getResource(Namespaces.JOURNEYS+"/Resource/ScheduledStopPoint/"+id_point);
            stopPointInJourneyPattern_resource.addProperty(Namespaces.scheduledStopPoint, stop_resource);
        }

        return stopPointInJourneyPattern_resource;
    }

    @Override
    public Resource mapRoute(Route route) {
        String id_route = route.getId();
        Resource route_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/Route/"+id_route);
        rdfManager.addType(route_resource, Namespaces.ROUTE_resource);
        route_resource.addProperty(RDFS.label, id_route);
        route_resource.addProperty(SchemaDO.name, route.getName().getValue());
        route_resource.addProperty(SchemaDO.additionalName, route.getShortName().getValue());
        route_resource.addProperty(Namespaces.onLine, Namespaces.JOURNEYS+"/Resource/Line/"+route.getLineRef().getValue().getRef());
        route_resource.addProperty(Namespaces.allowedLineDirections, route.getDirectionType().value());

        route.getPointsInSequence().getPointOnRoute().forEach(
                (pointOnRoute) -> {
                    String id_pointOnRoute = pointOnRoute.getId();
                    Resource pointOnRoute_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/PointOnRoute/"+id_pointOnRoute);
                    rdfManager.addType(pointOnRoute_resource, Namespaces.POINT_ON_ROUTE_resource);
                    pointOnRoute_resource.addProperty(RDFS.label, id_pointOnRoute);
                    pointOnRoute_resource.addLiteral(Namespaces.order, pointOnRoute.getOrder());
                    pointOnRoute_resource.addProperty(Namespaces.madeUpOf, route_resource);

                    String id_routePoint = pointOnRoute.getPointRef().getValue().getRef();
                    //Resource routePoint_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/RoutePoint/"+id_routePoint);
                    //rdfManager.addType(routePoint_resource, Namespaces.ROUTE_POINT_resource);
                    //routePoint_resource.addProperty(RDFS.label, id_routePoint);
                    Resource routePoint_resource = rdfManager.rdf.getResource(Namespaces.JOURNEYS+"/Resource/RoutePoint/"+id_routePoint);
                    routePoint_resource.addProperty(Namespaces.aViewOf, pointOnRoute_resource);
                }
        );

        return route_resource;
    }

    @Override
    public Resource mapRoutePoint(RoutePoint routePoint) {
        String id_routePoint = routePoint.getId();
        Resource routePoint_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/RoutePoint/"+id_routePoint);
        rdfManager.addType(routePoint_resource, Namespaces.ROUTE_POINT_resource);
        routePoint_resource.addProperty(RDFS.label, id_routePoint);

        PointProjection projections = (PointProjection) routePoint.getProjections().getProjectionRefOrProjection().get(0).getValue();
        routePoint_resource.addProperty(Namespaces.hasPointProjection, projections.getId());
        routePoint_resource.addProperty(
                Namespaces.scheduledStopPoint,
                projections.getProjectedPointRef().getRef()
        );

        return routePoint_resource;
    }

    @Override
    public Resource mapLine(Line line) {
        String id_line = line.getId();
        Resource line_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/Line/"+id_line);
        rdfManager.addType(line_resource, Namespaces.LINE_resource);
        line_resource.addProperty(RDFS.label, id_line);
        line_resource.addProperty(SchemaDO.name, line.getName().getValue());
        line_resource.addProperty(Namespaces.hasTransportMode, line.getTransportMode().value());
        line_resource.addProperty(Namespaces.hasPublicCode, line.getPublicCode());
        line_resource.addProperty(Namespaces.hasPrivateCode, line.getPrivateCode().getValue());

        OperatorRefStructure op = line.getOperatorRef();
        if(op != null)
            line_resource.addProperty(
                    Namespaces.runBy,
                    rdfManager.rdf.getResource(Namespaces.ORGANISATIONS+"/Resource/Operator/"+op.getRef())
            );

        return line_resource;
    }
}






















