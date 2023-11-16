public class Alignment {

    private final StringBuilder x;
    private final StringBuilder y;

    public StringBuilder x() {
        return x;
    }

    public StringBuilder y() {
        return y;
    }

    public Alignment(StringBuilder x, StringBuilder y) {
        this.x = x;
        this.y = y;
    }

    public void print()
    {
        System.out.println(x);
        System.out.println(y);
    }
}
