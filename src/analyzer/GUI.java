package analyzer;

import common.Log;
import common.TotalScaleLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * This window is shown to select log-files for analyzing.
 *
 * @author Michel Bartsch
 */
public class GUI
{
    private final static String TITLE = "Log Analyzer";
    private final static int WINDOW_WIDTH = 700;
    private final static int WINDOW_HEIGHT = 600;
    private final static int STANDARD_SPACE = 10;
    private final static int CHECKBOX_WIDTH = 24;
    private final static Color LIST_HIGHLIGHT = new Color(150, 150, 255);
    private final static String CLEAN = "Clean";
    private final static String ANALYZE = "Analyze";

    public final static String HTML = "<html>";
    public final static String HTML_LF = "<br>";
    public final static String HTML_RED = "<font color='red'>";
    public final static String HTML_END = "</font>";
    
    private final JFrame frame;
    private final DefaultListModel<CheckListItem> list;
    private final ListSelectionModel selection;
    private final JLabel info;

    /**
     * Creates a new GUI.
     */
    public GUI()
    {
        frame = new JFrame(TITLE);

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        frame.setLocation((width-WINDOW_WIDTH)/2, (height-WINDOW_HEIGHT)/2);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        TotalScaleLayout layout = new TotalScaleLayout(frame);
        frame.setLayout(layout);
        
        list = new DefaultListModel<CheckListItem>();
        JList<CheckListItem> listDisplay = new JList<CheckListItem>(list);
        listDisplay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listDisplay.setCellRenderer(new CheckListRenderer());
        listDisplay.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent event)
            {
                if (event.getPoint().x > CHECKBOX_WIDTH) {
                    return;
                }
                JList list = (JList)event.getSource();
                int index = list.locationToIndex(event.getPoint());
                CheckListItem item = (CheckListItem)list.getModel().getElementAt(index);
                item.selected = !item.selected;
                list.repaint(list.getCellBounds(index, index));
            }
        });
        selection = listDisplay.getSelectionModel();
        JScrollPane scrollArea = new JScrollPane(listDisplay);
        info = new JLabel();
        Border paddingBorder = BorderFactory.createEmptyBorder(STANDARD_SPACE/2, STANDARD_SPACE/2, STANDARD_SPACE/2, STANDARD_SPACE/2);
        Border border = BorderFactory.createLineBorder(Color.GRAY);
        info.setBorder(BorderFactory.createCompoundBorder(border, paddingBorder));
        info.setBackground(Color.WHITE);
        info.setOpaque(true);
        info.setVerticalAlignment(SwingConstants.TOP);
        JButton clean = new JButton(CLEAN);
        clean.addActionListener(new ActionListener()
                                {
                                    @Override
                                    public void actionPerformed(ActionEvent e)
                                    {
                                        clean();
                                    }
                                });
        JButton analyze = new JButton(ANALYZE);
        analyze.addActionListener(new ActionListener()
                                  {
                                      @Override
                                      public void actionPerformed(ActionEvent e)
                                      {
                                          analyze();
                                      }
                                  });
        layout.add(.03, .03, .45, .94, scrollArea);
        layout.add(.52, .03, .45, .8, info);
        layout.add(.52, .87, .175, .1, clean);
        layout.add(.735, .87, .235, .1, analyze);
        
        updateList();

        selection.addListSelectionListener(new ListSelectionListener()
        {
            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                int i = selection.getMinSelectionIndex();
                info.setText(i >= 0 ? Main.logs.get(i).getInfo() : "");
            }
        });

        frame.setVisible(true);
    }
    
    /**
     * Updates the list of logs, should be used after loading logs.
     */
    private void updateList()
    {
        selection.clearSelection();
        list.removeAllElements();
        for (LogInfo log : Main.logs) {
            list.addElement(new CheckListItem(log.toString(), log.isRealLog()));
        }
    }
    
    /**
     * Moves all logs which are not selected (unchecked) in the list to
     * another directory.
     */
    private void clean()
    {
        File droppedDir = new File(Main.PATH_DROPPED);
        if (!droppedDir.isDirectory()) {
            droppedDir.mkdir();
        }
        int i = 0;
        for (LogInfo log : Main.logs) {
            if (!list.getElementAt(i++).selected) {
                log.file.renameTo(new File(Main.PATH_DROPPED+"/"+log.file.getName()));
            }
        }
        Main.load();
        updateList();
    }
    
    /**
     * Analyze all logs which are selected (checked) in the list to create
     * the statistic output file.
     */
    private void analyze()
    {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Main.stats = fc.getSelectedFile();
        try {
            Main.stats.createNewFile();
            Main.writer = new FileWriter(Main.stats);
            Main.writer.write("datetime,action,team,player,blue,red\n");
        } catch (IOException e) {
            Log.error("Cannot create and open/write to file "+Main.stats);
            return;
        }
        int i = 0;
        for (LogInfo log : Main.logs) {
            if (list.getElementAt(i++).selected) {
                Parser.statistic(log);
            }
        }
        try{
            Main.writer.flush();
            Main.writer.close();
        } catch (IOException e) {
            Log.error("cannot close file "+Main.stats);
        }
        JOptionPane.showMessageDialog(null, "Done");
    }

    /**
     * Instances of this class represent a line in the list of logs with a
     * checkbox to select them.
     */
    private class CheckListItem
    {
        /* String shown to represent the log */
        public final String label;
        /* If this log is selected (the checkbox is true) */
        public boolean selected;

        /**
        * Creates a new CheckListItem.
        * 
        * @param label  A String representing the log behind this item.
        * @param selected   If true, this item will be selected (checkbox
        *                   true) from the beginning.
        */
        public CheckListItem(String label, boolean selected)
        {
            this.label = label;
            this.selected = selected;
        }
    }
    
    /**
     * This class is used to render a list item with checkbox.
     */
    private class CheckListRenderer extends JCheckBox implements ListCellRenderer<CheckListItem>
    {
        @Override
        public Component getListCellRendererComponent(JList list, CheckListItem value, int index, boolean isSelected, boolean hasFocus)
        {
            setEnabled(list.isEnabled());
            setSelected(value.selected);
            setFont(list.getFont());
            setBackground(isSelected ? LIST_HIGHLIGHT : list.getBackground());
            setForeground(list.getForeground());
            setText(value.label);
            return this;
        }
    }
}