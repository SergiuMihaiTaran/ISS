package Domain;

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
}
