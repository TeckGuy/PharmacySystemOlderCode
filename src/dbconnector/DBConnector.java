package dbconnector;

import sun.applet.Main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DBConnector extends Component {

    String path = null;

    URL urlbackup = Main.class.getResource("/BackUpFile/BackUp.bat");
    URL urlxampp = Main.class.getResource("/BackUpFile/Start.bat");
    URL url5 = Main.class.getResource("/img/logo.png");
    URL url13 = Main.class.getResource("/img/dbconnection.png");
    URL url1 = Main.class.getResource("/img/connection.png");
    URL urlbackuppic = Main.class.getResource("/img/backup.png");

    // final ImageIcon icon2 = new ImageIcon(url1);
    Image iconimage = new ImageIcon(url5).getImage();
    final ImageIcon iconcon = new ImageIcon(url13);
    final ImageIcon icon2 = new ImageIcon(url1);
    final ImageIcon iconbackup = new ImageIcon(urlbackuppic);

    private static final String dbname = "dbpharmacy";
    private static final String USERNAME = "TechGuy";
    private static final String PASSWORD = "jobvinny";
    private static final String CONN = "jdbc:mysql://127.0.0.1:3306/DBpharmacy";
    private static final String SQCONN = "jdbc:sqlite:dbpharmacy.sqlite";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(CONN, USERNAME, PASSWORD);
        //return DriverManager.getConnection(SQCONN);
    }

    //method for backup of data
    public void getBackup() {
        try {
            Connection connection = getConnection();
            if (connection != null) {
                JFileChooser fc = new JFileChooser();
                //fc.showOpenDialog(this);
                fc.showSaveDialog(this);
                fc.setDialogTitle("Select Path");
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                //do backup
                try {
                    File f = fc.getSelectedFile();
                    path = f.getAbsolutePath();
                    // path = path.replace('\\', '/');
                    path = "\"" + path + "_" + date + ".sql" + "\"";
                    //System.out.println(path);

                    //code fro saving backup to the path
                    try {
                        Process runtimeprocess = Runtime.getRuntime().exec("cmd.exe " + "/c" + " start /B " + "C:/xampp/mysql/bin/mysqldump.exe -u TechGuy -pjobvinny dbpharmacy > " + " " + path);
                        int processcomplete = runtimeprocess.waitFor();
                        if (processcomplete == 0) {
                            //System.out.println(processcomplete);
                            Toolkit.getDefaultToolkit().beep();
                            JOptionPane.showMessageDialog(null, "Backup created successfully", "Backup Notification", JOptionPane.INFORMATION_MESSAGE, iconbackup);

                        } else {
                            //System.out.println(processcomplete);
                            Toolkit.getDefaultToolkit().beep();
                            JOptionPane.showMessageDialog(null, "Could not create the backup", "Backup Notification", JOptionPane.ERROR_MESSAGE);

                        }
                    } catch (IOException | InterruptedException bv) {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null, "BackUp Error Occurred Please Try Again" + "\n" + bv, "Backup Notification", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (Exception e) {
                    // JOptionPane.showMessageDialog(null, "You Have Cancelled", "Backup Notification", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {

            }
        } catch (Exception bfailed) {
            String[] option = {"Retry", "Ok"};
            Toolkit.getDefaultToolkit().beep();
            int dbstate = JOptionPane.showOptionDialog(null, "Database Connection Failure\nBackup Can't Be done\n\nTry To Start Database Connection", "BackUp Notification", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
            if (dbstate == 0) {
                restartXampp();
            }
            if (dbstate == 1) {
                JOptionPane.showMessageDialog(null, "Please Establish Database Connection", "Notification", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    //start xampp
    public void startXampp() {
        try {
            Process runtimeprocess = Runtime.getRuntime().exec("cmd.exe " + "/c" + " start /B " + "C:/xampp/xampp_start.exe");
            int processcomplete = runtimeprocess.waitFor();
            if (processcomplete == 0) {
                //do nothing
            } else {
                stopXampp();
            }
        } catch (Exception bb) {
            JOptionPane.showMessageDialog(null, "Sorry Service Can't Be Started", "Database Connection", JOptionPane.INFORMATION_MESSAGE, icon2);
        }
    }

    //start xampp
    public void stopXampp() {
        try {
            Process runtimeprocess = Runtime.getRuntime().exec("cmd.exe " + "/c" + " start /B " + "C:/xampp/xampp_stop.exe");
            int processcomplete = runtimeprocess.waitFor();
            if (processcomplete == 0) {
                //System.out.println(processcomplete);
            } else {
                //System.out.println(processcomplete);
            }
        } catch (Exception bb) {
            JOptionPane.showMessageDialog(null, "Sorry Service Can't Be Started", "Database Connection", JOptionPane.INFORMATION_MESSAGE, icon2);
        }
    }

    //restart xampp
    public void restartXampp() {

        try {
            Process runtimeprocess = Runtime.getRuntime().exec("cmd.exe " + "/c" + " start /B " + "C:/xampp/xampp_start.exe");
            int processcomplete = runtimeprocess.waitFor();
            if (processcomplete == 0) {
                Connection connection = getConnection();
                if (connection != null) {
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null, "Database Connection Established", "Connection Establishment", JOptionPane.INFORMATION_MESSAGE, iconcon);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null, "Connection Failed\nPlease Exit the System and Start\nThe Database Application Manually", "Notification", JOptionPane.ERROR_MESSAGE, icon2);
                    stopXampp();
                    System.exit(0);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Failed", "Database Connection", JOptionPane.INFORMATION_MESSAGE, icon2);
            }
        } catch (Exception bb) {
            restartXampp();
            //JOptionPane.showMessageDialog(null, "Sorry Service Can't Be Started", "Database Connection", JOptionPane.INFORMATION_MESSAGE, icon2);
        }
    }

    //code for restarting connection icase of failure during operation
    public void getCon() {
        String[] option = {"Retry", "Ok"};
        Toolkit.getDefaultToolkit().beep();
        int dbstate = JOptionPane.showOptionDialog(null, "Database Connection Failure\nRetry Starting The Connection\n\nAnd Try Again", "Notification", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
        if (dbstate == 0) {
            restartXampp();
        }
        if (dbstate == 1) {
            JOptionPane.showMessageDialog(null, "Please Establish Database Connection", "Notification", JOptionPane.INFORMATION_MESSAGE);
        }
    }
//        try {
//            Runtime rt = Runtime.getRuntime();
//            rt.exec("cmd.exe " + "/c" + " start " + urlxampp);
//            System.exit(0);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
}
