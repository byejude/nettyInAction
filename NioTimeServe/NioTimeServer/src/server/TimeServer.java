package server;

public class TimeServer {
    private static int index = 0;
    public static void main(String[]args){
        int port = 8080;
        if(args !=null&&args.length >0){
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                //e.printStackTrace();
            }
        }
        NioTimeServer nioTimeServer = new NioTimeServer(port);
        String name = "NIO-timeServerThread-"+ ++index;
        new Thread(nioTimeServer,name).start();
        System.out.println(name);

    }
}
