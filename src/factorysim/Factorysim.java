/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package factorysim;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
/**
 *
 * @author sbuys
 */
    class event implements Comparable<event>{
        int time;
        truck truck;
        String type;
        int id;
        public event(int ti, truck tt, String ty,int i){
            time = ti;
            truck = tt;
            type = ty;
            id = i;
        }
        public void display()
        {
           System.out.println("Event id: "+id +" Type: "+type+" Truck id: "+truck.id+ " Time: "+time);
        }
        public Integer getTime() {
            return time;
        }
        @Override
        public int compareTo(event o) {
            return this.getTime().compareTo(o.getTime());
        }
    }
    class truck{
        int id;
        int arrival; //queue waiting time
        public truck(int i){
            id= i;
            arrival = 0;
        }
    }
    class helpers{
        Queue<truck> loaderq = new LinkedList<>();
        Queue<truck> scalerq = new LinkedList<>();
        List<event> FEL=new ArrayList<>();
        List<truck> loader = new ArrayList<>(2);
        int scaler ;
        
        public int getloadq()
        {
            return loaderq.size();
        }
        public int getscalerq()
        {
            return scalerq.size();
        }
        public void sortFEL()
        {
             Collections.sort(FEL);
        }
        public int loadTime()
        {
            Random rnd = new Random();
            int i = rnd.nextInt(10);
            if(i<3)
            {
                return 5;
            }
            else if (i<8)
            {
                return 10;
            }
            else
            {
                return 15;
            }
        }
        public int weighTime()
        {
            Random rand=new Random();
            int temp=rand.nextInt(10);
            if(temp<7)
                return 12;
            else
                return 16;
        }
        public int travelTime()
        {
            Random rand=new Random();
            int temp=rand.nextInt(10);
            if(temp<4)
                return 40;
            else if(temp<5)
                return 60;
            else if(temp<9)
                return 80;
            else
                return 100;
        }
    }

public class Factorysim {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int loaderWorkTime=0;
        int scalerWorkTime=0;
        int loaderWaitingTime=0;
        int scalerWaitingTime=0;
        int systemLock = 0;
        Boolean flag2 = true; //event lock
        Boolean flag = true;
        int eventId = 0;
        int systemClock = 0;
        helpers helpers = new helpers();
        truck[] trucks = new truck[6]; 
        for(int i = 0; i<6;i++)
        {
            trucks[i] = new truck(i+1);
        }
        //fel doldur
        helpers.FEL.add(new event(systemClock+helpers.weighTime(),trucks[0],"EW",eventId++));
        helpers.scaler = 1;
        int lt=0;
        lt = helpers.loadTime();
        helpers.FEL.add(new event(systemClock+lt,trucks[1],"EL",eventId++));
        loaderWorkTime += (lt);
        lt=helpers.loadTime();
        helpers.FEL.add(new event(systemClock+lt,trucks[2],"EL",eventId++));
        loaderWorkTime += (lt);
        
        //L(t) doldur
        helpers.loader.add(trucks[2]);
        helpers.loader.add(trucks[1]);
        
        //LQ(t) doldur
        helpers.loaderq.add(trucks[3]);
        helpers.loaderq.add(trucks[4]);
        helpers.loaderq.add(trucks[5]);
        
        
        event nextev = null;
        System.out.println("How many loads do you want to simulate?");
        int a; 
        Scanner S=new Scanner(System.in); 
        a=S.nextInt();  
        while(true)
        {
            if(eventId > a && flag2)
            {
                flag2 = false;
                systemLock=systemClock; // simülasyon tüm trucklar dönene kadar çalışıyor. 
                                        //burada sistem lock değişkeninde, son oluşturulan eventin zamanı tutuluyor
                                        //böyleleikle avg hesabı yapabiliyoruz
            }
                
            helpers.sortFEL();
        
            System.out.println("System clock: "+systemClock);
            System.out.println("LQ(t):"+helpers.loaderq.size()+"  L(t):"+helpers.loader.size()+ "  WQ(t):"+helpers.scalerq.size()+" W(t):"+helpers.scaler);
            System.out.println("Total Events: "+eventId);
            System.out.print("Loader Queue: ");
            for(truck s : helpers.loaderq) { 
                System.out.print(s.id+" "); 
              }
             System.out.print("\n");
             System.out.print("Scaler Queue: ");
              for(truck s : helpers.scalerq) { 
                System.out.print(s.id+" "); 
              }
             System.out.print("\n");
             
            System.out.println("FEL: ");
            for(int i = 0; i<helpers.FEL.size();i++)
            {
                 System.out.println("Type: "+helpers.FEL.get(i).type +" Time: "+helpers.FEL.get(i).time+"  Truck:"+helpers.FEL.get(i).truck.id);
            }
            
            System.out.println("---------------------------------------------");
            
            if(helpers.loaderq.size()==6)
            {
                System.out.println("All trucks returned to base. Generating simulation results: ");
                System.out.println("Loader Work Time: "+loaderWorkTime);
                System.out.println("Loader Queue Waiting Time: "+loaderWaitingTime);
                System.out.println("Scaler Work Time: "+scalerWorkTime);
                System.out.println("Scaler Queue Waiting Time: "+scalerWaitingTime);
                System.out.println("---------------------------------");
                System.out.println("System Clock Locked at: "+systemLock);
                double tt=(double)(loaderWorkTime/2)/systemLock;
                System.out.println("Average Loader Utilizatin: "+tt);
                tt=(double)(scalerWorkTime)/systemLock;
                System.out.println("Average Scaler Utilizatin: "+tt);
                break;
            }
                
            if(nextev != null)
                if(helpers.FEL.get(0).time == nextev.time)
                {
                    //systemClock -= nextev.time;
                    flag = false;
                }
                    
            nextev = helpers.FEL.get(0);
            helpers.FEL.remove(0);
            systemClock=nextev.time;
            
            flag = true;
            
            if("EL".equals(nextev.type))
            {
                helpers.loader.remove(nextev.truck);
                nextev.truck.arrival = systemClock;
                helpers.scalerq.add(nextev.truck);
            }
            else if("EW".equals(nextev.type) )
            {
                helpers.scaler = 0;
                helpers.FEL.add(new event(systemClock+helpers.travelTime(),nextev.truck,"ALQ",eventId));
            }
            else //ALQ
            {
                nextev.truck.arrival = systemClock;
                helpers.loaderq.add(nextev.truck);
            }
            
            //move next events
            if(helpers.loader.size()<2 && !helpers.loaderq.isEmpty()&& flag2)
            {
                int time = helpers.loadTime();
                truck nextt= helpers.loaderq.poll();
                helpers.FEL.add(new event(systemClock+time,nextt,"EL",eventId++));
                loaderWorkTime += (time);
                helpers.loader.add(nextt);
                loaderWaitingTime += systemClock-nextt.arrival;
                
            }
            if(helpers.scaler == 0 && !helpers.scalerq.isEmpty())
            {
                int time = helpers.weighTime();
                truck nextt= helpers.scalerq.poll();
                helpers.FEL.add(new event(systemClock+time,nextt,"EW",eventId));
                scalerWorkTime += (time);
                helpers.scaler = 1;
                scalerWaitingTime += systemClock-nextt.arrival;
            }
            
        }
        
        
        
    }
    
    
}
