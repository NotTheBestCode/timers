
package GreatKhanz.Timers;

import java.util.Arrays;
import GreatKhanz.Timers.Timer;
import GreatKhanz.Timers.Settings;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
//import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;



@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
public class Timers {

	public static Timer[] TimerArray;
	
	public static String[] Timers = {
			"Cult","Jacob","Dark Auction","Enchant Table","Fetcher","Puzzler",
			"Commision","Voting","New Mayor","New Years","Zoo","Bank","Xmas",
			"Spooky","Garry","Marina Fish","Mine Fiesta","unknown","Magma","Forge"			
			};
	
	@Mod.Instance("Timers")
	public static Timers instance;
	
	@EventHandler()
	public static void preInit(FMLPreInitializationEvent event) {
		System.out.println("preinit");
		boolean si = Settings.init();
		long unixTime = System.currentTimeMillis() / 1000L;
		TimerArray = new Timer[20];
		for (int i = 0; i < 20; i++)
		{
			TimerArray[i] = new Timer();
			Settings.setMaxTime(i,getTimeBetweenEvent(i));
			if (!si)
			{	
				Settings.setVisibleTime(i,getTimeBetweenEvent(i));
				Settings.setName(i, Timers[i]);
			}
				
			TimerArray[i].posX=Settings.posX;
			TimerArray[i].posY=Settings.posY;
		//	void init(String n,int next, int dur, int sT, int vTime, boolean e)
			TimerArray[i].init(Settings.getName(i), getTime(i), getDurationTime(i), Settings.getSwitchToSoon(i), Settings.getVisibleTime(i), Settings.enabled(i));
			TimerArray[i].timeTil();
		}
			
		sortTimes();
	}
	
	@EventHandler()
	public static void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new Timers());
        MinecraftForge.EVENT_BUS.register(new Timers());
	}
	
	@EventHandler()
	public static void postInit(FMLPostInitializationEvent event) {

	}
	//Fired when a chat message is about to be displayed on the client
    @SubscribeEvent
    public void xd(ClientChatReceivedEvent event) {
        //String message = event.message.getUnformattedText();
    	String msg = event.message.getUnformattedText().toString();
    	
    	System.out.println("MSG: " + msg);
    	sortTimes();

    	if (msg.equals("Puzzler gave you 1,000 Mithril Powder for solving the puzzle!"))
    		resetPuzzler();
    		

   // 	Settings.saveText(msg); //used to get puzzler message
    	
    //	System.out.println("MSG: " + msg);
    	if (msg.contains("420"))
    	{
    		Settings.saveFile();
    	}
    /*	else if (msg.contains("69"))
    		resetPuzzler();
    	else if (msg.contains("642"))
    		Settings.loadFile();


    //    System.out.println("Current unix: " + System.currentTimeMillis() / 1000L);
		for (int i = 0; i < 14; i++)
		{
	//		TimerArray[i].print();
		}
    //	int secIntoMonth = (int) (System.currentTimeMillis() / 1000L+5097)%37200;
    //    System.out.println("secIntoMonth: " + secIntoMonth + " :: current day " + (System.currentTimeMillis() / 1000L+5097.0)%37200/1200.0);
*/
    }
    @SubscribeEvent
    public void render(RenderGameOverlayEvent event) {
        if (event.isCancelable() || event.type != ElementType.EXPERIENCE) {
            return;
        }

		for (int i = 0; i < 14; i++)
			if (TimerArray[i].timeTil() < 0) //get new times for everything but puzzler
				if (i != 5)
				{
					TimerArray[i].setTime(getTime(i));
					sortTimes();
				}
				else
					TimerArray[i].mode = 0;
			
		for (int i = 0; i < 14; i++)
			TimerArray[i].update();

    }

    public static void sortTimes()
    {
    	int[] order;
		order = new int[14];
		int pos = 0;
		for (int i = 0; i < 14; i++)
		{
			if (TimerArray[i].visible())
				order[i] = TimerArray[i].timeTil();
			else
				order[i] = 442042069;
			TimerArray[i].setPos(15);
		}
		Arrays.sort(order);
		for (int i = 0; i < 14; i++)
		{
			for (int ii = 0; ii < 14; ii++)
			{
				if (order[i] == TimerArray[ii].timeTil() && pos == i)
				{
					TimerArray[ii].setPos(i);
					pos++;
				}
			}
		}
    	
    }
    public static void resetPuzzler()
    {
		TimerArray[5].setTime(getTimeBetweenEvent(5)); //set next time in 24 hours
		Settings.setLastPuzzler(); //save new puzzler time
		TimerArray[5].visible=true;
		TimerArray[5].mode=2;
		
		TimerArray[5].update(); //update the text
		
		sortTimes();
    }
    public static int getNextCult(Long unixTime) {
    	//cult 7,14,21,28
    	//cult = 7 sb days = 8400
    	//mo = 31 sb days = 37200 seconds
    	int secIntoMonth = (int) (unixTime+5097)%37200;
    	int currentDay = secIntoMonth/1200;
    	if (currentDay<28) //next one follows 7 day plan
    		return 8400-(secIntoMonth%8400);
    	else return 38400+8400-secIntoMonth; //next one is on the seventh
    }
    public static int getTime(int id) //returns time till event (in seconds)
    {
    	//roughly sorted from most occurrences to least
		long unixTime = System.currentTimeMillis() / 1000L;
		int t = getTimeBetweenEvent(id);
		switch (id) {
		
		//good/accurate timers
			case 0 : return getNextCult(unixTime); //Cult
			case 1 : return t - (int)(unixTime-900)%t; //Jacob
			case 2 : return t - (int)(unixTime-3302)%t; //Dark Auction
			case 3 : return t - (int)(unixTime+5)%t; //Enchant Table
			case 4 : return t - (int)(unixTime-18000)%t; //Fetcher
			case 5 : return Math.max(t+Settings.getLastPuzzler()-(int)unixTime,5); //Puzzler
			case 6 : return t - (int)(unixTime-17940)%t; //Commision
			case 7 : return t - (int)(unixTime-324918)%t; //Voting
			case 8 : return t - (int)(unixTime-213304)%t; //New Mayor
			case 9 : return t - (int)(unixTime-104102)%t; //New Years
			case 10 : return t- (int)(unixTime+3897)%(t); //Zoo
			case 11 : return t - (int)(unixTime+3900)%t; //Bank
			case 12 : return t - (int)(unixTime+347819)%t; //Xmas
			case 13 : return t - (int)(unixTime+44707)%t; //Spooky
			
			
			
		//	case 14 : return t - (int)(unixTime-42)%t; //Garry Servers vary by up to 10 minutes (event starts ~ever 20mins)
		//	case 15 : return ; //Marina Fish
		//	case 16 : return ; //Mine Fiesta
		//	case 17 : return ; //unknown
		//	case 18 : return ; //Magma
		//	case 19 : return ; //Forge
			
			default : return 9999999;
		}
    }
    public static int getTimeBetweenEvent(int id)
    {
		switch (id) {
		case 0 : return 8400; //Cult (7day max should only be called for settings)
		case 1 : return 3600; //Jacob
		case 2 : return 3600; //Dark Auction
		case 3 : return 3600*24; //Enchant Table
		case 4 : return 3600*24; //Fetcher
		case 5 : return 3600*24; //Puzzler
		case 6 : return 3600*24; //Commision
		case 7 : return 3600*124; //Voting
		case 8 : return 3600*124; //New Mayor
		case 9 : return 3600*124; //New Years
		case 10 : return 3600*62; //Zoo
		case 11 : return 3600*31; //Bank
		case 12 : return 3600*124; //Xmas
		case 13 : return 3600*124; //Spooky
	/*	case 14 : return 1200; //Garry
		case 15 : return ; //Marina Fish
		case 16 : return ; //Mine Fiesta
		case 17 : return ; //unknown
		case 18 : return ; //Magma
		case 19 : return ; //Forge
	*/
		default : return 99999999;
		}
	}
	public static int getDurationTime(int id)
	{
		switch (id) {
			case 0 : return 5*60; //Cult (7day max should only be called for settings)
			case 1 : return 20*60; //Jacob
			case 2 : return 0; //Dark Auction
			case 3 : return 0; //Enchant Table
			case 4 : return 0; //Fetcher
			case 5 : return 0; //Puzzler
			case 6 : return 0; //Commision
			case 7 : return 0; //Voting 117*3600-but have the new mayor to show the timing
			case 8 : return 0; //New Mayor
			case 9 : return 3600; //New Years
			case 10 : return 3600; //Zoo
			case 11 : return 0; //Bank
			case 12 : return 3600; //Xmas
			case 13 : return 3600; //Spooky
		/*	case 14 : return 0; //Garry
			case 15 : return 3600; //Marina Fish
			case 16 : return 3600*5; //Mine Fiesta
			case 17 : return 0; //unknown
			case 18 : return 0; //Magma
			case 19 : return 0; //Forge
		*/
			default : return 0;
		}
		
    }
    public static int pullFromCalendar(String name)
    {
    	return 999999;
    }
    
    
}
