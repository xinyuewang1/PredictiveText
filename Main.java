import dsai.core.List;
import dsai.impl.LinkedList;

/**
 * This class implements the main method for the application. You should
 * run this class. You will need to modify this file to load the dictionary
 * as is instructed in question A4.
 * 
 * @author remcollier
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Dictionary dictionary = new Dictionary();
                
                // TODO: Uncomment the line below to load a dictionary
                //       file at startup.
                dictionary.load("testDict");
//                System.out.println(dictionary);
//                List<Integer> l = new LinkedList<Integer>();
//        		l.insertLast(6);
//        		l.insertLast(6);
//        		l.insertLast(8);
//                System.out.println(dictionary.findWords(l));
                new Keyboard(new TextSystem(dictionary)).setVisible(true);
            }
        });
    }

}
