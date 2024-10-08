package core;

import core.ui.GUI;
import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Main {

    private final static String IP = "";
    private final static int PORT = -1;

    public static void main(String[] args) {
        final JSONObject data = getData();
        if (data == null) {
            openError();
        } else {
            GUI gui = new GUI(data);
            gui.open();
        }
    }

    private static void openError() {
        JFrame frame = new JFrame("Error");
        frame.setLayout(null);
        frame.setSize(300,60);
        final JLabel label = new JLabel("Data null. Please start server.");
        frame.add(label);
        label.setBounds(50,0,200, 20);
        frame.setVisible(true);

        try {
            Thread.sleep(30_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    private static JSONObject getData() {

        try {
            Socket socket = new Socket(IP, PORT);
            socket.setSoTimeout(5_000);
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            String output = ";supersecretpassword69420ggwp";
            out.write(output);

            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String resp = in.readLine();
            socket.close();
            return new JSONObject(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
