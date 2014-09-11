/**
 * Created by forando on 06.04.14.
 * The starting point
 */
public class Main {

    public static void main(String[] args) {
        //XMLVARIABLES variables = new XMLVARIABLES(APP.VARIABLES_PATH);
        final HostServer server = new HostServer();
        final DisplayForm form = new DisplayForm();
        server.addHostServerListener(new HostServerListener() {
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
                server.socketOrganizer.send(terminals, SocketMessage.REQUEST_CLIENT, client);
            }

            @Override
            public void onAcceptClient(int terminalIndex, int client) {
                int[] terminals = {terminalIndex};
                server.socketOrganizer.send(terminals, SocketMessage.ACCEPT_CLIENT, client);
            }

            @Override
            public void onHoldTerminals(int[] terminals, int val) {
                System.out.println("onHoldTerminals val = " + val);
                server.socketOrganizer.send(terminals, SocketMessage.HOLD_TERMINAL, val);
            }
        });
        form.mainUIPanel.initialTerminalAssignmentCheck();



        /*GraphicsEnvironment env = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        GraphicsDevice vc = env.getDefaultScreenDevice();
        vc.setFullScreenWindow(form);*/
    }
}
