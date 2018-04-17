package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class TimeServerHandle implements Runnable {
    private Socket socket;
    public TimeServerHandle(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream(),true);
            String currentTime = null;
            String body = null;
            while(true) {
                body = in.readLine();        //从客户端读
                if(body == null) break;
                System.out.println("The time server receive order: " + body);
                currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ?
                        new java.util.Date(System.currentTimeMillis()).toString() : "BAD ORDER";
                out.print(currentTime);    //向客户端写
                out.close(); //在同一个项目中需要用完即关
                System.out.println("The time server send  " + currentTime);
            }
        } catch (Exception e) {

            }
    }
}