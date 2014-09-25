import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by forando on 06.04.14.
 * The starting point
 */
public class Host implements HostServer.HostServerListener {

    List<HashMap<String, Integer>> terminalRows;

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
    int[] clientValues;
    private int restOfClients;
    private final HostServer server;

    public static void main(String[] args) {

        new Host();
        //XMLVARIABLES variables = new XMLVARIABLES(APP.VARIABLES_PATH);
        /*final HostServer server = new HostServer();
        final DisplayForm form = new DisplayForm();
        server.addHostServerListener(new HostServer.HostServerListener() {
            @Override
            public void onTerminalServerMessage(HostServer.SocketOrganizer.SocketObject soc) {
                form.submitEvent(soc.message.terminal);
            }
        });
        server.start();
        form.addMainFormListener(new DisplayForm.MainFormListener() {
            @Override
            public void onAssignClient(int terminalIndex, int client) {
                int[] terminals = {terminalIndex};
                server.socketOrganizer.sendTerminals(terminals, SocketMessage.REQUEST_CLIENT, client);
            }

            @Override
            public void onAcceptClient(int terminalIndex, int client) {
                int[] terminals = {terminalIndex};
                server.socketOrganizer.sendTerminals(terminals, SocketMessage.ACCEPT_CLIENT, client);
            }

            @Override
            public void onHoldTerminals(int[] terminals, int val) {
                System.out.println("onHoldTerminals val = " + val);
                server.socketOrganizer.sendTerminals(terminals, SocketMessage.HOLD_TERMINAL, val);
            }
        });
        form.mainUIPanel.initialTerminalAssignmentCheck();*/



        /*GraphicsEnvironment env = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        GraphicsDevice vc = env.getDefaultScreenDevice();
        vc.setFullScreenWindow(form);*/
    }

    public Host(){
        server = new HostServer();
        server.start();
        initVariables();
    }

    private void initVariables() {
        variables = new XMLVARIABLES(APP.VARIABLES_PATH);
        variables.setLastClient(variables.getLastClient() + 1);
        lastClient = variables.getLastClient();

        clientValues = new int[APP.TERMINAL_QUANTITY];

        terminalRows = new ArrayList<>();

        for (int i=0; i<APP.TERMINAL_QUANTITY; i++){
            HashMap<String, Integer> terminalRowData = variables.getTerminalRowData(i);
            terminalRows.add(terminalRowData);
            clientValues[i] = terminalRowData.get("clientnumber");
        }

        nextClient = variables.getNextClient();

        buttonClicked = variables.getButtonClicked();
        ticketsPrinted = variables.getTicketsPrinted();
        clicksToChangeBattery = variables.getClicksToChangeBattery();
        ticketsToInsertPaper = variables.getTicketsToInsertPaper();
        standardBlinkRate = variables.getStandardBlinkRate();
        takeTicketBlinkRate = variables.getTakeTicketBlinkRate();

        if (nextClient == 0) {
            nextClient = lastClient;
        }

        variables.setNextClient(nextClient);

        setRestOfClients();
    }

    private void setRestOfClients() {
        if (nextClient > 0) {
            restOfClients = lastClient - nextClient + 1;
        } else {
            restOfClients = 0;
        }
    }

    private void executeSystemCommand(int command) {
        switch (command) {
            case APP.RESET_SYSTEM:
                total = 1;
                lastClient = 0;
                nextClient = 0;
                for (int i=0; i< APP.TERMINAL_QUANTITY; i++){
                    clientValues[i] = 0;
                    final HashMap<String, Integer> terminalData = terminalRows.get(i);
                    terminalData.put("clientnumber", 0);
                }

                variables.setLastClient(lastClient);
                variables.setNextClient(nextClient);

                SERVICE_STOPPED = false;
                triggerService(SERVICE_STOPPED, true);
                break;
            case APP.PRINTER_ERROR_ON:
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
                break;
            case APP.PRINTER_ERROR_OFF:
                if (!SERVICE_STOPPED) {
                    PRINTER_ERROR = false;
                    printTicket();
                    sendToDisplay(APP.PRINTER_ERROR_OFF, null);
                }
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
            default:
                break;
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
            sendToDisplay(APP.RESET_SERVICE, resettingSystem ? terminalRows : null);
            if (!PRINTER_ERROR){
                if (TICKET_TAKEN || resettingSystem) {
                    printTicket();
                }
            }
        }
    }

    private void printTicket(){
        server.socketOrganizer.sendPrinters(new int[]{0},
                new PrinterMessage(0, PrinterMessage.PRINT_TICKET, total, new Date(), true));
    }

    private void sendToDisplay(int operation, List<HashMap<String, Integer>>dataList){
        server.socketOrganizer.sendDisplays(new int[]{0},
                new DisplayMessage(0, operation, dataList, new Date(), true));
    }

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

    }

    @Override
    public void onDisplayAvailable(HostServer.SocketOrganizer.SocketObject soc) {
        DisplayMessage message = new DisplayMessage(soc.id, DisplayMessage.INIT_ROWS,
                terminalRows, new Date(), true);
        soc.send(message);
    }

    @Override
    public void onDisplayMessage(HostServer.SocketOrganizer.SocketObject soc) {

    }
}
