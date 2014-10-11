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

package nu.nethome.home.items.net;

import nu.nethome.home.item.*;
import nu.nethome.home.system.Event;
import nu.nethome.util.plugin.Plugin;

import java.util.logging.Logger;

@SuppressWarnings("UnusedDeclaration")
@Plugin
@HomeItemType("Ports")
public class Message extends HomeItemAdapter implements HomeItem {

    public static final String EVENT_TYPE = "Message";
    public static final String TO = "To";
    public static final String SUBJECT = "Subject";
    public static final String BODY = "Body";
    public static final String DIRECTION = "Direction";
    public static final String OUT_BOUND = "Out";

    private final String MODEL = ("<?xml version = \"1.0\"?> \n"
			+ "<HomeItem Class=\"ValueLogger\" Category=\"Ports\"  >"
            + "  <Attribute Name=\"To\" Type=\"String\" Get=\"getTo\" Set=\"setTo\"  />"
            + "  <Attribute Name=\"Subject\" Type=\"String\" Get=\"getSubject\" 	Set=\"setSubject\" />"
            + "  <Attribute Name=\"Message\" Type=\"Text\" Get=\"getMessage\" 	Set=\"setMessage\" />"
            + "  <Action Name=\"Send\" Method=\"send\" Default=\"true\" />"
			+ "</HomeItem> ");

	private static Logger logger = Logger.getLogger(Message.class.getName());
    private String to;
    private String subject;
    private String message;

	public String getModel() {
		return MODEL;
	}

    public void send() {
        Event messageEvent = server.createEvent(EVENT_TYPE, "");
        messageEvent.setAttribute(TO, to);
        messageEvent.setAttribute(SUBJECT, subject);
        messageEvent.setAttribute(BODY, message);
        messageEvent.setAttribute(DIRECTION, OUT_BOUND);
        server.send(messageEvent);
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

