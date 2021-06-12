package base;

import aco.ProblemInstance;

public class City {
    private final int id;
    private final double x;
    private final double y;

    public City(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void calculateDistance(City city) {
        double dist = Math.hypot(city.x - this.x, city.y - this.y);
        ProblemInstance.instance.distance[this.id-1][city.id-1] = dist;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{ City : ");
        stringBuilder.append("id = ").append(id).append(" - ");
        stringBuilder.append("x = ").append(x).append(" - ");
        stringBuilder.append("y = ").append(y);
        stringBuilder.append(" }");
        return stringBuilder.toString();
    }
}