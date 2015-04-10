import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.JOptionPane;
import java.awt.Robot;
import java.awt.AWTException;
import java.awt.event.InputEvent;
import java.awt.MouseInfo;
import java.awt.Color;
import java.awt.datatransfer.*;
import java.awt.Toolkit;
 
public class x extends JFrame {
    //the address of the roku
    private String address = "";
    //the roku object for later
    private Roku r = new Roku("");

    private JTextField txt1 = new JTextField("Use the arrow keys/enter to control.");
    private JLabel txt2 = new JLabel("To enter text type normally.");
    private JLabel txt3 = new JLabel("To send home press Escape.");
    private JLabel txt4 = new JLabel("To exit to terminal Alt + F4.");
     
    //key action handler
    private keyPressedHandler kpHandler;
     
    public class keyPressedHandler implements KeyListener {   
        
	public void keyTyped(KeyEvent e) {
            //do nothing
        }
        
	// /** Handle the key pressed event from the text field. */
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == 37) {
		//System.out.println("Left");
		r.keypress("left");
	    } else if (e.getKeyCode() == 38) {
		//System.out.println("Up");
		r.keypress("up");
	    } else if (e.getKeyCode() == 39) {
		//System.out.println("Right");
		r.keypress("right");
	    } else if (e.getKeyCode() == 40) {
		//System.out.println("Down");
		r.keypress("down");
	    } else if (e.getKeyCode() == 10) {
		//System.out.println("Enter");
		r.keypress("select");
	    } else if (e.getKeyCode() == 8) {
	    	//System.out.println("Backspace");
		r.keypress("backspace");
	    } else if (e.getKeyCode() == 27) {
	    	//System.out.println("Escape");
		r.keypress("home");
	    } else if (e.getKeyCode() == 32) {
	    	//System.out.println("Space");
		r.keypress("Lit_+");
	    } else {
		String pressed = Character.toString((char) e.getKeyCode());
		//System.out.println(e.getKeyCode());
		r.keypress("Lit_" + pressed.toLowerCase());
	    }
	}
        
	public void keyReleased(KeyEvent e) {
            //do nothing
        }

    }
     
    public x(String addr) {
	//update the address with the value provided
	address = addr;
	//update the roku object
	r = new Roku(address);
    	
        //specify handler for key listener
        kpHandler = new keyPressedHandler();
        txt1.addKeyListener(kpHandler);
         
        Container pane = getContentPane(); //get HWND for GUI frame
        pane.setLayout(new GridLayout(4, 1)); //set the layout type to a 1 X 1 grid
        
	//make top button look like label
	txt1.setBorder(BorderFactory.createLineBorder(new Color(-1118482)));
	
	//make text not bold
	txt2.setFont(txt2.getFont().deriveFont(Font.PLAIN));
	txt3.setFont(txt3.getFont().deriveFont(Font.PLAIN));
	txt4.setFont(txt4.getFont().deriveFont(Font.PLAIN));

        //make the text fields uneditable
        txt1.setEditable(false);
        pane.add(txt1);
	pane.add(txt2);
	pane.add(txt3);
	pane.add(txt4);
         
        //finish up
        setTitle("roku@" + address); //set title
        setSize(255,78); //set size
        setVisible(true); //make visible
        setDefaultCloseOperation(EXIT_ON_CLOSE); //define what the X does
        setAlwaysOnTop(true); //always on top

	//allow window to be closed without killing whole program
	this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
     
    public static void main(String[] args) {
    	x Window = new x("192.168.1.25");
    }
}
