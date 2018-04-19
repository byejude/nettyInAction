package server;



import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class NioTimeServer implements Runnable {

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private volatile boolean stop;

    public NioTimeServer(int port) {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port),1024);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("the time server is start "+port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop(){
        this.stop = true;
    }

    @Override
    public void run() {
        while(!stop){
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeySet.iterator();
                SelectionKey key = null;
                while(it.hasNext()){
                    key = it.next();
                    it.remove();
                    try {
                        handleInput(key);
                    } catch (Exception e) {
                        //异常下关闭channel以及其key
                     if(key!=null){
                         key.cancel();
                        if(key.channel()!=null){
                            key.channel().close();
                        }
                     }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        //关闭selector
        if(selector!=null){
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key ) throws IOException{
        if(key.isValid()){
            //处理新接入的客户端请求信息
            if(key.isAcceptable()){
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false); //设置为异步非阻塞
                socketChannel.register(selector,SelectionKey.OP_READ);
            }

            if(key.isReadable()){
                SocketChannel socketChannel = (SocketChannel)key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = socketChannel.read(readBuffer);
                if(readBytes>0){
                    //读取到字节 对字节进行编解码
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes,"UTF-8");
                    System.out.println("the server receive order :"+body);
                    String currentTime = "QUERY TIME".equalsIgnoreCase(body)?new Date(System.currentTimeMillis()).toString():"BAD OREDER";
                    System.out.println("the server send:"+currentTime);
                    doWrite(socketChannel,currentTime);

                }else if(readBytes<0){
                    //return -1 链路已经关闭
                    key.cancel();
                    socketChannel.close();
                }else {
                    //readBytes==0 没有读取到字节
                }

            }
        }
    }

    private void doWrite(SocketChannel socketChannel,String response) throws IOException{
        if(response != null && response.trim().length()>0){
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            socketChannel.write(writeBuffer);
            if(!writeBuffer.hasRemaining()){
                System.out.println("send to server sucessed");
            }
        }
    }

}
