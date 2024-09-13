package project3.name_search_workers;

import java.io.BufferedReader;
import java.io.File;
import java.io.*;
//import java.lang.reflect.Array;
import java.util.*;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;


public class Boss_Actor extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    /*private ArrayList<Integer> main  =new ArrayList<>();
    private ArrayList<Integer> subs0=new ArrayList<>();
    private ArrayList<Integer> subs1=new ArrayList<>();
    private ArrayList<Integer> subs2=new ArrayList<>();
    private ArrayList<Integer> subs3=new ArrayList<>();*/
    //private Object[] intArr=new Object[4];      //this will contain a string of the path to the .csv files
    private String[] intsam=new String[4];
    //private ArrayList<String> container = new ArrayList<>(Arrays.asList("sub0", "subs1"));
    private  Random random_number = new Random();
    private Integer container_int=0;
    private Integer ping=0;

    @Override
    public void preStart() {

        log.info("Starting");
        populate();
        String mesage="please search this:"+user_input();
       /* for(Object i: intArr)
        {
            System.out.println(Object.toString(i)+"this is the out");
        }
        
        /*for(String i: container)
        {System.out.println(i);
           if(container instanceof ArrayList)
           {System.out.println("this is a test");}
        }*/
        
         ActorRef workers[] = new ActorRef [4];             //create an array of workers
        
        for (int i = 0; i < 4; i++) {
        	log.info("creating worker #" +  i);
            
        	String name="Worker"+i;                       //the name should be a string
        	
            workers[i] = getContext().actorOf(Props.create(Worker_Actor.class), name);

           workers[i].tell(intsam[i], getSelf());//done
            workers[i].tell(mesage,getSelf()); //instuction

        }
    }
    public String user_input()
    {
        System.out.println("enter the name you want to search");
        System.out.println(("full name following the convention of including the , in the middle of the first and last name:\n sample first_name,last_name "));
        try (Scanner input = new Scanner(System.in)) 
        {
            String user_in=input.nextLine();
            user_in=user_in.strip();
            //System.out.println(user_in+"this is track");
            input.close();
            return user_in;
        }
        
    }

    public void populate()
    {
        File []direc_patch=new File("demo\\src\\main\\java\\project3\\name_search_workers").listFiles();//get the directory where your .csv files are located
        int cont=0;
        for(File i:direc_patch)
        {
            //System.out.println(i.getName());
            if (i.getName().contains(".csv"))
            {
                //System.out.println(i.getName()+" this is the filter at work");
                intsam[cont]=i.getAbsolutePath();
                cont+=1;

            }
        }
        /*for(Object i: intsam)
        {
            System.out.println(i +" this is the patch that in the object container");
        }*/


    }
   
    @Override
    public void onReceive(Object msg) {
        /*if (msg instanceof String[])
        {
            //log.info("the sender is "+getSender().path().name().toString());
            //Objects[] arr=msg;
            //System.out.println(msg.toString()+"check this out");
            //System.out.println(msg.size);
            String ouputex="";
            for(String i:( String[])msg)
            {
                if(i!=null)
                //System.out.println(i+": this bad");
                {ouputex+=i;
                String rep=ouputex.replace("The average is: ", "");
                container_int+=Integer.valueOf(rep);
                }
                
            }

            log.info("Worker "+getSender().path().name().toString()+"\nrecieve message: "+ouputex);
        }*/
        //if(msg instanceof String && ((String) msg).contains("reported: "))
        if(msg instanceof String && ((String)msg).contains("reported: "))
        {   
            //System.out.println("it go in");
            String a=(String)(getSender().path().name().toString());
            Integer conv=Integer.valueOf(a.replaceAll("Worker", ""));
            //System.out.println(a+" this is the worker name");
            //System.out.println(conv+" this is the value its return");
            if (conv==0)
            {   conv+=1;}
            else if(conv!=0)
            {   conv+=1;}
            log.info("the sender "+getSender().path().name().toString()+" "+msg+" matches in the file "+conv);
        }
        
        if (msg instanceof String && msg == "done with the work") {

        	
        	//log.info("the sender of this message is " + getSender().path().name().toString());  //print the identity of the sender, the name of the sender
        	//log.info("receive message: "+ msg.toString());
        	
        	if(getSender().path().name().toString().equals("Worker0")) {
        		
        		ActorRef W0 = (ActorRef)getContext().getChild("Worker0");            //get a reference for Worker0
                
                W0.tell("identified terminate", getSelf());                          //use that reference to send dedicated message
                ping+=1;
                
        	}
            
             else if(ping==3){
        		getSender().tell("terminate", getSelf());
                postStop();                         
        	}
            else //whoever was the sender, send the terminate message
            {
                getSender().tell("terminate", getSelf());
                ping+=1;
            }
            
            
    	}else {
            unhandled(msg);
    	}

        
    }
    
    
    @Override
    public void postStop() {         
    	//what to do when terminated
        log.info("This is the subs total of all the average:"+container_int);
     	log.info("terminating the Boss actor");
        //getContext().stop(getSelf());
        //log.info("this is the sub total:"+container_int);
    }
}