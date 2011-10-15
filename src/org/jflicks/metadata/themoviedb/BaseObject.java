/*
    This file is part of ATM.

    ATM is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ATM is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ATM.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.jflicks.metadata.themoviedb;

import java.io.Serializable;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * This is a base object that all objects from themoviedb.org will
 * need to extend.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseObject implements Serializable {

    private Element element;

    /**
     * An extension needs to handle all their specific data needs.
     */
    public abstract void handle();

    /**
     * Default empty constructor.
     */
    public BaseObject() {
    }

    /**
     * Constructor with the required argument.
     *
     * @param e The element that will be examined for data.
     */
    public BaseObject(Element e) {

        setElement(e);
        handle();
    }

    /**
     * An object gets it's information from an element.
     *
     * @return This objects element.
     */
    public Element getElement() {
        return (element);
    }

    /**
     * An object gets it's information from an element.
     *
     * @param e This objects element.
     */
    public void setElement(Element e) {
        element = e;
    }

    /**
     * Given an element and a name find a child element value.
     *
     * @param e The given parent element.
     * @param name The child element name.
     * @return The value of the element.
     */
    public String expectElement(Element e, String name) {

        String result = null;

        if ((e != null) && (name != null)) {

            Element child = e.getChild(name);
            if (child != null) {

                result = child.getText();
            }
        }

        return (result);
    }

    /**
     * Given an element and a name find an attribute value.
     *
     * @param e The given parent element.
     * @param name The attribute name.
     * @return The value of the attribute.
     */
    public String expectAttribute(Element e, String name) {

        String result = null;

        if ((e != null) && (name != null)) {

            Attribute attr = e.getAttribute(name);
            if (attr != null) {

                result = attr.getValue();
            }
        }

        return (result);
    }

    /**
     * Given an Element name and children's name find an array of Elements.
     * This method finds "arrays or repeatable structures in an XML
     * document.
     *
     * @param e The given parent element.
     * @param name The child of the parent.
     * @param children The child of the name property.
     * @return An array of elements.
     */
    public Element[] expectElements(Element e, String name, String children) {

        Element[] result = null;

        if ((e != null) && (name != null)) {

            Element parent = e.getChild(name);
            if (parent != null) {

                List list = parent.getChildren(children);
                if ((list != null) && (list.size() > 0)) {

                    result = new Element[list.size()];
                    for (int i = 0; i < result.length; i++) {
                        result[i] = (Element) list.get(i);
                    }
                }
            }
        }

        return (result);
    }

    public void dump(Document doc) {

        System.out.println("----------------------------");
        Format f = Format.getPrettyFormat();
        f.setEncoding("ISO-8859-1");
        XMLOutputter out = new XMLOutputter(f);
        System.out.println(out.outputString(doc));
        System.out.println("----------------------------");
    }
}

