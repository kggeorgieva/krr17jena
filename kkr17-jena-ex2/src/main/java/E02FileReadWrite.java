import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class E02FileReadWrite {

    public static void writeToFile(File file, Model model) {

        try (FileOutputStream f1 = new FileOutputStream(file)) {
            //TODO try different formats
            RDFWriter d = model.getWriter("RDF/XML-ABBREV");
            d.write(model, f1, null);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Model readFromFile(String fileName) {
        return null;
    }

    public static void main(String[] args) {

        Model model = E04FamilyTree.createFamilyTreeModel();
        //TODO Check if file not exists then create it
        writeToFile(new File(E02FileReadWrite.class.getClassLoader()
                .getResource("family.rdf").getPath()), model);

    }
}
