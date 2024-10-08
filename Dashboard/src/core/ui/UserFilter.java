package core.ui;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;

public class UserFilter {

    private final JFrame frame = new JFrame("User Filter");

    private final JTextField idTextField = new JTextField();
    private final JTextField nameTextField = new JTextField();

    private final GUI gui;

    public UserFilter(GUI gui) {
        this.gui = gui;
        this.init();
    }

    public void open() {
        this.frame.setVisible(true);
    }

    public void close() {
        this.frame.setVisible(false);
        this.frame.dispose();
    }

    private void init() {
        this.frame.setSize(330, 130);
        this.frame.setResizable(false);
        this.frame.setLayout(null);

        final JLabel idLabel = new JLabel("Unique ID:");
        idLabel.setBounds(5,5,100,20);
        this.frame.add(idLabel);

        this.frame.add(this.idTextField);
        this.idTextField.setBounds(100,5,200,20);

        final JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(5,30,100,20);
        this.frame.add(nameLabel);

        this.frame.add(this.nameTextField);
        this.nameTextField.setBounds(100,30,200,20);

        final JButton filterButton = new JButton("Filter");
        filterButton.setBounds(0,70,330,20);
        this.frame.add(filterButton);
        filterButton.addActionListener(this::filter);

    }

    private void filter(ActionEvent e) {
        if (!this.idTextField.getText().equals("")) {
            final LinkedHashMap<String, JSONObject> map = this.gui.getUserCache().getUserByID(this.idTextField.getText());
            this.gui.repaintUserTable(this.gui.getTableGenerator().getFilteredUsersTable(map));
        } else if (!this.nameTextField.getText().equals("")) {
            final LinkedHashMap<String, JSONObject> map = this.gui.getUserCache().getUserByName(this.nameTextField.getText());
            this.gui.repaintUserTable(this.gui.getTableGenerator().getFilteredUsersTable(map));
        } else {
            this.gui.repaintUserTable(this.gui.getTableGenerator().getUserTable());
        }
        this.close();
    }
}
