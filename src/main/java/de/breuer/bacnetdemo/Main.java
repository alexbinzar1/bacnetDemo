package de.breuer.bacnetdemo;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.RemoteObject;
import com.serotonin.bacnet4j.ServiceFuture;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.ErrorAPDUException;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.service.acknowledgement.ReadPropertyAck;
import com.serotonin.bacnet4j.service.acknowledgement.ReadPropertyMultipleAck;
import com.serotonin.bacnet4j.service.confirmed.CreateObjectRequest;
import com.serotonin.bacnet4j.service.confirmed.ReadPropertyMultipleRequest;
import com.serotonin.bacnet4j.service.confirmed.ReadPropertyRequest;
import com.serotonin.bacnet4j.service.confirmed.WritePropertyRequest;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.constructed.*;
import com.serotonin.bacnet4j.type.enumerated.EngineeringUnits;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.enumerated.Segmentation;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.util.DiscoveryUtils;
import de.breuer.bacnetdemo.service.BacnetService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        IpNetwork network = new IpNetworkBuilder().withBroadcast("192.168.0.138", 1883).build();

        Transport transport = new DefaultTransport(network);
        System.out.println("inside main...." + transport);
        transport.setTimeout(500);
        transport.setSegTimeout(150);
        final LocalDevice localDevice = new LocalDevice(1, transport);
        System.out.println("inside main...." + localDevice);


        ObjectIdentifier oid = new ObjectIdentifier(ObjectType.analogInput, 1);
        BACnetObject bo = new BACnetObject(localDevice, oid);
        bo.writeProperty(new ValueSource(), new PropertyValue(PropertyIdentifier.objectName, new CharacterString("SENSOR_FROM_SIM")));
        bo.writeProperty(new ValueSource(), new PropertyValue(PropertyIdentifier.description, new CharacterString("SIM_DESCRIPTION")));
        bo.writeProperty(new ValueSource(), new PropertyValue(PropertyIdentifier.units, EngineeringUnits.degreesCelsius));
        localDevice.addObject(bo);

        localDevice.getEventHandler().addListener(new DeviceEventAdapter() {
            @Override
            public void iAmReceived(RemoteDevice device) {
                System.out.println("inside I am received...");
                System.out.println("Discovered device " + device);
//                localDevice.addRemoteDevice(device);

                final RemoteDevice remoteDevice = device;
//                        (RemoteDevice) localDevice
//                        .getRemoteDevice(device.getInstanceNumber());

//                remoteDevice
//                        .setSegmentationSupported(Segmentation.segmentedBoth);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            try {
                                DiscoveryUtils.getExtendedDeviceInformation(
                                        localDevice, remoteDevice);
                            } catch (BACnetException e) {
                                e.printStackTrace();
                            }
                            System.out.println(remoteDevice.getName() + " "
                                    + remoteDevice.getVendorName() + " "
                                    + remoteDevice.getModelName() + " "
                                    + remoteDevice.getAddress() + " "
//                                    + remoteDevice.getProtocolRevision() + " "
//                                    + remoteDevice.getProtocolVersion()
                            );

                            ReadPropertyAck ack = localDevice.send(
                                            remoteDevice,
                                            new ReadPropertyRequest(remoteDevice
                                                    .getObjectIdentifier(),
                                                    PropertyIdentifier.objectList))
                                    .get();
                            SequenceOf<ObjectIdentifier> value = ack.getValue();

                            for (ObjectIdentifier id : value) {

                                List<ReadAccessSpecification> specs = new ArrayList<ReadAccessSpecification>();
                                specs.add(new ReadAccessSpecification(id,
                                        PropertyIdentifier.presentValue));
                                specs.add(new ReadAccessSpecification(id,
                                        PropertyIdentifier.units));
                                specs.add(new ReadAccessSpecification(id,
                                        PropertyIdentifier.objectName));
                                specs.add(new ReadAccessSpecification(id,
                                        PropertyIdentifier.description));
                                specs.add(new ReadAccessSpecification(id,
                                        PropertyIdentifier.objectType));
                                ReadPropertyMultipleRequest multipleRequest = new ReadPropertyMultipleRequest(
                                        new SequenceOf<ReadAccessSpecification>(
                                                specs));

                                ReadPropertyMultipleAck send = localDevice
                                        .send(remoteDevice, multipleRequest)
                                        .get();
                                SequenceOf<ReadAccessResult> readAccessResults = send
                                        .getListOfReadAccessResults();

                                System.out.print(id.getInstanceNumber() + " "
                                        + id.getObjectType() + ", ");
                                for (ReadAccessResult result : readAccessResults) {
                                    for (ReadAccessResult.Result r : result
                                            .getListOfResults()) {
                                        System.out.print(r.getReadResult()
                                                + ", ");
                                    }
                                }
                                System.out.println();
                            }

                            ObjectIdentifier mode = new ObjectIdentifier(
                                    ObjectType.analogValue, 11);

                            ServiceFuture send = localDevice.send(remoteDevice,
                                    new WritePropertyRequest(mode,
                                            PropertyIdentifier.presentValue,
                                            null, new Real(2), null));
                            System.out.println(send.getClass());
                            System.out.println(send.get().getClass());

                        } catch (ErrorAPDUException e) {
                            System.out.println("Could not read value "
                                    + e.getApdu().getError() + " " + e);
                        } catch (BACnetException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }

            @Override
            public void iHaveReceived(RemoteDevice device, RemoteObject object) {
                System.out.println("Value reported " + device + " " + object);
            }
        });

        localDevice.initialize();
        localDevice.sendGlobalBroadcast(new WhoIsRequest());

        List<RemoteDevice> remoteDevices = localDevice.getRemoteDevices();
        for (RemoteDevice device : remoteDevices) {
            System.out.println("Remote dev " + device);
        }

        System.in.read();
        localDevice.terminate();
    }

}
