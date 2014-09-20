package lib;

/**
 * Medžio klasė
 */
public class Tree extends Plant {

    public Tree(String name) {
        super(name);
    }

    @Override
    public boolean isTree() {
        return true;
    }

    @Override
    public String getType() {
        return "Medis";
    }
}
