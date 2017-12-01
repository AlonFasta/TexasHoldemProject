import XMLobject.GameDescriptor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;

public class SimpleJAXBMain {
    private static String FILE_NAME;

    public SimpleJAXBMain(String fileName) {
        FILE_NAME = fileName;
    }

    public static GameDescriptor fromXmlFileToObject() throws Exception {
        File file = new File(FILE_NAME);
        if (!file.getPath().endsWith(".xml"))
            throw new Exception("Xml Provided is not valid xml format");
        if (!file.exists())
            throw new Exception(file.getName() + " Not Found.");

            JAXBContext jaxbContext = JAXBContext.newInstance(GameDescriptor.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            GameDescriptor game = (GameDescriptor) jaxbUnmarshaller.unmarshal(file);
            return game;

    }
}
