import DataManager.Netex.NetexManager;
import DataManager.Ontology.RDFManager;
import Parsers.FromRDFToXML.NetexParserFromRDF;
import Parsers.FromXMLToRDF.OntologyParserFromNetex;

public class NetexToOntology {
    public static void main(String[] args) {
        testMapping();
    }

    private static void testMapping() {
        String args_0 = "journey.rdf"; // input ontology file
        String args_1 = "./output/linked.ttl"; // output ontology file
        String args_2 = "/Users/carlosmorote/Master local/TFM/OpenTripPlannerCompile/data/norway/OST-netex.zip";

        RDFManager rdfManager = new RDFManager(args_0, args_1);
        NetexManager netexManager = new NetexManager(args_2);
        OntologyParserFromNetex ontologyParser = new OntologyParserFromNetex(rdfManager, netexManager);

        //ontologyParser.castNetexToOntology();
        //rdfManager.saveRDF("Turtle");

        // ---------------------------------

        String rdfFile = args_1;
        String outPath_Folder = "/Users/carlosmorote/Master local/TFM/GTFS_Ontology_NeTEx/output/";
        NetexParserFromRDF rdfParser = new NetexParserFromRDF(rdfFile, outPath_Folder);
        //rdfParser.parse();

        System.out.println("\nVerificando XML generado");
        try{
            new NetexManager("/Users/carlosmorote/Master local/TFM/GTFS_Ontology_NeTEx/output.zip");
            System.out.println("XML generado correctamente");
        } catch (Exception ex){
            System.out.println("Errores en el formato NeTEx");
            throw ex;
        }
    }

}
