package com.nekol.controller;

import com.nekol.dao.UserDAO;
import java.io.IOException;
import lombok.Data;

@Data
public class Room {

    private int id;
    private ServerThread user1;
    private ServerThread user2;
    private String password;
    private UserDAO userDAO;

    public Room(ServerThread user1) {
        System.out.println("Tạo phòng thành công, ID là: " + Server.ID_room);
        this.password = " ";
        this.id = Server.ID_room++;
        userDAO = new UserDAO();
        this.user1 = user1;
        this.user2 = null;
    }

    public int getNumberOfUser() {
        return user2 == null ? 1 : 2;
    }

    public void boardCast(String message) {
        try {
            user1.write(message);
            user2.write(message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public int getCompetitorId(int idClientBumber) {
        if (user1.getClientNumber() == idClientBumber) {
            return user2.getUser().getId();
        }
        return user1.getUser().getId();
    }

    public ServerThread getCompetitor(int idClientNumber) {
        if (user1.getClientNumber() == idClientNumber) {
            return user2;
        }
        return user1;
    }

    public void setUsersToPlaying() {
        userDAO.updateToPlaying(user1.getUser().getId());
        if (user2 != null) {
            userDAO.updateToPlaying(user2.getUser().getId());
        }
    }

    public void setUsersToNotPlaying() {
        userDAO.updateToNotPlaying(user1.getUser().getId());
        if (user2 != null) {
            userDAO.updateToNotPlaying(user2.getUser().getId());
        }
    }

    public void increaseNumberOfGame() {
        userDAO.addGame(user1.getUser().getId());
        userDAO.addGame(user2.getUser().getId());
    }

    public void increaseNumberOfDraw() {
        userDAO.addDrawGame(user1.getUser().getId());
        userDAO.addDrawGame(user2.getUser().getId());
    }

    public void decreaseNumberOfGame() {
        userDAO.decreaseGame(user1.getUser().getId());
        userDAO.decreaseGame(user2.getUser().getId());
    }

}
