package WuZiQi;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.util.ArrayList;

public class ReadMsg extends Thread{
    public InputStream m_br;
    public BufferedWriter m_bw;
    public Board m_board;

    public ReadMsg(InputStream is, BufferedWriter bw, Board board) {
        m_br = is;
        m_bw = bw;
        m_board = board;
    }

    @Override
    public void run() {
        super.run();

        byte[] bytes = new byte[4096];
        String content;

         while (true) {
             try {
                 int len = m_br.read(bytes, 0, bytes.length);
                 content = new String(bytes, 0, len);
                 boolean IsQiuhe = false;
                 boolean IsEnd = false;

//                 System.out.println(content);
                 if (!content.isEmpty()) {
                     ArrayList<Loc> list_black = new ArrayList<>();
                     ArrayList<Loc> list_white = new ArrayList<>();
                     boolean IsBlack = true;
                     int player = 1;
                     int currentplayer = 1;
                     String[] strings = content.split("\n|;");
                     for (String s:
                             strings) {
                         if (s.startsWith("Blacklist")) {
                             String[] black_list = s.split(":|---");
                             for (int i = 1; i < black_list.length - 1; i += 2) {
                                 int x = Integer.parseInt(black_list[i]);
                                 int y = Integer.parseInt(black_list[i+1]);
                                 Loc loc = new Loc(x, y);
                                 list_black.add(loc);
                             }
                         }
                         else if (s.startsWith("Whitelist")) {
                             String[] white_list = s.split(":|---");
                             for (int i = 1; i < white_list.length - 1; i += 2) {
                                 int x = Integer.parseInt(white_list[i]);
                                 int y = Integer.parseInt(white_list[i+1]);
                                 Loc loc = new Loc(x, y);
                                 list_white.add(loc);
                             }
                         }
                         else if (s.startsWith("IsBlack")) {
                             String[] strings1 = s.split(":");
                             IsBlack  = Boolean.valueOf(strings1[1]);
                         }
                         else if (s.startsWith("Player")) {
                             String[] strings1 = s.split(":");
                             player  = Integer.parseInt(strings1[1]);
                         }
                         else if (s.startsWith("CurrentPlayer")) {
                             String[] strings1 = s.split(":");
                             currentplayer  = Integer.parseInt(strings1[1]);
                         }
                         else if (s.startsWith("qiuhe")) {
                             IsQiuhe = true;
                         }
                         else if (s.startsWith("end")) {
                             IsEnd = true;
                         }
                     }
                     if (IsQiuhe) {
                         int res = JOptionPane.showConfirmDialog(null, "是否接受求和", "求和", JOptionPane.YES_NO_CANCEL_OPTION);
                         if (res == JOptionPane.YES_OPTION) {
                             m_board.init();
                             m_board.display_MsgQiuhe();
                             m_board.sendMsg_end();
                             JOptionPane.showMessageDialog(null, "平局！");
                         }
                         else {
                             m_board.Recurrence(list_black, list_white, IsBlack, player, currentplayer);
                         }
                     }
                     else if (IsEnd) {
                         m_board.init();
                         m_board.display_MsgQiuhe();
                     }
                     else {
                         m_board.Recurrence(list_black, list_white, IsBlack, player, currentplayer);
                     }
                 }
             }
             catch (Exception e) {
                 e.printStackTrace();
             }
        }
    }
}
