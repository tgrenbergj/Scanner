public class MiniRERunner {

	public static void main(String[] args) {

		if(args.length<1 || args.length>1){
			System.out.println("Usage: java MiniRERunner script.txt");
			return;
		}
		MiniREParser mrp = new MiniREParser(args[0], "minire_spec.txt");
		mrp.run();
	}

}
