package HEXGUI.codes;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import HEXGUI.codes.HexBoardPanel.GameMode;

import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class HexGameUtil extends JPanel{
    public HexGameUtil(HexBoardPanel game) {
        _game = game;
        _sizeField = new JTextField();
        _saveName = new JTextField();
        _loadName = new JTextField();
        _save = new JButton("SAVE ");
        _load = new JButton("LOAD");
        _undo = new JButton("UNDO");
        _reset = new JButton("RESET");
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        

        c.gridx = 0;
        c.gridy = 0;
        add(new JLabel("Board Size:"), c);
        
        _sizeField.addActionListener(new TextFieldHandler());
        c.gridx++;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(_sizeField, c);
        
        _pvp = new JRadioButton("User vs. User");
        _pvp.setSelected(true);
        _pvp.setEnabled(false);
        c.gridy++;
        c.gridx--;
        c.gridwidth = 2;
        _pvp.addActionListener(new ModRadioButHandler());
        add(_pvp, c);

        _pve = new JRadioButton("Computer vs. User");
        c.gridy++;
        _pve.addActionListener(new ModRadioButHandler());
        add(_pve, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx++;
        c.gridy++;
        c.weightx = 5.0;
        _saveName.addActionListener(new TextFieldHandler());
        add(_saveName, c);
        c.gridx--;
        c.fill = GridBagConstraints.NONE;
        _save.addActionListener(new ButtonHandler());
        add(_save, c);

        c.gridx++;
        c.gridy++;
        c.fill = GridBagConstraints.HORIZONTAL;
        _loadName.addActionListener(new TextFieldHandler());
        add(_loadName, c);
        c.gridx--;
        c.fill = GridBagConstraints.NONE;
        _load.addActionListener(new ButtonHandler());
        add(_load, c);

        c.gridy++;
        _undo.addActionListener(new ButtonHandler());
        add(_undo, c);

        c.gridy++;
        _reset.addActionListener(new ButtonHandler());
        add(_reset, c);
    }

    class TextFieldHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == _sizeField) {
                Integer size = Integer.parseInt(_sizeField.getText());
                _game.resize(size);
                _game.setVisible(false);
                _game.setVisible(true);
                _sizeField.setText("");
            } else if (event.getSource() == _save) {
                _game.save(_saveName.getText());

                _game.setVisible(false);
                _game.setVisible(true);
                _saveName.setText("");
            }
            else if (event.getSource() == _load) {
                _game.load(_loadName.getText());
                
                _game.setVisible(false);
                _game.setVisible(true);
                _loadName.setText("");
            }
        }
    }
    
    class ButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == _save) {           
                _saveName.getActionListeners()[0].actionPerformed(event);   //save button calls actionperformed function of save textfield
            }
            else if (event.getSource() == _load) {                  
                _loadName.getActionListeners()[0].actionPerformed(event);   //load button calls actionperformed function of save textfield
            }
            else if (event.getSource() == _undo) {
                _game.undo();
            }
            else if (event.getSource() == _reset) {
                _game.reset();
                _game.setVisible(false);
                _game.setVisible(true);
            }
        }
    }

    class ModRadioButHandler implements ActionListener {    //changes gamemode
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == _pvp) {
                if (_pve.isSelected())
                    _pve.setSelected(false);
                _game.changeMod(GameMode.pvp);
                _game.setVisible(false);
                _game.setVisible(true);
                _pvp.setEnabled(false);
                _pve.setEnabled(true);
                
            }
            else if (event.getSource() == _pve) {
                if(_pvp.isSelected())
                    _pvp.setSelected(false);
                _game.changeMod(GameMode.pve);
                _game.setVisible(false);
                _game.setVisible(true);
                _pve.setEnabled(false);
                _pvp.setEnabled(true);
            }
        }
    }

    private HexBoardPanel _game;
    private JTextField _sizeField, _saveName, _loadName;
    private JButton _save, _load, _reset, _undo;
    private JRadioButton _pve, _pvp;
}
