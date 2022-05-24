package DataManager.Netex;

import org.entur.netex.NetexParser;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.entur.netex.index.impl.NetexEntityMapByIdImpl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class NetexManager {
    private NetexEntitiesIndex netex;

    public NetexManager(String zip_path) {
        NetexParser parser = new NetexParser();
        try {
            this.netex = parser.parse(zip_path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection getData(String property){
        String method = String.format("get%sIndex", property);
        try {
            return ((NetexEntityMapByIdImpl) this.netex.getClass().getMethod(method).invoke(netex)).getAll();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: No existe metodo de exportación. Hay que crearlo de cero.
    public void exportData(String out_path){
    }
}
