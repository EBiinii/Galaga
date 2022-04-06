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
	Timer startClock;					// 시계구현 타이머(게임진행시간을 세서 지정한 시간되면 게임종료)
	Timer gametimer;

	JFrame startframe;
	JFrame frame;

	JTextArea incoming;			// 수신된 메시지를 출력하는 곳
	JTextArea outgoing;			// 송신할 메시지를 작성하는

	JList counterParts;			// 현재 로그인한 채팅 상대목록을 나타내는 리스트.

	ObjectInputStream reader;	// 수신용 스트림
	ObjectOutputStream writer;	// 송신용 스트림
	Socket sock;				// 서버 연결용 소켓

	JScrollPane qScroller;

	private boolean turnCheck=false;
	private boolean startCheck=false;	//게임 시작 활성을 체크함
	public boolean userCheck = false;	//유저 중 시작버튼을 누른 사람을 체크함

	ImageIcon end = new ImageIcon("/res/backend.png");
	ImageIcon stBtn = new ImageIcon("ddd/login.png");

	private int count=0,count1=0;
	private int i,j;
	int b=0;
	int score;	//총점수
	int times;	//초를 세는 변수
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
	String user;				// 이 클라이언트로 로그인 한 유저의 이
	JPanel ending;
	JPanel startBack;
	JPanel user1Panel = new JPanel(); //상대방 게임 패널
	JPanel user2Panel = new JPanel(); //상대방 게임 패널
	JPanel countSet;	//이름, 점수, 시간경과를 보여주는 패널
	JPanel playPanel;	//게임패널

	JLabel u1 = new JLabel("0");
	JLabel u2 = new JLabel("0");
	JLabel u1sco = new JLabel("0");
	JLabel u2sco = new JLabel("0");


	JLabel time = new JLabel("준비");
	private JLabel time1Count;
	private JLabel time2Count;
	private JLabel whiteCount;	//user1의 점수를 보여줌
	private JLabel blackCount;	//user2의 점수를 보여줌



	JButton start;	//게임시작버튼
	JButton logButton;			// 토글이 되는 로그인/로그아웃 버튼
	JButton sendButton;	//메세지 보내기 버튼
	JButton goGame;

	MainPanel game;

	ClockListener clockListener;	// 시계를 구현하기 위한 리스너

	private AudioClip bgmSound;		//배경음악

	private final String BGM_SOUND = "/res/galaga.wav";	

	ArrayList<Keyplay> enemyList;// 외계인 넣는 arraylist
	ArrayList<Keyplay1> enemyList1;// 외계인 넣는 arraylist
	ArrayList<Keyplay2> star;// 아이템을 넣는 arraylist
	ArrayList<Keyplay3> bomb;// 폭탄을 넣는 arraylist
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

	private boolean right = false, left = false; // 움직임을 부드럽게 하기위한
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
		frame = new JFrame(frameTitle + " : 로그인하세요");
		startframe = new JFrame("입장 전 주의사항");
		game = new MainPanel();

		game.setLayout(null); //기본으로 주어지는 레이아웃이 있는데 이걸 안쓰고 절대좌표로 주기위해


		// 메시지 디스플레이 창
		incoming = new JTextArea(5,10);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);
		qScroller = new JScrollPane(incoming);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		qScroller.setBounds(210,705,495,140);
		qScroller.setBackground(Color.BLACK);

		// 대화 상대 목록. 초기에는 "전체" - ChatMessage.ALL 만 있음
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

		// 메시지 디스플레이 창  - 보내는 창
		outgoing = new JTextArea(5,10);
		outgoing.addKeyListener(new EnterKeyListener());
		//outgoing.addKeyListener(new DirectionListener());
		outgoing.setLineWrap(true);
		outgoing.setWrapStyleWord(true);
		outgoing.setEditable(true);
		outgoing.setBounds(210,860,400, 35);
		//outgoing.setBackground(Color.BLACK);

		//보내기 버튼
		sendButton = new JButton(new ImageIcon("ddd/send.png"));	//보내기버튼()에 파일 지정으로 이미지 넣기
		sendButton.setBorderPainted(false);
		sendButton.setFocusPainted(false);
		sendButton.setContentAreaFilled(false);
		sendButton.addActionListener(new SendButtonListener());
		sendButton.setBounds(615,860,35, 35);

		//로그인 버튼
		//logButton = new JButton("login");
		logButton = new JButton(stBtn);	//로그인버튼-()에 파일 지정으로 이미지 넣기
		logButton.setText("login");
		logButton.setBorderPainted(false);
		logButton.setFocusPainted(false);
		logButton.setContentAreaFilled(false);
		logButton.addActionListener(new LogButtonListener());
		logButton.setBounds(660,860,45, 35);

		//시작버튼
		start=new JButton(new ImageIcon("ddd/start.png"));
		start.setBorderPainted(false);
		start.setFocusPainted(false);
		start.setContentAreaFilled(false);
		start.setBounds(645, 5, 60, 35);
		start.addActionListener(new StartButtonListener());

		//시간경과
		time = new JLabel("준비");
		time.setBounds(600, 5, 50, 35);		
		time.setForeground(Color.WHITE);	//시간경과 색은 하얀색

		//user1
		user1 = new JTextField();
		user1.setBounds(5, 5, 50, 35);		
		user1.setForeground(Color.WHITE);	//user1의 이름을 나타내는 색은 하얀색
		user1.setBackground(Color.BLACK);	//user1의 이름을 나타내는 배경은 검정색
		user1.setEditable(false);
		user1.setHorizontalAlignment(JTextField.CENTER);

		//user1의 점수
		ImageIcon whiteIcon1 = new ImageIcon("/res/하얀색.jpg");
		whiteCount = new JLabel("0",whiteIcon1, SwingConstants.CENTER);
		whiteCount.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
		whiteCount.setBounds(60,5,100,35);
		whiteCount.setForeground(Color.WHITE);	//user1의 총점을 나타내는 색은 하얀색

		//user2
		user2 = new JTextField();
		user2.setBounds(200, 5, 50, 35);		
		user2.setForeground(Color.WHITE);	//user2의 이름을 나타내는 색은 하얀색
		user2.setBackground(Color.BLACK);	//user2의 이름을 나타내는 배경은 검정색
		user2.setEditable(false);
		user2.setHorizontalAlignment(JTextField.CENTER);

		//user2의 점수
		ImageIcon blackIcon1 = new ImageIcon("/res/하얀색.jpg");
		blackCount = new JLabel("0",blackIcon1, SwingConstants.CENTER);
		blackCount.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
		blackCount.setBounds(255,5,100,35);
		blackCount.setForeground(Color.WHITE);	//user2의 총점을 나타내는 색은 하얀색

		//이름, 총점, 시간경과를 보여주는 패널 셋팅
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

		//엔딩화면 안 설정
		u1.setBounds(470, 200, 50, 100);
		u1.setForeground(Color.WHITE);
		u2.setBounds(470, 290, 50, 100);
		u2.setForeground(Color.WHITE);
		u1sco.setBounds(610, 200, 50, 100);
		u1sco.setForeground(Color.WHITE);
		u2sco.setBounds(610, 290, 50, 100);
		u2sco.setForeground(Color.WHITE);


		//엔딩화면 설정
		ending = new ending();
		ending.add(u1);
		ending.add(u2);
		ending.add(u1sco);
		ending.add(u2sco);

		ending.setBounds(0,47,705,655);
		ending.setLayout(null);
		ending.setVisible(false);

		//게임이 실행되는 화면설정
		playPanel = new PlayPanel();
		playPanel.setBounds(0,46,705,655);
		playPanel.addKeyListener(new DirectionListener());// 키보드 리스너 설치
		playPanel.requestFocus(); // (키리스너가 작동하게하기위해서)독점할 권한을 준다. 혹시모를 버그 방지
		playPanel.setFocusable(true);// 초기에는 포키싱 안되게 함(즉 키 안먹음


		//리스너 설정
		gametimer = new Timer(10,new AnimeListener());
		startClock=new Timer(1000, new ClockListener());

		//game.addKeyListener(new DirectionListener());
		//game.requestFocus();
		//game.setFocusable(true);

		//입장버튼 설정
		goGame =  new JButton(new ImageIcon("ddd/go.png"));	
		goGame.setBorderPainted(false);
		goGame.setFocusPainted(false);
		goGame.setContentAreaFilled(false);
		goGame.setBounds(190,330,120,50);
		goGame.addActionListener(new GoGameListener());

		//입장패널 설정
		startBack = new startBack();
		startBack.add(goGame);
		startBack.setBounds(0, 0, 500, 400);
		startBack.setLayout(null);
		startBack.setVisible(true);

		// 클라이언드 프레임 창 조정
		game.add(outgoing);
		game.add(sendButton);
		game.add(logButton);
		game.add(cScroller);
		game.add(qScroller);
		game.add(countSet);
		game.add(playPanel);
		game.add(ending);
		game.repaint();

		//입장프레임 창 조절
		startframe.add(startBack);
		startframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		startframe.setBounds(100,100,500,400);
		startframe.setLayout(null);
		startframe.setVisible(true);
		startframe.setResizable(false);

		//메인프레임 창 조절
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(game);
		frame.setSize(720,930);
		frame.setVisible(false);
		frame.setResizable(false);	//화면크기를 조정가능/ 불가능 설정
		frame.setBackground(Color.BLACK);
		// 프레임이 살아 있으므로 여기서 만들은 스레드는 계속 진행 됨
		// 이 프레임 스레드를 종료하면, 이 프레임에서 만든 스레드들은 예외를 발생하게되고
		// 이를 이용해 모든 스레드를 안전하게 종료 시키도록 함
		// 소리 파일 사용
		try {//예외가 생길 수 있는 코드
			bgmSound = JApplet.newAudioClip(getClass().getResource(BGM_SOUND));	//배경소리

		}
		catch(Exception e){//예외가 생겼을 때 실행될 구문
			System.out.println("소리파일에서 에러가 떴습니다.");
		}
		bgmSound.play();

	}

	//게임종료판넬
	class startBack extends JPanel{

		public void paintComponent(Graphics g){		
			Image img=new ImageIcon(getClass().getResource(STARTBACK)).getImage();
			g.drawImage(img, 0, 0, 500,400,this);

		}
	}

	//네트워크 셋팅
	private void setUpNetworking() {  
		try {
			//sock = new Socket("220.69.203.11", 5000);
			sock = new Socket("127.0.0.1", 5000);			// 소켓 통신을 위한 포트는 5000번 사용키로 함
			reader = new ObjectInputStream(sock.getInputStream());
			writer = new ObjectOutputStream(sock.getOutputStream());

		} catch(Exception ex) {
			JOptionPane.showMessageDialog(null, "서버접속에 실패하였습니다. 접속을 종료합니다.");
			ex.printStackTrace();
			frame.dispose();		// 네트워크가 초기 연결 안되면 클라이언트 강제 종료
		}
	} // close setUpNetworking   


	// 로그인과 아웃을 담당하는 버튼의 감청자. 처음에는 Login 이었다가 일단 로그인 되고나면 Logout을 처리
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
		// 로그인 처리
		private void processLogin() {
			user = JOptionPane.showInputDialog("사용자 이름을 입력하세요");
			try {
				writer.writeObject(new GameMessage(GameMessage.MsgType.LOGIN, user, "", ""));
				writer.flush();
				frame.setTitle(frameTitle + " (로그인 : " + user + ")");
			} catch(Exception ex) {
				JOptionPane.showMessageDialog(null, "로그인 중 서버접속에 문제가 발생하였습니다.");
				ex.printStackTrace();
			}
		}
		// 로그아웃 처리
		private void processLogout() {
			int choice = JOptionPane.showConfirmDialog(null, "Logout합니다");
			if (choice == JOptionPane.YES_OPTION) {
				try {
					writer.writeObject(new GameMessage(GameMessage.MsgType.LOGOUT, user, "", ""));
					writer.flush();
					// 연결된 모든 스트림과 소켓을 닫고 프로그램을 종료 함
					writer.close(); reader.close(); sock.close();
				} catch(Exception ex) {
					JOptionPane.showMessageDialog(null, "로그아웃 중 서버접속에 문제가 발생하였습니다. 강제종료합니다");
					ex.printStackTrace();
				} finally {
					System.exit(100);			// 클라이언트 완전 종료 
				}
			}
		}
	}  // close LoginButtonListener inner class

	//send버튼 누르면 반응하는 리스너
	public class SendButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			String to = (String) counterParts.getSelectedValue();
			if (to == null) {
				JOptionPane.showMessageDialog(null, "송신할 대상을 선택한 후 메시지를 보내세요");
				return;
			}
			try {
				incoming.append(user + " : " + outgoing.getText() + "\n"); // 나의 메시지 창에 보이기
				writer.writeObject(new GameMessage(GameMessage.MsgType.CLIENT_MSG, user, to, outgoing.getText()));
				writer.flush();
				outgoing.setText("");
				outgoing.requestFocus();
			} catch(Exception ex) {
				JOptionPane.showMessageDialog(null, "메시지 전송중 문제가 발생하였습니다.");
				ex.printStackTrace();
			}
		}
	}  // close SendButtonListener inner class

	//엔터 누르면 반응하는 리스너
	public class EnterKeyListener implements KeyListener{
		boolean presscheck=false;	//키입력체크
		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			if(e.getKeyCode()==KeyEvent.VK_SHIFT){
				presscheck = true;	//키입력들어감
			}
			else if(e.getKeyCode()==KeyEvent.VK_ENTER){
				if(presscheck == true){
					String str = outgoing.getText() +"\r\n";	//텍스트입력
					outgoing.setText(str);
					presscheck = false;
				}
				else{
					e.consume();
					presscheck = false;
					String to = (String) counterParts.getSelectedValue();
					if (to == null) {
						JOptionPane.showMessageDialog(null, "송신할 대상을 선택한 후 메시지를 보내세요");
						return;
					}
					try {
						incoming.append(user + " : " + outgoing.getText() + "\n"); // 나의 메시지 창에 보이기
						incoming.setSelectionStart(incoming.getText().length());
						qScroller.getVerticalScrollBar().setValue(qScroller.getVerticalScrollBar().getMaximum());
						writer.writeObject(new GameMessage(GameMessage.MsgType.CLIENT_MSG, user, to, outgoing.getText()));
						writer.flush();
						outgoing.setText("");
						outgoing.requestFocus();
					} catch(Exception ex) {
						JOptionPane.showMessageDialog(null, "메시지 전송중 문제가 발생하였습니다.");
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
	// 서버에서 보내는 메시지를 받는 스레드 작업을 정의하는 클래스
	public class IncomingReader implements Runnable {
		public void run() {
			GameMessage message;             
			GameMessage.MsgType type;
			String[] users={};

			try {
				while (true) {
					message = (GameMessage) reader.readObject();     	 // 서버로기 부터의 메시지 대기                   
					type = message.getType();

					if (type == GameMessage.MsgType.LOGIN_FAILURE) {	 // 로그인이 실패한 경우라면
						JOptionPane.showMessageDialog(null, "Login이 실패하였습니다. 다시 로그인하세요");
						frame.setTitle(frameTitle + " : 로그인 하세요");
						logButton.setText("Login");
					} else if (type == GameMessage.MsgType.LOGIN_FULL) { // 메시지를 받았다면 보여줌
						JOptionPane.showMessageDialog(null, "인원이 가득 찼습니다.");						
					} else if (type == GameMessage.MsgType.SERVER_MSG) { // 메시지를 받았다면 보여줌
						if (message.getSender().equals(message.getReceiver())) continue;  // 내가 보낸 편지면 보일 필요 없음
						incoming.append(message.getSender() + " : " + message.getContents() + "\n");
						qScroller.getVerticalScrollBar().setValue(qScroller.getVerticalScrollBar().getMaximum());

					} else if (type == GameMessage.MsgType.LOGIN_LIST) {
						// 유저 리스트를 추출 해서 counterParts 리스트에 넣어 줌.
						// 나는  빼고 (""로 만들어 정렬 후 리스트 맨 앞에 오게 함)
						users = message.getContents().split("/");
						for (int i=0; i<users.length; i++) {
							if (user.equals(users[i]))users[i] = "";
						}
						users = sortUsers(users);		// 유저 목록을 쉽게 볼 수 있도록 정렬해서 제공
						users[0] =  GameMessage.ALL;	// 리스트 맨 앞에 "전체"가 들어가도록 함
						counterParts.setListData(users);
						counterParts.setSelectedIndex(0);
						frame.repaint();

					} else if (type == GameMessage.MsgType.NO_ACT){
						// 아무 액션이 필요없는 메시지. 그냥 스킵
					}
					else if (type == GameMessage.MsgType.GAME_INFO){
						System.out.println("안녕 나 클라이언트");
						System.out.println("i:"+count1);
						System.out.println("너의 값은 존재한다");
						if (message.getSender().equals(my2));
						else count1=message.count;
						BoardSet(count1);  
					}
					else if(type == GameMessage.MsgType.GAME_START){
						System.out.println("게임시작");
						try{
							Thread.sleep(1000);
							startCheck = true;
							userCheck = true;
							playPanel.requestFocus(); // (키리스너가 작동하게하기위해서)독점할 권한을 준다. 혹시모를 버그 방지
							playPanel.setFocusable(true);// 초기에는 포키싱 안되게 함(즉 키 안먹음
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
							JOptionPane.showMessageDialog(null, "게임 상대가 없습니다.");
							setStartCheck(false);
						}
					}
					else {
						// 정체가 확인되지 않는 이상한 메시지
						throw new Exception("서버에서 알 수 없는 메시지 도착했음");
					}
				} // close while
			} catch(Exception ex) {
				System.out.println("클라이언트 스레드 종료");		// 프레임이 종료될 경우 이를 통해 스레드 종료
				ex.printStackTrace();
			}
		} // close run

		// 주어진 String 배열을 정렬한 새로운 배열 리턴
		private String [] sortUsers(String [] users) {
			String [] outList = new String[users.length];
			ArrayList<String> list = new ArrayList<String>();
			for (String s : users) {
				list.add(s);
			}
			Collections.sort(list);				// Collections.sort를 사용해 한방에 정렬
			for (int i=0; i<users.length; i++) {
				outList[i] = list.get(i);
			}
			return outList;
		}
	} // close inner class 

	//goGame - 입장하기 버튼 누르면 시작되는 것들
	private class GoGameListener implements ActionListener{
		public void actionPerformed(ActionEvent ev) {
			startframe.dispose();
			frame.setVisible(true);
		}
	}

	//start버튼 누르면 시작되는 것들
	public class StartButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){


			if(e.getSource()==start){
				try {
					startCheck = true;                          // 게임시작 활성 
					userCheck = true;
					writer.writeObject(new GameMessage(GameMessage.MsgType.GAME_START, userCheck));
					writer.flush();

				} catch(Exception ex) {
					JOptionPane.showMessageDialog(null, "메시지 전송중 문제가 발생하였습니다.");
					ex.printStackTrace();
				}
			}

			gametimer.start();
			playPanel.requestFocus(); // (키리스너가 작동하게하기위해서)독점할 권한을 준다. 혹시모를 버그 방지
			playPanel.setFocusable(true);// 초기에는 포키싱 안되게 함(즉 키 안먹음



			game.repaint();	//게임패널 repaint시키면서 계속 뜨게하기

		}
	}	//close inner class

	// 키보드 감청자
	class DirectionListener implements KeyListener {
		public void keyPressed(KeyEvent event) {
			// 키보드가 눌리면 true반환
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
			// 키보드가 떼어지면 false반환
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
	//게임종료판넬
	class ending extends JPanel{

		public void paintComponent(Graphics g){		
			Image img=new ImageIcon(getClass().getResource(BACKEND)).getImage();
			g.drawImage(img, 0, 0, 705,680,this);

			g.setFont(new Font("Yj BLOCK 중간",Font.BOLD,30));

			u1.setText(user1.getText());
			u2.setText(user2.getText());
			u1sco.setText(whiteCount.getText());
			u2sco.setText(blackCount.getText());

		}
	}

	// 게임이 종료되면 실행
	private void finishGame() {

		bgmSound.stop();
		startClock.stop();			//시간흘러가는것 멈춤
		gametimer.stop();
		playPanel.requestFocus();
		playPanel.setFocusable(false);
		playPanel.setVisible(false);	//게임화면 종료
		ending.setVisible(true);	//종료화면 띄워줌
	}

	// 키보드와 화살표 방향 값과 같을 때 콤보 체크하기
	public void checkCombo(){   
		whiteCount.setText(""+score);

		try {
			writer.writeObject(new GameMessage(GameMessage.MsgType.GAME_INFO, my2, score));
			writer.flush();

		} catch(Exception ex) {
			JOptionPane.showMessageDialog(null, "전송중 문제가 발생하였습니다.");
			ex.printStackTrace();

		}
	}

	//외계인 준비
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

	//게임 실행 패널
	class PlayPanel extends JPanel{
		JLabel Score = new JLabel("0");	//점수 라벨
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
				Score.setText(Integer.toString(score));  //String Score를 int형 score으로 변환
				whiteCount.setText(""+score);

				//player를 그려준다
				player.draw(g, this);	

				// 적과 아이템과 폭탄을 그려준다.
				for (Keyplay k : enemyList)
					k.draw(g, this);
				for (Keyplay1 k : enemyList1)
					k.draw(g, this);
				for (Keyplay2 k : star)
					k.draw(g, this);
				for (Keyplay3 k : bomb)
					k.draw(g, this);

				//fire을 그려준다
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
			// 적이 fire와 충돌하면 적과 fire 없어지고 스코어가 1 올라간다.
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
			// 적이 fire와 충돌하면 적과 fire 없어지고 스코어가 3 올라간다.
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
			// 플레이어와 별이 충돌하면 별이 없어지고 스코어 1, 사이즈가 5 올라간다.
			for (int j = 0; j < star.size(); j++) {
				for (int i = 0; i < player.xBoundary; i++) {
					if (star.get(j).collide(new Point(player.x, player.y))) {
						star.remove(j);
						// 점수 올리기
						score = score +1;
						FIRE_SIZE=FIRE_SIZE+3;
						break;
					}
				}
			}
			// 플레이어와 별이 충돌하면 별이 없어지고 스코어 1, 사이즈가 3줄어든다.
			for (int j = 0; j < bomb.size(); j++) {
				for (int i = 0; i < player.xBoundary; i++) {
					if (bomb.get(j).collide(new Point(player.x, player.y))) {
						bomb.remove(j);
						// 점수 올리기
						score = score - 1;
						BOMB_SIZE=BOMB_SIZE-3;
						break;
					}
				}
			}
			// 폭탄이 및으로 내려가면 없애준다.
			for (int j = 0; j < bomb.size(); j++) {
				if (bomb.get(j).getY() > 660) {
					bomb.remove(j);

					break;
				}
			}
			// 별이 및으로 내려가면 없애준다.
			for (int j = 0; j < star.size(); j++) {
				if (star.get(j).getY() > 660) {
					star.remove(j);

					break;
				}
			}
			// fire가 47이상으로 갈때 없애준다.
			for (int j = 0; j < shots.size(); j++) {
				if (shots.get(j).getY() < 47) {
					shots.remove(j);
					break;
				}
			}
			// 적을 arraylist에 넣어주고 움직인다.
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

			nanu++;	//화면을 새롭게 보여줄때 마다 늘려줌
			if ( nanu % 100 == 0)// CL은 이미지가 다시그려지고 외계인이 나타나는것 즉,시간
				enemyList.add(getRandomAttacker(ALIEN, ALIEN_SIZE, 3));
			if ( nanu % 200 == 0)// CL은 이미지가 다시그려지고 외계인 나타나는것 즉,시간
				enemyList1.add(getRandomAttacker1(ALIEN1, ALIEN_SIZE1, 5));
			if ( nanu % 200 == 0)// CL은 이미지가 다시그려지고 별이 나타나는것 즉,시간
				star.add(getStar(STAR, STAR_SIZE, 5));
			if ( nanu % 100 == 0)// CL은 이미지가 다시그려지고 폭탄이 나타나는것 즉,시간
				bomb.add(getBomb(BOMB, BOMB_SIZE, 4));
			if ( nanu % 200 == 0)// CL은 이미지가 다시그려지고 폭탄이 나타나는것 즉,시간
				bomb.add(getBomb(BOMB, BOMB_SIZE1, 6));
			playPanel.repaint();
		}
	}
	// fire나오는 메소드
	public void fire(Boolean b) {
		Bubble shot = new Bubble(getClass().getResource(FIRE), FIRE_SIZE, 705, 655, player.getX(),
				player.getY());

		shots.add(shot);// shots에 shot이미지 넣기

		playPanel.repaint();
	}

	// player의 움직임
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

	// 적이 랜덤으로 나오게하는 메소드-분홍
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
	// 적이 랜덤으로 나오게하는 메소드-분홍
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
	// 별을 랜덤으로 나오게하는 메소드-별
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
	// 별을 랜덤으로 나오게하는 메소드-폭탄
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


	// 시간 디스플레이를 위해 사용하는 시계
	private class ClockListener implements ActionListener {
		public void actionPerformed (ActionEvent event) {		
			times++;
			time.setText(""+times+"초");
			System.out.println(times+"초");
			if(times >=60){
				finishGame();		
			}
		}
		public void reset() {			
			times = 0;
		}
		public int getElaspedTime() {	//현재 시스템시간을 리턴
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

	private class QuitListener implements ActionListener 	// 종료 버튼(모든 프레임과 창이  강제로 닫힌다.)
	{                
		public void actionPerformed(ActionEvent event) 
		{
			System.exit(0);	// 프로그램 종료
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

}//class GameClient닫기


