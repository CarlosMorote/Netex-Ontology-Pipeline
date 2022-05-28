package Parsers;

import DataManager.Ontology.RDFManager;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class NetexParserFromRDF {

    public Model rdf;
    public Document xml;
    public String out_path;
    public XMLOutputter outputter;

    public NetexParserFromRDF(String rdf_path, String out_path){
        this.rdf = RDFDataMgr.loadModel(rdf_path);
        this.out_path = out_path;
        outputter = new XMLOutputter(Format.getPrettyFormat());
        this.initXML();
    }

    private void initXML(){
        this.xml = new Document();
        this.xml = XMLStructures.initSharedXML(xml);
    }

    private void generate_sharedData() throws IOException {


        String p = out_path + "_shared_data.xml";
        outputter.output(xml, new FileOutputStream(p));
        System.out.println("File "+p+" generated");
    }

    private void generate_stops(){

    }

    private void generate_Line(){

    }

    private void generate_Lines(){
        //Listar lineas e iterar sobre generate_Line
    }

    public void parse(){
        try {
            generate_sharedData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
