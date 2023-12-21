package WebRecorder;

public class ChatChange {

	public long recordingTime;
	public String changes;
	
	public ChatChange(long recordStartTime, String changes) {
		this.changes = changes;
		this.recordingTime = System.currentTimeMillis() - recordStartTime;
	}
	
	public ChatChange(String[] split) {
		this.recordingTime = Long.parseLong(split[0]);
		this.changes = split[1] + "[CHANGE_SPLITTER]" + split[2];
	}
	
}
