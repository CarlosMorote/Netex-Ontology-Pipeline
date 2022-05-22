import DataManager.OntologyEntityClasses;
import DataManager.OntologyManager;

public class NetexToOntology {
    public static void main(String[] args) {
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
}
