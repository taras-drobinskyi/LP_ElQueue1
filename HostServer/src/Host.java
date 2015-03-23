import display.TerminalData;
import main.APP;
import main.XMLVARIABLES;
import sockets.DisplayMessage;
import sockets.PrinterMessage;
import sockets.SocketMessage;
import sockets.TerminalMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by forando on 06.04.14.
 * The starting point
 */
public class Host implements HostServer.HostServerListener {

    public static final String TAG = "Host";

    List<Terminal>terminals;

    static int standardBlinkRate;
    static int clicksToChangeBattery;
    static int ticketsToInsertPaper;
    static int takeTicketBlinkRate;

    private boolean PRINTER_ERROR = false;
    private boolean SERVICE_STOPPED = false;
    private boolean TICKET_TAKEN = false;
    private boolean TICKET_IS_PRINTING = false;


    int lastClient = 0;
    int nextClient = 0;
    int buttonClicked = 0;
    int ticketsPrinted = 0;

    XMLVARIABLES variables;
    private int total = 0;
    private int usedLevels;

    private final HostServer server;

    public static void main(String[] args) {

        new Host();
    }

    public Host(){
        server = new HostServer();
        server.addHostServerListener(this);
        server.start();
        initVariables();
    }

    private void initVariables() {
        variables = new XMLVARIABLES(APP.VARIABLES_PATH);
        variables.setLastClient(variables.getLastClient() + 1);
        lastClient = variables.getLastClient();

        //fixme: the next line is just for testing and must be deleted when printer is connected
        total = lastClient +1;

        terminals = new ArrayList<>();
        usedLevels = 0;
        for (int i=0; i<APP.TERMINAL_QUANTITY; i++){
            HashMap<String, Integer> terminalData = variables.getTerminalRowData(i);
            Terminal terminal = new Terminal(terminalData);
            terminals.add(terminal);
            if (terminal.visible) usedLevels++;
        }

        nextClient = variables.getNextClient();

        buttonClicked = variables.getButtonClicked();
        ticketsPrinted = variables.getTicketsPrinted();
        clicksToChangeBattery = variables.getClicksToChangeBattery();
        ticketsToInsertPaper = variables.getTicketsToInsertPaper();
        standardBlinkRate = variables.getErrorBlinkRate();
        takeTicketBlinkRate = variables.getDefaultBlinkRate();

        if (nextClient == 0) {
            nextClient = lastClient;
        }

        variables.setNextClient(nextClient);
    }

    private int getRestOfClients() {
        int restOfClients;
        if (nextClient > 0) {
            restOfClients = lastClient - nextClient + 1;
        } else {
            restOfClients = 0;
        }
        return restOfClients;
    }

    private void resetSystem(){
        total = 1;
        lastClient = 0;
        nextClient = 0;

        for (Terminal t : terminals){
            t.clientNumber = 0;
            t.visible = false;
            t.levelIndex = -1;
            t.state = TerminalData.ACCEPTED;
            t.saveToXML();
        }

        variables.setLastClient(lastClient);
        variables.setNextClient(nextClient);

        SERVICE_STOPPED = false;
        triggerService(SERVICE_STOPPED, true);
    }

    private void startPrinterError(){
        if (!SERVICE_STOPPED) {
            PRINTER_ERROR = true;
            ticketsPrinted = 0;
            variables.setTicketsPrinted(ticketsPrinted);
            lastClient++;
            total = lastClient + 1;
            variables.setLastClient(lastClient);
            if (nextClient == 0) {
                nextClient = lastClient;
            }
            variables.setNextClient(nextClient);
            sendToDisplay(APP.PRINTER_ERROR_ON, null);
        }
    }

    private void stopPrinterError(){
        if (!SERVICE_STOPPED) {
            PRINTER_ERROR = false;
            printTicket();
            sendToDisplay(APP.PRINTER_ERROR_OFF, null);
        }
    }

    /**
     * By Service means possibility to accept new clients
     * @param turnOn Flag that indicates whether Service must be stopped
     * @param flags [Optional] Flags array, now consists only of one item that
     * indicates whether System is resetting to 0 values
     */
    private void triggerService (boolean turnOn, boolean... flags){
        boolean resettingSystem = false;
        if (flags.length>0){
            resettingSystem = flags[0];
        }
        if (turnOn){
            sendToDisplay(APP.STOP_SERVICE, null);

        }else{
            sendToDisplay(APP.RESET_SERVICE, resettingSystem ? terminals : null);
            if (!PRINTER_ERROR){
                if (TICKET_TAKEN || resettingSystem) {
                    printTicket();
                }
            }
        }
    }

    private void printTicket(){
        if (!PRINTER_ERROR) {
            if (TICKET_TAKEN){
                server.socketOrganizer.sendPrinters(new int[]{0},
                        new PrinterMessage(0, PrinterMessage.PRINT_TICKET, total, new Date(), true));

                ticketsPrinted++;
                variables.setTicketsPrinted(ticketsPrinted);
                TICKET_TAKEN = false;
                sendToDisplay(APP.PRINT_TICKET, null);
            }else {
                if (!SERVICE_STOPPED) {
                    server.socketOrganizer.sendPrinters(new int[]{0},
                            new PrinterMessage(0, PrinterMessage.PRINT_TICKET, total, new Date(), true));
                    ticketsPrinted++;
                    variables.setTicketsPrinted(ticketsPrinted);
                    sendToDisplay(APP.PRINT_TICKET, null);
                } else {
                    TICKET_TAKEN = true;
                    sendToDisplay(APP.PRINT_TICKET, null);
                }
            }
        }
    }

    private List<HashMap<String, Integer>> getTerminalsDataHashMapList(){
        List<HashMap<String, Integer>> terminalRows = new ArrayList<>();
        for (int i=0; i< APP.TERMINAL_QUANTITY; i++){
            HashMap<String, Integer>terminalData = new HashMap<>();
            Terminal t = terminals.get(i);
            terminalData.put("levelindex", t.levelIndex);
            terminalData.put("clientnumber", t.clientNumber);
            terminalData.put("terminalnumber", t.terminalNumber);
            terminalData.put("visible", t.visible ? 1 : 0);
            terminalData.put("state", t.state);

            terminalRows.add(terminalData);
        }
        return terminalRows;
    }

    private void sendToDisplay(int operation, List<Terminal>dataList){
        int[] IDs = server.socketOrganizer.getDisplaysIDs();
        if (dataList != null){
            List<TerminalData>listToSend = new ArrayList<>();
            for (Terminal t: dataList){
                listToSend.add(new TerminalData(t.levelIndex, t.clientNumber, t.terminalNumber, t.visible,t.state));
            }
            server.socketOrganizer.sendDisplays(server.socketOrganizer.getDisplaysIDs(),
                    new DisplayMessage(IDs[0], operation, listToSend, getRestOfClients(), new Date(), true));
        }else{
            server.socketOrganizer.sendDisplays(server.socketOrganizer.getDisplaysIDs(),
                    new DisplayMessage(IDs[0], operation, null, getRestOfClients(), new Date(), true));
        }

    }

    /*private void assignTerminal(int terminalIndex) {

        Terminal terminal = terminals.get(terminalIndex);
        System.out.println("assignTerminal index = " + terminalIndex);

        if (terminal.state == TerminalData.ACCEPTED) {
            if (nextClient > 0) {
                terminal.clientNumber = nextClient;
                if (nextClient < lastClient) {
                    nextClient++;
                } else {
                    nextClient = 0;
                }
                terminal.saveToXML();
                terminal.resetFromXML();
                List<Terminal> terminalsToSend = new ArrayList<>();
                terminalsToSend.add(terminal);
                sendToDisplay(DisplayMessage.ADD_ROW, terminalsToSend);
                terminal.state = TerminalData.WAITING;
                terminal.levelIndex = usedLevels;
                terminal.visible = true;
                usedLevels++;
                terminal.saveToXML();
                variables.setNextClient(nextClient);
            }
        }else if (terminal.state == TerminalData.WAITING){
            List<Terminal> terminalsToSend = new ArrayList<>();
            terminalsToSend.add(terminal);
            sendToDisplay(DisplayMessage.DELETE_ROW, terminalsToSend);

            for (Terminal r : terminals){
                if (r.levelIndex > terminal.levelIndex){
                    r.levelIndex --;
                    r.saveToXML();
                }
            }
            terminal.state = TerminalData.ACCEPTED;
            terminal.levelIndex = -1;
            terminal.visible = false;
            usedLevels--;
            terminal.saveToXML();
        }
        *//*buttonClicked++;
        variables.setButtonClicked(buttonClicked);*//*
    }*/

    private int requestClient(int terminalIndex){
        int client = -1;
        Terminal terminal = terminals.get(terminalIndex);
        System.out.println(TAG + ".requestClient: terminalIndex = " + terminalIndex);
        if (terminal.state == TerminalData.ACCEPTED) {
            if (nextClient > 0) {
                terminal.clientNumber = nextClient;
                if (nextClient < lastClient) {
                    nextClient++;
                } else {
                    nextClient = 0;
                }
                terminal.saveToXML();
                terminal.resetFromXML();
                client = terminal.clientNumber;
                List<Terminal> terminalsToSend = new ArrayList<>();
                terminalsToSend.add(terminal);
                sendToDisplay(DisplayMessage.ADD_ROW, terminalsToSend);
                terminal.state = TerminalData.WAITING;
                terminal.levelIndex = usedLevels;
                terminal.visible = true;
                usedLevels++;
                terminal.saveToXML();
                variables.setNextClient(nextClient);
            }
        }
        return client;
    }

    private void acceptClient(int terminalIndex){
        Terminal terminal = terminals.get(terminalIndex);
        System.out.println(TAG + ".acceptClient: terminalIndex = " + terminalIndex);
        if (terminal.state == TerminalData.WAITING){
            List<Terminal> terminalsToSend = new ArrayList<>();
            terminalsToSend.add(terminal);
            sendToDisplay(DisplayMessage.DELETE_ROW, terminalsToSend);

            for (Terminal r : terminals){
                if (r.levelIndex > terminal.levelIndex){
                    r.levelIndex --;
                    r.saveToXML();
                }
            }
            terminal.state = TerminalData.ACCEPTED;
            terminal.levelIndex = -1;
            terminal.visible = false;
            usedLevels--;
            terminal.saveToXML();
        }
    }


    private class Terminal extends TerminalData implements Serializable{

        public Terminal(HashMap<String, Integer> terminalData) {
            super(terminalData.get("levelindex"), terminalData.get("clientnumber"),
                    terminalData.get("terminalnumber"), terminalData.get("visible")==1? true:false,
                    terminalData.get("state"));
        }

        private void saveToXML(){
            HashMap<String, Integer> terminalRowData = new HashMap<>();
            terminalRowData.put("levelindex", levelIndex);
            terminalRowData.put("terminalnumber", terminalNumber);
            terminalRowData.put("clientnumber", clientNumber);
            if (visible){
                terminalRowData.put("visible", 1);
            }else {
                terminalRowData.put("visible", 0);
            }
            terminalRowData.put("state", state);
            variables.setTerminalRowData(terminalNumber, terminalRowData);
        }

        private void resetFromXML(){
            HashMap<String, Integer> terminalRowData = variables.getTerminalRowData(terminalNumber);
            this.levelIndex = terminalRowData.get("levelindex");
            this.clientNumber = terminalRowData.get("clientnumber");
            this.terminalNumber = terminalRowData.get("terminalnumber");
            int visibility = terminalRowData.get("visible");
            this.visible = visibility == 1;
            this.state = terminalRowData.get("state");
        }

        private HashMap<String, Integer>  getTerminalHashMap(){
            HashMap<String, Integer>terminalData = new HashMap<>();
            terminalData.put("levelindex", levelIndex);
            terminalData.put("clientnumber", clientNumber);
            terminalData.put("terminalnumber", terminalNumber);
            terminalData.put("visible", visible ? 1 : 0);
            terminalData.put("state", state);
            return terminalData;
        }
    }

    /*private void batteryCheck() {
        if (buttonClicked > clicksToChangeBattery) {
            errorSound.Play();
            //The next code will be invoked after main form is finally resized.
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    int width = mediaContentPanel.getSize().width;
                    int height = mediaContentPanel.getSize().height;
                    SystemMessageForm f = new SystemMessageForm(width, height);
                    f.addMessageFormListener(new SystemMessageFormListener() {
                        @Override
                        public void onReset() {
                            buttonClicked = 0;
                            variables.setButtonClicked(buttonClicked);
                            redrawLines();
                        }

                        @Override
                        public void onClose() {
                            redrawLines();
                        }

                        @Override
                        public void onPrintTicket() {
                            printTicket();
                        }
                    });
                }
            });
        }
    }*/

    @Override
    public void onPrinterAvailable(HostServer.SocketOrganizer.SocketObject soc) {
        //Print first ticket
        total = lastClient + 1;
        PrinterMessage message = new PrinterMessage(soc.id, PrinterMessage.PRINT_TICKET,
                total, new Date(), true);
       soc.send(message);
    }

    @Override
    public void onPrinterMessage(HostServer.SocketOrganizer.SocketObject soc) {

    }

    @Override
    public void onTerminalMessage(HostServer.SocketOrganizer.SocketObject soc) {
        TerminalMessage message = (TerminalMessage)soc.message;
        switch (message.operation){
            case SocketMessage.SOCKET_READY:
                Terminal terminal = terminals.get(message.id);
                if (terminal.state == Terminal.WAITING && terminal.visible){
                    message.operation = TerminalMessage.REQUEST_CLIENT;
                    message.value = terminal.clientNumber;
                    message.received = true;
                    message.date = new Date();
                    soc.send(message);
                }
                break;
            case TerminalMessage.REQUEST_CLIENT:
                message.value = requestClient(message.id);
                message.operation = TerminalMessage.REQUEST_CLIENT;
                message.received = true;
                message.date = new Date();
                soc.send(message);
                break;
            case TerminalMessage.ACCEPT_CLIENT:
                acceptClient(message.id);
                message.operation = TerminalMessage.ACCEPT_CLIENT;
                message.received = true;
                message.date = new Date();
                soc.send(message);
                break;
            default:
                System.out.println(TAG + ".onTerminalMessage: Host server has received a message, " +
                        "but message.operation has not been recognized. message.operation = " + message.operation);
                break;
        }
    }

    @Override
    public void onDisplayAvailable(HostServer.SocketOrganizer.SocketObject soc) {
        List<TerminalData>listToSend = new ArrayList<>();
        for (Terminal t: terminals){
            listToSend.add(new TerminalData(t.levelIndex,t.clientNumber,t.terminalNumber,t.visible,t.state));
        }
        DisplayMessage message = new DisplayMessage(soc.id, DisplayMessage.INIT_ROWS,
                listToSend, getRestOfClients(), new Date(), true);

        //----test-------------
        /*List<TerminalData>dataList = new ArrayList<>();
        dataList.add(new TerminalData(0,1,1,1,0));
        sockets.DisplayMessage m = new sockets.DisplayMessage(soc.id, 200, listToSend, 1, new Date(), true);
        Object obj = m;*/
        //----test------------

        soc.send(message);
    }

    @Override
    public void onDisplayMessage(HostServer.SocketOrganizer.SocketObject soc) {
        DisplayMessage message = (DisplayMessage)soc.message;
        switch (message.operation){
            case SocketMessage.SOCKET_READY:
                List<TerminalData>listToSend = new ArrayList<>();
                for (Terminal t: terminals){
                    listToSend.add(new TerminalData(t.levelIndex,t.clientNumber,t.terminalNumber,t.visible,t.state));
                }
                message.operation = DisplayMessage.INIT_ROWS;
                message.terminals = listToSend;
                message.restOfClients = getRestOfClients();
                message.date = new Date();
                soc.send(message);
                break;
            case SocketMessage.HOLD_CLIENT:
                break;
            case APP.PRINT_TICKET:
                if(!TICKET_IS_PRINTING) {
                    total++;
                    lastClient = total - 1;
                    if (nextClient == 0) {
                        nextClient = lastClient;
                    }
                    variables.setLastClient(lastClient);
                    variables.setNextClient(nextClient);
                    printTicket();
                }
                break;
            case DisplayMessage.ADD_ROW:
                requestClient(message.terminals.get(0).terminalNumber);
                break;
            case DisplayMessage.DELETE_ROW:
                acceptClient(message.terminals.get(0).terminalNumber);
                break;
            case APP.RESET_SYSTEM:
                resetSystem();
                break;
            case APP.PRINTER_ERROR_ON:
                startPrinterError();
                break;
            case APP.PRINTER_ERROR_OFF:
                stopPrinterError();
                break;
            case APP.STOP_SERVICE:
                SERVICE_STOPPED = true;
                triggerService(SERVICE_STOPPED);
                break;
            case APP.RESET_SERVICE:
                SERVICE_STOPPED = false;
                triggerService(SERVICE_STOPPED);
                break;
            case APP.TRIGGER_SERVICE:
                SERVICE_STOPPED = !SERVICE_STOPPED;
                triggerService(SERVICE_STOPPED);
                break;
            default:
                System.out.println(TAG + ".onDisplayMessage: Host server has received a message, " +
                        "but message.operation has not been recognized. message.operation = " + message.operation);
                break;
        }
    }
}
