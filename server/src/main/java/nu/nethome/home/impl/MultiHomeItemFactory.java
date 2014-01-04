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

package nu.nethome.home.impl;

import nu.nethome.home.item.HomeItem;
import nu.nethome.home.item.HomeItemInfo;

import java.util.*;

public class MultiHomeItemFactory implements HomeItemFactory {

    private List<HomeItemFactory> itemFactories = new ArrayList<HomeItemFactory>();

    public MultiHomeItemFactory(HomeItemFactory... factories) {
            addFactories(factories);
    }

    public void addFactories(HomeItemFactory... factories) {
        for (HomeItemFactory factory: factories) {
            itemFactories.add(factory);
        }
    }

    @Override
    public HomeItem createInstance(String className) {
        for (HomeItemFactory factory : itemFactories) {
            HomeItem item = factory.createInstance(className);
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    @Override
    public List<String> listClasses(boolean includeHidden) {
        Set<String> result = new HashSet<String>();
        for (HomeItemFactory factory : itemFactories) {
            result.addAll(factory.listClasses(includeHidden));
        }
        ArrayList<String> sortedResult = new ArrayList<String>(result);
        Collections.sort(sortedResult);
        return sortedResult;
    }

    @Override
    public List<HomeItemInfo> listItemTypes() {
        Set<HomeItemInfo> result = new HashSet<HomeItemInfo>();
        for (HomeItemFactory factory : itemFactories) {
            result.addAll(factory.listItemTypes());
        }
        ArrayList<HomeItemInfo> sortedResult = new ArrayList<HomeItemInfo>(result);
        Collections.sort(sortedResult, new Comparator<HomeItemInfo>() {
            @Override
            public int compare(HomeItemInfo o1, HomeItemInfo o2) {
                return o1.getClassName().compareTo(o2.getClassName());
            }
        });
        return sortedResult;
    }
}
