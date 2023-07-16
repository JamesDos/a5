package cs2110stf;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestExecutionResult.Status;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.*;

public class TestRunner {

    public static void main(String[] args){
        var tests = new ArrayList<TestClass>();
        for (int i = 0; i < args.length; i += 2) {
            tests.add(new TestClass(args[i], Double.parseDouble(args[i + 1])));
        }

        var launcher = LauncherFactory.create();
        var out = new PrintWriter(System.out);

        boolean extraneousOutput = false;
        double pointsEarned = 0.0;
        double pointsPossible = 0.0;
        for (var test : tests) {
            var grade = runTestSuite(test.testClass(), test.weight(), launcher, out);
            pointsEarned += grade.points;
            pointsPossible += test.weight();
            extraneousOutput |= grade.extraneousOutput;
        }

        //out.println();
        if (extraneousOutput) {
            var penalty = -1.0;
            out.printf("Penalty for extraneous output: %.1f\n", penalty);
            pointsEarned += penalty;
        }
        out.printf("Total score: %.1f / %.1f\n", pointsEarned, pointsPossible);

        out.close();

        // Kill any remaining testcase threads
        System.exit(0);
    }

    static GradeResult runTestSuite(String testClass, double weight, Launcher launcher, PrintWriter out) {
        // Find desired test case(s).
        var request = LauncherDiscoveryRequestBuilder.request()
                //.configurationParameter("junit.jupiter.execution.timeout.default", "10 s")
                .selectors(selectClass(testClass))
                .build();

        var listener = new GradingListener(out, true);

        out.println("Running tests from " + testClass + " ...");
        try {
            // Run the tests
            launcher.execute(request, listener);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(-1);
        }

        // Print summary info
        out.println("Passed " + listener.numSuccesses() + " / " + listener.numTests() + " tests");
        var points = listener.numSuccesses() * weight / listener.numTests();
        // Quadratic weighting
        //points = weight*((points/weight)*(points/weight));
        // Round to tenths
        points = 0.1*Math.round(10*points);

        if (listener.numSuccesses() < listener.numTests()) {
            out.printf("  Deductions: %.1f\n", weight - points);
        }

        // Report extraneous output
        if (listener.bytesWritten() > 0) {
            out.println("  Code-under-test wrote " +
                    listener.bytesWritten() +
                    " bytes of extraneous output");
        }

        out.println();

        return new GradeResult(points, listener.bytesWritten() > 0);
    }

    record GradeResult(double points, boolean extraneousOutput) { }

    record TestClass(String testClass, double weight) { }
}

/**
 * Drop all bytes written, but keep track of their count.
 */
class NullOutputStream extends OutputStream {

    /**
     * Number of bytes written.
     */
    private AtomicLong bytesWritten = new AtomicLong();

    /**
     * Number of bytes written.  Thread-safe.
     */
    public long getBytesWritten() {
        return bytesWritten.get();
    }

    @Override
    public void write(int b) {
        bytesWritten.getAndIncrement();
    }
}

class GradingListener implements TestExecutionListener {

    PrintWriter out;
    boolean printSuccess;
    ArrayList<TestIdentifier> stack;
    int printLevel;
    int nTests;
    int nSuccesses;

    PrintStream sysOutOrig;
    PrintStream sysErrOrig;
    NullOutputStream capture;

    private void printIndent(int level) {
        for (int i = 0; i < level; ++i) {
            out.print("  ");
        }
    }

    GradingListener(PrintWriter out, boolean printSuccess) {
        this.out = out;
        this.printSuccess = printSuccess;
        stack = new ArrayList<>();
        printLevel = 0;
        nTests = 0;
        nSuccesses = 0;

        capture = new NullOutputStream();
    }

    public int numTests() {
        return nTests;
    }

    public int numSuccesses() {
        return nSuccesses;
    }

    public long bytesWritten() {
        return capture.getBytesWritten();
    }

    private void printContext() {
        for (; printLevel < stack.size(); ++printLevel) {
            printIndent(printLevel);
            out.println(stack.get(printLevel).getDisplayName());
        }
    }

    @Override
    public void executionStarted(TestIdentifier id) {
        //out.println(id);
        if (id.getSource().isEmpty()) {
            return;
        }
        //printIndent(0);
        //out.println(id.getDisplayName());
        stack.add(id);
    }

    @Override
    public void executionFinished(TestIdentifier id, TestExecutionResult result) {
        if (id.getSource().isEmpty()) {
            return;
        }

        // Check for consistency
        var head = stack.get(stack.size() - 1);
        if (!id.equals(head)) {
            throw new RuntimeException("Test mismatch!");
        }

        if (id.isTest()) {
            nTests += 1;
            var status = result.getStatus();
            if (status == Status.SUCCESSFUL) {
                nSuccesses += 1;
            }

            if (printSuccess || status != Status.SUCCESSFUL) {
                printContext();
                printIndent(printLevel);
                var error = result.getThrowable();
                switch (status) {
                    case SUCCESSFUL:
                        //out.println(status);
                        out.println("PASSED");
                        break;
                    case FAILED:
                        out.print(status);
                        error.ifPresentOrElse(t -> {
                            if (t instanceof org.opentest4j.AssertionFailedError ||
                                    t instanceof java.util.concurrent.TimeoutException) {
                                out.println(": " + t.getMessage());
                            } else if (t instanceof NoSuchMethodError) {
                                out.println(": " + t);
                            } else {
                                out.println(": " + t);
                                var trace = t.getStackTrace();
                                if (trace.length > 0) {
                                    printIndent(printLevel);
                                    out.println("at " + trace[0]);
                                }
                            }
                        }, () -> out.println());
                        break;
                    case ABORTED:
                        out.println(status + ": " + error.map(t -> t.toString()).orElse(""));
                        break;
                }
                ;
            }
        }

        stack.remove(stack.size() - 1);
        printLevel = Math.min(printLevel, stack.size());
    }

    @Override
    public void testPlanExecutionStarted(TestPlan plan) {
        sysOutOrig = System.out;
        sysErrOrig = System.err;
        System.setOut(new PrintStream(capture));
        System.setErr(new PrintStream(capture));
    }

    @Override
    public void testPlanExecutionFinished(TestPlan plan) {
        System.setErr(sysErrOrig);
        System.setOut(sysOutOrig);
    }
}
