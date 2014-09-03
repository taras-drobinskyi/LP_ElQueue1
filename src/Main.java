import helpers.SocketMessage;
import services.TerminalServer;
import services.interfaces.TerminalServerListener;

/**
 * Created by forando on 06.04.14.
 * The starting point
 */
public class Main {

    public static void main(String[] args) {
        //XMLVARIABLES variables = new XMLVARIABLES(APP.VARIABLES_PATH);
        final TerminalServer server = new TerminalServer();
        final MainForm form = new MainForm();
        server.addTerminalServerListener(new TerminalServerListener() {
            @Override
            public void onTerminalServerMessage(TerminalServer.SocketOrganizer.SocketObject soc) {
                form.submitEvent(soc.message.terminal);
            }
        });
        server.start();
        form.addMainFormListener(new MainForm.MainFormListener() {
            @Override
            public void onAssignClient(int terminalIndex, int client) {
                int[] terminals = {terminalIndex};
                server.socketOrganizer.send(terminals, SocketMessage.REQUEST_CLIENT, client);
            }

            @Override
            public void onAcceptClient(int terminalIndex, int client) {
                int[] terminals = {terminalIndex};
                server.socketOrganizer.send(terminals, SocketMessage.ACCEPT_CLIENT, client);
            }

            @Override
            public void onHoldTerminals(int[] terminals, int val) {
                server.socketOrganizer.send(terminals, SocketMessage.HOLD_TERMINAL, val);
            }
        });



        /*GraphicsEnvironment env = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        GraphicsDevice vc = env.getDefaultScreenDevice();
        vc.setFullScreenWindow(form);*/
    }
}
