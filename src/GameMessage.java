import java.awt.event.ActionEvent;
import java.io.Serializable;

import javax.swing.JButton;

public class GameMessage implements Serializable {
	// �޽��� Ÿ�� ����
	// 1���� �޽��� ���� �ʵ�� 3���� String�� �ʵ�.
	// NO_ACT�� ������ �� �ִ� Dummy �޽���. ������ ������ ����ϱ� ���� ����� ����
	// (1) Ŭ���̾�Ʈ�� ������ �޽��� ����
	//	- LOGIN  : CLIENT �α���.
	//		�޽��� ���� : LOGIN, "�۽���", "", ""
	//	- LOGOUT : CLIENT �α׾ƿ�.
	//		�޽��� ���� : LOGOUT, "�۽���", "", ""
	// 	- CLIENT_MSG : �������� ������  ��ȭ .
	// 		�޽�������  : CLIENT_MSG, "�۽���", "������", "����"
	// (2) ������ ������ �޽��� ����
	// 	- LOGIN_FAILURE  : �α��� ����
	//		�޽��� ���� : LOGIN_FAILURE, "", "", "�α��� ���� ����"
	// 	- SERVER_MSG : Ŭ���̾�Ʈ���� �������� ������ ��ȭ 
	//		�޽�������  : SERVER_MSG, "�۽���", "", "����" 
	// 	- LOGIN_LIST : ���� �α����� ����� ����Ʈ.
	//		�޽��� ���� : LOGIN_LIST, "", "", "/�� ���е� ����� ����Ʈ"
	public enum MsgType {NO_ACT, LOGIN, LOGOUT, CLIENT_MSG, LOGIN_FAILURE, SERVER_MSG, LOGIN_LIST,LOGIN_FULL, GAME_INFO, GAME_START, GAME_P,GAME_ITEM,WHAT1,WHAT2};
	public static final String ALL = "��ü";	 // ����� �� �� �ڽ��� ������ ��� �α��εǾ� �ִ�
											 // ����ڸ� ��Ÿ���� �ĺ���
	private MsgType type;
	private String sender;
	private String receiver;
	private String contents;	

	public int i,j;
	public int count;
	public boolean userCheck;
	
	public GameMessage() {
		this(MsgType.NO_ACT, "", "", "");
	}
	public GameMessage(MsgType t, String sID, String rID, String mesg) {
		type = t;
		sender = sID;
		receiver = rID;
		contents = mesg;
	}
	/*
	 public OthelloMessage(MsgType t, int i, int j, int index){
		type = t;
		this.i=i;
		this.j=j;
		this.index= index;
		System.out.println("�ȳ� �� �޽�����. ���� "+i+"��"+j+"��"+index);
		
	}
	public OthelloMessage(MsgType t, int i, int j, int index){
		type = t;
		this.i=i;
		this.j=j;
		this.index= index;
	}*/
	

	public GameMessage(MsgType t,String s, int i){
		System.out.println("sender "+s);
		System.out.println("count "+i);
		type = t;
		count=i;
		sender=s;
		
	}
	public GameMessage(MsgType t, int count){
		type = t;
		this.i=count;
		
	}
	
	public GameMessage(MsgType t, boolean uk){
		type = t;
		userCheck = uk;
	}

	public GameMessage(MsgType t, String what,boolean uk){
		type = t;
		userCheck = uk;
		sender = what;
	}
	public void setType (MsgType t) {
		type = t;
	}
	public MsgType getType() {
		return type;
		
	}

	public void setSender (String id) {
		sender = id;
	}
	public String getSender() {
		return sender;
	}
	
	public void setReceiver (String id) {
		receiver = id;
	}
	public String getReceiver() {
		return receiver;
	}
	
	public void setContents (String mesg) {
		contents = mesg;
	}
	public String getContents() {
		return contents;
	}
	
	public String toString() {
		return ("�޽��� ���� : " + type + "\n" +
				"�۽���         : " + sender + "\n" +
				"������         : " + receiver + "\n" +
				"�޽��� ���� : " + contents + "\n");
	}	
}
