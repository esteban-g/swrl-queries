package org.swrlapi.example;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;

import java.io.File;
import java.util.AbstractSet;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

public class SWRLAPIExample3 {

    public static void main(String[] args) {
        if (args.length > 1) {
            Usage();
        }

        Optional<String> owlFilename = args.length == 0 ? Optional.<String>empty() : Optional.of(args[0]);
        Optional<File> owlFile = (owlFilename != null && owlFilename.isPresent())
                ? Optional.of(new File(owlFilename.get()))
                : Optional.<File>empty();

        File peopleOntologyFile = new File("src/main/resources/ontologyTest.owl");
        OWLOntology peopleLOntology;
        try {

            // Create an OWL ontology using the OWLAPI
            OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
            //OWLOntology ontoc = ontologyManager.loadOntology(iri)

            peopleLOntology = ontologyManager.loadOntologyFromOntologyDocument(peopleOntologyFile);
            System.out.println("People onto loaded" + peopleLOntology.getOntologyID());
            IRI peopleIRI = ontologyManager.getOntologyDocumentIRI(peopleLOntology);
            System.out.println("\t IRI:" + peopleIRI);

            // Create SQWRL query engine using the SWRLAPI
            SQWRLQueryEngine queryEngine2 = SWRLAPIFactory.createSQWRLQueryEngine(peopleLOntology);

            SQWRLResult result2 = queryEngine2.runSQWRLQuery("q2", "people:adult(?i) -> sqwrl:select(?i)");

            // Process the SQWRL result
            if (result2.next()) {
                System.out.println("query result 1: ");
                System.out.println("\t data 1:" + result2.getNamedIndividual(0));
            }

            SQWRLResult result3 = queryEngine2.runSQWRLQuery("q3", "people:adult(?p)^ people:eats(?p,?food) -> sqwrl:select(?food)");
            // Process the SQWRL result
            if (result3.next()) {
                System.out.println("query result 2: "+ result3.getNumberOfColumns() + "_"+ result3.getNumberOfRows());
                System.out.println("\t data 2:" + result3.getNamedIndividual(0));
            }
            while (result3.next()) {
                System.out.println("Adult: " + result3.getLiteral("adult").getString());
                System.out.println("Plant: " + result3.getLiteral("plant").getInteger());
            }
            /*
                if (result.next())
                  System.out.println("x: " + result.getLiteral("x").getInteger());
             */
        } catch (OWLOntologyCreationException e) {
            System.err.println("Error creating OWL ontology: " + e.getMessage());
            System.exit(-1);
        } catch (SWRLParseException e) {
            System.err.println("Error parsing SWRL rule or SQWRL query: " + e.getMessage());
            System.exit(-1);
        } catch (SQWRLException e) {
            System.err.println("Error running SWRL rule or SQWRL query: " + e.getMessage());
            System.exit(-1);
        } catch (RuntimeException e) {
            System.err.println("Error starting application: " + e.getMessage());
            System.exit(-1);
        }
    }

    private static void Usage() {
        System.err.println("Usage: " + SWRLAPIExample3.class.getName() + " [ <owlFileName> ]");
        System.exit(1);
    }
}
