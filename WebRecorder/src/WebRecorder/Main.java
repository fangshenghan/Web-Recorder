package WebRecorder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Main {
	
	public static WebDriver driver;
	public static ExecutorService es = Executors.newCachedThreadPool();
	
	public static List<ChatChange> changes = new ArrayList<ChatChange>();
	public static boolean isRecording = false;
	public static long recordingStartTime = System.currentTimeMillis();
	
	public static void main(String[] args) {
        Window.initializeWindow();
    }
	
	public static void startRecording() {
		Main.changes.clear();
		Main.recordingStartTime = System.currentTimeMillis();
		Main.isRecording = true;
	}
	
	public static void stopRecording() {
		String s = "";
		for(ChatChange cc : changes) {
			s += cc.recordingTime + "[CHANGE_SPLITTER]" + cc.changes + "[DATA_SPLITTER]";
		}
		Utils.write(s, Window.folderField.getText() + "\\" + 
				new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(System.currentTimeMillis()) + ".webrecording");
		Main.isRecording = false;
		JOptionPane.showMessageDialog(null, "Recording Saved!");
	}
	
	public static void openWebpage(String address) {
		es.submit(new Runnable() {
			@Override
			public void run() {
				if(driver != null) {
					driver.quit();
				}
				driver = new ChromeDriver();
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

    public static String findChanges(String oldChat, String newChat) {
        Document oldDoc = Jsoup.parse(oldChat);
        Document newDoc = Jsoup.parse(newChat);
        
        String added = "", removed = "";
        HashMap<String, String> map = new HashMap<>();
        
        for(Element e : oldDoc.selectFirst("body").children()) {
        	map.put(e.attr("data-timestamp"), e.html());
        }
        
        for(Element e : newDoc.selectFirst("body").children()) {
        	String timestamp = e.attr("data-timestamp");
        	if(map.containsKey(timestamp)) {
        		map.remove(timestamp);
        	}else {
        		added += e.html() + "\n";
        	}
        }
        
        for(String html : map.values()) {
        	removed += html + "\n";
        }

        return added + "[CHANGE_SPLITTER]" + removed;
    }
    
    public static List<ChatChange> replayChanges = new ArrayList<>();
    public static long replayStartTime = 0;
    public static boolean isReplaying = false;
    public static long totalTime = 0;
    
    public static void loadRecording(String path) {
    	String content = Utils.read(path);
    	for(String change : content.split("[DATA_SPLITTER]")) {
    		replayChanges.add(new ChatChange(change.split("[CHANGE_SPLITTER]")));
    	}
    	totalTime = replayChanges.get(replayChanges.size() - 1).recordingTime;
    }
    
    public static void startReplay() {
    	replayStartTime = System.currentTimeMillis();
    	isReplaying = true;
    }
    
    public static void stopReplay() {
    	
    }
	
}
