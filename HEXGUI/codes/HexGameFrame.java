package HEXGUI.codes;

import java.awt.Dimension;

import javax.swing.JFrame;

import java.awt.*;

@SuppressWarnings("serial")
public class HexGameFrame extends JFrame {
    public HexGameFrame() {
        super("HEX GAME");
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        setSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;
        _game = new HexBoardPanel(6, HexBoardPanel.GameMode.pvp);
        add(_game, c);
        _util = new HexGameUtil(_game);
        c.gridheight = 1;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        add(_util, c);
    }

    private HexGameUtil _util;
    private HexBoardPanel _game;
}
