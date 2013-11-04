package com.storassa.android.scuolasci;

import java.util.ArrayList;

public class Instructor {
	private final String name;
	private final String surname;
	private final ArrayList<String> discipline = new ArrayList<String>();
	private final ArrayList<String> skiArea = new ArrayList<String>();
	
	public Instructor(String _name, String _surname, String[] _skiArea, String[] _discipline) {
		name = _name;
		surname = _surname;
		
		for (String disc : _discipline)
			discipline.add(disc);
		
		for (String area : _skiArea)
			skiArea.add(area);
	}
	
	public ArrayList<String> getSkiArea() {
		return skiArea;
	}
	
	public ArrayList<String> getDiscipline() {
		return discipline;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSurname() {
		return surname;
	}
}
