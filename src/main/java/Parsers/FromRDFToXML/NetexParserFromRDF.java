package Parsers.FromRDFToXML;

import DataManager.Ontology.Namespaces;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
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
    }

    private void initXML(){
        this.xml = new Document();
    }

    private void saveXML(String fileName) throws IOException {
        String p = out_path + fileName;
        outputter.output(xml, new FileOutputStream(p));
        System.out.println("File "+p+" generated");
    }

    private void generate_sharedData() throws IOException{
        initXML();
        (new shared_data_XML(rdf,xml)).generate();
        saveXML("_shared_data.xml");
    }

    private void generate_LinesData() throws IOException{
        StmtIterator iterator = rdf.listStatements(null, RDF.type, Namespaces.LINE_resource);
        Resource currentResource;
        while(iterator.hasNext()){
            currentResource = rdf.getResource(iterator.nextStatement().getSubject().toString());

            initXML();
            (new line_data_XML(rdf,xml,currentResource)).generate();

            String lineName = currentResource.getProperty(RDFS.label).getObject().toString();
            saveXML(lineName.replace(":","_") + ".xml");
        }
    }

    public void parse(){
        try {
            generate_sharedData();
            generate_LinesData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
