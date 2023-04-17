package ch.uzh.ifi.hase.soprafs23.AsosApi;

public class Price {


    private Current current;

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

    @Override
    public String toString() {
        return "Price{" +
                "current=" + current +
                '}';
    }
}
