package HEXGUI.codes;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.metal.MetalButtonUI;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

@SuppressWarnings("serial")
public class HexBoardPanel extends JPanel implements Cloneable, HexInterface{
    static public enum GameMode {
        pvp(0), pve(1);

        private int val;

        GameMode(int num) {
            val = num;
        }

        public int getNumVal() {
            return val;
        }
    }

    static public enum CellState {
        emp(0), player1(1), player2(2), p1_win(3), p2_win(4), comp(5), comp_win(6), risk(7), h_priority(8), l_priority(9), k_priority(10), enemy(11),
        comp_closed(12), no(13), closed(14);

        private int val;

        CellState(int num) {
            val = num;
        }

        public int getNumVal() {
            return val;
        }
    }

    class Cell extends JButton implements Cloneable{    //in this project cells are buttons, so i extend Cell class from JButton
            Cell() { this(0, 0);}
            Cell(int col, int row)  { this(col, row, CellState.emp);}

            Cell(int col, int row, CellState value) {
                super();
                setter(col, row, value);
            }
        
            public void setter(int col, int row) {
                setter(col, row, CellState.emp);
            }

            public void setter(CellState value) {
                setter(pos_col, pos_row, value);
            }

            public void setter(int col, int row, CellState value){
                pos_col = col;
                pos_row = row;
                cell_value = value;
                if (value == CellState.emp) {
                    setBackground(Color.WHITE);
                }
                else if (value == CellState.player1) {
                    setBackground(new Color(153, 0, 153));
                }
                else if (value == CellState.player2) {
                    setBackground(Color.BLACK);
                }
                else if (value == CellState.p1_win || value == CellState.p2_win) {
                    setBackground(Color.YELLOW);
                }
            }

            public int takeCol() {
                return pos_col;
            }

            public int takeRow() {
                return pos_row;
            } 

            public CellState takeValue() {
                return cell_value;
            }

            public Object clone() {
                Object returnVal = null;
                try{
                    returnVal = super.clone();
                } catch (CloneNotSupportedException er) {
                    System.exit(0);
                }

                return returnVal;
            }
        
            private CellState cell_value;
            private int pos_col;
            private int pos_row;
    }

    public HexBoardPanel(int bSize, GameMode mod) {
        super();
        JFrame error;
        if (bSize < 6) {
            error = new JFrame("ERROR");
            error.setSize(300, 200);
            error.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            error.add(new JLabel("Board size should bigger than or equal to 6!"));
            error.setVisible(true);
        }
        if (mod != GameMode.pve && mod != GameMode.pvp) {
            System.out.println("Invalid gamemode");
            System.exit(0);
        }
        setGamemode(mod);
        _allMoves = new ArrayList<Cell>();
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createLineBorder(Color.black));
        _turnOwn = CellState.player1;
        _bSize = bSize;
        prepareButtonLocations();
        if (getGamemode() == GameMode.pve) {
            setComp();
        }
        setSize(new Dimension(500, 500));
        setBackground(new Color(255, 205, 104));
    }
    

    public int boardSize() {    //returns size
        return _bSize;
    }

    public CellState getTurnown() {     //returns turn owner
        return _turnOwn;
    }

    public void undo() {
        if (numberOfMoves() > 0 && getGamemode() == GameMode.pvp) {
            if (_end == true) {     //if game is ended this part of the code rewinds the ending and returning to game
                _end = false;
                _winner = CellState.emp;
                for(Cell temp : _allMoves){
                    if(temp.takeValue() == CellState.p1_win)
                        temp.setter(CellState.player1);
                    else if(temp.takeValue() == CellState.p2_win)
                        temp.setter(CellState.player2);
                }
            }
            _allMoves.get(_allMoves.size() - 1).setter(CellState.emp);
            _allMoves.remove(_allMoves.size() - 1);
            if(_allMoves.size() > 0)
                _lastMove = _allMoves.get(_allMoves.size() - 1);
            changeTurnown();
            _moveCount--;
        }
        else if (getGamemode() == GameMode.pve && numberOfMoves() >= 2) {
            boolean p1Win = false;
            if (_end == true) {     //if game is ended this part of the code rewinds the ending and returning to game
                _end = false;
                if (_winner == CellState.player1) {
                    p1Win = true;
                }
                _winner = CellState.emp;
                for (Cell temp : _allMoves) {
                    if (temp.takeValue() == CellState.p1_win)
                        temp.setter(CellState.player1);
                    else if (temp.takeValue() == CellState.p2_win)
                        temp.setter(CellState.player2);
                }
            }
            _allMoves.get(_allMoves.size() - 1).setter(CellState.emp);
            _allMoves.get(_allMoves.size() - 2).setter(CellState.emp);
            _hexCompCell[_allMoves.get(_allMoves.size() - 2).takeRow()][_allMoves.get(_allMoves.size() - 2).takeCol()].setter(CellState.emp);
            _allMoves.remove(_allMoves.size() - 1);
            _allMoves.remove(_allMoves.size() - 1);
            if (p1Win) {    //if there was a win and winner was player 1, than this part deletes one more move to prevent bugs 
                _allMoves.get(_allMoves.size() - 1).setter(CellState.emp);
                _hexCompCell[_allMoves.get(_allMoves.size() - 1).takeRow()][_allMoves.get(_allMoves.size() - 1)
                        .takeCol()].setter(CellState.emp);
                _allMoves.remove(_allMoves.size() - 1);
                _moveCount--;
            }
            _compMoves.get(_compMoves.size() - 1).setter(CellState.emp);
            _compMoves.remove(_compMoves.size() - 1);
            if(_allMoves.size() > 0)
                _lastMove = _allMoves.get(_allMoves.size() - 1);
            _moveCount-=2;
            _moveCountComp--;
            _turnOwn = CellState.player1;
        }
    }

    public void changeMod(GameMode newMod) {
        if (newMod == GameMode.pve || newMod == GameMode.pvp) {
            setGamemode(newMod);
            reset();
        }
    }

    public void reset() {
        removeAll();
        prepareButtonLocations();
        _turnOwn = CellState.player1;
        _end = false;
        _p1Left = false;
        _p1Right = false;
        _p2Dn = false;
        _p2Up = false;
        _moveCount = 0;
        _moveCountComp = 0;
        _allMoves.clear();
        _winner = CellState.emp;
        if (getGamemode() == GameMode.pve) {
            resetComp();    //resets comp board
        }
    }

    private void setComp() {
        resetComp();
    }
    
    private int numberOfMoves() {
        return _moveCount;
    }
    
    private void resetComp(){   //resets computer board
        if (boardSize() > 5)
        {
            _hexCompCell = new Cell[boardSize()][boardSize()];
            for (int i = 0; i < boardSize(); ++i) {
                for (int j = 0; j < boardSize(); ++j) {
                    _hexCompCell[i][j] = new Cell(j, i, CellState.emp);
                }
            }
            _compMoves = new ArrayList<>();
            _moveCountComp = 0;
        }
    }

    public void resize(int size) {
        if (size >= 6) {
            _bSize = size;
            reset();
        } else {
            Error error = new Error("Board size should bigger than or equal to 6!");
        }
    }
    
    class Error extends JFrame {    //this is for creating Errors
        public Error(String error) {
            super("ERROR");
            _close = new JButton("OK");
            setLayout(new FlowLayout());
            setSize(500, 200);
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            add(new JLabel(error));
            _close.addActionListener(new closehandler(this));
            add(_close);
            setVisible(true);
        }

        class closehandler implements ActionListener {
            public closehandler(JFrame window) {
                _window = window;
            }

            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == _close) {
                    _window.setVisible(false);
                }
            }

            private JFrame _window;
        }

        private JButton _close;
    }
    
    class Winner extends JFrame {   //this is for creating winner scenes
        public Winner(String win) {
            super("WINNER");
            _close = new JButton("OK");
            setLayout(new FlowLayout());
            setSize(500, 200);
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            add(new JLabel(win));
            _close.addActionListener(new closehandler(this));
            add(_close);
            setVisible(true);
        }

        class closehandler implements ActionListener {
            public closehandler(JFrame window) {
                _window = window;
            }

            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == _close) {
                    _window.setVisible(false);
                }
            }
            private JFrame _window;
        }

        private JButton _close;
    }

    public void save(String filename) {
        File file = new File(filename);
        int compBoard = 0;
        FileWriter fwrite;
        try {
            file.createNewFile();
            fwrite = new FileWriter(file.getAbsoluteFile());

            fwrite.write("!\n");
            /*SAVES ALL THE NUMERIC DATA TO FILE*/
            fwrite.write(String.format("%d\n", getGamemode().getNumVal()));
            fwrite.write(String.format("%d\n", boardSize()));
            fwrite.write(String.format("%d\n", numberOfMoves()));
            fwrite.write(String.format("%d\n", _moveCountComp));
            fwrite.write(String.format("%b\n", _p1Left));
            fwrite.write(String.format("%b\n", _p1Right));
            fwrite.write(String.format("%b\n", _p2Dn));
            fwrite.write(String.format("%b\n", _p2Up));
            fwrite.write(String.format("%d\n", getTurnown().getNumVal()));
            fwrite.write(String.format("%d\n", _winner.getNumVal()));
            if (numberOfMoves() > 0)
                fwrite.write(String.format("%d %d %d\n", lastMove().takeCol(), lastMove().takeRow(),
                        lastMove().takeValue().getNumVal()));
            /* SAVES MOVES OF THE COMPUTERS IF IT IS A PVE GAME */
            fwrite.write("-\n");
            for (int i = 0; i < _moveCountComp; ++i)
                fwrite.write(String.format("%d %d %d\n", _compMoves.get(i).takeCol(), _compMoves.get(i).takeRow(),
                        _compMoves.get(i).takeValue().getNumVal()));
            fwrite.write("-\n");

            /* SAVES THE AI BOARD TO FILE BY SENDS COORDINATES AND VALUE OF THE POINTS */
            fwrite.write("=\n");
            if (getGamemode() == GameMode.pve) {
                for (int i = 0; i < boardSize(); ++i)
                    for (int j = 0; j < boardSize(); ++j)
                        if (_hexCompCell[i][j].takeValue() != CellState.emp)
                            compBoard++;
                fwrite.write(String.format("%d\n", compBoard));
            }
            for (int i = 0; i < boardSize() && getGamemode() == GameMode.pve; ++i)
                for (int j = 0; j < boardSize(); ++j)
                    if (_hexCompCell[i][j].takeValue() != CellState.emp)
                        fwrite.write(String.format("%d %d %d\n", _hexCompCell[i][j].takeCol(),
                                _hexCompCell[i][j].takeRow(), _hexCompCell[i][j].takeValue().getNumVal()));
            fwrite.write("=\n");

            /* SAVES THE MAIN BOARD TO FILE BY SENDS COORDINATES AND VALUE OF THE POINTS */
            fwrite.write("{\n");
            for (int i = 0; i < _allMoves.size(); ++i) {
                fwrite.write(String.format("%d %d %d\n", _allMoves.get(i).takeCol(), _allMoves.get(i).takeRow(),
                _allMoves.get(i).takeValue().getNumVal()));
            }
            fwrite.write("}\n");

            fwrite.write("!");
            fwrite.close();
        } catch (IOException er) {
            System.out.println("File error!");
            er.printStackTrace();
            System.exit(0);
        }
    }
    
    public void load(String filename) {
        File file = new File(filename);
        try{
            Scanner fread = new Scanner(file);
        
        String check;
        _moveCount = 0;
        _moveCountComp = 0;
        fread.nextLine();
        /*SAVES ALL THE NUMERIC DATA TO FILE*/
        setGamemode(GameMode.values()[fread.nextInt()]);
        fread.nextLine();
        _bSize = fread.nextInt();
        reset();
        fread.nextLine();
        _moveCount = fread.nextInt();
        fread.nextLine();
        _moveCountComp = fread.nextInt();
        fread.nextLine();
        check = fread.nextLine();
        if (check.equals("true"))
            _p1Left = true;
        else if (check.equals("false"))
            _p1Left = false;
            
        check = fread.nextLine();
        if (check.equals("true"))
            _p1Right = true;
        else if (check.equals("false"))
            _p1Right = false;

        check = fread.nextLine();
        if (check.equals("true"))
            _p2Dn = true;
        else if (check.equals("false"))
            _p2Dn = false;

        check = fread.nextLine();
        if (check.equals("true"))
            _p2Up = true;
        else if (check.equals("false"))
            _p2Up = false;
        
        _turnOwn = CellState.values()[fread.nextInt()];
        fread.nextLine();
        

        _winner = CellState.values()[fread.nextInt()];
        fread.nextLine();

        if (numberOfMoves() > 0) {
            _lastMove = getCell(fread.nextInt(), fread.nextInt());
            fread.nextLine();
        }
        check = fread.nextLine();
		if(check.equals("-") && getGamemode()==GameMode.pve ){
            _compMoves.clear();
            for (int i = 0; i < _moveCountComp; ++i)
            {
                _compMoves.add(getcompCell(fread.nextInt(), fread.nextInt()));
                fread.nextLine();
                
            }
        }
        fread.nextLine();
        check = fread.nextLine();

        /* LOADS THE AI BOARD TO FILE BY SENDS COORDINATES AND VALUE OF THE POINTS */
        if(check.equals("=") && getGamemode()==GameMode.pve){
            int compBoard = fread.nextInt();
            fread.nextLine();
            for(int i=0; i < compBoard; ++i){
                getcompCell(fread.nextInt(), fread.nextInt()).setter(CellState.values()[fread.nextInt()]);
                fread.nextLine();
            }
        }
        fread.nextLine();
        check = fread.nextLine();
        /* LOADS THE MAIN BOARD TO FILE BY SENDS COORDINATES AND VALUE OF THE POINTS */
        if (check.equals("{")) {
            int col, row;
            int val;
            for (int j = 0; j < numberOfMoves(); ++j) {
                col = fread.nextInt();
                row = fread.nextInt();
                val = fread.nextInt();
                getCell(col, row).setter(CellState.values()[val]);
                _allMoves.add(getCell(col, row));
                fread.nextLine();
            }
        }
        fread.nextLine();
        check = fread.nextLine();
        fread.close();
    } catch (FileNotFoundException er) {
        Error err = new Error("There is no file named as that!");
    }
    }

    private void changeTurnown(){
        if (getTurnown() == CellState.player1) {
            _turnOwn = CellState.player2;
        }
        else if (getTurnown() == CellState.player2) {
            _turnOwn = CellState.player1;
        }
    }

    private void prepareButtonLocations() {         //prepares and puts all the buttons in panel
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        _hexCells = new Cell[boardSize()][boardSize()];

        for (int i = 0; i < boardSize(); ++i) {
            for (int j = 0; j < boardSize(); ++j) {
                _hexCells[i][j] = new Cell(j, i);
                _hexCells[i][j].setOpaque(true);
                _hexCells[i][j].setBackground(Color.WHITE);
                _hexCells[i][j].addActionListener(new HexButtonAction());
                _hexCells[i][j].setUI(new MetalButtonUI());
                c.gridx = j * 2 + i;
                c.gridy = i * 2;
                c.gridwidth = 2;
                c.gridheight = 2;
                this.add(_hexCells[i][j], c);
            }
        }
        for (int i = 0; i < boardSize() * 3; ++i) {
            c.gridx = i;
            c.gridy = boardSize() + 1;
            c.gridwidth = 1;
            add(Box.createHorizontalStrut(1), c);
        }
    }


    class HexButtonAction implements ActionListener {       //this is for hex cell game buttons
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() instanceof Cell) {
                Cell temp = (Cell) event.getSource();

                if (temp.takeValue().equals(CellState.emp) && !isEnd()) {

                    if (getTurnown().equals(CellState.player1)) {
                        temp.setter(CellState.player1);
                        if (temp.takeCol() == 0) {
                            _p1Left = true;
                        }
                        else if (temp.takeCol() == boardSize() - 1) {
                            _p1Right = true;
                        }
                    } else if (getTurnown().equals(CellState.player2)) {
                        temp.setter(CellState.player2);
                        if (temp.takeRow() == 0) {
                            _p2Up = true;
                        }
                        else if (temp.takeRow() == boardSize() - 1) {
                            _p2Dn = true;
                        }
                    }
                    addLastMove(temp);
                    checkWin();
                    changeTurnown();
                    if (getGamemode() == GameMode.pve && getTurnown() == CellState.player2 && !isEnd()) {
                        comp_save_enemy_move();
                        comp_find_path();
                        Cell move = comp_put_move();
                        _moveCountComp++;
                        if(move.takeRow() == 0 && _p2Up == false)
                            _p2Up = true;
                        else if(move.takeRow() == boardSize()-1 && _p2Dn == false)
                            _p2Dn = true;

                        addLastMove(getCell(move.takeCol(), move.takeRow()));
                        checkWin();
                        changeTurnown();
                    }
                    
                }
            }
        }
    }


    private void addLastMove(Cell move) {
        _lastMove = move;
        _allMoves.add(move);
        _moveCount++;
    }

    private Cell lastMove() {
        return _lastMove;
    }
    
    private boolean isEnd() {
        return _end;
    }

    private GameMode getGamemode() {
        return _mod;
    }
    
    private void setGamemode(GameMode mod) {
        _mod = mod;
    }

    private void checkWin() {
        Cell temp;
        if (_p1Left == true && _p1Right == true) {
            for (int i = 0; i < boardSize(); ++i) {
                temp = getCell(0, i);
                if (temp.takeValue() == CellState.player1)
                    if (checkWcond(temp.takeCol(), temp.takeRow(), CellState.player1)) {
                        setWinner(CellState.player1);
                        Winner window = new Winner("Winner is player 1!!!");
                    }
            }
        }
        if (_p2Up == true && _p2Dn == true) {
            for (int i = 0; i < boardSize(); ++i) {
                temp = getCell(i, 0);
                if (temp.takeValue() == CellState.player2)
                    if (checkWcond(temp.takeCol(), temp.takeRow(), CellState.player2)) {
                        setWinner(CellState.player2);
                        Winner window = new Winner("Winner is player 2!!!");
                    }
            }
        }
        if (!getWinner().equals(CellState.emp)) {
            _end = true;
        }
    }

    private void incrValues(int[] value, int direction){
        switch(direction){
            case 0:
                value[0]=0;
                value[1]=-1;
                break;
            case 1:
                value[0]=1;
                value[1]=-1;
                break;
            case 2:
                value[0]=0;
                value[1]=1;
                break;
            case 3:
                value[0]=-1;
                value[1]=1;
                break;
            case 4:
                value[0]=1;
                value[1]=0;
                break;
            case 5:
                value[0]=-1;
                value[1]=0;
                break;
            default:
                value[0]=0;
                value[1]=0;
                break;
        }
    }

    private boolean checkWcond( int col,  int row,  CellState player){
        boolean win = false;
        int[] incrVal = new int[2];
    
        if(player==CellState.player1 && (col>=0 && col< boardSize()) && (row>=0 && row<boardSize())){
            if(getCell(col, row).takeValue()==CellState.player1){
                getCell(col, row).setter(CellState.p1_win);
    
                if(col==boardSize()-1){
                    win = true;
                }
    
                for (int i = 0; i < 6 && win==false; ++i){
                    incrValues(incrVal, i);
                    win = checkWcond(col + incrVal[0], row + incrVal[1], player);
                }
    
                if(win==false)
                    getCell(col, row).setter(CellState.player1);
            }
        }
    
        else if(player==CellState.player2 && (col>=0 && col<boardSize()) && (row>=0 && row<boardSize())){
            if(getCell(col, row).takeValue()==CellState.player2){
                getCell(col, row).setter(CellState.p2_win);
    
                if(row==boardSize()-1){
                    win = true;
                }
    
                for (int i = 0; i < 6 && win==false; ++i)
                {
                    incrValues(incrVal, i);
                    win = checkWcond(col + incrVal[0], row + incrVal[1], player);
                }
    
                if(win==false)
                    getCell(col, row).setter(CellState.player2);
            }
        }
    
        return win;
    }
    

    public CellState getWinner() {
        return _winner;
    }

    private void setWinner(CellState winner){
        _winner = winner;
    }

    private Cell getCell(int col, int row) {
        return _hexCells[row][col];
    }


    


    private void comp_incr_values_upleft(int direction, int[] value) { //incrementation values for up leftt direction

        switch (direction) {
            case 0:
                value[0] = 0;
                value[1] = -1;
                break;
            case 1:
                value[0] = -1;
                value[1] = 0;
                break;
            case 2:
                value[0] = 1;
                value[1] = -1;
                break;
            case 3:
                value[0] = 1;
                value[1] = 0;
                break;
            default:
                value[0] = 0;
                value[1] = 0;
                break;
        }
    }
    
    private void comp_incr_values_dnleft(int direction, int[] value){	//incrementation values for down leftt direction

        switch (direction) {
        case 0:
            value[0] = -1;
            value[1] = 1;
            break;
        case 1:
            value[0] = 0;
            value[1] = 1;
            break;
        case 2:
            value[0] = -1;
            value[1] = 0;
            break;
        case 3:
            value[0] = 1;
            value[1] = 0;
            break;
        default:
            value[0] = 0;
            value[1] = 0;
            break;
        }
    }

    private void comp_incr_values_upright( int direction, int[] value) {		//incrementation values for up right direction

        switch (direction) {
        case 0:
            value[0] = 1;
            value[1] = -1;
            break;
        case 1:
            value[0] = 0;
            value[1] = -1;
            break;
        case 2:
            value[0] = 1;
            value[1] = 0;
            break;
        case 3:
            value[0] = -1;
            value[1] = 0;
            break;
        default:
            value[0] = 0;
            value[1] = 0;
            break;
        }
    }
    
    private void comp_incr_values_dnright(int direction, int[] value) { //incrementation values for down right direction

        switch (direction) {
            case 0:
                value[0] = 0;
                value[1] = 1;
                break;
            case 1:
                value[0] = 1;
                value[1] = 0;
                break;
            case 2:
                value[0] = -1;
                value[1] = 1;
                break;
            case 3:
                value[0] = -1;
                value[1] = 0;
                break;
            default:
                value[0] = 0;
                value[1] = 0;
                break;
        }
    }
    
    private boolean isAccessible( int col,  int row) {
        boolean returnVal = false;
        if (col < boardSize() && col >= 0 && row < boardSize() && row >= 0)
            returnVal = true;
    
        return returnVal;
    }
    
    private Cell getcompCell(int col, int row) {
        return _hexCompCell[row][col];
    }

    private boolean comp_is_empty( int loc_col,  int loc_row)  {	//checks the place is safe or not
        boolean returnVal=false;
        if (isAccessible(loc_col, loc_row))
            if(getcompCell(loc_col, loc_row).takeValue()!=CellState.enemy && getcompCell(loc_col, loc_row).takeValue()!=CellState.closed && getcompCell(loc_col, loc_row).takeValue()!=CellState.comp_closed)
                returnVal=true;
    
        return returnVal;
    }
    
    int comp_check_row( int col,  int row) {	//checks the enemy in the row
        int returnVal=-1;
        boolean end_look=false;
    
        for(int i=0; i<col; ++i){
            if(getcompCell(i, row).takeValue()==CellState.enemy){
                returnVal=col-i;
                end_look=true;
            }
            if(returnVal!=-1 && (getcompCell(i, row).takeValue()==CellState.comp || getcompCell(i, row).takeValue()==CellState.comp_closed)){
                returnVal=-1;
                end_look=false;
            }
        }
    
        for(int i=boardSize()-1; i>col && !end_look; --i){
            if(getcompCell(i, row).takeValue()==CellState.enemy)
                returnVal=i-col;
            if(returnVal!=-1 && (getcompCell(i, row).takeValue()==CellState.comp || getcompCell(i, row).takeValue()==CellState.comp_closed)){
                returnVal=-1;
            }
        }
    
        return returnVal;
    }
    
    int comp_check_row_risk( int col,  int row) {	//checks the risk value in a row
        int returnVal=-1;
        boolean end_look=false;
    
    
        for(int i=0; i<col; ++i){
            if(getcompCell(i, row).takeValue()==CellState.risk){
                returnVal=col-i;
                end_look=true;
            }
            if(returnVal!=-1 && (getcompCell(i, row).takeValue()==CellState.comp || getcompCell(i, row).takeValue()==CellState.comp_closed)){
                returnVal=-1;
                end_look=false;
            }
        }
    
        for(int i=boardSize()-1; i>col && !end_look; --i){
            if(getcompCell(i, row).takeValue()==CellState.risk)
                returnVal=i-col;
            if(returnVal!=-1 && (getcompCell(i, row).takeValue()==CellState.comp || getcompCell(i, row).takeValue()==CellState.comp_closed)){
                returnVal=-1;
            }
        }
    
        return returnVal;
    }
    
    int comp_check_risk( int col,  int row) {	//checks the risk value for edges of place
        int r_count=0;
        int[] incrVal = new int[2];
        for (int i = 0; i < 6; i++)
        {
            incrValues(incrVal, i);
            if(isAccessible(col+incrVal[0], row+incrVal[1]))
                if(getcompCell(col+incrVal[0], row+incrVal[1]).takeValue()==CellState.risk)
                    r_count+=1;
        }
        if((col==boardSize()-1 || col==0) && r_count>=1)
            r_count=5;
        return r_count;
    }
    
    int comp_check_neighbor( int col,  int row) {	//returns the count of already moved places at the edges of place
        int c_count=0;
        int[] incrVal = new int[2];
    
        for(int i=0; i<6; i++){
            incrValues(incrVal, i);
            if(isAccessible(col+incrVal[0], row+incrVal[1]))
                if(getcompCell(col+incrVal[0], row+incrVal[1]).takeValue()==CellState.comp)
                    c_count+=1;
        }
    
        return c_count;
    }
    
    void comp_reset_path(){	//resets L(Low priority values), H(High priority values) and K(Highest priority values)
        for(int i=0; i<boardSize(); ++i){
            for(int j=0; j<boardSize(); ++j){
                if(getcompCell(j, i).takeValue()==CellState.l_priority || getcompCell(j, i).takeValue()==CellState.h_priority || getcompCell(j, i).takeValue()==CellState.k_priority)
                    getcompCell(j, i).setter(CellState.emp);
            }
        }
    }
    
    boolean comp_update_pathfinding_upright( int col,  int row, myInt count){
        int[] incrVal = new int[2];
        boolean returnVal=false;
        CellState holderBf, holderAf;
        if(isAccessible(col, row)){
            if (getcompCell(col, row).takeValue() != CellState.comp)
                count.change(count.num+1);
    
            if (count.num < 4 * boardSize())
            {
                holderBf = getcompCell(col, row).takeValue(); //saving for in case turning it back
                holderAf = getcompCell(col, row).takeValue();
                if (holderBf != CellState.comp){
                    if ((comp_check_row(col, row) <= 1 && comp_check_row(col, row) >= 0) || comp_check_risk(col, row) >= 4 || holderBf == CellState.risk)
                        holderAf = CellState.k_priority;
                    else if (comp_check_risk(col, row) >= 1)
                        holderAf = CellState.h_priority;
                    
                    else
                        holderAf = CellState.l_priority;	
                }
                getcompCell(col, row).setter(CellState.closed); //changing the name of the place to make sure the program wont go to this place again
                if (holderBf == CellState.comp)
                    getcompCell(col, row).setter(CellState.comp_closed);; //if it is move of the computer program will change it to the W instead of Q
    
                if (row == 0) //if it is at the top, path condition satisfies and return true to end recursive
                    returnVal = true;
    
                for (int i = 0; i < 4 && returnVal == false; ++i)
                { //taking the increment values and checks the new place is safe or not
                    comp_incr_values_upright(i, incrVal);
    
                    returnVal = comp_is_empty(col + incrVal[0], row + incrVal[1]);
    
                    if (returnVal == true)
                        returnVal = comp_update_pathfinding_upright(col + incrVal[0], row + incrVal[1], count);
                }
    
                if (returnVal == false)
                { //if it has not succeed it takes the value of place back
                    getcompCell(col, row).setter(holderBf);
                    if (count.num < 4 * boardSize()) //if counter is more than the border, it doesn't decrease counter
                        count.change(count.num-1);
                }
                else
                {
                    getcompCell(col, row).setter(holderAf); //if it has succeed place takes the new value
                }
            }
        }
        return returnVal;
    }
    
    boolean comp_update_pathfinding_dnright( int col,  int row, myInt count){
        int[] incrVal = new int[2];
        boolean returnVal=false;
        CellState holderBf, holderAf;
    
        if(isAccessible(col, row)){
            if(getcompCell(col, row).takeValue()!=CellState.comp)
                count.change(count.num+1);
    
            if(count.num<4*boardSize()){
                
                holderBf = getcompCell(col, row).takeValue(); //saving for in case turning it back
                holderAf = getcompCell(col, row).takeValue();
    
                if(holderBf!=CellState.comp){	//in this if section program choose the priority for the place
                    if((comp_check_row(col, row)<=1 && comp_check_row(col, row)>=0) || comp_check_risk(col, row)>=4 || holderBf==CellState.risk)
                        holderAf=CellState.k_priority;
                    else if(comp_check_risk(col, row)>=1)
                        holderAf=CellState.h_priority;
                    else
                        holderAf=CellState.l_priority;
                }
    
                getcompCell(col, row).setter(CellState.closed);	//changing the name of the place to make sure the program wont go to this place again
                if(holderBf==CellState.comp)
                    getcompCell(col, row).setter(CellState.comp_closed);	//if it is move of the computer program will change it to the W instead of Q
    
                if(row==boardSize()-1)	//if it is at the top, path condition satisfies and return true to end recursive
                    returnVal=true;
    
                for(int i=0; i<4 && returnVal==false; ++i){	//taking the increment values and checks the new place is safe or not
                    comp_incr_values_dnright(i, incrVal);
    
                    returnVal=comp_is_empty(col+incrVal[0], row+incrVal[1]);
    
                    if(returnVal==true)
                        returnVal=comp_update_pathfinding_dnright(col+incrVal[0], row+incrVal[1], count);
                }
    
                if(returnVal==false){	//if it has not succeed it takes the value of place back
                    getcompCell(col, row).setter(holderBf);
                    if(count.num<4*boardSize())	//if counter is more than the border, it doesn't decrease counter
                        count.change(count.num-1);
                }
                else{
                    getcompCell(col, row).setter(holderAf);	//if it has succeed place takes the new value
                }
            }
        }
        return returnVal;
    }
    
    boolean comp_update_pathfinding_upleft( int col,  int row, myInt count){
        int[] incrVal = new int[2];
        boolean returnVal=false;
        CellState holderBf, holderAf;
    
        if(isAccessible(col, row)){
            if(getcompCell(col, row).takeValue()!=CellState.comp)
                count.change(count.num+1);
    
            if(count.num<4*boardSize()){
                
                holderBf = getcompCell(col, row).takeValue(); //saving for in case turning it back
                holderAf = getcompCell(col, row).takeValue();
    
                if(holderBf!=CellState.comp){	//in this if section program choose the priority for the place
                    if((comp_check_row(col, row)<=1 && comp_check_row(col, row)>=0) || comp_check_risk(col, row)>=4 || holderBf==CellState.risk)
                        holderAf=CellState.k_priority;
                    else if(comp_check_risk(col, row)>=1)
                        holderAf=CellState.h_priority;
                    else
                        holderAf=CellState.l_priority;
                }
    
                getcompCell(col, row).setter(CellState.closed);	//changing the name of the place to make sure the program wont go to this place again
                if(holderBf==CellState.comp)
                    getcompCell(col, row).setter(CellState.comp_closed);	//if it is move of the computer program will change it to the W instead of Q
    
                if(row==0)	//if it is at the top, path condition satisfies and return true to end recursive
                    returnVal=true;
    
                for(int i=0; i<4 && returnVal==false; ++i){	//taking the increment values and checks the new place is safe or not
                    comp_incr_values_upleft(i, incrVal);
    
                    returnVal=comp_is_empty(col+incrVal[0], row+incrVal[1]);
    
                    if(returnVal==true)
                        returnVal=comp_update_pathfinding_upleft(col+incrVal[0], row+incrVal[1], count);
                }
    
                if(returnVal==false){	//if it has not succeed it takes the value of place back
                    getcompCell(col, row).setter(holderBf);
                    if(count.num<4*boardSize())	//if counter is more than the border, it doesn't decrease counter
                        count.change(count.num-1);
                }
                else{
                    getcompCell(col, row).setter(holderAf);	//if it has succeed place takes the new value
                }
            }
        }
        return returnVal;
    }
    
    boolean comp_update_pathfinding_dnleft( int col,  int row, myInt count){
        int[] incrVal = new int[2];
        boolean returnVal=false;
        CellState holderBf, holderAf;
    
        if(isAccessible(col, row)){
            if(getcompCell(col, row).takeValue()!=CellState.comp)
                count.change(count.num+1);
    
            if(count.num<4*boardSize()){
                
                holderBf = getcompCell(col, row).takeValue(); //saving for in case turning it back
                holderAf = getcompCell(col, row).takeValue();
    
                if(holderBf!=CellState.comp){	//in this if section program choose the priority for the place
                    if((comp_check_row(col, row)<=1 && comp_check_row(col, row)>=0) || comp_check_risk(col, row)>=4 || holderBf==CellState.risk)
                        holderAf=CellState.k_priority;
                    else if(comp_check_risk(col, row)>=1)
                        holderAf=CellState.h_priority;
                    else
                        holderAf=CellState.l_priority;
                }
    
                getcompCell(col, row).setter(CellState.closed);	//changing the name of the place to make sure the program wont go to this place again
                if(holderBf==CellState.comp)
                    getcompCell(col, row).setter(CellState.comp_closed);	//if it is move of the computer program will change it to the W instead of Q
    
                if(row==boardSize()-1)	//if it is at the top, path condition satisfies and return true to end recursive
                    returnVal=true;
    
                for(int i=0; i<4 && returnVal==false; ++i){	//taking the increment values and checks the new place is safe or not
                    comp_incr_values_dnleft(i, incrVal);
    
                    returnVal=comp_is_empty(col+incrVal[0], row+incrVal[1]);
    
                    if(returnVal==true)
                        returnVal=comp_update_pathfinding_dnleft(col+incrVal[0], row+incrVal[1], count);
                }
    
                if(returnVal==false){	//if it has not succeed it takes the value of place back
                    getcompCell(col, row).setter(holderBf);
                    if(count.num<4*boardSize())	//if counter is more than the border, it doesn't decrease counter
                        count.change(count.num-1);
                }
                else{
                    getcompCell(col, row).setter(holderAf);	//if it has succeed place takes the new value
                }
            }
        }
        return returnVal;
    }
    
    int comp_calc_row_risk( int col,  int row) {	//calculates the risk at the row
        int risk_count_l=0, risk_count_r=0;
    
        for(int i=0; i<col; ++i){
            if(getcompCell(i, row).takeValue()==CellState.risk || getcompCell(i, row).takeValue()==CellState.enemy)
                risk_count_l+=1;
            else if(getcompCell(i, row).takeValue()==CellState.comp)
                risk_count_l=0;
        }
    
        for(int i=boardSize()-1; i>col; --i){
            if(getcompCell(i, row).takeValue()==CellState.risk || getcompCell(i, row).takeValue()==CellState.enemy)
                risk_count_r+=1;
            else if(getcompCell(i, row).takeValue()==CellState.comp)
                risk_count_r=0;
        }
        return (risk_count_l+risk_count_r);
    }
    
    Cell comp_put_move(){
        boolean end_loop=false, risk_check=false;
        int col, row, biggest_risk=-1, risk;
        Cell returnVal;

        int[] holder = new int[2];
        int[] c_move = new int[2];
    
        for(row=0; row<boardSize(); ++row){
            for(col=0; col<boardSize(); ++col){
                if(getcompCell(col, row).takeValue()==CellState.k_priority){					//checks the priority for highest priority level
                    risk=comp_calc_row_risk(col, row);
                    if(risk>biggest_risk){
                        holder[0]=col;
                        holder[1]=row;
                        biggest_risk=risk;
                    }
                    end_loop=true;
                }
            }
        }
    
        if(end_loop==false)
        for(row=0; row<boardSize(); ++row){
            for(col=0; col<boardSize(); ++col){
                if(getcompCell(col, row).takeValue()==CellState.h_priority){					//checks the priority for high priority level
                    risk=comp_calc_row_risk(col, row);
                    if(risk>biggest_risk){
                        holder[0]=col;
                        holder[1]=row;
                        biggest_risk=risk;
                    }
                    end_loop=true;
                }
            }
        }
    
        if(end_loop==false)
        for(row=0; row<boardSize(); ++row){
            for(col=0; col<boardSize(); ++col){
                if(getcompCell(col, row).takeValue()==CellState.l_priority){			//checks the priority for low priority level
                    risk=comp_calc_row_risk(col, row);
                    if(risk>biggest_risk){
                        holder[0]=col;
                        holder[1]=row;
                        biggest_risk=risk;
                    }
                    end_loop=true;
                }
            }
        }
        if(risk_check==false && end_loop==true){
            getcompCell(holder[0], holder[1]).setter(CellState.comp);		//makes the move
            getCell(holder[0], holder[1]).setter(CellState.player2);
            c_move[0]=holder[0];
            c_move[1]=holder[1];
            risk_check=true;
        }
        //returnVal.setter(c_move[0], c_move[1], CellState.comp);
        returnVal = _hexCompCell[c_move[1]][c_move[0]];
        addcompMove(returnVal);
    
        return getCell(c_move[0], c_move[1]);
    }
    
    void comp_save_enemy_move(){	//saves the enemy move to the ai board
        int row, col;
        int[] incrVal = new int[2];
    
        row=lastMove().takeRow();
        col=lastMove().takeCol();
        getcompCell(col, row).setter(CellState.enemy);
        for(int i=0; i<6; i++){
            incrValues(incrVal, i);
            if(isAccessible(col+incrVal[0], row+incrVal[1]))
                if((getcompCell(col+incrVal[0], row+incrVal[1]).takeValue()!=CellState.enemy && getcompCell(col+incrVal[0], row+incrVal[1]).takeValue()!=CellState.comp))
                    getcompCell(col+incrVal[0], row+incrVal[1]).setter(CellState.risk);	//it writes 'R' to the neighbors of new move, 'R' means that place has risk
        }
    }
    
    
    void comp_no_logical_move(){	//fills the ai board with logically rational priorities
        for(int i=0; i<boardSize(); ++i){
            for(int j=0; j<boardSize(); ++j){
                if(getcompCell(j, i).takeValue()!=CellState.comp && getcompCell(j, i).takeValue()!=CellState.enemy){
                    if((comp_check_row(j, i)<=1 && comp_check_row(j, i)>=0) || comp_check_risk(j, i)>=4)
                        getcompCell(j, i).setter(CellState.k_priority);
                    else if(getcompCell(j, i).takeValue()==CellState.risk || comp_check_risk(j, i)>=1)
                        getcompCell(j, i).setter(CellState.h_priority);
                    else
                        getcompCell(j, i).setter(CellState.l_priority);
                }
            }
        }
    }
    
    int comp_horizontal_side_risk_check() {
        int returnVal=1, left_risk=0, right_risk=0;
    
        for (int row = 0; row < boardSize(); ++row) {
            for (int col = 0; col < boardSize(); ++col) {
                if (getcompCell(col, row).takeValue() == CellState.enemy) {
                    if (col <= (boardSize() / 2))
                        ++left_risk;
                    else
                        ++right_risk;
                }
            }
        }
    
        if (left_risk >= right_risk)
            returnVal = 1;
        else
            returnVal = 0;
    
        return returnVal;
    }
    
    int comp_vertical_side_risk_check() {
        int returnVal = 1, up_risk = 0, dn_risk = 0;
    
        for (int row = 0; row < boardSize(); ++row) {
            for (int col = 0; col < boardSize(); ++col) {
                if (getcompCell(col, row).takeValue() == CellState.enemy) {
                    if (row <= (boardSize() / 2))
                        ++up_risk;
                    else
                        ++dn_risk;
                }
            }
        }
    
        if (up_risk >= dn_risk)
            returnVal = 1;
        else
            returnVal = 0;
    
        return returnVal;
    }
    
    class myInt {
        public int num = 0;

        void change(int count) {
            num = count;}
    }

    void comp_find_path() {
        int side_h, side_v, short_path=boardSize()*boardSize(), path_pointer=-1;
        boolean updateValup=false, updateValdn=false;
        int col, row;
        myInt length_up = new myInt();
        myInt length_dn = new myInt();
        side_h = comp_horizontal_side_risk_check();
        side_v = comp_vertical_side_risk_check();
        comp_reset_path();
    
        for (int i = 0; i < _moveCountComp; ++i) {   //checks the path for upright and downright directions
            col = _compMoves.get(i).takeCol();
            row = _compMoves.get(i).takeRow();
            if (comp_check_neighbor(col, row) <= 1) {
                if (side_h == 0) {
                    if (side_v == 0) {
                        updateValup = comp_update_pathfinding_upright(col, row, length_up);
                        updateValdn = comp_update_pathfinding_dnleft(col, row, length_dn);
                    }
                    else {
                        updateValup = comp_update_pathfinding_upleft(col, row, length_up);
                        updateValdn = comp_update_pathfinding_dnright(col, row, length_dn);
                    }
                }
    
                else {
                    if (side_v == 0) {
                        updateValup = comp_update_pathfinding_upleft(col, row, length_up);
                        updateValdn = comp_update_pathfinding_dnright(col, row, length_dn);
                    }
                    else {
                        updateValup = comp_update_pathfinding_upright(col, row, length_up);
                        updateValdn = comp_update_pathfinding_dnleft(col, row, length_dn);
                    }
                }
    
                if (updateValup == true && updateValdn == true) {  //if both of them reached to the end points program gets into this if
                    if (short_path > (length_up.num + length_dn.num)) {	//if it is the shortest path that have been found it saves the location
                        path_pointer = i;
                        short_path = length_up.num + length_dn.num;	//updating the shortest distance that have been found
                    }
                }
    
                comp_reset_path();
                length_up.num = 0;
                length_dn.num = 0;
            }
        }
    
    
    
        if (path_pointer == -1)
            comp_no_logical_move();
        
        else {
            col = _compMoves.get(path_pointer).takeCol();
            row = _compMoves.get(path_pointer).takeRow();
            if (side_h == 0) {
                if (side_v == 0) {
                    updateValup = comp_update_pathfinding_upright(col, row, length_up);
                    updateValdn = comp_update_pathfinding_dnleft(col, row, length_dn);
                }
                else {
                    updateValup = comp_update_pathfinding_upleft(col, row, length_up);
                    updateValdn = comp_update_pathfinding_dnright(col, row, length_dn);
                }
            }
    
            else {
                if (side_v == 0) {
                    updateValup = comp_update_pathfinding_upleft(col, row, length_up);
                    updateValdn = comp_update_pathfinding_dnright(col, row, length_dn);
                }
                else {
                    updateValup = comp_update_pathfinding_upright(col, row, length_up);
                    updateValdn = comp_update_pathfinding_dnleft(col, row, length_dn);
                }
            }
        }
    }
    
    Cell comp_first_move( int prowMove/*=default value=-1*/) { //calculating the first move of the computer
        int horizon, moveRow, moveCol;
        Cell returnVal;
        int actualSize = boardSize() - 1;
    
        horizon = comp_horizontal_side_risk_check();
    
        moveCol = (horizon == 0) ? (actualSize - (3 * actualSize / 4)) : (actualSize - (actualSize / 4));	//making a risk calculation, and decides where to move in column
    
        moveRow = (prowMove == -1) ? (actualSize / 2) : (prowMove);	//if first turn in the hands of the computer making its row move to the middle
    
        getcompCell(moveCol, moveRow).setter(CellState.k_priority);
        returnVal = comp_put_move();
    
        return returnVal;
    }
    
    
    
    void addcompMove( Cell newMove){
        _compMoves.add(newMove);
    }
    
    public Object clone() {     //deep copy
        HexBoardPanel returnVal = null;
        try{
            returnVal = (HexBoardPanel) super.clone();
            returnVal._hexCells = new Cell[boardSize()][boardSize()];
            returnVal._hexCompCell = new Cell[boardSize()][boardSize()];
            returnVal._lastMove = (Cell) _lastMove.clone();
            returnVal._allMoves = new ArrayList<Cell>();
            returnVal._compMoves = new ArrayList<Cell>();

            for (int i = 0; i < boardSize(); ++i) {
                for (int j = 0; j < boardSize(); ++j) {
                    returnVal._hexCells[i][j].setter(j, i, _hexCells[i][j].takeValue());
                    returnVal._hexCompCell[i][j].setter(j, i, _hexCompCell[i][j].takeValue());
                }
            }

            for (Cell temp : _allMoves) {
                returnVal._allMoves.add((Cell) temp.clone());
            }

            for (Cell temp : _compMoves) {
                returnVal._compMoves.add((Cell) temp.clone());
            }

        } catch (CloneNotSupportedException er) {
            System.exit(0);
        }

        return (Object) returnVal;
    }

    private CellState _winner = CellState.emp;
    private boolean _end = false;
    private boolean _p1Left = false, _p1Right = false, _p2Dn = false, _p2Up = false;
    private GameMode _mod;
    private Cell _hexCells[][];
    private Cell _hexCompCell[][];
    private Cell _lastMove;
    private ArrayList<Cell> _allMoves, _compMoves;
    private int _moveCount = 0;
    private int _moveCountComp=0;
    private int _bSize;
    private CellState _turnOwn;
}