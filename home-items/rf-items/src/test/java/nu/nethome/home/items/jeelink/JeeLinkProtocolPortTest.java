package nu.nethome.home.items.jeelink;

import org.junit.Ignore;
import org.junit.Test;

/**
 *
 */
public class JeeLinkProtocolPortTest {

    @Ignore
    @Test
    public void testReadingVersion() throws Exception {

        JeeLinkProtocolPort port = new JeeLinkProtocolPort(null);
        port.setSerialPort("COM7");
        port.open();

        // port.readArduinoVersion();

        port.close();
    }
}
