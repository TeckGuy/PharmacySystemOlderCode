package login;

import dbconnector.DBConnector;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

import sun.applet.Main;

public class Welcome {

    //getting images
    URL url4 = Main.class.getResource("/img/tech.jpg");
    URL url1 = Main.class.getResource("/img/connection.png");
    URL url5 = Main.class.getResource("/img/logo.png");

    //setting images
    ImageIcon image4 = new ImageIcon(url4);

    //setting ma default image icon to my frames
    final ImageIcon icon2 = new ImageIcon(url1);
    Image iconimage = new ImageIcon(url5).getImage();

    //getting components;
    JProgressBar current;
    JLabel notify, notify2, notify3, notify4, splashimg, slogan, pwby;

    //setting panels
    JPanel panelbar = new JPanel(new GridBagLayout());

    //setting Frame
    JFrame Floadingbar;
    int num = 0;

    //start xampp application
    DBConnector startdb = new DBConnector();

    //check on starting status of xampp
    private void stateXampp() {
        try {
            Connection con = DBConnector.getConnection();
        } catch (SQLException ee) {
            startdb.startXampp();
            try {
                Connection con = DBConnector.getConnection();
            } catch (SQLException eev) {
                startdb.startXampp();
                try {
                    Connection con = DBConnector.getConnection();
                } catch (SQLException eevv) {
                    startdb.startXampp();
                }
            }
        }
    }

    //try connetion
    private void trycon() {
        notify4.setForeground(Color.red.darker());
        Toolkit.getDefaultToolkit().beep();
        String[] option = {"Retry", "Exit"};
        int dbstate = JOptionPane.showOptionDialog(null, "Connection Failure\nRetry Starting The Database Application", "Database Connection Notification", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
        if (dbstate == 0) {
            //start xampp
            startdb.startXampp();
            current.setForeground(Color.BLUE);
        }
        if (dbstate == 1) {
            System.exit(0);
        }
    }

    private void LoadingBar() {
        notify = new JLabel("Loading System Modules...");
        notify.setFont(new Font("Tahoma", Font.BOLD + Font.ITALIC, 12));
        notify.setForeground(Color.BLUE);
        notify2 = new JLabel("Establishing Database Connection...");
        notify2.setFont(new Font("Tahoma", Font.BOLD + Font.ITALIC, 12));
        notify2.setForeground(Color.BLUE);
        notify3 = new JLabel("Connection Established...");
        notify3.setFont(new Font("Tahoma", Font.BOLD + Font.ITALIC, 12));
        notify3.setForeground(Color.BLUE);
        notify4 = new JLabel("Connection Failure...");
        notify4.setFont(new Font("Tahoma", Font.BOLD + Font.ITALIC, 12));
        notify4.setForeground(Color.BLUE);
        slogan = new JLabel("Pharmacy Management System");
        slogan.setFont(new Font("Tahoma", Font.PLAIN, 13));
        slogan.setForeground(Color.BLACK);
        pwby = new JLabel("Powered By TecksolKE");
        pwby.setFont(new Font("Tahoma", Font.BOLD, 10));
        pwby.setForeground(Color.BLACK);
        splashimg = new JLabel(image4);

        //progressbar
        current = new JProgressBar(0, 2000);
        current.setBorder(null);
        current.setBackground(Color.lightGray);
        current.setForeground(Color.BLUE);
        current.setValue(0);
        current.setPreferredSize(new Dimension(500, 5));
        current.setStringPainted(false);
        //current.setForeground(Color.blue.darker());

        //adding components to panels pMain
        GridBagConstraints v = new GridBagConstraints();
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelbar.add(splashimg, v);
//        v.insets = new Insets(10, 0, 0, 0);
//        v.gridy++;
//        panelbar.add(notify, v);
//        panelbar.add(notify2, v);
//        panelbar.add(notify3, v);
//        panelbar.add(notify4, v);
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        v.anchor = GridBagConstraints.SOUTH;
        panelbar.add(current, v);
        v.anchor = GridBagConstraints.EAST;
        v.insets = new Insets(12, 0, 0, 10);
        v.gridy++;
        panelbar.add(slogan, v);
        v.insets = new Insets(0, 0, 2, 10);
        v.gridy++;
        panelbar.add(pwby, v);
        panelbar.setBackground(Color.lightGray);

        while (num < 2000) {
            current.setValue(num);
            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
            }
            num += 95;
            //System.out.println(num);
            if (num == 95) {
                //start xampp
                startdb.startXampp();
                //frame code
                Floadingbar = new JFrame("Pharmacy System");
                try {
                    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                    UIManager.put("nimbusBase", Color.blue);
                } catch (Exception c) {
                }
                Floadingbar.setUndecorated(true);
                Floadingbar.setIconImage(iconimage);
                Floadingbar.add(panelbar);
                Floadingbar.setVisible(true);
                Floadingbar.setSize(500, 380);
                Floadingbar.setLocationRelativeTo(null);
                Floadingbar.revalidate();
                Floadingbar.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                Floadingbar.setBackground(Color.black);
                //end of frame code
            }
            if (num == 380) {
                try {
                    try {
                        Connection con = DBConnector.getConnection();
                    } catch (SQLException e) {
                        startdb.startXampp();
                        stateXampp();
                    }
                } catch (Exception trydb) {
                    current.setForeground(Color.red.darker());
                }
            }
            if (num == 950) {
                try {
                    stateXampp();
                } catch (Exception trydb) {
                    current.setForeground(Color.red.darker());
                }
            }
            if (num == 1425) {
                try {
                    try {
                        Connection con = DBConnector.getConnection();
                    } catch (SQLException e) {
                        startdb.startXampp();
                        try {
                            Connection con = DBConnector.getConnection();
                        } catch (SQLException ee) {
                            startdb.startXampp();
                            stateXampp();
                        }
                    }
                } catch (Exception trydb) {
                    current.setForeground(Color.red.darker());
                    trycon();
                }
            }
            if (num == 1615) {
//                notify.setVisible(false);
//                notify2.setVisible(false);
//                notify3.setVisible(true);
//                notify4.setVisible(false);
            }
            if (num == 2090) {
                try {
                    Connection con = DBConnector.getConnection();
                    if (con != null) {
                        Floadingbar.setVisible(false);
                        Login r2 = new Login();
                        r2.LoginSection();
                        con.close();
                    } else {
                        current.setForeground(Color.red.darker());
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null, "Connection Failed\nPlease Exit the System and Start\nThe Database Application Manually", "Database Notification", JOptionPane.ERROR_MESSAGE, icon2);
                        startdb.stopXampp();
                        System.exit(0);
                    }

                } catch (SQLException e) {
                    current.setForeground(Color.red.darker());
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null, "Connection Failed\nPlease Exit the System and Start\nThe Database Application Manually", "Database Notification", JOptionPane.ERROR_MESSAGE, icon2);
                    startdb.stopXampp();
                    System.exit(0);
                }
            }
        }

    }

//     public static void main(String[] args) {
//         Welcome wl = new Welcome();
//         wl.LoadingBar();
//     }
}
