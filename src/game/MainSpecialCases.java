package game;

import diver.McDiver;
import java.util.Locale;
import java.util.Random;

/** The main program for the McDiver application. Run with --help to see the various options.
 */
public class MainSpecialCases {
    /**
     * The main program. By default, runs seek() and scram() on a random seed, with a
     * graphical user interface.
     */
    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        var useGUI = false;

        long[] seeds = {-280019746129361794L,1908492650781828577L,-6462246516877662954L,
                8035820871068432943L,-4004310660161599891L,-3026730162232494481L};
        int totalScore = 0;
        int seekFailures = 0;
        int seekErrors = 0;
        int scramFailures = 0;
        int scramErrors = 0;
        int timeouts = 0;
        var runs = seeds.length;
        for (var seed : seeds) {
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
            System.out.println();
        }

        var deductions = Math.max(-3*(timeouts + seekFailures + scramFailures), -18);

        System.out.println();
        System.out.println("========================");
        System.out.println("    Timeouts: " + timeouts + " / " + runs);
        System.out.println("    Seek failures: " + seekFailures + " / " + (runs - timeouts) + " (" + seekErrors + " crashes)");
        System.out.println("    Scram failures: " + scramFailures + " / " + (runs - timeouts - seekFailures) + " (" + scramErrors + " crashes)");
        System.out.println("========================");
        System.out.println("Deductions: " + deductions + " (" + (-deductions/3) + " / 6 cases failed)");
        System.out.println("========================");
    }
}
