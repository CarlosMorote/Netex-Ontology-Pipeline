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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Class which captures the logic and implementation of the pipeline from Turtle/RDF to NeTEx
 */
public class NetexParserFromRDF {

    public Model rdf;
    public Document xml;
    public String out_path;
    public XMLOutputter outputter;

    /**
     * Constructor of the class
     *
     * @param rdf_path linked data file path
     * @param out_path folder which will contain the output
     */
    public NetexParserFromRDF(String rdf_path, String out_path){
        this.rdf = RDFDataMgr.loadModel(rdf_path);
        this.out_path = out_path;
        outputter = new XMLOutputter(Format.getPrettyFormat());
    }

    /**
     * Initialize a new empty xml (NeTEx) model
     */
    private void initXML(){
        this.xml = new Document();
    }

    /**
     * Write the current xml into output folder
     *
     * @param fileName      name of the file to be saved
     * @throws IOException
     */
    private void saveXML(String fileName) throws IOException {
        String p = out_path + fileName;
        outputter.output(xml, new FileOutputStream(p));
        System.out.println("File "+p+" generated");
    }

    /**
     * Map the version of each object
     *
     * @param rdf       Resource which contains version value
     * @param current   Element to add version value
     * @return          current Element with the version value within it
     */
    public static Element mapVersion(Resource rdf, Element current){
        Statement v = rdf.getProperty(Namespaces.version);
        if(v != null)
            current.setAttribute("version", v.getObject().toString());
        return current;
    }

    /**
     * Generate shared file
     *
     * @throws IOException
     */
    private void generate_sharedData() throws IOException{
        initXML();
        (new shared_data_XML(rdf,xml)).generate();
        saveXML("_OST_shared_data.xml"); //TODO: OST must be added dynamic
    }

    /**
     * Generate all <i>Lines</i> files
     *
     * @throws IOException
     */
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

    /**
     * Main method of the class. It runs all the logic.
     * First generate the shared files, then the individual Lines.
     * Finally generate a zip file with those
     */
    public void parse(){
        try {
            generate_sharedData();
            generate_LinesData();
            zip_files();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Zip the resulting folder with the NeTEx information
     *
     * @throws IOException
     */
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
