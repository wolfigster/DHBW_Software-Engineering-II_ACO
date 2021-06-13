package configuration;

import random.MersenneTwister;

import java.text.DecimalFormat;
import java.util.HashMap;

public enum Configuration {
    instance;

    public final String userDirectory = System.getProperty("user.dir");
    public final String fileSeparator = System.getProperty("file.separator");
    public final String dataDirectory = userDirectory + fileSeparator + "data" + fileSeparator;
    public final String logDirectory = userDirectory + fileSeparator + "log" + fileSeparator;
    public final String logFile = logDirectory + "swarm_intelligence.log";

    public final MersenneTwister randomGenerator = new MersenneTwister(System.currentTimeMillis());
    public final DecimalFormat decimalFormat = new DecimalFormat("#0.000000000000000");

    public final double startPheromoneValue = 0.00005;

    public final boolean isDebug = false;

    public boolean loglevel = false;
    public double alpha = 0;
    public double beta = 0;
    public double decayFactor = 0.3;
    public int numberOfAnts = 25;
    public int numberOfIterations = 50;
    public int numberOfThreads = 4;
    public String data = "pr299.tsp";
    public String result = "result.log";

    // method to set all necessary variables
    public void set(String... args) throws Exception {
        HashMap<String, String> values = new HashMap<>();
        if (args.length % 2 != 0) {
            throw new Exception("Bitte Eingabewerte überprüfen");
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i].charAt(0) == '-') {
                values.put(args[i].substring(1), args[i+1]);
                i++;
            }
        }
        // try to get all the inputs from the hashmap and parse them to the correct datatype
        try {
            this.data = values.get("data") == null ? this.data : dataDirectory + values.get("data");
            this.result = values.get("result") == null ? this.result : logDirectory + values.get("result");
            double _alpha = values.get("alpha") == null ? this.alpha : Double.parseDouble(values.get("alpha"));
            if (_alpha > 10 || _alpha < 1) {
                throw new Exception("-alpha has to be between 0 and 10");
            }
            this.alpha = _alpha;

            double _beta = values.get("beta") == null ? this.beta : Double.parseDouble(values.get("beta"));
            if (_beta > 10 || _beta < 1) {
                throw new Exception("-beta has to be between 0 and 10");
            }
            this.beta = _beta;

            double _decayFactor = values.get("decay") == null ? this.decayFactor : Double.parseDouble(values.get("decay"));
            if (!(0 <= _decayFactor && _decayFactor <= 1)) {
                throw new Exception("-decay has to be between 0 and 1");
            }
            this.decayFactor = _decayFactor;

            int _numberOfIterations = values.get("niterations") == null ? this.numberOfIterations : Integer.parseInt(values.get("niterations"));
            if (_numberOfIterations < 1) {
                throw new Exception("-niterations has to be greater than 0");
            }
            this.numberOfIterations = _numberOfIterations;

            int _numberOfAnts = values.get("nants") == null ? this.numberOfAnts : Integer.parseInt(values.get("nants"));
            if (_numberOfAnts < 1) {
                throw new Exception("-nants has to be greater than 0");
            }
            this.numberOfAnts = _numberOfAnts;

            int _numberOfThreads = values.get("nthreads") == null ? this.numberOfThreads : Integer.parseInt(values.get("nthreads"));
            if (_numberOfThreads < 1) {
                throw new Exception("-nthreads has to be greater than 0");
            }
            this.numberOfThreads = _numberOfThreads;

            int _loglevel = values.get("loglevel") == null ? 0 : Integer.parseInt(values.get("loglevel"));
            if (!(_loglevel == 1 || _loglevel == 0)) {
                throw new Exception("-loglevel has to be 0 or 1");
            }
            this.loglevel = _loglevel == 1;
        } catch (NumberFormatException nfe) {
            throw new Exception("Ein oder mehrere Parameter konnten nicht übergeben werden. Überprüfen Sie ihre Eingaben!");
        }
    }
}