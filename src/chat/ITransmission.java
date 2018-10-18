/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.Closeable;
import java.io.IOException;

public interface ITransmission extends Closeable {
	void sendBytes(byte[] in) throws IOException;
	int receiveBytes(byte[] out, int offset, int length) throws IOException;
	boolean ready() throws IOException;
}
