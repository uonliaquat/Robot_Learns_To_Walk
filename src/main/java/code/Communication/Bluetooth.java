package code.Communication;

import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.io.OutputStream;

public class Bluetooth {
    private SerialPort[] ports;
    private  SerialPort serialPort;
    private OutputStream outputStream;

    public boolean Connect() {
        ports = SerialPort.getCommPorts();
        int chosenPort = 3;

        serialPort = ports[chosenPort - 1];
        if (serialPort.openPort()) {
            System.out.println("Port opened successfully.");
            return true;
        }
        else {
            System.out.println("Unable to open the port.");
            return false;
        }

    }

    public void sendCommand(int value){
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        byte[] bytes = toBytes(value);
        outputStream = serialPort.getOutputStream();
        try {
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    byte[] toBytes(int i)
    {
        byte[] result = new byte[4];

        result[0] = (byte) (i >> 24);
        result[1] = (byte) (i >> 16);
        result[2] = (byte) (i >> 8);
        result[3] = (byte) (i /*>> 0*/);

        return result;
    }
}
