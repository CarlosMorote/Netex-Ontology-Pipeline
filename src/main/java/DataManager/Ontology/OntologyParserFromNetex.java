package DataManager.Ontology;

import DataManager.Netex.NetexManager;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.*;
import org.rutebanken.netex.model.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.stream.Collectors;

public class OntologyParserFromNetex implements OntologyParserInterface {

    private RDFManager rdfManager;
    private NetexManager netexManager;
    private String[] classesToCast;
    private Collection information;

    public OntologyParserFromNetex(RDFManager RDFManager, NetexManager netexManager) {
        this.rdfManager = RDFManager;
        this.netexManager = netexManager;
        this.classesToCast = new String[]{
                "Authority", "Operator", "JourneyPattern"
        };
    }

    public void castNetexToOntology(){
        for (String cls: this.classesToCast) {
            information = this.netexManager.getData(cls);
            information.stream().map(this::parse).collect(Collectors.toList());
        }
    }


    @Override
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
    public Authority mapAuthority(Authority authority) {
        String id = authority.getId();
        Resource authority_resource = this.rdfManager.rdf.createResource(Namespaces.CORE+"/Resource/Authority/"+id);
        this.rdfManager.addType(authority_resource, Namespaces.ORGANISATIONS+"#Authority");
        authority_resource.addProperty(RDFS.label, id);
        authority_resource.addProperty(SKOS.notation, authority.getCompanyNumber());
        authority_resource.addProperty(SKOS.prefLabel, authority.getName().getValue());

        return authority;
    }

    @Override
    public Operator mapOperator(Operator operator) {
        String id = operator.getId();
        Resource operator_resource = this.rdfManager.rdf.createResource(Namespaces.ORGANISATIONS+"/Resource/Operator/"+id);
        this.rdfManager.addType(operator_resource, Namespaces.ORGANISATIONS+"#Operator");
        operator_resource.addProperty(RDFS.label, id);
        operator_resource.addProperty(SKOS.notation, operator.getCompanyNumber());
        operator_resource.addProperty(VCARD4.hasName, operator.getName().getValue());
        operator_resource.addProperty(VCARD4.hasURL, operator.getCustomerServiceContactDetails().getUrl());

        return operator;
    }

    // Metodo deprecated. Service link ni se puede ni es necesario mappearlo
    @Deprecated
    @Override
    public ServiceLink mapServiceLink(ServiceLink serviceLink) {
        String id = serviceLink.getId();
        Resource serviceLink_resource = this.rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/ServiceLink/"+id);
        this.rdfManager.addType(serviceLink_resource, Namespaces.JOURNEYS+"#ServiceLink");
        serviceLink_resource.addProperty(RDFS.label, id);

        AllModesEnumeration mode = serviceLink.getVehicleMode();
        if(mode != null)
            serviceLink_resource.addProperty(
                    Namespaces.getResource(this.rdfManager.rdf, Namespaces.COMMONS, "#vehicleMode"),
                    mode.value()
            );

        return serviceLink;
    }

    @Override
    public JourneyPattern mapJourneyPattern(JourneyPattern journeyPattern) {
        String id = journeyPattern.getId();
        Resource journeyPattern_resource = this.rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/JourneyPattern/"+id);
        this.rdfManager.addType(journeyPattern_resource, Namespaces.JOURNEYS+"#JourneyPattern");
        journeyPattern_resource.addProperty(RDFS.label, id);

        JourneyPatternHeadways_RelStructure headway = journeyPattern.getHeadways();
        if(headway != null) {
            journeyPattern_resource.addProperty(
                Namespaces.getResource(this.rdfManager.rdf, Namespaces.JOURNEYS, "#headway"),
                headway.getId() //TODO. SE SUPONE QUE ES DATETIMESTAMP
            );
        }

        return journeyPattern;
    }
}






















