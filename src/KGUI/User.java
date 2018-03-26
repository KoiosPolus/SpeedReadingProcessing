package KGUI;

import java.io.*;

public class User implements Serializable {
    String username = null;
    String password = null;

    public boolean save() {
        try {
            File file = new File("C:\\testFile.txt");
            OutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutput outputStream = new ObjectOutputStream(fileOutputStream);
            outputStream.writeObject(this);
            System.out.println("I've stored the User object into the file: " + file.getName());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

