package WebRecorder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Utils {

	public static boolean write(String cont, String path) {
		try {
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(new File(path)), "UTF-8");
			BufferedWriter writer = new BufferedWriter(osw);
			writer.write(cont);
			writer.flush();
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static String read(String path) {
		File file = new File(path);
		InputStreamReader isr;
		try {
			isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			StringBuffer res = new StringBuffer();
			String line = null;
			try {
				while ((line = br.readLine()) != null) {
					res.append(line + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return res.toString();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return "";
	}
	
}
