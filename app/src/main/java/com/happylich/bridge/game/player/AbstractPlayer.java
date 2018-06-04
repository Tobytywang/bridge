package com.happylich.bridge.game.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.happylich.bridge.engine.util.Position;
import com.happylich.bridge.game.scene.Call;
import com.happylich.bridge.game.scene.Ready;
import com.happylich.bridge.game.scene.Table;
import com.happylich.bridge.game.res.CardImage;

import java.util.ArrayList;

/**
 * Created by wangt on 2017/11/16.
 *
 * 玩家状态（根据状态采取不同的绘制策略）
 * 000表示叫牌阶段：玩家+叫牌
 * 100表示调整座位：
 * 200：玩家+桌面
 * 200：不绘制
 * 201：下方，向上
 * 202：下方，向下
 * 211：上方，向上
 * 212：上方，向下
 * 221：左方，向上
 * 222：左方，向下
 * 231：右方，向上
 * 232：右方，向下
 * 3表示结算：结算
 */

/**
 * TODO:玩家的绘制工作与Game.stage无关！
 */
public abstract class AbstractPlayer {

    protected Context context;

    // 绘图工具
    Rect des = new Rect();
    protected Position position1 = new Position();
    protected Position positionSelected1 = new Position();
    protected Position positionSelected2 = new Position();

    protected int width, height;
    protected int top, left;

    protected int playerStage;

    public Ready ready;
    public Call call;
    public Table table;

    protected ArrayList<Integer> cards;
    protected int selectCardIndex = -1;
    protected int selectCard = -1;
    public int direction = -1;
    public int position = -1;

    /**
     * 构造函数
     */
    public AbstractPlayer() {
    }

    /**
     * 设置绘图模式
     * 现在position表示玩家的逻辑
     * @param playerStage
     */
    public void setPlayerStage(int playerStage) {
        if (this.position == 0) {
            if (playerStage == 1) {
                this.playerStage = 201;
            } else if (playerStage == 2) {
                this.playerStage = 202;
            }
        } else if (this.position == 1) {
            if (playerStage == 1) {
                this.playerStage = 211;
            } else if (playerStage == 2) {
                this.playerStage = 212;
            }
        } else if (this.position == 2) {
            if (playerStage == 1) {
                this.playerStage = 221;
            } else if (playerStage == 2) {
                this.playerStage = 222;
            }
        } else if (this.position == 3) {
            if (playerStage == 1) {
                this.playerStage = 231;
            } else if (playerStage == 2) {
                this.playerStage = 232;
            }
        }
    }


    /**
     * 玩家初始化座次
     * @param direction
     */
    public void setDirection(int direction) {
        if (direction > 3) {
            direction = direction - 4;
        }
        if (direction < 0) {
            direction = direction + 4;
        }
        this.direction = direction;
    }

    /**
     * 玩家初始化座次
     * @param position
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * 设置玩家持有的Ready副本
     * @param ready
     */
    public void setReady(Ready ready) {
        this.ready = ready;
    }

    /**
     * 设置玩家持有的call副本
     * @param call
     */
    public void setCall(Call call) {
        this.call = call;
    }

    /**
     * 设置玩家持有的table副本
     * @param table
     */
    public void setTable(Table table) {
        this.table = table;
    }


    /**
     * 获得玩家手牌
     * @return
     */
    public ArrayList getCards() {
        return cards;
    }

    /**
     * 玩家初始化手牌
     */
    public void setCards(ArrayList<Integer> cards) {
        this.cards = cards;
    }

    /**
     * 玩家出牌
     * @param cardNumber
     */
    public int removeCard(int cardNumber) {
        return this.cards.remove(cardNumber);
    }


    /**
     * 表示玩家是否处于就绪状态
     */
    public boolean isInOrder() {
        if (this.direction == 0 || this.direction == 1 ||
                this.direction == 2 || this.direction == 3) {
            return true;
        }
        return false;
    }

    /**
     * 设置绘图基准点
     * @param position
     */
    public void setPosition(int[] position) {
        this.left = position[0];
        this.top = position[1];
    }

    /**
     * 设置宽高
     */
    public void setWidthHeight(int width, int height) {
        this.width = width;
        this.height = height;
    }




    /**
     * 叫牌函数
     * 叫牌函数返回0-35，0-34表示有效叫牌值，35表示pass
     */
    abstract public boolean callCard();

    /**
     * 打牌函数
     */
    abstract public boolean dropCard();

    /**
     * 获得大牌点
     * @return
     */
    public int getPoints() {
        int point = 0;
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i) % 13 >= 9) {
                point = point + ((cards.get(i) / 13) - 8);
            }
        }
        return point;
    }

    /**
     * 获得牌点调整值
     * @param color
     */
    public int getPointsExt(int color) {
        int point = 0;
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i) / 13 == color) {
                point++;
            }
        }
        switch (point) {
            case 0:
                return 5;
            case 1:
                return 3;
            case 2:
                return 1;
            default:
                return 0;
        }
    }

    /**
     * 是否是均型牌
     * @return
     */
    public int isBalance() {
        int[] numbers = {0, 0, 0, 0};
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i) / 13 == 0) {
                numbers[0]++;
            } else if (cards.get(i) / 13 == 1) {
                numbers[1]++;
            } else if (cards.get(i) / 13 == 2) {
                numbers[2]++;
            } else {
                numbers[3]++;
            }
        }

        // 写一个冒泡循环
        for (int i = 4; i > 0; i++) {
            for (int j = 0; j < i; j++) {
                if (numbers[j] < numbers[j+1]) {
                    int temp = numbers[j];
                    numbers[j] = numbers[j+1];
                    numbers[j+1] = temp;
                }
            }
        }

        if (numbers[0] == 4 && numbers[1] == 3 && numbers[2] == 3 && numbers[3] == 3) {
            return 0;
        }
        if (numbers[0] == 4 && numbers[1] == 4 && numbers[2] == 3 && numbers[3] == 2) {
            return 0;
        }
        if (numbers[0] == 5 && numbers[1] == 3 && numbers[2] == 3 && numbers[3] == 2) {
            return 0;
        }
        if (numbers[0] == 5 && numbers[1] == 4 && numbers[2] == 2 && numbers[3] == 2) {
            return 1;
        }
        if (numbers[0] == 6 && numbers[1] == 3 && numbers[2] == 2 && numbers[3] == 2) {
            return 1;
        }
        return 2;
    }
}
