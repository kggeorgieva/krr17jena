import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class E04FamilyTree {

    static final String familyUri = "http://family/";
    static final String relationshipUri = "http://purl.org/vocab/relationship/";

    public static Model createFamilyTreeModel() {

        Model model = ModelFactory.createDefaultModel();

        model.setNsPrefix("rela", relationshipUri);

        // Create the types of Property we need to describe relationships in the model
        List<String> allProperties =
                Arrays.asList("siblingOf", "spouseOf", "parentOf", "childOf");
        Map<String, Property> properties = allProperties.stream()
                .map(predicate -> model.createProperty(relationshipUri, predicate))
                .collect(Collectors.toMap(Property::getLocalName, Function.identity()));

        // Create resources representing the people in our model
        List<String> allPeople =
                Arrays.asList("adam", "dotty", "beth", "chuck", "edward", "fan", "greg", "harriet");

        Map<String, Resource> resources = allPeople.stream().map(name -> model.createResource(familyUri + name))
                .collect(Collectors.toMap(Resource::getLocalName, Function.identity()));

        Property siblingOf = properties.get("siblingOf");
        resources.get("adam").addProperty(siblingOf, resources.get("beth"));
        resources.get("beth").addProperty(siblingOf, resources.get("adam"));
        resources.get("edward").addProperty(siblingOf, resources.get("greg"));
        model.add(model.createStatement(resources.get("greg"), siblingOf, resources.get("edward")));

        Property spouseOf = properties.get("spouseOf");
        resources.get("adam").addProperty(spouseOf, resources.get("dotty"));
        resources.get("beth").addProperty(spouseOf, resources.get("chuck"));
        resources.get("fan").addProperty(spouseOf, resources.get("greg"));
        resources.get("dotty").addProperty(spouseOf, resources.get("adam"));
        resources.get("chuck").addProperty(spouseOf, resources.get("beth"));
        resources.get("greg").addProperty(spouseOf, resources.get("fan"));

        Property parentOf = properties.get("parentOf");
        Property childOf = properties.get("childOf");
        resources.get("fan").addProperty(parentOf, resources.get("harriet"));
        resources.get("greg").addProperty(parentOf, resources.get("harriet"));
        resources.get("harriet").addProperty(childOf, resources.get("fan"));
        resources.get("harriet").addProperty(childOf, resources.get("greg"));

        resources.get("adam").addProperty(parentOf, resources.get("edward"));
        resources.get("dotty").addProperty(parentOf, resources.get("edward"));
        resources.get("adam").addProperty(parentOf, resources.get("greg"));
        resources.get("dotty").addProperty(parentOf, resources.get("greg"));
        resources.get("edward").addProperty(childOf, resources.get("adam"));
        resources.get("greg").addProperty(childOf, resources.get("adam"));
        resources.get("edward").addProperty(childOf, resources.get("dotty"));
        resources.get("greg").addProperty(childOf, resources.get("dotty"));

        model.write(System.out, "RDF/XML");

        return model;
    }
}
