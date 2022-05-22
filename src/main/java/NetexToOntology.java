import DataManager.NetexManager;
import DataManager.OntologyEntityClasses;
import DataManager.OntologyManager;
import org.rutebanken.netex.model.Route;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class NetexToOntology {
    public static void main(String[] args) {
        //this.testOntologyMannager();
        testNetexMannager();
    }

    public static void testOntologyMannager(){
        String args_0 = "ontology.rdf"; // input ontology file
        String args_1 = "writeTest.rdf"; // output ontology file

        OntologyManager ontologyManager = new OntologyManager(args_0, args_1);
        ontologyManager.addIndividual(
                OntologyEntityClasses.COMMONS,
                "Presentation",
                "TestPresentationNewIndividual"
        );
        ontologyManager.saveModel();
    }


    public static void testNetexMannager(){
        String args_0 = "/Users/carlosmorote/Master local/TFM/OpenTripPlannerCompile/data/norway/netex_OST.zip";
        NetexManager netexManager = new NetexManager(args_0);
        List iterator = (List) netexManager.getData("Route");
        iterator.stream().map(this::parse);
    }
}
