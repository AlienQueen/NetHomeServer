/**
 * Copyright (C) 2005-2015, Stefan Strömberg <stefangs@nethome.nu>
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

/*
 * This file is contributed by Jari Sarkka as part of his Jython integration
 * in OpenNetHome.
 */

package nu.nethome.home.impl;

import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * @author Jari Sarkka
 */
public class Python {
    private PyObject compiledCode = null;
    private String scriptSourceFileName = "/home/nethome/nethome.py";
    private long sourceFilelastModifiedDate = 0;
    private PythonInterpreter interp;
    private static Logger logger = Logger.getLogger(Python.class.getName());

    public Python(HomeServer server) {
        interp = new PythonInterpreter();
        interp.set("server", server);
        interp.set("log", logger);
    }

    public synchronized boolean callFunction(String functionCall) throws FileNotFoundException {
        reinterpretIfNeeded();
        String arguments = null;
        String functionName;
        int startIndex = functionCall.indexOf('(');
        if (startIndex >= 0) {
            functionName = functionCall.substring(0, startIndex);
            int endIndex = functionCall.lastIndexOf(')');
            if (endIndex > 0) {
                arguments = functionCall.substring(startIndex + 1, endIndex);
            }
        } else {
            functionName = functionCall;
        }
        PyObject func = interp.get(functionName);
        if (func != null) {
            if (arguments != null) {
                func.__call__(new PyString(arguments));
            } else {
                func.__call__();
            }
            return true;
        } else {
            return false;
        }
    }

    private void reinterpretIfNeeded() throws FileNotFoundException {
        File file = new File(getScriptSourceFileName());

        if (compiledCode == null || file.lastModified() > sourceFilelastModifiedDate) {
            compiledCode = compileScript(getScriptSourceFileName());
            interp.exec(compiledCode);
            sourceFilelastModifiedDate = file.lastModified();
        }
    }

    private PyObject compileScript(String fileName) throws FileNotFoundException {
        return interp.compile(new InputStreamReader(new FileInputStream(fileName)));
    }

    public String getScriptSourceFileName() {
        return scriptSourceFileName;
    }

    public void setScriptSourceFileName(String scriptSourceFileName) {
        this.scriptSourceFileName = scriptSourceFileName;
    }
}
