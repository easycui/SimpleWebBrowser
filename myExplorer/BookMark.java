package myExplorer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


class Mark
{
	public String names;
	public String urls;
}

//implement the class to handle bookmarks
public class BookMark {
	List<Mark> list=new ArrayList<Mark>();
	public BookMark()
	{
		String info;
		try {
			BufferedReader readBookmark=new BufferedReader(new FileReader(new File("d://bookmark")));
			try {
				int i=0;				
				while((info=readBookmark.readLine())!=null)
				{
					Mark mark=new Mark();

					String[] key=info.split("@",2);
					System.out.println(info);
					System.out.println("name:"+key[0]);
					System.out.println("url:"+key[1]);
					mark.names=key[0];
					mark.urls=key[1];
					list.add(mark);
					i++;
				}			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getBookmarkName(int i)
	{		
		String name=((Mark)list.get(i)).names;
		return name;
	}
	public String getBookmarkUrl(int i)
	{
		String url=((Mark)list.get(i)).urls;
		return url;
	}
	public void addBookmark(String name, String url)
	{
		Mark mark=new Mark();
		mark.names=name;
		mark.urls=url;
		list.add(mark);
		writeToFile();
	}
	public void deleteBookmark(int i)
	{
		list.remove(i);
		writeToFile();
	}
	void writeToFile()
	{
		BufferedWriter writeBookmark=null;
		try {
			writeBookmark = new BufferedWriter(new FileWriter(new File("d://bookmark")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0;i<list.size();i++)
		{
			try {
				writeBookmark.write(((Mark)list.get(i)).names+"@"+((Mark)list.get(i)).urls+"\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			writeBookmark.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int length()
	{
		return list.size();
	}
	public void rename(int i,String name)
	{
		Mark mark=new Mark();
		mark.names=name;
		mark.urls=((Mark)list.get(i)).urls;
		list.remove(i);
		list.add(i,mark);
		writeToFile();
	}
}
