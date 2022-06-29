import DataManager.Netex.NetexManager;
import DataManager.Ontology.RDFManager;
import Parsers.FromRDFToXML.NetexParserFromRDF;
import Parsers.FromXMLToRDF.OntologyParserFromNetex;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class Main {

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("Main").build()
                .defaultHelp(true)
                .description("Cast files between NeTEx and Turtle. It is specify within the parameters.");
        parser.addArgument("-f", "--flow")
                .choices("N-T", "T-N").setDefault("N-T")
                .help("Direction of the Pipeline. N-T (NeTEx to Turtle). T-N (Turtle to NeTEx)");
        parser.addArgument("input")
                .help("Direction of the input file");
        parser.addArgument("output")
                .help("Direction of the output file");

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        switch (ns.getString("flow")){
            case "N-T":
                Netex_Turtle(ns.getString("input"), ns.getString("output"));
                break;

            case "T-N":
                Turtle_Netex(ns.getString("input"), ns.getString("output"));
                break;
        }

        //testMapping();
    }

    public static void Netex_Turtle(String netex_dir, String turtle_dir){
        RDFManager rdfManager = new RDFManager(turtle_dir);
        NetexManager netexManager = new NetexManager(netex_dir);
        OntologyParserFromNetex ontologyParser = new OntologyParserFromNetex(rdfManager, netexManager);

        ontologyParser.castNetexToOntology();
        rdfManager.saveRDF("Turtle");
    }

    public static void Turtle_Netex(String turtle_dir, String netex_dir){
        NetexParserFromRDF rdfParser = new NetexParserFromRDF(turtle_dir, netex_dir);
        rdfParser.parse();

        System.out.println("\nVerificando XML generado");
        try{
            NetexManager output_netex = new NetexManager(netex_dir+"output.netex.zip");
            System.out.println("XML generado correctamente");
        } catch (Exception ex){
            System.out.println("Errores en el formato NeTEx");
            throw ex;
        }
    }

}
