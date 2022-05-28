package Parsers;

import DataManager.Netex.NetexManager;
import DataManager.Ontology.Namespaces;
import DataManager.Ontology.RDFManager;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.*;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
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
                "Authority", "Operator", "ScheduledStopPoint", "JourneyPattern"
        };
    }

    public void castNetexToOntology(){
        for (String cls: this.classesToCast) {
            information = this.netexManager.getData(cls);
            information.stream().map(this::parse).collect(Collectors.toList());
            System.out.println(cls + " Mapped");
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
    public Resource mapAuthority(Authority authority) {
        String id = authority.getId();
        Resource authority_resource = this.rdfManager.rdf.createResource(Namespaces.CORE+"/Resource/Authority/"+id);
        this.rdfManager.addType(authority_resource, Namespaces.ORGANISATIONS+"#Authority");
        authority_resource.addProperty(RDFS.label, id);
        authority_resource.addProperty(SKOS.notation, authority.getCompanyNumber());
        authority_resource.addProperty(SKOS.prefLabel, authority.getName().getValue());

        return authority_resource;
    }

    @Override
    public Resource mapOperator(Operator operator) {
        String id = operator.getId();
        Resource operator_resource = this.rdfManager.rdf.createResource(Namespaces.ORGANISATIONS+"/Resource/Operator/"+id);
        this.rdfManager.addType(operator_resource, Namespaces.ORGANISATIONS+"#Operator");
        operator_resource.addProperty(RDFS.label, id);
        operator_resource.addProperty(SKOS.notation, operator.getCompanyNumber());
        operator_resource.addProperty(VCARD4.hasName, operator.getName().getValue());
        operator_resource.addProperty(VCARD4.hasURL, operator.getCustomerServiceContactDetails().getUrl());

        return operator_resource;
    }

    // Metodo deprecated. Service link ni se puede ni es necesario mappearlo
    @Deprecated
    @Override
    public Resource mapServiceLink(ServiceLink serviceLink) {
        String id = serviceLink.getId();
        Resource serviceLink_resource = this.rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/ServiceLink/"+id);
        this.rdfManager.addType(serviceLink_resource, Namespaces.JOURNEYS+"#ServiceLink");
        serviceLink_resource.addProperty(RDFS.label, id);

        AllModesEnumeration mode = serviceLink.getVehicleMode();
        if(mode != null)
            serviceLink_resource.addProperty(
                    Namespaces.getProperty(this.rdfManager.rdf, Namespaces.COMMONS, "#vehicleMode"),
                    mode.value()
            );

        return serviceLink_resource;
    }

    @Override
    public Resource mapScheduledStopPoint(ScheduledStopPoint scheduledStopPoint) {
        String id = scheduledStopPoint.getId();
        Resource scheduledStopPoint_resource = this.rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/ScheduledStopPoint/"+id);
        this.rdfManager.addType(scheduledStopPoint_resource, Namespaces.JOURNEYS+"#ScheduledStopPoint");
        scheduledStopPoint_resource.addLiteral(SchemaDO.name, scheduledStopPoint.getName().getValue());

        return scheduledStopPoint_resource;
    }

    @Override
    public Resource mapJourneyPattern(JourneyPattern journeyPattern) {
        String id = journeyPattern.getId();
        Resource journeyPattern_resource = this.rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/ServiceJourneyPattern/"+id);
        this.rdfManager.addType(journeyPattern_resource, Namespaces.JOURNEYS+"#ServiceJourneyPattern");
        journeyPattern_resource.addProperty(RDFS.label, id);
        journeyPattern_resource.addProperty(SchemaDO.name, journeyPattern.getName().getValue());
        journeyPattern_resource.addProperty(Namespaces.onRoute, journeyPattern.getRouteRef().getRef());

        journeyPattern.getPointsInSequence()
                .getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern()
                .forEach((point) -> {
                    mapStopPointInJourneyPattern((StopPointInJourneyPattern) point, journeyPattern_resource);
                });


        return journeyPattern_resource;
    }

    @Override
    public Resource mapStopPointInJourneyPattern(StopPointInJourneyPattern point, Resource journeyPattern_resource) {
        String id_point = point.getId();
        Resource stopPointInJourneyPattern = rdfManager.rdf.createResource(Namespaces.JOURNEYS+"/Resource/StopPointsInJourneyPattern/"+id_point);
        journeyPattern_resource.addProperty(Namespaces.journeyPatternMadeUpOf, stopPointInJourneyPattern);
        rdfManager.addType(stopPointInJourneyPattern, Namespaces.JOURNEYS+"#StopPointsInJourneyPattern");

        Boolean forAlighting = point.isForAlighting();
        if(forAlighting != null)
            stopPointInJourneyPattern.addLiteral(Namespaces.forAlighting, forAlighting);

        JAXBElement<? extends ScheduledStopPointRefStructure> scheduledStopPoint = ((StopPointInJourneyPattern) point).getScheduledStopPointRef();
        if(scheduledStopPoint != null){
            Resource stop_resource = this.rdfManager.rdf.getResource(Namespaces.JOURNEYS+"/Resource/ScheduledStopPoint/"+id_point);
            stopPointInJourneyPattern.addProperty(Namespaces.scheduledStopPoint, stop_resource);
        }

        return stopPointInJourneyPattern;
    }

    @Override
    public Resource mapRoute(Route route) {
        return null;
    }
}






















