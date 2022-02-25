package com.dayton.websci.assignment1;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;


public class Assignment1
{
	static class ArtistRow{
		String artist;
		String country;
		String movie;
		ArtistRow(String artist, String country, String movie) {
			this.artist = artist;
			this.country = country;
			this.movie = movie;
		}
	}
	private static final String FILE_PATH = "C:/Users/public/data.csv";

	public static void main(String[] args) {
		List<ArtistRow> artists = readCsv();
		generateRdf(artists);

	}
	private static List<ArtistRow> readCsv() {
		List<ArtistRow> artists = new ArrayList<>();
		File file = new File(FILE_PATH);
		if(!file.exists()) {
			System.out.println("File is missing");
		}
		else {
			try {
				FileReader filereader = new FileReader(file);
				Scanner sc = new Scanner(filereader);
				String line = "";
				while(sc.hasNextLine()) {
					line=sc.nextLine();
					String[] spitLine = line.split(",");
					artists.add(new ArtistRow(spitLine[0],spitLine[1], spitLine[2]));
				}
			}
			catch(IOException io) {
				io.printStackTrace();
			}
		}
		return artists;
	}

	private static void generateRdf(List<ArtistRow> artists) {
		Model model = ModelFactory.createDefaultModel();
		Property movie = model.createProperty("http://mani.org/property/movie");
		Property country =  model.createProperty("http://mani.org/property/country");
		for(ArtistRow artist: artists) {
			Resource actor = model.createResource("http://mani.org/property/"+artist.artist);
			actor.addProperty(movie, artist.movie);
			actor.addProperty(country, artist.country);
		}
		model.write(System.out, "TURTLE");
		try {
			Writer wr = new FileWriter("C:/Users/public/output.ttl");
			model.write(wr,"TURTLE");}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
