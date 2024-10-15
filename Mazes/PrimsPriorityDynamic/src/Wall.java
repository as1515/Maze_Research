public class Wall implements Comparable<Wall> {
    Cell first;
    Cell second;
    Double weight;

    public Wall(Cell first, Cell second) {
        this.first = first;
        this.second = second;
        this.weight = Math.random();
    }

    @Override
    public int compareTo(Wall other) {
        return Double.compare(this.weight, other.weight);
    }
}