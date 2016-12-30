package org.openhab.binding.heos.resources;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetInputListener;

public class Telnet {

    private String ip = "";
    private int port = 0;

    private String readResult = "";
    private String readLineResult = "";

    private TelnetClient client = null;
    private DataOutputStream outStream = null;
    private InputStream inputStream = null;
    private BufferedInputStream bufferedStream = null;

    private MyStringPropertyChangeListener eolNotifyer = new MyStringPropertyChangeListener();

    private TelnetInputListener inputListener = null;

    public Telnet() {
        client = new TelnetClient();

    }

    public boolean connect(String ip, int port) {

        this.ip = ip;
        this.port = port;
        return openConnection();
    }

    private boolean openConnection() {

        try {
            client.connect(ip, port);
            // Debug
            // System.out.println(client.isConnected());
            outStream = new DataOutputStream(client.getOutputStream());
            inputStream = client.getInputStream();
            bufferedStream = new BufferedInputStream(inputStream);

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return client.isConnected();

    }

    /*
     * Appends \r\n to the command.
     * For clear send use sendClear
     */

    public void send(String command) {
        sendClear(command + "\r\n");

    }

    /*
     * Send command without additional commands
     */

    public void sendClear(String command) {

        // Debug
        // System.out.println("Send");
        try {
            outStream.writeBytes(command);
            outStream.flush();

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    /*
     * The read function reads the input of the Telnet connection
     * it determine the amount of bytes to read.
     * If no bytes available i is 0, if End of Line is detected i=-1
     * Bytes are read into buffer and changed to String
     * Then the single values are merged by function concatReadResult
     */

    public void read() {

        try {
            // bufferedStream.read();
            int i = 1;
            while (i != -1) {

                i = bufferedStream.available();
                byte[] buffer = new byte[i];
                bufferedStream.read(buffer);
                String str = new String(buffer, "UTF-8");
                i = concatReadResult(str);

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    public String readLine() {

        readLineResult = "";
        try {

            int i = 1;
            while (i != -1) {

                i = bufferedStream.available();
                byte[] buffer = new byte[i];
                bufferedStream.read(buffer);
                String str = new String(buffer, "UTF-8");
                i = concatReadLineResult(str);

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return readLineResult;
    }

    /*
     * Disconnect Telnet and close all Streams
     */

    public void disconnect() {
        // Debug
        // System.out.println("Disconnect");
        try {

            inputStream.close();
            outStream.close();
            client.disconnect();
            // Debug
            // System.out.println(client.isConnected());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    /*
     * Input Listener which fires event if input is detected
     */

    public void startInputListener() {

        inputListener = new TelnetInputListener() {
            @Override
            public void telnetInputAvailable() {
                inputAvailableRead();
            }
        };
        client.registerInputListener(inputListener);

    }

    public void stopInputListener() {

        client.unregisterInputListener();

    }

    /*
     * Reader for InputListenerOnly which only reads the
     * available data without any check
     *
     */

    private void inputAvailableRead() {

        try {

            int i = bufferedStream.available();
            byte[] buffer = new byte[i];
            bufferedStream.read(buffer);
            String str = new String(buffer, "UTF-8");
            i = concatReadResult(str);

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    /*
     * Read values until end of line is reached.
     * Then fires event for change Listener.
     * Returns -1 to indicate that end of line is reached
     * Else returns 0
     */
    private int concatReadResult(String value) {

        readResult = readResult.concat(value);
        if (readResult.contains("\r\n")) {
            eolNotifyer.setValue(readResult.trim());
            readResult = "";
            return -1;
        }
        return 0;
    }

    private int concatReadLineResult(String value) {

        readLineResult = readLineResult.concat(value);
        if (readLineResult.contains("\r\n")) {
            readLineResult = readLineResult.trim();

            return -1;
        }
        return 0;
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    public MyStringPropertyChangeListener getReadResultListener() {
        return eolNotifyer;
    }
}
