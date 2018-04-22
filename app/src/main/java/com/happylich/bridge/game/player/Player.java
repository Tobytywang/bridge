package com.happylich.bridge.game.player;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;

import com.happylich.bridge.engine.util.Position;

/**
 * Created by wangt on 2018/3/22.
 */


/**
 * 这是反应棋盘上一个玩家状态的一个绘制类
 * 当这个类被远程玩家代理时如何处理？
 * 这个玩家的操作会被本地解析，同时也会被远程代理？
 * 如果一个Player被Remote代理了，应该会有标志表示这个类需要向远程传递消息
 */
public class Player extends AbstractPlayer {

    /**
     * 构造函数
     * @param context
     * @param position
     */
    public Player(Context context, int position) {
        this.context = context;
        this.position = position;
    }

    /**
     * 南家触摸事件
     * @param x
     * @param y
     * @return
     */
    public int touchBottom(int x, int y) {
        Position position;
        Position positionSelected;

        // 虽然规定了left，但是并不采用，实际情况下还是根据width重新绘制
        int left = (1440 - (cards.size() - 1) * 90 - 180) / 2;
        int top = this.top;

        for (int i=0; i<cards.size(); i++) {

            if (selectCard != -1) {
                // 如果已经选中牌了，则出牌或者重新选牌
                if (i < cards.size() - 1) {
                    position = new Position(top, left + i * 90,
                            top + 240, left + 90 + i * 90);
                } else {
                    position = new Position(top, left + i * 90,
                            top + 240, left + 180 + i * 90);
                }
                position.resieze((float)this.width / (float)1440);

                positionSelected = new Position(top - 120, left + selectCardIndex * 90,
                        top, left + 180 + selectCardIndex * 90);
                positionSelected.resieze((float)this.width / (float)1440);

                if (Position.inPosition(x, y, positionSelected)) {
                    // 出牌
                    Log.v(this.getClass().getName(), "出牌");
                    table.dropCard(this.position, cards.remove(selectCardIndex));
                    selectCardIndex = -1;
                    selectCard = -1;
                    return 2;
                } else if (Position.inPosition(x, y, position)) {
                    // 换牌
                    Log.v(this.getClass().getName(), "换牌");
                    selectCardIndex = i;
                    selectCard = cards.get(i);
                    return 1;
                }
                Log.v(this.getClass().getName(), "既不出牌也不换牌");
            } else {
                // 如果没有选中牌，则选牌
                if (i < cards.size() - 1) {
                    position = new Position(top, left + i * 90,
                            top + 240, left + 90 + i * 90);
                } else {
                    position = new Position(top, left + i * 90,
                            top + 240, left + 180 + i * 90);
                }
                position.resieze((float)this.width / (float)1440);
                if (Position.inPosition(x, y, position)) {
                    // 选中牌
                    Log.v(this.getClass().getName(), "选中牌");
                    selectCardIndex = i;
                    selectCard = cards.get(i);
                    return 1;
                }
            }
        }
        Log.v(this.getClass().getName(), "什么都不做");
        return 0;
    }


    /**
     * 北家触摸事件
     * @param x
     * @param y
     * @return
     */
    public int touchTop(int x, int y) {
        Position position;
        Rect des = new Rect();

        // 虽然规定了left，但是并不采用，实际情况下还是根据width重新绘制
        int left = (1440 - (cards.size() - 1) * 80 - 180) / 2;
        int top = this.top;

        Log.v(this.getClass().getName(), "touch-top:" + String.valueOf(this.top));
        Log.v(this.getClass().getName(), "touch-width:" + String.valueOf(this.width));

        // 绘制纸牌（底部玩家）
        for (int i=0; i<cards.size(); i++) {
            if (selectCard != -1) {
                position = new Position(top + 120, left + i * 90,
                        top + 360, left + i * 90);
                position.resieze((float)this.width / (float)1440);
                if (Position.inPosition(x, y, position)) {
                    // 出牌
                    Log.v(this.getClass().getName(), "出牌");
                    return 2;
                }
            } else {
                position = new Position(top, left + i * 80,
                        top + 240, left + 180 + i * 80);
                position.resieze((float)this.width / (float)1440);
                if (Position.inPosition(x, y, position)) {
                    // 选中牌
                    Log.v(this.getClass().getName(), "选中牌");
                    selectCard = i;
                    return 1;
                }
            }
        }
        Log.v(this.getClass().getName(), "什么都不做");
        return 0;
    }


    /**
     * 获得叫牌值
     */
    @Override
    public boolean callCard() {
        // getCall执行完，就进入下一个回合
//        call.setFinish(false);
        return call.isFinish();
    }

    /**
     * 获得出牌值
     * 人类玩家的出牌值要调用触摸函数得到
     */
    @Override
    public boolean dropCard() {
        // 参考叫牌函数的实现
        return table.isFinish();
    }

}
