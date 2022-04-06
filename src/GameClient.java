import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class GameClient {
	Timer startClock;					// �ð豸�� Ÿ�̸�(��������ð��� ���� ������ �ð��Ǹ� ��������)
	Timer gametimer;

	JFrame startframe;
	JFrame frame;

	JTextArea incoming;			// ���ŵ� �޽����� ����ϴ� ��
	JTextArea outgoing;			// �۽��� �޽����� �ۼ��ϴ�

	JList counterParts;			// ���� �α����� ä�� ������� ��Ÿ���� ����Ʈ.

	ObjectInputStream reader;	// ���ſ� ��Ʈ��
	ObjectOutputStream writer;	// �۽ſ� ��Ʈ��
	Socket sock;				// ���� ����� ����

	JScrollPane qScroller;

	private boolean turnCheck=false;
	private boolean startCheck=false;	//���� ���� Ȱ���� üũ��
	public boolean userCheck = false;	//���� �� ���۹�ư�� ���� ����� üũ��

	ImageIcon end = new ImageIcon("/res/backend.png");
	ImageIcon stBtn = new ImageIcon("ddd/login.png");

	private int count=0,count1=0;
	private int i,j;
	int b=0;
	int score;	//������
	int times;	//�ʸ� ���� ����
	int nanu=0;

	int ALIEN_SIZE=45;
	int ALIEN_SIZE1=30;
	int PLAYER_SIZE=45;
	int FIRE_SIZE=10;
	int STAR_SIZE=20;
	int BOMB_SIZE=10;
	int BOMB_SIZE1=20;

	public JTextField user1,user2;
	String my2, you2;
	String frameTitle = "GALAGA";
	String user;				// �� Ŭ���̾�Ʈ�� �α��� �� ������ ��
	JPanel ending;
	JPanel startBack;
	JPanel user1Panel = new JPanel(); //���� ���� �г�
	JPanel user2Panel = new JPanel(); //���� ���� �г�
	JPanel countSet;	//�̸�, ����, �ð������ �����ִ� �г�
	JPanel playPanel;	//�����г�

	JLabel u1 = new JLabel("0");
	JLabel u2 = new JLabel("0");
	JLabel u1sco = new JLabel("0");
	JLabel u2sco = new JLabel("0");


	JLabel time = new JLabel("�غ�");
	private JLabel time1Count;
	private JLabel time2Count;
	private JLabel whiteCount;	//user1�� ������ ������
	private JLabel blackCount;	//user2�� ������ ������



	JButton start;	//���ӽ��۹�ư
	JButton logButton;			// ����� �Ǵ� �α���/�α׾ƿ� ��ư
	JButton sendButton;	//�޼��� ������ ��ư
	JButton goGame;

	MainPanel game;

	ClockListener clockListener;	// �ð踦 �����ϱ� ���� ������

	private AudioClip bgmSound;		//�������

	private final String BGM_SOUND = "/res/galaga.wav";	

	ArrayList<Keyplay> enemyList;// �ܰ��� �ִ� arraylist
	ArrayList<Keyplay1> enemyList1;// �ܰ��� �ִ� arraylist
	ArrayList<Keyplay2> star;// �������� �ִ� arraylist
	ArrayList<Keyplay3> bomb;// ��ź�� �ִ� arraylist
	private ArrayList<Bubble> shots = new ArrayList();
	private final String PLAYER="/res/starship.png";
	private final String ALIEN="res/alien.png";
	private final String ALIEN1="res/alien1.png";
	private final String FIRE="/res/fire.png";
	private final String BACK="/res/back.png";
	private final String BACKEND="/res/backend.png";
	private final String STARTBACK="/res/startback.png";
	private final String STAR="/res/star.png";
	private final String BOMB="/res/bomb.png";

	private boolean right = false, left = false; // �������� �ε巴�� �ϱ�����
	Keyplay player;

	public static void main(String[] args) {
		GameClient client = new GameClient();
		client.go();
	}

	private void go() {
		setUpNetworking();
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();

		// build GUI
		frame = new JFrame(frameTitle + " : �α����ϼ���");
		startframe = new JFrame("���� �� ���ǻ���");
		game = new MainPanel();

		game.setLayout(null); //�⺻���� �־����� ���̾ƿ��� �ִµ� �̰� �Ⱦ��� ������ǥ�� �ֱ�����


		// �޽��� ���÷��� â
		incoming = new JTextArea(5,10);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);
		qScroller = new JScrollPane(incoming);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		qScroller.setBounds(210,705,495,140);
		qScroller.setBackground(Color.BLACK);

		// ��ȭ ��� ���. �ʱ⿡�� "��ü" - ChatMessage.ALL �� ����
		String[] list = {GameMessage.ALL};
		counterParts = new JList(list);
		JScrollPane cScroller = new JScrollPane(counterParts);
		cScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		cScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		cScroller.setBounds(5,705,200, 190);
		cScroller.setBackground(Color.BLACK);

		counterParts.setVisibleRowCount(5);
		counterParts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		counterParts.setFixedCellWidth(180);

		// �޽��� ���÷��� â  - ������ â
		outgoing = new JTextArea(5,10);
		outgoing.addKeyListener(new EnterKeyListener());
		//outgoing.addKeyListener(new DirectionListener());
		outgoing.setLineWrap(true);
		outgoing.setWrapStyleWord(true);
		outgoing.setEditable(true);
		outgoing.setBounds(210,860,400, 35);
		//outgoing.setBackground(Color.BLACK);

		//������ ��ư
		sendButton = new JButton(new ImageIcon("ddd/send.png"));	//�������ư()�� ���� �������� �̹��� �ֱ�
		sendButton.setBorderPainted(false);
		sendButton.setFocusPainted(false);
		sendButton.setContentAreaFilled(false);
		sendButton.addActionListener(new SendButtonListener());
		sendButton.setBounds(615,860,35, 35);

		//�α��� ��ư
		//logButton = new JButton("login");
		logButton = new JButton(stBtn);	//�α��ι�ư-()�� ���� �������� �̹��� �ֱ�
		logButton.setText("login");
		logButton.setBorderPainted(false);
		logButton.setFocusPainted(false);
		logButton.setContentAreaFilled(false);
		logButton.addActionListener(new LogButtonListener());
		logButton.setBounds(660,860,45, 35);

		//���۹�ư
		start=new JButton(new ImageIcon("ddd/start.png"));
		start.setBorderPainted(false);
		start.setFocusPainted(false);
		start.setContentAreaFilled(false);
		start.setBounds(645, 5, 60, 35);
		start.addActionListener(new StartButtonListener());

		//�ð����
		time = new JLabel("�غ�");
		time.setBounds(600, 5, 50, 35);		
		time.setForeground(Color.WHITE);	//�ð���� ���� �Ͼ��

		//user1
		user1 = new JTextField();
		user1.setBounds(5, 5, 50, 35);		
		user1.setForeground(Color.WHITE);	//user1�� �̸��� ��Ÿ���� ���� �Ͼ��
		user1.setBackground(Color.BLACK);	//user1�� �̸��� ��Ÿ���� ����� ������
		user1.setEditable(false);
		user1.setHorizontalAlignment(JTextField.CENTER);

		//user1�� ����
		ImageIcon whiteIcon1 = new ImageIcon("/res/�Ͼ��.jpg");
		whiteCount = new JLabel("0",whiteIcon1, SwingConstants.CENTER);
		whiteCount.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
		whiteCount.setBounds(60,5,100,35);
		whiteCount.setForeground(Color.WHITE);	//user1�� ������ ��Ÿ���� ���� �Ͼ��

		//user2
		user2 = new JTextField();
		user2.setBounds(200, 5, 50, 35);		
		user2.setForeground(Color.WHITE);	//user2�� �̸��� ��Ÿ���� ���� �Ͼ��
		user2.setBackground(Color.BLACK);	//user2�� �̸��� ��Ÿ���� ����� ������
		user2.setEditable(false);
		user2.setHorizontalAlignment(JTextField.CENTER);

		//user2�� ����
		ImageIcon blackIcon1 = new ImageIcon("/res/�Ͼ��.jpg");
		blackCount = new JLabel("0",blackIcon1, SwingConstants.CENTER);
		blackCount.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
		blackCount.setBounds(255,5,100,35);
		blackCount.setForeground(Color.WHITE);	//user2�� ������ ��Ÿ���� ���� �Ͼ��

		//�̸�, ����, �ð������ �����ִ� �г� ����
		countSet = new JPanel();
		countSet.setLayout(null);
		countSet.add(user1);
		countSet.add(whiteCount);
		countSet.add(user2);
		countSet.add(blackCount);
		countSet.add(start);
		countSet.add(time);
		countSet.setBackground(Color.BLACK);
		countSet.setBounds(0, 0, 703, 45);
		countSet.setLayout(null);

		//����ȭ�� �� ����
		u1.setBounds(470, 200, 50, 100);
		u1.setForeground(Color.WHITE);
		u2.setBounds(470, 290, 50, 100);
		u2.setForeground(Color.WHITE);
		u1sco.setBounds(610, 200, 50, 100);
		u1sco.setForeground(Color.WHITE);
		u2sco.setBounds(610, 290, 50, 100);
		u2sco.setForeground(Color.WHITE);


		//����ȭ�� ����
		ending = new ending();
		ending.add(u1);
		ending.add(u2);
		ending.add(u1sco);
		ending.add(u2sco);

		ending.setBounds(0,47,705,655);
		ending.setLayout(null);
		ending.setVisible(false);

		//������ ����Ǵ� ȭ�鼳��
		playPanel = new PlayPanel();
		playPanel.setBounds(0,46,705,655);
		playPanel.addKeyListener(new DirectionListener());// Ű���� ������ ��ġ
		playPanel.requestFocus(); // (Ű�����ʰ� �۵��ϰ��ϱ����ؼ�)������ ������ �ش�. Ȥ�ø� ���� ����
		playPanel.setFocusable(true);// �ʱ⿡�� ��Ű�� �ȵǰ� ��(�� Ű �ȸ���


		//������ ����
		gametimer = new Timer(10,new AnimeListener());
		startClock=new Timer(1000, new ClockListener());

		//game.addKeyListener(new DirectionListener());
		//game.requestFocus();
		//game.setFocusable(true);

		//�����ư ����
		goGame =  new JButton(new ImageIcon("ddd/go.png"));	
		goGame.setBorderPainted(false);
		goGame.setFocusPainted(false);
		goGame.setContentAreaFilled(false);
		goGame.setBounds(190,330,120,50);
		goGame.addActionListener(new GoGameListener());

		//�����г� ����
		startBack = new startBack();
		startBack.add(goGame);
		startBack.setBounds(0, 0, 500, 400);
		startBack.setLayout(null);
		startBack.setVisible(true);

		// Ŭ���̾�� ������ â ����
		game.add(outgoing);
		game.add(sendButton);
		game.add(logButton);
		game.add(cScroller);
		game.add(qScroller);
		game.add(countSet);
		game.add(playPanel);
		game.add(ending);
		game.repaint();

		//���������� â ����
		startframe.add(startBack);
		startframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		startframe.setBounds(100,100,500,400);
		startframe.setLayout(null);
		startframe.setVisible(true);
		startframe.setResizable(false);

		//���������� â ����
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(game);
		frame.setSize(720,930);
		frame.setVisible(false);
		frame.setResizable(false);	//ȭ��ũ�⸦ ��������/ �Ұ��� ����
		frame.setBackground(Color.BLACK);
		// �������� ��� �����Ƿ� ���⼭ ������ ������� ��� ���� ��
		// �� ������ �����带 �����ϸ�, �� �����ӿ��� ���� ��������� ���ܸ� �߻��ϰԵǰ�
		// �̸� �̿��� ��� �����带 �����ϰ� ���� ��Ű���� ��
		// �Ҹ� ���� ���
		try {//���ܰ� ���� �� �ִ� �ڵ�
			bgmSound = JApplet.newAudioClip(getClass().getResource(BGM_SOUND));	//���Ҹ�

		}
		catch(Exception e){//���ܰ� ������ �� ����� ����
			System.out.println("�Ҹ����Ͽ��� ������ �����ϴ�.");
		}
		bgmSound.play();

	}

	//���������ǳ�
	class startBack extends JPanel{

		public void paintComponent(Graphics g){		
			Image img=new ImageIcon(getClass().getResource(STARTBACK)).getImage();
			g.drawImage(img, 0, 0, 500,400,this);

		}
	}

	//��Ʈ��ũ ����
	private void setUpNetworking() {  
		try {
			//sock = new Socket("220.69.203.11", 5000);
			sock = new Socket("127.0.0.1", 5000);			// ���� ����� ���� ��Ʈ�� 5000�� ���Ű�� ��
			reader = new ObjectInputStream(sock.getInputStream());
			writer = new ObjectOutputStream(sock.getOutputStream());

		} catch(Exception ex) {
			JOptionPane.showMessageDialog(null, "�������ӿ� �����Ͽ����ϴ�. ������ �����մϴ�.");
			ex.printStackTrace();
			frame.dispose();		// ��Ʈ��ũ�� �ʱ� ���� �ȵǸ� Ŭ���̾�Ʈ ���� ����
		}
	} // close setUpNetworking   


	// �α��ΰ� �ƿ��� ����ϴ� ��ư�� ��û��. ó������ Login �̾��ٰ� �ϴ� �α��� �ǰ��� Logout�� ó��
	private class LogButtonListener implements ActionListener {
		ImageIcon image;
		public void actionPerformed(ActionEvent ev) {
			if (logButton.getText().equals("login")) {
				processLogin();			
				stBtn = new ImageIcon("ddd/logout.png");
				logButton.setIcon(stBtn);
				logButton.setText("logout");
			}
			else
				processLogout();
		}
		// �α��� ó��
		private void processLogin() {
			user = JOptionPane.showInputDialog("����� �̸��� �Է��ϼ���");
			try {
				writer.writeObject(new GameMessage(GameMessage.MsgType.LOGIN, user, "", ""));
				writer.flush();
				frame.setTitle(frameTitle + " (�α��� : " + user + ")");
			} catch(Exception ex) {
				JOptionPane.showMessageDialog(null, "�α��� �� �������ӿ� ������ �߻��Ͽ����ϴ�.");
				ex.printStackTrace();
			}
		}
		// �α׾ƿ� ó��
		private void processLogout() {
			int choice = JOptionPane.showConfirmDialog(null, "Logout�մϴ�");
			if (choice == JOptionPane.YES_OPTION) {
				try {
					writer.writeObject(new GameMessage(GameMessage.MsgType.LOGOUT, user, "", ""));
					writer.flush();
					// ����� ��� ��Ʈ���� ������ �ݰ� ���α׷��� ���� ��
					writer.close(); reader.close(); sock.close();
				} catch(Exception ex) {
					JOptionPane.showMessageDialog(null, "�α׾ƿ� �� �������ӿ� ������ �߻��Ͽ����ϴ�. ���������մϴ�");
					ex.printStackTrace();
				} finally {
					System.exit(100);			// Ŭ���̾�Ʈ ���� ���� 
				}
			}
		}
	}  // close LoginButtonListener inner class

	//send��ư ������ �����ϴ� ������
	public class SendButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			String to = (String) counterParts.getSelectedValue();
			if (to == null) {
				JOptionPane.showMessageDialog(null, "�۽��� ����� ������ �� �޽����� ��������");
				return;
			}
			try {
				incoming.append(user + " : " + outgoing.getText() + "\n"); // ���� �޽��� â�� ���̱�
				writer.writeObject(new GameMessage(GameMessage.MsgType.CLIENT_MSG, user, to, outgoing.getText()));
				writer.flush();
				outgoing.setText("");
				outgoing.requestFocus();
			} catch(Exception ex) {
				JOptionPane.showMessageDialog(null, "�޽��� ������ ������ �߻��Ͽ����ϴ�.");
				ex.printStackTrace();
			}
		}
	}  // close SendButtonListener inner class

	//���� ������ �����ϴ� ������
	public class EnterKeyListener implements KeyListener{
		boolean presscheck=false;	//Ű�Է�üũ
		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			if(e.getKeyCode()==KeyEvent.VK_SHIFT){
				presscheck = true;	//Ű�Էµ�
			}
			else if(e.getKeyCode()==KeyEvent.VK_ENTER){
				if(presscheck == true){
					String str = outgoing.getText() +"\r\n";	//�ؽ�Ʈ�Է�
					outgoing.setText(str);
					presscheck = false;
				}
				else{
					e.consume();
					presscheck = false;
					String to = (String) counterParts.getSelectedValue();
					if (to == null) {
						JOptionPane.showMessageDialog(null, "�۽��� ����� ������ �� �޽����� ��������");
						return;
					}
					try {
						incoming.append(user + " : " + outgoing.getText() + "\n"); // ���� �޽��� â�� ���̱�
						incoming.setSelectionStart(incoming.getText().length());
						qScroller.getVerticalScrollBar().setValue(qScroller.getVerticalScrollBar().getMaximum());
						writer.writeObject(new GameMessage(GameMessage.MsgType.CLIENT_MSG, user, to, outgoing.getText()));
						writer.flush();
						outgoing.setText("");
						outgoing.requestFocus();
					} catch(Exception ex) {
						JOptionPane.showMessageDialog(null, "�޽��� ������ ������ �߻��Ͽ����ϴ�.");
						ex.printStackTrace();
					}
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if(e.getKeyCode()==KeyEvent.VK_SHIFT){
				presscheck = false;
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}
	}
	// �������� ������ �޽����� �޴� ������ �۾��� �����ϴ� Ŭ����
	public class IncomingReader implements Runnable {
		public void run() {
			GameMessage message;             
			GameMessage.MsgType type;
			String[] users={};

			try {
				while (true) {
					message = (GameMessage) reader.readObject();     	 // �����α� ������ �޽��� ���                   
					type = message.getType();

					if (type == GameMessage.MsgType.LOGIN_FAILURE) {	 // �α����� ������ �����
						JOptionPane.showMessageDialog(null, "Login�� �����Ͽ����ϴ�. �ٽ� �α����ϼ���");
						frame.setTitle(frameTitle + " : �α��� �ϼ���");
						logButton.setText("Login");
					} else if (type == GameMessage.MsgType.LOGIN_FULL) { // �޽����� �޾Ҵٸ� ������
						JOptionPane.showMessageDialog(null, "�ο��� ���� á���ϴ�.");						
					} else if (type == GameMessage.MsgType.SERVER_MSG) { // �޽����� �޾Ҵٸ� ������
						if (message.getSender().equals(message.getReceiver())) continue;  // ���� ���� ������ ���� �ʿ� ����
						incoming.append(message.getSender() + " : " + message.getContents() + "\n");
						qScroller.getVerticalScrollBar().setValue(qScroller.getVerticalScrollBar().getMaximum());

					} else if (type == GameMessage.MsgType.LOGIN_LIST) {
						// ���� ����Ʈ�� ���� �ؼ� counterParts ����Ʈ�� �־� ��.
						// ����  ���� (""�� ����� ���� �� ����Ʈ �� �տ� ���� ��)
						users = message.getContents().split("/");
						for (int i=0; i<users.length; i++) {
							if (user.equals(users[i]))users[i] = "";
						}
						users = sortUsers(users);		// ���� ����� ���� �� �� �ֵ��� �����ؼ� ����
						users[0] =  GameMessage.ALL;	// ����Ʈ �� �տ� "��ü"�� ������ ��
						counterParts.setListData(users);
						counterParts.setSelectedIndex(0);
						frame.repaint();

					} else if (type == GameMessage.MsgType.NO_ACT){
						// �ƹ� �׼��� �ʿ���� �޽���. �׳� ��ŵ
					}
					else if (type == GameMessage.MsgType.GAME_INFO){
						System.out.println("�ȳ� �� Ŭ���̾�Ʈ");
						System.out.println("i:"+count1);
						System.out.println("���� ���� �����Ѵ�");
						if (message.getSender().equals(my2));
						else count1=message.count;
						BoardSet(count1);  
					}
					else if(type == GameMessage.MsgType.GAME_START){
						System.out.println("���ӽ���");
						try{
							Thread.sleep(1000);
							startCheck = true;
							userCheck = true;
							playPanel.requestFocus(); // (Ű�����ʰ� �۵��ϰ��ϱ����ؼ�)������ ������ �ش�. Ȥ�ø� ���� ����
							playPanel.setFocusable(true);// �ʱ⿡�� ��Ű�� �ȵǰ� ��(�� Ű �ȸ���
							startClock.start(); 
							gametimer.start();
							game.repaint();
						}catch(Exception ex) {

						}


						String my="", you="";
						for (int i=0; i<users.length; i++) {
							if (user.equals(users[i]));
							else you = users[i];	
						}

						if(users.length>1) 
						{
							gameSet(user, you);
							setStartCheck(true);
							setTurnCheck(false);
							game.repaint();

						}
						else
						{	
							JOptionPane.showMessageDialog(null, "���� ��밡 �����ϴ�.");
							setStartCheck(false);
						}
					}
					else {
						// ��ü�� Ȯ�ε��� �ʴ� �̻��� �޽���
						throw new Exception("�������� �� �� ���� �޽��� ��������");
					}
				} // close while
			} catch(Exception ex) {
				System.out.println("Ŭ���̾�Ʈ ������ ����");		// �������� ����� ��� �̸� ���� ������ ����
				ex.printStackTrace();
			}
		} // close run

		// �־��� String �迭�� ������ ���ο� �迭 ����
		private String [] sortUsers(String [] users) {
			String [] outList = new String[users.length];
			ArrayList<String> list = new ArrayList<String>();
			for (String s : users) {
				list.add(s);
			}
			Collections.sort(list);				// Collections.sort�� ����� �ѹ濡 ����
			for (int i=0; i<users.length; i++) {
				outList[i] = list.get(i);
			}
			return outList;
		}
	} // close inner class 

	//goGame - �����ϱ� ��ư ������ ���۵Ǵ� �͵�
	private class GoGameListener implements ActionListener{
		public void actionPerformed(ActionEvent ev) {
			startframe.dispose();
			frame.setVisible(true);
		}
	}

	//start��ư ������ ���۵Ǵ� �͵�
	public class StartButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){


			if(e.getSource()==start){
				try {
					startCheck = true;                          // ���ӽ��� Ȱ�� 
					userCheck = true;
					writer.writeObject(new GameMessage(GameMessage.MsgType.GAME_START, userCheck));
					writer.flush();

				} catch(Exception ex) {
					JOptionPane.showMessageDialog(null, "�޽��� ������ ������ �߻��Ͽ����ϴ�.");
					ex.printStackTrace();
				}
			}

			gametimer.start();
			playPanel.requestFocus(); // (Ű�����ʰ� �۵��ϰ��ϱ����ؼ�)������ ������ �ش�. Ȥ�ø� ���� ����
			playPanel.setFocusable(true);// �ʱ⿡�� ��Ű�� �ȵǰ� ��(�� Ű �ȸ���



			game.repaint();	//�����г� repaint��Ű�鼭 ��� �߰��ϱ�

		}
	}	//close inner class

	// Ű���� ��û��
	class DirectionListener implements KeyListener {
		public void keyPressed(KeyEvent event) {
			// Ű���尡 ������ true��ȯ
			switch (event.getKeyCode()) {
			case KeyEvent.VK_SPACE:
				fire(true);
				break;
			case KeyEvent.VK_RIGHT:
				right = true;
				break;
			case KeyEvent.VK_LEFT:
				left = true;
				break;
			}
		}

		public void keyTyped(KeyEvent event) {
		}

		public void keyReleased(KeyEvent event) {
			// Ű���尡 �������� false��ȯ
			switch (event.getKeyCode()) {

			case KeyEvent.VK_SPACE:
				fire(false);
				break;
			case KeyEvent.VK_RIGHT:
				right = false;
				break;
			case KeyEvent.VK_LEFT:
				left = false;
				break;
			}
		}
	}
	//���������ǳ�
	class ending extends JPanel{

		public void paintComponent(Graphics g){		
			Image img=new ImageIcon(getClass().getResource(BACKEND)).getImage();
			g.drawImage(img, 0, 0, 705,680,this);

			g.setFont(new Font("Yj BLOCK �߰�",Font.BOLD,30));

			u1.setText(user1.getText());
			u2.setText(user2.getText());
			u1sco.setText(whiteCount.getText());
			u2sco.setText(blackCount.getText());

		}
	}

	// ������ ����Ǹ� ����
	private void finishGame() {

		bgmSound.stop();
		startClock.stop();			//�ð��귯���°� ����
		gametimer.stop();
		playPanel.requestFocus();
		playPanel.setFocusable(false);
		playPanel.setVisible(false);	//����ȭ�� ����
		ending.setVisible(true);	//����ȭ�� �����
	}

	// Ű����� ȭ��ǥ ���� ���� ���� �� �޺� üũ�ϱ�
	public void checkCombo(){   
		whiteCount.setText(""+score);

		try {
			writer.writeObject(new GameMessage(GameMessage.MsgType.GAME_INFO, my2, score));
			writer.flush();

		} catch(Exception ex) {
			JOptionPane.showMessageDialog(null, "������ ������ �߻��Ͽ����ϴ�.");
			ex.printStackTrace();

		}
	}

	//�ܰ��� �غ�
	private void prepareEnemy() {
		enemyList = new ArrayList<Keyplay>();
		enemyList1 = new ArrayList<Keyplay1>();
		star = new ArrayList<Keyplay2>();
		bomb = new ArrayList<Keyplay3>();
		for (int i = 0; i < 5; i++)
			enemyList.add(new Keyplay(getClass().getResource(ALIEN), ALIEN_SIZE, 3, 705, 655));
		enemyList1 = new ArrayList<Keyplay1>();
		for (int i = 0; i < 5; i++)
			enemyList1.add(new Keyplay1(getClass().getResource(ALIEN1), ALIEN_SIZE1, 5, 705, 655));

	}

	//���� ���� �г�
	class PlayPanel extends JPanel{
		JLabel Score = new JLabel("0");	//���� ��
		public PlayPanel(){

			player = new Keyplay(getClass().getResource(PLAYER),PLAYER_SIZE,705,655);
			prepareEnemy();
			score=0;
			player.x=10;
			player.y=600;

		}

		public void paintComponent(Graphics g){
			countSet.repaint();
			Image img=new ImageIcon(getClass().getResource(BACK)).getImage();
			g.drawImage(img, 0, 0, this.getWidth(),this.getHeight(),this);

			if(startCheck==true){
				Score.setText(Integer.toString(score));  //String Score�� int�� score���� ��ȯ
				whiteCount.setText(""+score);

				//player�� �׷��ش�
				player.draw(g, this);	

				// ���� �����۰� ��ź�� �׷��ش�.
				for (Keyplay k : enemyList)
					k.draw(g, this);
				for (Keyplay1 k : enemyList1)
					k.draw(g, this);
				for (Keyplay2 k : star)
					k.draw(g, this);
				for (Keyplay3 k : bomb)
					k.draw(g, this);

				//fire�� �׷��ش�
				for (int i = 0; i < shots.size(); i++) {
					Bubble sprite = (Bubble) shots.get(i);
					sprite.draw(g, this);
				}
			}
		}


	}	//close inner class
	class AnimeListener implements ActionListener{ //player
		public void actionPerformed(ActionEvent e){
			moving();
			// ���� fire�� �浹�ϸ� ���� fire �������� ���ھ 1 �ö󰣴�.
			for (int j = 0; j < enemyList.size(); j++) {
				for (int i = 0; i < shots.size(); i++) {
					if (enemyList.get(j).collide(new Point(shots.get(i).getX(), shots.get(i).getY()))) {
						enemyList.remove(j);
						shots.remove(i);
						score++;
						checkCombo();
						break;
					}
				}
			}
			// ���� fire�� �浹�ϸ� ���� fire �������� ���ھ 3 �ö󰣴�.
			for (int j = 0; j < enemyList1.size(); j++) {
				for (int i = 0; i < shots.size(); i++) {
					if (enemyList1.get(j).collide(new Point(shots.get(i).getX(), shots.get(i).getY()))) {
						enemyList1.remove(j);
						shots.remove(i);
						score = score +3;
						checkCombo();
						break;
					}
				}
			}
			// �÷��̾�� ���� �浹�ϸ� ���� �������� ���ھ� 1, ����� 5 �ö󰣴�.
			for (int j = 0; j < star.size(); j++) {
				for (int i = 0; i < player.xBoundary; i++) {
					if (star.get(j).collide(new Point(player.x, player.y))) {
						star.remove(j);
						// ���� �ø���
						score = score +1;
						FIRE_SIZE=FIRE_SIZE+3;
						break;
					}
				}
			}
			// �÷��̾�� ���� �浹�ϸ� ���� �������� ���ھ� 1, ����� 3�پ���.
			for (int j = 0; j < bomb.size(); j++) {
				for (int i = 0; i < player.xBoundary; i++) {
					if (bomb.get(j).collide(new Point(player.x, player.y))) {
						bomb.remove(j);
						// ���� �ø���
						score = score - 1;
						BOMB_SIZE=BOMB_SIZE-3;
						break;
					}
				}
			}
			// ��ź�� ������ �������� �����ش�.
			for (int j = 0; j < bomb.size(); j++) {
				if (bomb.get(j).getY() > 660) {
					bomb.remove(j);

					break;
				}
			}
			// ���� ������ �������� �����ش�.
			for (int j = 0; j < star.size(); j++) {
				if (star.get(j).getY() > 660) {
					star.remove(j);

					break;
				}
			}
			// fire�� 47�̻����� ���� �����ش�.
			for (int j = 0; j < shots.size(); j++) {
				if (shots.get(j).getY() < 47) {
					shots.remove(j);
					break;
				}
			}
			// ���� arraylist�� �־��ְ� �����δ�.
			for (Keyplay k : enemyList) 
				k.move();
			for (Keyplay1 k : enemyList1) 
				k.move();
			for (Keyplay2 k2 : star) 
				k2.move();

			for (Keyplay3 k2 : bomb) 
				k2.move();


			for (int i = 0; i < shots.size(); i++) {
				Bubble sprite = (Bubble) shots.get(i);
				sprite.move();
			}

			nanu++;	//ȭ���� ���Ӱ� �����ٶ� ���� �÷���
			if ( nanu % 100 == 0)// CL�� �̹����� �ٽñ׷����� �ܰ����� ��Ÿ���°� ��,�ð�
				enemyList.add(getRandomAttacker(ALIEN, ALIEN_SIZE, 3));
			if ( nanu % 200 == 0)// CL�� �̹����� �ٽñ׷����� �ܰ��� ��Ÿ���°� ��,�ð�
				enemyList1.add(getRandomAttacker1(ALIEN1, ALIEN_SIZE1, 5));
			if ( nanu % 200 == 0)// CL�� �̹����� �ٽñ׷����� ���� ��Ÿ���°� ��,�ð�
				star.add(getStar(STAR, STAR_SIZE, 5));
			if ( nanu % 100 == 0)// CL�� �̹����� �ٽñ׷����� ��ź�� ��Ÿ���°� ��,�ð�
				bomb.add(getBomb(BOMB, BOMB_SIZE, 4));
			if ( nanu % 200 == 0)// CL�� �̹����� �ٽñ׷����� ��ź�� ��Ÿ���°� ��,�ð�
				bomb.add(getBomb(BOMB, BOMB_SIZE1, 6));
			playPanel.repaint();
		}
	}
	// fire������ �޼ҵ�
	public void fire(Boolean b) {
		Bubble shot = new Bubble(getClass().getResource(FIRE), FIRE_SIZE, 705, 655, player.getX(),
				player.getY());

		shots.add(shot);// shots�� shot�̹��� �ֱ�

		playPanel.repaint();
	}

	// player�� ������
	public void moving() {
		if (right) {
			if (player.x <= 690)
				player.x += 10;
		}
		if (left) {
			if (player.x >= 0)
				player.x -= 10;
		}
	}

	// ���� �������� �������ϴ� �޼ҵ�-��ȫ
	private Keyplay getRandomAttacker(String pic, int margin, int steps) {
		int rand = (int) (Math.random() * 3) + 1;
		Keyplay newAttacker;
		switch (rand) {
		case 1:
			newAttacker = new Keyplay(getClass().getResource(pic), margin, steps, 690, 655);
			break;
		case 2:
			newAttacker = new Keyplay(getClass().getResource(pic), margin, steps,690, 655);
			break;
		case 3:
			newAttacker = new Keyplay(getClass().getResource(pic), margin, steps, 690, 655);
			break;
		default:
			newAttacker = new Keyplay(getClass().getResource(pic), margin, steps, 690, 655);
		}
		return newAttacker;
	}
	// ���� �������� �������ϴ� �޼ҵ�-��ȫ
	private Keyplay1 getRandomAttacker1(String pic, int margin, int steps) {
		int rand = (int) (Math.random() * 3) + 1;
		Keyplay1 newAttacker1;
		switch (rand) {
		case 1:
			newAttacker1 = new Keyplay1(getClass().getResource(pic), margin, steps, 690, 655);
			break;
		case 2:
			newAttacker1 = new Keyplay1(getClass().getResource(pic), margin, steps,690, 655);
			break;
		case 3:
			newAttacker1 = new Keyplay1(getClass().getResource(pic), margin, steps, 690, 655);
			break;
		default:
			newAttacker1 = new Keyplay1(getClass().getResource(pic), margin, steps, 690, 655);
		}
		return newAttacker1;
	}
	// ���� �������� �������ϴ� �޼ҵ�-��
	private Keyplay2 getStar(String pic, int margin, int steps) {
		int rand = (int) (Math.random() * 3) + 1;
		Keyplay2 newstar;
		switch (rand) {
		case 1:
			newstar = new Keyplay2(getClass().getResource(pic), margin, steps, 690, 655);
			break;
		case 2:
			newstar = new Keyplay2(getClass().getResource(pic), margin, steps, 690, 655);
			break;
		case 3:
			newstar = new Keyplay2(getClass().getResource(pic), margin, steps, 690, 655);
			break;
		default:
			newstar = new Keyplay2(getClass().getResource(pic), margin, steps, 690, 655);
		}
		return newstar;
	}
	// ���� �������� �������ϴ� �޼ҵ�-��ź
	private Keyplay3 getBomb(String pic, int margin, int steps) {
		int rand = (int) (Math.random() * 3) + 1;
		Keyplay3 newbomb;
		switch (rand) {
		case 1:
			newbomb = new Keyplay3(getClass().getResource(pic), margin, steps, 690, 655);
			break;
		case 2:
			newbomb = new Keyplay3(getClass().getResource(pic), margin, steps, 690, 655);
			break;
		case 3:
			newbomb = new Keyplay3(getClass().getResource(pic), margin, steps, 690, 655);
			break;
		default:
			newbomb = new Keyplay3(getClass().getResource(pic), margin, steps, 690, 655);
		}
		return newbomb;
	}


	// �ð� ���÷��̸� ���� ����ϴ� �ð�
	private class ClockListener implements ActionListener {
		public void actionPerformed (ActionEvent event) {		
			times++;
			time.setText(""+times+"��");
			System.out.println(times+"��");
			if(times >=60){
				finishGame();		
			}
		}
		public void reset() {			
			times = 0;
		}
		public int getElaspedTime() {	//���� �ý��۽ð��� ����
			return times;
		}
	}	//close inner class
	public void setTurnCheck(boolean x)
	{
		turnCheck = x;
		if(turnCheck==true){b++;}

	}

	public void setStartCheck(boolean x)
	{
		startCheck = x;
	}

	private class QuitListener implements ActionListener 	// ���� ��ư(��� �����Ӱ� â��  ������ ������.)
	{                
		public void actionPerformed(ActionEvent event) 
		{
			System.exit(0);	// ���α׷� ����
		}
	}	//close inner class

	public void gameSet(String my, String you){
		my2=my;
		you2=you;
		if(userCheck) {
			user1.setText(my); 
			user2.setText(you); 
		}
		if(!userCheck) {
			user1.setText(my); 
			user2.setText(you);
		}

		if(this.turnCheck){
			user1Panel.setBackground(new Color(100,200,40));
			countSet.setBackground(new Color(100,200,40));
		}
		else {
			user1Panel.setBackground(Color.BLACK);
			countSet.setBackground(Color.BLACK);
		}
	}

	public void BoardSet(int i) {
		// TODO Auto-generated method stub
		System.out.println(i);
		blackCount.setText(""+i);
		countSet.repaint();
	}

}//class GameClient�ݱ�


