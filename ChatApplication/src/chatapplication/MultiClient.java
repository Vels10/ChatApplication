/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapplication;

import static chatapplication.ChatApp.id;
import static chatapplication.MulticastClient.port;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import javax.swing.JOptionPane;

/**
 *
 * @author vels10
 */
public class MultiClient {
    
     MultiClient(){ 
        
        id = JOptionPane.showInputDialog("Enter your name");
        int count = 0;
        while(id == null || id.equals("") ){ 
            if(id == null){
                new ChatApp().setVisible(true);
                count++;
                break;
            }
            if(id.equals("")){
                JOptionPane.showMessageDialog(new ChatApp(),"Enter a real Name");
                id = JOptionPane.showInputDialog("Enter your Name");
            }
        }
       
        if(count == 0){
            new MulticastClient().setVisible(true);
            Thread t1 = new Thread(new Client());
            t1.start();
        }
    }
}

class Client implements Runnable {

        public Client() {
            try{
                MulticastClient.socket = new MulticastSocket(1114);
                MulticastClient.port = new DatagramSocket();
                MulticastClient.ip = InetAddress.getByName("10.10.10.10");
                MulticastClient.socket.joinGroup(MulticastClient.ip);
            }
            catch(Exception e){
                JOptionPane.showMessageDialog(new ChatApp(),"Cannot bind..");
            }    
        }

        @Override
        public void run(){
        
        Thread t3 = new Thread(new OnlineStatus()); 
        t3.start();
        Thread t4 = new Thread(new ReceiveOnlineStatus());
        t4.start();
        newUser();
        while(true){  
            try{        
                DatagramPacket packet;
                byte[] buf = new byte[256];
                packet = new DatagramPacket(buf, buf.length);
                MulticastClient.socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                MulticastClient.jTextArea1.setText(MulticastClient.jTextArea1.getText()+received+"\n");        // Recieve all the Packets
                MulticastClient.jTextArea2.setText("");
            }
            catch(IOException e){
                System.out.println(e);
            }
        }  
    } 
    
    void newUser(){
        String text = id+" has logged into the chat room ";
        byte buf[] = text.getBytes();
        try{  
            InetAddress data = InetAddress.getByName("10.10.10.10");
            DatagramPacket packet = new DatagramPacket(buf,buf.length,data,1114);  
            port.send(packet);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}

