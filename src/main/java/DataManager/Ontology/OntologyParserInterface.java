package DataManager.Ontology;

import org.rutebanken.netex.model.Authority;
import org.rutebanken.netex.model.Branding;
import org.rutebanken.netex.model.Operator;

public interface OntologyParserInterface {
    Operator mapOperator(Operator operator);

    Branding mapBranding(Branding branding);
}
