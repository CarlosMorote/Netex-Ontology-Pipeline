package DataManager.Ontology;

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
    public static final Property departureTime = getProperty(m, Namespaces.JOURNEYS, "departureTime");

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

    public Namespaces() {
    }

    // TODO: ARREGLAR NO FUNCIONA
    public static Property getProperty(Model model, String n, String end){
        return model.createProperty(n+end);
    }

}
