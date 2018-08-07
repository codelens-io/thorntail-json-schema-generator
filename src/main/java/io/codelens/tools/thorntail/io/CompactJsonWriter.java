package io.codelens.tools.thorntail.io;

import java.io.IOException;
import java.io.Writer;

public class CompactJsonWriter extends Writer {
    
    private static final int BUFFER_SIZE = 16384;
    
    private Writer out;
    private StringBuilder sb = new StringBuilder();
    
    public CompactJsonWriter(Writer writer) {
        super();
        this.out = writer;
    }
    
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if ((off < 0) || (off > cbuf.length) || (len < 0) ||
                ((off + len) > cbuf.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        
        sb.append(cbuf, off, len);
        
        if (sb.length() >= BUFFER_SIZE) {
            synchronized (lock) {
                if (sb.length() >= BUFFER_SIZE) {
                    flushBuffer(false);
                }
            }
        }
    }
    
    private void writeLine(String line) throws IOException {
        char[] lineChars = (line.trim() + "\n").toCharArray();
        out.write(lineChars, 0, lineChars.length);
    }
    
    private void flushBuffer(boolean finalFlush) throws IOException {
        String buffer = sb.toString();
        if (buffer.isEmpty()) {
            return;
        }
        
        String[] lines = buffer.split("\n");
        
        if (lines.length > 1) {
            for (int i = 0; i < lines.length - 1; i++) {
                writeLine(lines[i]);
            }
        }

        if (finalFlush) {
            sb.setLength(0);
            writeLine(lines[lines.length - 1]);
        } else {
            sb.setLength(0);
            sb.append(lines[lines.length - 1]);
        }
    }

    @Override
    public void flush() throws IOException {
        flushBuffer(true);
        out.flush();
    }

    @Override
    public void close() throws IOException {
        flush();
        out.close();
    }
    
}
