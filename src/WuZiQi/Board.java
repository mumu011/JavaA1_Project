package WuZiQi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Board extends JPanel {
    // 预落子列表
    ArrayList<Checker> list_yuLuoZi = new ArrayList<>();
    // 落子列表
    ArrayList<Checker> list_LuoZi = new ArrayList<>();
    // 当前是黑棋还是白棋
    boolean IsBlack = true;
    // 黑棋落子位置集合
    ArrayList<Loc> list_Black = new ArrayList<>();
    // 白棋落子位置集合
    ArrayList<Loc> list_White = new ArrayList<>();
    // 先手玩家
    public int player = 0;
    // 当前玩家
    public int current_player = 0;
    // 停止指令
    public boolean IsPaused = true;
    // 棋局间隔
    public int distance = 50;
    // 棋子半径
    public int radius = 20;
    // 显示panel,用于显示当前玩家
    public Display m_display;
    // 步数
    public int steps = 0;
    // 复盘列表
    ArrayList<Step> list_steps = new ArrayList<>();
    // 是否网络对战
    public boolean IsOnline = false;
    // socket写端口
    public BufferedWriter bufferedWriter;

    public Board (Display display) {
        m_display = display;

        setBackground(new Color( 210, 167, 135));

        // 添加鼠标移动监听 预览棋子
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                if (IsPaused) {
                    return;
                }

                int x = e.getX();
                int y = e.getY();
//                System.out.println(x + "---" + y);

                int grid_x1 = x / distance;
                int grid_y1 = y / distance;
                int grid_x2 = grid_x1 + 1;
                int grid_y2 = grid_y1 + 1;
                int bound = 10;

                // 检测x1,y1
                if(Math.abs(x - distance * grid_x1) <= bound && Math.abs(y - distance * grid_y1) <= bound) {
                    if (grid_x1 > 0 && grid_x1 < 17 && grid_y1 > 0 && grid_y1 < 17) {
                        Checker checker = new Checker(grid_x1, grid_y1, Color.GRAY);
                        list_yuLuoZi.add(checker);
                    }
                }

                // 检测x1,y2
                else if(Math.abs(x - distance * grid_x1) <= bound && Math.abs(y - distance * grid_y2) <= bound) {
                    if (grid_x1 > 0 && grid_x1 < 17 && grid_y2 > 0 && grid_y2 < 17) {
                        Checker checker = new Checker(grid_x1, grid_y2, Color.GRAY);
                        list_yuLuoZi.add(checker);
                    }
                }

                // 检测x2,y1
                else if(Math.abs(x - distance * grid_x2) <= bound && Math.abs(y - distance * grid_y1) <= bound) {
                    if (grid_x2 > 0 && grid_x2 < 17 && grid_y1 > 0 && grid_y1 < 17) {
                        Checker checker = new Checker(grid_x2, grid_y1, Color.GRAY);
                        list_yuLuoZi.add(checker);
                    }
                }

                // 检测x2,y2
                else if(Math.abs(x - distance * grid_x2) <= bound && Math.abs(y - distance * grid_y2) <= bound) {
                    if (grid_x2 > 0 && grid_x2 < 17 && grid_y2 > 0 && grid_y2 < 17) {
                        Checker checker = new Checker(grid_x2, grid_y2, Color.GRAY);
                        list_yuLuoZi.add(checker);
                    }
                }

                else {
                    list_yuLuoZi.clear();
                }

                repaint();
            }
        });

        // 添加鼠标点击监听 落子
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (IsPaused) {
                    return;
                }

                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (!list_yuLuoZi.isEmpty()) {
                        int grid_x = list_yuLuoZi.get(0).m_x;
                        int grid_y = list_yuLuoZi.get(0).m_y;
                        Loc loc = new Loc(grid_x, grid_y);
                        if (list_Black.contains(loc) || list_White.contains(loc)) {
                            return;
                        }
                        else {
                            Checker checker = getChecker(grid_x, grid_y);
                            list_LuoZi.add(checker);
                            addLoc(loc);
                            // 交换下棋方
                            IsBlack = !IsBlack;
                            // 更新显示内容
                            if (current_player == 1) {
                                current_player = 2;
                                m_display.UpdateTextField(current_player, IsBlack);
                            }
                            else {
                                current_player = 1;
                                m_display.UpdateTextField(current_player, IsBlack);
                            }
                        }
                    }
                    repaint();

                    // socket发送
                    if (IsOnline) {
                        senddata(bufferedWriter);
                    }
                    // 判断是否有赢家
                    int result = getWinner();
                    if (result == 1) {
//                        JOptionPane.showMessageDialog(null, "黑棋赢");
                        if (player == 1) {
                            m_display.UpdateWinner(1,true);
                            JOptionPane.showMessageDialog(null,"玩家一赢");
                        }
                        else {
                            m_display.UpdateWinner(2,true);
                            JOptionPane.showMessageDialog(null,"玩家二赢");
                        }
                        IsPaused = true;
                    }
                    if (result == 2) {
//                        JOptionPane.showMessageDialog(null, "白棋赢");
                        if (player == 1) {
                            m_display.UpdateWinner(2,false);
                            JOptionPane.showMessageDialog(null,"玩家二赢");
                        }
                        else {
                            m_display.UpdateWinner(1,false);
                            JOptionPane.showMessageDialog(null,"玩家一赢");
                        }
                        IsPaused = true;
                    }

                    // 调试用，显示黑棋子和白棋子位置
//                    System.out.println("黑棋坐标");
//                    for (Loc loc:
//                         list_Black) {
//                        System.out.println(String.valueOf(loc.m_x) + "---" + String.valueOf(loc.m_y));
//                    }
//                    System.out.println("白棋坐标");
//                    for (Loc loc:
//                            list_White) {
//                        System.out.println(String.valueOf(loc.m_x) + "---" + String.valueOf(loc.m_y));
//                    }
//                    System.out.println("结果");
//                    System.out.println(result);
                }
            }
        });
    }

    // 向socket发送数据报
    // 数据报包括ArrayList<Loc> blacklist, ArrayList<Loc> whitelist, boolean isBlack, int Player, int CurrentPlayer以便重启游戏
    public void senddata(BufferedWriter bw) {

        try {
            bw.write("Blacklist:");
            for (Loc loc:
             list_Black) {
                bw.write(loc.toString());
            }
//            bw.write("\n");
            bw.flush();

            bw.write("Whitelist:");
            for (Loc loc:
                 list_White) {
                bw.write(loc.toString());
            }
//            bw.write("\n");
            bw.flush();

            bw.write("IsBlack:" + String.valueOf(IsBlack));
            bw.flush();

            bw.write("Player:" + String.valueOf(player));
            bw.flush();

            bw.write("CurrentPlayer:" + String.valueOf(current_player) + "\n");
            bw.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取当前棋子
    private Checker getChecker(int grid_x, int grid_y) {
        Checker checker;
        if (IsBlack) {
            checker = new Checker(grid_x, grid_y, Color.BLACK);
        }
        else {
            checker = new Checker(grid_x, grid_y, Color.WHITE);
        }

        return checker;
    }

    // 更新棋子列表并更新复盘列表
    private void addLoc(Loc loc) {
        if (IsBlack) {
            list_Black.add(loc);
        }
        else {
            list_White.add(loc);
        }

        steps = steps + 1;
        Step step = new Step(steps, IsBlack, list_Black, list_White);
        list_steps.add(step);
    }

    // 将复盘列表数据存入txt文件
    public void record() {
        try {
            File w_f = new File("Record.txt");
            w_f.createNewFile();
            FileWriter writer = new FileWriter(w_f);
            BufferedWriter out = new BufferedWriter(writer);

            for (Step s:
                 list_steps) {
                out.write("Step:" + String.valueOf(s.m_step) + "\r\n");
                out.write("list_Black:");
                for (Loc loc:
                     s.m_listBlack) {
                    out.write(loc.toString() + ":");
                }
                out.write("\r\n");
                out.write("list_White:");
                for (Loc loc:
                        s.m_listWhite) {
                    out.write(loc.toString() + ":");
                }
                out.write("\r\n");
                out.flush();
            }

            // 先手玩家
            out.write("Player:" + String.valueOf(player) + "\r\n");

            // 黑棋、白棋所走步数
            int max_BlackStep = 0;
            int max_WhiteStep = 0;

            if (IsBlack) {
                max_WhiteStep = (steps + 1) / 2;
                max_BlackStep = steps / 2;
            }
            else {
                max_BlackStep = (steps + 1) / 2;
                max_WhiteStep = steps / 2;
            }

            out.write("BlackStep:" + String.valueOf(max_BlackStep) + "\r\n");
            out.write("WhiteStep:" + String.valueOf(max_WhiteStep) + "\r\n");

            out.close();
            IsPaused = true;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 以给定状态重启游戏
    public void Recurrence(ArrayList<Loc> blacklist, ArrayList<Loc> whitelist, boolean isBlack, int Player, int CurrentPlayer) {
        // 更新白棋、黑棋位置
        list_Black.clear();
        list_White.clear();
        for (Loc loc:
             blacklist) {
            list_Black.add(loc);
        }
        for (Loc loc:
             whitelist) {
            list_White.add(loc);
        }

        // 更新落子、预落子列表
        list_yuLuoZi.clear();
        list_LuoZi.clear();
        for (Loc loc:
             blacklist) {
            Checker checker = new Checker(loc.m_x, loc.m_y, Color.BLACK);
            list_LuoZi.add(checker);
        }
        for (Loc loc:
                whitelist) {
            Checker checker = new Checker(loc.m_x, loc.m_y, Color.WHITE);
            list_LuoZi.add(checker);
        }

        // 更新控制元素
        IsBlack = isBlack;
        player = Player;
        current_player = CurrentPlayer;
        IsPaused =false;

        // 更新display panel
        m_display.UpdateTextField(current_player, IsBlack);

        repaint();
    }

    // 判断是否有赢家 0 无 1 黑棋赢 2 白棋赢
    private int getWinner() {
        for (Loc loc:
             list_Black) {
            // 判断斜边1
            for (int k = 1; k <= 5; k++) {
                Loc target_loc = new Loc(loc.m_x + k, loc.m_y + k);
                if (k == 5) {
                    return 1;
                }
                if (list_Black.contains(target_loc)) {
                    continue;
                }
                else {
                    break;
                }
            }

            // 判断斜边2
            for (int k = 1; k <= 5; k++) {
                Loc target_loc = new Loc(loc.m_x + k, loc.m_y - k);
                if (k == 5) {
                    return 1;
                }
                if (list_Black.contains(target_loc)) {
                    continue;
                }
                else {
                    break;
                }
            }

            // 判断竖列
            for (int k = 1; k <= 5; k++) {
                Loc target_loc = new Loc(loc.m_x, loc.m_y + k);
                if (k == 5) {
                    return 1;
                }
                if (list_Black.contains(target_loc)) {
                    continue;
                }
                else {
                    break;
                }
            }

            // 判断横列
            for (int k = 1; k <= 5; k++) {
                Loc target_loc = new Loc(loc.m_x + k, loc.m_y);
                if (k == 5) {
                    return 1;
                }
                if (list_Black.contains(target_loc)) {
                    continue;
                }
                else {
                    break;
                }
            }
        }

        for (Loc loc:
                list_White) {
            // 判断斜边1
            for (int k = 1; k <= 5; k++) {
                Loc target_loc = new Loc(loc.m_x + k, loc.m_y + k);
                if (k == 5) {
                    return 2;
                }
                if (list_White.contains(target_loc)) {
                    continue;
                }
                else {
                    break;
                }
            }

            // 判断斜边2
            for (int k = 1; k <= 5; k++) {
                Loc target_loc = new Loc(loc.m_x + k, loc.m_y - k);
                if (k == 5) {
                    return 2;
                }
                if (list_White.contains(target_loc)) {
                    continue;
                }
                else {
                    break;
                }
            }

            // 判断竖列
            for (int k = 1; k <= 5; k++) {
                Loc target_loc = new Loc(loc.m_x, loc.m_y + k);
                if (k == 5) {
                    return 2;
                }
                if (list_White.contains(target_loc)) {
                    continue;
                }
                else {
                    break;
                }
            }

            // 判断横列
            for (int k = 1; k <= 5; k++) {
                Loc target_loc = new Loc(loc.m_x + k, loc.m_y);
                if (k == 5) {
                    return 2;
                }
                if (list_White.contains(target_loc)) {
                    continue;
                }
                else {
                    break;
                }
            }
        }

        return 0;
    }

    // 重新初始化
    public void init() {
        list_yuLuoZi.clear();
        list_LuoZi.clear();
        list_Black.clear();
        list_White.clear();
        IsBlack = true;
        m_display.init();
        player = 0;
        steps = 0;
        list_steps.clear();

        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        int x_start = distance;
        int y_start = distance;
        // 画竖线
        for (int i = 0; i < 16; i++) {
            int x1 = x_start + i * distance;
            int y1 = y_start;
            g.drawLine(x1, y1, x1, y1 + 15 * distance);
        }
        // 画横线
        for (int i = 0; i < 16; i++) {
            int x1 = x_start;
            int y1 = y_start + i * distance;
            g.drawLine(x1, y1, x1 + 15 * distance, y1);
        }

        // 绘制预落子
        for (Checker c:
                list_yuLuoZi) {
            g.setColor(c.m_color);
            g.fillOval(c.m_x * distance - radius, c.m_y * distance - radius, 2 * radius, 2 * radius);
        }

        // 绘制落子
        for (Checker c:
             list_LuoZi) {
            g.setColor(c.m_color);
            g.fillOval(c.m_x * distance - radius, c.m_y * distance - radius, 2 * radius, 2 * radius);
        }
    }
}
