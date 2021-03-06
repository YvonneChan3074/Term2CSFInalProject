import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;
import javax.swing.text.*;
import javax.swing.undo.*;
import java.util.*;

@SuppressWarnings("unchecked")
public class Menu extends JFrame implements ActionListener{
    private Container editor;
    private JTextPane textPane;
    private JButton bold,italic,undo,redo;
    private JRadioButton lalign,center,ralign;
    private ButtonGroup alignment;
    private Font font;    
    private Font[] fontlist;
    private int size,caretPosition, prevPosition;
    private JComboBox fontselect, textsize;
    private Textbank bank;

    private JMenuBar menu;
    private JMenu filemenu;
    private JMenuItem save, saveAs, openitem;
    private File savefile;
    private String currentFile;
    private JTextField filenamebox;
    private boolean BoldOn, ItalicOn;
    private Stack<String> changes;
    private Stack<String> backtrack;
    private JFrame saveas, open;

    public Menu(){
	
	this.setTitle("Wizarding Word of Java");
	this.setSize(800,600);
	this.setLocation(400,200);
	this.setDefaultCloseOperation(EXIT_ON_CLOSE);

	bank = new Textbank();
	
	editor = this.getContentPane();
	editor.setLayout(new BoxLayout(editor, BoxLayout.PAGE_AXIS));
	textPane = new JTextPane();
	
	setUpAlignment();
	setUpTextPane();
	setUpFont();
	setUpSize();	
	setUpMenuBar();
	setUpStyle();
	setUpBacktrack();
	
	
	editor.add(bold);
	editor.add(italic);
	editor.add(lalign);
	editor.add(center);
	editor.add(ralign);
	editor.add(textsize);
	editor.add(fontselect);
	editor.add(undo);
	editor.add(redo);
	editor.add(textPane);	
    }

    public void setUpTextPane(){
	
	StyledDocument doc = textPane.getStyledDocument();
	addStylesToDocument(doc);
	caretPosition = doc.getLength();
	doc.setParagraphAttributes(0,caretPosition, doc.getStyle("regular"),false);
	/*
	try {
	    for (int i=0; i < bank.getLength(); i++) {
		doc.insertString(caretPosition,String.valueOf(bank.getText(i)),doc.getStyle("regular"));
		//doc.insertString(caretPosition,bank.getText(i),doc.getStyle(convertStyles(i)));
	    }
	} catch (BadLocationException e) {
	    System.out.println("unable to insert text into text pane.");
	}
	*/
    }

    public void setUpMenuBar(){
	menu = new JMenuBar();
	setJMenuBar(menu);
	filemenu = new JMenu("File");
	save = filemenu.add("Save");
	save.setActionCommand("Save");
	save.addActionListener(this);
	saveAs = filemenu.add("Save As...");
	saveAs.setActionCommand("SaveAs");
	saveAs.addActionListener(this);
	openitem = filemenu.add("Open");
	openitem.setActionCommand("Open");
	openitem.addActionListener(this);
	menu.add(filemenu);
    }
    

    public void setUpFont(){
	GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
	fontlist = e.getAllFonts();
	font = new Font(fontlist[10].getFamily(), Font.PLAIN, size);
	textPane.setFont(font);

	String[] listfont = new String[fontlist.length];
	for(int i = 0; i < fontlist.length; i++){
		listfont[i] = fontlist[i].getFamily();
	}
	
	fontselect = new JComboBox(listfont);
	fontselect.setSelectedIndex(10);
	fontselect.setEditable(true);
	fontselect.setPreferredSize(new Dimension(225,25));
	fontselect.setMaximumSize(fontselect.getPreferredSize());
	fontselect.setActionCommand("font");
	fontselect.addActionListener(this);
    }

    
    public void setUpAlignment(){
	alignment = new ButtonGroup();
	
	lalign = new JRadioButton("Left-aligned");
	lalign.setActionCommand("Left-aligned");
	lalign.addActionListener(this);
	lalign.setSelected(true);
	
	center = new JRadioButton("Center");
	center.setActionCommand("Center");
	center.addActionListener(this);
	
	ralign = new JRadioButton("Right-aligned");
	ralign.setActionCommand("Right-aligned");
	ralign.addActionListener(this);

	alignment.add(lalign);
	alignment.add(center);
	alignment.add(ralign);

    }
    

    public void setUpStyle(){
	BoldOn = false;
	ItalicOn = false;
	
	bold = new JButton("bold");
	bold.addActionListener(this);
	bold.setActionCommand("turnB");
	//bold.setMnemonic(KeyEvent.VK_B);
	
        italic = new JButton("italic");
	italic.addActionListener(this);
	italic.setActionCommand("turnI");
	//italic.setMnemonic(KeyEvent.VK_I);
    }

    public void setUpSize(){
	String[] sizelist = new String[20];
	int tempsize = 12;
	for (int x = 0; x < sizelist.length; x++){
	    sizelist[x] = tempsize + "";
	    tempsize += 4;
	}

	textsize = new JComboBox(sizelist);
	textsize.setSelectedIndex(2);
	size = 20;
	textsize.setEditable(true);
	textsize.setPreferredSize(new Dimension(50,25));
        textsize.setMaximumSize(textsize.getPreferredSize());
        textsize.addActionListener(this);

    }

    // converts int format of font class to intialized styles documentation
    private String convertStyles(int index){
	if (index > 0 && index < bank.getLength()){
	    int tempStyle = bank.getStyle(index);
	    if (tempStyle == Font.BOLD){
		return "bold";
	    }else if (tempStyle == Font.ITALIC){
		return "italic";
	    }else{
		return "regular";
	    }
	}
	return "";
    }

    private void setUpBacktrack(){
	changes = new Stack<String>();
	backtrack = new Stack<String>();
	
	undo = new JButton("undo");
	undo.addActionListener(this);
	undo.setActionCommand("undo");

	redo = new JButton("redo");
	redo.addActionListener(this);
	redo.setActionCommand("redo");
    }

    private void addStylesToDocument(StyledDocument doc) {
        //Initialize some styles and fonts
        Style def = StyleContext.getDefaultStyleContext().
                        getStyle(StyleContext.DEFAULT_STYLE);

	Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def,"Arial");
       
        Style s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);

	s = doc.addStyle("plain", regular);
	StyleConstants.setBold(s, false);
	StyleConstants.setItalic(s, false);

	s = doc.addStyle("notBold", regular);
	StyleConstants.setBold(s, false);

	s = doc.addStyle("notItalic", regular);
	StyleConstants.setItalic(s, false);

	String fontname;
	if (fontlist == null){
	    fontname = "Arial";
	}else{
	    fontname = fontlist[fontselect.getSelectedIndex()].getFamily();
	}
	s = doc.addStyle("font",regular);
	StyleConstants.setFontFamily(s,fontname);
	
	int newsize;
	if (textsize == null){
	    newsize = 20;
	}else{
	    newsize = 12 + 4*textsize.getSelectedIndex();
	}
	
	s = doc.addStyle("size", regular);
	StyleConstants.setFontSize(s, newsize);
	
        s = doc.addStyle("center", regular);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
	StyleConstants.setComponent(s, center);

	s = doc.addStyle("left", regular);
	StyleConstants.setAlignment(s, StyleConstants.ALIGN_LEFT);
	StyleConstants.setComponent(s, lalign);

	s = doc.addStyle("right", regular);
	StyleConstants.setAlignment(s, StyleConstants.ALIGN_RIGHT);
	StyleConstants.setComponent(s, ralign);

	
    }
    
    public void actionPerformed(ActionEvent e){
		        
	String event = e.getActionCommand();
	size = 16;
	StyledDocument doc = textPane.getStyledDocument();
	addStylesToDocument(doc);
	int newStyle = 0;
	String currentalignment = "";
	
	
	//System.out.println(words);
	//doc.insertString(doc.getLength(),words, doc.getStyle("regular"));

	String selected = textPane.getSelectedText();
	int start = textPane.getSelectionStart();
	int end = textPane.getSelectionEnd();
	//System.out.println(selected);
	//System.out.println(start);
	//System.out.println(end);
	if (selected == null){
	    selected = "";
	}

	
	if(event.equals("turnB")){
	    if (BoldOn){
		doc.setCharacterAttributes(start,end-start,doc.getStyle("notBold"),false);
		BoldOn = false;
	    }else{
		doc.setCharacterAttributes(start,end-start,doc.getStyle("bold"),false);
		BoldOn = true;
		newStyle = 1;
	    }
	    
	    
	}else if(event.equals("turnI")){
	    if (ItalicOn){
		doc.setCharacterAttributes(start,end-start,doc.getStyle("notItalic"),false);
		ItalicOn = false;
	    }else{
		doc.setCharacterAttributes(start,end-start,doc.getStyle("italic"),false);
		ItalicOn = true;
		newStyle = 2;
	    }
	}else if (event.equals("font")){
	    doc.setCharacterAttributes(start,end-start,doc.getStyle("font"),false);
	}else if (event.equals("Left-aligned")){
	    lalign.setSelected(true);
	    doc.setParagraphAttributes(0,doc.getLength(),doc.getStyle("left"),false);
	    currentalignment = "left";
	}else if (event.equals("Right-aligned")){
	    ralign.setSelected(true);
	    doc.setParagraphAttributes(0,doc.getLength(),doc.getStyle("right"),false);
	    currentalignment = "right";
	}else if (event.equals("Center")){
	    center.setSelected(true);
	    doc.setParagraphAttributes(0, doc.getLength(),doc.getStyle("center"),false);
	    currentalignment = "center";
	}else if(event.equals("SaveAs")){
		save(true);
	}else if(event.equals("Save")){
		save(false);
	}else if(event.equals("savefile")){
		currentFile = filenamebox.getText();
		save(false);
	}else if (event.equals("undo")){
	    doc.setCharacterAttributes(start,end-start,doc.getStyle("plain"),false);
	}else if (event.equals("redo")){
	    doc.setCharacterAttributes(start,end-start,doc.getStyle("plain"),false);
	
	}
	else if(event.equals("Open")){
		open();
	}
	else if(event.equals("openfile")){ // I recommend putting this in another method
		currentFile = filenamebox.getText();
		String text = "";
		try{
			FileReader reader = new FileReader(currentFile + ".txt");
			char[] textary = new char[10000000];
			int chars = reader.read(textary);
			try{
				for(char c : textary){
					if(chars > 0){
						System.out.println(c);
						text += c;
						chars--;
					}
				}
			}
			catch(NullPointerException arbitary){}
			textPane.setText(text);
			reader.close();
		}
		catch(IOException i){
			System.out.println("Cannot open this file at this time. Please try again.");
		}
		open.dispose();
	}
	else if (event.equals("size")){
	    doc.setCharacterAttributes(start, end-start,doc.getStyle("size"),false);
	    size = textsize.getSelectedIndex();
	    
	}else{
		saveas.dispose();
	}
	/*
	if (newStyle == 0){
	    font = new Font(fontlist[fontselect.getSelectedIndex()].getFamily(),Font.PLAIN , size);
	}else if (newStyle == 1){
	    font = new Font(fontlist[fontselect.getSelectedIndex()].getFamily(),Font.BOLD , size);
	}else{
	    font = new Font(fontlist[fontselect.getSelectedIndex()].getFamily(),Font.ITALIC , size);
	}
	*/
	
	updateBank(size,currentalignment);
    }

    public String wordBank(){
	return bank.toString();
    }

    public void updateBank(int size, String currentalign){
	String words = textPane.getText();
	System.out.println(words.length());
	System.out.println(bank.getLength());
	try{
	    if (!(words.equals(bank.toString()))){
		for (int x = 0; x < words.length(); x++){
		    bank.set(x,words.charAt(x),new Font(font.getFamily(),font.getStyle(),size), currentalign);
		}
		
		
		for (int x = words.length(); x < bank.getLength(); x++){
		    bank.set(x,'~',new Font("Arial",Font.PLAIN,size),currentalign);
		}
		
	    }
	   
	}
	catch(IndexOutOfBoundsException i){
		System.out.println("There is no text.");
	}
	
	System.out.println(wordBank());
    }

    public void save(boolean as){
    	if(as){
	    saveas = new JFrame();
	    saveas.setTitle("Save As...");
	    saveas.setSize(200, 100);
	    saveas.setLocation(200, 100);
	    saveas.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    		saveas.setVisible(true);
		filenamebox = new JTextField("Type your filename here.", 10);
		filenamebox.addActionListener(this);
		JButton save = new JButton("Save");
		save.addActionListener(this);
		save.setActionCommand("savefile");
		saveas.add(filenamebox);
		saveas.add(save);
		Container savepane = saveas.getContentPane();
		savepane.setLayout(new BoxLayout(savepane, BoxLayout.PAGE_AXIS));
    	}
    	else{
	    /*look up how to save a pdf file, would be useful for printing*/
	    	if(currentFile == null){
	    		save(true);
	    	}
    		savefile = new File(System.getProperty("user.dir"), currentFile + ".txt");
			try{
				savefile.createNewFile();
				FileWriter writer = new FileWriter(savefile);
				String text = textPane.getText();
				writer.write(text);
				writer.flush();
				writer.close();
			}
			catch(IOException i){
				System.out.println("Cannot save at this time, please try again later");
			}
		}
    }

    public void open(){
    	open = new JFrame();
	    open.setTitle("Open...");
	    open.setSize(200, 100);
	    open.setLocation(200, 100);
	    open.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	open.setVisible(true);
		filenamebox = new JTextField("Type your filename here.", 10);
		filenamebox.addActionListener(this);
		JButton openfile = new JButton("Open");
		openfile.addActionListener(this);
		openfile.setActionCommand("openfile");
		open.add(filenamebox);
		open.add(openfile);
		Container openpane = open.getContentPane();
		openpane.setLayout(new BoxLayout(openpane, BoxLayout.PAGE_AXIS));
    }

    public static void main(String[] args){
	Menu test = new Menu();
	System.out.println(test.wordBank());
	test.setVisible(true);
    }
    
}
