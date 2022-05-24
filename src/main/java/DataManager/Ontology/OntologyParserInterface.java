package DataManager.Ontology;

import org.rutebanken.netex.model.*;

public interface OntologyParserInterface {

    Authority mapAuthority(Authority authority);
    Operator mapOperator(Operator operator);
    ServiceLink mapServiceLink(ServiceLink serviceLink);
    JourneyPattern mapJourneyPattern(JourneyPattern journeyPattern);

}
