package com.kob.backend.consumer.utils;

import lombok.Getter;

public class Game {
    final private int rows;
    final private int cols;
    final private int inner_walls_count;
    @Getter
    final private int[][] g;

    public Game(Integer rows, Integer cols, Integer inner_walls_count) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
    }

    private boolean check_connective(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = 1;

        int[] dx = {-1, 0, 1, 0}, dy = {0, -1, 0, 1};
        for (int i = 0; i < 4; i++) {
            int x = sx + dx[i], y = sy + dy[i];
            if (x >= 0 && x < rows && y >= 0 && y < cols &&  g[x][y] == 0 && check_connective(x, y, tx, ty)){
                g[sx][sy] = 0;
                return true;
            }
        }
        g[sx][sy] = 0;
        return false;
    }

    private boolean draw() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                g[i][j] = 0;
            }
        }
        //给四周加上障碍物
        for (int r = 0; r < rows; r++) {
            g[r][0] = g[r][cols - 1] = 1;
        }
        for (int c = 0; c < cols; c++) {
            g[0][c] = g[rows - 1][c] = 1;
        }
        //创建随机障碍物
        for (int i = 0; i < this.inner_walls_count / 2; i++) {
            for (int j = 0; ; j++) {
                int r = (int)(Math.random() * rows);
                int c = (int)(Math.random() * cols);
                if (g[r][c] == 1 || g[rows - 1- r][cols - 1 - c] == 1)
                    continue;
                if ((r == rows - 2 && c == 1 )|| (r == 1 && c == cols - 2))
                    continue;
                g[r][c] = g[rows - 1 - r][cols - 1 - c] = 1;
                break;
            }
        }
        return check_connective(rows - 2, 1, 1, cols - 2);
    }

    public void createMap() {
        for (int i = 0; ; i++) {
            if (draw())
                break;
        }
    }
}
