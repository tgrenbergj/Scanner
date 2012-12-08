

public class MiniRERunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Commented for testing
//		if(args.length<1 || args.length>0){
//			System.out.println("Incorrect number of parameters");
//			return;
//		}
		MiniREParser mrp = new MiniREParser("src\\input_phase2\\script.txt", "src\\input_phase2\\minire_spec.txt");
		mrp.run();
	}

}
