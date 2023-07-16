//package cs2110stf;
//
//import java.io.PrintStream;
//import java.io.PrintWriter;
//import java.util.LinkedList;
//import java.util.Random;
//
//import diver.McDiver;
//import game.*;
//
//public class A6Grader {
//
//    private static String GRADER = "";
//    /**
//     * Point values of random seek and scram seeds
//     */
//    private static final int SEEK_CORRECTNESS_RAN = 30, SCRAM_CORRECTNESS_RAN = 25;
//
//    /**
//     * Point value for each known seek map and scram map
//     */
//    private static final int SEEK_POINTS_PER_SEED = 5;
//    private static final int SCRAM_POINTS_PER_SEED = 5;
//
//    /**
//     * List of known seek-ring maps
//     */
//    private static final long[] SEEK_SEEDS = {-280019746129361794L, // Finds ring wo backtracking
//            1908492650781828577L,                // Finds ring with backtracking
//            -6462246516877662954L};        // Very large map
//
//    /**
//     * List of known scram maps
//     */
//    private static final long[] SCRAM_SEEDS = {8035820871068432943L,
//            // Dijkstra's unit edge weights
//            -4004310660161599891L,                // Dijkstra's non-unit edge weights
//            -3026730162232494481L};        // No reachable coins
//
//    /**
//     * Point value of your solution against the basic
//     */
//    private static final int BEAT_BASIC = 5;
//
//    /**
//     * Point value of your solution against instructor's
//     */
//    private static final int OPTIMIZATION = 10;
//
//    /**
//     * Percent of basic score needed to beat it
//     */
//    private static final double BEAT_BASIC_CUTOFF = 1.1;
//
//    /**
//     * Higher makes it harder to get all optimization points
//     */
//    private static final double PUNISH_FACTOR = 1.5;
//
//    /**
//     * Number of random seeds to test
//     */
//    private static final int NUM_RANDOMS = 75;
//
//    /**
//     * Percent of instructor score needed to get bonus
//     */
//    private static final double BONUS_FACTOR = 1.05;
//
//    /**
//     * Number of points awarded for beating instructor score
//     */
//    private static final int BONUS_POINTS = 2;
//
//    /**
//     * Points deducted if student has print statements
//     */
//    private static final int PRINT_PENALTY = 5;
//
//    /**
//     * Total seek-ring correctness point value
//     */
//    private static final int SCRAM_CORRECTNESS = SCRAM_CORRECTNESS_RAN +
//            SCRAM_POINTS_PER_SEED * SCRAM_SEEDS.length;
//
//    /**
//     * Total scram correctness point value
//     */
//    private static final int SEEK_CORRECTNESS =
//
//            SEEK_CORRECTNESS_RAN +
//                    SEEK_POINTS_PER_SEED * SEEK_SEEDS.length;
//
//    /**
//     * Total correctness point value
//     */
//    private static final int CORRECTNESS = SEEK_CORRECTNESS + SCRAM_CORRECTNESS;
//
//    /**
//     * Maximum attainable total score (without bonus)
//     */
//    private static final double TOTAL_SCORE = CORRECTNESS + BEAT_BASIC + OPTIMIZATION;
//
//    /**
//     * Student print statements encountered
//     */
//    public static boolean printingFlag = false;
//
//    /**
//     * The list of random seeds
//     */
//    private static LinkedList<Long> seeds = new LinkedList<>();
//
//    // Generate the same list of seeds to be used for all students
//    static {
//        Random r = new Random(0xe9b7cadc); // Last 4 bytes of the hash of the most recent BTC block
//        while (seeds.size() < NUM_RANDOMS) {
//            seeds.add(r.nextLong());
//        }
//    }
//
//    /**
//     * Run grading script. arg[0] is grader's name, arg[1] is student's netid.
//     */
//    public static void main(String[] args) throws Exception {
//        // Turn off printing flag
//        GameState.shouldPrint = false;
//        // Parse grader information from args
//        GRADER = args[0];
//        String netID = args[1];
//        PrintStream stdout = System.out;
//        // Check whether the student left print statements in their code or not
////		System.setOut(new PrintStream(new OutputStream() {
////			@Override
////			public void write(int b) {
////				// YOU GET NOTHING - penalty if student does printing
////				//printingFlag = true;
////			}
////		}));
//
//        PrintWriter pw = new PrintWriter("Submissions/" + netID + "/" + netID + "_feedback.txt");
//        String s = "Hello, this is " + GRADER + " grading your A6";
//        s += "\n\n";
//        s += "We grade A6 by running your McDiver on a number of sewer systems and ";
//        s += "checking that\nMcDiver successfully seeks the ring and scrams the ";
//        s += "sewer system. Then we\ncompare your score to our benchmark score ";
//        s += "to see whether you collected enough\ncoins to fund future expeditions. ";
//        s += String.format(
//                "The correctness of your solution is worth %d%%\nand the " +
//                        "score is worth %d%% of your grade on A8.\n",
//                CORRECTNESS, OPTIMIZATION + BEAT_BASIC);
//        s += "\n";
//
//        String seekString = "";
//        double seekCorrectness = SEEK_CORRECTNESS - SEEK_CORRECTNESS_RAN;
//        double seekCorrectnessRan = 0;
//        String scramString = "";
//        double scramCorrectness = SCRAM_CORRECTNESS - SCRAM_CORRECTNESS_RAN;
//        double scramCorrectnessRan = 0;
//
//        // Correctness tests - SEEK
//        seekString += "\n\n\nTesting Find ring...\n";
//        seekString += "Hand-picked sewers\n";
//        boolean seekAllPass = true;
//        for (long seed : SEEK_SEEDS) {
//            GameState state = new GameState(seed, false, new McDiver());
//            state.runFindWithTimeout();
//            if (!state.getSeekSucceeded()) {
//                seekCorrectness -= SEEK_POINTS_PER_SEED;
//                seekString += "    -" + SEEK_POINTS_PER_SEED +
//                        ": Find-the-ring failed on seed " + seed + "\n";
//                seekAllPass = false;
//            }
//        }
//        if (seekAllPass) {
//            seekString += "    No errors\n";
//        }
//
//        // Correctness tests - scram
//        scramString += "\nTesting scraminging...\n";
//        scramString += "Hand-picked sewers\n";
//        boolean scramAllPass = true;
//        for (long seed : SCRAM_SEEDS) {
//            GameState state = new GameState(seed, false, new McDiver());
//            state.runScramWithTimeout();
//            if (!state.scramSucceeded()) {
//                scramCorrectness -= SCRAM_POINTS_PER_SEED;
//                scramString += "    -" + SCRAM_POINTS_PER_SEED +
//                        ": scram failed on seed " + seed + "\n";
//                scramAllPass = false;
//            }
//        }
//
//        if (scramAllPass) {
//            scramString += "    No errors\n";
//        }
//
//        s += "The results of running your McDiver on random sewers are shown below.\n";
//        s += "\n\nTest Results...(" + NUM_RANDOMS + " maps tested)\n" +
//                String.format("%-22s%-11s%-11s%-9s%-9s%s", "Seed", "seek", "scram",
//                        "Score", "Basic", "Benchmark").replace(' ', '.');
//
//        // Run the random maps
//        double totalS = 0;
//        int totalIScore = 0;
//        int totalSScore = 0;
//        int totalBScore = 0;
//        seekString += "Random sewers\n";
//        scramString += "Random sewers\n";
//        // exploreAllPass = true;
//        scramAllPass = true;
//        for (Long seed : seeds) {
//            GameState student = new GameState(seed, false, new McDiver());
//            String exStatus;
//            boolean allPass = true;
//            student.runWithTimeLimit();
//            if (student.getSeekSucceeded()) {
//                exStatus = "Success";
//                seekCorrectnessRan += 1;
//            } else if (student.getSeekErrored()) {
//                exStatus = "Exception";
//                allPass = false;
//            } else if (student.getSeekTimeout()) {
//                exStatus = "Timed Out";
//                allPass = false;
//            } else {
//                exStatus = "Failure";
//                allPass = false;
//            }
//            if (!student.getSeekSucceeded()) {
//                seekString += "    Find did not succeed on seed " + seed + "\n";
//                seekAllPass = false;
//            }
//            String esStatus;
//            if (student.getScramSucceeded()) {
//                esStatus = "Success";
//                scramCorrectnessRan += 1;
//            } else if (student.getScramErrored()) {
//                esStatus = "Exception";
//                allPass = false;
//            } else if (student.getScramTimeout()) {
//                esStatus = "Timed Out";
//                allPass = false;
//            } else {
//                esStatus = "Failure";
//                allPass = false;
//            }
//            if (!student.getScramSucceeded()) {
//                scramString += "    scram did not succeed on seed " + seed + "\n";
//                scramAllPass = false;
//            }
//            int sScore = allPass ? student.getScore() : 0;
//            GameState inst = new GameState(seed, false, new InstructorSolution());
//            inst.run();
//            GameState basic = new GameState(seed, false, new McDiver());
//            basic.run();
//            double scoreScore = Math.max(0,
//                    (double) (sScore - basic.getScore()) / (inst.getScore() - basic.getScore()));
//            scoreScore *= 100;
//            totalS += scoreScore;
//            s += "\n" + String.format("%20s  %-11s%-11s%-9s%-9s%-9s", seed,
//                    exStatus, esStatus, sScore, basic.getScore(), inst.getScore());
//            totalSScore += sScore;
//            totalIScore += inst.getScore();
//            totalBScore += basic.getScore();
//        }
//
//        if (seekAllPass) {
//            seekString += "    No errors\n";
//        }
//        if (scramAllPass) {
//            scramString += "    No errors\n";
//        }
//
//        // Finalize computations of correctness score
//        seekCorrectnessRan = seekCorrectnessRan / seeds.size() * SEEK_CORRECTNESS_RAN;
//        scramCorrectnessRan = scramCorrectnessRan / seeds.size() * SCRAM_CORRECTNESS_RAN;
//        seekCorrectness = Math.max(0, seekCorrectness + seekCorrectnessRan);
//        seekString += String.format("Find correctness score: %.1f / %d\n", seekCorrectness,
//                SEEK_CORRECTNESS);
//        scramCorrectness = Math.max(0, scramCorrectness + scramCorrectnessRan);
//        scramString += String.format("Flee correctness score: %.1f / %d\n", scramCorrectness,
//                SCRAM_CORRECTNESS);
//        s += seekString + "\n" + scramString + "\n";
//        double totalC = seekCorrectness + scramCorrectness;
//
//        // Compute the proportion of the solution score we got
//        double solutionProp = Math.min(100.0, totalS / NUM_RANDOMS) / 100;
//        // Scale the error by PUNISH_FACTOR to control how much error is penalized
//        solutionProp = 1.0 - (1.0 - solutionProp) * PUNISH_FACTOR;
//        totalS = Math.max(0, solutionProp * OPTIMIZATION);
//        // Determine bonus points for beating basic
//        double basicBeat = totalSScore > totalBScore * BEAT_BASIC_CUTOFF ? BEAT_BASIC : 0;
//        double grade = totalC + totalS + basicBeat;
//        s += "\n\n\n";
//        s += String.format("%-20s %4.1f / %2d", "Correctness:", totalC, CORRECTNESS);
//        s += "\n";
//        s += String.format("%-20s %4.1f / %2d", "Beat basic solution:", basicBeat, BEAT_BASIC);
//        s += "\n";
//        s += String.format("%-20s %4.1f / %2d", "Optimization:", totalS, OPTIMIZATION);
//        s += "\n\n";
//        if (totalSScore >= BONUS_FACTOR * totalIScore) {
//            s += "Congratulations! Your score was high enough to earn a bonus! +" + BONUS_POINTS +
//                    " points\n";
//            grade += BONUS_POINTS;
//        }
//        if (printingFlag) {
//            s += "Your code included print statements. -" + PRINT_PENALTY + " points\n";
//            grade -= PRINT_PENALTY;
//        }
//        grade = Math.max(grade, 0);
//        s += String.format("Total Weighted Grade out of 100: %.1f%%", grade); // sp 2020
//        // s+= String.format("Total Weighted Grade: %.1f%% of %.1f%%", grade, TOTAL_SCORE);
//        pw.println(s);
//        pw.close();
//        // Get the netIDs from the args - will either have length 1 or 2.
//        String[] netIDs = null;
//        if (netID.startsWith("group_of_")) {
//            netIDs = new String[2];
//            String p = netID.substring(9); // length of group_of_
//            netIDs[0] = p.substring(0, p.indexOf('_'));
//            netIDs[1] = p.substring(p.indexOf('_') + 1);
//        } else {
//            netIDs = new String[1];
//            netIDs[0] = netID;
//        }
//        // Do grade printing to console where it will be picked up by graph
//        for (String id : netIDs) {
//            stdout.printf("%s,%.1f\n", id, grade);
//        }
//    }
//}
