package nu.nethome.home.items.fineoffset;

import nu.nethome.home.impl.LocalHomeItemProxy;
import nu.nethome.home.items.util.TstEvent;
import nu.nethome.home.system.Event;
import nu.nethome.home.system.HomeService;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

/**
 *
 */
public class FooGadgetEnergyTest {

    private FooGadgetEnergy energyMeter;
    private Date now;
    private LocalHomeItemProxy proxy;

    @Before
    public void setUp() throws Exception {
        energyMeter = new FooGadgetEnergy(){
            @Override
            Date getCurrentTime() {
                return now;
            }
        };
        Calendar calendar = Calendar.getInstance();
        calendar.set(2014, Calendar.MAY, 25, 23, 30);
        now = calendar.getTime();
        energyMeter.setEnergyK("1");
        proxy = new LocalHomeItemProxy(energyMeter);
    }

    @Test
    public void canGetCurrentPower() throws Exception {
        pushValue(1, 1, 1);
        assertThat(proxy.getAttributeValue("Power"), is("60,000"));
    }

    @Test
    public void canGetCurrentValue() throws Exception {
        for (int i = 0; i < 15; i++) {
            pushValue(1, i + 1, 1);
            passTime(1);
        }
        assertThat(energyMeter.getValue(), is("60,000"));
    }

    @Test
    public void canCountLostValues() throws Exception {
        assertThat(proxy.getAttributeValue("LostSamples"), is("0"));
        pushValue(1, 1, 1);
        assertThat(proxy.getAttributeValue("LostSamples"), is("0"));
        pushValue(1, 2, 1);
        assertThat(proxy.getAttributeValue("LostSamples"), is("0"));
        pushValue(1, 4, 1);
        assertThat(proxy.getAttributeValue("LostSamples"), is("0"));
        pushValue(1, 8, 1);
        assertThat(proxy.getAttributeValue("LostSamples"), is("2"));
        pushValue(1, 11, 1);
        assertThat(proxy.getAttributeValue("LostSamples"), is("3"));
    }

    @Test
    public void canGetTotalEnergy() throws Exception {
        assertThat(proxy.getAttributeValue("TotalEnergy"), is("0,000"));
        proxy.setAttributeValue("TotalSavedPulses", "10");
        assertThat(proxy.getAttributeValue("TotalEnergy"), is("10,000"));
    }

    @Test
    public void canGetEnergyToday() throws Exception {
        assertThat(proxy.getAttributeValue("EnergyToday"), is(""));
        pushValue(0, 1, 1);
        assertThat(proxy.getAttributeValue("EnergyToday"), is("1,000"));
        pushValue(1, 2, 1);
        assertThat(proxy.getAttributeValue("EnergyToday"), is("2,000"));
    }

    @Test
    public void canGetEnergyTomorrow() throws Exception {
        assertThat(proxy.getAttributeValue("EnergyToday"), is(""));
        pushValue(0, 1, 1);
        assertThat(proxy.getAttributeValue("EnergyToday"), is("1,000"));
        passTime(60); // Passes 00.00
        pushValue(1, 2, 1);
        assertThat(proxy.getAttributeValue("EnergyToday"), is("1,000"));
    }

    private void passTime(int minutes) {
        Event event = new TstEvent(HomeService.MINUTE_EVENT_TYPE);
        for (int i = 0; i < minutes; i++) {
            now = new Date(now.getTime() + 1000 * 60);
            energyMeter.receiveEvent(event);
        }
    }

    private void pushValue(int previous, int prevCounter, int current) {
        Event prevEvent = new TstEvent("FooGadgetEnergy_Message");
        prevEvent.setAttribute("FooGadgetEnergy.Energy", previous);
        prevEvent.setAttribute("FooGadgetEnergy.Counter", prevCounter);
        energyMeter.receiveEvent(prevEvent);
        Event currEvent = new TstEvent("FooGadgetEnergy_Message");
        currEvent .setAttribute("FooGadgetEnergy.Energy", current);
        currEvent .setAttribute("FooGadgetEnergy.Counter", (prevCounter + 1) % 100);
        energyMeter.receiveEvent(currEvent );
    }
}
