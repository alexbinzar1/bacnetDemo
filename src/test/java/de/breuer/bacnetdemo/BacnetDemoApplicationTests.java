package de.breuer.bacnetdemo;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.service.unconfirmed.UnconfirmedCovNotificationRequest;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import de.breuer.bacnetdemo.service.BacnetService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.IntStream;

@SpringBootTest
class BacnetDemoApplicationTests {

    private LocalDevice ld;

    private int SUB_IDENT = 18;

    private int DEVICE = 100;

    private int ANALOG_INPUT = 10;

    private int TIME_REMAINING = 0;

    private long SLEEP_TIME = 50;

    @BeforeAll
    void setUp() throws Exception {
        ld = BacnetService.setUp();
        ld.initialize();
    }

    @Test
    void testSimpleMessage() throws Exception {
        BacnetService.send(ld,
                new UnconfirmedCovNotificationRequest(new UnsignedInteger(SUB_IDENT), new ObjectIdentifier(ObjectType.device, DEVICE), new ObjectIdentifier(
                        ObjectType.analogInput, ANALOG_INPUT), new UnsignedInteger(0), new SequenceOf<>(BacnetService.generateValues()))
        );
    }

    @Test
    void test100Req() {
        IntStream.range(0,100)
                .forEach(i-> {
                    try {
                        BacnetService.send(ld,
                                new UnconfirmedCovNotificationRequest(new UnsignedInteger(SUB_IDENT), new ObjectIdentifier(ObjectType.device, DEVICE), new ObjectIdentifier(
                                        ObjectType.analogInput, ANALOG_INPUT), new UnsignedInteger(TIME_REMAINING), new SequenceOf<>(BacnetService.generateValues()))
                        );
                        Thread.sleep(SLEEP_TIME);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

}
