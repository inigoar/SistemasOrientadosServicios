package es.upm.fi.sos.upmbank.client;

import com.github.javafaker.Faker;
import org.apache.axis2.AxisFault;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class jUnitClientTest {

    private jUnitClient client;
    private static Faker faker;
    private static HashMap<String, String> usedUsers;
    private static final int NUMUSERS = 5;
    private static final String ADMIN = "admin";
    private static final double STARTAMMOUNT = 5000;

    @BeforeAll
    static void loadFaker() {
        faker = new Faker();
        usedUsers = new HashMap<>();
    }

    @BeforeEach
    void loadClient() throws AxisFault {
        client = new jUnitClient();
    }

    @Nested
    @DisplayName("Sudo Actions")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class SudoActions {
        private jUnitClient client;

        @BeforeAll
        void loadClient() throws AxisFault {
            this.client = new jUnitClient();
        }

        @Test
        @DisplayName("SudoLogin")
        @Order(0)
        void sudoLogin() {
            this.client.setUsername(ADMIN);
            this.client.setPassword(ADMIN);
            try {
                assertTrue(client.login());
            } catch (RemoteException e) {
                e.printStackTrace();
                fail("Exception thrown");
            }
        }

        @Test
        @DisplayName("Sudo change password")
        @Order(1)
        void sudoChange() {
            try {
                assertTrue(client.changePassword("1111", "admin").getResponse());
                client.setPassword("1111");
                client.logout();
                assertTrue(client.login());
                assertTrue(client.changePassword("admin", "1111").getResponse());
                client.logout();
                client.setPassword("admin");
                assertTrue(client.login());
            } catch (RemoteException e) {
                e.printStackTrace();
                fail("Exception thrown");
            }
        }

        @ParameterizedTest
        @DisplayName("Sudo Add Users")
        @MethodSource("es.upm.fi.sos.upmbank.client.jUnitClientTest#generateUsername")
        @Order(2)
        void addUsers(String input) {
            try {
                UPMBankWSStub.AddUserResponse response = client.addUser(input);
                assertTrue(response.getResponse());
                usedUsers.put(input, response.getPwd());
            } catch (RemoteException e) {
                e.printStackTrace();
                fail("Exception thrown");
            }
        }

        @ParameterizedTest
        @DisplayName("Remove one user")
        @MethodSource(("es.upm.fi.sos.upmbank.client.jUnitClientTest#randomUser"))
        @Order(3)
        void removeUser(String input) {
            try {
                UPMBankWSStub.Response response = client.removeUser(input);
                assertTrue(response.getResponse());
                usedUsers.remove(input);
            } catch (RemoteException e) {
                e.printStackTrace();
                fail("Exception thrown");
            }
        }

        @ParameterizedTest
        @DisplayName("Add Existing Users")
        @MethodSource(("es.upm.fi.sos.upmbank.client.jUnitClientTest#randomUser"))
        @Order(4)
        void addExstUser(String input) {
            try {
                UPMBankWSStub.AddUserResponse response = client.addUser(input);
                assertFalse(response.getResponse());
            } catch (RemoteException e) {
                e.printStackTrace();
                fail("Exception thrown");
            }
        }

        @Test
        @DisplayName("Remove non-existent user")
        @Order(5)
        void removeNonExtUser() {
            try {
                UPMBankWSStub.Response response = client.removeUser("AaBbCcDd97531");
                assertFalse(response.getResponse());
            } catch (RemoteException e) {
                e.printStackTrace();
                fail("Exception thrown");
            }
        }

        @Test
        @DisplayName("Sudo logout")
        @Order(6)
        void sudoLogout() {
            try {
                client.logout();
            } catch (Exception e) {
                e.printStackTrace();
                fail("Exception thrown");
            }
        }

        @ParameterizedTest
        @MethodSource("es.upm.fi.sos.upmbank.client.jUnitClientTest#generateUsername")
        @DisplayName("Add user without login")
        @Order(7)
        void addUser(String input) {
            try {
                UPMBankWSStub.AddUserResponse response = client.addUser(input);
                assertFalse(response.getResponse());
            } catch (RemoteException e) {
                e.printStackTrace();
                fail("Exception thrown");
            }
        }

        @ParameterizedTest
        @MethodSource("es.upm.fi.sos.upmbank.client.jUnitClientTest#randomUser")
        @DisplayName("Remove user without login")
        @Order(7)
        void removeUserL(String input) {
            try {
                UPMBankWSStub.Response response = client.removeUser(input);
                assertFalse(response.getResponse());
            } catch (RemoteException e) {
                e.printStackTrace();
                fail("Exception thrown");
            }
        }

        @Nested
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DisplayName("Actions for One Registered User")
        class User1 {
            private jUnitClient client;

            private  String username;
            private  String password;

            @BeforeAll
            void loadClient() throws AxisFault {
                this.client = new jUnitClient();
                Object[] keys = usedUsers.keySet().toArray();
                Object key = keys[0];
                username = key.toString();
                password = usedUsers.get(key.toString());
            }

            @Test
            @DisplayName("User Login")
            @Order(0)
            void login() {
                this.client.setUsername(username);
                this.client.setPassword(password);
                try {
                    assertTrue(client.login());
                } catch (RemoteException e) {
                    e.printStackTrace();
                    fail("Exception thrown");
                }
            }

            @Test
            @DisplayName("Attempt to add user")
            @Order(1)
            void addUser() {
                try {
                    UPMBankWSStub.AddUserResponse response = client.addUser("AaBbCcDd8462");
                    assertFalse(response.getResponse());
                } catch (RemoteException e) {
                    e.printStackTrace();
                    fail("Exception thrown");
                }
            }

            @ParameterizedTest
            @DisplayName("Attempt to remove user")
            @MethodSource("es.upm.fi.sos.upmbank.client.jUnitClientTest#randomUser")
            @Order(2)
            void removeUser(String input) {
                try {
                    UPMBankWSStub.Response response = client.removeUser(input);
                    assertFalse(response.getResponse());
                } catch (RemoteException e) {
                    e.printStackTrace();
                    fail("Exception thrown");
                }
            }

            @Test
            @DisplayName("ChangePassword correcto")
            @Order(3)
            void changePassword() {
                String newPwd = faker.regexify("[a-z1-9]{8}");
                String oldPwd = client.getUser().getPwd();
                try {
                    UPMBankWSStub.Response response = client.changePassword(newPwd, oldPwd);
                    assertTrue(response.getResponse());
                } catch (RemoteException e) {
                    e.printStackTrace();
                    fail("Exception thrown");
                }
            }

            @Test
            @DisplayName("ChangePassword incorrecto")
            @Order(4)
            void changePassword1() {
                String newPwd = faker.regexify("[a-z1-9]{8}");
                try {
                    UPMBankWSStub.Response response = client.changePassword(newPwd, newPwd);
                    assertFalse(response.getResponse());
                } catch (RemoteException e) {
                    e.printStackTrace();
                    fail("Exception thrown");
                }
            }

            @Test
            @DisplayName("Add account")
            @Order(5)
            void addAccount() {
                try {
                    UPMBankWSStub.BankAccountResponse response = client.addBankAcc(STARTAMMOUNT);
                    assertTrue(response.getResult());
                } catch (RemoteException e) {
                    e.printStackTrace();
                    fail("Exception thrown");
                }
            }

            @Test
            @DisplayName("Add income")
            @Order(6)
            void addIncome() {
                double qtty = 1000;
                try {
                    UPMBankWSStub.AddMovementResponse response = client.addMovement(client.getIBAN(), qtty);
                    assertTrue(response.getResult());
                    assertEquals(response.getBalance(), STARTAMMOUNT + qtty);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    fail("Exception thrown");
                }
            }

            @Test
            @DisplayName("Add income to wrong account")
            @Order(7)
            void addIncome1() {
                double qtty = 100;
                try {
                    UPMBankWSStub.AddMovementResponse response = client.addMovement("1111", qtty);
                    assertFalse(response.getResult());
                } catch (RemoteException e) {
                    e.printStackTrace();
                    fail("Exception thrown");
                }
            }

            @Test
            @DisplayName("Add witdrawal")
            @Order(7)
            void addWithdrawal() {
                double qtty = 50;
                try {
                    UPMBankWSStub.AddMovementResponse response = client.addWithdrawal(client.getIBAN(), qtty);
                    assertTrue(response.getResult());
                    assertEquals(response.getBalance(), STARTAMMOUNT + 1000 - qtty);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Test
            @DisplayName("Add withdrawal to wrong account")
            @Order(8)
            void addWithdrawal1() {
                double qtty = 100;
                try {
                    UPMBankWSStub.AddMovementResponse response = client.addMovement("1111", qtty);
                    assertFalse(response.getResult());
                } catch (RemoteException e) {
                    e.printStackTrace();
                    fail("Exception thrown");
                }
            }

            @Test
            @DisplayName("Get movements")
            @Order(9)
            void getMovements() {
                double[] res = {0,0,0,0,0,0,0,0,50,1000};
                try {
                    UPMBankWSStub.MovementList response = client.getMyMovements();
                    assertArrayEquals(res, response.getMovementQuantities());
                } catch (RemoteException e) {
                    e.printStackTrace();
                    fail("Exception thrown");
                }
            }
        }
    }


    static Stream<String> generateUsername() {
        String[] array = new String[NUMUSERS];
        for (int i = 0; i < NUMUSERS; i++) {
            String var = faker.regexify("[A-Z]{2}+[0-9]{2}");
            array[i] = var;
        }
        return Stream.of(array);
    }

    static Stream<String> randomUser() {
        Object[] keys = usedUsers.keySet().toArray();
        Object key = keys[new Random().nextInt(keys.length)];
        return Stream.of(key.toString());
    }
}