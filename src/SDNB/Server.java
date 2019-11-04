/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SDNB;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 *
 * @author chistopher
 */
public class Server {

    /**
     * @param args the command line arguments
     */
    
    static int PUERTO = 1234;
            
    public static void main(String[] args) {
        
        try{
		DatagramChannel s = DatagramChannel.open();
		s.configureBlocking(false);
                
                Selector sel = Selector.open();
                s.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                
                InetSocketAddress pto = new InetSocketAddress(PUERTO);
                s.bind(pto);
                //s.socket().bind(pto);
                
                System.out.println("Servidor iniciado...");
                
                s.register(sel, SelectionKey.OP_READ);
                
                while(true){
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
                            InetSocketAddress emisor = (InetSocketAddress) e;
                            
                            b.flip();
                            
                            String nombre = new String(b.array(), 0, b.limit());
                            
                            System.out.println("Datagrama recibido desde " + emisor.getAddress() + ":"
                                    + emisor.getPort() + " con el nombre: " + nombre + "\nEnviando saludo...");
                            
                            String saludo = "Hola " + nombre;
                            
                            ByteBuffer b1 = ByteBuffer.wrap(saludo.getBytes());
                            
                            ch.send(b1, e);
                            
                            continue;
                        }
                    }
                    
                }                
		
	} catch (Exception e){
		e.printStackTrace();
	}	
        
    }
    
}
