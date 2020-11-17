package asu.shell.gui;

import asu.shell.sh.History;
import asu.shell.sh.RegisterCommand;
import asu.shell.sh.commands.Command;
import asu.shell.sh.util.EnvManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.undo.UndoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author suk
 */
public class GuiConsole extends JFrame {

    private static final long serialVersionUID = -7426652250914443368L;
    private static final Logger log = LoggerFactory.getLogger(GuiConsole.class);
    public static void main(String[] args) {
        GuiConsole c = new GuiConsole();
        c.setVisible(true);
    }

    /**
     * 撤销管理类
     */
    private UndoManager um = new UndoManager();
    private Runnable exitCall;

    private JMenuItem menuitemQuit;

    private JMenu fileMenu;

    private JMenuBar menubar;

    private JScrollPane jScrollPane0;

    private ConsoleTextArea consoleTextArea;

    private JTextField cmdline;

    private JPanel cmdPanel;

    private final BlockingQueue<String> cmd = new LinkedBlockingQueue<String>();

    private JMenuItem aboutMenuitem;

    private JMenu helpMenu;

    private JMenu toolMenu;

    private JMenuItem saveMenuitem;

    private JLabel jLabel;

    private JMenuItem reloadMenuitem;

    private JMenuItem historyMenuitem;
    private JMenuItem fullScreenMenuitem;

    private JMenu viewMenu;
    private JMenuItem clearMenuitem;

    private JMenu editMenu;
    PipedInputStream pis = new PipedInputStream();
    PipedOutputStream pos = new PipedOutputStream();
    InputStream       oldInputStream = System.in;
    private int historyNum = 0;
    private int historyCur = 0;
    private String[] historys;


    private volatile boolean fullScreenFlag =  false;
    public GuiConsole() {
        initComponents();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initPipe();
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.setIn(oldInputStream);
            try {
                pis.close();
            } catch (IOException ignored) {
            }
            try {
                pos.close();
            } catch (IOException ignored) {
            }
        }));
    }

    private void initPipe()
    {
        try {
            pos.connect(pis);
            System.setIn(pis);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //public String getCmdLine() {
    //    try {
    //        return this.cmd.take();
    //    } catch (InterruptedException e) {
    //        return null;
    //    }
    //}

    public void clear() {
        try {
            consoleTextArea.setText("");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public Runnable getExitCall() {
        return exitCall;
    }

    public void setExitCall(Runnable exitCall) {
        this.exitCall = exitCall;
    }

    // //////////////////////////////////////////////////////////////////
    /* UI */
    // //////////////////////////////////////////////////////////////////

    @Override
    public JMenuBar getJMenuBar() {
        if (menubar == null) {
            menubar = new JMenuBar();
            menubar.add(getFileMenu());
            menubar.add(getEditMenu());
            menubar.add(getViewMenu());
            menubar.add(getToolMenu());
            menubar.add(getHelpMenu());

        }
        return menubar;
    }

    private JTextField getCmdline() {
        if (cmdline == null) {
            cmdline = new JTextField();
            cmdline.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent event) {
                    cmdlineKeyKeyPressed(event);
                }
            });
        }
        return cmdline;
    }

    private JLabel getPromptLabel() {
        if (jLabel == null) {
            jLabel = new JLabel(EnvManager.getPS());
        }

        return jLabel;
    }

    private JPanel getCmdPanel() {
        if (cmdPanel == null) {
            cmdPanel = new JPanel();
            cmdPanel.setPreferredSize(new Dimension(800, 30));
            cmdPanel.setLayout(new BorderLayout());
            cmdPanel.add(getPromptLabel(), BorderLayout.WEST);
            cmdPanel.add(getCmdline(), BorderLayout.CENTER);
        }
        return cmdPanel;
    }

    private ConsoleTextArea getConsoleTextArea() {
        try {
            consoleTextArea = new ConsoleTextArea();
            consoleTextArea.setFont(java.awt.Font.decode("monospaced"));
            consoleTextArea.setColumns(120);
            consoleTextArea.setRows(30);
            consoleTextArea.setEditable(false);

            consoleTextArea.setText("(~.~) Asu's Simple Ultra Toolkits.\n");
        } catch (IOException e) {
            System.err.println("cannot create LoopedStreams" + e);
            System.exit(1);
        }
        return consoleTextArea;
    }

    private JMenu getFileMenu() {
        if (fileMenu == null) {
            fileMenu = new JMenu();
            fileMenu.setText("文件");
            fileMenu.add(getSaveMenuitem());
            fileMenu.add(getReloadMenuitem());
            fileMenu.add(getMenuitemQuit());
        }
        return fileMenu;
    }

    private JScrollPane getJScrollPane0() {
        if (jScrollPane0 == null) {
            jScrollPane0 = new JScrollPane();
        }
        jScrollPane0.setViewportView(this.getConsoleTextArea());
        return jScrollPane0;
    }

    private JMenuItem getMenuitemQuit() {
        if (menuitemQuit == null) {
            menuitemQuit = new JMenuItem();
            menuitemQuit.setText("退出");
            menuitemQuit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    menuitemExitMouseMouseAction(event);
                }
            });
        }
        return menuitemQuit;
    }

    private void initComponents() {
        GUITools.initLookAndFeel();
        setTitle("Asu's Simple Ultra Toolkits");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // Disables or enables decorations for this frame.
//        setUndecorated(false);
        add(getCmdPanel(), BorderLayout.SOUTH);
        add(getJScrollPane0(), BorderLayout.CENTER);
        setJMenuBar(getJMenuBar());
        GUITools.attachKeyListener(um, cmdline, consoleTextArea);
        // setSize(800, 600);
        pack();
        GUITools.center(this);

    }


    private JMenu getEditMenu() {
        if (editMenu == null) {
            editMenu = new JMenu();
            editMenu.setText("编辑");
            editMenu.add(getClearMenuitem());
        }
        return editMenu;
    }

    private JMenu getToolMenu() {
        if (toolMenu == null) {
            toolMenu = new JMenu();
            toolMenu.setText("工具");
            // toolMenu.add(getAutocodeMenu());
        }
        return toolMenu;
    }

    private JMenuItem getClearMenuitem() {
        if (clearMenuitem == null) {
            clearMenuitem = new JMenuItem();
            clearMenuitem.setText("清除");
            clearMenuitem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent event) {
                    clearMenuitemActionPerformed(event);
                }
            });
        }
        return clearMenuitem;
    }

    private JMenu getViewMenu() {
        if (viewMenu == null) {
            viewMenu = new JMenu();
            viewMenu.setText("视图");
            viewMenu.add(getHistoryMenuitem());
            viewMenu.add(getFullScreenMenuitem());
        }
        return viewMenu;
    }

    private JMenuItem getHistoryMenuitem() {
        if (historyMenuitem == null) {
            historyMenuitem = new JMenuItem();
            historyMenuitem.setText("历史");
            historyMenuitem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent event) {
                    historyMenuitemActionPerformed(event);
                }
            });
        }
        return historyMenuitem;
    }

    private JMenuItem getFullScreenMenuitem() {
        if (fullScreenMenuitem == null) {
            fullScreenMenuitem = new JMenuItem();
            fullScreenMenuitem.setText("全屏");
            fullScreenMenuitem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent event) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (fullScreenFlag) {
                                fullScreenFlag = false;
                                GUITools.fullscreen(GuiConsole.this, fullScreenFlag);
                            } else {
                                fullScreenFlag = true;
                                fullScreenMenuitem.setText("退出全屏");
                                GUITools.fullscreen(GuiConsole.this, fullScreenFlag);
                            }
                        }
                    });
                }
            });
        }
        return fullScreenMenuitem;
    }

    private JMenuItem getReloadMenuitem() {
        if (reloadMenuitem == null) {
            reloadMenuitem = new JMenuItem();
            reloadMenuitem.setText("重新加载");
            reloadMenuitem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent event) {
                    reloadMenuitemActionPerformed(event);
                }
            });
        }
        return reloadMenuitem;
    }

    private JMenuItem getSaveMenuitem() {
        if (saveMenuitem == null) {
            saveMenuitem = new JMenuItem();
            saveMenuitem.setText("保存输出");
            saveMenuitem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent event) {
                    saveActionPerformed(event);
                }
            });
        }
        return saveMenuitem;
    }

    private JMenu getHelpMenu() {
        if (helpMenu == null) {
            helpMenu = new JMenu();
            helpMenu.setText("帮助");
            helpMenu.add(getAboutMenuitem());
        }
        return helpMenu;
    }

    private JMenuItem getAboutMenuitem() {
        if (aboutMenuitem == null) {
            aboutMenuitem = new JMenuItem();
            aboutMenuitem.setText("关于");
            aboutMenuitem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent event) {
                    aboutMenuitemActionPerformed(event);
                }
            });
        }
        return aboutMenuitem;
    }

    ////////////////////////////////////////////////////////////////////
	// actions                                                        //
    ////////////////////////////////////////////////////////////////////

    /** the action of save script */
    private void saveActionPerformed(ActionEvent e) {
        try {
            // save file dialog
            JFileChooser saveChooser = new JFileChooser();
            int result = saveChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                saveChooser.addChoosableFileFilter(null);
                File file = saveChooser.getSelectedFile();

                // get the content from the scriptText which input with someone
                String data = this.consoleTextArea.getText();
                Files.write(file.toPath(), data.getBytes());
                //FileUtils.writeStringToFile(file, data, System.getProperty("file.encoding"));
                JOptionPane.showMessageDialog(this, "save the output text!");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void cmdlineKeyKeyPressed(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                String text = this.cmdline.getText();
                //if (StringUtils.isEmpty(text)) {
                //    return;
                //}
                //this.cmd.put(text);
                pos.write(text.getBytes());
                pos.write(KeyEvent.VK_ENTER);
                this.cmdline.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }
            historyCur = -1;
        } else if (event.getKeyCode() == KeyEvent.VK_UP) {
            historys = History.getInstance().getHistArray();
            historyNum = historys.length;
            if (historyNum == 0) {
                return;
            }
            if (historyCur == -1) {
                historyCur = historyNum;
            }
            historyCur--;
            if (historyCur < 0) {
                historyCur = historyNum - 1;
            }
            String cmd = historys[historyCur];
            this.cmdline.setText(cmd);
        } else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
            historyNum = historys.length;
            if (historyNum == 0) {
                return;
            }
            if (historyCur == -1) {
                historyCur = historyNum;
            }
            historyCur++;
            historyCur = historyCur % historyNum;
            String cmd = historys[historyCur];
            this.cmdline.setText(cmd);
        }

    }

    private void menuitemExitMouseMouseAction(ActionEvent event) {
        if (exitCall != null) {
            exitCall.run();
        }
        this.setVisible(false);
        System.exit(0);
    }

    private void aboutMenuitemActionPerformed(ActionEvent event) {
        JOptionPane.showMessageDialog(null, "Asu's Simple Ultra Toolkits");
    }

    private void reloadMenuitemActionPerformed(ActionEvent event) {
        Command cmd = RegisterCommand.getCommand("reload");
        try {
            cmd.executeBg();
            JOptionPane.showMessageDialog(this, "重新加载成功。");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "重新加载失败。" + e.getMessage(), "失败",
                                          JOptionPane.ERROR_MESSAGE);
        }
    }

    private void historyMenuitemActionPerformed(ActionEvent event) {
        Command cmd = RegisterCommand.getCommand("history");
        try {
            cmd.execute();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "执行失败。" + e.getMessage(), "失败",
                                          JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearMenuitemActionPerformed(ActionEvent event) {
        clear();
    }

}
