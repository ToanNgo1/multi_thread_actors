package project3.name_search_workers;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;


public class Worker_Actor extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private ArrayList <Integer> outpacketEX=new ArrayList<Integer>();
    private  Random random_number = new Random();
    private String path_csv;            //inorder for each actor to read the file or do a operation of a unique value, you will need to create a object container for that value.
    
    @Override
    public void preStart() {
        log.info("Starting");
        //populate(outpacketEX);
    }

    
    @Override
    public void onReceive(Object msg) {
        //what to do when message is received
        //ArrayList<String> test=new ArrayList<>();
        if (msg instanceof String &&((String) msg).contains(".csv") )
        { 
            //log.info("receive object");
            //log.info(msg);kl
            //log.info(msg+" the path");
            path_csv=msg.toString();
            //System.out.println(msg);
        }
        // if (msg instanceof ArrayList)//int [] this is a int array object, arraylist
       // {
         //   log.info("receive object");
            //Object[] objects = (msg);
            //for (Object obj : objects)
            //System.out.println(obj);
            //System.out.println(msg[0]);
           /*   for(Integer i: (ArrayList<Integer>)msg)
            {
                System.out.println(i+" ...check this");
            }*/
            //Integer[] conver=(ArrayList<Integer>)msg.toArray();
           // String[] pack=output((ArrayList<Integer>)msg);
            //String[] sample=new String[]{"this","that is"};
            
            //ArrayList<String> container = new ArrayList<>(Arrays.asList("sub0", "subs1"));
            //Object[] o=container.toArray();
           // getSender().tell(pack, getSelf());
           // getSender().tell("done with the work", getSelf()); 

        //}
    	
        if (msg instanceof String && msg.equals("hello worker")) {
        	
        	log.info("Recevied GreetingMessage!");
            
            getSender().tell("done with the work", getSelf());    //respond to the sender
            
        }
        else if(msg instanceof String && ((String) msg).contains("please search this:"))
        {
            //log.info(msg+"the String");
            String conver=((String) msg).replace("please search this:","");
            //log.info(conver+" this is after cut");
            String extract=read_sample(conver,path_csv);
            //System.out.println(extract);
            getSender().tell("reported: "+extract, getSelf());
            getSender().tell("done with the work",getSelf());
        }
        
        else if(msg instanceof String && msg.equals("Task: Report Statistics"))
        {
            log.info("received message report");
            //populate(outpacketEX);
            String[] extract=output(outpacketEX);
            getSender().tell(extract,getSelf());
            getSender().tell("done with the work", getSelf());
            

        }
         else if (msg instanceof String && msg.equals("terminate")) {
        	
        	//log.info("Recevied terminate message!");
            
        	 getContext().stop(getSelf());                               //stop the sender and with it the application
            
        } else if (msg instanceof String && msg.equals("identified terminate")) {
        	
        	//log.info("Recevied identified terminate message!");
            
        	 getContext().stop(getSelf());                               //stop the sender and with it the application
            
        } else {
            unhandled(msg);
        }
    }
    //////////////////////////////////////////////////////////////
    private static String read_sample(String search,String path)
        {
            String First_N_search="";
            String second_N_search="";
            int count=0;
            String re;
            String sample=search.strip();
            if( sample.contains(","))
            {   //System.out.println("full name");
                String[] container=sample.split(",");
                for(String i:container)
                {
                    //System.out.println(i.strip());
                    if(count ==1)
                    {   
                        First_N_search=i;
                        count+=1;
                    }
                    else
                    {
                        second_N_search=i;
                        count+=1;
                    }
                }
                String ext=read(First_N_search, second_N_search,path);
                //System.out.println(ext);
                re=ext;
                return re;
            }
            else
            {
                First_N_search=search;
                //System.out.println("only contain 1 name");
                String ext=read(First_N_search,"null",path);
                //System.out.println(ext+" this a ");
                re=ext;
                return re;
            }
        }
        ////////////////////////////////////////////////////////////
        private static String read(String search_F,String search_L,String path)
        {   String line = ""; //container  
            String splitBy = ",";  ///split
            int line_count=0;
            Integer match_count=0;
            Integer count_full_name=0; 
            //System.out.println(path+"check");
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(path));
            while(((line=br.readLine())!=null) )
            {
                String[] test=line.split(splitBy);
                for(String i: test)
                {
                    String extract=i.replaceAll("\n", "").toUpperCase();
                    //if(line_count<20)
                    //System.out.println(i);
                    //System.out.println(!search_F.equals("null") & !search_L.equals("null"));Peggi,Scutter
                    if(!search_F.equals("null") & !search_L.equals("null"))
                    {   //System.out.print("this is a test okokok");
                        if(extract.strip().equals(search_F.toUpperCase()) || extract.strip().equals(search_L.toUpperCase()) & count_full_name<2 )
                        {
                            count_full_name+=1;
                            if (count_full_name==2)
                            {
                                count_full_name=0;
                                match_count+=1;
                            }
                        }
                        else
                        {
                            count_full_name=0;
                        }
                    }
                    else if(extract.strip().equals(search_F.toUpperCase()) & search_L.equals("null"))
                    {
                        match_count+=1;
                    }
                    
                    //line_count+=1;

                }
            } 
            br.close();
        } 
       
            catch (FileNotFoundException  e)
            {
                    e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace(); 
            
            }
            String conver=String.valueOf(match_count);
            return conver;
        }
        //////////////////////////////////////////////////////////////////////////////////////////
    public void populate(ArrayList<Integer> outpacketEX)//populate the local array worker. 
    {
        for (int i=0 ; i<2000;i++)
        {   
            outpacketEX.add(random_number.nextInt(50));
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////
    public static String[] output(ArrayList<Integer> input)//this will get call either using a passby array or a local array
    {
        Integer counter=0;
        Integer advegave=0;
        Integer sumation=0;
        Integer max=-999;
        String[] outpacket=new String[3];
        //working for sum//
        for(Integer i: input)
        {
            sumation+=i;
            counter+=1;
            if(i >max)
            {
                max=i;
            }
        }
        //find advegave//
            advegave=sumation/counter;
            /*System.out.println(counter+"il");
            System.out.println(sumation);*/
        //conver to string
        for(int a=0;a<outpacket.length;a++)
        {
            /*if(a==0)
            {
            outpacket[a]="The summation is: "+String.valueOf(sumation);
            }*/
            if(a==1)
            {
                outpacket[a]="The average is: "+String.valueOf(advegave);
            }
           /*  else
            {
                outpacket[a]="The max value is: "+String.valueOf(max);
            }*/
        }
        return outpacket;
    }
    
    
    @Override
    public void postStop() {         
     	log.info("terminating");
    }
}