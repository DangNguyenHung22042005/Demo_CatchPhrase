package view;

import java.awt.EventQueue;
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
import javax.swing.border.EmptyBorder;
import controller.ResetPlayerUIListener;
import infor.BossInfor;
import infor.PlayerInfor;
import panel.DrawingPanel;

public class PlayerView extends JFrame implements ActionListener {
	private JTextField textField_Answer;
	private DrawingPanel drawing;
	private Socket playerSocket;
	private PlayerInfor inforOfPlayerSend;
	private ObjectOutputStream outputStream;

	public PlayerView() {
		init();
		try {
			playerSocket = new Socket("localhost", 1111);
			listenToServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
		PlayerAppear();
	}

	public void init() {
		ResetPlayerUIListener listen = new ResetPlayerUIListener(this);

		this.setTitle("Player Room");
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

		JButton button_Guess = new JButton("G");
		button_Guess.setFont(new Font("Tahoma", Font.PLAIN, 20));
		button_Guess.setBounds(395, 240, 71, 42);
		button_Guess.addActionListener(this);
		getContentPane().add(button_Guess);

		this.setVisible(true);
	}

	public void PlayerAppear() {
		try {
			inforOfPlayerSend = new PlayerInfor();
			outputStream = new ObjectOutputStream(playerSocket.getOutputStream());
			outputStream.writeObject(inforOfPlayerSend);
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void ResetAnswer() {
		this.textField_Answer.setText("");
	}

	public void actionPerformed(ActionEvent e) {
		System.out.println("Đã nhấn G");
		try {
			inforOfPlayerSend = new PlayerInfor();
			inforOfPlayerSend.setAnswerOfPlayer(this.textField_Answer.getText());

			outputStream = new ObjectOutputStream(playerSocket.getOutputStream());
			outputStream.writeObject(inforOfPlayerSend);
			outputStream.flush();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void listenToServer() {
		new Thread(() -> {
				try {			
					while (true) {
						ObjectInputStream inputStream = new ObjectInputStream(playerSocket.getInputStream());
						Object receivedData = inputStream.readObject();
						if (receivedData instanceof BossInfor) {
							BossInfor boss = (BossInfor) receivedData;
						this.drawing.setLines(boss.getLines());
						drawing.repaint();
						} 			
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		}).start();
	}

	public static void main(String[] args) {
		new PlayerView();
	}

}
