package SDNB;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 *
 * @author chistopher
 */
public class Client {
    
    private static String host =  "127.0.0.1";
    private static int pto = 1234;

    public static void main(String[] args) {
        
        try{
            InetSocketAddress dst = new InetSocketAddress(host,pto);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            
            DatagramChannel cl = DatagramChannel.open();
            cl.configureBlocking(false);
            
            Selector sel = Selector.open();
            
            cl.register(sel, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
            
            while( true ){
                sel.select();
                        
                Iterator<SelectionKey> it = sel.selectedKeys().iterator();   
                
                while(it.hasNext()){
                        SelectionKey k = (SelectionKey) it.next();
                        it.remove();
                        
                        if (k.isReadable()) {
                            DatagramChannel ch = (DatagramChannel) k.channel();
                            
                            ByteBuffer b = ByteBuffer.allocate(2000);
                            b.clear();
                            
                            SocketAddress e = ch.receive(b);
                            
                            b.flip();
                            
                            String nombre = new String(b.array(), 0, b.limit());
                            
                            System.out.println(nombre);
                            
                            k.interestOps(SelectionKey.OP_WRITE);
                            continue;
                        }
                        if(k.isWritable()){
                            
                            DatagramChannel ch = (DatagramChannel) k.channel();
                            
                            System.out.println("Escriba su nombre");
                            String nombre = br.readLine();
                            
                            ByteBuffer b1 = ByteBuffer.wrap(nombre.getBytes());
                            
                            ch.send(b1, dst);
                            k.interestOps(SelectionKey.OP_READ);
                            continue;
                        }
                    }
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
}
