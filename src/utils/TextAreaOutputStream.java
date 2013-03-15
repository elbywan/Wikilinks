package utils;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.swing.JTextArea;

public class TextAreaOutputStream extends OutputStream {
	
	JTextArea textArea;
	ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	public TextAreaOutputStream(JTextArea textArea) {
		this.textArea = textArea;
	}

	public void flush() throws UnsupportedEncodingException{
		textArea.append(buffer.toString("UTF-8"));
		buffer.reset();
	}
	public void write(int b) {
		buffer.write(b);
		if((char)b == '\n')
			try {
				flush();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
	}

}
