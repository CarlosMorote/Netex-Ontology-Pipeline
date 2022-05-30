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
    public static final Property journeyPatternMadeUpOf = getProperty(m, Namespaces.JOURNEYS, "#journeyPatternMadeUpOf");
    public static final Property forAlighting = getProperty(m, Namespaces.JOURNEYS, "#forAlighting");
    public static final Property scheduledStopPoint = getProperty(m, Namespaces.JOURNEYS, "#scheduledStopPoint");


    public static final Resource AUTHORITY_resource = m.createResource(Namespaces.ORGANISATIONS + "#Authority");
    public static final Resource OPERATOR_resource = m.createResource(Namespaces.ORGANISATIONS + "#Operator");
    public static final Resource SCHEDULE_STOP_POINT_resource = m.createResource(Namespaces.JOURNEYS+"#ScheduledStopPoint");
    public static final Resource SERVICE_JOURNEY_PATTERN_resource = m.createResource(Namespaces.JOURNEYS+"#ServiceJourneyPattern");
    public static final Resource STOP_POINT_IN_JOURNEY_PATTERN_resource = m.createResource(Namespaces.JOURNEYS+"#StopPointsInJourneyPattern");

    public Namespaces() {
    }

    // TODO: ARREGLAR NO FUNCIONA
    public static Property getProperty(Model model, String n, String end){
        return model.createProperty(n+end);
    }

}
