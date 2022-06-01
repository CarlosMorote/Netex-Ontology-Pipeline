package Parsers;

import DataManager.Ontology.Namespaces;
import DataManager.Ontology.RDFManager;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.impl.StatementImpl;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

public class NetexParserFromRDF {

    public Model rdf;
    public Document xml;
    public String out_path;
    public XMLOutputter outputter;

    public NetexParserFromRDF(String rdf_path, String out_path){
        this.rdf = RDFDataMgr.loadModel(rdf_path);
        this.out_path = out_path;
        outputter = new XMLOutputter(Format.getPrettyFormat());
    }

    private void initXML(){
        this.xml = new Document();
    }

    private void generate_sharedData() throws IOException {
        initXML();
        (new shared_data_XML(rdf,xml)).generate();

        String p = out_path + "_shared_data.xml";
        outputter.output(xml, new FileOutputStream(p));
        System.out.println("File "+p+" generated");
    }

    public void parse(){
        try {
            generate_sharedData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
