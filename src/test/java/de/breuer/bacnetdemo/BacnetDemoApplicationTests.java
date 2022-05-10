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



    @Test
    void testSetup() throws Exception {
//        bacnetService.setUp();
        IpNetworkUtils.getLocalInterfaceAddresses();
    }

    @Test
    void contextLoads() throws Exception {
        var result = BacnetService.send(BacnetService.setUp(),
                new CreateObjectRequest(ObjectType.analogInput, new SequenceOf<>(BacnetService.generateValues()))
        );
        System.out.println("some result: " + result);
    }

}
