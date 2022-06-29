package Parsers.FromRDFToXML;

import DataManager.Ontology.Namespaces;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    public static Element mapVersion(Resource rdf, Element current){
        Statement v = rdf.getProperty(Namespaces.version);
        if(v != null)
            current.setAttribute("version", v.getObject().toString());
        return current;
    }

    private void generate_sharedData() throws IOException{
        initXML();
        (new shared_data_XML(rdf,xml)).generate();
        saveXML("_OST_shared_data.xml"); //TODO: OST must be added dynamic
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
            zip_files();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void zip_files() throws IOException {
        List<String> srcFiles = Arrays.stream((new File(this.out_path).list()))
                .filter(file -> !(file.toUpperCase().contains("DS_STORE") | file.contains(".ttl")))
                .collect(Collectors.toList());
        FileOutputStream fos = new FileOutputStream(this.out_path+"output.netex.zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        for (String srcFile : srcFiles) {
            File fileToZip = new File(this.out_path+srcFile);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }
        zipOut.close();
        fos.close();
    }
}
