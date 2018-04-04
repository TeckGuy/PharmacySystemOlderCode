package login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import javax.swing.border.TitledBorder;

import dbconnector.DBConnector;
import sun.applet.Main;

public class Reseting {

    //getting images from files
    URL url = Main.class.getResource("/img/lock.png");
    URL url2 = Main.class.getResource("/img/save4.png");
    URL url3 = Main.class.getResource("/img/cancel5.png");
    URL url4 = Main.class.getResource("/img/logo.png");

    //image setting
    ImageIcon image = new ImageIcon(url);
    ImageIcon image2 = new ImageIcon(url2);
    ImageIcon image3 = new ImageIcon(url3);

    //setting ma default image icon to my frames
    Image iconimage = new ImageIcon(url4).getImage();

    //set components
    JLabel lid, lpnumber, lpass, lconfirm, lpic, linfo;
    JTextField tid, tpnumber;
    JPasswordField ppass, pconfirm;
    JButton breset, bcancel;
    private JComboBox ltype2;

    //panels
    JPanel pMain = new JPanel(new GridBagLayout());

    //frames
    JFrame FReset;

    //ariables
    String type3;

    // database variables
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    PreparedStatement prs = null;

    public void ResetingSection() {
        lpic = new JLabel(image);
        linfo = new JLabel("Reset Password");
        linfo.setFont(new Font("Tahoma", Font.BOLD, 15));
        linfo.setForeground(Color.BLUE);
        lid = new JLabel("National ID");
        lid.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lpnumber = new JLabel("Personal Number/PIN");
        lpnumber.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lpass = new JLabel("Password");
        lpass.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lconfirm = new JLabel("Confirm Password");
        lconfirm.setFont(new Font("Tahoma", Font.PLAIN, 15));

        tid = new JTextField(20);
        tid.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tpnumber = new JTextField(20);
        tpnumber.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ppass = new JPasswordField(20);
        ppass.setFont(new Font("Tahoma", Font.PLAIN, 15));
        pconfirm = new JPasswordField(20);
        pconfirm.setFont(new Font("Tahoma", Font.PLAIN, 15));

        String[] resetype = {"Reset Type", "Admin Reset", "User Reset"};
        ltype2 = new JComboBox(resetype);
//        ltype2.addItem("Admin Reset");
//        ltype2.addItem("User Reset");
        ltype2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ltype2.setFont(new Font("Tahoma", Font.PLAIN, 12));

        ltype2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                JComboBox<String> largment = (JComboBox<String>) event.getSource();
                type3 = (String) largment.getSelectedItem();
                if (type3.equalsIgnoreCase("Admin Reset")) {
                    //JOptionPane.showMessageDialog(null, type3, "", JOptionPane.INFORMATION_MESSAGE);
                } else if (type3.equalsIgnoreCase("User Reset")) {
                    //JOptionPane.showMessageDialog(null, type3, "", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        breset = new JButton(image2);
        breset.setBackground(Color.GREEN);
        breset.setToolTipText("Reset Password");
        breset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bcancel = new JButton(image3);
        bcancel.setBackground(Color.red);
        bcancel.setToolTipText("Cancel/Back");
        bcancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        //adding components to panels pMain
        GridBagConstraints v = new GridBagConstraints();
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 5, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        pMain.add(lpic, v);
        v.gridy++;
        pMain.add(linfo, v);

        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 3;
        pMain.add(lid, v);
        v.gridy++;
        v.anchor = GridBagConstraints.FIRST_LINE_START;
        pMain.add(tid, v);
        v.gridy++;
        v.anchor = GridBagConstraints.CENTER;
        pMain.add(lpnumber, v);
        v.gridy++;
        v.anchor = GridBagConstraints.FIRST_LINE_START;
        pMain.add(tpnumber, v);
        v.gridy++;
        v.anchor = GridBagConstraints.CENTER;
        pMain.add(lpass, v);
        v.gridy++;
        v.anchor = GridBagConstraints.FIRST_LINE_START;
        pMain.add(ppass, v);
        v.gridy++;
        v.anchor = GridBagConstraints.CENTER;
        pMain.add(lconfirm, v);
        v.insets = new Insets(0, 0, 10, 0);
        v.gridy++;
        v.anchor = GridBagConstraints.FIRST_LINE_START;
        pMain.add(pconfirm, v);
        v.gridy++;
        v.anchor = GridBagConstraints.LAST_LINE_END;
        pMain.add(ltype2, v);
        v.gridy++;
        v.anchor = GridBagConstraints.FIRST_LINE_START;
        pMain.add(breset, v);
        v.anchor = GridBagConstraints.LAST_LINE_END;
        pMain.add(bcancel, v);
        pMain.setBorder(new TitledBorder(""));

        FReset = new JFrame("Pharmacy System");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", Color.blue);
        } catch (Exception c) {
        }
        FReset.setIconImage(iconimage);
        FReset.add(pMain);
        FReset.setVisible(true);
        FReset.setSize(500, 500);
        FReset.setLocationRelativeTo(null);
        FReset.setResizable(false);
        FReset.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        bcancel.addActionListener(e -> {
            FReset.setVisible(false);
            Login r2 = new Login();
            r2.LoginSection();
        });
        breset.addActionListener(e -> {
            String nid = tid.getText();
            String pnum = tpnumber.getText();
            //String pass1 = ppass.getText();
            String pass2 = pconfirm.getText();

            //encripting passwords
            String pass1 = ppass.getText();
            String generatedPassword = null;
            // Create MessageDigest instance for MD5
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
            //Add password bytes to digest
            md.update(pass1.getBytes());
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
            String natid = tid.getText();
            int ID = Integer.parseInt(natid);
            String admintype = "Admin Reset";
            String usertype = "User Reset";
            if (type3.equalsIgnoreCase(admintype)) {
                try {
                    Connection con = DBConnector.getConnection();
                    if (con != null) {
                        if (nid.equalsIgnoreCase("") && pnum.equalsIgnoreCase("") && pass1.equalsIgnoreCase("") && pass2.equalsIgnoreCase("")) {
                            JOptionPane.showMessageDialog(null, "Please Fill In The Fields", "Error Message", JOptionPane.ERROR_MESSAGE);
                        } else {
                            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            String sql = "SELECT NationalID,personalnumber FROM adminlogin WHERE NationalID ='" + ID + "' &&  personalnumber = '" + tpnumber.getText() + "' ";
                            rs = stmt.executeQuery(sql);
                            if (rs.next()) {
                                if (ppass.getText().equalsIgnoreCase(pconfirm.getText())) {
                                    String sqlupdate = "UPDATE adminlogin set password = '" + generatedPassword + "' WHERE NationalID ='" + ID + "'";
                                    prs = con.prepareStatement(sqlupdate);
                                    prs.execute();
                                    if (prs != null) {
                                        JOptionPane.showMessageDialog(null, "Password Updated Successfully", "Notification", JOptionPane.INFORMATION_MESSAGE);
                                        FReset.setVisible(false);
                                        Login r2 = new Login();
                                        r2.LoginSection();
                                        rs.close();
                                        stmt.close();
                                        con.close();
                                        prs.close();
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Password Not Updated", "Error Message", JOptionPane.ERROR_MESSAGE);
                                        rs.close();
                                        stmt.close();
                                        con.close();
                                        prs.close();
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(null, "Password don't Match\nRecheck and try again", "Password Validation", JOptionPane.INFORMATION_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Wrong National ID or Personal_Number/PIN\nPlease recheck and try again", "Reset Notification", JOptionPane.INFORMATION_MESSAGE);
                                rs.close();
                                stmt.close();
                                con.close();
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Connection Failure", "Error Message", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException x) {
                    JOptionPane.showMessageDialog(null, "Connection Failure" + "\n" + x, "Error Message", JOptionPane.ERROR_MESSAGE);
                }
            } else if (type3.equalsIgnoreCase(usertype)) {
                try {
                    Connection con = DBConnector.getConnection();
                    if (con != null) {
                        if (nid.equalsIgnoreCase("") && pnum.equalsIgnoreCase("") && pass1.equalsIgnoreCase("") && pass2.equalsIgnoreCase("")) {
                            JOptionPane.showMessageDialog(null, "Please Fill In The Fields", "Error Message", JOptionPane.ERROR_MESSAGE);
                        } else {
                            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            String sql = "SELECT NationalID,personalnumber FROM userlogin WHERE NationalID ='" + ID + "' &&  personalnumber = '" + tpnumber.getText() + "' ";
                            rs = stmt.executeQuery(sql);
                            if (rs.next()) {
                                if (ppass.getText().equalsIgnoreCase(pconfirm.getText())) {
                                    String sqlupdate = "UPDATE userlogin set password = '" + generatedPassword + "' WHERE NationalID ='" + ID + "'";
                                    prs = con.prepareStatement(sqlupdate);
                                    prs.execute();
                                    if (prs != null) {
                                        JOptionPane.showMessageDialog(null, "Password Updated Successfully", "Notification", JOptionPane.INFORMATION_MESSAGE);
                                        FReset.setVisible(false);
                                        Login r2 = new Login();
                                        r2.LoginSection();
                                        rs.close();
                                        stmt.close();
                                        con.close();
                                        prs.close();
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Password Not Updated", "Error Message", JOptionPane.ERROR_MESSAGE);
                                        rs.close();
                                        stmt.close();
                                        con.close();
                                        prs.close();
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(null, "Password don't Match\nRecheck and try again", "Password Validation", JOptionPane.INFORMATION_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Wrong National ID or Personal_Number/PIN\nPlease recheck and try again", "Reset Notification", JOptionPane.INFORMATION_MESSAGE);
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
                    JOptionPane.showMessageDialog(null, "Connection Failure" + "\n" + x, "Error Message", JOptionPane.ERROR_MESSAGE);
                    //System.exit(0);
                }
            }
        });
    }

//    public static void main(String[] args) {
//        Reseting r = new Reseting();
//        r.ResetingSection();
//    }
}
