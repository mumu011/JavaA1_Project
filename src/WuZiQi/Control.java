package WuZiQi;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Control {
    private JPanel control;
    private JButton btn_start;
    private JButton btn_exit;
    private JButton btn_stop;
    private JButton btn_restart;
    private JButton btn_record;
    private JButton btn_recurrence;
    private JComboBox comboBox_player;
    private JComboBox comboBox_steps;
    private JButton btn_server;
    private JButton btn_client;
    private JButton btn_qiuhe;
    private JButton btn_renshu;
    private JLabel label_online;
    private JTextField textField_online;
    private JLabel label_time;
    private JTextField textField_time;

    // 棋盘panel,用于信息交互
    public Board m_board;
    // 复盘文件内容
    public ArrayList<String> list_record = new ArrayList<>();
    // client
    public Client client;
    // server
    public Server server;
    // 是否网络对战
    public boolean IsOnline = false;

    public Control(Board board) {
        m_board = board;

        // 设置combobox选项
        comboBox_player.addItem("");
        comboBox_player.addItem("黑棋");
        comboBox_player.addItem("白棋");

        // 设置网络对战显示
        textField_online.setText("not online\t");

        btn_start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double random = Math.random();
                m_board.IsPaused = false;
                if (random < 0.5) {
                    m_board.player = 1;
                    m_board.current_player = 1;
                    m_board.m_display.UpdateTextField(1,m_board.IsBlack);
                    JOptionPane.showMessageDialog(null,"玩家一先手");
                }
                else {
                    m_board.player = 2;
                    m_board.current_player = 2;
                    m_board.m_display.UpdateTextField(2,m_board.IsBlack);
                    JOptionPane.showMessageDialog(null,"玩家二先手");
                }
                if (IsOnline) {
                    m_board.senddata();
                }
            }
        });
        btn_exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(null, "确认退出?", "确认", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if(result == JOptionPane.OK_OPTION){
                    System.exit(0);
                }
            }
        });
        btn_stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 如果游戏开始
                if (m_board.player != 0) {
                    if (m_board.IsPaused) {
                        JOptionPane.showMessageDialog(null, "游戏重新开始");
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "游戏暂停");
                    }
                    m_board.IsPaused = !m_board.IsPaused;
                }
                else {
                    // 游戏还未开始
                    JOptionPane.showMessageDialog(null, "游戏还未开始");
                }
            }
        });
        btn_restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m_board.init();
                m_board.IsPaused = false;
                double random = Math.random();
                if (random < 0.5) {
                    m_board.player = 1;
                    m_board.current_player = 1;
                    m_board.m_display.UpdateTextField(1,m_board.IsBlack);
                    JOptionPane.showMessageDialog(null,"玩家一先手");
                }
                else {
                    m_board.player = 2;
                    m_board.current_player = 2;
                    m_board.m_display.UpdateTextField(2,m_board.IsBlack);
                    JOptionPane.showMessageDialog(null,"玩家二先手");
                }
                if (IsOnline) {
                    m_board.senddata();
                }
            }
        });
        btn_record.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m_board.record();
                JOptionPane.showMessageDialog(null,"记录成功");
            }
        });
        btn_recurrence.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Read();
//                for (String s:
//                     list_record) {
//                    System.out.println(s);
//                }
                m_board.init();
                JOptionPane.showMessageDialog(null,"请开始复盘");
            }
        });

        comboBox_player.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 为combobox_steps添加元素
                String item = (String) comboBox_player.getSelectedItem();
                if (item.equals("黑棋")) {
                    for (String s:
                            list_record) {
                        if (s.startsWith("BlackStep")) {
                            String[] strings = s.split(":");
                            int num = Integer.parseInt(strings[1]);
                            comboBox_steps.removeAllItems();
                            comboBox_steps.addItem("");
                            for (int i = 1; i <= num; i++) {
                                comboBox_steps.addItem(String.valueOf(i));
                            }
                        }
                    }
                }
                else if (item.equals("白棋")) {
                    for (String s:
                            list_record) {
                        if (s.startsWith("WhiteStep")) {
                            String[] strings = s.split(":");
                            int num = Integer.parseInt(strings[1]);
                            comboBox_steps.removeAllItems();
                            comboBox_steps.addItem("");
                            for (int i = 1; i <= num; i++) {
                                comboBox_steps.addItem(String.valueOf(i));
                            }
                        }
                    }
                }
            }
        });
        comboBox_steps.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String item = (String) e.getItem();
                    if (!item.isEmpty()) {
                        String item1 = (String) comboBox_player.getSelectedItem();
                        ArrayList<Loc> list_black = new ArrayList<>();
                        ArrayList<Loc> list_white = new ArrayList<>();
                        boolean isBlack = true;
                        if (item1.equals("黑棋")) {
                            isBlack = false;
                            int step = Integer.parseInt(item);
                            // 目标步数
                            step = 2 * step - 1;
                            int index = list_record.indexOf("Step:" + String.valueOf(step));
                            // 黑棋列表
                            String[] black_list = list_record.get(index+1).split(":|---");
                            for (int i = 1; i < black_list.length - 1; i += 2) {
                                int x = Integer.parseInt(black_list[i]);
                                int y = Integer.parseInt(black_list[i+1]);
                                Loc loc = new Loc(x, y);
                                list_black.add(loc);
                            }
                            // 白棋列表
                            String[] white_list = list_record.get(index+2).split(":|---");
                            for (int i = 1; i < white_list.length - 1; i += 2) {
                                int x = Integer.parseInt(white_list[i]);
                                int y = Integer.parseInt(white_list[i+1]);
                                Loc loc = new Loc(x, y);
                                list_white.add(loc);
                            }
                        }
                        else if (item1.equals("白棋")) {
                            isBlack = true;
                            int step = Integer.parseInt(item);
                            // 目标步数
                            step = 2 * step;
                            int index = list_record.indexOf("Step:" + String.valueOf(step));
                            // 黑棋列表
                            String[] black_list = list_record.get(index+1).split(":|---");
                            for (int i = 1; i < black_list.length - 1; i += 2) {
                                int x = Integer.parseInt(black_list[i]);
                                int y = Integer.parseInt(black_list[i+1]);
                                Loc loc = new Loc(x, y);
                                list_black.add(loc);
                            }
                            // 白棋列表
                            String[] white_list = list_record.get(index+2).split(":|---");
                            for (int i = 1; i < white_list.length - 1; i += 2) {
                                int x = Integer.parseInt(white_list[i]);
                                int y = Integer.parseInt(white_list[i+1]);
                                Loc loc = new Loc(x, y);
                                list_white.add(loc);
                            }
                        }
                        else {
                            return;
                        }

                        // 重新开始
                        int player = 1;
                        for (String s:
                             list_record) {
                            if (s.startsWith("Player")) {
                                String[] strings = s.split(":");
                                player = Integer.parseInt(strings[1]);
                            }
                        }
                        int currentplayer = 1;
                        if (isBlack) {
                            if (player == 1) {
                                currentplayer = 1;
                            }
                            else {
                                currentplayer = 2;
                            }
                        }
                        else {
                            if (player == 1) {
                                currentplayer = 2;
                            }
                            else {
                                currentplayer = 1;
                            }
                        }

                        m_board.Recurrence(list_black, list_white, isBlack, player, currentplayer);

//                        System.out.println("黑棋");
//                        for (Loc loc:
//                             list_black) {
//                            System.out.println(loc);
//                        }
//                        System.out.println("白棋");
//                        for (Loc loc:
//                                list_white) {
//                            System.out.println(loc);
//                        }
                    }
                }
            }
        });
        btn_client.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 端口号
                int port = 8000;
                // host
                String host = "localhost";
                client = new Client(host, port, m_board, textField_online);

                client.start();
                try {
                    client.join();
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }

                m_board.IsOnline = true;
                m_board.bufferedWriter = client.bw;
                IsOnline = true;
            }
        });
        btn_server.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 端口号
                int port = 8000;
                server = new Server(port, m_board, textField_online);

                server.start();
                try {
                    server.join();
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }

                m_board.IsOnline = true;
                m_board.bufferedWriter = server.bw;
                IsOnline = true;
            }
        });
        btn_qiuhe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m_board.sendMsg_qiuhe();
            }
        });
        btn_renshu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m_board.sendMsg_renshu();
            }
        });
    }

    // 读取复盘文件
    public void Read() {
        try {
            FileReader reader = new FileReader("Record.txt");
            BufferedReader br = new BufferedReader(reader);
            String line = null;

            while ((line = br.readLine()) != null) {
                list_record.add(line);
            }
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public JPanel getPanel() {
        return control;
    }
}
