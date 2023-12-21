package WebRecorder;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class Window {

	public static JFrame frame = new JFrame();
	public static JTextField addressField;
	public static JTextField folderField;
	public static JTextField loadField;
	public static JLabel nowTimeLabel, maxTimeLabel;
	public static JButton replayStop, replayStart, replayLoad;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public static void initializeWindow() {
		frame = new JFrame();
		frame.setTitle("Web Recorder");
		frame.setBackground(Color.WHITE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 521, 284);
		frame.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("Address:");
		lblNewLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		lblNewLabel.setBounds(9, 10, 81, 28);
		frame.getContentPane().add(lblNewLabel);

		addressField = new JTextField();
		addressField.setText("https://chat.laplace.live/obs/24048374?altDanmakuLayout=false&baseFontSize=18&colorScheme=dark&customAvatarApi=&limitEventAmount=50&showAutoDanmaku=false&showAvatar=true&showAvatarFrame=false&showCurrentRank=false&showDanmaku=true&showEnterEvent=false&showEnterEventCurrentGuardOnly=false&showFollowEvent=true&showGift=true&showGiftFree=false&showGiftHighlightAbove=29.99&showGiftPriceAbove=0&showGiftStickyAbove=29.99&showGiftStickyAlways=false&showMedal=false&showMedalLightenedOnly=false&showModBadge=true&showOnlyCurrentMedal=false&showPhoneNotVerified=true&showStickyBar=true&showSuperChat=true&showSystemMessage=true&showToast=true&showUserLevelAbove=0&showUserLvl=false&showUsername=true&showWealthMedal=false&sortStickyBarByPrice=false");
		addressField.setBounds(71, 14, 414, 21);
		frame.getContentPane().add(addressField);
		addressField.setColumns(10);

		JButton loadPageBtn = new JButton("Load Webpage");
		loadPageBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.openWebpage(addressField.getText());
			}
		});
		loadPageBtn.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		loadPageBtn.setBounds(9, 92, 141, 29);
		frame.getContentPane().add(loadPageBtn);

		JButton recordStart = new JButton("Start Recording");
		recordStart.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		recordStart.setBounds(175, 92, 141, 29);
		frame.getContentPane().add(recordStart);

		JButton recordStop = new JButton("Stop Recording");
		recordStop.setEnabled(false);
		recordStop.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		recordStop.setBounds(344, 92, 141, 29);
		frame.getContentPane().add(recordStop);

		JLabel lblFolder = new JLabel("Save Path:");
		lblFolder.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		lblFolder.setBounds(9, 48, 74, 28);
		frame.getContentPane().add(lblFolder);

		recordStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				recordStart.setEnabled(false);
				recordStop.setEnabled(true);
				Main.startRecording();
			}
		});

		recordStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				recordStart.setEnabled(true);
				recordStop.setEnabled(false);
				Main.stopRecording();
			}
		});

		folderField = new JTextField();
		folderField.setText("C:\\Users\\fangs\\Desktop\\WebRecording");
		folderField.setColumns(10);
		folderField.setBounds(82, 52, 403, 21);
		frame.getContentPane().add(folderField);

		JLabel lblLoadPath = new JLabel("Load Path:");
		lblLoadPath.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		lblLoadPath.setBounds(9, 131, 74, 28);
		frame.getContentPane().add(lblLoadPath);

		loadField = new JTextField();
		loadField.setText("C:\\Users\\fangs\\Desktop\\WebRecording\\file.webrecording");
		loadField.setColumns(10);
		loadField.setBounds(82, 135, 403, 21);
		frame.getContentPane().add(loadField);

		replayLoad = new JButton("Load Recording");
		replayLoad.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		replayLoad.setBounds(9, 169, 141, 29);
		frame.getContentPane().add(replayLoad);

		replayStart = new JButton("Start Replay");
		replayStart.setEnabled(false);
		replayStart.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		replayStart.setBounds(175, 169, 141, 29);
		frame.getContentPane().add(replayStart);

		replayStop = new JButton("Stop Replay");
		replayStop.setEnabled(false);
		replayStop.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		replayStop.setBounds(344, 169, 141, 29);

		replayLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.loadRecording(loadField.getText());
				replayStart.setEnabled(true);
				replayStop.setEnabled(false);
			}
		});

		replayStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.startReplay();
				replayStart.setEnabled(false);
				replayStop.setEnabled(true);
			}
		});

		replayStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.stopReplay();
				replayStart.setEnabled(false);
				replayStop.setEnabled(false);
			}
		});
		frame.getContentPane().add(replayStop);
		
		JSlider slider = new JSlider();
		slider.setMaximum(1000);
		slider.setValue(0);
		slider.setBounds(67, 208, 380, 22);
		frame.getContentPane().add(slider);
		
		nowTimeLabel = new JLabel("00:00:00");
		nowTimeLabel.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		nowTimeLabel.setBounds(9, 208, 50, 22);
		frame.getContentPane().add(nowTimeLabel);
		
		maxTimeLabel = new JLabel("--:--:--");
		maxTimeLabel.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		maxTimeLabel.setBounds(451, 208, 50, 22);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				double pa = slider.getValue() / 1000.0D;
				long nowTime = (long) (Main.totalTime * pa);
				nowTimeLabel.setText(new SimpleDateFormat("HH:mm:ss").format(nowTime + 16L * 60 * 60 * 1000L));
			}
		});
		frame.getContentPane().add(maxTimeLabel);
		frame.setVisible(true);
	}
}
