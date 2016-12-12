package editor;

import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.FlowLayout;
import java.awt.Font;
//import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.markdown4j.Markdown4jProcessor;

/*import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder; */

//import org.markdown4j.Markdown4jProcessor;

public class Edit extends JFrame{
	private static final long serialVersionUID = 1L;
	
	class MyTreeNode {
		public String labelName;
		public int rowNumber;
		public MyTreeNode(String labelName, int rowNumber) {
			this.labelName = labelName;
			this.rowNumber = rowNumber;
		}
		@Override
		public String toString() {
			return labelName;
		}
	}
	
	class MdFileFilter extends FileFilter {
		
		public String getDescription() {
			return "*.md";
		}
		
		public boolean accept(File file) {
			String name = file.getName();
			return file.isDirectory() || name.toLowerCase().endsWith(".md");
		}
	}
	
	private JEditorPane htmlPane = new JEditorPane();
	private JTextArea text = new JTextArea("Plese input your markdown text here.\n123\n123", 0, 50);
	private JTree headerTree = new JTree(new DefaultMutableTreeNode("markdown"));
	
	private JFileChooser jFileChooser = new JFileChooser(new File("."));
	private File file;
	
	private File cssFile;

	public Edit(){
		JMenuSetup();
		JTimerSetup(2000);
		
		//setLayout(new FlowLayout());
		//setLayout(new GridLayout(0, 2, 5, 0));
		setLayout(new BorderLayout(5, 5));
		
		JScrollPane scrollPane = new JScrollPane(text);
		
		JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, headerTree, scrollPane);
		
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		text.setEditable(true);
		text.setFont(new Font("Courier", Font.PLAIN, 14));
		
		//text.setBorder(new LineBorder(Color.black, 2));
		
		add(jsp, BorderLayout.CENTER);
		
		/* headerTree = new JTree(new DefaultMutableTreeNode("root"));
		headerTree.setModel(new DefaultMutableTreeNode("root"));
		headerTree.updateUI(); */
		
		//add(headerTree, BorderLayout.CENTER);
		//add(htmlPane);
		//add(scrollPane, BorderLayout.EAST);
		//htmlPane.setPage()
		
		MdFileFilter mdFilter = new MdFileFilter();
		jFileChooser.addChoosableFileFilter(mdFilter);
		jFileChooser.setFileFilter(mdFilter);

		headerTree.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent node) {
				// TODO Auto-generated method stub
				TreePath path = node.getPath();
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) path.getLastPathComponent();
				System.out.println(n);
				cursorMoveTo(((MyTreeNode)n.getUserObject()).rowNumber);
			}
		});
		
		System.out.println(scrollPane.getVerticalScrollBarPolicy());
	}
	
	public void JMenuSetup() {
		JMenuItem jmiOpen, jmiSave, jmiSaveAs, jmiExportHTML, jmiExportDocx, 
				jmiExportHTMLWithCss, jmiSelectCss;
		JMenuBar jmb = new JMenuBar();
		setJMenuBar(jmb);
		
		//add File menu to MenuBar
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		jmb.add(fileMenu);
		
		//add Export menu to MenuBar
		JMenu exportMenu = new JMenu("Export");
		exportMenu.setMnemonic('E');
		jmb.add(exportMenu);
		
		JMenu cssMenu = new JMenu("Css");
		cssMenu.setMnemonic('C');
		jmb.add(cssMenu);
		
		//add some menu items to menu, and add keyboard accelerators
		fileMenu.add(jmiOpen = new JMenuItem("Open", 'O'));
		fileMenu.add(jmiSave = new JMenuItem("Save", 'S'));
		fileMenu.add(jmiSaveAs = new JMenuItem("Save As"));
		exportMenu.add(jmiExportHTML = new JMenuItem("html(without css)", 'H'));
		exportMenu.add(jmiExportHTMLWithCss = new JMenuItem("html(with css)", 'C'));
		exportMenu.add(jmiExportDocx = new JMenuItem("docx", 'D'));
		cssMenu.add(jmiSelectCss = new JMenuItem("Select CSS", 'C'));
		
		//set CTRL+S to "Save As" Action 
		jmiSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		
		jmiOpen.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});
		
		jmiSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (file != null) {
					saveFile(file);
				}
				else {
					saveFile();
				}
			}
		});
		
		jmiSaveAs.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveFile();
			}
		});
		
		jmiExportHTML.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				exportHTML();
			}
		});
		
		jmiExportHTMLWithCss.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				exportHTMLWithCss();
			}
		});
		
		jmiExportDocx.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		jmiSelectCss.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectCss();
			}
		});
	}
	
	public void JTimerSetup(int timeInterval) {	
		
		Timer time = new Timer(timeInterval, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] latestText = text.getText().split("\n");
				int rowCount = 0;
				DefaultMutableTreeNode tmp;
				DefaultMutableTreeNode[] latestNodes = new DefaultMutableTreeNode[6];
				latestNodes[0] = new DefaultMutableTreeNode(new MyTreeNode("markdown", 0));
				
				for (String line : latestText) {
					
					int level = 0;
					
					while (level < line.length() && line.charAt(level) == '#' && level < 5) {
						level++;
					}
					
					if (level == 0) {
						rowCount++;
						continue;						
					}

					
					for (int i = level - 1; i >= 0; i--) {
						if (latestNodes[i] != null) {
							tmp = new DefaultMutableTreeNode(new MyTreeNode(line.substring(level), rowCount));
							latestNodes[i].add(tmp);
							for (int j = i + 1; j <= 5; j++) {
								if (j != level) {
									latestNodes[j] = null;
								} else {
									latestNodes[j] = tmp;
								}
							}
							break;
						}
					}
					
					rowCount++;
				}
				
				//headerTree = new JTree(latestNodes[0]);
				headerTree.setModel(new DefaultTreeModel(latestNodes[0]));
				headerTree.updateUI();
				
				TreeNode root = (TreeNode) headerTree.getModel().getRoot();
				expandTree(headerTree, new TreePath(root));
			}
			
			
		});	
		
		time.start();
	}
	
	private void expandTree(JTree tree, TreePath parent) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandTree(tree, path);
			}
		}
		tree.expandPath(parent);
	}
	
	private void openFile() {
		if (jFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			file = jFileChooser.getSelectedFile();
			openFile(file);
		}
	}
	
	private void openFile(File file) {
		try {
			BufferedReader in = 
					new BufferedReader(new InputStreamReader
							(new FileInputStream(file), Charset.forName("UTF-8")));
			
			StringBuffer buf = new StringBuffer();
			while (in.ready()) {
				buf.append(in.readLine());
				buf.append("\n");
			}
			
			text.setText(buf.toString());
			in.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void saveFile() {
		if (jFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			file = jFileChooser.getSelectedFile();
			saveFile(file);
		}
	}
	
	private void saveFile(File file) {
		try {
			OutputStreamWriter out = new OutputStreamWriter
					(new FileOutputStream(file), Charset.forName("UTF-8"));
			out.write(text.getText());
			out.flush();
			out.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void exportHTML() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("html Files", "html"));
		File htmlFile;
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			htmlFile = fileChooser.getSelectedFile();
		} else {
			return;
		}

		try {
			String htmlHead = "<!doctype html> <html> <head> "
					+ "<meta charset='UTF-8'><meta name='viewport' "
					+ "content='width=device-width initial-scale=1'> <title>123.md</title></head><body>";
			String htmlTail = "</body> </html>";
			String html = new Markdown4jProcessor().process(text.getText());
			OutputStreamWriter out = new OutputStreamWriter
					(new FileOutputStream(htmlFile), Charset.forName("UTF-8"));
			out.write(htmlHead + htmlTail + html);
			out.flush();
			out.close();			
		}
		catch (IOException e){
			e.printStackTrace();
		}	
		
	}
	
	private void selectCss() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("css Files", "css"));	
		
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			cssFile = fileChooser.getSelectedFile();
		}
	}
	
	private String getCssEntry() {
		try {
			if (cssFile == null)
				return "";
			
			BufferedReader in = 
					new BufferedReader(new InputStreamReader
							(new FileInputStream(cssFile), Charset.forName("UTF-8")));
			
			StringBuffer buf = new StringBuffer();
			while (in.ready()) {
				buf.append(in.readLine());
				buf.append("\n");
			}
			
			in.close();	
			
			return buf.toString();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	private void exportHTMLWithCss() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("html Files", "html"));
		File htmlFile;
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			htmlFile = fileChooser.getSelectedFile();
		} else {
			return;
		}

		try {
			String htmlHead = "<!doctype html> <html> <head> " +
					"<meta charset='UTF-8'><meta name='viewport' " +
					"content='width=device-width initial-scale=1'> <title>123.md</title>" +
					"<style>" + getCssEntry() + "</style>" + "</head><body>";
			String htmlTail = "</body> </html>";
			String html = new Markdown4jProcessor().process(text.getText());
			OutputStreamWriter out = new OutputStreamWriter
					(new FileOutputStream(htmlFile), Charset.forName("UTF-8"));
			out.write(htmlHead + html + htmlTail);
			out.flush();
			out.close();			
		}
		catch (IOException e){
			e.printStackTrace();
		}			
	}
	
	private void cursorMoveTo(int row) {
		try {
			int location = text.getLineStartOffset(row);
			System.out.println("row = " + row);
			System.out.println("line = " + location);
			text.setSelectionStart(0);
			text.setSelectionEnd(0);
			//Thread.sleep(2000);
			//System.out.println("---");
			text.setSelectionStart(location);
			text.setSelectionEnd(location);
			//text.setCaretPosition(1);
			//Thread.sleep(2000);
			/*text.setCaretPosition(text.getText().length() - 1);
			Thread.sleep(2000);*/
			//text.setSelectionStart(location);
			//text.setSelectionEnd(location);
			//text.setCaretPosition(location);
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public static void main(String[] args) {
		Edit frame = new Edit();
		frame.setTitle("Markdown Editor");
		frame.setSize(800,600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setVisible(true);
		
		try {		
			frame.htmlPane.setPage("file:///e:/tmp/test.html");		
			//while (true) {
				/*String htmlText = frame.text.getText();
				//String html = new Markdown4jProcessor().process("This is a bold text<br> \n###hi\n**hi,bitches**");
				String html = new Markdown4jProcessor().process(htmlText);
				File file = new File("e:/tmp/test.html");
				FileWriter fw = new FileWriter(file);
				fw.write(html);
				fw.flush();
				fw.close(); */
				frame.htmlPane.setPage("file:///D:/2015-2016-2/Computer_Organization/week7/模拟器/计算机组成 第七周作业without.html");				
			//}
			//System.out.println(html);
		}
		catch (IOException ioExp) {
			ioExp.printStackTrace();
		}
		
	}
}
