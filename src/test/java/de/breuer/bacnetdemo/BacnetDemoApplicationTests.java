package de.breuer.bacnetdemo;

import com.serotonin.bacnet4j.npdu.ip.IpNetworkUtils;
import com.serotonin.bacnet4j.service.confirmed.CreateObjectRequest;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.util.sero.IpAddressUtils;
import de.breuer.bacnetdemo.service.BacnetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BacnetDemoApplicationTests {

    @Autowired
    private BacnetService bacnetService;

    @Test
    void testSetup() throws Exception {
//        bacnetService.setUp();
        IpNetworkUtils.getLocalInterfaceAddresses();
    }

    @Test
    void contextLoads() throws Exception {
        var result = bacnetService.send(bacnetService.setUp(),
                new CreateObjectRequest(ObjectType.analogInput, new SequenceOf<>(bacnetService.generateValues()))
        );
        System.out.println("some result: " + result);
    }

}
