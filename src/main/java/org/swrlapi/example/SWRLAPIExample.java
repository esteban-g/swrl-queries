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
import java.util.Optional;
import org.semanticweb.owlapi.model.IRI;

public class SWRLAPIExample {

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
            System.out.println("\t INFO:" + peopleIRI);

            OWLOntology ontology = owlFile.isPresent()
                    ? ontologyManager.loadOntologyFromOntologyDocument(owlFile.get())
                    : ontologyManager.createOntology();

            // Create SQWRL query engine using the SWRLAPI
            SQWRLQueryEngine queryEngine = SWRLAPIFactory.createSQWRLQueryEngine(ontology);

            // Create SQWRL query engine using the SWRLAPI
            SQWRLQueryEngine queryEngine2 = SWRLAPIFactory.createSQWRLQueryEngine(peopleLOntology);

            // Create and execute a SQWRL query using the SWRLAPI
            SQWRLResult result = queryEngine.runSQWRLQuery("q1", "swrlb:add(?x, 2, 2) -> sqwrl:select(?x)");

            SQWRLResult result2 = queryEngine2.runSQWRLQuery("q2", "people:adult(?i) -> sqwrl:select(?i)");
            

// Process the SQWRL result
            if (result2.next()) {
                System.out.println("query result: " );
                System.out.println("\t data:"+result2.getNamedIndividual(0));
             
                
                
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
        System.err.println("Usage: " + SWRLAPIExample.class.getName() + " [ <owlFileName> ]");
        System.exit(1);
    }
}
