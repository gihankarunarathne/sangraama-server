package org.sangraama.controller;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;
import org.sangraama.assets.DummyPlayer;
import org.sangraama.assets.Player;
import org.sangraama.assets.Ship;
import org.sangraama.controller.clientprotocol.ClientEvent;
import org.sangraama.controller.clientprotocol.ClientTransferReq;
import org.sangraama.controller.clientprotocol.PlayerDelta;
import org.sangraama.controller.clientprotocol.TileInfo;
import org.sangraama.controller.clientprotocol.TransferInfo;
import org.sangraama.util.VerifyMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class WebSocketConnection extends MessageInbound {
    // Local Debug or logs
    private static boolean LL = true;
    private static boolean LD = true;
    private static final String TAG = "WebSocketConnection : ";
    public static final Logger log = LoggerFactory.getLogger(WebSocketConnection.class);

    private Player player = null;
    private Gson gson;
    private DummyPlayer dPlayer = null;

    public WebSocketConnection() {
        this.gson = new Gson();
    }

    /**
     * Set the player who is own this web socket connection
     * 
     * @param ship
     *            the instance of player which is connect to client
     */
    public void setPlayer(Ship player) {
        if (this.dPlayer != null) {
            this.dPlayer.removeWebSocketConnection();
            this.dPlayer = null;
        }
        this.player = player;
        System.out.println(TAG + " created a PLAYER conection...");
    }

    public void setDummyPlayer(DummyPlayer dPlayer) {
        if (this.player != null) {
            this.player.removeWebSocketConnection();
            this.player = null;
        }
        this.dPlayer = dPlayer;
        System.out.println(TAG + " created a DUMMY PLAYER conection...");
    }

    @Override
    protected void onOpen(WsOutbound outbound) {
        // log.info("Open Connection");
        System.out.println(TAG + " Open Connection");
    }

    @Override
    protected void onClose(int status) {
        // log.info("Connection closed");
        
        if (this.player != null) {
            this.player.removeWebSocketConnection();
        }
        if (this.dPlayer != null) {
            this.dPlayer.removeWebSocketConnection();
        }
        System.out.println(TAG + " Close connection");
    }

    @Override
    protected void onBinaryMessage(ByteBuffer byteBuffer) throws IOException {
        // log.warn("binary messages are not supported");
        System.out.println("Binary");
        throw new UnsupportedOperationException("not supported binary messages");
    }

    @Override
    protected void onTextMessage(CharBuffer charBuffer) throws IOException {
        String user = charBuffer.toString();
        ClientEvent event = gson.fromJson(user, ClientEvent.class);

        if (this.player != null) {
            this.playerEvents(event);
        } else if (this.dPlayer != null) {
            this.dummyPlayerEvents(event);
        } else {
            this.newPlayerEvent(event);
        }
    }

    private void newPlayerEvent(ClientEvent event) {
        String T = " newPlayerEvent ";
        switch (Integer.parseInt(event.getType())) {
            case 1:// create new player & set the connection
                this.setPlayer(new Ship(event.getUserID(), event.getX(), event.getY(),event.getW(),event.getH(), this));
                System.out.println(TAG + T + " Add new Player " + event.toString());
                this.player.setV(event.getV_x(), event.getV_y());
                this.player.setAngle(event.getV_a());
                // this.player.shoot(clientEvent.getS());
                System.out.println(TAG + T + " set user events " + event.getV_x() + " : "
                        + event.getV_y() + " when creating player");
                break;
            case 2:
                TransferInfo playerInfo;
                String info = event.getInfo();
                byte[] signedInfo = event.getSignedInfo();
                boolean msgVerification = VerifyMsg.INSTANCE.verifyMessage(info, signedInfo);
                if (msgVerification) {
                    playerInfo = gson.fromJson(info, TransferInfo.class);
                    //to be add w and h
                    this.player = new Ship(event.getUserID(), playerInfo.getPositionX(),
                            playerInfo.getPositionY(),0,0, this);
                    System.out
                            .println(TAG + T + "Adding player from another server to GameEngine.");
                }
                break;
            // Create a dummy player and set AOI of the player
            case 3:
                //to be add w and h
                this.setDummyPlayer(new DummyPlayer(event.getUserID(), event.getX(), event.getY(),0,0,
                        this));
                this.dPlayer.setAOI(event.getAoi_w(), event.getAoi_h());
                System.out.println(TAG + T + " set AOI of player: " + event.getUserID());
                break;

            default:
                break;
        }
    }

    private void playerEvents(ClientEvent event) {
        String T = " playerevent ";
        switch (Integer.parseInt(event.getType())) {
            case 1: // setting user event request
                this.player.setV(event.getV_x(), event.getV_y());
                this.player.setAngle(event.getV_a());
                this.player.shoot(event.getS());
                System.out.println(TAG + T + " set user events " + event.getV_x() + " : "
                        + event.getV_y());
                break;
            case 2: // requesting for interesting area
                this.player.reqInterestIn(event.getX(), event.getY());
                System.out.println(TAG + T + "player interesting in x:" + event.getX() + " & y:"
                        + event.getY());
                break;
            // set AOI of the player
            case 3:
                this.player.setAOI(event.getAoi_w(), event.getAoi_h());
                System.out.println(TAG + T + " set AOI of player: " + event.getUserID());
                break;
            case 4: // Reset settings and make dummy player
                this.player.setV(event.getV_x(), event.getV_y());
                this.player.setAngle(event.getV_a());
                this.player.shoot(event.getS());
                System.out.println(TAG + T + " RESET user events " + event.getV_x() + " : "
                        + event.getV_y());
                //to be add w and h
                this.setDummyPlayer(new DummyPlayer(event.getUserID(), event.getX(), event.getY(),0,0,
                        this));
                this.dPlayer.setAOI(event.getAoi_w(), event.getAoi_h());
                this.player = null;
                break;

            default:
                break;
        }
    }

    private void dummyPlayerEvents(ClientEvent event) {
        String T = " dummyPlayerEvent ";
        switch (Integer.parseInt(event.getType())) {
            case 1: // create new player and pass the connection
                //to be add w and h
                this.setPlayer(new Ship(event.getUserID(), event.getX(), event.getY(),0,0, this));
                System.out.println(TAG + T + " changed to Player " + event.getUserID());
                this.player.setV(event.getV_x(), event.getV_y());
                this.player.setAngle(event.getV_a());
                // this.player.shoot(clientEvent.getS());
                System.out.println(TAG + T + " set user events " + event.getV_x() + " : "
                        + event.getV_y() + " when creating player");
                // Unset dummy player
                this.dPlayer = null;
                break;
            case 2: // requesting for interesting area
                this.dPlayer.reqInterestIn(event.getX(), event.getY());
                System.out.println(TAG + T + "player interesting in x:" + event.getX() + " & y:"
                        + event.getY());
                break;
            // set AOI of the player
            case 3:
                this.dPlayer.setAOI(event.getAoi_w(), event.getAoi_h());
                System.out.println(TAG + T + " set AOI of player: " + event.getUserID());
                break;

            default:
                break;
        }
    }

    /**
     * Send new updates of players states to the particular client
     * 
     * @param playerDeltaList
     *            delta updates of players who are located inside AOI
     */
    public void sendUpdate(List<PlayerDelta> playerDeltaList) {
        try {
            String convertedString = gson.toJson(playerDeltaList);
            getWsOutbound().writeTextMessage(CharBuffer.wrap(convertedString));

        } catch (IOException e) {
            System.out.println(TAG + " Unable to send update ");
            e.printStackTrace();
            log.error(TAG, e);
        }
    }

    /**
     * Send new connection details as a list. Because updates are send as a list, sending new single
     * connection details can't recognize by client side.
     * 
     * @param transferReq
     *            details about new connection server ArrayList<ClientTransferReq>
     */
    public void sendNewConnection(ArrayList<ClientTransferReq> transferReq) {
        try {
            getWsOutbound().writeTextMessage(CharBuffer.wrap(gson.toJson(transferReq)));
            //System.out.println(TAG + " new con details " + gson.toJson(transferReq));
        } catch (IOException e) {
            System.out.println(TAG + " Unable to send new connnection information");
            log.error(TAG, e);
        }
    }

    /**
     * Send coordination details about tile size on this server
     * 
     * @param tilesInfo
     *            ArrayList of details about tile of current server
     */
    public void sendTileSizeInfo(ArrayList<TileInfo> tilesInfo) {
        try {
            getWsOutbound().writeTextMessage(CharBuffer.wrap(gson.toJson(tilesInfo)));
            //System.out.println(TAG + " send size of tile " + gson.toJson(tilesInfo));
        } catch (IOException e) {
            System.out.println(TAG + " Unable to send tile size information");
            log.error(TAG, e);
        }
    }

    /**
     * Send coordination detail about tile
     * 
     * @param tileInfo
     *            details about tile
     */
    public void sendTileSizeInfo(TileInfo tileInfo) {
        ArrayList<TileInfo> tilesInfo = new ArrayList<TileInfo>();
        tilesInfo.add(tileInfo);
        this.sendTileSizeInfo(tilesInfo);
    }

    /**
     * Close the WebSocket connection of the player
     * 
     * @return null
     */
    public void closeConnection() {
        try {
            getWsOutbound().flush();
            getWsOutbound().close(1, null);
        } catch (IOException e) {
            System.out.println(TAG + " Unable to close connnection ");
            log.error(TAG, e);
        }
    }
}