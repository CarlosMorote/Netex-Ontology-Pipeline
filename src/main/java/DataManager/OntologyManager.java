package DataManager;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class OntologyManager {
    private String ont_source;

    private String ont_dir_output;

    final private Namespaces namespaces = new Namespaces();

    public OntModel model;

    public OntologyManager(String ont_source) {
        this.ont_source = ont_source;
        this.model = this.loadModel(ont_source);
    }

    public OntologyManager(String ont_source, String ont_dir_output) {
        this.ont_source = ont_source;
        this.ont_dir_output = ont_dir_output;
        this.model = this.loadModel(ont_source);
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

    public void saveModel(){
        this.saveModel(this.ont_dir_output);
    }

    public void saveModel(String output){
        FileWriter out = null;
        try {
            out = new FileWriter(output);
            this.model.write(out, "RDF/XML-ABBREV");
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void printModel() {
        this.model.write( System.out );
    }

    public Individual addIndividual(@NotNull OntologyEntityClasses entity, String ont_class, String new_individual){
        String url = "";
        switch (entity){
            case FACILITIES:
                url = namespaces.getFACILITIES();
                break;

            case CORE:
                url = namespaces.getCORE();
                break;

            case COMMONS:
                url = namespaces.getCOMMONS();
                break;

            case JOURNEYS:
                url = namespaces.getJOURNEYS();
        }

        OntClass Noun = this.model.getOntClass(url + ont_class);
        return this.model.createIndividual(url + new_individual, Noun);
    }
}
