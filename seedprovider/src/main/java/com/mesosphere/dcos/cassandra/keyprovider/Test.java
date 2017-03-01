package com.mesosphere.dcos.cassandra.keyprovider;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class Test {

	
	public static void main(String arg[]) {
		//https://s3-us-west-1.amazonaws.com/cassandra-arun-bucket/test.txt
			
			
		try {
			URL url = new URL("https://s3-us-west-1.amazonaws.com/cassandra-arun-bucket/adobe.keystore");
		Scanner s = new Scanner(url.openStream());
		System.out.println(s.nextLine());
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
}
