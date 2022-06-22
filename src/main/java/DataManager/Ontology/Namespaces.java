package DataManager.Ontology;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.SKOS;

public class Namespaces {
    private static final Model m = ModelFactory.createDefaultModel();
    private static final String facilities = "facilities";
    private static final String commons = "commons";
    private static final String journeys = "journeys";
    private static final String organisations = "organisations";
    private static final String core = "core";

    // URIs
    public static final String TRANSMODEL_ROOT = "https://w3id.org/mobility/transmodel/";
    public static final String SKOS_ROOT = "http://www.w3.org/2004/02/skos/";

    public static final String FACILITIES = TRANSMODEL_ROOT + facilities;
    public static final String COMMONS = TRANSMODEL_ROOT + commons;
    public static final String JOURNEYS = TRANSMODEL_ROOT + journeys;
    public static final String ORGANISATIONS = TRANSMODEL_ROOT + organisations;
    public static final String CORE = SKOS.getURI().split("#")[0];


    public static final Property onRoute = getProperty(m, Namespaces.JOURNEYS, "#onRoute");
    public static final Property onLine = getProperty(m, Namespaces.JOURNEYS, "#onLine");
    public static final Property journeyPatternMadeUpOf = getProperty(m, Namespaces.JOURNEYS, "#journeyPatternMadeUpOf");
    public static final Property forAlighting = getProperty(m, Namespaces.JOURNEYS, "#forAlighting");
    public static final Property scheduledStopPoint = getProperty(m, Namespaces.JOURNEYS, "#scheduledStopPoint");
    public static final Property allowedLineDirections = getProperty(m, Namespaces.JOURNEYS, "#allowedLineDirections");
    public static final Property madeUpOf = getProperty(m, Namespaces.JOURNEYS, "#madeUpOf");
    public static final Property order = getProperty(m, Namespaces.JOURNEYS, "#order");
    public static final Property aViewOf = getProperty(m, Namespaces.JOURNEYS, "#aViewOf");
    public static final Property hasPointProjection = getProperty(m, Namespaces.JOURNEYS, "#hasPointProjection");
    public static final Property hasTransportMode = getProperty(m, Namespaces.JOURNEYS, "#hasTransportMode");
    public static final Property hasPublicCode = getProperty(m, Namespaces.JOURNEYS, "#hasPublicCode");
    public static final Property hasPrivateCode = getProperty(m, Namespaces.JOURNEYS, "#hasPrivateCode");
    public static final Property runBy = getProperty(m, Namespaces.JOURNEYS, "#runBy");
    public static final Property hasValidity = getProperty(m, Namespaces.JOURNEYS, "#hasValidity");
    public static final Property representedByGroup = getProperty(m, Namespaces.JOURNEYS, "representedByGroup");
    public static final Property hasDestinationDisplay = getProperty(m, Namespaces.COMMONS, "#hasDestinationDisplay");
    public static final Property frontText = getProperty(m, Namespaces.COMMONS, "#frontText");
    public static final Property hasPointsInJourneyPattern = getProperty(m, Namespaces.JOURNEYS, "#hasPointsInJourneyPattern"); //No en ontologia
    public static final Property workedOn = getProperty(m, Namespaces.JOURNEYS, "#workedOn"); //En ontologia, pero no enlazado
    public static final Property followsJourneyPattern = getProperty(m, Namespaces.JOURNEYS, "#followsJourneyPattern");
    public static final Property passesAt = getProperty(m, Namespaces.JOURNEYS, "#passesAt");
    public static final Property departureTime = getProperty(m, Namespaces.JOURNEYS, "#departureTime");
    public static final Property arrivalTime = getProperty(m, Namespaces.JOURNEYS, "#ArrivalTime");
    public static final Property daysOfWeek = getProperty(m, Namespaces.JOURNEYS, "#daysOfWeek");
    public static final Property specifying = getProperty(m, Namespaces.JOURNEYS, "#specifying");
    public static final Property date = getProperty(m, Namespaces.JOURNEYS, "#date");
    public static final Property definedBy = getProperty(m, Namespaces.JOURNEYS, "#definedBy");
    public static final Property startingAt = getProperty(m, Namespaces.JOURNEYS, "#startingAt");
    public static final Property endingAt = getProperty(m, Namespaces.JOURNEYS, "#endingAt");
    public static final Property authorizedBy = getProperty(m, Namespaces.COMMONS, "#authorizedBy");
    public static final Property networkMadeUpOf = getProperty(m, Namespaces.COMMONS, "#networkMadeUpOf");
    public static final Property forStopPoint = getProperty(m, Namespaces.JOURNEYS, "#forStopPoint");
    public static final Property forQuay = getProperty(m, Namespaces.JOURNEYS, "#forQuay");
    public static final Property version = getProperty(m, Namespaces.CORE, "#version");
    public static final Property isAvailable = getProperty(m, Namespaces.JOURNEYS, "#isAvailable");
    public static final Property forBoarding = getProperty(m, Namespaces.JOURNEYS, "#forBoarding");
    public static final Property departureOffset = getProperty(m, Namespaces.JOURNEYS, "#departureOffset");
    public static final Property arrivalOffset = getProperty(m, Namespaces.JOURNEYS, "#arrivalOffset");

    public static final Resource AUTHORITY_resource = m.createResource(Namespaces.ORGANISATIONS + "#Authority");
    public static final Resource OPERATOR_resource = m.createResource(Namespaces.ORGANISATIONS + "#Operator");
    public static final Resource SCHEDULE_STOP_POINT_resource = m.createResource(Namespaces.JOURNEYS+"#ScheduledStopPoint");
    public static final Resource SERVICE_JOURNEY_PATTERN_resource = m.createResource(Namespaces.JOURNEYS+"#ServiceJourneyPattern");
    public static final Resource STOP_POINT_IN_JOURNEY_PATTERN_resource = m.createResource(Namespaces.JOURNEYS+"#StopPointsInJourneyPattern");
    public static final Resource ROUTE_resource = m.createResource(Namespaces.JOURNEYS+"#Route");
    public static final Resource POINT_ON_ROUTE_resource = m.createResource(Namespaces.COMMONS+"#PointOnRoute");
    public static final Resource ROUTE_POINT_resource = m.createResource(Namespaces.JOURNEYS+"#RoutePoint");
    public static final Resource LINE_resource = m.createResource(Namespaces.JOURNEYS+"#Line");
    public static final Resource DESTINATION_DISPLAY_resource = m.createResource(Namespaces.COMMONS+"#DestinationDisplay");
    public static final Resource POINT_IN_LINK_SEQUENCE_resource = m.createResource(Namespaces.JOURNEYS+"#PointInLinkSequence");
    public static final Resource LINK_SEQUENCE_resource = m.createResource(Namespaces.JOURNEYS+"#LinkSequence");
    public static final Resource VEHICLE_JOURNEY_resource = m.createResource(Namespaces.JOURNEYS+"#VehicleJourney");
    public static final Resource TIMETABLED_PASSING_TIME_resource = m.createResource(Namespaces.JOURNEYS+"TimetabledPassingTime");
    public static final Resource DAY_TYPE_resource = m.createResource(Namespaces.JOURNEYS+"#DayType");
    public static final Resource DAY_TYPE_ASSIGNMENT_resource = m.createResource(Namespaces.JOURNEYS+"#DayTypeAssignment");
    public static final Resource OPERATING_PERIOD_resource = m.createResource(Namespaces.JOURNEYS+"#OperatingPeriod");
    public static final Resource NETWORK_resource = m.createResource(Namespaces.COMMONS+"#Network");
    public static final Resource QUAY_resource = m.createResource(Namespaces.FACILITIES+"#Quay");
    public static final Resource PASSENGER_STOP_ASSIGNMENT_resource = m.createResource(Namespaces.JOURNEYS+"#PassengerStopASsignment");
    public static final Resource GROUP_resource = m.createResource(Namespaces.COMMONS+"#GroupOfLines");

    public Namespaces() {
    }

    public static Property getProperty(Model model, String n, String end){
        return model.createProperty(n+end);
    }

}
