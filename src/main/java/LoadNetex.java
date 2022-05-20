import org.entur.netex.NetexParser;
import org.entur.netex.index.api.NetexEntitiesIndex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LoadNetex {
    public static void main(String[] args) {
        String args_0 = "/Users/carlosmorote/Master local/TFM/OpenTripPlannerCompile/data/norway/netex_OST.zip";
        NetexEntitiesIndex index;
        NetexParser parser = new NetexParser();
        //File file = new File("/Users/carlosmorote/Master local/TFM/OpenTripPlannerCompile/data/norway/...");
        try {
            //index = parser.parse(new FileInputStream(file));
            index = parser.parse(args_0);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        index.getStopPlaceIdByQuayIdIndex();
    }
}
