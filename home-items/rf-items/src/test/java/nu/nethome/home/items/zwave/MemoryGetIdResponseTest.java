package nu.nethome.home.items.zwave;

import nu.nethome.home.items.zwave.messages.Event;
import nu.nethome.home.items.zwave.messages.MemoryGetIdResponse;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 *
 */
public class MemoryGetIdResponseTest {
    @Test
    public void canDecodeKnownData() throws Exception, Event.DecoderException {

        MemoryGetIdResponse response = new MemoryGetIdResponse(Hex.hexStringToByteArray("0120F9819C1C01"));

        assertThat(response.nodeId, is(1));
        assertThat(response.homeId, is(0xF9819C1C));
    }
}
