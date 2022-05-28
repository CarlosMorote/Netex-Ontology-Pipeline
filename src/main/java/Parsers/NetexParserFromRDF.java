package Parsers;

import DataManager.Ontology.RDFManager;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.json.XML;

public class NetexParserFromRDF {

    public Model rdf;
    public XML xml;

    public NetexParserFromRDF(String rdf_path){
        this.rdf = RDFDataMgr.loadModel(rdf_path);
    }

    private void generate_sharedData(){

    }

    private void generate_stops(){

    }

    private void generate_Line(){

    }

    private void generate_Lines(){
        //Listar lineas e iterar sobre generate_Line
    }

    public void parse(){

    }
}
