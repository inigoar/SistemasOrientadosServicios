/**
 * UPMBankWSSkeleton.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */
package es.upm.fi.sos.upmbank;

import UPMAuthenticationAuthorization.UPMAuthenticationAuthorizationWSSkeletonStub;
import es.upm.fi.sos.upmbank.xsd.*;
import org.apache.axis2.AxisFault;

import java.rmi.RemoteException;
import java.util.*;

/**
 * UPMBankWSSkeleton java skeleton for the axisService
 */
public class UPMBankWSSkeleton {

    private static HashMap<String, User> listaUsuarios;
    private static HashMap<String, Integer> usuariosOnline;                     //Username, Nº Sesiones
    private static HashMap<String, ArrayList<BankAccount>> accountList;         //Username, [BankAccount]
    private static HashMap<String, Double> accounts;                            //IBAN, Balance
    private static HashMap<String, Queue<Movement>> movements;                  //Username, Queue<Movimientos>
    private static UPMAuthenticationAuthorizationWSSkeletonStub AuthClient;
    User admin;
    private User sesionActual;
    private boolean online;


    public UPMBankWSSkeleton() {
        if (listaUsuarios == null) {
            listaUsuarios = new HashMap<>();
        }
        if (usuariosOnline == null) {
            usuariosOnline = new HashMap<>();
        }
        if (accountList == null) {
            accountList = new HashMap<>();
        }
        if (accounts == null) {
            accounts = new HashMap<>();
        }
        if (movements == null) {
            movements = new HashMap<>();
        }
        admin = new User();
        admin.setName("admin");
        admin.setPwd("admin");
        listaUsuarios.put("admin", admin);
        sesionActual = null;

        try {
            AuthClient = new UPMAuthenticationAuthorizationWSSkeletonStub();
        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        }
    }


    /**
     * Auto generated method signature
     *
     * @param addBankAcc
     * @return addBankAccResponse
     */

    public es.upm.fi.sos.upmbank.AddBankAccResponse addBankAcc
    (
            es.upm.fi.sos.upmbank.AddBankAcc addBankAcc
    ) {
        BankAccountResponse response = new BankAccountResponse();

        boolean exist = false;

        if (online) {
            String username = sesionActual.getName();
            UPMAuthenticationAuthorizationWSSkeletonStub.Username userCheck = new UPMAuthenticationAuthorizationWSSkeletonStub.Username();
            userCheck.setName(username);
            UPMAuthenticationAuthorizationWSSkeletonStub.ExistUser userExist = new UPMAuthenticationAuthorizationWSSkeletonStub.ExistUser();
            userExist.setUsername(userCheck);

            try {
                exist = AuthClient.existUser(userExist).get_return().getResult();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            if (exist) {
                Double quantity = addBankAcc.getArgs0().getQuantity();
                Random random = new Random();
                String ibanInt = String.format("%04d", random.nextInt(10000));
                String IBAN = sesionActual.getName() + ibanInt;

                BankAccount account = new BankAccount();
                account.setIBAN(IBAN);

                ArrayList<BankAccount> aux;
                if (accountList.containsKey(sesionActual.getName())) {
                    aux = accountList.get(sesionActual.getName());
                } else {
                    aux = new ArrayList<>();
                }
                aux.add(account);
                accountList.put(sesionActual.getName(), aux);
                accounts.put(IBAN, quantity);

                response.setResult(true);
                response.setIBAN(IBAN);
            }
        } else {
            response.setResult(false);
            response.setIBAN("");
        }

        AddBankAccResponse endResponse = new AddBankAccResponse();
        endResponse.set_return(response);

        return endResponse;

    }


    /**
     * Auto generated method signature
     *
     * @param closeBankAcc
     * @return closeBankAccResponse
     */

    public es.upm.fi.sos.upmbank.CloseBankAccResponse closeBankAcc
    (
            es.upm.fi.sos.upmbank.CloseBankAcc closeBankAcc
    ) {

        Response response = new Response();

        boolean exist = false;

        if (online) {
            BankAccount userBank = closeBankAcc.getArgs0();
            String userIban = userBank.getIBAN();
            String username = sesionActual.getName();
            UPMAuthenticationAuthorizationWSSkeletonStub.Username userCheck = new UPMAuthenticationAuthorizationWSSkeletonStub.Username();
            userCheck.setName(username);
            UPMAuthenticationAuthorizationWSSkeletonStub.ExistUser userExist = new UPMAuthenticationAuthorizationWSSkeletonStub.ExistUser();
            userExist.setUsername(userCheck);

            try {
                exist = AuthClient.existUser(userExist).get_return().getResult();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            if (accounts.containsKey(userIban) && accounts.get(userIban).equals(0.0) && exist) {

                accounts.remove(userIban);
                response.setResponse(true);

            }
        } else {
            response.setResponse(false);
        }

        CloseBankAccResponse endResponse = new CloseBankAccResponse();
        endResponse.set_return(response);

        return endResponse;
    }


    /**
     * Auto generated method signature
     *
     * @param logout
     * @return
     */

    public void logout
    (
            es.upm.fi.sos.upmbank.Logout logout
    ) {
        if (online && usuariosOnline != null && sesionActual != null && sesionActual.getName() != null) {
            int numberOfSessions = usuariosOnline.get(sesionActual.getName());

            while (numberOfSessions >= 1) {
                if (numberOfSessions == 1) {
                    usuariosOnline.remove(sesionActual.getName());
                    online = false;
                    sesionActual = null;
                    numberOfSessions--;
                } else {
                    numberOfSessions--;
                    usuariosOnline.put(sesionActual.getName(), numberOfSessions);
                }
            }
        }
    }


    /**
     * Auto generated method signature
     *
     * @param removeUser
     * @return removeUserResponse
     */

    public es.upm.fi.sos.upmbank.RemoveUserResponse removeUser
    (
            es.upm.fi.sos.upmbank.RemoveUser removeUser
    ) {

        Response response = new Response();
        boolean exist = false;


        UPMAuthenticationAuthorizationWSSkeletonStub.RemoveUser userRemoveService = new UPMAuthenticationAuthorizationWSSkeletonStub.RemoveUser();
        UPMAuthenticationAuthorizationWSSkeletonStub.RemoveUserE userRemoved = new UPMAuthenticationAuthorizationWSSkeletonStub.RemoveUserE();

        if (online) {
            Username user = removeUser.getArgs0();
            String username = user.getUsername();
            ArrayList<BankAccount> numeroCuentas = accountList.get(username);
            String onlineUser = sesionActual.getName();
            UPMAuthenticationAuthorizationWSSkeletonStub.Username userCheck = new UPMAuthenticationAuthorizationWSSkeletonStub.Username();
            userCheck.setName(username);
            UPMAuthenticationAuthorizationWSSkeletonStub.ExistUser userExist = new UPMAuthenticationAuthorizationWSSkeletonStub.ExistUser();
            userExist.setUsername(userCheck);

            try {
                exist = AuthClient.existUser(userExist).get_return().getResult();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (exist && onlineUser.equals("admin") && (numeroCuentas == null || numeroCuentas.isEmpty()) && !username.equals("admin")) {

                userRemoveService.setName(username);
                userRemoved.setRemoveUser(userRemoveService);

                try {
                    response.setResponse(AuthClient.removeUser(userRemoved).get_return().getResult());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else {
            response.setResponse(false);
        }

        RemoveUserResponse endResponse = new RemoveUserResponse();
        endResponse.set_return(response);

        return endResponse;
    }

    /**
     * Auto generated method signature
     *
     * @param addWithdrawal
     * @return addWithdrawalResponse
     */

    public es.upm.fi.sos.upmbank.AddWithdrawalResponse addWithdrawal
    (
            es.upm.fi.sos.upmbank.AddWithdrawal addWithdrawal
    ) {

        AddMovementResponse response = new AddMovementResponse();

        boolean exist = false;
        response.setResult(false);
        response.setBalance(0);

        if (online) {
            Movement info = addWithdrawal.getArgs0();
            String ibanNumber = info.getIBAN();
            Double quantityNumber = info.getQuantity();
            String username = sesionActual.getName();
            UPMAuthenticationAuthorizationWSSkeletonStub.Username userCheck = new UPMAuthenticationAuthorizationWSSkeletonStub.Username();
            userCheck.setName(username);
            UPMAuthenticationAuthorizationWSSkeletonStub.ExistUser userExist = new UPMAuthenticationAuthorizationWSSkeletonStub.ExistUser();
            userExist.setUsername(userCheck);

            try {
                exist = AuthClient.existUser(userExist).get_return().getResult();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            if (accounts.containsKey(ibanNumber) && exist) {
                if (accounts.get(ibanNumber) >= quantityNumber) {
                    double newQuantity = accounts.get(ibanNumber) - quantityNumber;
                    accounts.put(ibanNumber, newQuantity);

                    Queue<Movement> mov = movements.get(sesionActual.getName());
                    Movement var = new Movement();
                    if (mov == null) mov = new LinkedList<>();
                    var.setIBAN(ibanNumber);
                    var.setQuantity(quantityNumber);

                    if (mov.size() >= 10) {
                        mov.remove();
                    }

                    mov.add(var);

                    movements.put(sesionActual.getName(), mov);
                    response.setResult(true);
                    response.setBalance(newQuantity);
                }
            }
        }

        AddWithdrawalResponse endResponse = new AddWithdrawalResponse();
        endResponse.set_return(response);

        return endResponse;

    }


    /**
     * Auto generated method signature
     *
     * @param addUser
     * @return addUserResponse
     */

    public es.upm.fi.sos.upmbank.AddUserResponse addUser
    (
            es.upm.fi.sos.upmbank.AddUser addUser
    ) {
        es.upm.fi.sos.upmbank.xsd.AddUserResponse response = new es.upm.fi.sos.upmbank.xsd.AddUserResponse();

        if (online && sesionActual.getName().equals("admin")) {
            String username = addUser.getArgs0().getUsername();

            UPMAuthenticationAuthorizationWSSkeletonStub.AddUser addUserService = new UPMAuthenticationAuthorizationWSSkeletonStub.AddUser();
            UPMAuthenticationAuthorizationWSSkeletonStub.UserBackEnd userBackEnd = new UPMAuthenticationAuthorizationWSSkeletonStub.UserBackEnd();

            userBackEnd.setName(username);
            addUserService.setUser(userBackEnd);

            try {
                UPMAuthenticationAuthorizationWSSkeletonStub.AddUserResponseBackEnd userResponseBackEnd = AuthClient.addUser(addUserService).get_return();
                response.setResponse(userResponseBackEnd.getResult());
                response.setPwd(userResponseBackEnd.getPassword());

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            response.setResponse(false);
            response.setPwd("");
        }

        AddUserResponse endResponse = new AddUserResponse();
        endResponse.set_return(response);
        return endResponse;

    }


    /**
     * Auto generated method signature
     *
     * @param addIncome
     * @return addIncomeResponse
     */

    public es.upm.fi.sos.upmbank.AddIncomeResponse addIncome
    (
            es.upm.fi.sos.upmbank.AddIncome addIncome
    ) {
        AddMovementResponse response = new AddMovementResponse();

        response.setResult(false);
        response.setBalance(0);

        boolean exist = false;

        if (online) {
            String IBAN = addIncome.getArgs0().getIBAN();
            String username = sesionActual.getName();
            UPMAuthenticationAuthorizationWSSkeletonStub.Username userCheck = new UPMAuthenticationAuthorizationWSSkeletonStub.Username();
            userCheck.setName(username);
            UPMAuthenticationAuthorizationWSSkeletonStub.ExistUser userExist = new UPMAuthenticationAuthorizationWSSkeletonStub.ExistUser();
            userExist.setUsername(userCheck);

            try {
                exist = AuthClient.existUser(userExist).get_return().getResult();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (exist) {
                for (BankAccount aux : accountList.get(sesionActual.getName())) {
                    if (aux.getIBAN().equals(IBAN)) {
                        Double balance = accounts.get(IBAN);
                        balance += addIncome.getArgs0().getQuantity();
                        accounts.put(IBAN, balance);

                        Queue<Movement> mov = movements.get(sesionActual.getName());
                        if (mov == null) mov = new LinkedList<>();
                        Movement var = new Movement();
                        var.setQuantity(addIncome.getArgs0().getQuantity());
                        var.setIBAN(IBAN);

                        while (mov.size() >= 10) {
                            mov.remove();
                        }
                        mov.add(var);
                        movements.put(sesionActual.getName(), mov);

                        response.setBalance(balance);
                        response.setResult(true);
                        break;
                    }
                }
            }
        }

        AddIncomeResponse endResponse = new AddIncomeResponse();
        endResponse.set_return(response);
        return endResponse;
    }


    /**
     * Auto generated method signature
     *
     * @param login
     * @return loginResponse
     */

    public es.upm.fi.sos.upmbank.LoginResponse login
    (
            es.upm.fi.sos.upmbank.Login login
    ) {
        Response response = new Response();
        response.setResponse(false);

        User user = login.getArgs0();
        String username = user.getName();
        String password = user.getPwd();

        if (username.equals("admin") && password.equals(admin.getPwd())) {
            response.setResponse(true);
        } else {
            UPMAuthenticationAuthorizationWSSkeletonStub.Login loginService = new UPMAuthenticationAuthorizationWSSkeletonStub.Login();
            UPMAuthenticationAuthorizationWSSkeletonStub.LoginBackEnd loginBackEnd = new UPMAuthenticationAuthorizationWSSkeletonStub.LoginBackEnd();

            loginBackEnd.setName(username);
            loginBackEnd.setPassword(password);
            loginService.setLogin(loginBackEnd);

            try {
                response.setResponse(AuthClient.login(loginService).get_return().getResult());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        if (response.getResponse()) {
            if (online) {
                if (sesionActual.getName().equals(username)) {
                    int numberOfSessions = usuariosOnline.get(username);
                    numberOfSessions++;
                    usuariosOnline.put(username, numberOfSessions);
                } else {
                    response.setResponse(false);
                }

            } else {
                usuariosOnline.put(username, 1);
                online = true;
                sesionActual = user;
            }

        }

        LoginResponse endResponse = new LoginResponse();
        endResponse.set_return(response);

        return endResponse;
    }


    /**
     * Auto generated method signature
     *
     * @param getMyMovements
     * @return getMyMovementsResponse
     */

    public es.upm.fi.sos.upmbank.GetMyMovementsResponse getMyMovements
    (
            es.upm.fi.sos.upmbank.GetMyMovements getMyMovements
    ) {
        MovementList response = new MovementList();
        double[] res = new double[10];

        boolean exist = false;

        if (online) {
            String username = sesionActual.getName();
            UPMAuthenticationAuthorizationWSSkeletonStub.Username userCheck = new UPMAuthenticationAuthorizationWSSkeletonStub.Username();
            userCheck.setName(username);
            UPMAuthenticationAuthorizationWSSkeletonStub.ExistUser userExist = new UPMAuthenticationAuthorizationWSSkeletonStub.ExistUser();
            userExist.setUsername(userCheck);

            try {
                exist = AuthClient.existUser(userExist).get_return().getResult();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        if (exist && online && movements.containsKey(sesionActual.getName())) {
            Queue<Movement> mov = new LinkedList<>(movements.get(sesionActual.getName()));
            int size = mov.size();
            for (int i = 0; i < size; i++) {
                int v = 9 - i;
                res[v] = mov.remove().getQuantity();
            }
            response.setResult(true);
            response.setMovementQuantities(res);
        } else {
            response.setResult(false);
        }

        GetMyMovementsResponse endResponse = new GetMyMovementsResponse();
        endResponse.set_return(response);
        return endResponse;
    }


    /**
     * Auto generated method signature
     *
     * @param changePassword
     * @return changePasswordResponse
     */

    public es.upm.fi.sos.upmbank.ChangePasswordResponse changePassword
    (
            es.upm.fi.sos.upmbank.ChangePassword changePassword
    ) {
        Response response = new Response();
        boolean exist = false;

        if (online) {
            String username = sesionActual.getName();
            String oldPwd = changePassword.getArgs0().getOldpwd();
            String newPwd = changePassword.getArgs0().getNewpwd();
            UPMAuthenticationAuthorizationWSSkeletonStub.Username userCheck = new UPMAuthenticationAuthorizationWSSkeletonStub.Username();
            userCheck.setName(username);
            UPMAuthenticationAuthorizationWSSkeletonStub.ExistUser userExist = new UPMAuthenticationAuthorizationWSSkeletonStub.ExistUser();
            userExist.setUsername(userCheck);

            try {
                exist = AuthClient.existUser(userExist).get_return().getResult();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            if (username.equals("admin")) {

                if (admin.getPwd().equals(oldPwd) && !oldPwd.equals(newPwd)) {
                    admin.setPwd(newPwd);
                    response.setResponse(true);
                } else {
                    response.setResponse(false);
                }

            } else if (exist && !username.equals("admin") && !oldPwd.equals(newPwd)) {

                UPMAuthenticationAuthorizationWSSkeletonStub.ChangePassword changePasswordService = new UPMAuthenticationAuthorizationWSSkeletonStub.ChangePassword();
                UPMAuthenticationAuthorizationWSSkeletonStub.ChangePasswordBackEnd changePasswordBackEnd = new UPMAuthenticationAuthorizationWSSkeletonStub.ChangePasswordBackEnd();
                changePasswordBackEnd.setName(username);
                changePasswordBackEnd.setNewpwd(newPwd);
                changePasswordBackEnd.setOldpwd(oldPwd);
                changePasswordService.setChangePassword(changePasswordBackEnd);

                try {
                    response.setResponse(AuthClient.changePassword(changePasswordService).get_return().getResult());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        } else {
            response.setResponse(false);
        }

        ChangePasswordResponse endResponse = new ChangePasswordResponse();
        endResponse.set_return(response);

        return endResponse;
    }

}
