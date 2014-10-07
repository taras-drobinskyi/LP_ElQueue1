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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public int getUSEDlevels(){
        Element rootElement = doc.getDocumentElement();
        NodeList terminals_List = rootElement.getElementsByTagName("terminals");
        Element terminals_Node = (Element) terminals_List.item(0);

        return Integer.valueOf(terminals_Node.getAttribute("usedlevels"));
    }

    public void setUSEDlevels(int val){
        Element rootElement = doc.getDocumentElement();
        NodeList terminals_List = rootElement.getElementsByTagName("terminals");
        Element terminals_Node = (Element) terminals_List.item(0);

        terminals_Node.setAttribute("usedlevels", String.valueOf(val));
    }

    public HashMap<String, Integer> getTerminalRowData(int position) {
        HashMap<String, Integer> terminalRowData = new HashMap<String, Integer>();
        Element Terminal_Node = getTerminalNode(position);
        int levelindex = Integer.valueOf(Terminal_Node.getAttribute("levelindex"));
        terminalRowData.put("levelindex", levelindex);
        int terminalnumber = Integer.valueOf(Terminal_Node.getAttribute("terminalnumber"));
        terminalRowData.put("terminalnumber", terminalnumber);
        Element clientassigned_Node = getClientaAsignedNode(Terminal_Node);
        int clientnumber = Integer.valueOf(clientassigned_Node.getTextContent());
        terminalRowData.put("clientnumber", clientnumber);
        int visible = Integer.valueOf(Terminal_Node.getAttribute("visible"));
        terminalRowData.put("visible", visible);
        int state = Integer.valueOf(Terminal_Node.getAttribute("state"));
        terminalRowData.put("state", state);

        return terminalRowData;
    }

    public void setTerminalRowData(int position, HashMap<String, Integer> terminalRowData) {
        Element Terminal_Node = getTerminalNode(position);
        Terminal_Node.setAttribute("levelindex", String.valueOf(terminalRowData.get("levelindex")));
        Terminal_Node.setAttribute("terminalnumber", String.valueOf(terminalRowData.get("terminalnumber")));
        Terminal_Node.setAttribute("visible", String.valueOf(terminalRowData.get("visible")));
        Terminal_Node.setAttribute("state", String.valueOf(terminalRowData.get("state")));
        Element clientassigned_Node = getClientaAsignedNode(Terminal_Node);
        clientassigned_Node.setTextContent(String.valueOf(terminalRowData.get("clientnumber")));
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

    public int getErrorBlinkRate() {
        Element settings_Node = getSettingsNode();
        NodeList errorBlinkRate_List = settings_Node.getElementsByTagName("errorBlinkRate");
        Element errorBlinkRate_Node = (Element) errorBlinkRate_List.item(0);
        return Integer.valueOf(errorBlinkRate_Node.getTextContent());
    }

    public void setErrorBlinkRate(int val) {
        Element settings_Node = getSettingsNode();
        NodeList errorBlinkRate_List = settings_Node.getElementsByTagName("errorBlinkRate");
        Element errorBlinkRate_Node = (Element) errorBlinkRate_List.item(0);
        errorBlinkRate_Node.setTextContent(String.valueOf(val));
        saveDocument();
    }

    public int getDefaultBlinkRate() {
        Element settings_Node = getSettingsNode();
        NodeList defaultBlinkRate_List = settings_Node.getElementsByTagName("defaultBlinkRate");
        Element defaultBlinkRate_Node = (Element) defaultBlinkRate_List.item(0);
        return Integer.valueOf(defaultBlinkRate_Node.getTextContent());
    }

    public void setDefaultBlinkRate(int val) {
        Element settings_Node = getSettingsNode();
        NodeList defaultBlinkRate_List = settings_Node.getElementsByTagName("defaultBlinkRate");
        Element defaultBlinkRate_Node = (Element) defaultBlinkRate_List.item(0);
        defaultBlinkRate_Node.setTextContent(String.valueOf(val));
        saveDocument();
    }

    private Element getMediaContentNode() {
        Element rootElement = doc.getDocumentElement();
        NodeList mediacontent_List = rootElement.getElementsByTagName("mediacontent");
        return (Element) mediacontent_List.item(0);
    }

    private Element getVideosNode() {
        Element mediacontent_Node = getMediaContentNode();
        NodeList videos_List = mediacontent_Node.getElementsByTagName("videos");
        return (Element) videos_List.item(0);
    }

    private Element getMessagesNode() {
        Element mediacontent_Node = getMediaContentNode();
        NodeList messages_List = mediacontent_Node.getElementsByTagName("messages");
        return (Element) messages_List.item(0);
    }

    public List<String> getMessages(){
        List<String> messages = new ArrayList<String>();
        Element messages_Node = getMessagesNode();
        NodeList message_List = messages_Node.getElementsByTagName("message");
        for(int i=0; i< message_List.getLength(); i++){
            Element message_Node = (Element) message_List.item(i);
            messages.add(message_Node.getTextContent());
        }
        if (messages.size()>0) {
            return messages;
        }else{
            return null;
        }
    }

    public HashMap<String, String> getCurrentVieoData(){
        HashMap<String, String> video = new HashMap<>();
        String path = "";
        String attr = "";
        Element videocontent_Node = getVideosNode();
        Element videopath_Node = (Element)videocontent_Node.getElementsByTagName("currentvideopath").item(0);
        path = videopath_Node.getTextContent();
        path = path.trim();
        attr = videopath_Node.getAttribute("play");
        video.put("path", path);
        video.put("play", attr);
        if (path == ""){
            return null;
        }else {
            return video;
        }
    }

    public HashMap<String, String> getNewVieoData(){
        HashMap<String, String> video = new HashMap<>();
        String path = "";
        String attr = "";
        Element videocontent_Node = getVideosNode();
        Element videopath_Node = (Element)videocontent_Node.getElementsByTagName("newvideopath").item(0);
        path = videopath_Node.getTextContent();
        path = path.trim();
        attr = videopath_Node.getAttribute("change");
        video.put("path", path);
        video.put("change", attr);
        if (path == ""){
            return null;
        }else {
            return video;
        }
    }
}
