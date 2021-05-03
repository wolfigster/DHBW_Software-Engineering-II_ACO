import java.text.DecimalFormat;

public enum Configuration {
    instance;

    public final String userDirectory = System.getProperty("user.dir");
    public final String fileSeparator = System.getProperty("file.separator");
    public final String logDirectory = userDirectory + fileSeparator + "log" + fileSeparator;
    public final String logfilePath = userDirectory + fileSeparator + "log" + fileSeparator + "debug.log";

    public final double decayFactor = 0.3;
    public final double startPheromoneValue = 0.00005;
    public final int numberOfAnts = 25;
    public final int numberOfIterations = 50;

    public final MersenneTwister randomGenerator = new MersenneTwister(System.currentTimeMillis());

    public final DecimalFormat decimalFormat = new DecimalFormat("#0.000000000000000");

    public final boolean isDebug = true;
}