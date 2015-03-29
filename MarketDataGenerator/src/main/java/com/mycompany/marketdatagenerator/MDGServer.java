package com.mycompany.marketdatagenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;
import quickfix.field.MDEntryID;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.MDUpdateAction;
import quickfix.field.MinQty;
import quickfix.field.Symbol;
import quickfix.fix44.MarketDataIncrementalRefresh;
import quickfix.fix44.MarketDataSnapshotFullRefresh;


public class MDGServer implements Application{
    private ArrayList<SessionID> sessions_;
    private ArrayList<String> symbols_;
    private Random rand;
    
    @Override
    public void onCreate(SessionID sessionID){}
    
    @Override
    public void onLogon(SessionID sessionID){
        System.out.println("onLogon ...  " + sessionID);
        sessions_.add(sessionID);
    }

    @Override
    public void onLogout(SessionID sessionID){
        System.out.println("onLogout ...  " + sessionID);
        int i;
        for(i=0;i<sessions_.size();i++){
            if(sessions_.get(i).getSenderCompID().equals(sessionID.getSenderCompID()))
                break;
        }
        sessions_.remove(i);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionID){}

    @Override
    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon{}
    
    @Override
    public void toApp(Message message, SessionID sessionID) throws DoNotSend{}

    @Override
    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType{
        System.out.println("fromApp ...  " + sessionID + "   " + message);
        //crack(message, sessionID);
    }
    
    public void sendFixMessages(){
        MarketDataIncrementalRefresh message = new MarketDataIncrementalRefresh();
        MarketDataIncrementalRefresh.NoMDEntries group = new MarketDataIncrementalRefresh.NoMDEntries();
        int symbolCount = symbols_.size();
        Symbol symbol = new Symbol(symbols_.get(rand.nextInt(symbolCount)));
        int rndActionType = rand.nextInt(3);
        char actionType = (rndActionType<2.0?(rndActionType<1?'0':'1'):'2');//0=new,1=update,2=delete
        MDUpdateAction action = new MDUpdateAction(actionType);
        String entry = "entry";
        entry+=rand.nextInt(200);
        MDEntryID entryID = new MDEntryID(entry);
        MDEntryType entryType = new MDEntryType(rand.nextInt(2)<1?'0':'1');
        MDEntryPx price = new MDEntryPx((rand.nextInt(100) + 100)/100);
        MDEntrySize size = new MDEntrySize((rand.nextInt(100) + 100));
        MinQty qty = new MinQty((rand.nextInt(100) + 100)*100);
        group.set( action );
        group.set( entryID );
        group.set( entryType );
        group.set( symbol );
        group.set( price );
        group.set( size );
        group.set( qty );
        message.addGroup(group);
        int i;
        for(i=0;i<sessions_.size();i++){
            try
            {
                Session.sendToTarget(message,sessions_.get(i));
            }
            catch(SessionNotFound ex){
            }
        }
    }
    
    public MDGServer(){
        sessions_ = new ArrayList<SessionID>();
        symbols_ = new ArrayList<String>();
        symbols_.add("EURUSD");
        symbols_.add("AUDCAD");
        symbols_.add("USDTRY");
        rand = new Random();
    }
}
