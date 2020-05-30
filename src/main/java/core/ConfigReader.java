package core;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ConfigReader {
    private static DocumentBuilder documentBuilder;
    private static Document document;
    private static List<ConfigParams> configList;

    public static List<ConfigParams> getConfigList() {
        return configList;
    }

    public static void init() {
        try {
            configList = new ArrayList<>();
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = documentBuilder.parse(new File(
                    Objects.requireNonNull(ConfigReader.class.getClassLoader()
                            .getResource("config.xml")).getFile()
            ));
            parseFormat();
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace(System.out);
        }
    }

    private static String getText(Node node) {
        return node.getChildNodes().item(0).getTextContent().trim();
    }

    private static String getBetterConf(String conf) {
        return conf.toLowerCase().trim();
    }

    private static void parseConfig(NodeList propList, String vendor) {
        ConfigParams config = new ConfigParams();
        config.setVendor(vendor);

        for (int h = 0; h < propList.getLength(); h++) {
            Node dbName = propList.item(h);
            NodeList dbNameList = dbName.getChildNodes();

            if (dbName.getNodeName().toLowerCase().trim().equals("params")) {
                if (dbName.getTextContent() != null)
                    config.makeParams(
                            Arrays.asList(dbName.getTextContent().split("\n")));
                else config.setParams("");
            }

            if (dbName.getNodeName().toLowerCase().trim().equals("conn")) {
                for (int j = 0; j < dbNameList.getLength(); j++) {
                    Node props = dbNameList.item(j);
                    if (props != null && props.getNodeName() != null && props.getNodeType() != Node.TEXT_NODE) {
                        String conf;
                        switch (props.getNodeName().toLowerCase().trim()) {
                            case "alias":
                                conf = getText(props);
                                config.setAlias(conf);
                                break;
                            case "port":
                                conf = getText(props);
                                config.setPort(getBetterConf(conf));
                                break;
                            case "dbname":
                                conf = getText(props);
                                config.setDbName(getBetterConf(conf));
                                break;
                            case "host":
                                conf = getText(props);
                                config.setHost(getBetterConf(conf));
                                break;
                            case "user":
                                conf = getText(props);
                                config.setUserName(getBetterConf(conf));
                                break;
                            case "pass":
                                conf = getText(props);
                                config.setUserPass(getBetterConf(conf));
                                break;
                        }
                    }
                }
            }

            configList.add(config);
        }
    }

    private static void parseFormat() {
        Node root = document.getDocumentElement();
        NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node itemProps = list.item(i);
            NodeList propList = itemProps.getChildNodes();
            if (itemProps.getNodeType() != Node.TEXT_NODE)
                parseConfig(propList, itemProps.getNodeName().toLowerCase().trim());
        }
    }

}
