/*
 * Copyright (c) 2014. This code is a LogosProg property. All Rights Reserved.
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * This Class gets connected to external xml file with all project META DATA
 */
public class XMLVARIABLES {
    String path = null;
    Document doc = null;

    public XMLVARIABLES(String path) {
        this.path = path;
        try {
            this.doc = getDocument();
            doc.getDocumentElement().normalize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Возвращает объект Document, который является объектным представлением
     * XML документа.
     */
    private Document getDocument() throws Exception {
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setValidating(false);
            DocumentBuilder builder = f.newDocumentBuilder();
            return builder.parse(new File(path));
        } catch (Exception exception) {
            String message = "XML parsing error!";
            throw new Exception(message);
        }
    }

    private void saveDocument() {
        DOMSource source = new DOMSource(doc);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(path);
            transformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private Element getLastClientNode() {
        Element rootElement = doc.getDocumentElement();
        NodeList counters_List = rootElement.getElementsByTagName("counters");
        Element counters_Node = (Element) counters_List.item(0);

        NodeList currentinfo_List = counters_Node.getElementsByTagName("currentinfo");
        Element currentinfo_Node = (Element) currentinfo_List.item(0);

        NodeList lastclient_List = currentinfo_Node.getElementsByTagName("lastclient");
        return (Element) lastclient_List.item(0);
    }

    public int getLastClient() {
        Element lastclient_Node = getLastClientNode();
        return Integer.valueOf(lastclient_Node.getTextContent());
    }

    public void setLastClient(int val) {
        Element lastclient_Node = getLastClientNode();
        lastclient_Node.setTextContent(String.valueOf(val));
        saveDocument();
    }

    private Element getNextClientNode() {
        Element rootElement = doc.getDocumentElement();
        NodeList counters_List = rootElement.getElementsByTagName("counters");
        Element counters_Node = (Element) counters_List.item(0);

        NodeList currentinfo_List = counters_Node.getElementsByTagName("currentinfo");
        Element currentinfo_Node = (Element) currentinfo_List.item(0);

        NodeList nextclient_List = currentinfo_Node.getElementsByTagName("nextclient");
        return (Element) nextclient_List.item(0);
    }

    public int getNextClient() {
        Element nextclient_Node = getNextClientNode();
        return Integer.valueOf(nextclient_Node.getTextContent());
    }

    public void setNextClient(int val) {
        Element nextclient_Node = getNextClientNode();
        nextclient_Node.setTextContent(String.valueOf(val));
        saveDocument();
    }

    private Element getButtonClickedNode() {
        Element rootElement = doc.getDocumentElement();
        NodeList counters_List = rootElement.getElementsByTagName("counters");
        Element counters_Node = (Element) counters_List.item(0);

        NodeList servicecounters_List = counters_Node.getElementsByTagName("servicecounters");
        Element servicecounters_Node = (Element) servicecounters_List.item(0);

        NodeList buttonclicked_List = servicecounters_Node.getElementsByTagName("buttonclicked");
        return (Element) buttonclicked_List.item(0);
    }

    public int getButtonClicked() {
        Element buttonclicked_Node = getButtonClickedNode();
        return Integer.valueOf(buttonclicked_Node.getTextContent());
    }

    public void setButtonClicked(int val) {
        Element buttonclicked_Node = getButtonClickedNode();
        buttonclicked_Node.setTextContent(String.valueOf(val));
        saveDocument();
    }

    private Element getTicketsPrintedNode() {
        Element rootElement = doc.getDocumentElement();
        NodeList counters_List = rootElement.getElementsByTagName("counters");
        Element counters_Node = (Element) counters_List.item(0);

        NodeList servicecounters_List = counters_Node.getElementsByTagName("servicecounters");
        Element servicecounters_Node = (Element) servicecounters_List.item(0);

        NodeList ticketsprinted_List = servicecounters_Node.getElementsByTagName("ticketsprinted");
        return (Element) ticketsprinted_List.item(0);
    }

    public int getTicketsPrinted() {
        Element TicketsPrinted_Node = getTicketsPrintedNode();
        return Integer.valueOf(TicketsPrinted_Node.getTextContent());
    }

    public void setTicketsPrinted(int val) {
        Element TicketsPrinted_Node = getTicketsPrintedNode();
        TicketsPrinted_Node.setTextContent(String.valueOf(val));
        saveDocument();
    }

    private Element getTerminalNode(int position) {
        Element rootElement = doc.getDocumentElement();
        NodeList terminals_List = rootElement.getElementsByTagName("terminals");
        Element terminals_Node = (Element) terminals_List.item(0);

        NodeList terminal_List = terminals_Node.getElementsByTagName("terminal");
        return (Element) terminal_List.item(position);
    }

    private Element getClientaAsignedNode(Element terminal_Node) {
        NodeList clientassigned_List = terminal_Node.getElementsByTagName("clientassigned");
        return (Element) clientassigned_List.item(0);
    }

    public int getClientAsigned(int position) {
        Element Terminal_Node = getTerminalNode(position - 1);
        Element clientassigned_Node = getClientaAsignedNode(Terminal_Node);
        return Integer.valueOf(clientassigned_Node.getTextContent());
    }

    public void setClientAsigned(int position, int val) {
        Element Terminal_Node = getTerminalNode(position - 1);
        Element clientassigned_Node = getClientaAsignedNode(Terminal_Node);
        clientassigned_Node.setTextContent(String.valueOf(val));
        saveDocument();
    }

    private Element getSettingsNode() {
        Element rootElement = doc.getDocumentElement();
        NodeList settings_List = rootElement.getElementsByTagName("settings");
        return (Element) settings_List.item(0);
    }

    public int getClicksToChangeBattery() {
        Element settings_Node = getSettingsNode();
        NodeList clicksToChangeBattery_List = settings_Node.getElementsByTagName("clicksToChangeBattery");
        Element clicksToChangeBattery_Node = (Element) clicksToChangeBattery_List.item(0);
        return Integer.valueOf(clicksToChangeBattery_Node.getTextContent());
    }

    public void setClicksToChangeBattery(int val) {
        Element settings_Node = getSettingsNode();
        NodeList clicksToChangeBattery_List = settings_Node.getElementsByTagName("clicksToChangeBattery");
        Element clicksToChangeBattery_Node = (Element) clicksToChangeBattery_List.item(0);
        clicksToChangeBattery_Node.setTextContent(String.valueOf(val));
        saveDocument();
    }

    public int getTicketsToInsertPaper() {
        Element settings_Node = getSettingsNode();
        NodeList ticketsToInsertPaper_List = settings_Node.getElementsByTagName("ticketsToInsertPaper");
        Element ticketsToInsertPaper_Node = (Element) ticketsToInsertPaper_List.item(0);
        return Integer.valueOf(ticketsToInsertPaper_Node.getTextContent());
    }

    public void setTicketsToInsertPaper(int val) {
        Element settings_Node = getSettingsNode();
        NodeList ticketsToInsertPaper_List = settings_Node.getElementsByTagName("ticketsToInsertPaper");
        Element ticketsToInsertPaper_Node = (Element) ticketsToInsertPaper_List.item(0);
        ticketsToInsertPaper_Node.setTextContent(String.valueOf(val));
        saveDocument();
    }

    public int getStandardBlinkRate() {
        Element settings_Node = getSettingsNode();
        NodeList standardBlinkRate_List = settings_Node.getElementsByTagName("standardBlinkRate");
        Element standardBlinkRate_Node = (Element) standardBlinkRate_List.item(0);
        return Integer.valueOf(standardBlinkRate_Node.getTextContent());
    }

    public void setStandardBlinkRate(int val) {
        Element settings_Node = getSettingsNode();
        NodeList standardBlinkRate_List = settings_Node.getElementsByTagName("standardBlinkRate");
        Element standardBlinkRate_Node = (Element) standardBlinkRate_List.item(0);
        standardBlinkRate_Node.setTextContent(String.valueOf(val));
        saveDocument();
    }

    public int getTakeTicketBlinkRate() {
        Element settings_Node = getSettingsNode();
        NodeList takeTicketBlinkRate_List = settings_Node.getElementsByTagName("takeTicketBlinkRate");
        Element takeTicketBlinkRate_Node = (Element) takeTicketBlinkRate_List.item(0);
        return Integer.valueOf(takeTicketBlinkRate_Node.getTextContent());
    }

    public void setTakeTicketBlinkRate(int val) {
        Element settings_Node = getSettingsNode();
        NodeList takeTicketBlinkRate_List = settings_Node.getElementsByTagName("takeTicketBlinkRate");
        Element takeTicketBlinkRate_Node = (Element) takeTicketBlinkRate_List.item(0);
        takeTicketBlinkRate_Node.setTextContent(String.valueOf(val));
        saveDocument();
    }
}
