package lab5;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AAQuizGUI extends JFrame
{
	/**
	 * GUI to run amino acid quiz for 30 seconds
	 * 
	 * potential improvements:
	 * 	make correct box green after correct answer
	 * 	make incorrect box red when incorrect
	 * 
	 */
	private static final long serialVersionUID = -1586708998847307196L;
	private int numCorrect;
	private int numIncorrect;
	private final int timeLimit = 30;
	private int timeRemaining = timeLimit;
	private boolean cancel = true;
	
	private JButton startButton = new JButton("Start quiz!");
	private JButton cancelButton = new JButton("Cancel");
	private JTextField aaPrompt = new JTextField();
	private JTextField timeText = new JTextField();
	private JTextField timeLeft = new JTextField();
	private JTextField correctCounts = new JTextField();
	private JTextField incorrectCounts = new JTextField();
	private JTextField aaText = new JTextField("Amino acid:");
	private JTextField guessText = new JTextField("Input the one-letter code below");
	private JTextField guessBox = new JTextField();
	
	// assign important variables
	public static String[] SHORT_NAMES = 
		{"A", "R", "N", "D", "C", "Q", "E", 
		"G", "H", "I", "L", "K", "M", "F", 
		"P", "S", "T", "W", "Y", "V" };
	public static String[] FULL_NAMES = 
		{"alanine", "arginine", "asparagine", 
		"aspartic acid", "cysteine",
		"glutamine",  "glutamic acid",
		"glycine" ,"histidine", "isoleucine",
		"leucine",  "lysine", "methionine", 
		"phenylalanine", "proline", 
		"serine", "threonine", "tryptophan", 
		"tyrosine", "valine"};
	public final static int numAAs = SHORT_NAMES.length;
	// create map of AA codes/names
	public static Map<String, String> aaMap;
	static{
		Map<String, String> nameMap = new HashMap<>();
		for (int i=0 ; i < numAAs ; i++)
		{
			nameMap.put(SHORT_NAMES[i], FULL_NAMES[i]);
		}
		aaMap = Collections.unmodifiableMap(nameMap);
	}
	
	// check whether answer correct and count it up
	public void verifyGuess(String guess, String answer)
	{
		if (guess.equals("ROUND OVER"))
			return;
		if (! aaMap.containsKey(guess))
		{
			System.out.println("Incorrect");
			numIncorrect++;
			incorrectCounts.setText("# incorrect:\t"+numIncorrect);
			return;
		}
		String aaGuess = aaMap.get(guess);
		if (aaGuess.equals(answer))
		{
			System.out.println("Correct");
			numCorrect++;
			correctCounts.setText("# correct:\t"+numCorrect);
		}
		else
		{
			System.out.println("Incorrect");
			numIncorrect++;
			incorrectCounts.setText("# incorrect:\t"+numIncorrect);
		}	
	}
	
	// get random AA
	public String getAA()
	{
		// prepare to get random numbers
		Random random = new Random();
		// get AA name
		return FULL_NAMES[random.nextInt(numAAs)];
	}
	
	// run the quiz
	private class startQuiz implements Runnable
	{
		public void run()
		{
			try
			{
				SwingUtilities.invokeAndWait(new Runnable()
				{
					
					@Override
					public void run()
					{
						startButton.setEnabled(false);
						cancelButton.setEnabled(true);
						timeText.setText("Time remaining:");
					}
				});
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			try
			{
				while( ! cancel)
				{
					// get aa prompt
					String answer = getAA();
					System.out.println(answer);
					aaPrompt.setText(answer);
					
					// let user answer
					String guess;
					while(true)
					{
						guessBox.requestFocusInWindow();
						guess = guessBox.getText().toUpperCase();
						if (! guess.equals(""))
						{
							break;
						}
						Thread.sleep(10);
					}
					System.out.println(guess);
					
					// check guess & update counts
					verifyGuess(guess, answer);
					guess = "";
					if (timeRemaining > 0)
						guessBox.setText(guess);
				}
			}
			catch(Exception e)
			{
				aaPrompt.setText(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	// keep time and update clock every second
	private class startTimer implements Runnable
	{
		public void run()
		{
			try
			{
				while( ! cancel && timeRemaining > 0)
				{
					Thread.sleep(1000);
					// update time
					timeRemaining--;
					timeLeft.setText(""+timeRemaining);
				}
				cancelButton.doClick();
			}
			catch(Exception e)
			{
				timeLeft.setText(e.getMessage());
				e.printStackTrace();
			}
		}
	}	
	
	public class CancelActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			cancel = true;
			// reset things for another round
			startButton.setEnabled(true);
			cancelButton.setEnabled(false);
			timeLeft.setText("0");
			guessBox.setText("Round over");
			guessBox.setEditable(false);
			startButton.setText("Start new quiz!");
			startButton.requestFocusInWindow();
		}
	}

	public class StartActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			cancel = false;
			startButton.setEnabled(true);
			cancelButton.setEnabled(false);
			timeRemaining = timeLimit;
			timeLeft.setText(""+timeRemaining);
			numCorrect = 0;
			numIncorrect = 0;
			incorrectCounts.setText("# incorrect:\t" + numIncorrect);
			correctCounts.setText("# correct:\t" + numCorrect);
			guessBox.setEditable(true);
			
			new Thread( new startTimer()).start();
			
			new Thread( new startQuiz()).start();
		}
	}
	
	// start quiz if user hits enter
	public class enterListener implements KeyListener
	{
		@Override
		public void keyPressed(KeyEvent pressSomeKey)
		{
			System.out.println("some key got pressed");
			if (pressSomeKey.getKeyCode()==KeyEvent.VK_ENTER){
				startButton.doClick();
			}
		}
		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
		}
		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
		}
	}
	
	// start/cancel buttons
	private JPanel bottomPanel()
	{
		JPanel panel = new JPanel();
		GridLayout makePairGrid = new GridLayout(0,2,50,20);
//		makePairGrid.setHgap(25);
//		GridLayout layout = makePairGrid; 
		panel.setLayout(makePairGrid);
		startButton.addActionListener(new StartActionListener());
		cancelButton.addActionListener(new CancelActionListener());
		panel.add(startButton);
		panel.add(cancelButton);
		return panel;
	}
	
	// present counts/timing
	private JPanel topPanel()
	{
		JPanel panel = new JPanel();
		GridLayout make2by2Grid = new GridLayout(2,2,100,10);
		panel.setLayout(make2by2Grid);
		panel.add(correctCounts);
		correctCounts.setText("# correct:\t" + numCorrect);
		correctCounts.setEditable(false);
		panel.add(timeText);
		timeText.setText("Time limit:");
		timeText.setEditable(false);
		panel.add(incorrectCounts);
		incorrectCounts.setText("# incorrect:\t" + numIncorrect);
		incorrectCounts.setEditable(false);
		panel.add(timeLeft);
		timeLeft.setText(""+timeLimit);
		timeLeft.setEditable(false);
		return panel;
	}
	
	// show AA, get guess
	private JPanel middlePanel()
	{
		JPanel panel = new JPanel();
		GridLayout make2by2Grid = new GridLayout(2,2,10,10);
		panel.setLayout(make2by2Grid);
		panel.add(aaText);
		aaText.setEditable(false);
		panel.add(guessText);
		guessText.setEditable(false);
		panel.add(aaPrompt);
		aaPrompt.setText("Press start to begin.");
		aaPrompt.setEditable(false);
		panel.add(guessBox);
		guessBox.setText("...your guess...");
		guessBox.setEditable(false);
		return panel;
	}
	
	// initialize with no inputs
	public AAQuizGUI()
	{
		// create gui jframe
		super("Amino Acid Quiz");
		setSize(400, 180);
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(bottomPanel(),BorderLayout.SOUTH);
		getContentPane().add(topPanel(),BorderLayout.NORTH);
		getContentPane().add(middlePanel(),BorderLayout.CENTER);
		getRootPane().setDefaultButton(startButton);
		validate();
	}
	
	public static void main(String[] args)
	{
		// create/run gui jframe
		new AAQuizGUI();
	}
	
}
