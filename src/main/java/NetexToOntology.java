import DataManager.Netex.NetexManager;
import DataManager.Ontology.RDFManager;
import Parsers.NetexParserFromRDF;
import Parsers.OntologyParserFromNetex;

public class NetexToOntology {
    public static void main(String[] args) {
        testMapping();
    }

    private static void testMapping() {
        String args_0 = "journey.rdf"; // input ontology file
        String args_1 = "writeTest.ttl"; // output ontology file
        String args_2 = "/Users/carlosmorote/Master local/TFM/OpenTripPlannerCompile/data/norway/OST-netex.zip";

        RDFManager rdfManager = new RDFManager(args_0, args_1);
        NetexManager netexManager = new NetexManager(args_2);
        OntologyParserFromNetex ontologyParser = new OntologyParserFromNetex(rdfManager, netexManager);

        ontologyParser.castNetexToOntology();

        //rdfManager.printRDF();
        rdfManager.saveRDF("Turtle");

        // ---------------------------------

        String rdfFile = args_1;
        String outPath_Folder = "/Users/carlosmorote/Master local/TFM/GTFS_Ontology_NeTEx/";
        NetexParserFromRDF rdfParser = new NetexParserFromRDF(rdfFile, outPath_Folder);
        rdfParser.parse();
    }

}
