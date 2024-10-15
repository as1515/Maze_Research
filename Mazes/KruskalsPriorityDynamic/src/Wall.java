public class Wall implements Comparable<Wall> {
    int cell1;
    int cell2;
    double weight;

    Wall(int cell1, int cell2) {
        this.cell1 = cell1;
        this.cell2 = cell2;
        this.weight = Math.random();
    }

    @Override
    public int compareTo(Wall other) {
        return Double.compare(this.weight, other.weight);
    }
}