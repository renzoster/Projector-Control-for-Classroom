/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.Closeable;
import java.io.IOException;

public final class StreamUtilities {
	public static void tryCloseStream(Closeable obj) {
		if (obj != null) {
			try {
				obj.close();
			} catch (IOException e) {
			}
		}
	}

	StreamUtilities() {
	}
}
