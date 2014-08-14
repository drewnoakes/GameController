package controller.ui.controls;

import data.League;

import javax.swing.*;
import java.awt.*;

public class LeagueListCellRenderer implements ListCellRenderer<League>
{
    @Override
    public Component getListCellRendererComponent(JList<? extends League> list,
                                                  League value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus)
    {
        JLabel label = new JLabel();
        label.setOpaque(true);
        label.setText(value.getName());
        if (isSelected) {
            label.setForeground(list.getSelectionForeground());
            label.setBackground(list.getSelectionBackground());
        } else {
            label.setForeground(list.getForeground());
            label.setBackground(list.getBackground());
        }
        return label;
    }
}
