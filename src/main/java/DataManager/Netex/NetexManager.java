package DataManager.Netex;

import org.entur.netex.NetexParser;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.entur.netex.index.api.VersionedNetexEntityIndex;
import org.entur.netex.index.impl.NetexEntityMapByIdImpl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * Class intenden to be an abstraction to execute repetitive tasks over NeTEx data
 */
public class NetexManager {
    public NetexEntitiesIndex netex;

    /**
     * Class generator. Only takes as parameter a direction of a zip file which contains the NeTEx data.
     *
     * @param zip_path
     */
    public NetexManager(String zip_path) {
        NetexParser parser = new NetexParser();
        try {
            this.netex = parser.parse(zip_path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Relective method which returns the data absed on a property name.
     * For instance, to get <i>Line</i> information it must be <i>getData("Line")</i>
     *
     * @param property  Property as string. Such as, <i>Line</i>
     * @return          A <b>Collection</b> of instances of the property asked
     */
    public Collection getData(String property){
        String method = String.format("get%sIndex", property);
        try {
            return property.equals("Quay") ?
                    ((VersionedNetexEntityIndex) this.netex.getClass().getMethod(method).invoke(netex)).getLatestVersions() :
                    ((NetexEntityMapByIdImpl) this.netex.getClass().getMethod(method).invoke(netex)).getAll();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
