package com.solentech.node.sensor;

/**
 * Created by Saeed Shariati on 12/1/16.
 */

import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

public class SensorAPI {

    public static int UDP_PORT_COMMAND = 8080;
    public static int EPC_MAX_LENGTH = 32;
    public static int SOCKET_TIMEOUT_MS = 2000;

    InetAddress _address;
    DatagramSocket _socket;

    public SensorAPI(String address) throws UnknownHostException, SocketException {
        _address = InetAddress.getByName(address);
        _socket = new DatagramSocket();
        _socket.setSoTimeout(SOCKET_TIMEOUT_MS);
    }

    public ArrayList<Tag> ReadTags() throws Exception {
        ArrayList<Tag> result = new ArrayList<Tag>();

        Random rand = new Random();
        int trID = rand.nextInt();

        byte[] sendData = generateMessage(trID);
        byte[] receiveData = new byte[1024];

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, _address, UDP_PORT_COMMAND);
        _socket.send(sendPacket);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try {
            _socket.receive(receivePacket);
        }
        catch(SocketTimeoutException e)
        {
            throw e;
        }
        byte[] data_byte = receivePacket.getData();

        //Check the TRID to be the same as original one
        int trID_back = ByteBuffer.wrap(data_byte, 0, 4).getInt();
        trID_back = trID_back ^ 0xFFFFFFFF;
        if(trID_back != trID)
            throw new Exception("trID is not compatible");

        // Check return value
        if(ByteBuffer.wrap(data_byte,4,2).getShort() < 0)
            return result;


        int HEADER_SIZE = 8;
        //Fill observed data_byte
        int data_size = ByteBuffer.wrap(data_byte,6,2).getShort();

        for (int i = HEADER_SIZE; (i < data_size ) && ( i <= data_size - 44); )
        {
            Tag t = new Tag();

            t.setRn16(ByteBuffer.wrap(data_byte, i, 2).getShort());
            i += 2;

            t.setPc(ByteBuffer.wrap(data_byte, i, 2).getShort());
            i += 2;

            byte[] epcBuffer = new byte[EPC_MAX_LENGTH];
            System.arraycopy(data_byte, i,epcBuffer, 0 , EPC_MAX_LENGTH);
            i += EPC_MAX_LENGTH;
            t.setEpc(epcBuffer);

            t.setEpclen(data_byte[i++]);

            t.setHandle(ByteBuffer.wrap(data_byte, i, 2).getShort());
            i += 2;

            t.setRssi(data_byte[i++]);

            t.setCount(ByteBuffer.wrap(data_byte, i,4).getInt());
            i += 4;

            result.add(t);
        }

        return result;
    }

    private byte[] generateMessage(int trid)
    {
        String buf = "";

        buf += "?trid=" + String.format("%08x",trid);
        buf += "?reader?command=gen2";
        buf += "?antenna=" + "0";
        buf += "?inventory_fast?";
        return  buf.getBytes();

    }

}
