package analysis;

import login.Sections;
import sun.applet.Main;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class AnalysisType {

    //getting images
    URL url1 = Main.class.getResource("/img/logo.png");
    URL url = Main.class.getResource("/img/analysis.png");
    URL url2 = Main.class.getResource("/img/stock.png");
    URL url3 = Main.class.getResource("/img/payments.png");
    URL url4 = Main.class.getResource("/img/view1.png");

    //setting in imageicon
    ImageIcon imageanalysis = new ImageIcon(url);
    ImageIcon imagestock = new ImageIcon(url2);
    ImageIcon imagepayments = new ImageIcon(url3);
    ImageIcon imagerecords = new ImageIcon(url4);

    //setting ma default image icon to my frames
    final ImageIcon iconstock = new ImageIcon(url2);
    Image iconimage = new ImageIcon(url1).getImage();

    //setting labels
    JLabel llogo, llogo2, ltitle, larchives, lstock, lsupply;
    JButton bpayments, bstock, bdistributers, bback;

    //panels
    JPanel panelanalysis = new JPanel(new GridBagLayout());

    //frame
    JFrame Fanalysis = new JFrame();

    public void ChooseSection() {
        llogo = new JLabel(imageanalysis);
        llogo2 = new JLabel(imageanalysis);
        ltitle = new JLabel("ANALYSIS   SECTION");
        ltitle.setFont(new Font("Tahoma", Font.BOLD, 20));
        ltitle.setForeground(Color.BLACK);

        larchives = new JLabel("Archives");
        larchives.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lstock = new JLabel("Analysis");
        lstock.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lsupply = new JLabel("Supplier Payment");
        lsupply.setFont(new Font("Tahoma", Font.PLAIN, 13));

        bdistributers = new JButton(imagepayments);
        bdistributers.setToolTipText("Analyze Supplies/Payments");
        bdistributers.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bpayments = new JButton(imagerecords);
        bpayments.setToolTipText("Analyze Payment Records");
        bpayments.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bstock = new JButton(imagestock);
        bstock.setToolTipText("Analyze the Stock/Cash Flow");
        bstock.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bback = new JButton("BACK");
        bback.setFont(new Font("Tahoma", Font.BOLD, 15));
        bback.setBackground(Color.LIGHT_GRAY);
        bback.setCursor(new Cursor(Cursor.HAND_CURSOR));

        //adding components to panel
        GridBagConstraints v = new GridBagConstraints();
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelanalysis.add(llogo, v);
        v.gridy++;
        panelanalysis.add(ltitle, v);

        v.anchor = GridBagConstraints.WEST;
        v.insets = new Insets(30, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 3;
        v.gridx = 0;
        panelanalysis.add(bstock, v);
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        panelanalysis.add(lstock, v);

        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(30, 200, 0, 200);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 3;
        v.gridx = 0;
        panelanalysis.add(bdistributers, v);
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        panelanalysis.add(lsupply, v);

        v.anchor = GridBagConstraints.EAST;
        v.insets = new Insets(30, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 3;
        v.gridx = 0;
        panelanalysis.add(bpayments, v);
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        panelanalysis.add(larchives, v);

        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(30, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 5;
        v.gridx = 0;
        panelanalysis.add(llogo2, v);
        v.gridy++;
        panelanalysis.add(bback, v);

        //frame
        Fanalysis = new JFrame("Pharmacy System");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", Color.blue);
        } catch (Exception ignored) {
        }
        Fanalysis.setUndecorated(true);
        Fanalysis.setIconImage(iconimage);
        Fanalysis.add(panelanalysis);
        Fanalysis.setVisible(true);
        Fanalysis.setSize(600, 400);
        Fanalysis.revalidate();
        Fanalysis.setLocationRelativeTo(null);
        Fanalysis.setResizable(false);
        Fanalysis.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //actions
        bstock.addActionListener(e -> {
            Fanalysis.setVisible(false);
            StockAnalysis sa = new StockAnalysis();
            //JOptionPane.showMessageDialog(null, "Updating the Stock Please Wait", "Stock Analysis Notification", JOptionPane.INFORMATION_MESSAGE, iconstock);
            //sa.analyzingdata();
            sa.CriticalAnalysis();
            // JOptionPane.showMessageDialog(null, "Stock Successifully Updated", "Stock Analysis Notification", JOptionPane.INFORMATION_MESSAGE, iconstock);
            //JOptionPane.showMessageDialog(null, "Please Start Analysis Of Stock" + "\n" + "To have Updated Stock", "Stock Analysis Notification", JOptionPane.INFORMATION_MESSAGE, iconstock);

        });
        bpayments.addActionListener(e -> {
            Fanalysis.setVisible(false);
            Archives archive = new Archives();
            archive.Archives();

        });
        bdistributers.addActionListener(e -> {
            Fanalysis.setVisible(false);
            SuppliersAnalysis sup = new SuppliersAnalysis();
            sup.Suppliers();

        });
        bback.addActionListener(e -> {
            Fanalysis.setVisible(false);
            Sections sec = new Sections();
            sec.Operations();
        });
    }

}
