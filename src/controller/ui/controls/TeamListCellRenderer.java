package controller.ui.controls;

import data.Team;

import javax.swing.*;
import java.awt.*;

public class TeamListCellRenderer implements ListCellRenderer<Team>
{
    @Override
    public Component getListCellRendererComponent(JList<? extends Team> list,
                                                  Team value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus)
    {
        JLabel label = new JLabel();
        label.setOpaque(true);
        label.setText(value.getName() + " (" + value.getNumber() + ")");
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
