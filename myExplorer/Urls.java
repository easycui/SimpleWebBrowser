package myExplorer;

import java.util.ArrayList;
import java.util.List;

//implement the urls class to store urls and deal with back and forward
public class Urls {

	private int current = -1;
	private int top = -1;
	private List<String> list = new ArrayList<String>();

	public String Back() {
		if (current > 0) {
			current -= 1;
		}
		return (String) list.get(current);
	}

	public void Add(String url) {
		top += 1;
		current = top;
		list.add(top, url);
	}

	public String Forward() {
		if (current < top) {
			current += 1;
		}
		return (String) list.get(current);
	}

	public boolean isTop() {
		if (current == top) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isBottom() {
		System.out.println("current:" + current);
		if (current < 1) {
			return true;
		} else {
			return false;
		}
	}

	public String getUrl() {
		if (current > -1) {
			return (String) list.get(current);
		} else {
			return "";

		}
	}
}
