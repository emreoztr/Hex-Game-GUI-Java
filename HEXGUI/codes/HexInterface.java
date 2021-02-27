package HEXGUI.codes;

public interface HexInterface { //a hex game should have these functions
    public int boardSize(); //returns board size

    public void undo(); //undo last move

    public void reset(); //resets game

    public void resize(int size); //resize game

    public void save(String filename);  //saves game

    public void load(String filename); //loads game
}
