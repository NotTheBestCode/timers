package GreatKhanz.Timers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumChatFormatting;

public class Timer {
	String name;
	long nextUnixTime; //time till next event (seconds)
	int duration; //time event lasts (seconds)
	int soonTime; //when to change to soon color
	int visTime; //when to become visible
	int x=5,y=5; //where timer exists
	int posX=5,posY=5;
	boolean active = false;
	boolean visible = false; //show or not
	boolean enabled = false;
	FontRenderer text;
	FontRenderer textShadow;
	int mode=2; //0="ACTIVE", 1="SOON", 2="Old"
	Object[] modeColors = {
			EnumChatFormatting.RED, //active
			EnumChatFormatting.YELLOW, //soon
			EnumChatFormatting.GREEN //old
		};


	void init(String n,int next, int dur, int sT, int vTime, boolean e)
	{
		name = n;
		nextUnixTime = next + System.currentTimeMillis() / 1000L;
		duration = dur;
		enabled = e;
		visTime = vTime;
		soonTime = sT;
	}
	void setTime(int next)
	{
		nextUnixTime = next + System.currentTimeMillis() / 1000L;		
	}
	void setPos(int i)
	{
		y = posY + i*10;
	}
	void show()
	{
		visible = true;
		update();
	}
	void hide()
	{
		text = null;
		textShadow = null;
		visible = false;
	}
	void toggle()
	{
		visible = !visible;			
	}
	boolean visible()
	{
		return visible;

	}
	void update()
	{
		if (visible)
		{
	//		textShadow = Minecraft.getMinecraft().fontRendererObj;
	//		textShadow.drawString(EnumChatFormatting.BLACK + formatTime()+" " + name, x+0, y+1, 0);
	//		textShadow.drawString(EnumChatFormatting.BLACK + formatTime()+" " + name, x+0, y-1, 0);
	//		textShadow.drawString(EnumChatFormatting.BLACK + formatTime()+" " + name, x+1, y+0, 0);
	//		textShadow.drawString(EnumChatFormatting.BLACK + formatTime()+" " + name, x-1, y+0, 0);
			text = Minecraft.getMinecraft().fontRendererObj;			
			text.drawString(EnumChatFormatting.WHITE + formatTime()+" "+modeColors[mode] + name, posX, y, 0);
		}
		else
		{
			text = null;
			textShadow = null;
		}
	}
	int timeTil()
	{
		int timeUntil = (int) (nextUnixTime-System.currentTimeMillis() / 1000L);
		
		if (!enabled)
			return 99999;
		else if (timeUntil < visTime || active)
			visible = true;
		else
			visible = false;
		
		if (timeUntil > soonTime && !active)
			mode = 2;
		else if (timeUntil < 0) //timer expired
		{
			if  (!active) //event started
			{
				mode = 0;
				nextUnixTime = duration + System.currentTimeMillis() / 1000L;
				timeUntil = (int)(nextUnixTime-System.currentTimeMillis() / 1000L);
			}
			else
			{
				mode = 2;
			}
			active = !active;
		}
		else if (!active)
			mode = 1;
		return timeUntil;
		 
	}
	//seconds to time format
	String formatTime()
	{

		long unixTime = System.currentTimeMillis() / 1000L;
		int time = (int)(nextUnixTime - unixTime);
		return formatTime(time);
	}
	String formatTime(int time)
	{
		time = Math.abs(time);
		String r = "";
		int days = time / 86400;
		if (days > 0)
		{
			r += days + "D, ";
			if (((time%86400) / 3600) > 0)
				r += (time%86400) / 3600 + "H";
	//		if (((time%3600) / 60) > 0)
	//			r += (time%3600) / 60 + "M";
			return r;
		}
		r+= (time%86400) / 3600 + ":";
		int min = (time%3600) / 60;
		if (min < 10)
			r+= "0";
		r+= min + ":";
		int sec = time%60;
		if (sec <10)
			r+= "0";
		r+= sec;
		return r;
	}
	void print()
	{
		int timeUntil = (int) (nextUnixTime-System.currentTimeMillis() / 1000L);
		long unixTime = System.currentTimeMillis() / 1000L;
		//	System.out.println(name + ": " + nextUnixTime + " : " + (nextUnixTime-unixTime) + " [] " + formatTime());
		System.out.println(name + ": " + formatTime() + " MODE: + " + mode + " TimeTil: "+ timeUntil + " soont:" +soonTime+ " dur" +duration + active);
		System.out.println(name + ": " + formatTime() + " visTime: + " + visTime + " visible: "+ visible + " enabled:" +enabled );
		
	}

}
