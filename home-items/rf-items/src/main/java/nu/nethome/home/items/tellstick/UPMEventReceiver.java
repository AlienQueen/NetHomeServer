/**
 * Copyright (C) 2005-2013, Stefan Strömberg <stefangs@nethome.nu>
 *
 * This file is part of OpenNetHome  (http://www.nethome.nu)
 *
 * OpenNetHome is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenNetHome is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package nu.nethome.home.items.tellstick;

import nu.nethome.coders.decoders.UPMDecoder;
import nu.nethome.util.ps.ProtocolDecoderSink;

public class UPMEventReceiver extends TellstickEventReceiverAdaptor {

    UPMDecoder decoder = new UPMDecoder();

    public UPMEventReceiver(ProtocolDecoderSink sink) {
        decoder.setTarget(sink);
    }

    @Override
    public void processActiveEvent(TellstickEvent event) {
        long binaryData = event.getData() | 0xC00000000L;
        decoder.decodeMessage(binaryData);
    }

    @Override
    public String getEventType() {
        return "protocol:mandolyn;model:temperaturehumidity";
    }

    @Override
    public String getProtocolName() {
        return "UPM";
    }
}
