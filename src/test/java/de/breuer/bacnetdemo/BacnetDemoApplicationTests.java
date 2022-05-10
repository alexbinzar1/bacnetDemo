package de.breuer.bacnetdemo;

import com.serotonin.bacnet4j.npdu.ip.IpNetworkUtils;
import com.serotonin.bacnet4j.service.confirmed.CreateObjectRequest;
import com.serotonin.bacnet4j.service.unconfirmed.UnconfirmedCovNotificationRequest;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.sero.IpAddressUtils;
import de.breuer.bacnetdemo.service.BacnetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BacnetDemoApplicationTests {



    @Test
    void testSetup() throws Exception {
//        bacnetService.setUp();
        IpNetworkUtils.getLocalInterfaceAddresses();
    }

    @Test
    void contextLoads() throws Exception {
        var result = BacnetService.send(BacnetService.setUp(),
                new UnconfirmedCovNotificationRequest(new UnsignedInteger(18), new ObjectIdentifier(ObjectType.device, 4), new ObjectIdentifier(
                        ObjectType.analogInput, 10), new UnsignedInteger(0), new SequenceOf<>(BacnetService.generateValues()))
        );
        //ObjectType.analogInput, )
        System.out.println("some result: " + result);
    }

}
