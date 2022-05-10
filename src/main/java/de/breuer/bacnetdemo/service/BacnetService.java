package de.breuer.bacnetdemo.service;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.ServiceFuture;
import com.serotonin.bacnet4j.exception.BACnetServiceException;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.service.confirmed.ConfirmedRequestService;
import com.serotonin.bacnet4j.service.unconfirmed.UnconfirmedRequestService;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.constructed.PropertyValue;
import com.serotonin.bacnet4j.type.constructed.ValueSource;
import com.serotonin.bacnet4j.type.enumerated.EngineeringUnits;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.Real;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class BacnetService {

    public static LocalDevice setUp() throws Exception {
        IpNetwork network = new IpNetworkBuilder().withBroadcast("192.168.0.138", 1883).build();
        Transport transport = new DefaultTransport(network);
        transport.setTimeout(500);
        transport.setSegTimeout(150);
        var localDevice = new LocalDevice(1, transport);
        IntStream.range(0,100)
                .forEach(i -> {
                    ObjectIdentifier oid = new ObjectIdentifier(ObjectType.analogInput, i);
                    BACnetObject bo = new BACnetObject(localDevice, oid);
                    try {
                        bo.writeProperty(new ValueSource(), new PropertyValue(PropertyIdentifier.objectName, new CharacterString("SENSOR_FROM_SIM"+i)));
                        bo.writeProperty(new ValueSource(), new PropertyValue(PropertyIdentifier.description, new CharacterString("SIM_DESCRIPTION"+i)));
                        bo.writeProperty(new ValueSource(), new PropertyValue(PropertyIdentifier.units, EngineeringUnits.degreesCelsius));
                        localDevice.addObject(bo);
                    } catch (BACnetServiceException e) {
                        throw new RuntimeException(e);
                    }
                });
        return localDevice;
    }

    public static List<PropertyValue> generateValues() {
        return List.of(
                new PropertyValue(PropertyIdentifier.presentValue, new Real((float) (Math.random()*100)))
//                new PropertyValue(PropertyIdentifier.objectIdentifier, new Real(1)),
//                new PropertyValue(PropertyIdentifier.objectName, new CharacterString("SENSOR_FROM_SIM")),
//                new PropertyValue(PropertyIdentifier.description, new CharacterString("SIM_DESCRIPTION"))
        );
    }

    public static void send(LocalDevice d, UnconfirmedRequestService s) throws Exception {
          d.send(d.getLocalBroadcastAddress(),s);
    }
}
