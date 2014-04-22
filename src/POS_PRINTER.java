import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Created by forando on 06.04.14.
 * Class to perform printing to POS printer
 */
public class POS_PRINTER {
    String clearSettings;
    String cutP;
    String newLine;
    String alignCenter;
    String setSize;
    String setUpsideDown;

    public POS_PRINTER() {

        clearSettings = new String(new char[]{0x1b, '@'});
        cutP = new String(new char[]{0x1d, 'V', 1});
        newLine = new String(new char[]{0x0a, 0x0a, 0x0a, 0x0a});
        alignCenter = new String(new char[]{0x1b, 'a', 1});
        setSize = new String(new char[]{0x1d, '!', 68});
        setUpsideDown = new String(new char[]{0x1b, '{', 1});

    }

    public boolean Print(int data) {
        try {

            //open printer as if it were a file
            FileOutputStream os = new FileOutputStream("/dev/usb/lp0");
            //wrap stream in "friendly" PrintStream
            PrintStream ps = new PrintStream(os);

            String Ptxt = clearSettings + alignCenter + setSize + setUpsideDown +
                    data + " \n \n" + newLine + cutP;


            //print text here
            ps.println(Ptxt);

            //form feed -- this is important
            //Without the form feed, the text
            //will simply sit in the print
            //buffer until something else
            //gets printed.
            ps.print("\f");

            //flush buffer and close
            ps.close();
            return true;
        } catch (Exception ex) {
            System.out.println("Exception occurred: " + ex);
            return false;
        }
    }

    public boolean Reset() {
        try {

            //open printer as if it were a file
            FileOutputStream os = new FileOutputStream("/dev/usb/lp0");
            //wrap stream in "friendly" PrintStream
            PrintStream ps = new PrintStream(os);

            String Ptxt = clearSettings;


            //print text here
            ps.println(Ptxt);

            //form feed -- this is important
            //Without the form feed, the text
            //will simply sit in the print
            //buffer until something else
            //gets printed.
            ps.print("\f");

            //flush buffer and close
            ps.close();
            return true;
        } catch (Exception ex) {
            System.out.println("Exception occurred: " + ex);
            return false;
        }
    }
}
