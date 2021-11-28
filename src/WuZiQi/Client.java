package WuZiQi;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class Client extends Thread{
    public String m_host;
    public int m_port;
    // socket读端口
    InputStream is;
    // socket写端口
    BufferedWriter bw;
    // 棋盘
    Board m_board;
    // 网络情况显示
    JTextField m_textfield;

    public Client(String host, int port, Board board, JTextField textField) {
        m_host = host;
        m_port = port;
        m_board = board;
        m_textfield = textField;
    }

    @Override
    public void run() {
        super.run();

        try {
            Socket socketClient = new Socket(m_host, m_port);
            m_textfield.setText("connected!");

            is = socketClient.getInputStream();
            bw = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));

            ReadMsg readMsgfromserver = new ReadMsg(is, bw, m_board);
            readMsgfromserver.start();
//            readMsgfromserver.join();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
