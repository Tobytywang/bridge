package com.happylich.bridge.game.scene;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.happylich.bridge.game.player.AbstractPlayer;
import com.happylich.bridge.game.res.CardImage;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by lich on 2018/4/10.
 * 负责牌桌的绘制和结算
 */

public class Table extends AbstractScene {
     Path path = new Path();
    RectF round = new RectF();

    // 叫牌阶段标志位
    private int dropStage;

    // 叫牌阶段完成
    private boolean finish;

    // 绘制尺寸
    private int modifier;

    // 庄家
    private AbstractPlayer dealerPlayer = null;

    // 领牌人（本轮首次出牌的）
    private int player = -1;
//    private int leader = -1;

    // 定约阶
    private int level = -1;
    private int suits = -1;

    // ?
    private int full = 0;
    private int tricks = 0;
    private int cardFirstPlayer = -1;
    private int cardFirst = -1;
    private int cardBottom;
    private int cardLeft;
    private int cardTop;
    private int cardRight;
//    private ArrayList<Integer> dropHistory;
    private LinkedHashMap<Integer, Integer> dropHistory = new LinkedHashMap();
    private int tmpPlayer;
    private int tmpCard;


//    private ArrayList<Integer> callHistory  = new ArrayList<>();
//    private ArrayList<Integer> callHistoryN = new ArrayList<>();
//    private ArrayList<Integer> callHistoryE = new ArrayList<>();
//    private ArrayList<Integer> callHistoryS = new ArrayList<>();
//    private ArrayList<Integer> callHistoryW = new ArrayList<>();

    /**
     * 构造函数
     * @param context
     */
    public Table(Context context) {
        this.context = context;
    }

    public void finish() {this.finish = true; }

    /**
     * 结束标志
     */
    public boolean isFinish() {
        return finish;
    }


    /**
     * 设置调整值
     * @param modifier
     */
    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    /**
     * 设置庄家
     * @param dealerPlayer
     */
    public void setDealerAndContract(AbstractPlayer dealerPlayer, int level, int suits) {
        this.dealerPlayer = dealerPlayer;
        this.level = level;
        this.suits = suits;
    }

    /**
     * 设置出牌者
     * @param player
     */
    public void setPlayer(int player) {
        this.player = player;
    }

    /**
     * 获得出牌者
     * @return
     */
    public int getPlayer() {
        // 如果是第一把，leader是dealer++
        // 如果不是第一把，leader是最大的
        if (player == -1) {
            if (dealerPlayer.position < 3) {
                player = dealerPlayer.position + 1;
                return player;
            }
            player = 0;
            return 0;
        }
        return player;
    }

    /**
     * 设置callstage
     * @param stage
     */
    public void setDropStage(int stage) {
        this.dropStage = stage;
    }

    /**
     * 出牌函数，被AbstractPlayer子类调用
     * @param drawPosition
     * @param card
     */
    public void setDrop(int drawPosition, int card) {

        // TODO:应该根据类型而不是位置决定出牌方法

        switch (drawPosition) {
            case 0:
                if (cardFirst == -1) {
                    cardFirst = card;
                    cardFirstPlayer = 0;
                }
                this.dropHistory.put(0, card);
                this.cardBottom = card;
                this.tmpPlayer = 0;
                this.tmpCard = card;
                break;
            case 1:
                if (cardFirst == -1) {
                    cardFirst = card;
                    cardFirstPlayer = 1;
                }
                this.dropHistory.put(1, card);
                this.cardLeft = card;
                this.tmpPlayer = 1;
                this.tmpCard = card;
                break;
            case 2:
                if (cardFirst == -1) {
                    cardFirst = card;
                    cardFirstPlayer = 2;
                }
                this.dropHistory.put(2, card);
                this.cardTop = card;
                this.tmpPlayer = 2;
                this.tmpCard = card;
                break;
            case 3:
                if (cardFirst == -1) {
                    cardFirst = card;
                    cardFirstPlayer = 3;
                }
                this.dropHistory.put(3, card);
                this.cardRight = card;
                this.tmpPlayer = 3;
                this.tmpCard = card;
                break;
        }

        if (this.dropHistory.size() == 4) {
            this.player = sortCards(cardFirst, cardBottom, cardLeft, cardTop, cardRight);

            cardFirst = -1;
            cardFirstPlayer = -1;

            cardBottom = -1;
            cardLeft = -1;
            cardTop = -1;
            cardRight = -1;
            this.tricks++;
            if (tricks == 13) {
                finish();
            }
            this.dropHistory.clear();
        } else {
            if ((++player) > 3) {
                player = 0;
            }
        }
    }

    private int sortCards(int card0, int card1, int card2, int card3, int card4) {
        // 比较四张牌的大小
        // suits从小到大依次是CDHSNT
        // 只需要取最大就行了
        // 选择一个依次去比，

        // 以this.suits和card0/13作为比较依据
        int cardTmp = -1;
        int cardTmpPlayer = -1;

        int tmp = sortTwoCardBySuits(card0, card1, card2);
        if (tmp == 0) {
            // 说明cardFirstPlayer比较大
            cardTmp = card0;
            cardTmpPlayer = cardFirstPlayer;
        } else if (tmp == 1) {
            cardTmp = card1;
            cardTmpPlayer = 0;
        } else if (tmp == 2) {
            cardTmp = card2;
            cardTmpPlayer = 1;
        }

        tmp = sortTwoCardBySuits(card0, cardTmp, card3);
        if (tmp == 0) {
            // 说明cardFirstPlayer比较大
            cardTmp = card0;
            cardTmpPlayer = cardFirstPlayer;
        } else if (tmp == 1) {
            cardTmp = cardTmp;
            cardTmpPlayer = cardTmpPlayer;
        } else if (tmp == 2) {
            cardTmp = card3;
            cardTmpPlayer = 2;
        }

        tmp = sortTwoCardBySuits(card0, cardTmp, card4);
        if (tmp == 0) {
            // 说明cardFirstPlayer比较大
            cardTmp = card0;
            cardTmpPlayer = cardFirstPlayer;
        } else if (tmp == 1) {
            cardTmp = cardTmp;
            cardTmpPlayer = cardTmpPlayer;
        } else if (tmp == 2) {
            cardTmp = card2;
            cardTmpPlayer = 3;
        }

        switch (cardTmpPlayer) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
        }
        return 5;
    }

    /**
     * 比较两个牌的大小(有将牌的情况下-无将牌的时候适用)
     * @param card
     * @param cardOne
     * @param cardTwo
     * @return
     */
    private int sortTwoCardBySuits(int card, int cardOne, int cardTwo) {
        if (cardOne/13 == this.suits && cardTwo/13 != this.suits) {
            return 1;
        } else if (cardOne/13 != this.suits && cardTwo/13 == this.suits) {
            return 2;
        } else if (cardOne/13 == this.suits && cardTwo/13 == this.suits) {
            if (cardOne > cardTwo) {
                return 1;
            }
            return 2;
        }

        if (cardOne/13 == card/13 && cardTwo/13 != card/13) {
            return 1;
        } else if (cardOne/13 != card/13 && cardTwo == card/13) {
            return 2;
        } else if (cardOne/13 == card/13 && cardTwo == card/13) {
            if (cardOne > cardTwo) {
                return 1;
            }
            return 2;
        }

        // 这种情况说明card0比较大
        return 0;
    }

    /**
     * 处理触摸事件
     * @param x
     * @param y
     * @return 0 表示没有反应，1表示选中，2表示出牌
     */
    public int onTouch(int x, int y) {
        int touch;
        Log.v(this.getClass().getName(), "轮到 " + String.valueOf(dropStage));

        switch(dropStage) {
            case 0:
                touch = playerBottom.touchBottom(x, y);
                if (touch == 1) {
                    // 选中牌
                    return 1;
                } else if(touch == 2){
                    // 出牌
                    // TODO:这个出牌的玩家需要修改
                    // TODO:出牌的玩家手动出牌，而不是出第一张
                    return 2;
                }
                return 0;
            case 1:
                touch = playerTop.touchTop(x, y);
                if (touch == 1) {
                    // 选中牌
                    return 1;
                } else if (touch == 2) {
                    return 2;
                }
                return 0;
            default:
                return 0;
        }
    }

    /**
     * 绘制图形界面
     * @param canvas
     */
    public void draw(Canvas canvas, Paint paint, Rect des) {
        // TODO:do nothing
        Bitmap Image;

        int left = this.left + 240 * (this.modifier - 1);
        int top = this.top;

        // 绘制底版
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);
        round.left = left;
        round.right = left + 720;
        round.top = top + 360 + 180;
        // 1144 = 134*7 + 8*6 + 134 + 8*3
        round.bottom = top + 360 + 720 + 180;
        canvas.drawRoundRect(round, 20, 20, paint);

        left = this.left + 240 * (this.modifier - 1);
        top = this.top + 360 + 180;
        // 绘制庄家

//        Image = CardImage.decodeSampledBitmapFromResource(context.getResources(), CardImage.callImages[level * 5 + suits], 180, 240);
        Image = CardImage.callBitmapImages.get(level * 5 + suits);
//        Image = BitmapFactory.decodeResource(context.getResources(),
//                CardImage.callImages[level * 5 + suits]);
        paint.setColor(Color.BLACK);
        paint.setTextSize(80);
        paint.setTextAlign(Paint.Align.CENTER);

        if (dealerPlayer.position == 0) {
            if (dealerPlayer.direction == 0) {
                des.set(left + 300, top + 580, left + 300 + 120, top + 700);
                canvas.drawBitmap(Image, null, des, paint);
                canvas.drawText("西", left + 80, top + 400, paint);
                canvas.drawText("北", left + 360, top + 100, paint);
                canvas.drawText("东", left + 640, top + 400, paint);
            } else if (dealerPlayer.direction == 1) {
                des.set(left + 300, top + 580, left + 300 + 120, top + 700);
                canvas.drawBitmap(Image, null, des, paint);
                canvas.drawText("北", left + 80, top + 400, paint);
                canvas.drawText("东", left + 360, top + 100, paint);
                canvas.drawText("南", left + 640, top + 400, paint);
            } else if (dealerPlayer.direction == 2) {
                des.set(left + 300, top + 580, left + 300 + 120, top + 700);
                canvas.drawBitmap(Image, null, des, paint);
                canvas.drawText("东", left + 80, top + 400, paint);
                canvas.drawText("南", left + 360, top + 100, paint);
                canvas.drawText("西", left + 640, top + 400, paint);
            } else if (dealerPlayer.direction == 3) {
                des.set(left + 300, top + 580, left + 300 + 120, top + 700);
                canvas.drawBitmap(Image, null, des, paint);
                canvas.drawText("南", left + 80, top + 400, paint);
                canvas.drawText("西", left + 360, top + 100, paint);
                canvas.drawText("北", left + 640, top + 400, paint);
            }
//            des.set(left + 300, top + 580, left + 300 + 120, top + 700);
//            canvas.drawBitmap(Image, null, des, paint);
//            canvas.drawText("西", left + 80, top + 400, paint);
//            canvas.drawText("北", left + 360, top + 100, paint);
//            canvas.drawText("东", left + 640, top + 400, paint);
        } else if (dealerPlayer.position == 1) {
            if (dealerPlayer.direction == 0) {
                canvas.drawText("东", left + 360, top + 680, paint);
                des.set(left + 20, top + 300, left + 20 + 120, top + 420);
                canvas.drawBitmap(Image, null, des, paint);
                canvas.drawText("西", left + 360, top + 100, paint);
                canvas.drawText("北", left + 640, top + 400, paint);
            } else if (dealerPlayer.direction == 1) {
                canvas.drawText("南", left + 360, top + 680, paint);
                des.set(left + 20, top + 300, left + 20 + 120, top + 420);
                canvas.drawBitmap(Image, null, des, paint);
                canvas.drawText("北", left + 360, top + 100, paint);
                canvas.drawText("东", left + 640, top + 400, paint);
            } else if (dealerPlayer.direction == 2) {
                canvas.drawText("西", left + 360, top + 680, paint);
                des.set(left + 20, top + 300, left + 20 + 120, top + 420);
                canvas.drawBitmap(Image, null, des, paint);
                canvas.drawText("东", left + 360, top + 100, paint);
                canvas.drawText("南", left + 640, top + 400, paint);
            } else if (dealerPlayer.direction == 3) {
                canvas.drawText("北", left + 360, top + 680, paint);
                des.set(left + 20, top + 300, left + 20 + 120, top + 420);
                canvas.drawBitmap(Image, null, des, paint);
                canvas.drawText("南", left + 360, top + 100, paint);
                canvas.drawText("西", left + 640, top + 400, paint);
            }
//            canvas.drawText("南", left + 360, top + 680, paint);
//            des.set(left + 20, top + 300, left + 20 + 120, top + 420);
//            canvas.drawBitmap(Image, null, des, paint);
//            canvas.drawText("北", left + 360, top + 100, paint);
//            canvas.drawText("东", left + 640, top + 400, paint);
        } else if (dealerPlayer.position == 2) {
            if (dealerPlayer.direction == 0) {
                canvas.drawText("北", left + 360, top + 680, paint);
                canvas.drawText("东", left + 80, top + 400, paint);
                des.set(left + 300, top + 20, left + 300 + 120, top + 140);
                canvas.drawBitmap(Image, null, des, paint);
                canvas.drawText("西", left + 640, top + 400, paint);
            } else if (dealerPlayer.direction == 1) {
                canvas.drawText("东", left + 360, top + 680, paint);
                canvas.drawText("南", left + 80, top + 400, paint);
                des.set(left + 300, top + 20, left + 300 + 120, top + 140);
                canvas.drawBitmap(Image, null, des, paint);
                canvas.drawText("北", left + 640, top + 400, paint);
            } else if (dealerPlayer.direction == 2) {
                canvas.drawText("南", left + 360, top + 680, paint);
                canvas.drawText("西", left + 80, top + 400, paint);
                des.set(left + 300, top + 20, left + 300 + 120, top + 140);
                canvas.drawBitmap(Image, null, des, paint);
                canvas.drawText("东", left + 640, top + 400, paint);
            } else if (dealerPlayer.direction == 3) {
                canvas.drawText("西", left + 360, top + 680, paint);
                canvas.drawText("北", left + 80, top + 400, paint);
                des.set(left + 300, top + 20, left + 300 + 120, top + 140);
                canvas.drawBitmap(Image, null, des, paint);
                canvas.drawText("南", left + 640, top + 400, paint);
            }
//            canvas.drawText("南", left + 360, top + 680, paint);
//            canvas.drawText("西", left + 80, top + 400, paint);
//            des.set(left + 300, top + 20, left + 300 + 120, top + 140);
//            canvas.drawBitmap(Image, null, des, paint);
//            canvas.drawText("东", left + 640, top + 400, paint);
        } else if (dealerPlayer.position == 3) {
            if (dealerPlayer.position == 0) {
                canvas.drawText("西", left + 360, top + 680, paint);
                canvas.drawText("北", left + 80, top + 400, paint);
                canvas.drawText("东", left + 360, top + 100, paint);
                des.set(left + 580, top + 300, left + 700, top + 420);
                canvas.drawBitmap(Image, null, des, paint);
            } else if (dealerPlayer.position == 1) {
                canvas.drawText("北", left + 360, top + 680, paint);
                canvas.drawText("东", left + 80, top + 400, paint);
                canvas.drawText("南", left + 360, top + 100, paint);
                des.set(left + 580, top + 300, left + 700, top + 420);
                canvas.drawBitmap(Image, null, des, paint);
            } else if (dealerPlayer.position == 2) {
                canvas.drawText("东", left + 360, top + 680, paint);
                canvas.drawText("南", left + 80, top + 400, paint);
                canvas.drawText("西", left + 360, top + 100, paint);
                des.set(left + 580, top + 300, left + 700, top + 420);
                canvas.drawBitmap(Image, null, des, paint);
            } else if (dealerPlayer.position == 3) {
                canvas.drawText("南", left + 360, top + 680, paint);
                canvas.drawText("西", left + 80, top + 400, paint);
                canvas.drawText("北", left + 360, top + 100, paint);
                des.set(left + 580, top + 300, left + 700, top + 420);
                canvas.drawBitmap(Image, null, des, paint);
            }
//            canvas.drawText("南", left + 360, top + 680, paint);
//            canvas.drawText("西", left + 80, top + 400, paint);
//            canvas.drawText("北", left + 360, top + 100, paint);
//            des.set(left + 580, top + 300, left + 700, top + 420);
//            canvas.drawBitmap(Image, null, des, paint);
        }

        // 绘制应该出牌玩家的标志
        left = this.left + 240 * (this.modifier - 1);
        top = this.top + 360 + 180;

        paint.setColor(Color.WHITE);
        if (player == 0) {
            path.reset();
            path.moveTo(left + 360, top + 820);
            path.lineTo(left + 330, top + 740);
            path.lineTo(left + 390, top + 740);
        } else if (player == 1) {
            path.reset();
            path.moveTo(left - 100, top + 360);
            path.lineTo(left - 20, top + 330);
            path.lineTo(left - 20, top + 390);
        } else if (player == 2) {
            path.reset();
            path.moveTo(left + 360, top - 100);
            path.lineTo(left + 330, top - 20);
            path.lineTo(left + 390, top - 20);
        } else if (player == 3) {
            path.reset();
            path.moveTo(left + 820, top + 360);
            path.lineTo(left + 740, top + 330);
            path.lineTo(left + 740, top + 390);
        }

        path.close();
        canvas.drawPath(path, paint);

        // 绘制各家的出牌
        // 需要知道谁先出的牌！
        left = this.left + 240 * (this.modifier - 1);
        top = this.top + 360 + 180;

        Iterator it = dropHistory.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry entity = (Map.Entry) it.next();
            for (int i = 0; i < dropHistory.size(); i++) {
//                Image = CardImage.decodeSampledBitmapFromResource(context.getResources(), CardImage.cardImages[(Integer)entity.getValue()], 180, 240);
                Image = CardImage.cardBitmapImages.get((Integer)entity.getValue());
//                Image =  BitmapFactory.decodeResource(context.getResources(),
//                        CardImage.cardImages[(Integer)entity.getValue()]);
                if ((Integer)entity.getKey() == 0) {
                    // 绘制出牌
                    des.set(left + 275 + 50, top + 340, left + 445 + 50, top + 580);
                    canvas.drawBitmap(Image, null, des, paint);
                } else if ((Integer)entity.getKey() == 1) {
                    des.set(left + 140, top + 270, left + 310, top + 480);
                    canvas.drawBitmap(Image, null, des, paint);
                } else if ((Integer)entity.getKey() == 2) {
                    des.set(left + 275 + 50, top + 140, left + 445 + 50, top + 380);
                    canvas.drawBitmap(Image, null, des, paint);
                } else if ((Integer)entity.getKey() == 3) {
                    des.set(left + 410, top + 270, left + 580, top + 480);
                    canvas.drawBitmap(Image, null, des, paint);
                }
                Image = null;
            }
//            System.out.println("[ key = " + entity.getKey() + ", value = " + entity.getValue() + " ]");
        }
    }
}
