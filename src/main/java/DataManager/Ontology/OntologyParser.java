package DataManager.Ontology;

import org.rutebanken.netex.model.*;

import java.lang.reflect.InvocationTargetException;

public class OntologyParser implements OntologyParserInterface {

    RDFManager RDFManager;

    public OntologyParser(RDFManager RDFManager) {
        this.RDFManager = RDFManager;
    }

    public Object parse(Object o){
        String method = String.format("map%s", o.getClass().getSimpleName());

        try {
            return this.getClass().getMethod(method, o.getClass()).invoke(this, o);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Operator mapOperator(Operator operator) {
        OntologyEntityClasses entity = OntologyEntityClasses.ORGANISATIONS;
        String ont_class = "Operator";

        String name = operator.getName().getValue();

        this.RDFManager.addIndividual(entity, ont_class, name);
        return operator;
    }

    @Override
    public Branding mapBranding(Branding branding) {
        return null;
    }
}






















