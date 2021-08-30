package main.hr.kcosic.project.models;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Properties;

import main.hr.kcosic.project.Main;
import main.hr.kcosic.project.utils.LogUtils;
import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XmlProperties extends Properties {

    @Override
    public synchronized void load(InputStream inStream) throws IOException {
        try {
            parse(inStream);
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void store(OutputStream out, String comments) {
        try {
            Document document = createDocument("root");
            Element settings = document.createElement("settings");
            document.getDocumentElement().appendChild(settings);

            forEach((key, value)->{
                var entry = createElement(document, "entry");
                entry.appendChild(createElement(document, "key", key.toString()));
                entry.appendChild(createElement(document, "value", value.toString()));
                settings.appendChild(entry);
            });

            saveDocument(document, out);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }

    }


    private Document createDocument(String element) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation domImplementation = builder.getDOMImplementation();
        DocumentType documentType = null;
        try {
            LogUtils.logInfo("TEST->"+ Main.class.getResource("/assets/settings.dtd").toURI().toURL());

            documentType = domImplementation.createDocumentType("DOCTYPE", null, Main.class.getResource("/assets/settings.dtd").toURI().toURL().toString());
        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
        }
        return domImplementation.createDocument(null, element, documentType);
    }

    private Attr createAttribute(Document document, String name, String value) {
        Attr attr = document.createAttribute(name);
        attr.setValue(value);
        return attr;
    }

    private Node createElement(Document document, String tagName) {
        Element element = document.createElement(tagName);
        return element;
    }

    private Node createElement(Document document, String tagName, String data) {
        Element element = document.createElement(tagName);
        Text text = document.createTextNode(data);
        element.appendChild(text);
        return element;
    }

    private void saveDocument(Document document, OutputStream outputStream) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, document.getDoctype().getSystemId());
        transformer.transform(new DOMSource(document), new StreamResult(outputStream));
    }

    private void parse(InputStream inStream) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(SAXParseException exception) {
                System.err.println("Warning: " + exception);
            }

            @Override
            public void error(SAXParseException exception) {
                System.err.println("Error: " + exception);
            }

            @Override
            public void fatalError(SAXParseException exception) {
                System.err.println("Fatal error: " + exception);
            }
        });
        Document document = builder.parse(inStream);
        processNode(document.getDocumentElement());
    }

    private void processNode(Node node) {

        if((node.getNodeName().equals("entry"))){
            NodeList childNodes = node.getChildNodes();
            var key = "";
            var value = "";
            for (int i = 0; i < childNodes.getLength(); i++) {
                var childNode = childNodes.item(i);
                if(childNode.getNodeName().equals("key")){
                    key = childNode.getChildNodes().item(0).getNodeValue();
                }
                else if(childNode.getNodeName().equals("value")){
                    value = childNode.getChildNodes().item(0).getNodeValue();
                }
            }
            put(key,value);
        }

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            processNode(childNodes.item(i));
        }
    }

}
