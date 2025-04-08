package Domain;

import java.util.Objects;

public class Competition extends Entity<Integer> {
    private int distance;
    private String style;

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public Competition(int distance, String style) {
        this.distance = distance;
        this.style = style;
    }

    @Override
    public String toString() {
        return "Competition{" +
                "distance=" + distance +
                ", style='" + style + '\'' +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Competition that = (Competition) o;
        return getId() == that.getId();  // or style + distance if no ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId()
        );  // or style + distance
    }
}
