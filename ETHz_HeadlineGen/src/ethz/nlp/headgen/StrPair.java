package ethz.nlp.headgen;

public class StrPair implements Comparable<StrPair> {

		private String one;
		private String two;
		public StrPair(String one, String two) {
			super();
			this.one = one;
			this.two = two;
		}
		public boolean equals(StrPair in){
			if(one.equals(in.getOne())){
				if(two.equals(in.getTwo())){
					return true;
				}
			}
			if(one.equals(in.getTwo())){
				if(two.equals(in.getOne())){
					return true;
				}
			}
			return false;
		}
		public int compareTo(StrPair in){
			if(one.equals(in.getOne())){
				if(two.equals(in.getTwo())){
					return 0;
				}
			}
			if(one.equals(in.getTwo())){
				if(two.equals(in.getOne())){
					return 0;
				}
			}
			return (one+two).compareTo(in.getOne()+in.getTwo());
		}
		
		public String getOne() {
			return one;
		}
		public void setOne(String one) {
			this.one = one;
		}
		public String getTwo() {
			return two;
		}
		public void setTwo(String two) {
			this.two = two;
		}
		@Override
		public String toString() {
			return "StrPair [one=" + one + ", two=" + two + "]";
		}
		
		
	
}
