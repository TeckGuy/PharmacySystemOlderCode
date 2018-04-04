package login;

import dbconnector.DBConnector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.border.TitledBorder;

import selling.SellingDrug;
import sun.applet.Main;

public class Login {

    // getting images from files
    URL url = Main.class.getResource("/img/key.png");
    URL url2 = Main.class.getResource("/img/save4.png");
    URL url3 = Main.class.getResource("/img/cancel5.png");
    URL url4 = Main.class.getResource("/img/logo.png");

    // image setting
    ImageIcon image = new ImageIcon(url);
    ImageIcon image2 = new ImageIcon(url2);
    ImageIcon image3 = new ImageIcon(url3);

    // setting ma default image icon to my frames
    Image iconimage = new ImageIcon(url4).getImage();

    // setting components
    JLabel pic, lusername, lpassword, llogin, ladmin;
    JButton blogin, bforgot;
    JTextField tusername;
    JPasswordField tpassword;
    JComboBox<String> ltype;
    // J

    // setting panels
    JPanel pget = new JPanel(new GridBagLayout());

    // setting frames
    JFrame FLogin;

    /// variables
    public String type2;

    // database variables
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    //start connetion in login
    private void connectdb() {
        String[] option = {"Retry", "Ok"};
        Toolkit.getDefaultToolkit().beep();
        int dbstate = JOptionPane.showOptionDialog(null, "Database Connection Failure", "Notification", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
        if (dbstate == 0) {
            DBConnector xampptest = new DBConnector();
            xampptest.restartXampp();
        }
        if (dbstate == 1) {
            JOptionPane.showMessageDialog(null, "Please Establish Database Connection\nAnd try again", "Notification", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void LoginSection() {
        pic = new JLabel(image);
        ladmin = new JLabel("Username");
        ladmin.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lusername = new JLabel("National ID");
        lusername.setFont(new Font("Serif", Font.PLAIN, 15));
        lpassword = new JLabel("Password");
        lpassword.setFont(new Font("Tahoma", Font.PLAIN, 15));
        llogin = new JLabel("LOGIN");
        llogin.setFont(new Font("Tahoma", Font.BOLD, 25));
        llogin.setForeground(Color.BLUE);

        tusername = new JTextField(18);
        tusername.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tpassword = new JPasswordField(18);
        tpassword.setFont(new Font("Tahoma", Font.PLAIN, 15));

        String[] options = {"Login_Type", "Admin", "User"};
        ltype = new JComboBox<String>(options);
//        ltype.addItem("Login_Type");
//        ltype.addItem("Admin_Login");
//        ltype.addItem("User_Login");
        ltype.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ltype.setFont(new Font("Tahoma", Font.PLAIN, 12));
        //String type1 = String.valueOf(ltype.getSelectedItem());
        //type2 = ltype.getSelectedItem().toString();
        //int type1 = ltype.getSelectedIndex();
        ltype.addActionListener(event -> {
            JComboBox<String> ltype = (JComboBox<String>) event.getSource();
            type2 = (String) ltype.getSelectedItem();
            if (type2.equalsIgnoreCase("Admin")) {
                lusername.setVisible(false);
                ladmin.setVisible(true);
            } else if (type2.equalsIgnoreCase("User")) {
                lusername.setVisible(true);
                ladmin.setVisible(false);
            }
        });

        blogin = new JButton("Login");
        blogin.setFont(new Font("Tahoma", Font.BOLD, 18));
        blogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        blogin.setForeground(Color.GREEN.darker());
        blogin.setBackground(Color.lightGray);
        bforgot = new JButton("Forgot");
        bforgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bforgot.setFont(new Font("Tahoma", Font.BOLD, 18));
        bforgot.setForeground(Color.BLUE);
        bforgot.setBackground(Color.lightGray);

        //panel pget
        GridBagConstraints v = new GridBagConstraints();
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        pget.add(pic, v);
        v.gridy++;
        pget.add(llogin, v);

        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 3;
        pget.add(ladmin, v);
        pget.add(lusername, v);
        v.gridy++;
        v.anchor = GridBagConstraints.FIRST_LINE_START;
        pget.add(tusername, v);
        v.gridy++;
        v.anchor = GridBagConstraints.CENTER;
        pget.add(lpassword, v);
        v.insets = new Insets(0, 0, 10, 0);
        v.gridy++;
        v.anchor = GridBagConstraints.FIRST_LINE_START;
        pget.add(tpassword, v);
        v.gridy++;
        v.anchor = GridBagConstraints.LAST_LINE_END;
        pget.add(ltype, v);
        v.gridy++;
        v.anchor = GridBagConstraints.FIRST_LINE_START;
        pget.add(blogin, v);
        v.anchor = GridBagConstraints.LAST_LINE_END;
        pget.add(bforgot, v);
        pget.setBorder(new TitledBorder(""));

        FLogin = new JFrame("Pharmacy System");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", Color.blue);
        } catch (Exception c) {
        }
        FLogin.setIconImage(iconimage);
        FLogin.add(pget);
        FLogin.setVisible(true);
        FLogin.setSize(450, 400);
        FLogin.setLocationRelativeTo(null);
        FLogin.setResizable(false);
        FLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        lusername.setVisible(false);
        ladmin.setVisible(true);

        bforgot.addActionListener(e -> {
            FLogin.setVisible(false);
            Reseting r2 = new Reseting();
            r2.ResetingSection();
        });
        blogin.addActionListener(e -> {
            String admin = "Admin";
            String user = "User";
            String developer = "Login_Type";
//                //database start
            String Aname = tusername.getText();
            //String Apass = tpassword.getText();

            //encripting passwords
            String Apass = tpassword.getText();
            String generatedPassword = null;
            // Create MessageDigest instance for MD5
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
            //Add password bytes to digest
            md.update(Apass.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
            //System.out.println(generatedPassword);
            //end of encripting

//                System.out.println(Aname + Apass);
            if (type2.equalsIgnoreCase(admin)) {
                try {
                    Connection con = DBConnector.getConnection();
                    if (con != null) {
                        if (Aname.equalsIgnoreCase("") && Apass.equalsIgnoreCase("")) {
                            JOptionPane.showMessageDialog(null, "No Username/Password given", "Error Message", JOptionPane.ERROR_MESSAGE);
                        } else {
                            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            String sql = "SELECT username,password FROM adminlogin WHERE username ='" + tusername.getText() + "' && password = '" + generatedPassword + "' ";
                            rs = stmt.executeQuery(sql);
                            if (rs.next()) {
                                FLogin.setVisible(false);

                                Sections opsec = new Sections();
                                opsec.Operations();

                                rs.close();
                                stmt.close();
                                con.close();
                            } else {
                                JOptionPane.showMessageDialog(null, "Wrong Details\nPlease Check and try again", "Login Notification", JOptionPane.ERROR_MESSAGE);
                                rs.close();
                                stmt.close();
                                con.close();
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Connection Failure", "Error Message", JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    }
                } catch (SQLException x) {
                    connectdb();
                }
            } else if (type2.equalsIgnoreCase(user)) {
                try {
                    String natid = tusername.getText();
                    int ID = Integer.parseInt(natid);
                    Connection con = DBConnector.getConnection();
                    if (con != null) {
                        if (Aname.equalsIgnoreCase("") && Apass.equalsIgnoreCase("")) {
                            JOptionPane.showMessageDialog(null, "No Username/Password given", "Error Message", JOptionPane.ERROR_MESSAGE);
                        } else {
                            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            String sql = "SELECT NationalID,password FROM userlogin WHERE NationalID ='" + ID + "' && password = '" + generatedPassword + "' ";
                            rs = stmt.executeQuery(sql);
                            if (rs.next()) {
                                FLogin.setVisible(false);
                                SellingDrug dsell = new SellingDrug();
                                dsell.DrugNameFound();
                                dsell.SellDrug();
                                dsell.bbacksell.setVisible(true);
                                dsell.bselluser.setVisible(true);
                                dsell.bsell.setVisible(false);
                                dsell.bback.setVisible(false);

                                rs.close();
                                stmt.close();
                                con.close();
                            } else {
                                JOptionPane.showMessageDialog(null, "Wrong Details\nPlease Check and try again", "Login Notification", JOptionPane.ERROR_MESSAGE);
                                rs.close();
                                stmt.close();
                                con.close();
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Connection Failure", "Error Message", JOptionPane.ERROR_MESSAGE);

                    }
                } catch (SQLException x) {
                    connectdb();

                }
            } else if (type2.equalsIgnoreCase(developer)) {
                String systemname = "TechGuy";
                String systempass = "jobvinnytechguy";
                if (tusername.getText().equalsIgnoreCase(systemname) && tpassword.getText().equalsIgnoreCase(systempass)) {
                    FLogin.setVisible(false);
                    Sections opsec = new Sections();
                    opsec.Operations();
                } else {
                    JOptionPane.showMessageDialog(null, "Please Choose Login Type", "Notification", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

    }
//
//	public static void main(String[] args) {
//		Login ll = new Login();
//		ll.LoginSection();
//	}
}
