import DataManager.Netex.NetexManager;
import DataManager.Ontology.OntologyEntityClasses;
import DataManager.Ontology.RDFManager;
import DataManager.Ontology.OntologyParser;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.Operator;

import java.util.Collection;
import java.util.stream.Collectors;

public class NetexToOntology {
    public static void main(String[] args) {
        testOntologyMannager();
        //testNetexMannager();
        //testMapping();
    }

    private static void testMapping() {
        String args_0 = "ontology.rdf"; // input ontology file
        String args_1 = "writeTest.rdf"; // output ontology file
        String args_2 = "/Users/carlosmorote/Master local/TFM/OpenTripPlannerCompile/data/norway/netex_OST.zip";

        RDFManager RDFManager = new RDFManager(args_0, args_1);
        NetexManager netexManager = new NetexManager(args_2);
        OntologyParser ontologyParser = new OntologyParser(RDFManager);

        Collection information = netexManager.getData("Operator");
        information.parallelStream().map(ontologyParser::parse).collect(Collectors.toList());

        RDFManager.saveRDF();
    }

    public static void testOntologyMannager(){
        String args_0 = "ontology.rdf"; // input ontology file
        String args_1 = "writeTest.rdf"; // output ontology file

        RDFManager rdf = new RDFManager(args_0, args_1);
        rdf.addIndividual(
                OntologyEntityClasses.COMMONS,
                "Operator",
                "TestNewIndividual"
        );
        Operator operator = new Operator();
        MultilingualString aux = new MultilingualString();
        aux.setValue("Descripcion de prueba");
        operator.withDescription(aux);
        aux.setValue("Nombre de prueba");
        operator.withName(aux);
        rdf.addResource(operator);
        rdf.saveRDF();
    }


    public static void testNetexMannager(){
        String args_0 = "/Users/carlosmorote/Master local/TFM/OpenTripPlannerCompile/data/norway/netex_OST.zip";

        NetexManager netexManager = new NetexManager(args_0);
        OntologyParser o_parser = new OntologyParser(null); // Para test

        Collection information = netexManager.getData("Operator");
        information.parallelStream().map(o_parser::parse).collect(Collectors.toList());
    }
}
