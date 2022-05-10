package de.breuer.bacnetdemo.service;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.ServiceFuture;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.service.confirmed.ConfirmedRequestService;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.type.constructed.PropertyValue;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.Real;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BacnetService {

    public LocalDevice setUp() throws Exception {
        var localDevice = new LocalDevice(1, new DefaultTransport(new IpNetworkBuilder().withBroadcast("192.168.0.138", 1883).build()));
        localDevice.initialize();
        localDevice.sendGlobalBroadcast(new WhoIsRequest());
        return localDevice;
    }

    public static List<PropertyValue> generateValues() {
        return List.of(new PropertyValue(PropertyIdentifier.objectIdentifier, new Real(1)));
    }

    public static ServiceFuture send(LocalDevice d, ConfirmedRequestService s) throws Exception {
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
        return d.send(d.getLocalBroadcastAddress(),s);
//        return d.send(new Address(macAddressBytes),s).get();
//        Address a = new Address(InetAddrCache.get("localhost", 1956));
//        return d.send(a, null, MaxApduLength.UP_TO_50, Segmentation.segmentedBoth, s);
//        return null; //TODO implement
    }
}
