package ethz.nlp.headgen;

import java.util.ArrayList;
import java.util.Comparator;

import ethz.nlp.headgen.sum.FirstSentSum;

public class Constants {

	public Constants() {
		// TODO Auto-generated constructor stub
	}

	public static Comparator<ArrayList<String>> CompareObj=  new Comparator<ArrayList<String>>(){
		public int compare(ArrayList<String> o1, ArrayList<String> o2) {
		
				if(o1.size()!=o2.size()){
					int size = o1.size();
					if(o2.size()< o1.size())
						size=o2.size();
					int result=0;
					for(int i=0; i<size;i++){
						if(o1.get(i).equals(FirstSentSum.WILDCARD_STRING))
							continue;
						if(o2.get(i).equals(FirstSentSum.WILDCARD_STRING))
							continue;
						if(o1.get(i).equals(o2.get(i)))
							continue;
						else
							result = o1.get(i).compareTo(o2.get(i));
					}
					if(result==0){
						return result;
					}
					else{
						for(int j=size-1; j>=0 ; j--){
							if(o1.get(j).equals(FirstSentSum.WILDCARD_STRING))
								continue;
							if(o2.get(j).equals(FirstSentSum.WILDCARD_STRING))
								continue;
							if(o1.get(j).equals(o2.get(j)))
								continue;
							else
								result = o1.get(j).compareTo(o2.get(j));
						}
						return result;
						
					}

				}
				else{
					
					for(int i=0;i<o1.size();i++){
							if(o1.get(i).equals(FirstSentSum.WILDCARD_STRING))
								continue;
							if(o2.get(i).equals(FirstSentSum.WILDCARD_STRING))
								continue;
							if(o1.get(i).equals(o2.get(i)))
								continue;
							else
								return o1.get(i).compareTo(o2.get(i));
						
					}
					return 0;
					
					
				}
			
			
			
        }} ;

}
