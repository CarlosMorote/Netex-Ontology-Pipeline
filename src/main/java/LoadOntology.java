import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class LoadOntology {
    public static void main(String[] args) throws IOException {
        String args_0 = "ontology.rdf";
        String args_1 = "writeTests.rdf";

        //Ontology
        //Model model = RDFDataMgr.loadModel(args_0) ;
        //OntModel ontology = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM, model);

        // create an empty model
        OntModel model = ModelFactory.createOntologyModel();

        // use the FileManager to find the input file
        InputStream in = FileManager.get().open( args_0 );
        if (in == null) {
            throw new IllegalArgumentException(
                    "File: " + args_0 + " not found");
        }

        // read the RDF/XML file
        model.read(in, "");

        String namespace = "https://w3id.org/mobility/transmodel/journeys#";

        // Crea instancia 'individualTest' para la clase Journey
        OntClass Noun = model.getOntClass(namespace + "Journey");

        Individual ind = model.createIndividual(namespace + "individualTest", Noun);

        FileWriter out = new FileWriter( args_1 );
        model.write( out, "RDF/XML-ABBREV" );
        out.close();
    }

}
