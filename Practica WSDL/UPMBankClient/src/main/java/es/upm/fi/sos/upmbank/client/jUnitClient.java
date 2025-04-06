package es.upm.fi.sos.upmbank.client;

import es.upm.fi.sos.upmbank.client.UPMBankWSStub.*;
import org.apache.axis2.AxisFault;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;

public class jUnitClient {

    private final UPMBankWSStub stub;
    private User user;
    private Random random;
    private ArrayList<String> accounts;

    public jUnitClient(String username, String password) throws AxisFault {
        stub = new UPMBankWSStub();
        stub._getServiceClient().engageModule("addressing");
        stub._getServiceClient().getOptions().setManageSession(true);
        this.user = new User();
        this.user.setName(username);
        this.user.setPwd(password);
        this.random = new Random();
        this.accounts = new ArrayList<>();
    }

    public jUnitClient(String username) throws AxisFault {
        stub = new UPMBankWSStub();
        stub._getServiceClient().engageModule("addressing");
        stub._getServiceClient().getOptions().setManageSession(true);
        this.user = new User();
        this.user.setName(username);
        this.random = new Random();
        this.accounts = new ArrayList<>();
    }

    public jUnitClient() throws AxisFault {
        stub = new UPMBankWSStub();
        stub._getServiceClient().engageModule("addressing");
        stub._getServiceClient().getOptions().setManageSession(true);
        this.user = new User();
        this.accounts = new ArrayList<>();
    }

    boolean login() throws RemoteException {
        Login login = new Login();
        login.setArgs0(user);
        LoginResponse response = stub.login(login);
        return response.get_return().getResponse();
    }

    void logout() throws RemoteException {
        Logout logout = new Logout();
        stub.logout(logout);
    }

    AddUserResponse addUser(String username) throws RemoteException {
        Username var = new Username();
        var.setUsername(username);
        AddUser addUser = new AddUser();
        addUser.setArgs0(var);
        AddUserResponse response = stub.addUser(addUser).get_return();
        if (response.getResponse()) {
            this.user.setPwd(response.getPwd());
        }
        return response;
    }

    Response removeUser(String username) throws RemoteException {
        Username var = new Username();
        var.setUsername(username);
        RemoveUser removeUser = new RemoveUser();
        removeUser.setArgs0(var);
        return stub.removeUser(removeUser).get_return();
    }

    Response changePassword(String newPwd, String oldPwd) throws RemoteException {
        PasswordPair var = new PasswordPair();
        var.setNewpwd(newPwd);
        var.setOldpwd(oldPwd);
        ChangePassword changePwd = new ChangePassword();
        changePwd.setArgs0(var);
        Response response = stub.changePassword(changePwd).get_return();
        if (response.getResponse()) {
            this.user.setPwd(newPwd);
        }
        return response;
    }

    BankAccountResponse addBankAcc(double qtty) throws RemoteException {
        Deposit var = new Deposit();
        var.setQuantity(qtty);
        AddBankAcc addBankAcc = new AddBankAcc();
        addBankAcc.setArgs0(var);
        AddBankAccResponse addBankAccResponse = stub.addBankAcc(addBankAcc);
        BankAccountResponse response = addBankAccResponse.get_return();
        if (response.getResult()) {
            accounts.add(response.getIBAN());
        }
        return response;
    }

    Response closeBankAcc(String iban) throws RemoteException {
        BankAccount var = new BankAccount();
        var.setIBAN(iban);
        CloseBankAcc closeBankAcc = new CloseBankAcc();
        closeBankAcc.setArgs0(var);
        Response response = stub.closeBankAcc(closeBankAcc).get_return();
        if (response.getResponse()) {
            accounts.remove(iban);
        }
        return response;
    }

    AddMovementResponse addMovement(String iban, double qtty) throws RemoteException {
        Movement var = new Movement();
        var.setIBAN(iban);
        var.setQuantity(qtty);
        AddIncome addIncome = new AddIncome();
        addIncome.setArgs0(var);
        return stub.addIncome(addIncome).get_return();
    }

    AddMovementResponse addWithdrawal(String iban, double qtty) throws RemoteException {
        Movement var = new Movement();
        var.setIBAN(iban);
        var.setQuantity(qtty);
        AddWithdrawal addWithdrawal = new AddWithdrawal();
        addWithdrawal.setArgs0(var);
        return stub.addWithdrawal(addWithdrawal).get_return();
    }

    MovementList getMyMovements() throws RemoteException {
        GetMyMovements var = new GetMyMovements();
        GetMyMovementsResponse response = stub.getMyMovements(var);
        return response.get_return();
    }


    public void setUsername(String username) {
        this.user.setName(username);
    }
    public void setPassword(String pwd) {
        this.user.setPwd(pwd);
    }
    public User getUser(){
        return this.user;
    }
    public String getIBAN() {
        if (!accounts.isEmpty()) return accounts.get(0);
        else return "";
    }
}
