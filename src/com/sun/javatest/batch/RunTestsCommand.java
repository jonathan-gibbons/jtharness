/*
 * $Id$
 *
 * Copyright (c) 2002, 2012, Oracle and/or its affiliates. All rights reserved.
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
package com.sun.javatest.batch;

import com.sun.javatest.Harness;
import com.sun.javatest.Parameters;
import com.sun.javatest.Status;
import com.sun.javatest.TestDescription;
import com.sun.javatest.TestFilter;
import com.sun.javatest.TestFinder;
import com.sun.javatest.TestResult;
import com.sun.javatest.tool.Command;
import com.sun.javatest.tool.CommandContext;
import com.sun.javatest.tool.VerboseCommand;
import com.sun.javatest.util.HelpTree;
import com.sun.javatest.util.I18NResourceBundle;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

class RunTestsCommand extends Command {
    private static final String DATE_OPTION = "date";
    private static final String NON_PASS_OPTION = "non-pass";
    private static final String START_OPTION = "start";
    private static final String FINISH_OPTION = "stop";
    private static final String PROGRESS_OPTION = "progress";
    private static I18NResourceBundle i18n = I18NResourceBundle.getBundleForClass(RunTestsCommand.class);
    private Harness harness;
    private CommandContext ctx;

    RunTestsCommand() {
        super(getName());
    }

    RunTestsCommand(ListIterator<String> argIter) {
        super(getName());
    }

    static String getName() {
        return "runTests";
    }

    static void initVerboseOptions() {
        VerboseCommand.addOption(DATE_OPTION, new HelpTree.Node(i18n, "runTests.verbose.date"));
        VerboseCommand.addOption(NON_PASS_OPTION, new HelpTree.Node(i18n, "runTests.verbose.nonPass"));
        VerboseCommand.addOption(START_OPTION, new HelpTree.Node(i18n, "runTests.verbose.start"));
        VerboseCommand.addOption(FINISH_OPTION, new HelpTree.Node(i18n, "runTests.verbose.stop"));
        VerboseCommand.addOption(PROGRESS_OPTION, new HelpTree.Node(i18n, "runTests.verbose.progress"));
    }

    @Override
    public boolean isActionCommand() {
        return true;
    }

    //-------------------------------------------------------------------------

    @Override
    public void run(CommandContext ctx) throws Fault {
        this.ctx = ctx;

        try {
            Parameters p = getConfig(ctx); // throws fault if not set

            // might want to move harness down into CommandContext
            // to share with GUI
            Harness h = new Harness();
            harness = h;

            Harness.Observer[] observers = ctx.getHarnessObservers();
            for (Harness.Observer observer : observers) {
                h.addObserver(observer);
            }

            // should really merge VerboseObserver and BatchObserver
            VerboseObserver vo = new VerboseObserver(ctx);
            h.addObserver(vo);

            BatchObserver bo = new BatchObserver();
            h.addObserver(bo);
            p.getTestSuite().getTestFinder().setErrorHandler(bo);

            boolean ok = h.batch(p);

            if (bo.getFinderErrorCount() > 0) {
                // other problems during run
                ctx.printErrorMessage(i18n, "runTests.warnError");
            }

            int[] boStats = bo.getStats();

            boStats[Status.NOT_RUN] = harness.getTestsFoundCount() - boStats[Status.PASSED] - boStats[Status.FAILED] - boStats[Status.ERROR];
            // tests that were rejected by filters so skipped from the run
            int skipped = harness.getTestIterator().getRejectCount();

            HashMap<TestFilter, ArrayList<TestDescription>> stats = harness.getTestIterator().getFilterStats();
            h.notifyOfTheFinalStats(stats != null ? Collections.unmodifiableMap(stats) : Collections.emptyMap(), boStats);

            if (!ctx.isVerboseQuiet()) {
                long tt = h.getElapsedTime();
                long setupT = h.getTotalSetupTime();
                long cleanupT = h.getTotalCleanupTime();
                ctx.printMessage(i18n, "runTests.totalTime", tt / 1000L);
                ctx.printMessage(i18n, "runTests.setupTime", setupT / 1000L);
                ctx.printMessage(i18n, "runTests.cleanupTime", cleanupT / 1000L);

                showResultStats(skipped, boStats);
            }


            int testsFound = h.getTestsFoundCount();

            if (testsFound > 0 && !ctx.isVerboseQuiet()) {
                ctx.printMessage(i18n, "runTests.resultsDone", p.getWorkDirectory().getPath());
            }

            if (!ok) {
                if (testsFound > 0 &&
                        testsFound != boStats[Status.PASSED]) {
                    // some tests are actually not passed, print
                    // appropriate message
                    ctx.printErrorMessage(i18n, "runTests.testsFailed");
                }
            }

            ctx.addTestStats(boStats);
        } catch (Harness.Fault e) {
            throw new Fault(i18n, "runTests.harnessError", e.getMessage());
        }
    }

    private void showResultStats(int skipped, int... stats) {

        int passed = stats[Status.PASSED];
        int failed = stats[Status.FAILED];
        int errors = stats[Status.ERROR];
        int notRun = stats[Status.NOT_RUN];

        harness.getResultTable().getTestFinder().totalNumberOfTestsInTheSuite().ifPresent(
                totalNumber -> ctx.printMessage(i18n, "runTests.testsInTheSuite", totalNumber)
        );

        if (passed + failed + errors + notRun + skipped == 0) {
            ctx.printMessage(i18n, "runTests.noTests");
        } else {
            // runTests.tests=Test results:
            // {0,choice,0#|0<passed: {0,number}}
            // {1,choice,0#|1#; }
            // {2,choice,0#|0<failed: {2,number}}
            // {3,choice,0#|1#; }
            // {4,choice,0#|0<error: {4,number}}
            // {5,choice,0#|1#; }
            // {6,choice,0#|0<not run:
            // {6,number}}
            ctx.printMessage(i18n, "runTests.tests",
                    Integer.valueOf(passed),
                    Integer.valueOf((passed > 0) && (failed + errors + notRun + skipped > 0) ? 1 : 0),
                    Integer.valueOf(failed),
                    Integer.valueOf((failed > 0) && (errors + notRun + skipped > 0) ? 1 : 0),
                    Integer.valueOf(errors),
                    Integer.valueOf((errors > 0) && (notRun + skipped > 0) ? 1 : 0),
                    Integer.valueOf(notRun),
                    Integer.valueOf((notRun > 0) && (skipped > 0) ? 1 : 0),
                    Integer.valueOf(skipped));
        }
        ctx.getLogWriter().println();
        for (Map.Entry<TestFilter, ArrayList<TestDescription>> entry : harness.getTestIterator().getFilterStats().entrySet()) {
            TestFilter filter = entry.getKey();
            int number = entry.getValue().size();
            ctx.getLogWriter().println(number + " " + (number == 1 ? "test" : "tests") +
                    " skipped by filter \"" + filter.getName() + "\"");
        }
    }

    //-------------------------------------------------------------------------

    private class BatchObserver
            implements Harness.Observer, TestFinder.ErrorHandler {

        private int[] stats;
        private int finderErrors;

        int[] getStats() {
            return stats;
        }

        int getFinderErrorCount() {
            return finderErrors;
        }

        @Override
        public void startingTestRun(Parameters params) {
            stats = new int[Status.NUM_STATES];
        }

        @Override
        public void finishedTest(TestResult tr) {
            stats[tr.getStatus().getType()]++;
        }

        @Override
        public void error(String msg) {
            ctx.printMessage(i18n, "runTests.error", msg);
            finderErrors++;
        }
    }

    private class VerboseObserver implements Harness.Observer {
        public static final int NO_DATE = 0;
        public static final int NON_PASS = 1;
        public static final int START = 2;
        public static final int FINISH = 3;
        public static final int PROGRESS = 4;
        public static final int DEFAULT = PROGRESS;
        private static final int OPTION_COUNT = 5;
        private boolean[] options;
        private boolean quiet_flag = false;
        private boolean max_flag = false;
        private DateFormat df;
        private CommandContext ctx;
        private PrintWriter out;
        private int[] stats;
        private boolean progressOnline = false;
        VerboseObserver(CommandContext ctx) {
            this.ctx = ctx;
            this.out = ctx.getLogWriter();

            quiet_flag = ctx.isVerboseQuiet();
            df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                    DateFormat.MEDIUM);
            ctx.addHarnessObserver(this);

            boolean defaultFlag = ctx.getVerboseOptionValue("default", false);
            options = new boolean[OPTION_COUNT];
            options[NO_DATE] = !ctx.isVerboseTimestampEnabled();
            options[NON_PASS] = ctx.getVerboseOptionValue(NON_PASS_OPTION, false);
            options[START] = ctx.getVerboseOptionValue(START_OPTION, false);
            options[FINISH] = ctx.getVerboseOptionValue(FINISH_OPTION, false);
            options[PROGRESS] = ctx.getVerboseOptionValue(PROGRESS_OPTION, defaultFlag);
        }

        public boolean isVerbose(int kind) {
            if (quiet_flag) {
                return false;
            }

            if (max_flag) {
                return true;
            }

            if (kind < OPTION_COUNT) {
                return options[kind];
            } else {
                return false;
            }
        }

        private void printTimestamp() {
            if (quiet_flag || options[NO_DATE]) {
                return;
            }

            out.print(df.format(new Date()));
            out.print(" ");
        }

        // ---- Harness.Observer ----
        @Override
        public void startingTestRun(Parameters params) {
            stats = new int[Status.NUM_STATES];

            if (!quiet_flag) {
                if (progressOnline) {
                    out.println();
                }
                printTimestamp();
                ctx.printMessage(i18n, "cmgr.verb.strt",
                        params.getEnv().getName());
                out.flush();
                progressOnline = false;
            }
        }

        @Override
        public void startingTest(TestResult tr) {
            if (!isVerbose(START)) {
                return;
            }

            if (progressOnline) {
                out.println();
            }

            printTimestamp();
            ctx.printMessage(i18n, "cmgr.verb.tsts", tr.getTestName());
            out.flush();

            progressOnline = false;
        }

        @Override
        public void finishedTest(TestResult tr) {
            Status s = tr.getStatus();
            stats[s.getType()]++;

            switch (s.getType()) {
                case Status.FAILED:
                case Status.ERROR:
                    if (isVerbose(NON_PASS) || isVerbose(FINISH)) {
                        printFinish(s, tr);
                        progressOnline = false;
                    }
                    break;
                default:
                    if (isVerbose(FINISH)) {
                        printFinish(s, tr);
                        progressOnline = false;
                    }
            }   // switch

            printStats();
            out.flush();
        }

        @Override
        public void stoppingTestRun() {
            if (progressOnline) {
                out.println();
            }

            printTimestamp();
            ctx.printMessage(i18n, "cmgr.verb.stpng");
            out.flush();

            progressOnline = false;
        }

        @Override
        public void finishedTesting() {
            if (!quiet_flag) {
                if (progressOnline) {
                    out.println();
                }

                printTimestamp();
                ctx.printMessage(i18n, "cmgr.verb.donerun");
                out.flush();

                progressOnline = false;
            }
        }

        @Override
        public void finishedTestRun(boolean allOK) {
            if (!quiet_flag) {
                if (progressOnline) {
                    out.println();
                }

                printTimestamp();
                ctx.printMessage(i18n, "cmgr.verb.finish");
                out.flush();

                progressOnline = false;
            }
        }

        @Override
        public void error(String msg) {
            if (progressOnline) {
                out.println();
            }

            printTimestamp();
            ctx.printErrorMessage(i18n, "cmgr.verb.err", msg);
            out.flush();

            progressOnline = false;
        }

        // utility methods
        private void printStats() {
            if (!isVerbose(PROGRESS)) {
                return;
            }

            if (progressOnline) {
                out.print("\r");
            }

            int p = stats[Status.PASSED];
            int f = stats[Status.FAILED];
            int e = stats[Status.ERROR];
            int nr = stats[Status.NOT_RUN] =
                    harness.getTestsFoundCount() - p - f - e;

            out.print(i18n.getString("cmgr.verb.prog",
                    Integer.valueOf(p), Integer.valueOf(f), Integer.valueOf(e), Integer.valueOf(nr)));
            out.print("    ");

            progressOnline = true;
        }

        private void printFinish(Status s, TestResult tr) {
            if (!quiet_flag) {
                // need to create newline if we are doing single-line
                // updates
                if (progressOnline) {
                    out.println();
                }

                printTimestamp();
                String[] args = {tr.getTestName(),
                        s.toString()};
                ctx.printMessage(i18n, "cmgr.verb.tstd", (Object[]) args);
                out.flush();
                progressOnline = false;
            }
        }

        /**
         * Is the text being displayed using println during the run?
         * This affects our ability to update a progress counter.
         */
        private boolean isScolling() {
            return isVerbose(START) || isVerbose(FINISH);
        }
    }
}
