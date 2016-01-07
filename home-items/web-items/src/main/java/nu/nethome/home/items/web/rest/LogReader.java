/**
 * Copyright (C) 2005-2014, Stefan Strömberg <stefangs@nethome.nu>
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

package nu.nethome.home.items.web.rest;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import nu.nethome.home.item.HomeItemProxy;
import nu.nethome.home.item.ValueItemLoggerFactory;
import nu.nethome.home.item.ValueItemLoggerFileBased;
import nu.nethome.home.item.ValueItemLogger;
import nu.nethome.home.system.HomeService;
import nu.nethome.home.system.ServiceConfiguration;

public class LogReader {
    private static final SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private HomeService service;
    private ServiceConfiguration config;

    public LogReader(HomeService server) {
        this.service = server;
        this.config = service.getConfiguration();
    }

    public List<Object[]> getLog(String startTimeString, String stopTimeString, HomeItemProxy item) throws IOException {
        if (item == null) {
            return Collections.emptyList();
        }
        Date startTime = parseParameterDate(startTimeString);
        Date stopTime = parseParameterDate(stopTimeString);

        if (stopTime == null) {
            stopTime = new Date();
        }
        if (startTime == null) {
            startTime = oneWeekBack(stopTime);
        }

        // Global logger wins if exists!
        String fileName = config.getValueItemLoggerDescriptor();

        if (StringUtils.isBlank(fileName)) {
            fileName = item.getAttributeValue("LogFile");
            if (fileName != null) {
                // TODO: WHY THIS?
                fileName = fromURL(fileName);
            }
        }

        String itemId = item.getAttributeValue(HomeItemProxy.ID_ATTRIBUTE);
        if (StringUtils.isBlank(fileName) || StringUtils.isBlank(itemId)) {
            return Collections.emptyList();
        }

        ValueItemLogger logger = ValueItemLoggerFactory.createValueItemLogger(fileName);

        // Must handle LoggerComponentFileBased specially
        if (logger instanceof ValueItemLoggerFileBased) {
            fileName = getFullFileName(fileName);
        }

        if (logger == null) {
            return Collections.emptyList();
        }
        return logger.loadBetweenDates(fileName, itemId, startTime, stopTime);
    }

    private String getFullFileName(String fileName) {
        if (fileName.contains(File.separator) || fileName.contains("/")) {
            return fileName;
        } else {
            return service.getConfiguration().getLogDirectory() + fileName;
        }
    }

    private static Date oneWeekBack(Date stopTime) {
        return new Date(stopTime.getTime() - 1000L * 60L * 60L * 24L * 7L);
    }

    private static Date parseParameterDate(String timeString) {
        Date result = null;
        try {
            if (timeString != null) {
                result = inputDateFormat.parse(timeString);
            }
        } catch (ParseException e1) {
            // Silently ignore
        }
        return result;
    }

    public static String fromURL(String aURLFragment) {
        String result;
        try {
            result = URLDecoder.decode(aURLFragment, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("UTF-8 not supported", ex);
        }
        return result;
    }

}
