package aco;

public enum ProblemInstance {
    instance;

    public double[][] distance;

    public void setDistance(double[][] distance) {
        this.distance = distance;
    }

    public int getNumberOfCities() {
        return distance.length;
    }

    public double getDistance(int from, int to) {
        return distance[from - 1][to - 1];
    }
}