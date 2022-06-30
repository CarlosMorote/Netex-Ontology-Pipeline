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

/**
 * Class which implements the actual logic and interfaces of OntologyParserInterface
 *
 * @see Parsers.FromXMLToRDF.OntologyParserInterface
 */
public class OntologyParserFromNetex implements OntologyParserInterface {

    private RDFManager rdfManager;
    private NetexManager netexManager;
    private String[] classesToCast;
    private Collection information;

    /**
     * Class constructor. Here it is defined the classes that are going to be mapped
     *
     * @param RDFManager    Manager of linked data
     * @param netexManager  Manager of NeTEx data
     */
    public OntologyParserFromNetex(RDFManager RDFManager, NetexManager netexManager) {
        this.rdfManager = RDFManager;
        this.netexManager = netexManager;
        this.classesToCast = new String[]{
                "Quay",
                "Authority",
                "Operator",
                "ScheduledStopPoint",
                "JourneyPattern",
                "RoutePoint",
                "Route",
                "Line",
                "OperatingPeriod",
                "DayType",
                "ServiceJourney",
                "Network"
        };
    }

    /**
     * Iterates over all the defined classes and iterates over each individual
     */
    public void castNetexToOntology(){
        for (String cls: this.classesToCast) {
            information = this.netexManager.getData(cls);
            information.stream().map(this::parse).collect(Collectors.toList());
            System.out.println(cls + " Mapped");
        }
    }

    /**
     * Receive an instance of <b>any</b> class and cast it into linked data
     *
     * @param o
     * @return
     */
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

    /**
     * Map the version of each object
     *
     * @param current   Current linked data that it is being parsed
     * @param obj       Netex object which contains the version
     * @return          A new resource with the version added
     */
    private Resource mapVersion(Resource current, EntityInVersionStructure obj){
        String v = obj.getVersion();

        if(v != null && v != "")
            current.addProperty(Namespaces.version, v);

        return current;
    }

    /**
     * Map a Quay instance
     *
     * @param quay  An instance of the class <i>Quay</i>
     * @return      A Resource that captures the same data into linked data
     */
    @Override
    public Resource mapQuay(Quay quay) {
        String id_quay = quay.getId();
        Resource quay_resource = rdfManager.rdf.createResource(Namespaces.FACILITIES+"/Resource/Quay/"+id_quay);
        quay_resource.addProperty(RDFS.label, id_quay);
        quay_resource.addProperty(RDF.type, Namespaces.QUAY_resource);
        mapVersion(quay_resource, quay);

        return quay_resource;
    }

    /**
     * Map an Authority instance
     *
     * @param authority    An instance of the class <i>Authority</i>
     * @return             A Resource that captures the same data into linked data
     */
    @Override
    public Resource mapAuthority(Authority authority) {
        String id = authority.getId();
        Resource authority_resource = this.rdfManager.rdf.createResource(Namespaces.CORE+"/Resource/Authority/"+id);
        this.rdfManager.addType(authority_resource, Namespaces.AUTHORITY_resource);
        authority_resource.addProperty(RDFS.label, id);
        authority_resource.addProperty(SKOS.notation, authority.getCompanyNumber());
        authority_resource.addProperty(SKOS.prefLabel, authority.getName().getValue());
        mapVersion(authority_resource, authority);

        return authority_resource;
    }

    /**
     * Map an Operator instance
     *
     * @param operator      An instance of the class <i>Operator</i>
     * @return              A Resource that captures the same data into linked data
     */
    @Override
    public Resource mapOperator(Operator operator) {
        String id = operator.getId();
        Resource operator_resource = this.rdfManager.rdf.createResource(Namespaces.ORGANISATIONS+"/Resource/Operator/"+id);
        this.rdfManager.addType(operator_resource, Namespaces.OPERATOR_resource);
        operator_resource.addProperty(RDFS.label, id);
        operator_resource.addProperty(SKOS.notation, operator.getCompanyNumber());
        operator_resource.addProperty(VCARD4.hasName, operator.getName().getValue());
        operator_resource.addProperty(VCARD4.hasURL, operator.getCustomerServiceContactDetails().getUrl());
        mapVersion(operator_resource, operator);

        return operator_resource;
    }

    /**
     * Map a ScheduledStopPoint instance
     *
     * @param scheduledStopPoint    An instance of the class <i>ScheduledStopPoint</i>
     * @return                      A Resource that captures the same data into linked data
     */
    @Override
    public Resource mapScheduledStopPoint(ScheduledStopPoint scheduledStopPoint) {
        String id = scheduledStopPoint.getId();
        Resource scheduledStopPoint_resource = this.rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/ScheduledStopPoint/"+id);
        this.rdfManager.addType(scheduledStopPoint_resource, Namespaces.SCHEDULE_STOP_POINT_resource);
        scheduledStopPoint_resource.addProperty(RDFS.label, id);
        scheduledStopPoint_resource.addProperty(SchemaDO.name, scheduledStopPoint.getName().getValue());
        mapVersion(scheduledStopPoint_resource, scheduledStopPoint);


        List<ValidBetween> validity = scheduledStopPoint.getValidBetween();
        if(!validity.isEmpty()){
            for(ValidBetween v: validity){
                if(v.getFromDate() != null)
                    scheduledStopPoint_resource.addProperty(Namespaces.hasValidity, v.getFromDate().toString());
            }
        }

        // PassengerStopAssigment
        netexManager.netex.getPassengerStopAssignmentsByStopPointRefIndex().get(id).forEach(
                (passengerStopAssignment -> {
                    String id_passenger = passengerStopAssignment.getId();
                    Resource passenger_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS + "/Resource/PassengerStopAssignment/" + id_passenger);
                    passenger_resource.addProperty(RDF.type, Namespaces.PASSENGER_STOP_ASSIGNMENT_resource);
                    passenger_resource.addProperty(RDFS.label, id_passenger);
                    passenger_resource.addProperty(Namespaces.order, passengerStopAssignment.getOrder().toString());
                    passenger_resource.addProperty(Namespaces.forStopPoint, scheduledStopPoint_resource);
                    mapVersion(passenger_resource, passengerStopAssignment);
                    if(passengerStopAssignment.getQuayRef() != null)
                        passenger_resource.addProperty(Namespaces.forQuay, rdfManager.rdf.getResource(Namespaces.FACILITIES + "/Resource/Quay/" + passengerStopAssignment.getQuayRef().getRef()));
                })
        );

        return scheduledStopPoint_resource;
    }

    /**
     * Map a JourneyPattern instance
     *
     * @param journeyPattern    An instance of the class <i>JourneyPattern</i>
     * @return                  A Resource that captures the same data into linked data
     */
    @Override
    public Resource mapJourneyPattern(JourneyPattern journeyPattern) {
        String id = journeyPattern.getId();
        Resource journeyPattern_resource = this.rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/JourneyPattern/"+id);
        this.rdfManager.addType(journeyPattern_resource, Namespaces.SERVICE_JOURNEY_PATTERN_resource);
        journeyPattern_resource.addProperty(RDFS.label, id);
        journeyPattern_resource.addProperty(SchemaDO.name, journeyPattern.getName().getValue());
        journeyPattern_resource.addProperty(Namespaces.onRoute, Namespaces.JOURNEYS+"/Resource/Route/"+journeyPattern.getRouteRef().getRef());
        mapVersion(journeyPattern_resource, journeyPattern);

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

    /**
     * Map a StopPointInJourneyPattern instance
     *
     * @param point                     An instance of the class <i>StopPointInJourneyPattern</i>
     * @param journeyPattern_resource   Resource which will be associated to <i>point</i>
     * @return                          A Resource that captures the same data into linked data
     */
    @Override
    public Resource mapStopPointInJourneyPattern(StopPointInJourneyPattern point, Resource journeyPattern_resource) {
        String id_point = point.getId();
        Resource stopPointInJourneyPattern_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/StopPointsInJourneyPattern/"+id_point);
        journeyPattern_resource.addProperty(Namespaces.journeyPatternMadeUpOf, stopPointInJourneyPattern_resource);
        rdfManager.addType(stopPointInJourneyPattern_resource, Namespaces.STOP_POINT_IN_JOURNEY_PATTERN_resource);
        stopPointInJourneyPattern_resource.addProperty(RDFS.label, id_point);
        stopPointInJourneyPattern_resource.addProperty(Namespaces.order, point.getOrder().toString());
        mapVersion(stopPointInJourneyPattern_resource, point);

        Boolean forAlighting = point.isForAlighting();
        if(forAlighting != null)
            stopPointInJourneyPattern_resource.addProperty(Namespaces.forAlighting, forAlighting.toString());

        Boolean forBoarding = point.isForBoarding();
        if(forBoarding != null)
            stopPointInJourneyPattern_resource.addProperty(Namespaces.forBoarding, forBoarding.toString());

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
            mapVersion(destinationRef_resource, netexManager.netex.getDestinationDisplayIndex().get(destinationDisplayRef.getRef()));
            DestinationDisplay dest = netexManager.netex.getDestinationDisplayIndex().get(destination_id);
            destinationRef_resource.addProperty(Namespaces.frontText, dest.getFrontText().getValue());

            stopPointInJourneyPattern_resource.addProperty(Namespaces.hasDestinationDisplay, destinationRef_resource);
        }

        return stopPointInJourneyPattern_resource;
    }

    /**
     * Map a ServiceLinkInJourneyPattern instance
     *
     * @param link                      An instance of the class <i>ServiceLinkInJourneyPattern</i>
     * @param journeyPattern_resource   Resource which will be associated to <i>link</i>
     * @return                          A Resource that captures the same data into linked data
     */
    @Override
    public Resource mapServiceLinkInJourneyPattern(ServiceLinkInJourneyPattern_VersionedChildStructure link, Resource journeyPattern_resource) {
        String link_id = link.getId();
        Resource link_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS + "/Resource/ServiceLinkInJourneyPattern/"+link_id);
        link_resource.addProperty(RDFS.label, link_id);
        rdfManager.addType(link_resource, Namespaces.LINK_SEQUENCE_resource);
        link_resource.addProperty(Namespaces.order, link.getOrder().toString());
        mapVersion(link_resource, link);

        Resource point_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS + "/Resource/ServiceLink/"+link.getServiceLinkRef().getRef());
        point_resource.addProperty(RDFS.label, link.getServiceLinkRef().getRef());

        link_resource.addProperty(Namespaces.aViewOf, point_resource); //No ontologia (ontology)
        journeyPattern_resource.addProperty(Namespaces.hasPointsInJourneyPattern, link_resource);

        return link_resource;
    }

    /**
     * Map a Route instance
     *
     * @param route     An instance of the class <i>Route</i>
     * @return          A Resource that captures the same data into linked data
     */
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
        mapVersion(route_resource, route);

        route.getPointsInSequence().getPointOnRoute().forEach(
                (pointOnRoute) -> {
                    String id_pointOnRoute = pointOnRoute.getId();
                    Resource pointOnRoute_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/PointOnRoute/"+id_pointOnRoute);
                    rdfManager.addType(pointOnRoute_resource, Namespaces.POINT_ON_ROUTE_resource);
                    pointOnRoute_resource.addProperty(RDFS.label, id_pointOnRoute);
                    pointOnRoute_resource.addProperty(Namespaces.order, pointOnRoute.getOrder().toString());
                    pointOnRoute_resource.addProperty(Namespaces.madeUpOf, route_resource);
                    mapVersion(pointOnRoute_resource, pointOnRoute);

                    String id_routePoint = pointOnRoute.getPointRef().getValue().getRef();
                    Resource routePoint_resource = rdfManager.rdf.getResource(Namespaces.JOURNEYS+"/Resource/RoutePoint/"+id_routePoint);
                    routePoint_resource.addProperty(Namespaces.aViewOf, pointOnRoute_resource);
                }
        );

        return route_resource;
    }

    /**
     * Map a RoutePoint instance
     *
     * @param routePoint    An instance of the class <i>RoutePoint</i>
     * @return              A Resource that captures the same data into linked data
     */
    @Override
    public Resource mapRoutePoint(RoutePoint routePoint) {
        String id_routePoint = routePoint.getId();
        Resource routePoint_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/RoutePoint/"+id_routePoint);
        rdfManager.addType(routePoint_resource, Namespaces.ROUTE_POINT_resource);
        routePoint_resource.addProperty(RDFS.label, id_routePoint);
        mapVersion(routePoint_resource, routePoint);

        PointProjection projections = (PointProjection) routePoint.getProjections().getProjectionRefOrProjection().get(0).getValue();
        routePoint_resource.addProperty(Namespaces.hasPointProjection, projections.getId());
        routePoint_resource.addProperty(
                Namespaces.scheduledStopPoint,
                projections.getProjectedPointRef().getRef()
        );

        return routePoint_resource;
    }

    /**
     * Map a Line instance
     *
     * @param line      An instance of the class <i>Line</i>
     * @return          A Resource that captures the same data into linked data
     */
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
        mapVersion(line_resource, line);

        OperatorRefStructure op = line.getOperatorRef();
        if(op != null)
            line_resource.addProperty(
                    Namespaces.runBy,
                    rdfManager.rdf.getResource(Namespaces.ORGANISATIONS+"/Resource/Operator/"+op.getRef())
            );

        GroupOfLinesRefStructure group = line.getRepresentedByGroupRef();
        if(group != null){
            Resource group_resource = rdfManager.rdf.createResource(Namespaces.COMMONS+"/Resource/GroupOfLines/"+group.getRef());
            rdfManager.addType(group_resource, Namespaces.GROUP_resource);
            group_resource.addProperty(RDFS.label, group.getRef());
            group_resource.addProperty(SchemaDO.name, netexManager.netex.getGroupOfLinesIndex().get(group.getRef()).getName().getValue());
            mapVersion(group_resource, netexManager.netex.getGroupOfLinesIndex().get(group.getRef()));
            line_resource.addProperty(
                    Namespaces.representedByGroup,
                    group_resource
            );
        }

        return line_resource;
    }

    /**
     * Map a ServiceJourney instance
     *
     * @param serviceJourney    An instance of the class <i>ServiceJourney</i>
     * @return                  A Resource that captures the same data into linked data
     */
    @Override
    public Resource mapServiceJourney(ServiceJourney serviceJourney) {
        String id_serviceJourney = serviceJourney.getId();
        Resource serviceJourney_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS + "/Resource/VehicleJourney/" + id_serviceJourney);
        serviceJourney_resource.addProperty(RDF.type, Namespaces.VEHICLE_JOURNEY_resource);
        serviceJourney_resource.addProperty(RDFS.label, id_serviceJourney);
        serviceJourney_resource.addProperty(SchemaDO.name, serviceJourney.getName().getValue());
        serviceJourney_resource.addProperty(Namespaces.hasPrivateCode, serviceJourney.getPrivateCode().getValue());
        mapVersion(serviceJourney_resource, serviceJourney);

        serviceJourney_resource.addProperty(Namespaces.workedOn,
                rdfManager.rdf.getResource(Namespaces.JOURNEYS+"/Resource/DayType/"+serviceJourney.getDayTypes().getDayTypeRef().get(0).getValue().getRef())
        );

        serviceJourney_resource.addProperty(Namespaces.followsJourneyPattern,
                rdfManager.rdf.getResource(Namespaces.JOURNEYS+"/Resource/JourneyPattern/"+serviceJourney.getJourneyPatternRef().getValue().getRef())
        );

        serviceJourney_resource.addProperty(Namespaces.onLine, // Relacción no existente en ontologia pero necesaria
                rdfManager.rdf.getResource(Namespaces.JOURNEYS+"/Resource/Line/"+serviceJourney.getLineRef().getValue().getRef())
        );

        serviceJourney_resource.addProperty(Namespaces.runBy,
                rdfManager.rdf.getResource(Namespaces.ORGANISATIONS + "/Resource/Operator/"+ serviceJourney.getOperatorRef().getRef())
        );

        serviceJourney.getPassingTimes().getTimetabledPassingTime().forEach(
                (timetabledPassingTime) -> {
                    String id_timetable = timetabledPassingTime.getId();
                    Resource timetable_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/TimetabledPassingTime/"+id_timetable);
                    timetable_resource.addProperty(RDF.type, Namespaces.TIMETABLED_PASSING_TIME_resource);
                    timetable_resource.addProperty(RDFS.label, id_timetable);
                    mapVersion(timetable_resource, timetabledPassingTime);

                    if(timetabledPassingTime.getDepartureDayOffset() != null)
                        timetable_resource.addProperty(Namespaces.departureOffset,
                                timetabledPassingTime.getDepartureDayOffset().toString()
                        );

                    if(timetabledPassingTime.getArrivalDayOffset() != null)
                        timetable_resource.addProperty(Namespaces.arrivalOffset,
                                timetabledPassingTime.getArrivalTime().toString()
                        );

                    if(timetabledPassingTime.getArrivalTime() != null)
                        timetable_resource.addProperty(Namespaces.arrivalTime,
                                timetabledPassingTime.getArrivalTime().format(DateTimeFormatter.ISO_LOCAL_TIME)
                        );

                    if(timetabledPassingTime.getDepartureTime() != null)
                        timetable_resource.addProperty(Namespaces.departureTime,
                                timetabledPassingTime.getDepartureTime().format(DateTimeFormatter.ISO_LOCAL_TIME)
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

    /**
     * Map a OperatingPeriod instance
     *
     * @param operatingPeriod   An instance of the class <i>OperatingPeriod</i>
     * @return                  A Resource that captures the same data into linked data
     */
    @Override
    public Resource mapOperatingPeriod(OperatingPeriod operatingPeriod) {
        String id_operatingPeriod = operatingPeriod.getId();
        Resource operatingPeriod_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS + "/Resource/OperatingPeriod/"+id_operatingPeriod);
        operatingPeriod_resource.addProperty(RDF.type, Namespaces.OPERATING_PERIOD_resource);
        operatingPeriod_resource.addProperty(RDFS.label, id_operatingPeriod);
        operatingPeriod_resource.addProperty(Namespaces.startingAt, operatingPeriod.getFromDate().format(DateTimeFormatter.ISO_DATE_TIME));
        operatingPeriod_resource.addProperty(Namespaces.endingAt, operatingPeriod.getToDate().format(DateTimeFormatter.ISO_DATE_TIME));
        mapVersion(operatingPeriod_resource, operatingPeriod);

        return operatingPeriod_resource;
    }

    /**
     * Map a DayType instance
     *
     * @param dayType   An instance of the class <i>DayType</i>
     * @return          A Resource that captures the same data into linked data
     */
    @Override
    public Resource mapDayType(DayType dayType) {
        String id_dayType = dayType.getId();
        Resource dayType_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/DayType/"+id_dayType);
        dayType_resource.addProperty(RDFS.label, id_dayType);
        dayType_resource.addProperty(RDF.type, Namespaces.DAY_TYPE_resource);
        mapVersion(dayType_resource, dayType);
        if(dayType.getProperties() != null)
            dayType_resource.addProperty(Namespaces.daysOfWeek,
                    dayType.getProperties().getPropertyOfDay().get(0).getDaysOfWeek()
                            .stream().map(x -> String.valueOf(x).substring(0,1) + String.valueOf(x).substring(1).toLowerCase())
                            .collect(Collectors.joining(" ","",""))
            );

        netexManager.netex.getDayTypeAssignmentsByDayTypeIdIndex().get(id_dayType).forEach(
                (dayTypeAssignment) -> {
                    String id_dayTypeA = dayTypeAssignment.getId();
                    Resource dayTypeAssignment_resource = rdfManager.rdf.createResource(Namespaces.JOURNEYS + "/Resource/DayTypeAssignment/" + id_dayTypeA);
                    dayTypeAssignment_resource.addProperty(RDFS.label, id_dayTypeA);
                    dayTypeAssignment_resource.addProperty(RDF.type, Namespaces.DAY_TYPE_ASSIGNMENT_resource);
                    dayTypeAssignment_resource.addProperty(Namespaces.specifying, dayType_resource);
                    mapVersion(dayType_resource, dayTypeAssignment);
                    if(dayTypeAssignment.getDate() != null)
                        dayTypeAssignment_resource.addProperty(Namespaces.date, dayTypeAssignment.getDate().format(DateTimeFormatter.ISO_DATE));
                    dayTypeAssignment_resource.addProperty(Namespaces.order, dayTypeAssignment.getOrder().toString());
                    if(dayTypeAssignment.getOperatingPeriodRef() != null)
                        dayTypeAssignment_resource.addProperty(Namespaces.definedBy,
                            rdfManager.rdf.getResource(Namespaces.JOURNEYS + "/Resource/OperatingPeriod/"+dayTypeAssignment.getOperatingPeriodRef().getRef())
                        );
                    if(dayTypeAssignment.isIsAvailable() != null)
                        dayTypeAssignment_resource.addProperty(Namespaces.isAvailable, dayTypeAssignment.isIsAvailable().toString());
                }
        );

        return dayType_resource;
    }

    /**
     * Map a Network instance
     *
     * @param network   An instance of the class <i>Network</i>
     * @return          A Resource that captures the same data into linked data
     */
    @Override
    public Resource mapNetwork(Network network) {
        String id_network = network.getId();
        Resource network_resource = rdfManager.rdf.createResource(Namespaces.COMMONS + "/Resource/Network/" + id_network);
        network_resource.addProperty(RDF.type, Namespaces.NETWORK_resource);
        network_resource.addProperty(RDFS.label, id_network);
        network_resource.addProperty(SchemaDO.name, network.getName().getValue());
        network_resource.addProperty(Namespaces.authorizedBy,
                rdfManager.rdf.getResource(Namespaces.CORE + "/Resource/Authority/" + network.getTransportOrganisationRef().getValue().getRef())
        );
        mapVersion(network_resource, network);

        network.getGroupsOfLines().getGroupOfLines().forEach(
                (groupOfLines -> {
                    network_resource.addProperty(Namespaces.networkMadeUpOf,
                            rdfManager.rdf.getResource(Namespaces.COMMONS + "/Resource/GroupOfLines/" + groupOfLines.getId())
                    );
                })
        );

        return network_resource;
    }
}






















