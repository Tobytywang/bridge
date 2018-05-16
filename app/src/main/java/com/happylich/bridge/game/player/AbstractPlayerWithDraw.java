package com.happylich.bridge.game.player;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.happylich.bridge.engine.util.Position;
import com.happylich.bridge.game.res.CardImage;

/**
 * Created by lich on 2018/5/16.
 */

public class AbstractPlayerWithDraw extends AbstractPlayer {

    @Override
    public boolean callCard() {
        return false;
    }

    @Override
    public boolean dropCard() {
        return false;
    }

    /**
     * 绘图函数
     * @param canvas
     */
    public void draw(Canvas canvas, Paint paint, Rect rect) {
        switch(playerStage) {
            case 201:
                paintBottomUp(canvas, paint, rect);
                break;
            case 202:
                paintBottomDown(canvas, paint, rect);
                break;
            case 211:
                paintLeftUp(canvas, paint, rect);
                break;
            case 212:
                paintLeftDown(canvas, paint, rect);
                break;
            case 221:
                paintTopUp(canvas, paint, rect);
                break;
            case 222:
                paintTopDown(canvas, paint, rect);
                break;
            case 231:
                paintRightUp(canvas, paint, rect);
                break;
            case 232:
                paintRightDown(canvas, paint, rect);
                break;
            // do-nothing
        }
    }

    /**
     * 南家触摸事件
     * @param x
     * @param y
     * @return
     */
    public int touchBottom(int x, int y) {

        // 虽然规定了left，但是并不采用，实际情况下还是根据width重新绘制
        int left = (1440 - (cards.size() - 1) * 90 - 180) / 2;
        int top = this.top;

        for (int i=0; i<cards.size(); i++) {

            if (selectCard != -1) {
                if (i < cards.size() - 1) {
                    position1.set(top, left + i * 90,
                            top + 240, left + 90 + i * 90);
                } else {
                    position1.set(top, left + i * 90,
                            top + 240, left + 180 + i * 90);
                }
                position1.resieze((float)this.width / (float)1440);

                positionSelected1.set(top - 120, left + selectCardIndex * 90,
                        top, left + 180 + selectCardIndex * 90);
                positionSelected1.resieze((float)this.width / (float)1440);

                if (selectCardIndex == cards.size() - 1) {
                    positionSelected2.set(top, left + selectCardIndex * 90,
                            top + 120, left + 180 + selectCardIndex * 90);
                    positionSelected2.resieze((float) this.width / (float) 1440);
                } else {
                    positionSelected2.set(top, left + selectCardIndex * 90,
                            top + 120, left + 90 + selectCardIndex * 90);
                    positionSelected2.resieze((float) this.width / (float) 1440);
                }

                if (Position.inPosition(x, y, positionSelected1) || Position.inPosition(x, y, positionSelected2)) {
                    table.setDrop(this.position, cards.remove(selectCardIndex));
                    selectCardIndex = -1;
                    selectCard = -1;
                    return 2;
                } else if (Position.inPosition(x, y, position1)) {
                    selectCardIndex = i;
                    selectCard = cards.get(i);
                    return 1;
                }
            } else {
                if (i < cards.size() - 1) {
                    position1.set(top, left + i * 90,
                            top + 240, left + 90 + i * 90);
                } else {
                    position1.set(top, left + i * 90,
                            top + 240, left + 180 + i * 90);
                }
                position1.resieze((float)this.width / (float)1440);
                if (Position.inPosition(x, y, position1)) {
                    // 选中牌
                    selectCardIndex = i;
                    selectCard = cards.get(i);
                    return 1;
                }
            }
        }
        return 0;
    }

    /**
     * 北家触摸事件
     * @param x
     * @param y
     * @return
     */
    public int touchTop(int x, int y) {

        // 虽然规定了left，但是并不采用，实际情况下还是根据width重新绘制
        int left = (1440 - (cards.size() - 1) * 80 - 180) / 2;
        int top = this.top;

        for (int i=0; i < cards.size(); i++) {

            if (selectCard != -1) {
                if (i < cards.size() - 1) {
                    position1.set(top, left + i * 80,
                            top + 240, left + 90 + i * 80);
                } else {
                    position1.set(top, left + i * 80,
                            top + 240, left + 180 + i * 80);
                }
                position1.resieze((float)this.width / (float)1440);

                positionSelected1.set(top + 240, left + selectCardIndex * 80,
                        top + 360, left + 180 + selectCardIndex * 80);
                positionSelected1.resieze((float)this.width / (float)1440);

                if (selectCardIndex == cards.size() - 1) {
                    positionSelected2.set(top + 120, left + selectCardIndex * 80,
                            top + 240, left + 180 + selectCardIndex * 80);
                    positionSelected2.resieze((float) this.width / (float) 1440);
                } else {
                    positionSelected2.set(top + 120, left + selectCardIndex * 80,
                            top + 240, left + 80 + selectCardIndex * 80);
                    positionSelected2.resieze((float) this.width / (float) 1440);
                }

                if (Position.inPosition(x, y, positionSelected1) || Position.inPosition(x, y, positionSelected2)) {
                    table.setDrop(this.position, cards.remove(selectCardIndex));
                    selectCardIndex = -1;
                    selectCard = -1;
                    return 2;
                } else if (Position.inPosition(x, y, position1)) {
                    selectCardIndex = i;
                    selectCard = cards.get(i);
                    return 1;
                }
            } else {
                if (i < cards.size() - 1) {
                    position1.set(top, left + i * 80,
                            top + 240, left + 90 + i * 80);
                } else {
                    position1.set(top, left + i * 90,
                            top + 240, left + 180 + i * 80);
                }
                position1.resieze((float)this.width / (float)1440);
                if (Position.inPosition(x, y, position1)) {
                    selectCardIndex = i;
                    selectCard = cards.get(i);
                    return 1;
                }
            }
        }
        return 0;
    }


    /**
     * 南家绘制
     * @param canvas
     */
    private void paintBottomUp(Canvas canvas, Paint paint, Rect des) {
        Bitmap Image;
        int left = (1440 - (cards.size() - 1) * 90 - 180) / 2;
        int top = this.top;

        for (int i=0; i<cards.size(); i++) {
            Image = CardImage.cardBitmapImages.get(cards.get(i));
            if ((selectCard != -1) && (cards.get(i) == selectCard)) {
                des.set(left + i * 90, top - 120, left + 180 + i * 90, top + 120);
            } else {
                des.set(left + i * 90, top, left + 180 + i * 90, top + 240);
            }
            canvas.drawBitmap(Image,null, des, paint);
            Image = null;
        }
    }

    /**
     * 南家绘制
     * @param canvas
     */
    private void paintBottomDown(Canvas canvas, Paint paint, Rect des) {
        Bitmap Image;
        int left = (1440 - (cards.size() - 1) * 90 - 180) / 2;
        int top = this.top;

        for (int i=0; i<cards.size(); i++) {
            Image = CardImage.backBitmapImage;
            des.set(left + i * 90, top, left + 180 + i * 90, top + 240);
            canvas.drawBitmap(Image,null, des, paint);
            Image = null;
        }
    }

    /**
     * 北家绘制（明）
     * @param canvas
     */
    private void paintTopUp(Canvas canvas, Paint paint, Rect des) {
        Bitmap Image;

        int left = (1440 - (cards.size() - 1) * 80 - 180) / 2;
        int top = this.top;

        // 绘制纸牌（底部玩家）
        for (int i=0; i<cards.size(); i++) {
            Image = CardImage.cardBitmapImages.get(cards.get(i));
            if ((selectCard != -1) && (cards.get(i) == selectCard)) {
                des.set(left + i * 80, top + 120, left + 180 + i * 80, top + 360);
            } else {
                des.set(left + i * 80, top, left + 180 + i * 80, top + 240);
            }
            canvas.drawBitmap(Image,null, des, paint);
            Image = null;
        }
    }

    /**
     * 北家绘制（暗）
     * @param canvas
     */
    private void paintTopDown(Canvas canvas, Paint paint, Rect des) {
        Bitmap Image;

        int left = (1440 - (cards.size() - 1) * 80 - 180) / 2;

        for (int i=0; i<cards.size(); i++) {
            Image = CardImage.backBitmapImage;
            des.set(left + i * 80, top, left + 180 + i * 80, top + 240);
            canvas.drawBitmap(Image,null, des, paint);
            Image = null;
        }
    }

    /**
     * 西家绘制（明）
     * @param canvas
     */
    private void paintLeftUp(Canvas canvas, Paint paint, Rect des) {
        Bitmap Image;

        // 虽然规定了left，但是并不采用，实际情况下还是根据width重新绘制
        int left = this.left;
        int top = this.top;

        int sflag = 0;
        int hflag = 0;
        int dflag = 0;
        int cflag = 0;

        int sFull = -1;
        int hFull = -1;
        int dFull = -1;
        int cFull = -1;

        for (int i=0; i<cards.size(); i++) {

            Image = CardImage.cardBitmapImages.get(cards.get(i));
            if (cards.get(i) >= 39 && cards.get(i) <= 51) {
                sFull++;
                sflag = 1;
            } else if (cards.get(i) >= 26 && cards.get(i) <= 38) {
                hFull++;
                hflag = 1;
            } else if (cards.get(i) >= 13 && cards.get(i) <= 25) {
                dFull++;
                dflag = 1;
            } else if (cards.get(i) >= 0 && cards.get(i) <= 12) {
                cFull++;
                cflag = 1;
            }

            if (cards.get(i) >= 39 && cards.get(i) <= 51) {
                des.set(left + 60 * sFull, top + (sflag + hflag + dflag + cflag) * 180,
                        left + 180 + 60 * sFull, top + 240 + (sflag + hflag + dflag + cflag) * 180);

            } else if (cards.get(i) >= 26 && cards.get(i) <= 38) {
                des.set(left + 60 * hFull, top + (sflag + hflag + dflag + cflag) * 180,
                        left + 180 + 60 * hFull, top + 240 + (sflag + hflag + dflag + cflag) * 180);

            } else if (cards.get(i) >= 13 && cards.get(i) <= 25) {
                des.set(left + 60 * dFull, top + (sflag + hflag + dflag + cflag) * 180,
                        left + 180 + 60 * dFull, top + 240 + (sflag + hflag + dflag + cflag) * 180);

            } else if (cards.get(i) >= 0 && cards.get(i) <= 12) {
                des.set(left + 60 * cFull, top + (sflag + hflag + dflag + cflag) * 180,
                        left + 180 + 60 * cFull, top + 240 + (sflag + hflag + dflag + cflag) * 180);

            }
            canvas.drawBitmap(Image,null, des, paint);
            Image = null;
        }
    }

    private void paintLeftDown(Canvas canvas, Paint paint, Rect des) {

        Bitmap Image;

        int left = this.left;
        int top = this.top;

        int sflag = 0;
        int hflag = 0;
        int dflag = 0;
        int cflag = 0;

        int sFull = -1;
        int hFull = -1;
        int dFull = -1;
        int cFull = -1;

        for (int i=0; i<cards.size(); i++) {

            Image = CardImage.backBitmapImage;
            if (cards.get(i) >= 39 && cards.get(i) <= 51) {
                sFull++;
                sflag = 1;
            } else if (cards.get(i) >= 26 && cards.get(i) <= 38) {
                hFull++;
                hflag = 1;
            } else if (cards.get(i) >= 13 && cards.get(i) <= 25) {
                dFull++;
                dflag = 1;
            } else if (cards.get(i) >= 0 && cards.get(i) <= 12) {
                cFull++;
                cflag = 1;
            }

            if (cards.get(i) >= 39 && cards.get(i) <= 51) {
                des.set(left + 60 * sFull, top + (sflag + hflag + dflag + cflag) * 180,
                        left + 180 + 60 * sFull, top + 240 + (sflag + hflag + dflag + cflag) * 180);

            } else if (cards.get(i) >= 26 && cards.get(i) <= 38) {
                des.set(left + 60 * hFull, top + (sflag + hflag + dflag + cflag) * 180,
                        left + 180 + 60 * hFull, top + 240 + (sflag + hflag + dflag + cflag) * 180);

            } else if (cards.get(i) >= 13 && cards.get(i) <= 25) {
                des.set(left + 60 * dFull, top + (sflag + hflag + dflag + cflag) * 180,
                        left + 180 + 60 * dFull, top + 240 + (sflag + hflag + dflag + cflag) * 180);

            } else if (cards.get(i) >= 0 && cards.get(i) <= 12) {
                des.set(left + 60 * cFull, top + (sflag + hflag + dflag + cflag) * 180,
                        left + 180 + 60 * cFull, top + 240 + (sflag + hflag + dflag + cflag) * 180);

            }
            canvas.drawBitmap(Image,null, des, paint);
            Image = null;
        }
    }
    /**
     * 东家绘制（明）
     * @param canvas
     */
    private void paintRightUp(Canvas canvas, Paint paint, Rect des) {
        Bitmap Image;

        int left = this.left;
        int top = this.top;

        int sflag = 0;
        int hflag = 0;
        int dflag = 0;
        int cflag = 0;

        int sFull = -1;
        int hFull = -1;
        int dFull = -1;
        int cFull = -1;

        for (int i=0; i<cards.size(); i++) {
            if (cards.get(i) >= 39 && cards.get(i) <= 51) {
                sFull++;
            } else if (cards.get(i) >= 26 && cards.get(i) <= 38) {
                hFull++;
            } else if (cards.get(i) >= 13 && cards.get(i) <= 25) {
                dFull++;
            } else if (cards.get(i) >= 0 && cards.get(i) <= 12) {
                cFull++;
            }
        }

        for (int i=0; i<cards.size(); i++) {
            if (cards.get(i) >= 39 && cards.get(i) <= 51) {
                sflag = 1;
            } else if (cards.get(i) >= 26 && cards.get(i) <= 38) {
                hflag = 1;
            } else if (cards.get(i) >= 13 && cards.get(i) <= 25) {
                dflag = 1;
            } else if (cards.get(i) >= 0 && cards.get(i) <= 12) {
                cflag = 1;
            }

            Image = CardImage.cardBitmapImages.get(cards.get(i));
            if (cards.get(i) >= 39 && cards.get(i) <= 51) {
                des.set(left - 180 - 60 * sFull, top + (sflag + hflag + dflag + cflag) * 180,
                        left - 60 * sFull, top + 240 + (sflag + hflag + dflag + cflag) * 180);
                sFull--;
            } else if (cards.get(i) >= 26 && cards.get(i) <= 38) {
                des.set(left - 180 - 60 * hFull, top + (sflag + hflag + dflag + cflag) * 180,
                        left - 60 * hFull, top + 240 + (sflag + hflag + dflag + cflag) * 180);
                hFull--;
            } else if (cards.get(i) >= 13 && cards.get(i) <= 25) {
                des.set(left - 180 - 60 * dFull, top + (sflag + hflag + dflag + cflag) * 180,
                        left - 60 * dFull, top + 240 + (sflag + hflag + dflag + cflag) * 180);
                dFull--;
            } else if (cards.get(i) >= 0 && cards.get(i) <= 12) {
                des.set(left - 180 - 60 * cFull, top + (sflag + hflag + dflag + cflag) * 180,
                        left - 60 * cFull, top + 240 + (sflag + hflag + dflag + cflag) * 180);
                cFull--;
            }
            canvas.drawBitmap(Image,null, des, paint);
            Image = null;
        }
    }

    private void paintRightDown(Canvas canvas, Paint paint, Rect des) {

        Bitmap Image;

        int left = this.left;
        int top = this.top;

        int sflag = 0;
        int hflag = 0;
        int dflag = 0;
        int cflag = 0;

        int sFull = -1;
        int hFull = -1;
        int dFull = -1;
        int cFull = -1;

        for (int i=0; i<cards.size(); i++) {
            if (cards.get(i) >= 39 && cards.get(i) <= 51) {
                sFull++;
            } else if (cards.get(i) >= 26 && cards.get(i) <= 38) {
                hFull++;
            } else if (cards.get(i) >= 13 && cards.get(i) <= 25) {
                dFull++;
            } else if (cards.get(i) >= 0 && cards.get(i) <= 12) {
                cFull++;
            }
        }

        for (int i=0; i<cards.size(); i++) {
            if (cards.get(i) >= 39 && cards.get(i) <= 51) {
                sflag = 1;
            } else if (cards.get(i) >= 26 && cards.get(i) <= 38) {
                hflag = 1;
            } else if (cards.get(i) >= 13 && cards.get(i) <= 25) {
                dflag = 1;
            } else if (cards.get(i) >= 0 && cards.get(i) <= 12) {
                cflag = 1;
            }

            Image = CardImage.backBitmapImage;
            if (cards.get(i) >= 39 && cards.get(i) <= 51) {
                des.set(left - 180 - 60 * sFull, top + (sflag + hflag + dflag + cflag) * 180,
                        left - 60 * sFull, top + 240 + (sflag + hflag + dflag + cflag) * 180);
                sFull--;
            } else if (cards.get(i) >= 26 && cards.get(i) <= 38) {
                des.set(left - 180 - 60 * hFull, top + (sflag + hflag + dflag + cflag) * 180,
                        left - 60 * hFull, top + 240 + (sflag + hflag + dflag + cflag) * 180);
                hFull--;
            } else if (cards.get(i) >= 13 && cards.get(i) <= 25) {
                des.set(left - 180 - 60 * dFull, top + (sflag + hflag + dflag + cflag) * 180,
                        left - 60 * dFull, top + 240 + (sflag + hflag + dflag + cflag) * 180);
                dFull--;
            } else if (cards.get(i) >= 0 && cards.get(i) <= 12) {
                des.set(left - 180 - 60 * cFull, top + (sflag + hflag + dflag + cflag) * 180,
                        left - 60 * cFull, top + 240 + (sflag + hflag + dflag + cflag) * 180);
                cFull--;
            }
            canvas.drawBitmap(Image,null, des, paint);
            Image = null;
        }
    }

}
