/*
 * $Id$
 *
 * Copyright (c) 1996, 2020, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.tck.lib.autd2.unittests.tgfported.algebra.generated;

import com.sun.tck.lib.tgf.Values;

interface ISimpleOperation {


    Values with(Values v1, Values v2);

    ISimpleOperation MULT = new ISimpleOperation() {
        public Values with(Values v1, Values v2) { return v1.multiply(v2); }

        public String toString() {
            return "MULT";
        }
    };

    ISimpleOperation P_MULT = new ISimpleOperation() {
        public Values with(Values v1, Values v2) { return v1.pseudoMultiply(v2); }

        public String toString() {
            return "PMULT";
        }
    };

    ISimpleOperation UNITE = new ISimpleOperation() {
        public Values with(Values v1, Values v2) { return v1.unite(v2); }

        public String toString() {
            return "UNITE";
        }
    };

    ISimpleOperation INTERSECT = new ISimpleOperation() {
        public Values with(Values v1, Values v2) { return v1.intersect(v2); }

        public String toString() {
            return "INTERSECT";
        }
    };

    ISimpleOperation[] OPERATIONS = new ISimpleOperation[] {MULT, P_MULT, UNITE, INTERSECT};

}
