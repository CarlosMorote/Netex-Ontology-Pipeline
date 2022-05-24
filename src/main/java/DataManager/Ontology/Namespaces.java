package DataManager.Ontology;

public class Namespaces {
    private String TRANSMODEL_ROOT = "https://w3id.org/mobility/transmodel/";
    private String SKOS_ROOT = "http://www.w3.org/2004/02/skos/";

    private String FACILITIES = this.TRANSMODEL_ROOT + "facilities#";
    private String COMMONS = this.TRANSMODEL_ROOT + "commons#";
    private String JOURNEYS = this.TRANSMODEL_ROOT + "journeys#";
    private String CORE = this.SKOS_ROOT + "core#";
    private String ORGANISATIONS = this.TRANSMODEL_ROOT + "organisations#";

    public String getTRANSMODEL_ROOT() {
        return TRANSMODEL_ROOT;
    }

    public String getSKOS_ROOT() {
        return SKOS_ROOT;
    }

    public String getFACILITIES() {
        return FACILITIES;
    }

    public String getCOMMONS() {
        return COMMONS;
    }

    public String getJOURNEYS() {
        return JOURNEYS;
    }

    public String getCORE() {
        return CORE;
    }

    public String getORGANISATIONS() {
        return ORGANISATIONS;
    }
}
