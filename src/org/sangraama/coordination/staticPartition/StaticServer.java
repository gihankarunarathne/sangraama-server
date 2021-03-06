package org.sangraama.coordination.staticPartition;

import org.sangraama.coordination.ServerLocation;

public class StaticServer {
    private Tile[] tiles;

    public StaticServer() {
        tiles = new Tile[2];
        this.tiles[0] = new Tile(0, "localhost", 8080, "sangraama-server", 7911, 0, 0);
        this.tiles[1] = new Tile(1, "localhost", 8081, "sangraama-server-clone", 7912, 1000, 0);
    }

    public ServerLocation getServerLocation(float x, float y) {
        ServerLocation serverLoc = null;
        for (int i = 0; i < this.tiles.length; i++) {
            if (this.tiles[i].isInTile(x, y)) {
                serverLoc = this.tiles[i].getServerLocation(i);
            }
        }
        return serverLoc;
    }

    public ServerLocation getThriftServerLocation(float x, float y) {
        ServerLocation serverLoc = null;
        for (int i = 0; i < this.tiles.length; i++) { // check in every tile
            if (this.tiles[i].isInTile(x, y)) {
                serverLoc = this.tiles[i].getThriftServerLocation(i);
            }
        }
        return serverLoc;
    }

    private class Tile {
        int tileID;
        String URL;
        private int serverPort = 0;
        private String dir;
        private int thriftPort = 0;
        float originX;
        float originY;
        float width;
        float height;

        public Tile(int tileID, String URL, int port, String dir, int thriftPort, float originX,
                float originY) {
            this.tileID = tileID;
            this.URL = URL;
            this.serverPort = port;
            this.dir = dir;
            this.thriftPort = thriftPort;
            this.originX = originX;
            this.originY = originY;
            this.width = 1000f;
            this.height = 1000f;
        }

        @SuppressWarnings("unused")
        public Tile(int tileID, String URL, int port, String dir, int thriftPort, float originX,
                float originY, float width, float height) {
            this.tileID = tileID;
            this.URL = URL;
            this.serverPort = port;
            this.dir = dir;
            this.thriftPort = thriftPort;
            this.originX = originX;
            this.originY = originY;
            this.width = width;
            this.height = height;
        }

        public boolean isInTile(float x, float y) {

            if (originX < x && x < originX + width && originY < y && y < originY + height) {
                return true;
            } else {
                return false;
            }
        }

        public ServerLocation getServerLocation(int tileID) {
            if (this.tileID == tileID) {
                System.out.println(" Get server loc :" + " port:" + this.serverPort);
                return new ServerLocation(this.URL, this.serverPort, this.dir);
            } else {
                return null;
            }
        }

        public ServerLocation getThriftServerLocation(int tileID) {
            if (this.tileID == tileID) {
                System.out.println(" Get thrift server loc :" + " port:" + this.thriftPort);
                return new ServerLocation(this.URL, this.thriftPort);
            } else {
                return null;
            }
        }
    }
}
