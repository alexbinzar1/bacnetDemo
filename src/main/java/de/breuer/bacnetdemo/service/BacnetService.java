package de.breuer.bacnetdemo.service;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.ServiceFuture;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.service.confirmed.ConfirmedRequestService;
import com.serotonin.bacnet4j.service.unconfirmed.UnconfirmedRequestService;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.constructed.PropertyValue;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.Real;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BacnetService {

    public static LocalDevice setUp() throws Exception {
        IpNetwork network = new IpNetworkBuilder().withBroadcast("192.168.0.138", 1883).build();

        Transport transport = new DefaultTransport(network);
        System.out.println("inside main...." + transport);
        transport.setTimeout(500);
        transport.setSegTimeout(150);
        return new LocalDevice(1, transport);
    }

    public static List<PropertyValue> generateValues() {
        return List.of(
                new PropertyValue(PropertyIdentifier.presentValue, new Real(12)),
                new PropertyValue(PropertyIdentifier.objectIdentifier, new Real(1))
        );
    }

    public static void send(LocalDevice d, UnconfirmedRequestService s) throws Exception {
//        String macAddress = "a4:83:e7:51:0f:ae"
//        String[] macAddressParts = macAddress.split(":");
//
//        // convert hex string to byte values
//        byte[] macAddressBytes = new byte[6];
//        for(int i=0; i<6; i++){
//            Integer hex = Integer.parseInt(macAddressParts[i], 16);
//            macAddressBytes[i] = hex.byteValue();
//        }
//        RemoteDevice remoteDevice = d.getRemoteDevice(1).get();
          d.send(d.getLocalBroadcastAddress(),s);
//        return d.send(new Address(macAddressBytes),s).get();
//        Address a = new Address(InetAddrCache.get("localhost", 1956));
//        return d.send(a, null, MaxApduLength.UP_TO_50, Segmentation.segmentedBoth, s);
//        return null; //TODO implement
    }
}
