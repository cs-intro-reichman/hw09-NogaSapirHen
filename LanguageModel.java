import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
        String window = "";
        char c ;
        In in = new In (fileName);
       for ( int i = 0; i < this.windowLength; i ++)
       {
        window += in.readChar();
       }
        while (!in.isEmpty())
        {
            c = in.readChar();
            List probs = this.CharDataMap.get(window);
            if (probs == null)
            {
                probs = new List();
                this.CharDataMap.put(window, probs);
            }
            probs.update(c);
            window = window.substring(1) + c;
        }
            for (List probs : this.CharDataMap.values()) {
                calculateProbabilities(probs);
            }
	}
    
    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	public void calculateProbabilities(List probs) {				
        Node current = probs.getFirstNode();
        int len = 0;
        while (current != null) {
            len += current.cp.count;
            current = current.next;
        }
       Node temp = probs.getFirstNode();
        double newP = 0;
        double prevCP = 0;
        while (temp != null ) {
             newP = (double) (temp.cp.count)/len;
             temp.cp.p = newP;
             temp.cp.cp = newP + prevCP;
             prevCP = temp.cp.cp;
             temp = temp.next;
        }
       
	}

    // Returns a random character from the given probabilities list.
	public char getRandomChar(List probs) {
        double r = randomGenerator.nextDouble();
        Node current = probs.getFirstNode();
        while (current != null ) {
            if (r < current.cp.cp)
            {
                return current.cp.chr;
            }
            current = current.next;
        }
        //if r is 0.99999
        return (probs.getLast().cp.chr);
	}

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
        if (initialText.length() < this.windowLength){
        return initialText;
        }
        StringBuilder generatedText = new StringBuilder(initialText);
        String window = initialText.substring (0, this.windowLength);
        for (int i = 0; i <= textLength; i ++)
        {
            List probs = this.CharDataMap.get(window);
            if (probs == null) {
                return generatedText.toString();
            }
            char chr = getRandomChar(probs);
            generatedText.append(chr);
            window = window.substring(1) + chr;
        }
        generatedText.deleteCharAt(generatedText.length()-1);
        return generatedText.toString();
	}

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {
		// Your code goes here
        List hi = new List();
        hi.addFirst(' ');
        hi.addFirst ('e');
        hi.addFirst('t');
        hi.addFirst('i');
        hi.addFirst('m');
        hi.addFirst('o');
        hi.addFirst('c');
        System.out.println(hi);
    }
}
