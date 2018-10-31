
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import dsai.core.Iterator;
import dsai.core.List;
import dsai.core.Position;
import dsai.impl.LinkedList;
import dsai.impl.ListIterator;
import dsaii.core.Map;
import dsaii.core.Tree;
import dsaii.impl.ChainMap;
import dsaii.impl.LinkedTree;

/**
 * This class implements the dictionary component of the predictive text
 * module of our mobile phone demonstrator. Underlying the implementation
 * is a tree data structure in which each node contains:
 * 
 * <ul>
 *   <li> an integer value representing the current keystroke
 *   <li> a list of strings that contains the word fragments that correspond to
 *        the sequence of keystrokes in the path from the root node to that
 *        node.
 * </ul>
 * 
 * This node data is modelled through the inner Keystroke class.
 * 
 * @author remcollier
 */
public class Dictionary {
	/**
	 * This class represents the contents of each node in the tree-based
	 * implementation of the dictionary. Each node basically represents
	 * a single keystroke, this class associates that keystroke with a list
	 * of words the sequence of keystrokes corresponding to the path from the
	 * root node to this node.
	 */
	private class Keystroke {
		int key;
		private List<String> words;

		/**
		 * Constructor for the Keystroke class, that takes an integer (the
		 * keystroke) as a parameter.
		 * 
		 * @param key
		 */
		public Keystroke(int key) {
			this.key = key;
			words = new LinkedList<String>();
		}

		/**
		 * Add another word to this node (this means that the word is a
		 * potential word for the combination of keystrokes that matches
		 * the path from the root node to this node).
		 * 
		 * @param word
		 */
		public void addWord(String word) {
			//			Check if list is empty, 
			//				if empty, add new stuff;
			//				if not, check duplication.
			if (! words.isEmpty()) {
//				System.out.println("words is not empty");
				boolean found = false;
				Position<String> node = words.first();
				while (node != words.last()) {
//					System.out.println("Not the last yet");
					if (node.element().equals(word)) {
//						System.out.println("words: "+words);
						found = true;
						words.insertFirst(word);
						words.remove(node);
//						words.insertLast(word);
						break;
					}
					node = words.next(node);
				}
				//				The while loop haven't check the last element
				if (!found && ! words.last().element().equals(word)) {
//					System.out.println("words.last() != word");
//					System.out.println(words.last().element()+" and "+word);
					words.insertLast(word);
				}
				else if (!found && words.last().element().equals(word)) {
//					System.out.println("words.l?ast() == word");
					words.insertFirst(word);
					words.remove(words.last());
//					words.insertLast(word);
				}
			} else
				words.insertLast(word);
		}

		/**
		 * Return the list of words that is associated with this
		 * keystroke. The current implementation does not impose any
		 * ordering on the list (it is built based on the order in
		 * which words are inserted into the node). In part B of the
		 * assignment, you will need to modify this method to return
		 * an ordered list of words.
		 * 
		 * @return a list of words
		 */
		public List<String> getWords() {
			return words;
		}

		/**
		 * Generate a string representation of the node data for outputing
		 * of the state of the tree during testing.
		 * 
		 * @return
		 */
		@Override
		public String toString() {
			StringBuffer buf = new StringBuffer();
			buf.append(key);
			buf.append(":");
			Iterator<String> it = new ListIterator<String>(words);
			while(it.hasNext()) {
				buf.append(" ");
				buf.append(it.next());
			}
			return buf.toString();
		}
	}

	/**
	 * This map associates characters with keystrokes and is used by the
	 * insertion algorithm to work out how to add words to the tree.
	 */
	private static Map<Character, Integer> characterMap;

	/**
	 * Initialization block for the characterMap
	 */
	static {
		characterMap = new ChainMap<Character, Integer>();
		characterMap.put('a',2);
		characterMap.put('b',2);
		characterMap.put('c',2);
		characterMap.put('d',3);
		characterMap.put('e',3);
		characterMap.put('f',3);
		characterMap.put('g',4);
		characterMap.put('h',4);
		characterMap.put('i',4);
		characterMap.put('j',5);
		characterMap.put('k',5);
		characterMap.put('l',5);
		characterMap.put('m',6);
		characterMap.put('n',6);
		characterMap.put('o',6);
		characterMap.put('p',7);
		characterMap.put('q',7);
		characterMap.put('r',7);
		characterMap.put('s',7);
		characterMap.put('t',8);
		characterMap.put('u',8);
		characterMap.put('v',8);
		characterMap.put('w',9);
		characterMap.put('x',9);
		characterMap.put('y',9);
		characterMap.put('z',9);
	}

	/**
	 * The tree
	 */
	private Tree<Keystroke> tree;

	/**
	 * Default Constructor that creates an empty dictionary.
	 */
	public Dictionary() {
		tree = new LinkedTree<Keystroke>();
		tree.addRoot(new Keystroke(-1));
	}

	/**
	 * Load the specified dictionary file. Each word in the file must
	 * be inserted into the dictionary.
	 * 
	 * @param filename the dictionary file to be loaded
	 */
	public void load(String filename) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException ex) {
			System.out.println("No Such File: " + filename);
		}
		try {
			String line = in.readLine(); 
			while(line != null) {
				// reads a line from the file or returns
				//				System.out.println(line);
				insert(line);
				line = in.readLine();
			}
			// null if there is no more to read
		} catch (IOException ioe) {
			System.out.println("Error reading from: " + filename);
		}
		try {
			in.close(); // closes the file
		} catch (IOException ioe) {
			System.out.println("Error closing: " + filename);
		}


	}

	/**
	 * Insert the word into the dictionary. This algorithm loops through the
	 * characters in the word, and uses the character map to work out what
	 * keystroke should be used to select that character (e.g. a,b,c would
	 * be selected by pressing the 2 key).
	 * 
	 * For each sequence of keystrokes, the substring that corresponds to that
	 * sequence is stored at the corresponding node so that 
	 * 
	 * @param word
	 */
	public void insert(String word) {
		// TODO: This method must be completed for question A2
		int[] sequence = new int[word.length()];
		Position<Keystroke> p = tree.root();
		String fragment="";
		for (int i=0; i<word.length(); i++) {
			sequence[i] = characterMap.get(word.charAt(i));
			Iterator<Position<Keystroke>> iter = tree.children(p);
			fragment+=word.charAt(i);
			if (!iter.hasNext()) {
				Keystroke key = new Keystroke(sequence[i]);
				p = tree.addChild(p, key);
				p.element().addWord(fragment);
			} 
			else { //it's not empty, then compare and match.
				boolean flag = false;
				Position<Keystroke> pointer = p;
				while (iter.hasNext()) {
					pointer = iter.next();
					if (pointer.element().key == sequence[i]) { //Match keystroke
						List<String> keyContains = pointer.element().getWords();
						Position<String> elem = keyContains.first();
						while (elem != keyContains.last()) { //compare elements in list from 0 to -2
							if(elem.element().equals(fragment)) {
								flag = true;
//								pointer.element().getWords();
								p = pointer;
								p.element().addWord(fragment);
								break;
							}
							elem = keyContains.next(elem);
						}
						if (elem.element().equals(fragment)) {
							flag = true;
							p = pointer;
							break;
						}
						if (!flag) {
							//Means key stroke is the same key stroke, but the segment doesn't exist
							p = pointer;
							p.element().addWord(fragment);
							flag=true;
						}
					}	
				}
				if (!flag) {
					Keystroke key = new Keystroke(sequence[i]);
					p = tree.addChild(p, key);
					p.element().addWord(fragment);
				}
			}
		}
	}

	/**
	 * Output the state of the dictionary (via delegation to the underlying
	 * tree implementation).
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		return tree.toString();
	}

	/**
	 * Find the list of words that corresponds to the given sequence of
	 * keystrokes.
	 * 
	 * @param keystrokes a sequence of keystrokes
	 * 
	 * @return a list of words
	 */
	public List<String> findWords(List<Integer> keystrokes) {

		/**
		 * The default null response will cause the invoking method to
		 * create a default word based on the sequence of keystrokes (i.e.
		 * the same behaviour as if there was not entry in the dictionary
		 * for the sequence of keystrokes).
		 */
		List<String> words = null;
		Position<Keystroke> p = tree.root();
		Position<Integer> enter = keystrokes.first();
		Iterator<Position<Keystroke>> iter = tree.children(p);
		//		Position<Keystroke> pointer = p;
		while (iter.hasNext()) {
			p = iter.next();
			//			System.out.println("Iter"+iter+"pointer"+pointer+"p"+p);
			if (p.element().key == enter.element()) { //Match keystroke
				//				System.out.println(p.element());
				words = p.element().getWords();
				//				System.out.println(words);
				if (enter != keystrokes.last()) {
					enter = keystrokes.next(enter);
					//					p = pointer;
					iter = tree.children(p);
				} else {
					return words;
				}
			}
		}
		return words;
	}

	static public void main(String[] args) {
		Dictionary dict = new Dictionary();
		Keystroke keyStroke = dict.new Keystroke(2);
		//		System.out.println("TEST: "+keyStroke);
		System.out.println(keyStroke.getWords().toString());
		keyStroke.addWord("apple");
		System.out.println(keyStroke.getWords().toString());
		keyStroke.addWord("alice");
		System.out.println(keyStroke.getWords().toString());
		keyStroke.addWord("apple");
		System.out.println(keyStroke.getWords().toString());
		keyStroke.addWord("arrow");
		System.out.println(keyStroke.getWords().toString());

		System.out.println(dict.toString());
		dict.insert("apple");
		System.out.println(dict.toString());
		dict.insert("alice");
		System.out.println(dict.toString());
		dict.insert("apple");
		System.out.println(dict.toString());
		dict.insert("arrow");
		System.out.println(dict.toString());

		List<Integer> l = new LinkedList<Integer>();
		l.insertLast(2);
		System.out.println(dict.findWords(l));
		l.insertLast(7);
		System.out.println(dict.findWords(l));
		l.insertLast(7);
		System.out.println(dict.findWords(l));
		l.insertLast(5);
		System.out.println(dict.findWords(l));
		l.insertLast(3);

		System.out.println(dict.findWords(l));

		dict.load("testDict");
		System.out.println(dict.toString());

		List<Integer> l1 = new LinkedList<Integer>();
		l1.insertLast(6);
		System.out.println(dict.findWords(l1));
		l1.insertLast(6);
		System.out.println(dict.findWords(l1));
		l1.insertLast(8);
		System.out.println(dict.findWords(l1));

		//		System.out.println(dict.tree.children(dict.tree.root()));
		
		List<Integer> l2 = new LinkedList<Integer>();
		l2.insertLast(2);
		System.out.println(dict.findWords(l2));
		l2.insertLast(4);
		System.out.println(dict.findWords(l2));
		l2.insertLast(3);
		System.out.println(dict.findWords(l2));

	}
}


