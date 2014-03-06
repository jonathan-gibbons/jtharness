/*
 * $Id$
 *
 * Copyright (c) 2006, 2009, Oracle and/or its affiliates. All rights reserved.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.sun.javatest.util.XMLWriter;


/**
 * <p>Java class for TestSuites complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TestSuites">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="testsuite" type="{http://qare.sfbay.sun.com/projects/COF/2003/2_0_2/Schema}TestSuite" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
/*@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TestSuites", propOrder = {
    "testsuite"
})*/
public class COFTestSuites extends COFItem{

static LinkedHashMap xmlAttributes;

        static LinkedHashMap xmlElements;

        static String xmlTagName;

        static {
        xmlElements = new LinkedHashMap();
        xmlElements.put("testsuite", "testsuite");
        xmlTagName = "testsuites";
    }

        //    @XmlElement(namespace = "http://qare.sfbay.sun.com/projects/COF/2003/2_0_2/Schema", required = true)
    protected List/*<COFTestSuite>*/ testsuite;

        LinkedHashMap getItemAttributes() {
                return xmlAttributes;
        }

        LinkedHashMap getItemElements() {
                return xmlElements;
        }

    String getItemTagName() {
                return xmlTagName;
        }

    /**
     * Gets the value of the testsuite property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the testsuite property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTestsuite().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link COFTestSuite }
     *
     *
     */
    public List/*<COFTestSuite>*/ getTestsuite() {
        if (testsuite == null) {
            testsuite = new ArrayList/*<COFTestSuite>*/();
        }
        return this.testsuite;
    }

        /* (non-Javadoc)
         * @see com.sun.javatest.cof.COFItem#write(com.sun.javatest.util.XMLWriter)
         */
        void write(XMLWriter out) throws IOException {
                out.newLine();
                super.write(out);
                out.newLine();
        }
}
