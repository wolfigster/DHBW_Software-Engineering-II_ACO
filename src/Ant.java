import java.text.DecimalFormat;
import java.util.Vector;

public class Ant {
    private final ProblemInstance data;
    private final AntColony antColony;
    private final int id;
    private double objectiveValue = 0.0;
    private int[] tour;
    private Vector<Integer> notJetVisited = null;

    public Ant(int id, ProblemInstance data, AntColony antColony) {
        this.id = id;
        this.data = data;
        this.antColony = antColony;
    }

    public double getObjectiveValue() {
        if (objectiveValue == 0.0) {
            int count = data.getNumberOfCities();

            for (int i = 0; i < count - 1; i++) {
                objectiveValue += data.getDistance(tour[i], tour[i + 1]);
            }

            objectiveValue += data.getDistance(tour[count - 1], tour[0]);
        }

        return objectiveValue;
    }

    public void newRound() {
        objectiveValue = 0.0;
        tour = new int[data.getNumberOfCities()];
        notJetVisited = new Vector<>();

        for (int i = 1; i <= data.getNumberOfCities(); i++) {
            notJetVisited.addElement(i);
        }
    }

    public void layPheromone() {
        double pheromone = Configuration.instance.decayFactor / objectiveValue;
        int count = data.getNumberOfCities();

        if (Configuration.instance.isDebug) {
            LogEngine.instance.write("--- Ant[" + id + "].layPheromone()");
            LogEngine.instance.write("decay factor   : " + Configuration.instance.decayFactor);
            LogEngine.instance.write("objectiveValue : " + objectiveValue);
            LogEngine.instance.write("pheromone      : " + pheromone);
        }

        for (int i = 0; i < count - 1; i++) {
            antColony.addPheromone(tour[i], tour[i + 1], pheromone);
            antColony.addPheromone(tour[i + 1], tour[i], pheromone);
        }

        antColony.addPheromone(tour[count - 1], tour[0], pheromone);
        antColony.addPheromone(tour[0], tour[count - 1], pheromone);

        if (Configuration.instance.isDebug) {
            LogEngine.instance.write("---");
        }
    }

    public void lookForWay() {
        DecimalFormat decimalFormat = new DecimalFormat("#0.000000000000000");

        if (Configuration.instance.isDebug) {
            LogEngine.instance.write("--- Ant[" + id + "].lookForWay");
        }

        int numberOfCities = data.getNumberOfCities();
        int randomIndexOfTownToStart = new Double((double) numberOfCities * Configuration.instance.randomGenerator.nextDouble()).intValue() + 1;

        if (Configuration.instance.isDebug) {
            LogEngine.instance.write("numberOfCities           : " + numberOfCities);
            LogEngine.instance.write("randomIndexOfTownToStart : " + randomIndexOfTownToStart);
        }

        tour[0] = randomIndexOfTownToStart;
        notJetVisited.removeElement(randomIndexOfTownToStart);

        for (int i = 1; i < numberOfCities; i++) {
            double sum = 0.0;

            if (Configuration.instance.isDebug) {
                LogEngine.instance.write("i : " + i + " - notJetVisited : " + notJetVisited);
            }

            for (int j = 0; j < notJetVisited.size(); j++) {
                int position = notJetVisited.elementAt(j);
                sum += antColony.getPheromone(tour[i - 1], position) / data.getDistance(tour[i - 1], position);
            }

            double selectionProbability = 0.0;
            double randomNumber = Configuration.instance.randomGenerator.nextDouble();

            if (Configuration.instance.isDebug) {
                LogEngine.instance.write("i : " + i + " - sum : " + decimalFormat.format(sum) + " - randomNumber : " + decimalFormat.format(randomNumber));
                LogEngine.instance.write("-");
            }

            for (int j = 0; j < notJetVisited.size(); j++) {
                int position = notJetVisited.elementAt(j);

                selectionProbability += antColony.getPheromone(tour[i - 1], position) / data.getDistance(tour[i - 1], position) / sum;

                if (Configuration.instance.isDebug) {
                    if (position < 10) {
                        LogEngine.instance.write("position : 0" + position + " - selectionProbability : " + decimalFormat.format(selectionProbability));
                    } else {
                        LogEngine.instance.write("position : " + position + " - selectionProbability : " + decimalFormat.format(selectionProbability));
                    }
                }

                if (randomNumber < selectionProbability) {
                    randomIndexOfTownToStart = position;
                    break;
                }
            }

            if (Configuration.instance.isDebug) {
                LogEngine.instance.write("randomIndexOfTownToStart : " + randomIndexOfTownToStart);
            }

            tour[i] = randomIndexOfTownToStart;
            notJetVisited.removeElement(randomIndexOfTownToStart);

            if (Configuration.instance.isDebug) {
                toString();
                LogEngine.instance.write("-");
            }
        }

        getObjectiveValue();

        if (Configuration.instance.isDebug) {
            LogEngine.instance.write("---");
        }
    }

    public String toString() {
        StringBuilder stringBuffer = new StringBuilder();
        int numberOfCities = data.getNumberOfCities();

        stringBuffer.append("[").append(id).append("] | tour : ");

        for (int i = 0; i < numberOfCities; i++) {
            stringBuffer.append(tour[i]).append(" ");
        }

        stringBuffer.append(" | objectiveValue : ").append(objectiveValue);

        return stringBuffer.toString();
    }
}