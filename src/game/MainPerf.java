package game;

import diver.McDiver;
import java.util.Locale;
import java.util.Random;

/** The main program for the McDiver application. Run with --help to see the various options.
 */
public class MainPerf {
    static long seed = new Random().nextLong();
    /**
     * The main program. By default, runs seek() and scram() on a random seed, with a
     * graphical user interface.
     */
    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        int argi = 0;
        // parse options
        boolean valid = true;
        int runs = 1;
        while (valid && argi < args.length) {
            if (args[argi].charAt(0) != '-') break;
            switch (args[argi++].toLowerCase(Locale.ROOT)) {
                case "-s":
                    try {
                        seed = Long.parseLong(args[argi++]);
                    } catch (NumberFormatException e) {
                        System.err.println("Error, -s must be followed by a numeric seed");
                        return;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("Error, -s must be followed by a seed");
                        return;
                    }
                    break;
                case "-n":
                    try {
                        runs = Integer.parseInt(args[argi++]);
                    } catch (NumberFormatException exc) {
                        runs = 1;
                    }
                    break;
                case "--help":
                    usage();
                    return;
            }
        }
        if (argi != args.length) {
            usage();
            return;
        }
        var useGUI = false;

        int totalScore = 0;
        int seekFailures = 0;
        int seekErrors = 0;
        int scramFailures = 0;
        int scramErrors = 0;
        int timeouts = 0;
        for (int i = 0; i < runs; i++) {
            GameState state = new GameState(seed, useGUI, new McDiver());
            System.out.println("Seed : " + seed);
            Thread t = new Thread(() -> state.run());
            t.start();
            try {
                // Wait at most 15 seconds (50% margin)
                t.join(15000);
            } catch (InterruptedException e) { throw new RuntimeException(e); }
            if (t.isAlive()) {
                t.stop();
                System.out.println("Timeout! Aborting run.");
                timeouts += 1;
            } else {
                totalScore += state.getScore();
                seekFailures += state.getSeekSucceeded() ? 0 : 1;
                seekErrors += state.getSeekErrored() ? 1 : 0;
                scramFailures += (state.getScramSucceeded() || !state.getSeekSucceeded()) ? 0 : 1;
                scramErrors += state.getScramErrored() ? 1 : 0;
            }

            seed = new Random(seed).nextLong();
            System.out.println();
        }

        if (runs > 1) {
            var avgScore = totalScore / (double) runs;
            var baseGrade = Math.min(85.0 + (15.0/20000.0)*avgScore, 100);
            var avgScoreInt = (int)Math.round(avgScore);
            var deductions = Math.max(-3*(seekFailures + scramFailures), -15);

            System.out.println();
            System.out.println("========================");
            System.out.printf("In our performance suite, your implementation of McDiver earned an average score of %d, giving you a base grade of %.1f\n", avgScoreInt, baseGrade);
            System.out.println("========================");
            System.out.println("    Average score : " + avgScoreInt);
            System.out.println("    Timeouts: " + timeouts + " / " + runs);
            System.out.println("    Seek failures: " + seekFailures + " / " + (runs - timeouts) + " (" + seekErrors + " crashes)");
            System.out.println("    Scram failures: " + scramFailures + " / " + (runs - timeouts - seekFailures) + " (" + scramErrors + " crashes)");
            System.out.println("========================");
            System.out.printf("Your optimization bonus is %.1f / 15\n", baseGrade - 85);
            System.out.println("Deductions: " + deductions);
            System.out.println("========================");
        }
    }

    /** Effect: Prints a usage message. */
    public static void usage() {
        System.out.println("Usage: Main [--help] [-s <seed>] [-n <runs>]");
    }
}
