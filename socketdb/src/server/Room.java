package server;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class Room {

    public RoomManager roomManager;
    public int no;
    public String title;
    public List<SocketClient> clients;
    public List<String> cluid;
    String CreateDate;
    SimpleDateFormat dateFormat;
    
    public Room(
            RoomManager roomManager,
            int no,
            String title,
            Date CreateDate) {
        this.roomManager = roomManager;
        this.no = no;
        this.title = title;
        
        dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        this.CreateDate= dateFormat.format(CreateDate);
        clients = new Vector<>();
        cluid = new Vector<>();
        
    }
    public Room(
            RoomManager roomManager,
            int no,
            String title) {
        this.roomManager = roomManager;
        this.no = no;
        this.title = title;
        
        clients = new Vector<>();
        cluid = new Vector<>();
        dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date now = new Date();
        this.CreateDate= dateFormat.format(now);
        
    }
    

    public void entryRoom(SocketClient client) {
		roomManager.roomRecord.put(client.clientUid, this);
		cluid.add(client.clientUid);
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
