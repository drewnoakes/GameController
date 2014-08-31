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
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * This window is shown to select log-files for analyzing.
 *
 * @author Michel Bartsch
 */
@SuppressWarnings("unchecked")
public class GUI implements ListSelectionListener
{
    /* Some attributes for the layout and appearance, feel free to change
     * them and look what happens. */
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
    
    /* This gui´s components. */
    private final JFrame frame;
    private final DefaultListModel list;
    private final JList listDisplay;
    private final ListSelectionModel selection;
    private final JScrollPane scrollArea;
    private final JLabel info;
    private final JButton clean;
    private final JButton analyze;

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
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        TotalScaleLayout layout = new TotalScaleLayout(frame);
        frame.setLayout(layout);
        
        list = new DefaultListModel();
        listDisplay = new JList(list);
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
                        JList list = (JList) event.getSource();
                        int index = list.locationToIndex(event.getPoint());
                        CheckListItem item = (CheckListItem)list.getModel().getElementAt(index);
                        item.selected = !item.selected;
                        list.repaint(list.getCellBounds(index, index));
                    }
                });
        selection = listDisplay.getSelectionModel();
        scrollArea = new JScrollPane(listDisplay);
        info = new JLabel();
        Border paddingBorder = BorderFactory.createEmptyBorder(STANDARD_SPACE/2, STANDARD_SPACE/2, STANDARD_SPACE/2, STANDARD_SPACE/2);
        Border border = BorderFactory.createLineBorder(Color.GRAY);
        info.setBorder(BorderFactory.createCompoundBorder(border, paddingBorder));
        info.setBackground(Color.WHITE);
        info.setOpaque(true);
        info.setVerticalAlignment(SwingConstants.TOP);
        clean = new JButton(CLEAN);
        clean.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        clean();
                    }
                }
        );
        analyze = new JButton(ANALYZE);
        analyze.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        analyze();
                    }
                }
        );
        layout.add(.03, .03, .45, .94, scrollArea);
        layout.add(.52, .03, .45, .8, info);
        layout.add(.52, .87, .175, .1, clean);
        layout.add(.735, .87, .235, .1, analyze);
        
        updateList();
        selection.addListSelectionListener(this);

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
            list.addElement(new CheckListItem(log+"", log.isRealLog()));
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
            if (!((CheckListItem)list.getElementAt(i++)).selected) {
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
            if (((CheckListItem)list.getElementAt(i++)).selected) {
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

    @Override
    public void valueChanged(ListSelectionEvent e)
    {
        int i = selection.getMinSelectionIndex();
        if (i >= 0) {
            info.setText(Main.logs.get(i).getInfo());
        } else {
            info.setText("");
        }
    }
    
    /**
     * Instances of this class represent a line in the list of logs with a
     * checkbox to select them.
     */
    class CheckListItem
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
    private class CheckListRenderer extends JCheckBox implements ListCellRenderer
    {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus)
        {
            setEnabled(list.isEnabled());
            setSelected(((CheckListItem)value).selected);
            setFont(list.getFont());
            if (!isSelected) {
                setBackground(list.getBackground());
            } else {
                setBackground(LIST_HIGHLIGHT);
            }
            setForeground(list.getForeground());
            setText(((CheckListItem)value).label);
            return this;
        }
    }
}