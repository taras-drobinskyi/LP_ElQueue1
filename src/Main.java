import helpers.SocketMessage;
import services.TerminalServer;
import services.interfaces.TerminalServerListener;

/**
 * Created by forando on 06.04.14.
 * The starting point
 */
public class Main {

    MainForm form;

    public static void main(String[] args) {
        //XMLVARIABLES variables = new XMLVARIABLES(APP.VARIABLES_PATH);
        final MainForm form = new MainForm();

        TerminalServer server = new TerminalServer();
        server.addTerminalServerListener(new TerminalServerListener() {
            @Override
            public void onTerminalMessage(TerminalServer.SocketOrganizer.SocketObject soc) {
                form.submitEvent(soc.message.value);
            }
        });
        server.start();



        /*GraphicsEnvironment env = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        GraphicsDevice vc = env.getDefaultScreenDevice();
        vc.setFullScreenWindow(form);*/
    }
}
