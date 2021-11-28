package WuZiQi;

import javax.swing.*;

public class Display {
    private JPanel display;
    private JLabel label_player;
    private JTextField textField_player;
    private JLabel label_winner;
    private JTextField textField_winner;

    public Display() {
        textField_player.setText("游戏尚未开始");
        textField_winner.setText("无\t");
    }

    public JPanel getPanel() {
        return display;
    }

    // 修改显示内容
    public void UpdateTextField(int current_player, boolean IsBlack) {
        if (current_player == 1) {
            if (IsBlack) {
                textField_player.setText("玩家一(黑棋)");
            }
            else {
                textField_player.setText("玩家一(白棋)");
            }
        }
        else {
            if (IsBlack) {
                textField_player.setText("玩家二(黑棋)");
            }
            else {
                textField_player.setText("玩家二(白棋)");
            }
        }
    }

    // 修改赢家显示
    public void UpdateWinner(int winner, boolean IsBlack) {
        if (winner == 1) {
            if (IsBlack) {
                textField_winner.setText("玩家一(黑棋)");
            }
            else {
                textField_winner.setText("玩家一(白棋)");
            }
        }
        else {
            if (IsBlack) {
                textField_winner.setText("玩家二(黑棋)");
            }
            else {
                textField_winner.setText("玩家二(白棋)");
            }
        }
    }

    // 和棋
    public void HeQi() {
        textField_winner.setText("平局");
        textField_player.setText("游戏结束");
    }

    // 初始化
    public void init() {
        textField_player.setText("游戏尚未开始");
        textField_winner.setText("无\t");
    }
}
