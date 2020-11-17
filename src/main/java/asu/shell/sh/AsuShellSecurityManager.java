package asu.shell.sh;

import java.io.FileDescriptor;
import java.net.InetAddress;

public class AsuShellSecurityManager extends SecurityManager {

    private boolean _ok_to_exit = false;
    @Override
    public void checkCreateClassLoader() {
    }
    @Override
    public void checkAccess(Thread paramThread) {
    }
    @Override
    public void checkAccess(ThreadGroup paramThreadGroup) {
    }
    @Override
    public void checkExit(int paramInt) {
        if (!this._ok_to_exit) {
            throw new AsuShellCommandExit(paramInt);
        }
    }
    @Override
    public void checkExec(String paramString) {
    }
    @Override
    public void checkLink(String paramString) {
    }
    @Override
    public void checkRead(FileDescriptor paramFileDescriptor) {
    }
    @Override
    public void checkRead(String paramString) {
    }
    @Override
    public void checkRead(String paramString, Object paramObject) {
    }
    @Override
    public void checkWrite(FileDescriptor paramFileDescriptor) {
    }
    @Override
    public void checkWrite(String paramString) {
    }
    @Override
    public void checkDelete(String paramString) {
    }
    @Override
    public void checkConnect(String paramString, int paramInt) {
    }
    @Override
    public void checkConnect(String paramString, int paramInt,
                             Object paramObject) {
    }
    @Override
    public void checkListen(int paramInt) {
    }
    @Override
    public void checkAccept(String paramString, int paramInt) {
    }
    @Override
    public void checkMulticast(InetAddress paramInetAddress) {
    }
    @Override
    public void checkMulticast(InetAddress paramInetAddress, byte paramByte) {
    }
    @Override
    public void checkPropertiesAccess() {
    }
    @Override
    public void checkPropertyAccess(String paramString) {
    }
    @Override
    public boolean checkTopLevelWindow(Object paramObject) {
        return true;
    }
    @Override
    public void checkPrintJobAccess() {
    }
    @Override
    public void checkSystemClipboardAccess() {
    }
    @Override
    public void checkAwtEventQueueAccess() {
    }
    @Override
    public void checkPackageAccess(String paramString) {
    }
    @Override
    public void checkPackageDefinition(String paramString) {
    }
    @Override
    public void checkSetFactory() {
    }
    @Override
    public void checkMemberAccess(Class paramClass, int paramInt) {
    }
    @Override
    public void checkSecurityAccess(String paramString) {
    }

    public void okToExit() {
        this._ok_to_exit = true;
    }
}