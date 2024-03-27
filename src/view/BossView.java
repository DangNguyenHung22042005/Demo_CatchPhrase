package view;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import controller.ResetBossUIListener;
import controller.ResetPlayerUIListener;
import infor.BossInfor;
import infor.PlayerInfor;
import panel.DrawingPanel;

public class BossView extends JFrame implements ActionListener {
	private JTextField textField_Answer;
	private DrawingPanel drawing;
	private Socket bossSocket;
	private BossInfor inforOfBossSend;
	private ObjectOutputStream outputStream;
	
	public BossView() {
		init();
		try {
			bossSocket = new Socket("localhost", 1111);
			listenToServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
		BossAppear();
	}
	
	public void init() {
		ResetBossUIListener listen = new ResetBossUIListener(this);

		this.setTitle("Boss Room");
		this.setSize(480, 320);
		this.setLocationRelativeTo(null);
		getContentPane().setLayout(null);

		JPanel panel_DrawArea = new JPanel();
		panel_DrawArea.setBounds(0, 0, 466, 242);
		panel_DrawArea.setLayout(null);
		drawing = new DrawingPanel();
		drawing.setLocation(0, 0);
		drawing.setSize(464, 242);
		panel_DrawArea.add(drawing);
		getContentPane().add(panel_DrawArea);

		textField_Answer = new JTextField();
		textField_Answer.setFont(new Font("Tahoma", Font.PLAIN, 20));
		textField_Answer.setBounds(0, 241, 327, 42);
		getContentPane().add(textField_Answer);
		textField_Answer.setColumns(10);

		JButton button_Delete = new JButton("D");
		button_Delete.setFont(new Font("Tahoma", Font.PLAIN, 20));
		button_Delete.setBounds(326, 240, 71, 42);
		button_Delete.addActionListener(listen);
		getContentPane().add(button_Delete);

		JButton button_Push = new JButton("P");
		button_Push.setFont(new Font("Tahoma", Font.PLAIN, 20));
		button_Push.setBounds(395, 240, 71, 42);
		button_Push.addActionListener(this);
		getContentPane().add(button_Push);

		this.setVisible(true);
	}
	
	public void BossAppear() {
		try {
			inforOfBossSend = new BossInfor();
			outputStream = new ObjectOutputStream(bossSocket.getOutputStream());
			outputStream.writeObject(inforOfBossSend);
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void ResetAll() {
		this.drawing.clearDrawing();
		this.textField_Answer.setText("");
	}
	
	public void actionPerformed(ActionEvent e) {
		System.out.println("Đã nhấn P");
		try {
			inforOfBossSend = new BossInfor();
			inforOfBossSend.setCorrectAnswer(this.textField_Answer.getText());
			inforOfBossSend.setLines(this.drawing.getLines());

			outputStream = new ObjectOutputStream(bossSocket.getOutputStream());
			outputStream.writeObject(inforOfBossSend);
			outputStream.flush();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public void listenToServer() {
		new Thread(() -> {
				try {			
					while (true) {
						ObjectInputStream inputStream = new ObjectInputStream(bossSocket.getInputStream());
						Object receivedData = inputStream.readObject();
						if (receivedData instanceof PlayerInfor) {
							PlayerInfor player = (PlayerInfor) receivedData;
							if (player.getAnswerOfPlayer().equals(textField_Answer.getText())) {
								System.out.println("Người dùng nào đó đã đoán đúng đáp án!");
							} else {
								System.out.println("Người dùng nào đó đã đoán sai đáp án!");
							}
						} 			
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		}).start();
	}

	public static void main(String[] args) {
		new BossView();
	}
}
