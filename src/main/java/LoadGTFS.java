import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.onebusaway.csv_entities.EntityHandler;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.serialization.GtfsReader;

import java.io.File;

public class LoadGTFS {
    public static void main(String[] args) throws Exception {
        // Parametros hardcoded
        String args_0 = "/Users/carlosmorote/Master local/TFM/OpenTripPlannerCompile/data/madrid/madrid_metro.gtfs.zip";

        // GTFS
        GtfsReader reader = new GtfsReader();
        reader.setInputLocation(new File(args_0));

        /**
         * You can register an entity handler that listens for new objects as they
         * are read
         */
        //reader.addEntityHandler(new GtfsEntityHandler());

        /**
         * Or you can use the internal entity store, which has references to all the
         * loaded entities
         */
        GtfsDaoImpl store = new GtfsDaoImpl();
        reader.setEntityStore(store);

        reader.run();

        // Access entities through the store
        for (Route route : store.getAllRoutes()) {
            System.out.println("route: " + route.getLongName());
        }

    }

    private static class GtfsEntityHandler implements EntityHandler {

        @Override
        public void handleEntity(Object bean) {
            if(bean instanceof Stop){
                Stop stop = (Stop) bean;
                System.out.println("stop: " + stop.getName());
            }
        }
    }
}
