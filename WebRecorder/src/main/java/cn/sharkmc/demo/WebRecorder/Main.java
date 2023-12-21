package cn.sharkmc.demo.WebRecorder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import cn.sharkmc.demo.DemoApplication;
import cn.sharkmc.demo.WebSocketController;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Main {
	
	public static WebDriver driver;
	public static ExecutorService es = Executors.newCachedThreadPool();
	
	public static List<ChatChange> changes = new ArrayList<ChatChange>();
	public static boolean isRecording = false;
	public static long recordingStartTime = System.currentTimeMillis();
	
	public static void startRecording() {
		Main.changes.clear();
		Main.recordingStartTime = System.currentTimeMillis();
		Main.isRecording = true;
	}
	
	public static void stopRecording() {
		String s = "";
		try{
			for(ChatChange cc : changes) {
				try{
					s += cc.recordingTime + "<C_S>" + cc.changes + "<D_S>";
				}catch (Exception ex){
					JOptionPane.showMessageDialog(null, "Error: \n" + ex.toString());
				}
			}
		}catch (Exception ex){
			JOptionPane.showMessageDialog(null, "Error: \n" + ex.toString());
		}
		try{
			Utils.write(s, Window.folderField.getText() + "\\" +
					new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(System.currentTimeMillis()) + ".webrecording");
		}catch (Exception ex){
			JOptionPane.showMessageDialog(null, "Error: \n" + ex.toString());
		}
		Main.isRecording = false;
		JOptionPane.showMessageDialog(null, "Recording Saved!");
		Window.recordStart.setEnabled(true);
		Window.recordStop.setEnabled(false);
	}
	
	public static void openWebpage(String address) {
		es.submit(new Runnable() {
			@Override
			public void run() {
				try{
					System.setProperty("webdriver.chrome.driver", DemoApplication.tempFolder + "\\chromedriver.exe");

					if(driver != null) {
						driver.quit();
					}
					ChromeOptions options = new ChromeOptions();
					options.addArguments("--remote-allow-origins=*");
					driver = new ChromeDriver(options);
					driver.get(address);

					String oldChat = "";

					while(true) {
						try {
							Thread.sleep(1000L);
						}catch(Exception ex) {
							ex.printStackTrace();
						}

						String newChat = Main.getChatContent(driver.getPageSource());
						if(oldChat.equals("")) {
							oldChat = newChat;
							JavascriptExecutor js = (JavascriptExecutor) driver;
							js.executeScript("var stylesheetLink = document.createElement(\"link\"); " +
									"stylesheetLink.rel = \"stylesheet\";" +
									"stylesheetLink.href = \"https://fs-im-kefu.7moor-fs1.com/29397395/4d2c3f00-7d4c-11e5-af15-41bf63ae4ea0/1692414067688/theme-1692413770517.css\";" +
									"stylesheetLink.as = \"style\";" +
									"document.head.appendChild(stylesheetLink);");
							continue;
						}

						if(newChat.equals(oldChat)) {
							continue;
						}

						if(isRecording) {
							changes.add(new ChatChange(Main.recordingStartTime, Main.findChanges(oldChat, newChat)));
						}

						oldChat = newChat;
					}
				}catch (Exception ex){
					ex.printStackTrace();
				}

			}
		});
	}
	
	public static String getChatContent(String source) {
        Document doc = Jsoup.parse(source);
        Elements list = doc.getElementsByClass("mantine-ScrollArea-viewport");
        if(list.size() == 0) {
        	return "";
        }
		return list.get(0).children().html();
	}

	public static HashMap<String, Boolean> recordedIDs = new HashMap<>();

    public static String findChanges(String oldChat, String newChat) {
        Document oldDoc = Jsoup.parse(oldChat);
        Document newDoc = Jsoup.parse(newChat);

        String added = "", removed = "";
        HashMap<String, String> map = new HashMap<>();
        
        for(Element e : oldDoc.getElementsByClass("event")) {
        	map.put(e.attr("data-timestamp"), e.outerHtml());
        }
        
        for(Element e : newDoc.getElementsByClass("event")) {
        	String timestamp = e.attr("data-timestamp");
        	if(map.containsKey(timestamp)) {
        		map.remove(timestamp);
        	}else {
				if(recordedIDs.containsKey(timestamp)){
					continue;
				}
				recordedIDs.put(timestamp, true);
        		added += e.outerHtml() + "\n";
        	}
        }
        
        for(String key : map.keySet()) {
        	removed += key + "<R_S>";
        }

        return added + "<C_S>" + removed;
    }

	public static List<ChatChange> replayChanges = new ArrayList<>();
	public static long replayStartTime = 0;
	public static boolean isReplaying = false;
	public static long totalTime = 0, skippedTime = 0;

	public static void loadRecording(String path) {
		String content = Utils.read(path);
		replayChanges.clear();
		for(String change : content.split("<D_S>")) {
			String[] split = change.split("<C_S>");
			if(split.length > 1){
				replayChanges.add(new ChatChange(split));
			}
		}
		totalTime = replayChanges.get(replayChanges.size() - 1).replayingTime;
		System.out.println(totalTime);
		Window.maxTimeLabel.setText(Utils.parseTime(totalTime));
		Window.slider.setEnabled(true);

		WebSocketController.instance.sendMessageToClient("Reload");
	}

	public static void startReplay() {
		replayStartTime = System.currentTimeMillis() - skippedTime;
		isReplaying = true;
		Window.slider.setEnabled(false);
		es.submit(new Runnable() {
			@Override
			public void run() {
				while(isReplaying){
					try {
						Thread.sleep(100L);
					}catch(InterruptedException ex) {
						ex.printStackTrace();
					}
					if(replayChanges.size() == 0){
						stopReplay();
						return;
					}

					long deltaTime = System.currentTimeMillis() - replayStartTime;
					List<ChatChange> toRemove = new ArrayList<>();
					for(ChatChange cc : replayChanges){
						if(deltaTime >= cc.replayingTime){
							toRemove.add(cc);
							WebSocketController.instance.sendMessageToClient("<ADD>" + cc.added);
							WebSocketController.instance.sendMessageToClient("<REMOVE>" + cc.removed);
						}
					}
					replayChanges.removeAll(toRemove);

					double pa = deltaTime / (double) totalTime;
					Window.slider.setValue((int) (pa * 1000));
					Window.nowTimeLabel.setText(Utils.parseTime(deltaTime));
				}
			}
		});
	}

	public static void stopReplay() {
		isReplaying = false;
		Window.replayStart.setEnabled(false);
		Window.replayStop.setEnabled(false);
		Window.slider.setEnabled(false);
	}
	
}
