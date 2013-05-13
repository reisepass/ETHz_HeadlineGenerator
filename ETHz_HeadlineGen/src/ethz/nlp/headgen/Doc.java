package ethz.nlp.headgen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import ethz.nlp.headgen.data.WordCountTree;
import ethz.nlp.headgen.io.ParsedDocReader;
import ethz.nlp.headgen.sum.Summerizer;

public class Doc  {
	public Doc() {
		
	}
	public Doc( String cont) {
		super();

		this.cont = cont;

	}
	public Doc(String docno, String docType, String textType, String cont,
			File f) {
		super();
		this.docno = docno;
		this.docType = docType;
		this.textType = textType;
		this.cont = cont;
		this.f = f;
	}

	public String docno;
	public String docType;
	public String textType;
	public String cont;
	private HashSet<String> presentWords;
	public File f;
	private Annotation annotation;
	public String summary;
	public List<String> models;
	public WordCountTree wordCounts;

	@Override
	public String toString() {
		return "Doc [docno=" + docno + ", docType=" + docType + ", textType="
				+ textType + "]";
	}


	public void setAno(Annotation in){
		this.annotation = in;
	}

	
	
	public Annotation getAno(){
		if( this.annotation==null){
			return null;
		}
		else 
			return this.annotation;
	}
	
	public String getParentDirName() {
		return f.getParentFile().getName();
	}

	public String getAnotFileName() {
		return f.getName() + ".parsed";
	}
	
	private void initPresentWords(){
		String[] wordRay = cont.split(" "); 
		for(String el : wordRay)
			el=el.trim();	//TODO check if this actually changes the strings in wordRay 
		presentWords = new HashSet<String>((Collection<String>)Arrays.asList(wordRay));
	}
	public boolean contains(String quer){
		if(presentWords==null){
			initPresentWords();
		}
		if(quer.indexOf(" ")!=-1){
			String [] splitup = quer.split(" ");
			int found =0;
			for(String el : splitup){
				if(this.contains(el.trim())){
					found++;
				}
			}
			return found==splitup.length;
		}
		
		return presentWords.contains(quer);
	}
	public boolean containsAll(ArrayList<String> query){
		if(presentWords==null)
			initPresentWords();
		boolean allThere = true;
		for(String el : query){
			if(!this.contains(el)){
				allThere=false;
				break;
			}
		}
		return allThere;
	}
	public boolean containsOneOrMore(ArrayList<String> query){
		if(presentWords==null)
			initPresentWords();

		for(String el : query){
			String[] splitt = el.split(" ");
			if(this.contains(el)){
				return true;
			}
		}
		return false;
	}
	
}
