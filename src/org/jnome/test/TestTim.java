package org.jnome.test;

import javax.swing.JDialog;

public class TestTim{

	
	
	private int [][] i = {{1,2},{3,4}};
	private int [][] j = new int[5][6];
	private int [][] k = new int[5][];
	private int [] l = {1,2,};
	private int [] m [] = new int [1][2];
	
	
	public double globalDouble = 5; 
	private JDialog jd = new JDialog();
	
	public static void main(String args []){
		System.out.println("test main");	
	}	
	
	private double getGlobalDouble(){
		return globalDouble;	
	}
} 