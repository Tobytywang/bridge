package com.happylich.bridge.game.wlan.wifihotspot.transmitdata;

import android.os.Message;
import android.widget.Toast;

import com.happylich.bridge.game.main.Game;
import com.happylich.bridge.game.player.Player;
import com.happylich.bridge.game.player.ProxyPlayer;
import com.happylich.bridge.game.player.RemotePlayer;
import com.happylich.bridge.game.player.Robot;
import com.happylich.bridge.game.wlan.wifihotspot.validconnection.GameServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by lich on 2018/5/24.
 *
 * 这个类的主要功能是：
 * 1. 发送所有玩家就绪状态
 * 2. 发送所有玩家手牌
 * 3. 发送所有玩家位置
 * 4. 发送所有玩家
 */

// 如果是服务端发送，需要向所有客户端发送消息
// 如果是客户端发送，需要向服务端发送，并向所有客户端转发
// 如果是客户端接收，则消费
// 如果是服务器接收，需要向客户端转发
// 总的说来，在包含N个主机的系统中，每条消息都需要被N-1个主机消费。
// 消息的组成："IP players xxx"
// 发送消息的主机IP、消息类型、消息内容

public class GameTransmitData {
    Socket s =null;
    OutputStream mOutputStream;
    Game game;

    boolean running = true;

    public void setRunning(boolean running) {
        this.running = running;
    }

    public GameTransmitData(Game game){
        this.game = game;
        // 初始化该Socket对应的输入流
    }

    /**
     *
     * @param messageType 012
     * @param message players
     */
    public void sendToClients(int messageType, String message) {
        message = transform(messageType, message);
        Toast.makeText(this.game.context, message, Toast.LENGTH_SHORT).show();
//        String[] messages = message.split(" ");
//        String ip = messages[0];
//        String content = messages[1];

        // 发送给本机？
//        Message message1 = new Message();
//        message1.what = messageType;
//        message1.obj = message;
//        game.mHandler.sendMessage(message1);
//        String[] messages = message.split(">");
//        Message message1 = new Message();
//        message1.what = Integer.parseInt(messages[0]);
//        message1.obj = messages[1];
//        game.mHandler.sendMessage(message1);

        // 发送给其他玩家
        // 需要区分客户端和服务端
        if (this.game.getGameType() == 2) {
            Toast.makeText(this.game.context, "向所有客户端广播", Toast.LENGTH_SHORT).show();
            if (this.game.sockets != null && this.game.sockets instanceof ArrayList && this.game.sockets.size() <10) {
                Toast.makeText(this.game.context, String.valueOf(this.game.sockets.size()), Toast.LENGTH_SHORT).show();
            }
            // 服务器
            for (Socket sokt : game.sockets) {
                try {
                    if (sokt != null) {
                        mOutputStream = sokt.getOutputStream();
                        mOutputStream.write((message+"\n").getBytes("utf-8"));
                        mOutputStream.flush();
                    }
                } catch (Exception e) {
                    try {
                        sokt.close();
                        game.sockets.remove(sokt);
                    } catch(Exception exception) {

                    }
                    Toast.makeText(this.game.context, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        } else if (this.game.getGameType() == 3){
            Toast.makeText(this.game.context, "向服务端广播", Toast.LENGTH_SHORT).show();
            // 客户端
            try {
                mOutputStream = this.game.socket.getOutputStream();
                mOutputStream.write((message+"\n").getBytes("utf-8"));
                mOutputStream.flush();
            } catch (Exception e) {
                Toast.makeText(this.game.context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 发送的时候需要知道type
    private String transform(int messageType, String message) {
        String tmp =  String.valueOf(messageType) + ">" + message + "=" + this.game.getServerIP() + ":";
        switch (message) {
            case "players":
                // 将当前服务器玩家状态表示成字符串
                if (this.game.getPlayerBottom() instanceof Player) {
                    if (this.game.getPlayerBottom().isInOrder()) {
                        tmp += this.game.getPlayerBottom().direction + " " + "zo ";
                    } else if(!this.game.getPlayerBottom().isInOrder()) {
                        tmp += this.game.getPlayerBottom().direction + " " + "zx ";
                    }
                }
                if (this.game.getPlayerLeft() instanceof ProxyPlayer) {
                    if (((ProxyPlayer) this.game.getPlayerLeft()).getRealPlayer() instanceof Robot) {
                        tmp += this.game.getPlayerLeft().direction + " " + "mo ";
                    } else if (((ProxyPlayer) this.game.getPlayerLeft()).getRealPlayer() instanceof RemotePlayer) {
                        if (((ProxyPlayer) this.game.getPlayerLeft()).getRealPlayer().isInOrder()) {
                            tmp += this.game.getPlayerLeft().direction + " " + "po ";
                        } else {
                            tmp += this.game.getPlayerLeft().direction + " " + "px ";
                        }
                    }
                }
                if (this.game.getPlayerTop() instanceof ProxyPlayer) {
                    if (((ProxyPlayer) this.game.getPlayerTop()).getRealPlayer() instanceof Robot) {
                        tmp += this.game.getPlayerTop().direction + " " + "mo ";
                    } else if (((ProxyPlayer) this.game.getPlayerTop()).getRealPlayer() instanceof RemotePlayer) {
                        if (((ProxyPlayer) this.game.getPlayerTop()).getRealPlayer().isInOrder()) {
                            tmp += this.game.getPlayerTop().direction + " " + "po ";
                        } else {
                            tmp += this.game.getPlayerTop().direction + " " + "px ";
                        }
                    }
                }
                if (this.game.getPlayerRight() instanceof ProxyPlayer) {
                    if (((ProxyPlayer) this.game.getPlayerRight()).getRealPlayer() instanceof Robot) {
                        tmp += this.game.getPlayerRight().direction + " " + "mo ";
                    } else if (((ProxyPlayer) this.game.getPlayerRight()).getRealPlayer() instanceof RemotePlayer) {
                        if (((ProxyPlayer) this.game.getPlayerRight()).getRealPlayer().isInOrder()) {
                            tmp += this.game.getPlayerRight().direction + " " + "po ";
                        } else {
                            tmp += this.game.getPlayerRight().direction + " " + "px ";
                        }
                    }
                }
                break;
        }
        return tmp;
    }
}
