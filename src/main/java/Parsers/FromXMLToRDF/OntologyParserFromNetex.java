package Parsers.FromXMLToRDF;

import DataManager.Netex.NetexManager;
import DataManager.Ontology.Namespaces;
import DataManager.Ontology.RDFManager;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.*;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.lang.reflect.InvocationTargetException;
import java.time.format.DateTimeFormatter;
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
                "Authority", "Operator", "ScheduledStopPoint", "JourneyPattern","RoutePoint", "Route", "Line", "OperatingPeriod", "DayType", "ServiceJourney"
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
        scheduledStopPoint_resource.addProperty(SchemaDO.name, scheduledStopPoint.getName().getValue());

        List<ValidBetween> validity = scheduledStopPoint.getValidBetween();
        if(!validity.isEmpty()){
            for(ValidBetween v: validity){
                if(v.getFromDate() != null)
                    scheduledStopPoint_resource.addProperty(Namespaces.hasValidity, v.getFromDate().toString());
            }
        }

        return scheduledStopPoint_resource;
    }

    @Override
    public Resource mapJourneyPattern(JourneyPattern journeyPattern) {
        String id = journeyPattern.getId();
        Resource journeyPattern_resource = this.rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/JourneyPattern/"+id);
        this.rdfManager.addType(journeyPattern_resource, Namespaces.SERVICE_JOURNEY_PATTERN_resource);
        journeyPattern_resource.addProperty(RDFS.label, id);
        journeyPattern_resource.addProperty(SchemaDO.name, journeyPattern.getName().getValue());
        journeyPattern_resource.addProperty(Namespaces.onRoute, Namespaces.JOURNEYS+"/Resource/Route/"+journeyPattern.getRouteRef().getRef());

        journeyPattern.getPointsInSequence()
                .getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern()
                .forEach((point) -> {
                    mapStopPointInJourneyPattern((StopPointInJourneyPattern) point, journeyPattern_resource);
                });

        journeyPattern.getLinksInSequence()
                .getServiceLinkInJourneyPatternOrTimingLinkInJourneyPattern()
                .forEach((link) -> {
                    mapServiceLinkInJourneyPattern((ServiceLinkInJourneyPattern_VersionedChildStructure) link, journeyPattern_resource);
                });


        return journeyPattern_resource;
    }

    @Override
    public Resource mapStopPointInJourneyPattern(StopPointInJourneyPattern point, Resource journeyPattern_resource) {
        String id_point = point.getId();
        Resource stopPointInJourneyPattern_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/StopPointsInJourneyPattern/"+id_point);
        journeyPattern_resource.addProperty(Namespaces.journeyPatternMadeUpOf, stopPointInJourneyPattern_resource);
        rdfManager.addType(stopPointInJourneyPattern_resource, Namespaces.STOP_POINT_IN_JOURNEY_PATTERN_resource);
        stopPointInJourneyPattern_resource.addProperty(RDFS.label, id_point);
        stopPointInJourneyPattern_resource.addProperty(Namespaces.order, point.getOrder().toString());

        Boolean forAlighting = point.isForAlighting();
        if(forAlighting != null)
            stopPointInJourneyPattern_resource.addProperty(Namespaces.forAlighting, forAlighting.toString());

        JAXBElement<? extends ScheduledStopPointRefStructure> scheduledStopPoint = point.getScheduledStopPointRef();
        if(scheduledStopPoint != null){
            Resource stop_resource = this.rdfManager.rdf.getResource(Namespaces.JOURNEYS+"/Resource/ScheduledStopPoint/"+scheduledStopPoint.getValue().getRef());
            stopPointInJourneyPattern_resource.addProperty(Namespaces.scheduledStopPoint, stop_resource);
        }

        DestinationDisplayRefStructure destinationDisplayRef = point.getDestinationDisplayRef();
        if(destinationDisplayRef != null){
            String destination_id = destinationDisplayRef.getRef();
            Resource destinationRef_resource = rdfManager.rdf.createResource(Namespaces.COMMONS + "/Resource/DestinationDisplay/"+destination_id);
            rdfManager.addType(destinationRef_resource, Namespaces.DESTINATION_DISPLAY_resource);
            destinationRef_resource.addProperty(RDFS.label, destination_id);
            DestinationDisplay dest = netexManager.netex.getDestinationDisplayIndex().get(destination_id);
            destinationRef_resource.addProperty(Namespaces.frontText, dest.getFrontText().getValue());

            stopPointInJourneyPattern_resource.addProperty(Namespaces.hasDestinationDisplay, destinationRef_resource);
        }

        return stopPointInJourneyPattern_resource;
    }

    @Override
    public Resource mapServiceLinkInJourneyPattern(ServiceLinkInJourneyPattern_VersionedChildStructure link, Resource journeyPattern_resource) {
        String link_id = link.getId();
        Resource link_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS + "/Resource/ServiceLinkInJourneyPattern/"+link_id);
        link_resource.addProperty(RDFS.label, link_id);
        rdfManager.addType(link_resource, Namespaces.LINK_SEQUENCE_resource);
        link_resource.addProperty(Namespaces.order, link.getOrder().toString());

        Resource point_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS + "/Resource/ServiceLink/"+link.getServiceLinkRef().getRef());
        point_resource.addProperty(RDFS.label, link.getServiceLinkRef().getRef());

        link_resource.addProperty(Namespaces.aViewOf, point_resource); //No ontologia (ontology)
        journeyPattern_resource.addProperty(Namespaces.hasPointsInJourneyPattern, link_resource);

        return link_resource;
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
                    pointOnRoute_resource.addProperty(Namespaces.order, pointOnRoute.getOrder().toString());
                    pointOnRoute_resource.addProperty(Namespaces.madeUpOf, route_resource);

                    String id_routePoint = pointOnRoute.getPointRef().getValue().getRef();
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

        GroupOfLinesRefStructure group = line.getRepresentedByGroupRef();
        if(group != null){
            Resource group_resource = rdfManager.rdf.createResource(Namespaces.COMMONS+"/Resource/GroupOfLines/"+group.getRef());
            group_resource.addProperty(RDFS.label, group.getRef());
            line_resource.addProperty(
                    Namespaces.representedByGroup,
                    group_resource
            );
        }

        return line_resource;
    }

    @Override
    public Resource mapServiceJourney(ServiceJourney serviceJourney) {
        String id_serviceJourney = serviceJourney.getId();
        Resource serviceJourney_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS + "/Resource/VehicleJourney/" + id_serviceJourney);
        serviceJourney_resource.addProperty(RDF.type, Namespaces.VEHICLE_JOURNEY_resource);
        serviceJourney_resource.addProperty(RDFS.label, id_serviceJourney);
        serviceJourney_resource.addProperty(SchemaDO.name, serviceJourney.getName().getValue());
        serviceJourney_resource.addProperty(Namespaces.hasPrivateCode, serviceJourney.getPrivateCode().getValue());

        serviceJourney_resource.addProperty(Namespaces.workedOn,
                rdfManager.rdf.getResource(Namespaces.JOURNEYS+"/Resource/DayType/"+serviceJourney.getDayTypes().getDayTypeRef().get(0).getValue().getRef())
        );

        serviceJourney_resource.addProperty(Namespaces.followsJourneyPattern,
                rdfManager.rdf.getResource(Namespaces.JOURNEYS+"/Resource/JourneyPattern/"+serviceJourney.getJourneyPatternRef().getValue().getRef())
        );

        serviceJourney_resource.addProperty(Namespaces.onLine, // Relacción no existente en ontologia pero necesaria
                rdfManager.rdf.getResource(Namespaces.JOURNEYS+"/Resource/Line/"+serviceJourney.getLineRef().getValue().getRef())
        );

        // No en ontología pero existian casos donde el operador de la linea no coincidia con el del viaje
        serviceJourney_resource.addProperty(Namespaces.runBy,
                rdfManager.rdf.getResource(Namespaces.ORGANISATIONS + "/Resource/Operator/"+ serviceJourney.getOperatorRef().getRef())
        );

        serviceJourney.getPassingTimes().getTimetabledPassingTime().forEach(
                (timetabledPassingTime) -> {
                    String id_timetable = timetabledPassingTime.getId();
                    Resource timetable_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/TimetabledPassingTime/"+id_timetable);
                    timetable_resource.addProperty(RDF.type, Namespaces.TIMETABLED_PASSING_TIME_resource);
                    timetable_resource.addProperty(RDFS.label, id_timetable);
                    if(timetabledPassingTime.getDepartureTime() != null)
                        timetable_resource.addProperty(Namespaces.departureTime,
                                timetabledPassingTime.getDepartureTime().format(DateTimeFormatter.ISO_LOCAL_TIME)
                        );
                    if(timetabledPassingTime.getArrivalTime() != null)
                        timetable_resource.addProperty(Namespaces.arrivalTime,
                                timetabledPassingTime.getArrivalTime().format(DateTimeFormatter.ISO_LOCAL_TIME)
                        );
                    if(timetabledPassingTime.getPointInJourneyPatternRef() != null)
                        timetable_resource.addProperty(Namespaces.passesAt,
                            rdfManager.rdf.getResource(Namespaces.JOURNEYS + "/Resource/StopPointsInJourneyPattern/" + timetabledPassingTime.getPointInJourneyPatternRef().getValue().getRef())
                        );
                    serviceJourney_resource.addProperty(Namespaces.passesAt, timetable_resource);
                }
        );

        return serviceJourney_resource;
    }

    @Override
    public Resource mapOperatingPeriod(OperatingPeriod operatingPeriod) {
        String id_operatingPeriod = operatingPeriod.getId();
        Resource operatingPeriod_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS + "/Resource/OperatingPeriod/"+id_operatingPeriod);
        operatingPeriod_resource.addProperty(RDF.type, Namespaces.OPERATING_PERIOD_resource);
        operatingPeriod_resource.addProperty(RDFS.label, id_operatingPeriod);
        operatingPeriod_resource.addProperty(Namespaces.startingAt, operatingPeriod.getFromDate().format(DateTimeFormatter.ISO_DATE_TIME));
        operatingPeriod_resource.addProperty(Namespaces.endingAt, operatingPeriod.getToDate().format(DateTimeFormatter.ISO_DATE_TIME));

        return operatingPeriod_resource;
    }

    @Override
    public Resource mapDayType(DayType dayType) {
        String id_dayType = dayType.getId();
        Resource dayType_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/DayType/"+id_dayType);
        dayType_resource.addProperty(RDFS.label, id_dayType);
        dayType_resource.addProperty(RDF.type, Namespaces.DAY_TYPE_resource);
        if(dayType.getProperties() != null)
            dayType_resource.addProperty(Namespaces.daysOfWeek,
                dayType.getProperties().getPropertyOfDay().get(0).getDaysOfWeek().stream().map(x -> String.valueOf(x)).collect(Collectors.joining(" ","",""))
            );

        netexManager.netex.getDayTypeAssignmentsByDayTypeIdIndex().get(id_dayType).forEach(
                (dayTypeAssignment) -> {
                    String id_dayTypeA = dayTypeAssignment.getId();
                    Resource dayTypeAssignment_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS + "/Resource/DayTypeAssignment/" + id_dayTypeA);
                    dayTypeAssignment_resource.addProperty(RDFS.label, id_dayTypeA);
                    dayTypeAssignment_resource.addProperty(RDF.type, Namespaces.DAY_TYPE_ASSIGNMENT_resource);
                    dayTypeAssignment_resource.addProperty(Namespaces.specifying, dayType_resource);
                    if(dayTypeAssignment.getDate() != null)
                        dayTypeAssignment_resource.addProperty(Namespaces.date, dayTypeAssignment.getDate().format(DateTimeFormatter.ISO_DATE));
                    dayTypeAssignment_resource.addProperty(Namespaces.order, dayTypeAssignment.getOrder().toString());
                    if(dayTypeAssignment.getOperatingPeriodRef() != null)
                        dayTypeAssignment_resource.addProperty(Namespaces.definedBy,
                            rdfManager.rdf.getResource(Namespaces.JOURNEYS + "/Resource/OperatingPeriod/"+dayTypeAssignment.getOperatingPeriodRef().getRef())
                        );
                }
        );

        return dayType_resource;
    }
}






















