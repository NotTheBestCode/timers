package GreatKhanz.Timers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;



public class Settings {

	static int[] maxTime = 
		{	10, 2, 3, 5, 60, 60, 60, 5, 60, 60, 5, 5, 60, 60, 1, 60, 30, 60, 5, 5		};
	static TimerSettings[] tSetting = new TimerSettings[maxTime.length+1];
	static int lastPuzzler;
	static int posX=5;
	static int posY=5;
	static String settingInfo = "\n\nSettings saved like this (dont erase anything, change values, keep space:space)\n"
			+ "DisplayName : Enabled : SoonTime : DisplayTime"
			+ "\n DisplayName what you want it to be called)"
			+ "\n Enabled if you want it to be displayed"
			+ "\n SoonTime when to show as yellow/soon (seconds til event)"
			+ "\n DisplayTime when you want to become visible (seconds til event)"
			+ "\n\nlastPuzzler is unix of last puzzler completion"
			+ "\npos:x:y sets the display location"
			+ "\n\nTimers that do not currently work:\n\r\n" + 
			"Garry:Time varies by up to 10 minutes between servers\n" + 
			"Marina Fish:Need to be able to pull calendar events\n" + 
			"Mine Fiesta:Need to be able to pull calendar events\n"	+ 
			"Jacob:Works but Need to be able to pull calendar events for crop types\n" + 
			"unknown:Was going to add a /setTimer but dont know how to do /commands\n" + 
			"Magma:Couldnt understand SBA code for this, querying website etc\n" + 
			"Forge:Not sure how to detect (doesnt give a chat notification on start)";
	
	static boolean init()
	{
		for (int i=0;i<maxTime.length;i++)
			tSetting[i] = new TimerSettings();
			
		if (loadFile())
			return true;
		loadDefaults();
		return false;
	}
	static void loadDefaults()
	{
		for (int i=0;i<maxTime.length;i++)
		{
			tSetting[i].switchToSoon = (int)maxTime[i]*60;
			tSetting[i].enabled = true;
		}
		lastPuzzler = (int)(System.currentTimeMillis() / 1000L);
	}
	static void saveText(String text)  //used this to find the puzzler reset
	{
		String t = "";
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(
					"testT.txt"));
			String line = reader.readLine();
			while (line != null) {
				t = t + line + "\n";
				// read next line
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		t += text;
		PrintWriter out;
		try {
			out = new PrintWriter("testT.txt");
		    out.println(t);
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} 
		
	}
	static boolean loadFile()
	{
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(
					"timerSettings.txt"));
			String line = reader.readLine();
			String name;
			int index=0;
			while (line != null) {
	//			System.out.println(line);

				int i=0;
				for (String s: line.split(" : ")) {
	//		         System.out.println(index + ": " + s + " : " + i);

					if (index < 20)
					{
						if (i == 0)
							tSetting[index].name = s;
						else if (i == 1)
							tSetting[index].enabled = Boolean.parseBoolean(s);
						else if (i == 2)
							tSetting[index].switchToSoon = Integer.parseInt(s);
						else if (i == 3)
							tSetting[index].switchToVisible= Integer.parseInt(s);
					}
					else if (index == 20 && i == 1)
						lastPuzzler = Integer.parseInt(s);
					else if (index == 21 && i == 1)
						posX = Integer.parseInt(s);
					else if (index == 21 && i == 2)
						posY = Integer.parseInt(s);
			        i++;
			        
			      }
				index++;
				// read next line
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
//		    System.out.println(": ");
		return true;
	}
	static void saveFile()
	{
		String timers = "";
		for (int i=0;i<maxTime.length;i++)
		{
			timers+= tSetting[i].name + " : ";
			timers+= tSetting[i].enabled + " : ";
			timers+= tSetting[i].switchToSoon + " : ";
			timers+= tSetting[i].switchToVisible;
			timers+="\n";
		}
		timers += "lastPuzzler : " + lastPuzzler + "\n";
		timers += "pos : " + posX + " : " + posY;
		System.out.println(timers);
		
		PrintWriter out;
		try {
			out = new PrintWriter("timerSettings.txt");
		    out.println(timers + settingInfo);
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} 

	}
	static void setName(int id, String name)
	{
		tSetting[id].name = name;
	}
	static String getName(int id)
	{
		return tSetting[id].name;
	}
	
	static int getSwitchToSoon(int id)
	{
		return tSetting[id].switchToSoon;
	}
	static void setSwitchToSoon(int id, int value)
	{
		tSetting[id].switchToSoon = (int) value;
	}
	static int getLastPuzzler() //saved in unix
	{
		return lastPuzzler;
	}
	static void setLastPuzzler()
	{
		lastPuzzler = (int)(System.currentTimeMillis() / 1000L);
		saveFile();
	}
	static int getVisibleTime(int id)
	{
		return tSetting[id].switchToVisible; //always show for now
	}
	static void setVisibleTime(int id, int value)
	{
		tSetting[id].switchToVisible = (int) value;
	}
	static void setMaxTime(int id, int value)
	{
		maxTime[id]= (int) value;
	}
	static boolean enabled(int id)
	{
		return tSetting[id].enabled;
	}
}
