package com.mycompany.marketdatagenerator;

import quickfix.DefaultMessageFactory;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;



public class MDGMain {

    public static void main(String[] args) {
        try{
            SessionSettings srvSettings = new SessionSettings("server.cfg");
            MDGServer fixServer = new MDGServer();
            MessageStoreFactory storeFactory = new FileStoreFactory(srvSettings);
            LogFactory logFactory = new FileLogFactory(srvSettings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            SocketAcceptor acceptor = new SocketAcceptor(fixServer, storeFactory, srvSettings, logFactory, messageFactory);
            
            acceptor.start();
            
            int i;
            for(i=0;i<1000;i++){
                Thread.sleep(5000);
                fixServer.sendFixMessages();
            }
            
            acceptor.stop();
        }
        catch(Exception ex){
        }
    }
    
}
