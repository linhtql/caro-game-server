package com.nekol.dao;

import com.nekol.model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class UserDAO extends ConnectDB {

    public UserDAO() {
        super();
    }

    public User verifyUser(User user) {

        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT *\n"
                    + "FROM user\n"
                    + "WHERE username = ?\n");
            preparedStatement.setString(1, user.getUsername());
//            preparedStatement.setString(2, passwordEncoder(user.getPassword()));
            System.out.println(passwordEncoder(user.getPassword()));
            System.out.println(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();
            System.out.println(rs);
            if (rs.next()) {
                System.out.println(rs.getString(3));
                return new User(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getInt(6),
                        rs.getInt(7),
                        (rs.getInt(8) != 0),
                        (rs.getInt(9) != 0),
                        getRank(rs.getInt(1)));
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public int getRank(int ID) {
        int rank = 1;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT user.ID\n"
                    + "FROM user\n"
                    + "ORDER BY (user.NumberOfGame+user.numberOfDraw*5+user.NumberOfWin*10) DESC");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) == ID) {
                    return rank;
                }
                rank++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public User getUserById(int id) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM user\n"
                    + "WHERE ID=?");
            preparedStatement.setInt(1, id);
            System.out.println(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getInt(6),
                        rs.getInt(7),
                        (rs.getInt(8) != 0),
                        (rs.getInt(9) != 0),
                        getRank(rs.getInt(1)));

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public void addUser(User user) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO user(username, password, avatar)\n"
                    + "VALUES(?,?,?)");
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, passwordEncoder(user.getPassword()));
            preparedStatement.setString(3, user.getAvatar());
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean checkDuplicate(String username) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM user WHERE username = ?");
            preparedStatement.setString(1, username);
            System.out.println(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public void updateToOnline(int id) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE user\n"
                    + "SET IsOnline = 1\n"
                    + "WHERE ID = ?");
            preparedStatement.setInt(1, id);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateToOffline(int id) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE user\n"
                    + "SET IsOnline = 0\n"
                    + "WHERE ID = ?");
            preparedStatement.setInt(1, id);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateToPlaying(int id) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE user\n"
                    + "SET IsPlaying = 1\n"
                    + "WHERE ID = ?");
            preparedStatement.setInt(1, id);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateToNotPlaying(int id) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE user\n"
                    + "SET IsPlaying = 0\n"
                    + "WHERE ID = ?");
            preparedStatement.setInt(1, id);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<User> getListFriend(int ID) {
        List<User> ListFriend = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT User.ID, User.NickName, User.IsOnline, User.IsPlaying\n"
                    + "FROM user\n"
                    + "WHERE User.ID IN (\n"
                    + "	SELECT ID_User1\n"
                    + "    FROM friend\n"
                    + "    WHERE ID_User2 = ?\n"
                    + ")\n"
                    + "OR User.ID IN(\n"
                    + "	SELECT ID_User2\n"
                    + "    FROM friend\n"
                    + "    WHERE ID_User1 = ?\n"
                    + ")");
            preparedStatement.setInt(1, ID);
            preparedStatement.setInt(2, ID);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                ListFriend.add(new User(rs.getInt(1),
                        (rs.getInt(2) == 1),
                        (rs.getInt(3)) == 1));
            }
//            ListFriend.sort(new Comparator<User>(){
            ListFriend.sort((User o1, User o2) -> {
                if (o1.isOnline() && !o2.isOnline()) {
                    return -1;
                }
                if (o1.isPlaying() && !o2.isPlaying()) {
                    return -1;
                }
                if (!o1.isPlaying() && o1.isOnline() && o2.isPlaying() && o2.isOnline()) {
                    return -1;
                }
                return 0;
            });
        } catch (SQLException e) {
            // TODO Auto-generated catch block

        }
        return ListFriend;
    }

    public boolean checkIsFriend(int id1, int id2) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT Friend.ID_User1\n"
                    + "FROM friend\n"
                    + "WHERE (ID_User1 = ? AND ID_User2 = ?)\n"
                    + "OR (ID_User1 = ? AND ID_User2 = ?)");
            preparedStatement.setInt(1, id1);
            preparedStatement.setInt(2, id2);
            preparedStatement.setInt(3, id2);
            preparedStatement.setInt(4, id1);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block

        }
        return false;
    }

    public void addFriendShip(int id1, int id2) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO friend(ID_User1, ID_User2)\n"
                    + "VALUES (?,?)");
            preparedStatement.setInt(1, id1);
            preparedStatement.setInt(2, id2);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
        }
    }

    public void removeFriendship(int id1, int id2) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM friend\n"
                    + "WHERE (ID_User1 = ? AND ID_User2 = ?)\n"
                    + "OR(ID_User1 = ? AND ID_User2 = ?)");
            preparedStatement.setInt(1, id1);
            preparedStatement.setInt(2, id2);
            preparedStatement.setInt(3, id2);
            preparedStatement.setInt(4, id1);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<User> getUserStaticRank() {
        List<User> list = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT *\n"
                    + "FROM user\n"
                    + "ORDER BY(user.NumberOfGame+user.numberOfDraw*5+user.NumberOfWin*10) DESC\n"
                    + "LIMIT 8");
            System.out.println(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                list.add(new User(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getInt(6),
                        rs.getInt(7),
                        (rs.getInt(8) != 0),
                        (rs.getInt(9) != 0),
                        getRank(rs.getInt(1))));
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }

    public void makeFriend(int id1, int id2) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO friend(ID_User1,ID_User2)\n"
                    + "VALUES(?,?)");
            preparedStatement.setInt(1, id1);
            preparedStatement.setInt(2, id2);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public int getNumberOfWin(int id) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT user.NumberOfWin\n"
                    + "FROM user\n"
                    + "WHERE user.ID = ?");
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    public int getNumberOfDraw(int id) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT user.NumberOfDraw\n"
                    + "FROM user\n"
                    + "WHERE user.ID = ?");
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    public void addDrawGame(int id) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE user\n"
                    + "SET user.NumberOfDraw = ?\n"
                    + "WHERE user.ID = ?");
            preparedStatement.setInt(1, new UserDAO().getNumberOfDraw(id) + 1);
            preparedStatement.setInt(2, id);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void addWinGame(int id) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE user\n"
                    + "SET user.NumberOfWin = ?\n"
                    + "WHERE user.ID = ?");
            preparedStatement.setInt(1, new UserDAO().getNumberOfWin(id) + 1);
            preparedStatement.setInt(2, id);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public int getNumberOfGame(int id) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT user.NumberOfGame\n"
                    + "FROM user\n"
                    + "WHERE user.ID = ?");
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    public void addGame(int id) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE user\n"
                    + "SET user.NumberOfGame = ?\n"
                    + "WHERE user.ID = ?");
            preparedStatement.setInt(1, new UserDAO().getNumberOfGame(id) + 1);
            preparedStatement.setInt(2, id);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void decreaseGame(int id) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE user\n"
                    + "SET user.NumberOfGame = ?\n"
                    + "WHERE user.ID = ?");
            preparedStatement.setInt(1, new UserDAO().getNumberOfGame(id) - 1);
            preparedStatement.setInt(2, id);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String passwordEncoder(String password) {
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(4));

        return hash;
    }

    public boolean checkPassword(String password1, String password2) {
        return BCrypt.checkpw(password1, password2);
    }

}
