package muehle;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GUIView extends JFrame implements Observer {
	private JFrame board;
	private JPanel boardPanel; // panel, which contains the whole game

	private JPanel selectionPanel;

	private JLabel playerLabel;
	private JRadioButton player1;
	private JRadioButton player2;
	private ButtonGroup player;
	private JPanel playerPanel;

	private JLabel difficultyLabel;
	private JPanel difficultyPanel;
	private JRadioButton difficulty_easy;
	private JRadioButton difficulty_avg;
	private JRadioButton difficulty_hard;
	private ButtonGroup difficultyGroup;

	private JLabel timeLabel;
	private JPanel timePanel;
	private JRadioButton time_easy;
	private JRadioButton time_avg;
	private JRadioButton time_hard;
	private ButtonGroup timeGroup;

	private JPanel textOutput;
	private JLabel textField;
	private JLabel textFieldTemp1;
	private JLabel textFieldTemp2;
	private JPanel textPanel;
	private JPanel piecesLeftPanel;
	private JLabel whitePiecesLeft;
	private JLabel blackPiecesLeft;
	private JPanel timerPanel;
	private JLabel timerText;

	private JPanel startPanel;
	private JButton start;
	private JPanel loadPanel;
	private JButton load;
	private JPanel restartPanel;
	private JButton restart;
	private JPanel undoPanel;
	private JButton undo;
	private JPanel savePanel;
	private JButton save;
	private JPanel pausePanel;
	private JButton pause;

	private int timeForTimer;
	private Timer timer;
	private ImageIcon millBoard; // picture of millboard
	private JLabel millBoardLabel; // contains ImageIcon millboard

	public byte player_var;
	public byte difficulty_var;
	public byte time_var;

	private JLayeredPane gameArea; // Includes tokens and background

	private boolean gameIsRunning;
	private boolean gamePaused = false;
	private int remaining;

	public BoardModell modell; 
	public GameController controller;
	public GUIController guiController;

	private JLabel[] circles = new JLabel[24];

	public GUIView(BoardModell m, GameController c, GUIController g) {
		modell = m;
		modell.addObserver(this);
		controller = c;
		guiController = g;
		makeFrame();
	}

	private void createCircles() {
		for (int i = 0; i < circles.length; i++) {
			JLabel circle = new JLabel();

			circle.setVerticalAlignment(JLabel.TOP);
			circle.setHorizontalAlignment(JLabel.CENTER);
			circle.setBackground(Color.gray);
			circle.setOpaque(false);
			circle.setBounds(guiController.POSITION[i][0], guiController.POSITION[i][1], 50, 50);
			circles[i] = circle;
		}
	}

	public void redrawCircle(JLabel circle, Color c) {
		circle.setOpaque(true);
		circle.setBackground(c);
	}

	public void deleteCircle(JLabel circle) {
		circle.setOpaque(false);
		circle.setBackground(Color.gray);
	}

	private void makeFrame() {
		board = new JFrame("MÜHLE");
		board.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent){
				System.exit(0);	            
			}        
		});   

		selectionPanel = new JPanel(new GridLayout(12, 1)); //Field on the left with selection options
		boardPanel = new JPanel(new BorderLayout()); 		// Panel for complete content
		textOutput = new JPanel(new GridLayout(1, 3)); 		// Texfield top

		// player selection
		playerLabel = new JLabel("Spielerauswahl:");
		playerPanel = new JPanel(new GridLayout(2, 1));
		player1 = new JRadioButton("Spieler vs. Spieler");
		player1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(player1.isSelected()) {
					difficultyLabel.setEnabled(false);
					difficulty_easy.setEnabled(false);
					difficulty_avg.setEnabled(false);
					difficulty_hard.setEnabled(false);
				} 
			}
		});

		player2 = new JRadioButton("Spieler vs. Computer");
		player2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(player2.isSelected()) {
					difficultyLabel.setEnabled(true);
					difficulty_easy.setEnabled(true);
					difficulty_avg.setEnabled(true);
					difficulty_hard.setEnabled(true);
				} 
			}
		});
		player2.setSelected(true);
		player = new ButtonGroup();
		player.add(player2);
		player.add(player1);
		playerPanel.add(player2);
		playerPanel.add(player1);

		// choose difficulty
		difficultyLabel = new JLabel("Schwierigkeitsgrad wählen:");
		difficultyPanel = new JPanel(new GridLayout(3, 1));
		difficulty_easy = new JRadioButton("leicht");
		difficulty_easy.setSelected(true);
		difficulty_avg = new JRadioButton("mittel");
		difficulty_hard = new JRadioButton("schwer");
		difficultyGroup = new ButtonGroup();
		difficultyGroup.add(difficulty_easy);
		difficultyGroup.add(difficulty_avg);
		difficultyGroup.add(difficulty_hard);
		difficultyPanel.add(difficulty_easy);
		difficultyPanel.add(difficulty_avg);
		difficultyPanel.add(difficulty_hard);

		// choose the time to think
		timeLabel = new JLabel("Zugzeit auswählen:");
		timePanel = new JPanel(new GridLayout(3, 1));
		time_easy = new JRadioButton("2 Minuten");
		time_avg = new JRadioButton("eine Minute");
		time_hard = new JRadioButton("30 Sekunden");
		time_hard.setSelected(true);
		timeGroup = new ButtonGroup();
		timeGroup.add(time_easy);
		timeGroup.add(time_avg);
		timeGroup.add(time_hard);
		timePanel.add(time_hard);
		timePanel.add(time_avg);
		timePanel.add(time_easy);
		timePanel.setBorder(new EmptyBorder(0, 0, 10, 0));

		// start button
		startPanel = new JPanel();
		start = new JButton("Spiel starten");
		start.setBorder(new LineBorder(Color.black));
		start.setBackground(Color.LIGHT_GRAY);
		start.setPreferredSize(new Dimension(200, 45));
		startPanel.add(start);

		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playerLabel.setEnabled(false);
				player1.setEnabled(false);
				if (player1.isSelected()) {
					player_var = 0;
					undo.setEnabled(false);
				}
				player2.setEnabled(false);
				if (player2.isSelected()) {
					player_var = 1;
				}
				difficultyLabel.setEnabled(false);
				difficulty_easy.setEnabled(false);
				if (difficulty_easy.isSelected()) {
					difficulty_var = 0;
				}
				difficulty_avg.setEnabled(false);
				if (difficulty_avg.isSelected()) {
					difficulty_var = 1;
				}
				difficulty_hard.setEnabled(false);
				if (difficulty_hard.isSelected()) {
					difficulty_var = 2;
				}
				timeLabel.setEnabled(false);
				time_easy.setEnabled(false);
				if (time_easy.isSelected()) {
					timeForTimer = 120; 
					time_var = 0;
				}
				time_avg.setEnabled(false);
				if (time_avg.isSelected()) {
					timeForTimer = 60;
					time_var = 1;
				}
				time_hard.setEnabled(false);
				if (time_hard.isSelected()) {
					timeForTimer = 30;
					time_var = 2;
				}
				start.setEnabled(false);
				load.setEnabled(true);
				gameIsRunning = true;
				timerText.setText("Setze ersten Stein");
				remaining = timeForTimer; 
				startTimer();
				guiController.startGame(difficulty_var, time_var, player_var);
				whitePiecesLeft.setText("Weisse Steine zu setzen: " + controller.getWhitePiecesInHand());
				blackPiecesLeft.setText("Schwarze Steine zu setzen: " + controller.getBlackPiecesInHand());
				textField.setText(controller.getActivePlayerText() + " ist am Zug.");
			}
		});

		// load game button
		loadPanel = new JPanel();
		load = new JButton("Spiel laden");
		load.setBorder(new LineBorder(Color.black));
		load.setBackground(Color.LIGHT_GRAY);
		load.setPreferredSize(new Dimension(200, 45));
		load.setEnabled(false);
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.clearGame();
				guiController.resetGUIController();
				try {
					readFromFile();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				guiController.temporaer = modell.getCurrentBoardCopy();
				modell.messageChanges();
				if(controller.time == GameController.TIME_EASY) {
					timeForTimer = 120;
					time_easy.setSelected(true);
				}
				if(controller.time == GameController.TIME_AVG) {
					timeForTimer = 60;
					time_avg.setSelected(true);
				}
				if(controller.time == GameController.TIME_HARD) {
					timeForTimer = 30;
					time_hard.setSelected(true);
				}
				if(controller.mode == GameController.MODE_MULTI) {
					player1.setSelected(true);
				}
				if(controller.mode == GameController.MODE_SINGLE) {
					player2.setSelected(true);
					
				}
				if(controller.difficulty == GameController.DIFFICULTY_EASY) {
					difficulty_easy.setSelected(true);
				}
				if(controller.difficulty == GameController.DIFFICULTY_AVG) {
					difficulty_avg.setSelected(true);
				}
				if(controller.difficulty == GameController.DIFFICULTY_HARD) {
					difficulty_hard.setSelected(true);
				}
				remaining = timeForTimer;
				startTimer();
				gameIsRunning = true;
				start.setEnabled(false);
				playerLabel.setEnabled(false);
				player1.setEnabled(false);
				player2.setEnabled(false);
				difficultyLabel.setEnabled(false);
				difficulty_easy.setEnabled(false);
				difficulty_avg.setEnabled(false);
				difficulty_hard.setEnabled(false);
				timeLabel.setEnabled(false);
				time_easy.setEnabled(false);
				time_avg.setEnabled(false);
				time_hard.setEnabled(false);
				System.out.println("Spiel geladen");
				System.out.println("GUI Ausgabe nach geladenem Spiel:");
				System.out.println(modell);
				modell.messageChanges();

			}    
		});
		loadPanel.add(load);

		// restart button
		restartPanel = new JPanel();
		restart = new JButton("Neues Spiel");
		restart.setBorder(new LineBorder(Color.black));
		restart.setBackground(Color.LIGHT_GRAY);
		restart.setPreferredSize(new Dimension(200, 45));
		restart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				guiController.newGame();
				gameIsRunning = false;
				controller.gameIsOver = false;
				playerLabel.setEnabled(true);
				player1.setEnabled(true);
				player2.setEnabled(true);
				if(player1.isSelected()) {
					difficultyLabel.setEnabled(false);
					difficulty_easy.setEnabled(false);
					difficulty_avg.setEnabled(false);
					difficulty_hard.setEnabled(false);
				} else {
					difficultyLabel.setEnabled(true);
					difficulty_easy.setEnabled(true);
					difficulty_avg.setEnabled(true);
					difficulty_hard.setEnabled(true);
				}

				timeLabel.setEnabled(true);
				time_easy.setEnabled(true);
				time_avg.setEnabled(true);
				time_hard.setEnabled(true);
				undo.setEnabled(true);
				timer.stop();
				timer = null;
				timerText.setText("--");
				start.setEnabled(true);
				load.setEnabled(false);
			}
		});

		restartPanel.add(restart);

		// undo button
		undoPanel = new JPanel();
		undo = new JButton("Rückgängig machen");
		undo.setBorder(new LineBorder(Color.black));
		undo.setBackground(Color.LIGHT_GRAY);
		undo.setPreferredSize(new Dimension(200, 45));
		undo.addActionListener(e -> guiController.undo());
		undoPanel.add(undo);

		// save game button
		savePanel = new JPanel();
		save = new JButton("Spiel speichern");
		save.setBorder(new LineBorder(Color.black));
		save.setBackground(Color.LIGHT_GRAY);
		save.setPreferredSize(new Dimension(200, 45));
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					writeToFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		savePanel.add(save);

		// pause game button
		pausePanel = new JPanel();
		pause = new JButton("Spiel pausieren");
		pause.setBorder(new LineBorder(Color.black));
		pause.setBackground(Color.LIGHT_GRAY);
		pause.setPreferredSize(new Dimension(200, 45));
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!gamePaused) { // If timer is not yet paused
					timer.stop();
					gamePaused = true;
					pause.setText("Weiter");
					restart.setEnabled(false);
					undo.setEnabled(false);
					save.setEnabled(false);
					load.setEnabled(false);
					gameArea.setVisible(false);
					pauseTimer();
				} 
				else {
					pause.setText("Spiel pausieren");
					restart.setEnabled(true);
					undo.setEnabled(true);
					save.setEnabled(true);
					load.setEnabled(true);
					gameArea.setVisible(true);
					continueTimer();
					gamePaused = false;
				}
			}
		});
		pausePanel.add(pause);

		// Add everything to the selection panel
		selectionPanel.add(playerLabel);
		selectionPanel.add(playerPanel);
		selectionPanel.add(difficultyLabel);
		selectionPanel.add(difficultyPanel);
		selectionPanel.add(timeLabel);
		selectionPanel.add(timePanel);
		selectionPanel.add(startPanel);
		selectionPanel.add(restartPanel);
		selectionPanel.add(undoPanel);
		selectionPanel.add(savePanel);
		selectionPanel.add(loadPanel);
		selectionPanel.add(pausePanel);

		selectionPanel.setBorder(new EmptyBorder(5, 20, 5, 5));

		textPanel = new JPanel(new GridLayout(2, 2));
		textField = new JLabel(controller.getActivePlayerText() + " ist am Zug. ");
		textFieldTemp1 = new JLabel("In Phase: " + controller.getGameState());
		textFieldTemp2 = new JLabel(controller.getAusgabeText());
		textPanel.add(textField);
		textPanel.add(textFieldTemp2);

		timerPanel = new JPanel(new GridLayout(1, 2));
		timerText = new JLabel();
		timerPanel.add(timerText);
		timerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		piecesLeftPanel = new JPanel(new GridLayout(2, 1));
		whitePiecesLeft = new JLabel("Weisse Steine zu setzen: " + controller.getWhitePiecesInHand());
		blackPiecesLeft = new JLabel("Schwarze Steine zu setzen: " + controller.getBlackPiecesInHand());
		piecesLeftPanel.add(whitePiecesLeft);
		piecesLeftPanel.add(blackPiecesLeft);

		textOutput.add(piecesLeftPanel);
		textOutput.add(textPanel);
		textOutput.add(timerPanel);

		textOutput.setBorder(new EmptyBorder(10, 10, 10, 10));

		millBoard = new ImageIcon(".\\src\\muehle\\MillBoard.jpg");
		try {
            Image image = ImageIO.read(new File(".\\src\\muehle\\Icon_MillBoard.png"));
            int width = image.getWidth(null) * 2; 
            int height = image.getHeight(null) * 2; 

            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            board.setIconImage(scaledImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		millBoardLabel = new JLabel(millBoard);
		millBoardLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
		millBoardLabel.setBounds(0, 0, 681, 681);

		createCircles();

		gameArea = new JLayeredPane();
		gameArea.setBounds(0, 0, 697, 697);
		gameArea.setLayout(null);
		gameArea.add(millBoardLabel, new Integer(0));
		for (int i = 0; i < circles.length; i++) {
			gameArea.add(circles[i], new Integer(1));
		}

		gameArea.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (gameIsRunning) {
					int coordX = e.getX();
					int coordY = e.getY();
					guiController.clickOnBoard(coordX, coordY);
				}
			}
		});

		boardPanel.add(selectionPanel, BorderLayout.WEST);
		boardPanel.add(textOutput, BorderLayout.NORTH);
		boardPanel.add(gameArea, BorderLayout.CENTER);

		board.add(boardPanel);
		board.pack();
		board.setResizable(false);
		board.setSize(1000, 800);
		board.setVisible(true);
	}

	private void continueTimer() {
		gameIsRunning = true;
		timer.start();
	}

	private void pauseTimer() {
		if (timer != null) {
			timer.stop();
			gameIsRunning = false;
		}
	}

	private boolean showTime() {
		if(controller.mode == GameController.MODE_SINGLE && controller.getActivePlayer() == GameController.BLACK_PLAYER) {
			return false;
		}
		else {
			return true;
		}
	}

	private void startTimer() {
		if(timer == null) {
			gameIsRunning = true;
			timer = new Timer(1000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					timerText.setText(String.format("Verbleibende Zeit: %01d:%02d", (remaining/60), (remaining % 60)));
					remaining--;
					if (remaining <= 0) {
						timer.stop();
						timerText.setText("Spiel ist vorbei!");
						JOptionPane.showMessageDialog(board, "SPIEL IST BEENDET: Zeit ist abgelaufen.", "Mühle", JOptionPane.WARNING_MESSAGE);
						gameIsRunning = false;
					}
					if(controller.gameIsOver) {
						timer.stop();
						timerText.setText("Spiel ist vorbei");
						gameIsRunning = false;
					}
				}
			});
			timer.start();	
		}
	}

	/** updates the whole GUI, is controlled by MVC pattern */
	@Override
	public void update(Observable o, Object arg) {
		System.out.println("geupdatet");

		byte[] tmp = new byte[24];
		tmp = modell.getAllIndex();
		if (GUIController.approved) {
			refreshCircles(tmp);
			System.out.println("Ausgabe im Update:");
			System.out.println(modell);
			
		}else {
			System.out.println("Aktualisierung nicht approved");
		}
		if (!(modell.isEqual(guiController.temporaer)) && guiController.makesMill && GUIController.approved) {
			refreshCircles(guiController.temporaer);
			
		}

		whitePiecesLeft.setText("Weisse Steine zu setzen: " + controller.getWhitePiecesInHand());
		blackPiecesLeft.setText("Schwarze Steine zu setzen: " + controller.getBlackPiecesInHand());
		textField.setText(controller.getActivePlayerText() + " ist am Zug.");
		if(showTime() && guiController.executed) {
			remaining = timeForTimer; 
			startTimer();	
		}
		textFieldTemp2.setText(controller.getAusgabeText());
		validate();
		repaint();

	}

	/** Repositions the stones in the playing field */
	public void refreshCircles(byte[] array) {

		for (int i = 0; i < array.length; i++) {
			if (array[i] == BoardModell.EMPTY) {
				deleteCircle(circles[i]);
			} else if (array[i] == BoardModell.BLACK) {
				redrawCircle(circles[i], Color.black);
			} else if (array[i] == BoardModell.WHITE) {
				redrawCircle(circles[i], Color.white);
			}
		}
	}


	/** all necessary informations will be stored */
	public void writeToFile() throws IOException {

		Component frame = null;
		try {
			FileOutputStream fileStream = new FileOutputStream("Spielstand");
			ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);

			objectStream.writeObject(controller.gameState);
			objectStream.writeObject(controller.countNoMills);
			objectStream.writeObject(controller.activePlayer);
			objectStream.writeObject(controller.whitePiecesInHand);
			objectStream.writeObject(controller.blackPiecesInHand);
			objectStream.writeObject(controller.history);
			objectStream.writeObject(modell);
			objectStream.writeObject(controller.undoEnabled);
			objectStream.writeObject(controller.textAusgabe);
			objectStream.writeObject(controller.difficulty);
			objectStream.writeObject(controller.time);
			objectStream.writeObject(controller.mode);
			objectStream.writeObject(controller.ai);

			objectStream.close();
			fileStream.close();

			JOptionPane.showMessageDialog(frame, "Spielstand wurde erfolgreich gespeichert!", "Mühle", JOptionPane.WARNING_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, e.toString() + "\n Spielstand konnte nicht gespeichert werden!", "Mühle", JOptionPane.WARNING_MESSAGE);
		}
	}

	/** all necessary informations will be restored */
	public void readFromFile() throws ClassNotFoundException, IOException {

		try {
			FileInputStream fileStream = new FileInputStream("Spielstand");
			ObjectInputStream objectStream = new ObjectInputStream(fileStream);

			controller.gameState = (byte) objectStream.readObject();
			controller.countNoMills = (int) objectStream.readObject();
			controller.activePlayer = (byte) objectStream.readObject();
			controller.whitePiecesInHand = (byte) objectStream.readObject();
			controller.blackPiecesInHand = (byte) objectStream.readObject();
			controller.history = (Stack) objectStream.readObject();

			this.modell = (BoardModell) objectStream.readObject();
			guiController.modell = modell;
			controller.modell = modell;
			App.modell = modell;
			modell.addObserver(this);

			controller.undoEnabled = (boolean) objectStream.readObject();
			controller.textAusgabe = (String) objectStream.readObject();
			controller.difficulty = (byte) objectStream.readObject();
			controller.time = (byte) objectStream.readObject();
			controller.mode = (byte) objectStream.readObject();
			controller.ai = (MillAI) objectStream.readObject();

			objectStream.close();
			fileStream.close();

		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		this.modell.overwriteBoard(modell.getAllIndex());
		GUIController.approved = true;
		modell.messageChanges();
		GUIController.approved = false;
	}

}
