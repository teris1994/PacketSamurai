@echo off
start javaw -Xms2048m -Xmx2048m -cp ./libs/*;packetsamurai.jar com.aionlightning.packetsamurai.PacketSamurai
exit