package genlab.core.commons;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * A printstream writing nowhere.
 * 
 * @author Samuel Thiriot
 *
 */
public class NullPrintStream extends PrintStream {

  public NullPrintStream() {
    super(new NullByteArrayOutputStream());
  }

  private static class NullByteArrayOutputStream extends ByteArrayOutputStream {

    @Override
    public void write(int b) {
      // do nothing
    }

    @Override
    public void write(byte[] b, int off, int len) {
      // do nothing
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
      // do nothing
    }

  }

}