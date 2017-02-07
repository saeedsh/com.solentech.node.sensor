package com.solentech.node.sensor;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by saeed on 12/1/16.
 */
public class Simulator {

    private int _port;
    private DatagramSocket _server_socket;


    public Simulator(int port) throws UnknownHostException, SocketException {
        if(port < 1024)
            throw new UnknownHostException("Port number should be an integer bigger than 1024");
        _port = port;

        _server_socket = new DatagramSocket(_port);
    }

    public void start() throws IOException {
        byte[] receiveData = new byte[1024];
        byte[] sendData;
        System.out.println(String.format("Server started on port %d ...", _port));

        String[] sample_epc = {
                "e20068060000000000000000",
                "300833b2ddd9014000000000",
                "0000ad000013062413031101",
                "e28068100000003900c7021f",
                "0000ad000013070122290611",
                "300833b2ddd9048035050000",
                "e28068100000003900c7021f",
                "000000000000000000000012",
                "e2806e8f000000390059e80e"};

        while(true)
        {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            _server_socket.receive(receivePacket);
            byte[] data_bytes = receivePacket.getData();

            String data = new String( data_bytes);

            //Pars data
            int trid = 0;
            String[] words = data.split("\\?");
            for (String w: words) {
                String[] values = w.split("=");
                for (int i = 0; i < values.length; i++) {
                    String v = (values[i].toLowerCase().trim());
                    if(v.compareTo("trid") == 0)
                    {
                        v = (values[i+1].toLowerCase().trim());
                        trid = Integer.parseUnsignedInt(v,16);
                    }
                }
            }

            //Generate Sensor info
            Random rand = new Random();

            int sleep_time = rand.nextInt(300) + 100;
            try {
                Thread.sleep(sleep_time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            ArrayList<Tag> tag_list = new ArrayList<Tag>();
            int tag_count = rand.nextInt(10);
            for (int i = 0; i < tag_count; i++) {
                Tag t = new Tag();
                t.setEpc(DatatypeConverter.parseHexBinary(sample_epc[rand.nextInt(sample_epc.length)]));
                t.setPc((short) 0x3000);
                t.setRssi((byte)rand.nextInt(256));
                t.setCount(0);
                t.setRn16((short) 0);
                t.setEpclen((byte) t.getEpc().length);
                t.setHandle((short) 0);

                tag_list.add(t);
            }
            sendData = generateResult(trid, tag_list);


            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            DatagramPacket sendPacket =
                    new DatagramPacket(sendData, sendData.length, IPAddress, port);
            _server_socket.send(sendPacket);
        }
    }


    private byte[] generateResult(int trid, ArrayList<Tag> tag_list) throws IOException {
        short cmd = 0;

        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        // Send
        // 0x023b720b --> 0xFDC48DF4 OR 37450251 --> -37450252
        trid = trid ^ 0xFFFFFFFF;
        buff.write(ByteBuffer.allocate(4).putInt(trid).array());
        buff.write(ByteBuffer.allocate(2).putShort(cmd).array());

        //Data size
        int size_offset = buff.size();
        buff.write(ByteBuffer.allocate(2).putShort((short)0xFFFF).array());

        //Fill Records
        for (Tag t: tag_list) {
            buff.write(ByteBuffer.allocate(2).putShort(t.getRn16()).array());
            buff.write(ByteBuffer.allocate(2).putShort(t.getPc()).array());
            buff.write(ByteBuffer.allocate(SensorAPI.EPC_MAX_LENGTH).put(t.getEpc()).array());
            buff.write((byte)t.getEpc().length);
            buff.write(ByteBuffer.allocate(2).putShort(t.getHandle()).array());
            buff.write(t.getRssi());
            buff.write(ByteBuffer.allocate(4).putInt(t.getCount()).array());
        }

        byte[] result = buff.toByteArray();
        short data_size = (short)(buff.size() - size_offset);
        result[size_offset] = (byte)((data_size >> 8) & 0xff);
        result[size_offset+1] = (byte)(data_size & 0xff);
        return result;
    }

    public static void main(String[] args) {
        try {
            Simulator ss = new Simulator(8080);
            ss.start();
        } catch (Exception e) {
            System.err.println("Error in simulation process");
            e.printStackTrace();
        }
    }
}
