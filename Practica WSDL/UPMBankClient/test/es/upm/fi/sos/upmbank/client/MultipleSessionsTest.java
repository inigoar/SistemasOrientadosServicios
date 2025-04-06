package es.upm.fi.sos.upmbank.client;

import org.junit.jupiter.api.*;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class MultipleSessionsTest {

    private static final double STARTAMMOUNT = 1000;
    private static final String ADMIN = "admin";
    private static final String USER1 = "AaBbCc01";
    private static final String USER2 = "AaBbCc02";
    private String user1Pwd = "AaBbCc013088";
    private String user2Pwd = "AaBbCc023920";

    @Test
    @DisplayName("Login and create users")
    void sudoLogin() {
        try {
            jUnitClient sudoClient = new jUnitClient(ADMIN, ADMIN);
            assertTrue(sudoClient.login());
            UPMBankWSStub.AddUserResponse response = sudoClient.addUser(USER1);
            if (!response.getResponse()) System.out.println(USER1 + " already registered");
            else user1Pwd = response.getPwd();
            response = sudoClient.addUser(USER2);
            if (!response.getResponse()) System.out.println(USER2 + " already registered");
            else user2Pwd = response.getPwd();
            sudoClient.logout();
        } catch (RemoteException axisFault) {
            axisFault.printStackTrace();
            fail("Exception thrown");
        }
    }

    @Test
    @DisplayName("Users test")
    void testUser() {
        try {
            jUnitClient user1 = new jUnitClient(USER1, user1Pwd);
            jUnitClient user2 = new jUnitClient(USER2, user2Pwd);
            assertTrue(user1.login());
            assertTrue(user2.login());

            assertTrue(user1.addBankAcc(STARTAMMOUNT).getResult());
            assertTrue(user2.addBankAcc(STARTAMMOUNT / 2).getResult());

            UPMBankWSStub.AddMovementResponse response1 = user1.addMovement(user1.getIBAN(), 2000);
            assertTrue(response1.getResult());
            assertEquals(response1.getBalance(), STARTAMMOUNT + 2000);

            UPMBankWSStub.AddMovementResponse response2 = user2.addWithdrawal(user2.getIBAN(), 500);
            assertTrue(response2.getResult());
            assertEquals(response2.getBalance(), (STARTAMMOUNT / 2) - 500);

            double ref1 = user1.addMovement(user1.getIBAN(), 0).getBalance();
            double ref2 = user2.addMovement(user2.getIBAN(), 0).getBalance();
            double bal = ref1;
            for (int i = 0; i <= 10; i++) {
                UPMBankWSStub.AddMovementResponse loop = user1.addMovement(user1.getIBAN(), 100);
                assertTrue(loop.getResult());
                ref1 += 100;
                bal = loop.getBalance();
            }
            assertEquals(ref1, bal);

            assertEquals(ref2, user2.addMovement(user2.getIBAN(), 0).getBalance());

            ref1 = user1.addMovement(user1.getIBAN(), 0).getBalance();
            user1.logout();
            assertTrue(user1.login());
            assertEquals(ref1, user1.addMovement(user1.getIBAN(), 0).getBalance());

            assertTrue(user1.changePassword("1111", user1Pwd).getResponse());
            user1.logout();
            user1.setPassword("1111");
            assertTrue(user1.login());
            assertTrue(user1.changePassword(user1Pwd, "1111").getResponse());
            user1.setPassword(user1Pwd);
            double[] res = user1.getMyMovements().getMovementQuantities();
            for (int i = 0; i < res.length; i++) {
                System.out.println(res[i]);
            }
            System.out.println("////");
            double[] res1 = user2.getMyMovements().getMovementQuantities();
            for (int i = 0; i < res1.length; i++) {
                System.out.println(res1[i]);
            }

        } catch (RemoteException axisFault) {
            axisFault.printStackTrace();
            fail("Exception thrown");
        }
    }
}
