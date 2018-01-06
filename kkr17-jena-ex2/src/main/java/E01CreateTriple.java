import org.apache.jena.graph.Graph;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.sparql.core.DatasetImpl;
import org.apache.jena.vocabulary.DC_11;
import org.apache.jena.vocabulary.VCARD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class E01CreateTriple {
    /**
     * Inspired by
     * https://www.vbox7.com/play:2409a886
     */

    public static final String NS = "http://krr17/jena-example/1.0#";
    private static final Logger logger = LoggerFactory.getLogger(E01CreateTriple.class);

    public static void main(String[] args) {

        String personUUID = NS + "PegyziMuzovski";
        String firstName = "Pegyzi";
        String lastName = "Muzovski";
        String fullName = firstName + " " + lastName;
        String nickName = "Barda";
        String addressUUID = NS + "Psihodispansera";

        Model model = ModelFactory.createDefaultModel();

        Resource pegyzMuzResource = model.createResource(personUUID);
        Resource addressResource = model.createResource(addressUUID);

        //Playing with W3C ontologies. Here we use predefined properties
        //VCARD for people and organizations - https://www.w3.org/TR/vcard-rdf/

        //FN = Full name
        pegyzMuzResource
                .addProperty(VCARD.FN, fullName)
                .addProperty(VCARD.NICKNAME, nickName)
                .addProperty(VCARD.ADR, addressResource);

        //Building simple hierarchy
        pegyzMuzResource.addProperty(VCARD.N,
                model.createResource()
                        .addProperty(VCARD.Given, firstName)
                        .addProperty(VCARD.Family, lastName));

        // DC Dublin Core Metadata Initiative -https://www.w3.org/wiki/DublinCore
        pegyzMuzResource.addProperty(DC_11.creator, pegyzMuzResource);

        //Create custom properties
        Property runFrom = new PropertyImpl(NS, "runFrom");
        pegyzMuzResource.addProperty(runFrom, addressResource);

        //Writing RDF models - https://jena.apache.org/documentation/io/rdf-output.html
        //Checkout the different formats for knowledge graph representation
        logger.info("Writing as RDF/XML");
        model.write(System.out);

        logger.info("Writing as turtle");
        model.write(System.out, "TURTLE");

        logger.info("Writing as variation of json-ld");
        RDFDataMgr.write(System.out, model, RDFFormat.JSONLD_COMPACT_PRETTY);

        logger.info("Writing as variation of RDF/JSON");
        RDFDataMgr.write(System.out, model, RDFFormat.RDFJSON);

        logger.info("Writing as N3");
        RDFDataMgr.write(System.out, model, RDFFormat.NTRIPLES);

        //DataSet vs Model vs Graph
        //https://stackoverflow.com/questions/6981467/jena-arq-difference-between-model-graph-and-dataset

        Graph graph = model.getGraph();
        Dataset dataSet = new DatasetImpl(model);

        //Playing with the Model interface
        Resource personResource = model.getResource(personUUID);
        Statement statement = personResource.getProperty(runFrom);//The triple Pegyzi runFrom Psihodispansera
        Resource subject = statement.getSubject();
        Property predicate = statement.getPredicate();
        RDFNode object = statement.getObject(); //!This is not a resource
        String literal = subject.getProperty(VCARD.N).getProperty(VCARD.Given).getString();

        StmtIterator listProps = personResource.listProperties();
        logger.info("Printing properties of resource..");
        while (listProps.hasNext()) {
            System.out.println(" " + listProps.nextStatement()
                    .getObject()
                    .toString());
        }

        StmtIterator listStatements = model.listStatements();
        logger.info("Printing statements of a model..");
        while (listStatements.hasNext()) {
            System.out.println(" " + listStatements.nextStatement()
                    .getObject()
                    .toString());
        }
    }
}
