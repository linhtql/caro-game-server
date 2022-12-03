package com.nekol.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    
    private int id;
    private String username;
    private String password;
    private String avatar;
    private int numberOfGame;
    private int numberOfWin;
    private int numberOfDraw;
    private boolean isOnline;
    private boolean isPlaying;
    private int rank;
    
    public User(int id, boolean isOnline, boolean isPlaying) {
        this.id = id;
        this.isOnline = isOnline;
        this.isPlaying = isPlaying;
    }
    
        public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
        
        public User(String username, String password, String avatar) {
        this.username = username;
        this.password = password;
        this.avatar = avatar;
    }
    
    

}