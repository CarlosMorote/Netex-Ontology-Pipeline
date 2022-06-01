package DataManager.Ontology;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class RDFManager {
    private String ont_source;

    private String ont_dir_output;

    final private Namespaces namespaces = new Namespaces();

    public OntModel ont;

    public Model rdf;

    public RDFManager(String ont_source) {
        this.ont_source = ont_source;
        this.ont = this.loadModel(ont_source);
        this.rdf = ModelFactory.createDefaultModel();
    }

    public RDFManager(String ont_source, String ont_dir_output) {
        this.ont_dir_output = ont_dir_output;
        this.ont_source = ont_source;
        this.ont = this.loadModel(ont_source);
        this.rdf = ModelFactory.createDefaultModel();
    }

    public OntModel loadModel(String source){
        OntModel model = ModelFactory.createOntologyModel();
        InputStream in = null;

        try{
            in = FileManager.get().open(source);
            if(in == null){
                throw new IllegalArgumentException(
                        "File: "+source+ " not found"
                );
            }
            model.read(in, "");
        }catch (Exception e){
            throw e;
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return model;
    }

    public void saveRDF(String format){
        this.saveRDF(this.ont_dir_output, format);
    }

    public void saveRDF(String output, String format){
        FileWriter out = null;
        try {
            out = new FileWriter(output);
            this.rdf.write(out, format);
            out.close();
            System.out.println("LinkedData generated. File in "+output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void printOntology() {
        this.ont.write( System.out );
    }

    public void printRDF() {
        this.rdf.write(System.out);
    }

    public void addType(Resource resource, Resource uri){
        resource.addProperty(
            RDF.type,
            uri
        );
    }
}





















