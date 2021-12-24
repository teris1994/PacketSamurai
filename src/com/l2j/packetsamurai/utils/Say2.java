package com.l2j.packetsamurai.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.l2j.packetsamurai.PacketSamurai;
import com.l2j.packetsamurai.parser.datatree.ValuePart;
import com.l2j.packetsamurai.session.DataPacket;
import com.sun.org.apache.xpath.internal.XPathAPI;

import javolution.util.FastList;

public class Say2 {
	private FastList<DataPacket> packets;
	private FastList<Chat> chats;
	private String sessionName;
		
	private int msgId = -1;
	public Say2(List<DataPacket> packets, String sessionName) {
		this.packets = new FastList<DataPacket>(packets);
		this.sessionName = sessionName;
		this.chats = new FastList<Chat>();
	}
	public void parse() {
		Long start = System.currentTimeMillis();

		try {
			
			// Collect info about all seen NPCs
			for (DataPacket packet : packets) {
				String packetName = packet.getName();
				//if ("SM_PLAYER_SPAWN".equals(packetName))
				//	this.worldId = Integer.parseInt(packet.getValuePartList().get(1).readValue());
				if ("SM_SAY2".equals(packetName)) {
					Chat chat = new Chat();
					
					FastList<ValuePart> valuePartList = new FastList<ValuePart>(packet.getValuePartList());
					for (ValuePart valuePart : valuePartList) {
						String partName = valuePart.getModelPart().getName();
						if ("ChatType".equals(partName))
							chat.chatType = Integer.parseInt(valuePart.readValue());
						else if ("ClientId".equals(partName))
							chat.clientId = Integer.parseInt(valuePart.readValue());
						else if ("Name".equals(partName))
							chat.name = valuePart.readValue().toString();
						else if ("msgId".equals(partName))
							chat.msgId = Integer.parseInt(valuePart.readValue());
						else if ("text".equals(partName))
							chat.text = valuePart.readValue().toString();
							System.out.println(chat.text);
						
						chats.add(chat);
					}
				}
			}
			// Open the file.
	        PrintWriter out = new PrintWriter(sessionName+"_say2.txt"); // Step 2

			for (Chat n : chats) {
	
		        // Write the name of four oceans to the file
		        out.println(n.name+" : "+n.text);

		       
			}
			 // Close the file.
	        out.close();  // Step 4
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PacketSamurai.getUserInterface().log(
				"The Say2 parser has been written in " + ((float) (System.currentTimeMillis() - start) / 1000) + "s");
	}

	class Chat{
		int chatType;
		int clientId;
		String name;
		int msgId;
		String text;
	
	}
}
