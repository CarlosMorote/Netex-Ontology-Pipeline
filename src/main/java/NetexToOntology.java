import DataManager.Netex.NetexManager;
import DataManager.Ontology.OntologyEntityClasses;
import DataManager.Ontology.RDFManager;
import DataManager.Ontology.OntologyParserFromNetex;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.VCARD;

import java.util.Collection;

public class NetexToOntology {
    public static void main(String[] args) {
        //testOntologyMannager();
        //testNetexMannager();
        testMapping();
    }

    private static void testMapping() {
        String args_0 = "journey.rdf"; // input ontology file
        String args_1 = "writeTest.rdf"; // output ontology file
        String args_2 = "/Users/carlosmorote/Master local/TFM/OpenTripPlannerCompile/data/norway/OST-netex.zip";

        RDFManager rdfManager = new RDFManager(args_0, args_1);
        NetexManager netexManager = new NetexManager(args_2);
        OntologyParserFromNetex ontologyParser = new OntologyParserFromNetex(rdfManager, netexManager);

        ontologyParser.castNetexToOntology();

        //rdfManager.printRDF();
        rdfManager.saveRDF();
    }

    public static void testOntologyMannager(){
        String args_0 = "journey.rdf"; // input ontology file
        String args_1 = "writeTest.rdf"; // output ontology file

        RDFManager rdf = new RDFManager(args_0, args_1);
        rdf.addIndividual(
                OntologyEntityClasses.COMMONS,
                "Operator",
                "TestNewIndividual"
        );

        Model modelTest = rdf.rdf;
        Resource test = modelTest.createResource("https://w3id.org/mobility/transmodel/journeys/resource/Route/L1-10");
        test.addProperty(RDF.value, "Pepe1-10");
        test.addProperty(VCARD.FN, "Familiy");
        modelTest.write(System.out);

        rdf.saveRDF();
    }


    public static void testNetexMannager(){
        String args_0 = "/Users/carlosmorote/Master local/TFM/OpenTripPlannerCompile/data/norway/netex_OST.zip";

        NetexManager netexManager = new NetexManager(args_0);
        OntologyParserFromNetex o_parser = new OntologyParserFromNetex(null, null); // Para test

        Collection information = netexManager.getData("Operator");
        //information.parallelStream().map(o_parser::parse).collect(Collectors.toList());
    }
}
