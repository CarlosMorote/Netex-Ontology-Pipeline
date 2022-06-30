package DataManager.Ontology;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class RDFManager {

    private String dir_output;

    public Model rdf;

    /**
     * Empty class constructor
     */
    public RDFManager() {
        this.init_rdf(null);
    }

    /**
     * Class constructor that takes the output directory as parameter.
     *
     * @param dir_output    output directory
     */
    public RDFManager(String dir_output) {
        this.dir_output = dir_output;
        this.init_rdf(null);
    }

    /**
     * Initializer of the rdf/turtle model setting the prefixes
     *
     * @param prefixes  Map of prefixes
     */
    public void init_rdf(Map<String, String> prefixes){
        this.rdf = ModelFactory.createDefaultModel();
        if (prefixes == null){
            prefixes = new HashMap<>(){{
                put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
                put("schema", "https://schema.org/");
                put("journeys", Namespaces.JOURNEYS +"#");
                put("vcard", "http://www.w3.org/2006/vcard/ns#");
                put("skos", "http://www.w3.org/2004/02/skos/core#");
                put("transmodel", Namespaces.TRANSMODEL_ROOT);
                put("organisations", Namespaces.ORGANISATIONS+"#");
                put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
                put("commons", Namespaces.COMMONS+"#");
                put("facilities", Namespaces.FACILITIES+"#");
            }};
        }
        this.rdf.setNsPrefixes(prefixes);
    }

    /**
     * Save the model as the indicated format in the direction saved at the construction time
     *
     * @param format    Format, such as, "Turtle"
     */
    public void save(String format){
        this.save(this.dir_output, format);
    }

    /**
     * Save the model in the direction specify as parameter
     *
     * @param output File direction where to save the data
     * @param format Format, such as, "Turtle"
     */
    public void save(String output, String format){
        OutputStream out;
        try {
            out = new FileOutputStream(output);
            this.rdf.write(out, format);
            System.out.println("LinkedData generated. File in "+output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Prints out the model
     */
    public void printRDF() {
        this.rdf.write(System.out);
    }

    /**
     * Generates resource with <i>type</i> property
     *
     * @param resource  Resource to add the type
     * @param uri       Type to be added as ur
     */
    public void addType(Resource resource, Resource uri){
        resource.addProperty(
            RDF.type,
            uri
        );
    }
}





















