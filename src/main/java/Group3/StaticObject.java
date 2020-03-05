package Group3;

public class StaticObject {

    /* Upper left */
    private double x1;
    private double y1;

    /* Upper right */
    private double x2;
    private double y2;

    /* Lower left */
    private double x3;
    private double y3;

    /* Lower right */
    private double x4;
    private double y4;

    public double getX1() {
        return x1;
    }

    public void setX1(double x) {
        this.x1 = x;
    }

    public double getX2() {
        return x2;
    }

    public void setX2(double x) {
        this.x2 = x;
    }

    public double getX3() {
        return x3;
    }

    public void setX3(double x) {
        this.x3 = x;
    }

    public double getX4() {
        return x4;
    }

    public void setX4(double x) {
        this.x4 = x;
    }

    public double getY1() {
        return y1;
    }

    public void setY1(double y) {
        this.y1 = y;
    }

    public double getY2() {
        return y2;
    }

    public void setY2(double y) {
        this.y2 = y;
    }

    public double getY3() {
        return y3;
    }

    public void setY3(double y) {
        this.y3 = y;
    }

    public double getY4() {
        return y4;
    }

    public void setY4(double y) {
        this.y4 = y;
    }

    /**
     * @param x
     * @param y
     * @return true if given point lies inside the rectangle object
     */
    public boolean isInside(double x, double y) {
        if (x > getX1() && x < getX4() &&
                y > getY1() && y < getY4()) return true;
        return false;
    }
}
