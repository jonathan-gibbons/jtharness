/*
 * $Id$
 *
 * Copyright (c) 2006, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.sun.javatest.cof;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
/*import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
*/

/**
 * <p>Java class for TestAttributes complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TestAttributes">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="attribute" type="{http://qare.sfbay.sun.com/projects/COF/2003/2_0_2/Schema}TestAttribute" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
/*@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TestAttributes", propOrder = {
    "attribute"
})*/
public class COFTestAttributes extends COFItem {

    static LinkedHashMap<String, String> xmlAttributes;

    static LinkedHashMap<String, String> xmlElements;
    static String xmlTagName;

    static {
        xmlTagName = "attributes";
        xmlElements = new LinkedHashMap<>();
        xmlElements.put("attribute", "attribute");
    }

    //    @XmlElement(namespace = "http://qare.sfbay.sun.com/projects/COF/2003/2_0_2/Schema", required = true)
    protected List<COFTestAttribute> attribute;

    /**
     * Gets the value of the attribute property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a {@code set} method for the attribute property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttribute().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link COFTestAttribute }
     */
    public List<COFTestAttribute> getAttribute() {
        if (attribute == null) {
            attribute = new ArrayList<>();
        }
        return this.attribute;
    }

    LinkedHashMap<String, String> getItemAttributes() {
        return xmlAttributes;
    }

    LinkedHashMap<String, String> getItemElements() {
        return xmlElements;
    }

    String getItemTagName() {
        return xmlTagName;
    }
}
