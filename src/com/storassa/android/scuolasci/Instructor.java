package com.storassa.android.scuolasci;

public class Instructor {
	private final String name;
	private final String surname;
	private final String[] discipline = new String[2];
	private final String[] skiArea = new String[3];
	
	public Instructor(String _name, String _surname, String[] _discipline, String[] _skiArea) {
		name = _name;
		surname = _surname;
		
		int count = 0;
		for (String disc : _discipline)
			discipline[count++] = disc;
		for (String area : _skiArea)
			skiArea[count++] = area;
	}
	
	public String[] getSkiArea() {
		return skiArea;
	}
	
	public String[] getDiscipline() {
		return discipline;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSurname() {
		return surname;
	}
}
