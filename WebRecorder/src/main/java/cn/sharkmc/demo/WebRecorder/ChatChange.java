package cn.sharkmc.demo.WebRecorder;

public class ChatChange {

	public long recordingTime;
	public String changes;
	
	public ChatChange(long recordStartTime, String changes) {
		this.changes = changes;
		this.recordingTime = System.currentTimeMillis() - recordStartTime;
	}

	public long replayingTime;
	public String added, removed;

	public ChatChange(String[] split) {
		String removed = "";
		if(split.length == 3){
			removed = split[2];
		}
		this.replayingTime = Long.parseLong(split[0]);
		this.added = split[1];
		this.removed = removed;
	}
	
}
