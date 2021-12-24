package com.l2j.packetsamurai.session;

import java.nio.BufferUnderflowException;


import com.l2j.packetsamurai.PacketSamurai;
import com.l2j.packetsamurai.parser.DataStructure;
import com.l2j.packetsamurai.protocol.PacketId;
import com.l2j.packetsamurai.protocol.Protocol;
import com.l2j.packetsamurai.protocol.protocoltree.PacketFormat;
import com.l2j.packetsamurai.protocol.protocoltree.PacketFamilly.PacketDirection;


/**
 * 
 * @author Gilles Duboscq
 *
 */
public class DataPacket extends DataStructure
{
    private PacketDirection _direction;
    private Protocol _protocol;
    private long _timeStamp;
    private PacketFormat _packetFormat;
    private PacketId _packetID;
    private byte[] _IdData;
    private int _size;
    
    public DataPacket(byte[] data, PacketDirection dir, long timeStamp, Protocol proto)
    {
        this(data,dir,timeStamp,proto,true);
    }
    
    public DataPacket(byte[] data, PacketDirection direction, long timeStamp, Protocol protocol, boolean parse)
    {
        super(data,null);
        _direction = direction;
        _protocol = protocol;
        _timeStamp = timeStamp;
        _packetID = new PacketId();
        _packetFormat = _protocol.getFormat(this,_packetID);
        if (_packetFormat == null)
        {
            this.getByteBuffer().rewind();
            _packetID = null;
        }
        _size = data.length;
        _IdData = new byte[this.getByteBuffer().position()];
        System.arraycopy(data, 0, _IdData, 0, this.getByteBuffer().position());
        this.getByteBuffer().compact();
        this.getByteBuffer().flip();
        if (_packetFormat != null)
        {
            this.setFormat(_packetFormat.getDataFormat());
        }
        if (parse)
        {
            try
            {
                this.parse();
                if (this.getProtocol() != null && this.getProtocol().isStrictLength() && this.getUnparsedData().length > 0)
                {
                    _warning = "Incomplete Format";
                }
                else if (this.getFormat() == null)
                {
                    _warning = "Missing Format";
                }
            }
            catch (BufferUnderflowException e)
            {
                _error = "Insuficient data for the specified format";
                if(PacketSamurai.VERBOSITY.ordinal() >= PacketSamurai.VerboseLevel.VERBOSE.ordinal())
                {
                    if(this.getFormat() != null)
                        System.out.println(this.getFormat().toString());
                    dumpParts();
                }
                PacketSamurai.getUserInterface().log("ERROR: Parsing packet ("+this.getName()+"), insuficient data for the specified format. Please verify the format."); 
            }
        }
    }
    
    public Protocol getProtocol()
    {
        return _protocol;
    }
    
    public boolean fromServer()
    {
        return (_direction == PacketDirection.serverPacket);
    }
    
    public long getTimeStamp()
    {
        return _timeStamp;
    }

    public PacketDirection getDirection()
    {
        return _direction;
    }
    
    public PacketFormat getPacketFormat()
    {
        return _packetFormat;
    }
    
    public String getName()
    {
        if (this.getPacketFormat() == null)
        {
            return null;
        }
        return this.getPacketFormat().getName();
    }
    
    public PacketId getPacketId()
    {
        return _packetID;
    }
    
    public int getRawSize()
    {
        return _size;
    }
    
    public byte[] getIdData()
    {
        return _IdData;
    }
}