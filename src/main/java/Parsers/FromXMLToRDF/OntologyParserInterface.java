package Parsers.FromXMLToRDF;

import org.apache.jena.rdf.model.Resource;
import org.rutebanken.netex.model.*;

/**
 * Interface which defines the methods that map the NeTEx classes into a linked data file
 */
public interface OntologyParserInterface {

    Object parse(Object o);

    /**
     * Map an Authority instance
     *
     * @param authority    An instance of the class <i>Authority</i>
     * @return             A Resource that captures the same data into linked data
     */
    Resource mapAuthority(Authority authority);

    /**
     * Map an Operator instance
     *
     * @param operator      An instance of the class <i>Operator</i>
     * @return              A Resource that captures the same data into linked data
     */
    Resource mapOperator(Operator operator);

    /**
     * Map a ScheduledStopPoint instance
     *
     * @param scheduledStopPoint    An instance of the class <i>ScheduledStopPoint</i>
     * @return                      A Resource that captures the same data into linked data
     */
    Resource mapScheduledStopPoint(ScheduledStopPoint scheduledStopPoint);

    /**
     * Map a JourneyPattern instance
     *
     * @param journeyPattern    An instance of the class <i>JourneyPattern</i>
     * @return                  A Resource that captures the same data into linked data
     */
    Resource mapJourneyPattern(JourneyPattern journeyPattern);

    /**
     * Map a StopPointInJourneyPattern instance
     *
     * @param point                     An instance of the class <i>StopPointInJourneyPattern</i>
     * @param journeyPattern_resource   Resource which will be associated to <i>point</i>
     * @return                          A Resource that captures the same data into linked data
     */
    Resource mapStopPointInJourneyPattern(StopPointInJourneyPattern point, Resource journeyPattern_resource);

    /**
     * Map a Route instance
     *
     * @param route     An instance of the class <i>Route</i>
     * @return          A Resource that captures the same data into linked data
     */
    Resource mapRoute(Route route);

    /**
     * Map a RoutePoint instance
     *
     * @param routePoint    An instance of the class <i>RoutePoint</i>
     * @return              A Resource that captures the same data into linked data
     */
    Resource mapRoutePoint(RoutePoint routePoint);

    /**
     * Map a Line instance
     *
     * @param line      An instance of the class <i>Line</i>
     * @return          A Resource that captures the same data into linked data
     */
    Resource mapLine(Line line);

    /**
     * Map a ServiceLinkInJourneyPattern instance
     *
     * @param link                      An instance of the class <i>ServiceLinkInJourneyPattern</i>
     * @param journeyPattern_resource   Resource which will be associated to <i>link</i>
     * @return                          A Resource that captures the same data into linked data
     */
    Resource mapServiceLinkInJourneyPattern(ServiceLinkInJourneyPattern_VersionedChildStructure link, Resource journeyPattern_resource);

    /**
     * Map a ServiceJourney instance
     *
     * @param serviceJourney    An instance of the class <i>ServiceJourney</i>
     * @return                  A Resource that captures the same data into linked data
     */
    Resource mapServiceJourney(ServiceJourney serviceJourney);

    /**
     * Map a DayType instance
     *
     * @param dayType   An instance of the class <i>DayType</i>
     * @return          A Resource that captures the same data into linked data
     */
    Resource mapDayType(DayType dayType);

    /**
     * Map a OperatingPeriod instance
     *
     * @param operatingPeriod   An instance of the class <i>OperatingPeriod</i>
     * @return                  A Resource that captures the same data into linked data
     */
    Resource mapOperatingPeriod(OperatingPeriod operatingPeriod);

    /**
     * Map a Network instance
     *
     * @param network   An instance of the class <i>Network</i>
     * @return          A Resource that captures the same data into linked data
     */
    Resource mapNetwork(Network network);

    /**
     * Map a Quay instance
     *
     * @param quay  An instance of the class <i>Quay</i>
     * @return      A Resource that captures the same data into linked data
     */
    Resource mapQuay(Quay quay);

}
