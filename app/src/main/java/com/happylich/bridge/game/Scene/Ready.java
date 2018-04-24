package com.happylich.bridge.game.Scene;

/**
 * Created by lich on 2018/4/24.
 */

import android.graphics.Canvas;

import com.happylich.bridge.engine.util.Position;

/**
 * 玩家准备界面
 */
public class Ready extends AbstractScene {

    private int readyStage = 0;

    /**
     * 检测按键（这个只有本地玩家有）
     * 在特定阶段被game.call调用
     * @param x
     * @param y
     * @return 表示事件类型，0表示无效区域，1表示有效区域
     */
    public int onTouch(int x, int y) {
        int touch;
        switch(readyStage) {
            case 0:
//                touch = touchSmall(x, y);
//                if (touch == 1) {
//                    return 1;
//                } else {
//                    return 0;
//                }
            case 1:
//                touch = touchBig(x, y);
//                if (touch == 2) {
//                    return 2;
//                } else if(touch == 3){
//                    return 3;
//                } else {
//                    return 0;
//                }
            default:
                return 0;
        }
    }

    public int touchReady(int x, int y) {
        int left = this.left;
        int top = this.top + 100;
        Position position = new Position(top, left,
                top + 1144, left + 720);
        position.resieze((float)this.width / (float)1440);
        if (Position.inPosition(x, y, position)) {
            return 1;
        }
        return 0;
    }

    /**
     * 绘制叫牌矩阵
     * @param canvas
     */
    public void draw(Canvas canvas) {
//        drawTest(canvas);
        switch (readyStage) {
            case 0:
//                drawSmall(canvas);
//                drawHistory(canvas);
                break;
            case 1:
//                drawBig(canvas);
                break;
            case 2:
//                drawBigSelected(canvas);
                break;
            default:
//                drawSmall(canvas);
//                drawHistory(canvas);
        }
    }

    public void drawUnready(Canvas canvas) {

    }

    public void drawReay(Canvas canvas) {

    }
}
