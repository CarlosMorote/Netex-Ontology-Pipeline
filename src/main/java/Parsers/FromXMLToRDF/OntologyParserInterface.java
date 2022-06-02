package Parsers.FromXMLToRDF;

import org.apache.jena.rdf.model.Resource;
import org.rutebanken.netex.model.*;

public interface OntologyParserInterface {

    Object parse(Object o);

    Resource mapAuthority(Authority authority);
    Resource mapOperator(Operator operator);
    Resource mapScheduledStopPoint(ScheduledStopPoint scheduledStopPoint);
    Resource mapJourneyPattern(JourneyPattern journeyPattern);
    Resource mapStopPointInJourneyPattern(StopPointInJourneyPattern point, Resource journeyPattern_resource);
    Resource mapRoute(Route route);
    Resource mapRoutePoint(RoutePoint routePoint);
    Resource mapLine(Line line);

}