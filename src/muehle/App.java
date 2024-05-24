package muehle;

public class App {

	public static GameController controller;
	public static GUIView gui;
	public static BoardModell modell;   
	public static GUIController guiControl;

	public App() {
		modell = new BoardModell(); 
		controller = new GameController(modell);
		guiControl = new GUIController(modell, controller);  
		gui = new GUIView(modell,controller,guiControl);
	}

	public static void main(String[] args) {
		App app = new App();
	}

}
