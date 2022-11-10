package server;


import java.util.Date;
import java.util.List;
import java.util.Vector;

public class Room {

    public RoomManager roomManager;
    public int no;
    public String title;
    public List<SocketClient> clients;
    private Date CreateDate;
    
    public Room(
            RoomManager roomManager,
            int no,
            String title ) {
        this.roomManager = roomManager;
        this.no = no;
        this.title = title;
        clients = new Vector<>();
    }

    public Room(
            RoomManager roomManager,
            int no,
            String title,
            Date CreateDate) {
        this.roomManager = roomManager;
        this.no = no;
        this.title = title;
        this.CreateDate=CreateDate;
        clients = new Vector<>();
    }
    

    public void entryRoom(SocketClient client) {
		roomManager.roomRecord.put(client.clientUid, this);
    }

   
    public void leaveRoom(SocketClient client) {
        client.room = null;
        
        /*
        if(this.clients.size() < 1) {
            roomManager.destroyRoom(this);
        }
        */
    }
}
